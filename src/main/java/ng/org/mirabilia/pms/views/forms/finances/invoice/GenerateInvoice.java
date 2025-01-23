package ng.org.mirabilia.pms.views.forms.finances.invoice;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import ng.org.mirabilia.pms.utils.PDFWriter;
import ng.org.mirabilia.pms.domain.entities.Installment;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.CreateStatusTag;
import ng.org.mirabilia.pms.domain.enums.InstallmentalPayments;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.modules.security.LoginView;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GenerateInvoice extends Dialog {

    public ComboBox<User> userNameOrUserCode;
    public ComboBox<Property> propertyCode;
    private UserService userService;
    private PropertyService propertyService;
    private InvoiceService invoiceService;
    private  User selectedUser;
    private FormLayout GenerateInvoiceFormLayout;
    public Grid<Installment> installmentGrid;
    BigDecimal propertyPrice;
    InstallmentalPayments installmentPayments;
    H4 paymentBreakdownText;
    public Button previewInvoiceButton;
    public Invoice newInvoice;
    List<Installment> installments;
    LoginView loginView;
    CreateStatusTag createStatusTag;
    PDFWriter pdfWriter;
    public Button sendInvoiceButton;
    public HorizontalLayout buttonLayout;
    InvoicePreview previewDialog;
    Property selectedProperty;
    public Button backToGenerateInvoiceButton;
    private final Consumer<Void> onSuccess;

    @Autowired
    public GenerateInvoice(UserService userService, PropertyService propertyService, InvoiceService invoiceService, Consumer<Void> onSuccess){
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;
        this.onSuccess = onSuccess;

        //initialized fields
        GenerateInvoiceFormLayout = new FormLayout();
        userNameOrUserCode = new ComboBox<>("UserCode | UserName");
        propertyCode = new ComboBox<>("Property Code");
        paymentBreakdownText = new H4("Payment Breakdown");
        installmentGrid = new Grid<>(Installment.class, false);
        loginView = new LoginView(userService);
        createStatusTag = new CreateStatusTag();
        pdfWriter = new PDFWriter();
        buttonLayout = new HorizontalLayout();
        previewDialog = new InvoicePreview();


        //buttons initialization and setup
        sendInvoiceButton = new Button("Send Invoice", e -> {
            saveInvoice();
            userNameOrUserCode.clear();
            propertyCode.clear();
        });


        previewInvoiceButton = new Button("Preview", e -> {
            userNameOrUserCode.setRequired(true);
            propertyCode.setRequired(true);
            if (userNameOrUserCode.isEmpty() || propertyCode.isEmpty()) {
                Notification.show("All fields are required!!", 2000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                openInvoicePreviewDialog();
            }
        });

        backToGenerateInvoiceButton = new Button("Back", e -> {
            resetPreviewDialog();
            previewDialog.close();
        });


        //Preview Button Layout
        buttonLayout.add(backToGenerateInvoiceButton, sendInvoiceButton);

        //populating Combo Boxes
        userNameOrUserCode.setItems(userService.getClients());

        userNameOrUserCode.setItemLabelGenerator(user ->
                user.getUserCode() + " | " + user.getUsername());

        userNameOrUserCode.addValueChangeListener(e -> {
            selectedUser = e.getValue();

            if (selectedUser != null) {
                List<Property> userProperties = getUserProperty(selectedUser);
                propertyCode.setItems(userProperties);
                propertyCode.setItemLabelGenerator(property -> "Property Code: " + property.getPropertyCode());
            } else {
                propertyCode.clear();
                propertyCode.setItems();
            }
        });

        //updating the installment grid by property
        propertyCode.addValueChangeListener(event -> {
            selectedProperty = propertyCode.getValue();
            if (!invoiceService.invoiceExists(selectedProperty)) {
                if (selectedProperty != null) {
                    propertyPrice = selectedProperty.getPrice();
                    installmentPayments = selectedProperty.getInstallmentalPayments();  // Get installment payment type (e.g., 3 months, 6 months)

                    List<Installment> installments = calculateInstallments();
                    installmentGrid.setItems(installments);
                } else {
                    installmentGrid.setItems(Collections.emptyList());
                }
            } else {
                Notification.show("Invoice Already Created!!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                userNameOrUserCode.clear();
                propertyCode.clear();
                close();
            }
        });

        // Set up the installment grid breakdown
        installmentGrid.addColumn(installment -> installment.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true);
        installmentGrid.addColumn(installment -> new DecimalFormat("#,###").format(installment.getPrice())).setHeader("Price").setSortable(true).setAutoWidth(true);
        installmentGrid.addComponentColumn(installment -> createStatusTag.createStatusTag(installment.getInvoiceStatus())).setHeader("Payment Status").setAutoWidth(true).setSortable(true);

        //adding to formLayout and inline styling
        GenerateInvoiceFormLayout.add(userNameOrUserCode, propertyCode, paymentBreakdownText, installmentGrid);
        GenerateInvoiceFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        GenerateInvoiceFormLayout.setMaxWidth("500px");
        paymentBreakdownText.getStyle().setMarginTop("10px").setMarginBottom("10px");

        //Display
        add(GenerateInvoiceFormLayout);
    }

    private List<Property> getUserProperty(User user){
        return  propertyService.getPropertyByUserId(user.getId());
    }


    private void resetPreviewDialog(){
        //clear existing details, so it doesn't appear as duplicate
        previewDialog.invoiceCode.removeFromParent();
        previewDialog.issueDate.removeFromParent();
        previewDialog.dueDate.removeFromParent();
        previewDialog.propertyName.removeFromParent();
        previewDialog.propertyType.removeFromParent();
        previewDialog.userNameOrUserCode.removeFromParent();
        previewDialog.paymentTerms.removeFromParent();
        previewDialog.issuedBy.removeFromParent();
        previewDialog.totalAmount.removeFromParent();
        previewDialog.paymentBreakDownText.removeFromParent();
        previewDialog.installmentGrid.removeFromParent();
        previewDialog.installmentGrid.setItems();
    }

    // calculate the installment plan of a user
    private List<Installment> calculateInstallments() {
        int plan = 0; // 3, 6, or 12 months
        BigDecimal percentage;

        // Determine the percentage based on the plan
        switch (installmentPayments) {
            case IMMEDIATE_PAYMENT:
                plan = 1;
                percentage = BigDecimal.valueOf(100); // 100%
                break;
            case THREE_MONTHS:
                plan = 3;
                percentage = BigDecimal.valueOf(33.33); // 33.33%
                break;
            case SIX_MONTHS:
                plan = 6;
                percentage = BigDecimal.valueOf(16.67); // 16.67%
                break;
            case TWELVE_MONTHS:
                plan = 12;
                percentage = BigDecimal.valueOf(8.33);  // 8.33%
                break;
            default:
                throw new IllegalArgumentException("Invalid installment plan: " + plan);
        }

        // Calculate the monthly payment
        BigDecimal monthlyAmountExact = propertyPrice.divide(BigDecimal.valueOf(plan), 2, RoundingMode.HALF_UP);

        LocalDate startDate = LocalDate.now();
        installments = new ArrayList<>();

        for (int i = 1; i <= plan; i++) {
            LocalDate dueDate = startDate.plusMonths(i);
            installments.add(new Installment(monthlyAmountExact, dueDate, InvoiceStatus.UNPAID, newInvoice));
        }
        return installments;
    }

    //Populate The Invoice  details
    public Invoice populateInvoice() {
        newInvoice = new Invoice();
        newInvoice.setId(newInvoice.getId());
        newInvoice.setInvoiceCode(generateInvoiceCode());
        newInvoice.setIssueDate(LocalDate.now());
        newInvoice.setPropertyCode(propertyCode.getValue());
        newInvoice.setUserNameOrUserCode(userNameOrUserCode.getValue());
        newInvoice.setInstallmentalPaymentList(installments);
        newInvoice.setCreatedBy(loginView.getLoggedInUserDetails());
        newInvoice.setPropertyTitle(selectedProperty.getTitle());
        newInvoice.setPropertyType(selectedProperty.getPropertyType());
        newInvoice.setPropertyPrice(selectedProperty.getPrice());

        for (Installment installment : installments) {
            installment.setInvoice(newInvoice);
        }

        newInvoice.setInstallmentalPaymentList(installments);

        if (!installments.isEmpty()) {
            newInvoice.setInvoiceStatus(installments.get(installments.size() - 1).getInvoiceStatus());
            newInvoice.setDueDate(installments.get(installments.size() - 1).getDueDate());
        } else {
            throw new IllegalStateException("Installments list is empty");
        }
        return newInvoice;
    }

    //save invoice to database
    public void saveInvoice(){
        populateInvoice();
        invoicePdfWriter(newInvoice);
        invoiceService.addInvoice(newInvoice);
        newInvoice.setInstallmentalPaymentList(installments);
        close();
        resetPreviewDialog();
        previewDialog.close();
        Notification.show("Invoice Sent",3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        onSuccess.accept(null);
    }

    //generate  invoice code
    public String generateInvoiceCode(){
        if (propertyCode.getValue() == null) {
            System.out.println("Property Code is Empty");
        }
            String prefix = "INV";
            String gettingPropertyCode = propertyCode.getValue().getPropertyCode();
            return prefix + "-" + gettingPropertyCode;
    }

    // to convert the invoice into a pdf to be sent to the user and saved in the database
    public Blob invoicePdfWriter(Invoice savedInvoice) {
        try {
            // Generate PDF bytes for the invoice
            byte[] pdfBytes = pdfWriter.generateInvoicePdf(savedInvoice);

            // Determine the desktop path
            String desktopPath = Paths.get(System.getProperty("user.home"), "Desktop").toString();
            String filePath = desktopPath + "\\Invoice_" + savedInvoice.getInvoiceCode() + ".pdf";


//            // convert the pdfBytes from bytes to blob
//            Blob pdfBlob = new SerialBlob(pdfBytes);
//            newInvoice.setPdfFile(pdfBlob);

            // Save the PDF to the desktop
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
            }
            // Show a success message
            System.out.println("Invoice saved to Desktop as: " + filePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to save invoice: " + ex.getMessage());
        }
        return null;
    }

    //invoice preview dialog
    private void openInvoicePreviewDialog() {
        // Create an instance of the preview dialog and pass the invoice data
        populateInvoice();

        // remove the header and footer of the dialog to avoid duplication
        previewDialog.getHeader().removeAll();
        previewDialog.getFooter().removeAll();

        previewDialog.setCloseOnOutsideClick(false);
        previewDialog.getHeader().add(new H4("INVOICE DETAILS"));
        previewDialog.getFooter().add(buttonLayout);
        previewDialog.updatePreview(newInvoice);

        previewDialog.setModal(true);
        previewDialog.setCloseOnOutsideClick(false);
        previewDialog.setCloseOnEsc(false);

        previewDialog.open();
    }

}

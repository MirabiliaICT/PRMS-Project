package ng.org.mirabilia.pms.views.forms.finances.invoice;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Style;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.PaymentReceipt;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PaymentMethod;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;


public class UploadReceipt extends Dialog {
    private final FinanceService financeService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private ComboBox<Invoice> invoice = new ComboBox<>("Invoice");

    private NumberField amountPaid = new NumberField("Amount Paid");
    private NumberField price = new NumberField("Price of Property");
    private ComboBox<PaymentMethod> paymentMethod = new ComboBox<>( "Payment Method");
    private TextField paidBy = new TextField( "Paid By");
    private TextField propertyType = new TextField( "Property Type");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);

    private final Consumer<Void> onSuccess;

    private byte[] uploadedImage;

    private final Finance finance = new Finance();




    public UploadReceipt(FinanceService financeService, InvoiceService invoiceService, UserService userService, Consumer<Void> onSuccess) {
        this.financeService = financeService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.onSuccess = onSuccess;

        configureFormFields();
        createFormLayout();
        configureUploadReceipt();
        addClassName("upload-receipt-dialog");
    }

    private void configureFormFields(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findByUsername(authentication.getName());

        List<Invoice> userInvoices = invoiceService.getInvoicesByUser(loggedInUser);
        if (userInvoices.isEmpty()) {
            Notification.show("No invoices found for the logged-in user.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            invoice.setItems(List.of());
        } else {
            invoice.setItems(userInvoices);
            invoice.setItemLabelGenerator(Invoice::getInvoiceCode);
        }



        paymentMethod.setItems(PaymentMethod.values());
        paymentMethod.setRequired(true);

        amountPaid.setMin(0);
        amountPaid.setPlaceholder("Price in NGN");
        amountPaid.setRequired(true);
        amountPaid.addClassName("custom-number-field");

        paidBy.addClassName("custom-text-field");
        paidBy.setRequired(true);

        upload.addClassName("receipt-upload");

        invoice.addValueChangeListener(event -> {
            Invoice selectedInvoice = event.getValue();

            if (selectedInvoice != null) {
                price.setValue(selectedInvoice.getPropertyPrice() != null ? selectedInvoice.getPropertyPrice().doubleValue() : 0.0);
                propertyType.setValue(selectedInvoice.getPropertyType().getDisplayName());
            } else {
                price.clear();
            }
        });


        price.setLabel("Price of Property");
        price.setReadOnly(true);
        propertyType.setReadOnly(true);

    }

    private void createFormLayout(){
        H2 headerTitle = new H2("Proof of Payment");
        H4 closeBtn = new H4("X");
        closeBtn.getStyle().setMarginRight("30px");
        closeBtn.addClickListener(e -> close());
        HorizontalLayout header = new HorizontalLayout(headerTitle, closeBtn);
        header.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        header.setWidthFull();
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);
        getHeader().add(header);
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout(invoice, paymentMethod, price, propertyType, amountPaid, paidBy, upload);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button saveButton = new Button("Save", e -> saveFinance());
        Button discardButton = new Button("Discard Charges", e -> close());
        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");


        HorizontalLayout buttonLayout = new HorizontalLayout(discardButton, saveButton);
        buttonLayout.setWidthFull();
        buttonLayout.getStyle().setDisplay(Style.Display.FLEX);
        buttonLayout.getStyle().setJustifyContent(Style.JustifyContent.FLEX_END);

        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);
        add(formLayout, buttonLayout);
    }

    private void saveFinance() {
        if (amountPaid == null || amountPaid.isEmpty() || invoice == null || invoice.isEmpty()
                || paymentMethod == null || paymentMethod.isEmpty() || paidBy == null || paidBy.isEmpty()) {
            Notification.show("Please fill all fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (buffer.getInputStream() != null) {
            try {
                uploadedImage = buffer.getInputStream().readAllBytes();
            } catch (IOException e) {
                Notification.show("Error processing uploaded file", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        }

        if (uploadedImage == null || uploadedImage.length == 0) {
            Notification.show("Please upload a receipt image", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }


        PaymentReceipt paymentReceipt = new PaymentReceipt();
        paymentReceipt.setReceiptImage(uploadedImage);
        paymentReceipt.setProperty(invoice.getValue().getPropertyCode());
        paymentReceipt.setLocalDateTime(LocalDateTime.now());

        finance.setAmountPaid(BigDecimal.valueOf(amountPaid.getValue()));
        finance.setInvoice(invoice.getValue());
        finance.setPaymentMethod(paymentMethod.getValue());
        finance.setPaidBy(paidBy.getValue());
        finance.setPaymentStatus(FinanceStatus.PENDING);
        finance.setOutstandingAmount(finance.updateOutstandingAmount());
        finance.setReceiptImage(paymentReceipt);

        financeService.saveFinance(finance);

        Notification.show("Finance saved successfully", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        onSuccess.accept(null);
        this.close();
    }

    private void configureUploadReceipt(){
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            try {
                uploadedImage = inputStream.readAllBytes();
                Notification.show("Image uploaded successfully", 2000, Notification.Position.TOP_CENTER);
            } catch (Exception e) {
                Notification.show("Failed to upload image", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }


}

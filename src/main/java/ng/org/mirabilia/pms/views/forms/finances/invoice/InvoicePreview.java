package ng.org.mirabilia.pms.views.forms.finances.invoice;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.textfield.TextField;
import ng.org.mirabilia.pms.domain.entities.Installment;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.enums.CreateStatusTag;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class InvoicePreview extends Dialog {

    private FormLayout previewFirstFormLayout;
    private FormLayout previewGridFormLayout;
    private FormLayout previewFormLayout;
    CreateStatusTag createStatusTag;

    //form fields
    public TextField invoiceCode;
    public TextField issueDate;
    public TextField dueDate;
    public TextField propertyName;
    public TextField propertyType;
    public TextField userNameOrUserCode;
    public TextField paymentTerms;
    public TextField issuedBy;
    public TextField totalAmount;
    public H4 paymentBreakDownText;
    public Grid<Installment> installmentGrid;

    @Autowired
    public InvoicePreview() {
        previewFirstFormLayout = new FormLayout();
        previewFormLayout = new FormLayout();
        previewGridFormLayout = new FormLayout();
        createStatusTag = new CreateStatusTag();

        //displaying
        add(previewFormLayout);
    }

   //preview function to be displayed
    public void updatePreview(Invoice invoice) {

        invoiceCode = new TextField("Invoice Code");
        invoiceCode.setValue(invoice.getInvoiceCode().toString());
        invoiceCode.setReadOnly(true);

        issueDate = new TextField("Issue Date");
        String issueDateFormatted = invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        issueDate.setValue(issueDateFormatted);
        issueDate.setReadOnly(true);

        dueDate = new TextField("Due Date");
        String dueDateFormatted = invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dueDate.setValue(dueDateFormatted);
        dueDate.setReadOnly(true);

        propertyName = new TextField("Property Name");
        propertyName.setValue(invoice.getPropertyCode().getTitle().toString());
        propertyName.setReadOnly(true);

        propertyType = new TextField("Property Type");
        propertyType.setValue(invoice.getPropertyCode().getPropertyType().toString().replace("_", " ").toLowerCase());
        propertyType.setReadOnly(true);

        userNameOrUserCode = new TextField("Client Name");
        String firstName = invoice.getUserNameOrUserCode().getFirstName();
        String lastName = invoice.getUserNameOrUserCode().getLastName();
        userNameOrUserCode.setValue(firstName + " " + lastName);
        userNameOrUserCode.setReadOnly(true);

        paymentTerms = new TextField("Payment Terms");
        paymentTerms.setValue(invoice.getPropertyCode().getInstallmentalPayments().toString().replace("_", " ").toLowerCase());
        paymentTerms.setReadOnly(true);

        issuedBy = new TextField("Issued By");
        issuedBy.setValue(invoice.getCreatedBy().toString());
        issuedBy.setReadOnly(true);

        totalAmount = new TextField("Amount");
        String formattedPrice = new DecimalFormat("#,###").format(invoice.getPropertyCode().getPrice());
        totalAmount.setValue(formattedPrice);
        totalAmount.setReadOnly(true);

       paymentBreakDownText = new H4("Payment Breakdown");
       paymentBreakDownText.getStyle().setMarginTop("10px").setMarginBottom("10px");

        installmentGrid  = new Grid<>(Installment.class, false);
        installmentGrid.addColumn(installment -> installment.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true);
        installmentGrid.addColumn(installment -> new DecimalFormat("#,###").format(installment.getPrice())).setHeader("Price").setSortable(true).setAutoWidth(true);
        installmentGrid.addComponentColumn(installment -> createStatusTag.createStatusTag(installment.getInvoiceStatus())).setHeader("Payment Status").setAutoWidth(true).setSortable(true);

        if (invoice.getInstallmentalPaymentList() != null && !invoice.getInstallmentalPaymentList().isEmpty()) {
            installmentGrid.setItems(invoice.getInstallmentalPaymentList());
        } else {
            System.out.println("Installment Payment List is null or empty.");
        }

        previewFirstFormLayout.add(invoiceCode, issueDate, dueDate, propertyName, propertyType, userNameOrUserCode, paymentTerms, issuedBy, totalAmount);
        previewGridFormLayout.add(paymentBreakDownText, installmentGrid);
        previewFirstFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        previewGridFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        previewFormLayout.add(previewFirstFormLayout, previewGridFormLayout);
        previewFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        previewFormLayout.setMaxWidth("700px");
    }

}

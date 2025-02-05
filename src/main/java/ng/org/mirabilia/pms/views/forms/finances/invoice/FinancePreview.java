package ng.org.mirabilia.pms.views.forms.finances.invoice;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.Finance;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class FinancePreview extends Dialog {

    Div imagePreview;
    Image receiptImage;
    public FormLayout financePreviewLayout;
    public VerticalLayout imagePreviewLayout;
    private final Consumer<Void> onSuccess;

    public FinancePreview(Consumer<Void> onSuccess){

        this.onSuccess = onSuccess;
        financePreviewLayout = new FormLayout();
        imagePreviewLayout = new VerticalLayout();

        add(imagePreviewLayout, financePreviewLayout);
    }

    public void updatePreview(Finance finance){
        TextField invoiceCodeField = new TextField("Invoice Code");
        invoiceCodeField.setValue(finance.getInvoice().getInvoiceCode());
        invoiceCodeField.setReadOnly(true);

        TextField ownerField = new TextField("Client Name");
        ownerField.setValue(finance.getInvoice().getUserNameOrUserCode().getLastName() + " " +
                finance.getInvoice().getUserNameOrUserCode().getFirstName());
        ownerField.setReadOnly(true);

        TextField paymentDateField = new TextField("Payment Date");
        paymentDateField.setValue(finance.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        paymentDateField.setReadOnly(true);

        TextField statusField = new TextField("Payment Status");
        statusField.setValue(finance.getPaymentStatus().toString().toLowerCase());
        statusField.setReadOnly(true);

        TextField propertyField = new TextField("Property Title");
        propertyField.setValue(finance.getInvoice().getPropertyTitle());
        propertyField.setReadOnly(true);

        TextField paidByField = new TextField("Paid By");
        paidByField.setValue(finance.getPaidBy());
        paidByField.setReadOnly(true);

        TextField amountPaidField = new TextField("Amount Paid");
        amountPaidField.setValue(finance.getAmountPaidFormattedToString());
        amountPaidField.setReadOnly(true);

        TextField outstandingAmountField = new TextField("Outstanding Amount");
        String outstandingAmount = finance.getOutstandingAmount().toString();
        outstandingAmountField.setValue(outstandingAmount);
        outstandingAmountField.setReadOnly(true);

        TextField paymentMethodField = new TextField("Payment Method");
        paymentMethodField.setValue(finance.getPaymentMethod().toString().replace("_", " ").toLowerCase());
        paymentMethodField.setReadOnly(true);

        //image configuration
        imagePreview = new Div();

        byte[] receipt = finance.getReceiptImage().getReceiptImage();

        if(receipt != null){
            ByteArrayInputStream streamForReceipt = new ByteArrayInputStream(receipt);
            StreamResource resource = new StreamResource("", () -> streamForReceipt);
            receiptImage = new Image(resource, "");
            receiptImage.setClassName("image");
            receiptImage.setHeight("600px");
            receiptImage.setWidthFull();
//            receiptImage.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
            imagePreview.add(receiptImage);
        } else{
            imagePreview.setHeight("10px");
            imagePreview.setWidth("10px");
            imagePreview.getStyle().setBackgroundColor("#162868");
        }


        //display on the form
        imagePreviewLayout.add(imagePreview);
        financePreviewLayout.add(invoiceCodeField, ownerField, paymentDateField, statusField, propertyField,
                paidByField, amountPaidField, outstandingAmountField, paymentMethodField);

        financePreviewLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        financePreviewLayout.setMaxWidth("700px");

        onSuccess.accept(null);
    }

}

package ng.org.mirabilia.pms.views.modules.finances.Client;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.finances.invoice.UploadReceipt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FinanceTab extends VerticalLayout {
    private final ComboBox<PropertyType> propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
    private final ComboBox<FinanceStatus> financeStatusFilter = new ComboBox<>("Status", FinanceStatus.values());
    private final DatePicker dateField = new DatePicker("Date");
    private final TextField searchField = new TextField();
    private final Grid<Finance> financeGrid = new Grid<>(Finance.class, false);

    private final FinanceService financeService;
    private final InvoiceService invoiceService;
    private final UserService userService;

    public FinanceTab(FinanceService financeService, InvoiceService invoiceService, UserService userService) {
        this.financeService = financeService;
        this.invoiceService = invoiceService;
        this.userService = userService;

        setSpacing(true);
        setPadding(false);
        addClassName("client-invoice-tab");

        searchField.setPlaceholder("Search Properties");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());

        dateField.addValueChangeListener(e -> updateGridItems());
        dateField.setClearButtonVisible(true);

        propertyTypeFilter.addValueChangeListener(e -> updateGridItems());
        propertyTypeFilter.addClassNames("custom-filter col-sm-6 col-xs-6 finance-combo-filter");
        propertyTypeFilter.setClearButtonVisible(true);
        propertyTypeFilter.setItemLabelGenerator(PropertyType::getDisplayName);

        financeStatusFilter.addValueChangeListener(e -> updateGridItems());
        financeStatusFilter.addClassNames("custom-filter col-sm-6 col-xs-6");
        financeStatusFilter.setClearButtonVisible(true);

        Button uploadReceiptBtn = new Button("Upload Receipt");
        uploadReceiptBtn.addClassName("receipt-btn");
        uploadReceiptBtn.addClickListener(e -> openAddPropertyDialog());

        HorizontalLayout searchReceipt = new HorizontalLayout(uploadReceiptBtn, searchField);
        searchReceipt.addClassName("search-receipt-layout");
        HorizontalLayout filterLayout = new HorizontalLayout(searchReceipt, dateField, propertyTypeFilter, financeStatusFilter);
        filterLayout.addClassName("finance-filter");

        Div invoiceContent = new Div();
        invoiceContent.setText("Finance History");
        HorizontalLayout titleAndFilters = new HorizontalLayout(invoiceContent, filterLayout);
        titleAndFilters.addClassName("finance-filter-horizontal");

        financeGrid.addClassName("custom-grid");

        financeGrid.addColumn(Finance::getInvoice).setSortable(true).setAutoWidth(true).setHeader("Invoice Code");
//        financeGrid.addColumn(finance -> {
//            User clientName = finance.getInvoice().getClientName();
//            return clientName != null ? clientName.getFirstName() + " " + clientName.getLastName() : "N/A";
//        }).setSortable(true).setAutoWidth(true).setHeader("Owner");

        financeGrid.addColumn(finance -> finance.getPaymentDate() != null
                ? finance.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : "").setHeader("Payment Date").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(new ComponentRenderer<>(finance -> {
            Div statusDiv = new Div();
            statusDiv.setText(finance.getPaymentStatus().name());
            statusDiv.addClassName("finance-status");

            if (finance.getPaymentStatus() == FinanceStatus.PENDING) {
                statusDiv.getStyle().setBackground("#ff9b10");
                statusDiv.getStyle().setColor("black");
            } else if (finance.getPaymentStatus() == FinanceStatus.APPROVED) {
                statusDiv.getStyle().setBackground("green");
                statusDiv.getStyle().setColor("white");
            }
            return statusDiv;
        })).setHeader("Status").setAutoWidth(true);
//        financeGrid.addColumn(finance -> finance.getInvoice().getPropertyTitle()).setSortable(true).setAutoWidth(true).setHeader("Property");
//        financeGrid.addColumn(Finance::getPaidBy).setSortable(true).setAutoWidth(true).setHeader("Paid By");
        financeGrid.addColumn(finance -> new DecimalFormat("#,###").format(finance.getInvoice().getPropertyPrice())).setSortable(true).setAutoWidth(true).setHeader("Property Price");
        financeGrid.addColumn(finance -> new DecimalFormat("#,###").format(finance.getAmountPaid())).setHeader("Amount Paid").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(Finance::getOutstandingFormattedToString).setHeader("Outstanding Amount").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(Finance::getPaymentMethod).setHeader("Payment Method").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(new ComponentRenderer<>(finance -> {
            if (finance.getReceiptImage() != null && finance.getReceiptImage().getReceiptImage() != null) {

                StreamResource streamResource = new StreamResource("receipt_" + finance.getId() + ".png",
                        () -> new ByteArrayInputStream(finance.getReceiptImage().getReceiptImage()));

                Anchor downloadLink = new Anchor(streamResource,  " Download");
                downloadLink.getElement().setAttribute("download", true);
                return downloadLink;
            } else {
                return new Div(new Text("No Receipt"));
            }
        })).setHeader("Receipt").setAutoWidth(true).setSortable(false);

        financeGrid.addItemClickListener(e -> openFinanceDetailsDialog(e.getItem()));

        add(titleAndFilters, financeGrid);

        updateGridItems();
    }

    public void updateGridItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        User loggedInUser = userService.findByUsername(loggedInUsername);
        if (loggedInUser == null) {
            financeGrid.setItems(List.of());
            return;
        }

        List<Finance> filteredFinances = financeService.searchFinanceByUserId(loggedInUser, searchField.getValue(),
                propertyTypeFilter.getValue(), financeStatusFilter.getValue(), dateField.getValue());

        financeGrid.setItems(filteredFinances);
    }



    private void openAddPropertyDialog() {
        UploadReceipt uploadReceipt = new UploadReceipt(financeService, invoiceService, userService, (v) -> updateGridItems());
        uploadReceipt.open();
    }

    private void openFinanceDetailsDialog(Finance finance) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeight("auto");
        dialog.setCloseOnOutsideClick(true);

        FormLayout formLayout = new FormLayout();

        TextField invoiceCodeField = createReadOnlyField("Invoice Code", finance.getInvoice().getInvoiceCode());
        TextField ownerField = createReadOnlyField("Owner", finance.getInvoice().getClientName() != null
                ? finance.getInvoice().getClientName().getFirstName() + " " + finance.getInvoice().getClientName().getLastName()
                : "N/A");
        TextField paymentDateField = createReadOnlyField("Payment Date", finance.getPaymentDate() != null
                ? finance.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : "N/A");
        TextField statusField = createReadOnlyField("Status", String.valueOf(finance.getPaymentStatus()));
        TextField propertyField = createReadOnlyField("Property", finance.getInvoice().getPropertyTitle());
        TextField paidByField = createReadOnlyField("Paid By", finance.getPaidBy());
        TextField propertyPriceField = createReadOnlyField("Property Price", new DecimalFormat("#,###").format(finance.getInvoice().getPropertyPrice()));
        TextField amountPaidField = createReadOnlyField("Amount Paid", new DecimalFormat("#,###").format(finance.getAmountPaid()));
        TextField outstandingAmountField = createReadOnlyField("Outstanding Amount", finance.getOutstandingFormattedToString());
        TextField paymentMethodField = createReadOnlyField("Payment Method", String.valueOf(finance.getPaymentMethod()));

        StreamResource streamResource = new StreamResource("receipt_" + finance.getId() + ".png",
                () -> new ByteArrayInputStream(finance.getReceiptImage().getReceiptImage()));

        Anchor downloadLink = new Anchor(streamResource, "Download Receipt");
        downloadLink.getElement().setAttribute("download", true); // Ensure the file is downloaded
        formLayout.add(new Div(new Text("Receipt: "), downloadLink));

        formLayout.add(
                invoiceCodeField,
                ownerField,
                paymentDateField,
                statusField,
                propertyField,
                paidByField,
                propertyPriceField,
                amountPaidField,
                outstandingAmountField,
                paymentMethodField
        );

        Button closeButton = new Button("X", event -> dialog.close());
        closeButton.addClassNames("finance-close");

        VerticalLayout dialogContent = new VerticalLayout(closeButton, formLayout);
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);

        dialog.add(dialogContent);

        dialog.open();
    }

    private TextField createReadOnlyField(String label, String value) {
        TextField textField = new TextField(label);
        textField.setValue(value != null ? value : "N/A");
        textField.setReadOnly(true);
        textField.setWidthFull();
        return textField;
    }
}

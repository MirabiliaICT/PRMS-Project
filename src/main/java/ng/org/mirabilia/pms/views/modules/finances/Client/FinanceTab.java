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
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.PaymentReceipt;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.ReceiptImageRepository;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.services.implementations.ReceiptImageService;
import ng.org.mirabilia.pms.views.forms.finances.invoice.UploadReceipt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FinanceTab extends VerticalLayout {
    private final ComboBox<PropertyType> propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
    private final ComboBox<FinanceStatus> financeStatusFilter = new ComboBox<>("Status", FinanceStatus.values());
    private final DatePicker dateField = new DatePicker("Date");
    private final TextField searchField = new TextField();
    private final Grid<Finance> financeGrid = new Grid<>(Finance.class, false);

    private final FinanceService financeService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final ReceiptImageService receiptImageService;
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);



    private final PaymentReceipt paymentReceipt = new PaymentReceipt();

    public FinanceTab(FinanceService financeService, InvoiceService invoiceService, UserService userService, ReceiptImageService receiptImageService) {
        this.financeService = financeService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.receiptImageService = receiptImageService;

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
                statusDiv.getStyle().setBackground("rgb(255 155 16 / 22%)");
                statusDiv.getStyle().setColor("rgb(255 155 16)");
                statusDiv.getStyle().setBorder("2px solid rgb(255 155 16)");
            } else if (finance.getPaymentStatus() == FinanceStatus.APPROVED) {
                statusDiv.getStyle().setBackground("rgb(52 168 83 / 22%)");
                statusDiv.getStyle().setColor("rgb(52 168 83)");
                statusDiv.getStyle().setBorder("2px solid rgb(52 168 83)");
            }
            return statusDiv;
        })).setHeader("Status").setAutoWidth(true);
//        financeGrid.addColumn(finance -> finance.getInvoice().getPropertyTitle()).setSortable(true).setAutoWidth(true).setHeader("Property");
//        financeGrid.addColumn(Finance::getPaidBy).setSortable(true).setAutoWidth(true).setHeader("Paid By");
        financeGrid.addColumn(finance ->"₦" + NumberFormat.getNumberInstance(Locale.US).format(finance.getInvoice().getPropertyPrice())).setSortable(true).setAutoWidth(true).setHeader("Property Price");
        financeGrid.addColumn(finance -> "₦" + NumberFormat.getNumberInstance(Locale.US).format(finance.getAmountPaid())).setHeader("Amount Paid").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(Finance::getOutstandingFormattedToString).setHeader("Outstanding Amount").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(Finance::getPaymentMethod).setHeader("Payment Method").setSortable(true).setAutoWidth(true);
        financeGrid.addColumn(new ComponentRenderer<>(finance -> {
            if (finance != null && finance.getReceiptImage() != null &&
                    finance.getReceiptImage().getReceiptImage() != null) {

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
        UploadReceipt uploadReceipt = new UploadReceipt(financeService, invoiceService, receiptImageService, userService, (v) -> updateGridItems());
        uploadReceipt.open();
    }

    private void openFinanceDetailsDialog(Finance finance) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeight("auto");
        dialog.setCloseOnOutsideClick(true);

        boolean isPendingStatus = finance.getPaymentStatus() == FinanceStatus.PENDING;

        VerticalLayout receiptLayout = new VerticalLayout();
        receiptLayout.setSpacing(false);
        receiptLayout.setPadding(false);

        Image receiptImage = new Image();
        receiptImage.setMaxWidth("100%");
        receiptImage.setHeight("auto");

        if (finance != null && finance.getReceiptImage() != null &&
                finance.getReceiptImage().getReceiptImage() != null) {
            byte[] imageData = finance.getReceiptImage().getReceiptImage();
            StreamResource streamResource = new StreamResource(
                    "receipt_" + finance.getId() + ".png",
                    () -> new ByteArrayInputStream(imageData)
            );
            receiptImage.setSrc(streamResource);
        } else {
            receiptImage.setAlt("No receipt available.");
        }

        receiptLayout.add(receiptImage);


        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/png", "image/jpeg");
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setVisible(isPendingStatus);


        upload.addSucceededListener(event -> {
            try {
                byte[] uploadedBytes = buffer.getInputStream().readAllBytes();

                PaymentReceipt existingReceipt = finance.getReceiptImage();
                if (existingReceipt == null) {
                    existingReceipt = new PaymentReceipt();
                    existingReceipt.setFinance(finance);
                    finance.setReceiptImage(existingReceipt);
                }

                existingReceipt.setReceiptImage(uploadedBytes);
                existingReceipt.setLocalDateTime(LocalDateTime.now());
                financeService.saveFinance(finance);

                StreamResource newResource = new StreamResource(
                        "receipt_" + finance.getId() + ".png",
                        () -> new ByteArrayInputStream(uploadedBytes)
                );
                receiptImage.setSrc(newResource);

                Notification.show("Receipt updated successfully!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (IOException ex) {
                Notification.show("Failed to upload receipt: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

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

        NumberField amountPaidField = new NumberField("Amount Paid");
        amountPaidField.setValue(finance.getAmountPaid() != null ? finance.getAmountPaid().doubleValue() : 0.0);
        amountPaidField.setStep(0.01);
        amountPaidField.setEnabled(isPendingStatus);
        if (!isPendingStatus) {
            amountPaidField.setHelperText("Amount can only be edited when payment status is pending");
        }

        TextField outstandingAmountField = createReadOnlyField("Outstanding Amount", finance.getOutstandingFormattedToString());
        TextField paymentMethodField = createReadOnlyField("Payment Method", String.valueOf(finance.getPaymentMethod()));

        Button saveButton = new Button("Save Changes", event -> {
            if (!isPendingStatus) {
                Notification.show("Changes can only be made when payment status is pending", 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Retrieve and update the amount paid
                double newAmount = amountPaidField.getValue();
                finance.setAmountPaid(BigDecimal.valueOf(newAmount));

                financeService.saveFinance(finance);
                Notification.show("Changes saved successfully!", 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Update the grid to reflect the new amount immediately
                updateGridItems();

                dialog.close();
            } catch (Exception e) {
                Notification.show("Failed to save changes: " + e.getMessage(), 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.setEnabled(isPendingStatus);

        Button deleteButton = new Button("Delete Receipt", event -> {
            if (!isPendingStatus) {
                Notification.show("Deletion is only allowed when payment status is pending", 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Dialog confirmationDialog = new Dialog();
            confirmationDialog.setWidth("400px");
            confirmationDialog.setHeight("auto");

            VerticalLayout confirmationContent = new VerticalLayout();
            confirmationContent.add(new Text("Are you sure you want to delete this receipt? This action cannot be undone."));

            Button confirmButton = new Button("Yes, Delete", confirmEvent -> {
                try {
                    if (finance != null) {
                        financeService.deleteFinance(finance.getId());
                        if (receiptImageService != null && paymentReceipt != null) {
                            receiptImageService.deleteExistingModel(paymentReceipt);
                        }
                        updateGridItems();

                        Notification.show("Finance and receipt deleted successfully!", 3000,
                                        Notification.Position.TOP_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        dialog.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Notification.show("Failed to delete Finance record: " + ex.getMessage(), 3000,
                                    Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } finally {
                    confirmationDialog.close();
                }
            });

            confirmButton.getStyle().setColor("red");

            Button cancelButton = new Button("Cancel", cancelEvent -> confirmationDialog.close());

            HorizontalLayout dialogActions = new HorizontalLayout(confirmButton, cancelButton);
            confirmationContent.add(dialogActions);

            confirmationDialog.add(confirmationContent);
            confirmationDialog.open();
        });
        deleteButton.setEnabled(isPendingStatus);
        deleteButton.getStyle().setColor("red");




        HorizontalLayout actionButtons = new HorizontalLayout(saveButton, deleteButton);
        actionButtons.setSpacing(true);

        receiptLayout.add(new Text("Upload a new receipt"), upload);

        StreamResource streamResource = new StreamResource("receipt_" + finance.getId() + ".png",
                () -> new ByteArrayInputStream(paymentReceipt.getReceiptImage()));

        Anchor downloadLink = new Anchor(streamResource, "Download Receipt");
        downloadLink.getElement().setAttribute("download", true);

        Button closeButton = new Button("X", event -> dialog.close());
        closeButton.addClassNames("finance-close");

        FlexLayout header = new FlexLayout(new H3("Preview Receipt"), closeButton);
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);

        dialog.getHeader().add(header);

        HorizontalLayout top = new HorizontalLayout(new Div(new Text("Receipt: "), downloadLink));
        top.addClassName("receipt-header");

        formLayout.add(
                invoiceCodeField,
                ownerField,
                paymentDateField,
                statusField,
                propertyField,
                paidByField,
                propertyPriceField,
                amountPaidField,
                amountPaidField,
                outstandingAmountField,
                paymentMethodField
        );


        dialog.add(new VerticalLayout(receiptImage, top, receiptLayout, formLayout, actionButtons));
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

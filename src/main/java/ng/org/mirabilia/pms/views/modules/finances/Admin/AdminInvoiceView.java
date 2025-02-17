package ng.org.mirabilia.pms.views.modules.finances.Admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.enums.CreateStatusTag;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.utils.PDFWriter;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.forms.finances.invoice.GenerateInvoice;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CssImport("./themes/my-theme/styles.css")
@Route(value = "adminMain/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminInvoiceView extends VerticalLayout {

    public GenerateInvoice generateInvoice;
    Button generateInvoiceButton;
    Grid<Invoice> invoiceGrid;
    CreateStatusTag createStatusTag;
    PDFWriter pdfWriter;
    DatePicker datePicker;

    UserService userService;
    PropertyService propertyService;
    InvoiceService invoiceService;
    ComboBox<InvoiceStatus> statuses;
    H4 invoiceHistoryText;

    @Autowired
    public AdminInvoiceView(UserService userService, PropertyService propertyService, InvoiceService invoiceService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;

        setSpacing(true);
        setPadding(false);

        Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        closeDialog.getStyle().setMarginLeft("auto").setDisplay(Style.Display.INLINE);

        closeDialog.addClickListener((e) -> {

            generateInvoice.userNameOrUserCode.clear();
            generateInvoice.propertyCode.clear();
            generateInvoice.installmentGrid.removeAllFooterRows();

            // Reset the validation state
            generateInvoice.userNameOrUserCode.setInvalid(false);
            generateInvoice.propertyCode.setInvalid(false);

            generateInvoice.close();
        });

        invoiceGrid = new Grid<>(Invoice.class, false);
        datePicker = new DatePicker();
        datePicker.setPlaceholder("Date");
        statuses = new ComboBox<>();
        statuses.setPlaceholder("Status");

        createStatusTag = new CreateStatusTag();
        pdfWriter = new PDFWriter();
        generateInvoice = new GenerateInvoice(userService, propertyService, invoiceService, (v) -> updateGridItems());

        generateInvoice.setModal(true);
        generateInvoice.setCloseOnOutsideClick(false);
        generateInvoice.setCloseOnEsc(false);

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetFilters());
        resetButton.addClassNames("custom-button custom-reset-button custom-toolbar-button col-sm-6 col-xs-6");
        invoiceHistoryText = new H4("Invoice History");

        generateInvoiceButton = new Button("Generate Invoice", e -> {

            // remove the header and footer of the dialog to avoid duplication
            generateInvoice.getHeader().removeAll();
            generateInvoice.getFooter().removeAll();

            generateInvoice.getHeader().add(new H4("INVOICE DETAILS"), closeDialog);
            generateInvoice.getFooter().add(generateInvoice.previewInvoiceButton);
            generateInvoice.setCloseOnOutsideClick(false);
            generateInvoice.open();
        });

        generateInvoiceButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        generateInvoiceButton.getStyle().setBackgroundColor("#34A853").setColor("#FFFFFF");

        //populating combo box and also adding functions to the filters
        statuses.setItems(InvoiceStatus.values());
        datePicker.addValueChangeListener(e -> filteringByDate());
        statuses.addValueChangeListener(e -> filteringByStatus());

        //horizontal layout for the filters
        HorizontalLayout filtersLayout = new HorizontalLayout();
        filtersLayout.add(generateInvoiceButton, datePicker, statuses, resetButton);

        //inline styling
        invoiceGrid.addClassName("custom-grid");
        invoiceGrid.getStyle().setBorder("none");
        filtersLayout.getStyle().setAlignSelf(Style.AlignSelf.END);
        invoiceHistoryText.getStyle().setMargin("10px");

        configureGrid();
        add(generateInvoice, filtersLayout, invoiceHistoryText, invoiceGrid);
        updateGridItems();
    }

    private void configureGrid() {
        invoiceGrid.addColumn(Invoice::getInvoiceCode)
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Invoice Code");

        invoiceGrid.addColumn(invoice -> invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Issue Date");

        invoiceGrid.addComponentColumn(invoice -> createStatusTag.createStatusTag(invoice.getInvoiceStatus()))
                .setAutoWidth(true)
                .setSortable(true)
                .setHeader("Status");

        invoiceGrid.addColumn(invoice -> invoice.getPropertyType().toString().replace("_", " ")
                        .toLowerCase())
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Property Type");

        invoiceGrid.addColumn(Invoice::getCreatedBy)
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Issued By");

        invoiceGrid.addColumn(invoice -> new DecimalFormat("#,###")
                        .format(invoice.getPropertyPrice()))
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Amount");

        invoiceGrid.addColumn(invoice -> invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .setSortable(true)
                .setAutoWidth(true)
                .setHeader("Due Date");

        invoiceGrid.addComponentColumn(invoice -> {
                    StreamResource pdfResource = createPdfResource(invoice);

                    // Create an Anchor component with the StreamResource
                    Anchor downloadLink = new Anchor(pdfResource, "");
                    downloadLink.getElement().setAttribute("download", true);
                    Icon downloadIcon = new Icon(VaadinIcon.CLOUD_DOWNLOAD_O);
                    downloadLink.add(new Button(downloadIcon));

                    //inline styling
                    downloadIcon.getStyle().setColor("black");
                    downloadLink.getStyle().set("border", "1px solid black").setBorderRadius("7px").setPadding("4px");

                    return downloadLink;
                })
                .setHeader("Download")
                .setAutoWidth(true);

        updateGridItems();
    }

    private void updateGridItems() {
        List<Invoice> invoiceRecords = invoiceService.getAllInvoices();
        invoiceGrid.setItems(invoiceRecords);
    }

    private void filteringByDate() {
        LocalDate dateFilter = datePicker.getValue();
        List<Invoice> invoiceFilteredByDate = invoiceService.searchByDate(dateFilter);
        invoiceGrid.setItems(invoiceFilteredByDate);
    }

    private void filteringByStatus() {
        InvoiceStatus invoiceStatus = statuses.getValue();
        List<Invoice> invoicesFilteredByStatus = invoiceService.searchByInvoiceStatus(invoiceStatus);
        invoiceGrid.setItems(invoicesFilteredByStatus);
    }

    private void resetFilters() {
        datePicker.clear();
        statuses.clear();
        updateGridItems();
    }

    public StreamResource createPdfResource(Invoice invoice) {
        return new StreamResource("Invoice_" + invoice.getInvoiceCode() + ".pdf",
                () -> {
                    byte[] pdfBytes = generateInvoicePdf(invoice);
                    if (pdfBytes == null || pdfBytes.length == 0) {
                        throw new RuntimeException("Failed to generate PDF or PDF is empty.");
                    }
                    return new ByteArrayInputStream(pdfBytes);
                });
    }

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            return pdfWriter.generateInvoicePdf(invoice);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

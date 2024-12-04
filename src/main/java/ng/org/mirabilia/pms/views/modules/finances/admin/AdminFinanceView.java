package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.enums.CreateStatusTag;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.forms.finances.invoice.GenerateInvoice;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminFinanceView extends VerticalLayout {

    public GenerateInvoice generateInvoice;
    Button generateInvoiceButton;
    Grid<Invoice> invoiceGrid;
    CreateStatusTag createStatusTag;

    UserService userService;
    PropertyService propertyService;
    InvoiceService invoiceService;

    @Autowired
    public AdminFinanceView(UserService userService, PropertyService propertyService, InvoiceService invoiceService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;

        invoiceGrid = new Grid<>(Invoice.class, false);
        createStatusTag = new CreateStatusTag();
        generateInvoice = new GenerateInvoice(userService, propertyService, invoiceService, (v) -> updateGridItems());

        generateInvoiceButton = new Button("Generate Invoice", e -> {

            // remove the header and footer of the dialog to avoid duplication
            generateInvoice.getHeader().removeAll();
            generateInvoice.getFooter().removeAll();

            generateInvoice.getHeader().add(new H4("INVOICE DETAILS"));
            generateInvoice.getFooter().add(generateInvoice.previewInvoiceButton);
            generateInvoice.open();
        });

        configureGrid();
        add(generateInvoice, generateInvoiceButton, new H4("Invoice History"), invoiceGrid);
    }

    private void configureGrid(){
        invoiceGrid.addColumn(Invoice::getInvoiceCode).setSortable(true).setAutoWidth(true).setHeader("Invoice Code");
        invoiceGrid.addColumn(invoice -> invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Issue Date");
        invoiceGrid.addComponentColumn(invoice -> createStatusTag.createStatusTag(invoice.getInvoiceStatus())).setAutoWidth(true).setSortable(true).setHeader("Status");
        invoiceGrid.addColumn(invoice -> invoice.getPropertyType().toString().replace("_", " ").toLowerCase()).setSortable(true).setAutoWidth(true).setHeader("Property Type");
        invoiceGrid.addColumn(Invoice::getCreatedBy).setSortable(true).setAutoWidth(true).setHeader("Issued By");
        invoiceGrid.addColumn(invoice -> new DecimalFormat("#,###").format(invoice.getPropertyPrice())).setSortable(true).setAutoWidth(true).setHeader("Amount");
        invoiceGrid.addColumn(invoice -> invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Due Date");
        updateGridItems();
    }

    private void updateGridItems() {
        List<Invoice> invoiceRecords = invoiceService.getAllInvoices();
        invoiceGrid.setItems(invoiceRecords);
    }

}

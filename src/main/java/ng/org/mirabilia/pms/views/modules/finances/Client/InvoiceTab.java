package ng.org.mirabilia.pms.views.modules.finances.Client;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.CreateStatusTag;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceTab extends VerticalLayout {
    private final ComboBox<PropertyType> propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
    private final ComboBox<InvoiceStatus> invoiceStatusFilter = new ComboBox<>("Status", InvoiceStatus.values());

    private final DatePicker dateField = new DatePicker("Date");
    private final TextField searchField = new TextField();
    Grid<Invoice> invoiceGrid = new Grid<>(Invoice.class, false);
    CreateStatusTag createStatusTag =  new CreateStatusTag();
    InvoiceService invoiceService;
    UserService userService;

    public InvoiceTab(InvoiceService invoiceService, UserService userService) {
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
        searchField.getElement().getStyle().set("background-color", "white");
        searchField.setClearButtonVisible(true);

        dateField.addValueChangeListener(e -> updateGridItems());
        dateField.setClearButtonVisible(true);


        propertyTypeFilter.addValueChangeListener(e -> updateGridItems());
        propertyTypeFilter .addClassNames("custom-filter col-sm-6 col-xs-6");
        propertyTypeFilter.setClearButtonVisible(true);
        propertyTypeFilter.setItemLabelGenerator(PropertyType::getDisplayName);

        invoiceStatusFilter.addValueChangeListener(e -> updateGridItems());
        invoiceStatusFilter.addClassNames("custom-filter col-sm-6 col-xs-6");
        invoiceStatusFilter.setClearButtonVisible(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout(searchField, dateField, propertyTypeFilter, invoiceStatusFilter);
        horizontalLayout.addClassName("finance-filter");
        Div invoiceContent = new Div();
        invoiceContent.setText("Invoice History");
        HorizontalLayout horizontalLayout1 = new HorizontalLayout(invoiceContent, horizontalLayout);
        horizontalLayout1.addClassName("finance-filter-horizontal");

        invoiceGrid.addColumn(Invoice::getInvoiceCode).setSortable(true).setAutoWidth(true).setHeader("Invoice Code");
        invoiceGrid.addColumn(invoice -> invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Issue Date");
        invoiceGrid.addComponentColumn(invoice -> createStatusTag.createStatusTag(invoice.getInvoiceStatus())).setAutoWidth(true).setSortable(true).setHeader("Status");
        invoiceGrid.addColumn(invoice -> invoice.getPropertyType().toString().replace("_", " ").toLowerCase()).setSortable(true).setAutoWidth(true).setHeader("Property Type");
        invoiceGrid.addColumn(Invoice::getCreatedBy).setSortable(true).setAutoWidth(true).setHeader("Issued By");
        invoiceGrid.addColumn(invoice -> new DecimalFormat("#,###").format(invoice.getPropertyPrice())).setSortable(true).setAutoWidth(true).setHeader("Amount");
        invoiceGrid.addColumn(invoice -> invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Due Date");
        invoiceGrid.addClassNames("custom-grid invoice-grid");

        add(horizontalLayout1, invoiceGrid);
        updateGridItems();

    }


    public void updateGridItems() {
        String keyword = searchField.getValue();
        LocalDate date = dateField.getValue();
        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
        InvoiceStatus selectedInvoiceStatus = invoiceStatusFilter.getValue();


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            invoiceGrid.setItems(List.of());
            return;
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            invoiceGrid.setItems(List.of());
            return;
        }

        List<Invoice> invoiceRecords;
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            invoiceRecords = invoiceService.searchInvoicesByFilters(keyword, date, selectedInvoiceStatus, selectedPropertyType);
        } else {
            invoiceRecords = invoiceService.searchInvoicesByUserId(keyword, date, selectedInvoiceStatus, selectedPropertyType, user.getId());
            System.out.println("Client " + user.getRoles());
            System.out.println("Authority is not null and is ADMINNNNNN");
        }

        invoiceGrid.setItems(invoiceRecords);
    }
}

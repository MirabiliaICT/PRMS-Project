package ng.org.mirabilia.pms.views.modules.finances.Client;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.services.implementations.ReceiptImageService;
import ng.org.mirabilia.pms.views.modules.finances.FinancesView;

@Route(layout = FinancesView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed("CLIENT")
public class ClientFinanceView extends VerticalLayout {

    private final Tabs tabs;
    private final InvoiceTab invoiceTab;
    private final FinanceTab financeTab;
//    private final Invoice invoice;


//    private final Div contentContainer;
    InvoiceService invoiceService;
    UserService userService;
    FinanceService financeService;
    ReceiptImageService receiptImageService;
    public ClientFinanceView(FinanceService financeService, InvoiceService invoiceService, UserService userService, ReceiptImageService receiptImageService) {
        this.financeService =financeService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.receiptImageService = receiptImageService;
//        this.invoice = invoice;

        getStyle().set("gap", "0");
        addClassNames("client-finance-content finance-content");

        tabs = new Tabs();
//        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.setWidthFull();

        invoiceTab = new InvoiceTab(invoiceService, userService);
        financeTab = new FinanceTab(financeService, invoiceService, userService, receiptImageService);

        Tab invoiceTabItem = new Tab("Invoice");
        Tab financeTabItem = new Tab("Finance");

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == invoiceTabItem) {
                remove(financeTab);
                invoiceTab.updateGridItems();
                add(invoiceTab);
            } else {
                remove(invoiceTab);
                financeTab.updateGridItems();
                add(financeTab);
            }
        });

        tabs.add(financeTabItem, invoiceTabItem);
        add(tabs, financeTab);



//        financeButton = new Button("Finances", event -> showFinanceContent());
//        invoiceButton = new Button("Invoices", event -> showInvoiceContent());
//        financeButton.addClassName("client-finance-button");
//        invoiceButton.addClassName("client-invoice-button");
//
//        financeButton.addClickListener(event -> {
//           financeButton.getStyle().setBackground("#ffffff");
//           financeButton.getStyle().set("color", "rgba(22, 40, 104, 1)");
//           invoiceButton.getStyle().set("color", "#000000");
//           invoiceButton.getStyle().setBackground("inherit");
//        });
//
//        invoiceButton.addClickListener(event -> {
//            invoiceButton.getStyle().setBackground("#ffffff");
//            invoiceButton.getStyle().set("color", "rgba(22, 40, 104, 1)");
//            financeButton.getStyle().set("color", "#000000");
//            financeButton.getStyle().setBackground("inherit");
//        });
//
//
//
//        HorizontalLayout horizontalLayout = new HorizontalLayout(financeButton, invoiceButton);
//        horizontalLayout.addClassName("client-finance-horizontal");
//
//        contentContainer = new Div();
//        contentContainer.setClassName("content-container");
//        contentContainer.setWidthFull();
//
//        Div financeContainer = new Div(horizontalLayout, contentContainer);
//        financeContainer.setClassName("finance-container");
//
//        add(financeContainer);
//        showFinanceContent();

//    }

//    private void showFinanceContent() {
        //        contentContainer.removeAll();
        //        financeButton.getStyle().setColor("rgba(22, 40, 104, 1)");
        //        financeButton.getStyle().setBackground("#ffffff");
        //        financeButton.getStyle().setPadding("8px");
//
//        Div financeContent = new Div();
//        financeContent.setText("Finance History");
//        contentContainer.add(financeContent);
//    }

//    private void showInvoiceContent() {
//        contentContainer.removeAll();
//        invoiceButton.getStyle().set("color", "#000000");
//        invoiceButton.getStyle().setBackground("inherit");
//        invoiceButton.getStyle().setPadding("8px");
//        Div invoiceContent = new Div();
//        invoiceContent.setText("Invoice History");
//
//        searchField.setPlaceholder("Search Properties");
//        searchField.setClearButtonVisible(true);
//        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
//        searchField.setValueChangeMode(ValueChangeMode.EAGER);
//        searchField.addValueChangeListener(e -> updateGridItems());
//        searchField.getElement().getStyle().set("background-color", "white");
//        searchField.setClearButtonVisible(true);
//
//        dateField.addValueChangeListener(e -> updateGridItems());
//        dateField.setClearButtonVisible(true);
//
//
//        propertyTypeFilter.addValueChangeListener(e -> updateGridItems());
//        propertyTypeFilter .addClassNames("custom-filter col-sm-6 col-xs-6");
//        propertyTypeFilter.setClearButtonVisible(true);
//        propertyTypeFilter.setItemLabelGenerator(PropertyType::getDisplayName);
//
//        invoiceStatusFilter.addValueChangeListener(e -> updateGridItems());
//        invoiceStatusFilter.addClassNames("custom-filter col-sm-6 col-xs-6");
//        invoiceStatusFilter.setClearButtonVisible(true);
//
//        HorizontalLayout horizontalLayout = new HorizontalLayout(searchField, dateField, propertyTypeFilter, invoiceStatusFilter);
//        horizontalLayout.addClassName("finance-filter");
//        HorizontalLayout horizontalLayout1 = new HorizontalLayout(invoiceContent, horizontalLayout);
//        horizontalLayout1.addClassName("finance-filter-horizontal");
//
//        configureInvoiceGrid();
//        contentContainer.add(horizontalLayout1, invoiceGrid);



    }

//    private void configureInvoiceGrid(){
//        invoiceGrid.addColumn(Invoice::getInvoiceCode).setSortable(true).setAutoWidth(true).setHeader("Invoice Code");
//        invoiceGrid.addColumn(invoice -> invoice.getIssueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Issue Date");
//        invoiceGrid.addComponentColumn(invoice -> createStatusTag.createStatusTag(invoice.getInvoiceStatus())).setAutoWidth(true).setSortable(true).setHeader("Status");
//        invoiceGrid.addColumn(invoice -> invoice.getPropertyType().toString().replace("_", " ").toLowerCase()).setSortable(true).setAutoWidth(true).setHeader("Property Type");
//        invoiceGrid.addColumn(Invoice::getCreatedBy).setSortable(true).setAutoWidth(true).setHeader("Issued By");
//        invoiceGrid.addColumn(invoice -> new DecimalFormat("#,###").format(invoice.getPropertyPrice())).setSortable(true).setAutoWidth(true).setHeader("Amount");
//        invoiceGrid.addColumn(invoice -> invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Due Date").setSortable(true).setAutoWidth(true).setHeader("Due Date");
//        invoiceGrid.addClassNames("custom-grid invoice-grid");
//        updateGridItems();
//    }

//    private void updateGridItems() {
//        String keyword = searchField.getValue();
//        LocalDate date = dateField.getValue();
//        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
//        InvoiceStatus selectedInvoiceStatus = invoiceStatusFilter.getValue();
//
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            invoiceGrid.setItems(List.of());
//            return;
//        }
//
//        User user = userService.findByUsername(authentication.getName());
//        if (user == null) {
//            invoiceGrid.setItems(List.of());
//            return;
//        }
//
//        List<Invoice> invoiceRecords;
//        if (authentication.getAuthorities().stream()
//                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
//            invoiceRecords = invoiceService.searchInvoicesByFilters(keyword, date, selectedInvoiceStatus, selectedPropertyType);
//        } else {
//            invoiceRecords = invoiceService.searchInvoicesByUserId(keyword, date, selectedInvoiceStatus, selectedPropertyType, user.getId());
//            System.out.println("Client " + user.getRoles());
//            System.out.println("Authority is not null and is ADMINNNNNN");
//        }
//
//        invoiceGrid.setItems(invoiceRecords);
//    }

}

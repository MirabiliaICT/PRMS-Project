package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.MainView;


@Route(value = "admin/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminMainView extends VerticalLayout {
    private final AdminInvoiceView adminInvoiceView;
    private final JakartaMailService mailService;
    AdminFinancesView adminFinancesView;


    UserService userService;
    PropertyService propertyService;
    InvoiceService invoiceService;
    Tabs tabs;
    FinanceService financeService;
    FinanceRepository financeRepository;

    public AdminMainView(UserService userService, PropertyService propertyService,
                         InvoiceService invoiceService, FinanceService financeService, JakartaMailService mailService,
                         FinanceRepository financeRepository) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;
        this.financeService = financeService;
        this.mailService = mailService;
        this.financeRepository = financeRepository;

        getStyle().set("gap", "0");
        addClassNames("client-finance-content finance-content");

        tabs = new Tabs();
        tabs.setWidthFull();

        adminInvoiceView = new AdminInvoiceView(userService, propertyService, invoiceService, mailService);
        adminFinancesView = new AdminFinancesView(financeService, financeRepository);

        Tab invoiceTabItem = new Tab("Invoice");
        Tab financeTabItem = new Tab("Finance");
        tabs.add(financeTabItem, invoiceTabItem);

        tabs.setSelectedTab(financeTabItem);
        add(tabs, adminFinancesView);

        tabs.addSelectedChangeListener(event -> {
            remove(adminFinancesView, adminInvoiceView);
            if (event.getSelectedTab() == financeTabItem) {
                add(adminFinancesView);
            } else if (event.getSelectedTab() == invoiceTabItem) {
                add(adminInvoiceView);
            }
        });
    }
}

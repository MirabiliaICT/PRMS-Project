package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.views.modules.finances.FinancesView;
import ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs.MonthlyTab;
import ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs.YearlyTab;

@Route(layout = FinancesView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminChartView extends VerticalLayout {
    private final Tabs tabs;
    private final Tab yearlyTabItem;
    private final Tab monthlyTabItem;
    private final YearlyTab yearlyTab;
    private final MonthlyTab monthlyTab;

    FinanceRepository financeRepository;

    public AdminChartView(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;

        getStyle().set("gap", "0");

        // Initialize tabs and components
        tabs = new Tabs();
        tabs.setWidthFull();

        yearlyTabItem = new Tab("Year");
        monthlyTabItem = new Tab("Month");
        tabs.add(yearlyTabItem, monthlyTabItem);

        yearlyTab = new YearlyTab(financeRepository);
        monthlyTab = new MonthlyTab(financeRepository);

        // Add tabs and set default selection "client-finance-content",
        tabs.setSelectedTab(yearlyTabItem);
        add(tabs, yearlyTab);

        // Add a listener for tab selection
        tabs.addSelectedChangeListener(event -> {
            remove(yearlyTab, monthlyTab); // Remove both tabs
            if (event.getSelectedTab() == yearlyTabItem) {
                add(yearlyTab);
            } else if (event.getSelectedTab() == monthlyTabItem) {
                add(monthlyTab);
            }
        });
    }
}

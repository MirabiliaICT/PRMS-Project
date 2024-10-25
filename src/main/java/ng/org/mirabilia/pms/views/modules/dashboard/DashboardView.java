package ng.org.mirabilia.pms.views.modules.dashboard;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.chart.ChartView;

@Route(value = "", layout = MainView.class)
@PageTitle("Property Management System")
@RolesAllowed({"ADMIN","MANAGER","AGENT","ACCOUNTANT", "CLIENT", "IT_SUPPORT"})
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSpacing(true);
        setPadding(true);
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        ChartView chartView = new ChartView();
        chartView.setWidth("50%");
        chartView.setHeight("30vh");

        HorizontalLayout metricsLayout = createMetricsLayout();
        HorizontalLayout overviewAndCustomers = createOverviewAndCustomers();
        add(metricsLayout, overviewAndCustomers, chartView);


    }

    private HorizontalLayout createMetricsLayout() {
        HorizontalLayout metricsLayout = new HorizontalLayout();
        metricsLayout.setWidthFull();
        metricsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Div totalProperties = createMetricBox("Total Properties", "0", "#0000ff");
        Div totalLandProperties = createMetricBox("Total Land Properties", "0", "#00b140");
        Div totalResidentialProperties = createMetricBox("Total Residential Properties", "0", "#ffa500");
        Div totalClients = createMetricBox("Total Clients", "0", "#800080");
        Div totalStaff = createMetricBox("Total Staff", "0", "#808080");

        metricsLayout.add(totalProperties, totalLandProperties, totalResidentialProperties, totalClients, totalStaff);
        return metricsLayout;
    }

    private Div createMetricBox(String title, String value, String color) {
        Div box = new Div();
        box.addClassName("metric-box");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "2rem").set("color", color).set("font-weight", "bold");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "1rem").set("color", "#5a5a5a");

        box.add(valueSpan, titleSpan);
        return box;
    }

    private HorizontalLayout createOverviewAndCustomers() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);

        VerticalLayout financialOverview = new VerticalLayout();
        financialOverview.addClassName("financial-overview");

        Span overviewTitle = new Span("Financial Overview");
        overviewTitle.getStyle().set("font-weight", "bold").set("font-size", "1.25rem");
        financialOverview.add(overviewTitle, createFinancialOverview());

        VerticalLayout recentCustomers = new VerticalLayout();
        recentCustomers.addClassName("recent-customers");

        Span customersTitle = new Span("Recent Customers");
        customersTitle.getStyle().set("font-weight", "bold").set("font-size", "1.25rem");
        recentCustomers.add(customersTitle, new Span("View All"));

        layout.add(financialOverview, recentCustomers);
        return layout;
    }

    private VerticalLayout createFinancialOverview() {
        VerticalLayout overviewContent = new VerticalLayout();
        overviewContent.addClassName("financial-overview-content");

        Div totalBought = createFinancialBox("₦0", "Total Properties Bought", "#0000ff");
        Div totalOutstanding = createFinancialBox("₦0", "Total Outstanding Amount", "#ff0000");
        Div totalPaid = createFinancialBox("₦0", "Total Amount Paid", "#00b140");

        overviewContent.add(totalBought, totalOutstanding, totalPaid);
        return overviewContent;
    }

    private Div createFinancialBox(String value, String title, String color) {
        Div box = new Div();
        box.addClassName("financial-box");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "1.5rem").set("color", color).set("font-weight", "bold");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "1rem").set("color", "#5a5a5a");

        box.add(valueSpan, titleSpan);
        return box;
    }
}

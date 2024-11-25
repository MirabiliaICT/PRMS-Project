package ng.org.mirabilia.pms.views.modules.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.services.DashboardService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.chart.ChartView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "", layout = MainView.class)
@PageTitle("Property Management System")
@RolesAllowed({"ADMIN","MANAGER","AGENT","ACCOUNTANT", "CLIENT", "IT_SUPPORT"})
public class DashboardView extends VerticalLayout {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    UserImageService userImageService;

    @Autowired
    PropertyService propertyService;

    Div layer1;

    Div layer2;

    Div layer3;
    Div layer4;
    Div layer5;
    Div layer6;
    @Autowired
    public DashboardView(DashboardService dashboardService, UserService userService, UserImageService userImageService, PropertyService propertyService) {
        setSpacing(true);
        setPadding(true);

        this.dashboardService = dashboardService;
        this.userService = userService;
        this.userImageService = userImageService;
        this.propertyService = propertyService;

        getStyle().setBackgroundColor("#F7F5F5");
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        ChartView chartView = new ChartView();
        chartView.setWidth("50%");
        chartView.setHeight("30vh");

        HorizontalLayout metricsLayout = createMetricsLayout();
        HorizontalLayout overviewAndCustomers = createOverviewAndCustomers();


        configureLayer1();
        configureLayer2();
        configureLayer3();
        configureLayer4();
        configureLayer5();
        configureLayer6();
        add(layer1, layer2, layer3, layer4, layer5, layer6);


    }

    void configureLayer1(){
        layer1 = new Div();
        H2 dashboard = new H2("Dashboard");
        layer1.add(dashboard);
    }
    void configureLayer2(){
        layer2 = new Div();
        layer2.getStyle().setBackgroundColor("white");
        layer2.getStyle().setBorderRadius("8px");
        layer2.getStyle().setPadding("8px");
        layer2.getStyle().setDisplay(Style.Display.FLEX);
        H4 filterBy = new H4("Filter by");
        layer2.add(filterBy);
    }

    void configureLayer3(){
        layer3 = new Div();
        layer3.getStyle().setDisplay(Style.Display.FLEX);

        Div propertyCard = metricsCard("Properties for Sale","","1,000","");
        propertyCard.getStyle().setMarginRight("8px");
        Div residentialCard = metricsCard("Total Residential Properties","","534","");
        residentialCard.getStyle().setMarginRight("8px");
        Div landedCard = metricsCard("Total Landed Properties","","437","");
        landedCard.getStyle().setMarginRight("8px");
        Div customerCard = metricsCard("Total Customers","","2,100","");

        layer3.add(propertyCard,residentialCard,landedCard,customerCard);
    }

    Div metricsCard(String label,String labelColor, String metric, String metricColor){
        H4 l = new H4(label);
        l.getStyle().setTextAlign(Style.TextAlign.CENTER);
        l.getStyle().setColor(labelColor);
        H1 m = new H1(metric);
        m.getStyle().setColor(metricColor);
        Div card = new Div(l,m);
        card.getStyle().setDisplay(Style.Display.FLEX);
        card.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);
        card.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        card.getStyle().setAlignItems(Style.AlignItems.CENTER);
        card.getStyle().setPadding("8px");
        card.getStyle().setBackgroundColor("white");
        card.getStyle().setWidth("25%");
        card.getStyle().setHeight("103px");
        card.getStyle().setBorderRadius("8px");
        return card;
    }

    void configureLayer4(){
        layer4 = new Div();
        layer4.getStyle().setDisplay(Style.Display.FLEX);

        Div left = new Div();
        left.setWidth("70%");
        left.getStyle().setBackgroundColor("white");
        left.getStyle().setBorderRadius("8px");

        Div space = new Div();
        space.setWidth("8px");

        Div right = recentCustomerCard();
        right.setWidth("30%");

        layer4.add(left,space,right);
    }

    Div recentCustomerCard(){
        Div card = new Div();
        card.getStyle().setDisplay(Style.Display.FLEX);
        card.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);
        card.getStyle().setPadding("8px");
        card.getStyle().setBackgroundColor("white");
        card.getStyle().setBorderRadius("8px");
        H3 title = new H3("Recent Customers");

        List<User> users = userService.getAllUsers();
        System.out.println("\n\n\nRecentCus\n\n" + users.size() + " \n\n\n");

        card.add(title);
        users.forEach((user)->{
            card.add(customerCardEntryView(user));
        });

        Div footer = new Div();
        footer.getStyle().setDisplay(Style.Display.FLEX);
        footer.getStyle().setJustifyContent(Style.JustifyContent.END);
        Button viewAll = new Button("View all");
        viewAll.getStyle().setBackgroundColor("white");
        footer.add(viewAll);
        card.add(footer);
        return card;
    }

    Div customerCardEntryView(User user){
        Div horizontal = new Div();
        horizontal.getStyle().setDisplay(Style.Display.FLEX);
        horizontal.getStyle().setAlignItems(Style.AlignItems.START);
        horizontal.getStyle().setMarginBottom("4px");

        Image image = new Image("/images/john.png","");
        image.setWidth("42px");
        image.setHeight("42px");
        image.getStyle().setBorderRadius("8px");
        image.getStyle().setMarginRight("8px");

        Div vertical = new Div();
        vertical.getStyle().setDisplay(Style.Display.FLEX);
        vertical.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);

        Span name = new Span(user.getFirstName());
        name.getStyle().setFontSize("16px");

        Span role = new Span("Owner");
        role.getStyle().setFontSize("10px");

        vertical.add(name,role);

        horizontal.add(image, vertical);
        return horizontal;
    }

    void configureLayer5(){
        layer5 = new Div();
        layer5.getStyle().setDisplay(Style.Display.FLEX);

        Div boughtCard = metricsCard("Total Properties Bought","#2F2F2F","$3,545,654","#6C5DD3");
        boughtCard.getStyle().setMarginRight("8px");
        Div revenueCard = metricsCard("Total Revenue","#2F2F2F","$756,876","#F4A74B");
        revenueCard.getStyle().setMarginRight("8px");
        Div paymentCard = metricsCard("Total Payments Completed","#2F2F2F","$756,876","#F4A74B");
        paymentCard.getStyle().setMarginRight("8px");
        Div outstandingCard = metricsCard("Total Outstanding Payments","#2F2F2F","2,100","#EB4444");

        layer5.add(boughtCard,revenueCard,paymentCard,outstandingCard);

    }

    void configureLayer6(){
        layer6 = new Div();
        layer6.getStyle().setMarginBottom("16px");
        layer6.getStyle().setDisplay(Style.Display.FLEX);

        Div left = new Div();
        left.setWidth("50%");
        left.setHeight("264px");
        left.getStyle().setBackgroundColor("white");
        left.getStyle().setBorderRadius("8px");

        Div spacer = new Div();
        spacer.setWidth("8px");

        Div right = new Div();
        right.setWidth("50%");
        right.setHeight("264px");
        right.getStyle().setBackgroundColor("white");
        right.getStyle().setBorderRadius("8px");

        Div gridCard = constructGridCard();
        right.add(gridCard);

        layer6.add(left,spacer, right);
    }

    Div constructGridCard(){
        Div card = new Div();
        card.setHeight("264px");
        card.getStyle().setDisplay(Style.Display.FLEX);
        card.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);

        Div header = new Div();
        header.getStyle().setDisplay(Style.Display.FLEX);
        header.getStyle().setFlexDirection(Style.FlexDirection.ROW);
        header.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        header.getStyle().setPadding("8px");

        H5 propertyList = new H5("Property Listing");

        Button seeAll = new Button("See all");
        seeAll.getStyle().setBackgroundColor("white");

        header.add(propertyList, seeAll);

        Grid<Property> propertyGrid = new Grid<>(Property.class);
        propertyGrid.setColumns("street","title","noOfBedrooms","noOfBathrooms");
        propertyGrid.setItems(propertyService.getAllProperties());

        card.add(header, propertyGrid);
        return card;
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

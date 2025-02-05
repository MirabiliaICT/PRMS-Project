package ng.org.mirabilia.pms.views.modules.dashboard;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.repositories.PropertyRepository;
import ng.org.mirabilia.pms.services.DashboardService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.chart.ChartView;
import ng.org.mirabilia.pms.views.modules.dashboard.charts.MonthlyDashBoardChart;
import ng.org.mirabilia.pms.views.modules.dashboard.charts.PropertyChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.pekkam.Canvas;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainView.class)
@PageTitle("Property Management System")
@RolesAllowed({"ADMIN","MANAGER","AGENT","ACCOUNTANT", "CLIENT", "IT_SUPPORT"})
@JsModule("chart.js")
@JsModule("https://cdn.jsdelivr.net/npm/chart.js")
public class DashboardView extends VerticalLayout {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    UserImageService userImageService;

    @Autowired
    PropertyService propertyService;

    @Autowired
    FinanceRepository financeRepository;

    @Autowired
    PropertyRepository propertyRepository;

    Div layer3;
    Div layer4;
    Div layer5;
    Div layer6;

    @Autowired
    public DashboardView(DashboardService dashboardService, UserService userService, UserImageService userImageService,
                         PropertyService propertyService, FinanceRepository financeRepository, PropertyRepository propertyRepository) {
        setSpacing(true);
        setPadding(true);

        this.dashboardService = dashboardService;
        this.userService = userService;
        this.userImageService = userImageService;
        this.propertyService = propertyService;
        this.financeRepository = financeRepository;
        this.propertyRepository = propertyRepository;

        getStyle().setBackgroundColor("#F7F5F5");
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        ChartView chartView = new ChartView();
        chartView.setWidth("50%");
        chartView.setHeight("30vh");

        configureLayer3();
        configureLayer4();
        configureLayer5();
        configureLayer6();

        add(layer3, layer4, layer5, layer6);
    }

    void configureLayer3(){
        layer3 = new Div();
        layer3.getStyle().setDisplay(Style.Display.FLEX);

        Div propertyCard = metricsCard("Properties for Sale","",
                String.valueOf(dashboardService.totalPropertiesForSale()),"");
        propertyCard.getStyle().setMarginRight("8px");
        Div residentialCard = metricsCard("Total Residential Properties","",
                String.valueOf(dashboardService.totalResidentialProperties()),"");
        residentialCard.getStyle().setMarginRight("8px");
        Div landedCard = metricsCard("Total Landed Properties","",
                String.valueOf(dashboardService.totalLandedProperties()),"");
        landedCard.getStyle().setMarginRight("8px");
        Div customerCard = metricsCard("Total Customers","",
                String.valueOf(dashboardService.totalCustomers()),"");

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
        layer4.setHeight("264px");

        Div left = new Div();
        left.setWidth("75%");
        left.getStyle().setBackgroundColor("white");
        left.getStyle().setBorderRadius("8px");
        left.add(layer4Graph());

        Div space = new Div();
        space.setWidth("8px");

        Div right = recentCustomerCard();
        right.setWidth("23%");

        layer4.add(left,space,right);
    }

    Div recentCustomerCard(){
        Div card = new Div();
        card.getStyle().setDisplay(Style.Display.FLEX);
        card.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);
        card.getStyle().setPadding("10px");
        card.getStyle().setBackgroundColor("white");
        card.getStyle().setBorderRadius("8px");

        H5 title = new H5("Recent Customers");
        title.getStyle().setMarginBottom("10px");

        List<User> users = userService.getAllUsers();

        card.add(title);

        //Only fetch last 3 Client
        int clientCnt = 0;
        List<User> recentClients = new ArrayList<>();

        for(int i = users.size()-1; i > -1; i--){
            if(clientCnt < 3 && users.get(i).getRoles().contains(Role.CLIENT)){
                recentClients.add(users.get(i));
                clientCnt++;
            }
        }
        recentClients.forEach((user)->{
            card.add(customerCardEntryView(user));

        });

        Div footer = new Div();
        footer.getStyle().setDisplay(Style.Display.FLEX);
        footer.getStyle().setJustifyContent(Style.JustifyContent.END);
        footer.getStyle().setFlexGrow("1");

        Button viewAll = new Button("View all");
        viewAll.getStyle().setBackgroundColor("white").setFontSize("10px");
        viewAll.getStyle().setAlignSelf(Style.AlignSelf.END).setMarginTop("0px").setPaddingTop("0px");
        viewAll.addClickListener((e)->{
            UI.getCurrent().navigate("/users");
        });

        footer.add(viewAll);

        Div spacer = new Div();
        spacer.getStyle().setFlexGrow("2");

        card.add(spacer,footer);
        return card;
    }

    Div layer4Graph() {
        Div parent = new Div();
        parent.getStyle().setBorderRadius("8px");
        parent.setHeightFull();

        Div graphL1 = new Div();
        graphL1.setHeight("20%");
        graphL1.getStyle().setDisplay(Style.Display.FLEX);
        graphL1.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        graphL1.getStyle().setAlignItems(Style.AlignItems.CENTER);

        H5 title = new H5("Financial Overview");
        title.getStyle().setMarginLeft("8px").setFontSize("18px");

        // Configure tabs
        Tabs tabs = new Tabs();
        tabs.getStyle().setMinHeight("0px");
        Tab monthly = new Tab("Month");
        tabs.add(monthly);

        // Add chart container
        Div chartContainer = new Div();
        chartContainer.setWidthFull();
        chartContainer.setHeight("80%");

        // Create and initialize the chart
        MonthlyDashBoardChart monthlyDashBoardChart = new MonthlyDashBoardChart(financeRepository);
        monthlyDashBoardChart.setWidthFull();
        monthlyDashBoardChart.setHeightFull();

        chartContainer.add(monthlyDashBoardChart);

        tabs.setSelectedTab(monthly);
        // Assemble layout
        graphL1.add(title, tabs);
        parent.add(graphL1, chartContainer);
        return parent;
    }


    Div customerCardEntryView(User user){
        Div horizontal = new Div();
        horizontal.getStyle().setDisplay(Style.Display.FLEX);
        horizontal.getStyle().setAlignItems(Style.AlignItems.START);
        horizontal.getStyle().setMarginBottom("4px");

        UserImage userImages = userImageService.getUserImageByNameAndUser("ProfileImage",user);
        byte [] userImageByte = null;
        Image image;
        Div imageContainer = new Div();
        imageContainer.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setWidth("45px").setHeight("45px")
                .setAlignItems(Style.AlignItems.CENTER)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setMarginRight("5px").setMarginBottom("10px")
                .setBorderRadius("15%")
                .setBackgroundColor("#162868");



        userImageByte = userImages.getUserImage();

        if(userImageByte != null){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userImageByte);
            StreamResource resource = new StreamResource("",()->{return byteArrayInputStream;});

            image = new Image(resource,"");
            image.setWidth("45px");
            image.setHeight("45px");
            image.getStyle().setBorderRadius("15%");

            imageContainer.getStyle().setBackgroundColor("white");
            imageContainer.add(image);
        }else{
            Span s = new Span(user.getUsername().substring(0,1));
            s.getStyle().setColor("white");
            imageContainer.add(s);
        }

        Div vertical = new Div();
        vertical.getStyle().setDisplay(Style.Display.FLEX);
        vertical.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);

        Span name = new Span(user.getFirstName());
        name.getStyle().setFontSize("18px");

        Span role = new Span("Owner");
        role.getStyle().setFontSize("10px");

        vertical.add(name,role);

        horizontal.add(imageContainer, vertical);
        return horizontal;
    }

    void configureLayer5(){
        layer5 = new Div();
        layer5.getStyle().setDisplay(Style.Display.FLEX);

        String totalPropertiesBought ="₦"+ new DecimalFormat("#, ###").format(dashboardService.totalPropertiesBought());
        String totalRevenue = "₦" + new DecimalFormat("#, ###").format(dashboardService.totalRevenue());
        String totalPaymentCompeted = "₦"+ new DecimalFormat("#, ###").format(dashboardService.totalPaymentCompleted());
        String totalOutStanding = "₦" + new DecimalFormat("#, ###").format(dashboardService.totalPaymentOutstanding());

        Div boughtCard = metricsCard("Total Properties Bought","#2F2F2F",totalPropertiesBought,"#6C5DD3");
        boughtCard.getStyle().setMarginRight("8px");
        Div revenueCard = metricsCard("Total Revenue","#2F2F2F",totalRevenue,"#F4A74B");
        revenueCard.getStyle().setMarginRight("8px");
        Div paymentCard = metricsCard("Total Payments Completed","#2F2F2F",totalPaymentCompeted,"#2ED480");
        paymentCard.getStyle().setMarginRight("8px");
        Div outstandingCard = metricsCard("Total Outstanding Payments","#2F2F2F",totalOutStanding,"#EB4444");

        layer5.add(boughtCard,revenueCard,paymentCard,outstandingCard);

    }

    void configureLayer6(){
        layer6 = new Div();
        layer6.getStyle().setMarginBottom("16px");
        layer6.getStyle().setDisplay(Style.Display.FLEX);

        Div left = new Div();
        left.setWidth("50%");
        left.setHeight("300px");
        left.getStyle().setBackgroundColor("white");
        left.getStyle().setBorderRadius("8px");
        left.add(layer6Graph());

        Div spacer = new Div();
        spacer.setWidth("8px");

        Div right = new Div();
        right.setWidth("50%");
        right.setHeight("300px");
        right.getStyle().setBackgroundColor("white");
        right.getStyle().setBorderRadius("8px");

        Div gridCard = constructGridCard();
        right.add(gridCard);

        layer6.add(left,spacer, right);
    }
    Div layer6Graph(){
        Div parent = new Div();
        parent.setWidthFull();
        parent.setHeightFull();

        Div layer1 = new Div();
        layer1.getStyle().setDisplay(Style.Display.FLEX);
        layer1.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        layer1.getStyle().setAlignItems(Style.AlignItems.CENTER);
        layer1.setWidthFull();
        layer1.setHeight("20%");

        H5 title = new H5("Property Overview");
        title.getStyle().setMarginLeft("8px").setMarginTop("10px").setFontSize("18px");
        layer1.add(title);


        // Add chart container
        Div chartContainer = new Div();
        chartContainer.setWidthFull();
        chartContainer.setHeight("100%");

        // Create and initialize the chart
        PropertyChart propertyChart = new PropertyChart(propertyService);
        propertyChart.setWidthFull();
        propertyChart.setHeightFull();

        chartContainer.add(propertyChart);

        Div graphL2 = new Div();
        graphL2.setHeight("85%");
        graphL2.getStyle().setMarginLeft("8px");

        //Configure chart canvas
        graphL2.add(title, chartContainer);
        parent.add(graphL2);
        return parent;
    }

    Div constructGridCard(){
        Div card = new Div();
        card.setHeight("300px");
        card.getStyle().setDisplay(Style.Display.FLEX);
        card.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);

        Div header = new Div();
        header.getStyle().setPaddingLeft("8px");
        header.getStyle().setPaddingRight("8px");
        header.getStyle().setDisplay(Style.Display.FLEX);
        header.getStyle().setFlexDirection(Style.FlexDirection.ROW);
        header.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        header.getStyle().setHeight("15%");
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);

        H5 propertyList = new H5("Property List");
        propertyList.getStyle().setFontSize("18px");

        Button seeAll = new Button("See all");
        seeAll.getStyle().setBackgroundColor("white");
        seeAll.addClickListener((e)->{
           UI.getCurrent().navigate("/properties");
        });
        seeAll.getStyle().setFontSize("10px");

        header.add(propertyList, seeAll);

        Grid<Property> propertyGrid = new Grid<>(Property.class, false);
        propertyGrid.setClassName("dashboard-grid");
        propertyGrid.getStyle().setTextAlign(Style.TextAlign.CENTER);
        propertyGrid.setHeight("85%");
        propertyGrid.getHeaderRows().forEach((headerRow)->{
            headerRow.getCells().forEach((x)->{
                x.getComponent().getStyle().setBackgroundColor("red");
            });
        });

        Grid.Column<Property> imageColumn = propertyGrid.addComponentColumn((property -> {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(property.getPropertyImages().get(0).getPropertyImages());
            StreamResource resource = new StreamResource("",()->{return byteArrayInputStream;});
            Image image = new Image(resource,"");
            image.setHeight("35px");
            image.setWidth("35px");
            image.getStyle().setBorderRadius("50%").setMarginRight("8px");

            Span name = new Span(property.getTitle());

            Div nameImg = new Div();
            nameImg.getStyle().setDisplay(Style.Display.FLEX);
            nameImg.getStyle().setAlignItems(Style.AlignItems.CENTER);
            nameImg.add(image,name);
            return nameImg;
        })).setHeader("Name");

        Grid.Column<Property> typeColumn = propertyGrid.addColumn((property)->property.getPropertyType()
                        .name().replace("_", " ").toLowerCase())
                .setHeader("Type");
        Grid.Column<Property> locationColumn = propertyGrid.addColumn(Property::getStreet)
                .setHeader("Location");
        Grid.Column<Property> statusColumn = propertyGrid.addColumn(property -> property.getPropertyStatus().name()
                .replace("_", " ").toLowerCase())
                .setHeader("Status");
        Grid.Column<Property> priceColumn = propertyGrid.addColumn(property -> "₦" + new DecimalFormat("#,###").format(property.getPrice()))
                .setHeader("Price");
        propertyGrid.setColumnOrder(imageColumn, locationColumn,statusColumn,typeColumn, priceColumn);
        propertyGrid.setItems(propertyService.getFourMostRecentProperties());


        card.add(header, propertyGrid);
        return card;
    }
    private Div createMetricBox(String title, String value, String color) {
        Div box = new Div();
        box.addClassName("metric-box");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "2rem").set("color", color).set("font-weight", "bold");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "18px").set("color", "#5a5a5a");

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
        titleSpan.getStyle().set("font-size", "18px").set("color", "#5a5a5a");

        box.add(valueSpan, titleSpan);
        return box;
    }
}

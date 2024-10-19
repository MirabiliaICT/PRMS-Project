package ng.org.mirabilia.pms.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.views.components.NavItem;
import ng.org.mirabilia.pms.views.modules.dashboard.DashboardView;
import ng.org.mirabilia.pms.views.modules.finances.FinancesView;
import ng.org.mirabilia.pms.views.modules.location.LocationView;
import ng.org.mirabilia.pms.views.modules.logs.LogsView;
import ng.org.mirabilia.pms.views.modules.maintenance.MaintenanceView;
import ng.org.mirabilia.pms.views.modules.properties.PropertiesView;
import ng.org.mirabilia.pms.views.modules.support.SupportView;
import ng.org.mirabilia.pms.views.modules.users.UsersView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class MainView extends AppLayout implements AfterNavigationObserver {

    private final List<RouterLink> routerLinks = new ArrayList<>();

    @Autowired
    private AuthenticationContext authContext;

    public MainView(AuthenticationContext authContext) {
        this.authContext = authContext;
        configureHeader();
        configureDrawer();
        configureMainContent();
    }

    private void configureHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("custom-toggle-button");

        HorizontalLayout header = new HorizontalLayout(toggle);
        header.addClassName("custom-header");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();

        addToNavbar(header);
        setPrimarySection(Section.DRAWER);
    }

    private void configureDrawer() {
        Image logo = new Image("images/logo.png", "Logo");
        logo.addClassName("drawer-logo");

        RouterLink dashboardLink = createNavItem("Dashboard", VaadinIcon.DASHBOARD, DashboardView.class);
        RouterLink locationLink = createNavItem("Location", VaadinIcon.LOCATION_ARROW, LocationView.class);
        RouterLink propertiesLink = createNavItem("Properties", VaadinIcon.WORKPLACE, PropertiesView.class);
        RouterLink usersLink = createNavItem("Users", VaadinIcon.USERS, UsersView.class);
        RouterLink financesLink = createNavItem("Finances", VaadinIcon.BAR_CHART, FinancesView.class);
        RouterLink maintenanceLink = createNavItem("Maintenance", VaadinIcon.BAR_CHART, MaintenanceView.class);
        RouterLink supportLink = createNavItem("Support", VaadinIcon.BAR_CHART, SupportView.class);
        RouterLink logsLink = createNavItem("Logs", VaadinIcon.BAR_CHART, LogsView.class);

        VerticalLayout drawerContent = new VerticalLayout(logo, dashboardLink, locationLink, propertiesLink,
                usersLink, financesLink, maintenanceLink, supportLink, logsLink);

        drawerContent.addClassName("drawer-content");

        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create(), event -> authContext.logout());
        logoutButton.addClassName("logout-button");
        logoutButton.addClassName("drawer-link");

        // Add the logout button at the bottom of the drawer
        drawerContent.add(logoutButton);

        addToDrawer(drawerContent);
    }

    private void configureMainContent() {
        VerticalLayout content = new VerticalLayout();
        content.addClassName("main-content");
        setContent(content);
    }

    private RouterLink createNavItem(String label, VaadinIcon icon, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.addClassName("drawer-link");

        link.add(new NavItem(icon.create(), label));
        link.setRoute(navigationTarget);

        routerLinks.add(link);

        return link;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String activeUrl = event.getLocation().getPath();

        routerLinks.forEach(link -> {
            if (link.getHref().equals(activeUrl)) {
                link.addClassName("active-link");
            } else {
                link.removeClassName("active-link");
            }
        });
    }
}
package ng.org.mirabilia.pms.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import ng.org.mirabilia.pms.views.components.NavItem;
import ng.org.mirabilia.pms.views.dashboard.DashboardView;
import ng.org.mirabilia.pms.views.finances.FinancesView;
import ng.org.mirabilia.pms.views.location.LocationView;
import ng.org.mirabilia.pms.views.properties.PropertiesView;
import ng.org.mirabilia.pms.views.users.UsersView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends AppLayout implements AfterNavigationObserver {

    private final List<RouterLink> routerLinks = new ArrayList<>(); // Store all links for navigation

    public MainView() {
        configureHeader();
        configureDrawer();
        configureMainContent();
    }

    // Header setup method
    private void configureHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("custom-toggle-button");

        HorizontalLayout header = new HorizontalLayout(toggle);
        header.addClassName("custom-header");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();

        addToNavbar(header);
        setPrimarySection(Section.DRAWER);  // Ensure the drawer is at the same level as the toggle
    }

    // Drawer setup method
    private void configureDrawer() {
        Image logo = new Image("images/logo.png", "Logo");
        logo.addClassName("drawer-logo");

        RouterLink dashboardLink = createNavItem("Dashboard", VaadinIcon.DASHBOARD, DashboardView.class);
        RouterLink locationLink = createNavItem("Location", VaadinIcon.LOCATION_ARROW, LocationView.class);
        RouterLink propertiesLink = createNavItem("Properties", VaadinIcon.WORKPLACE, PropertiesView.class);
        RouterLink usersLink = createNavItem("Users", VaadinIcon.USERS, UsersView.class);
        RouterLink financesLink = createNavItem("Finances", VaadinIcon.BAR_CHART, FinancesView.class);

        VerticalLayout drawerContent = new VerticalLayout(logo, dashboardLink, locationLink, propertiesLink, usersLink, financesLink);
        drawerContent.addClassName("drawer-content");

        addToDrawer(drawerContent);
    }

    // Content area setup method
    private void configureMainContent() {
        VerticalLayout content = new VerticalLayout();
        content.addClassName("main-content");
        setContent(content);
    }

    // Helper method to create navigation items
    private RouterLink createNavItem(String label, VaadinIcon icon, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.addClassName("drawer-link");

        // Use NavItem component for better encapsulation of icon and label
        link.add(new NavItem(icon.create(), label));
        link.setRoute(navigationTarget);

        // Track the links for dynamic active state management
        routerLinks.add(link);

        return link;
    }

    // Handle navigation events to highlight the active link
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String activeUrl = event.getLocation().getPath();  // Get the current active URL

        // Loop through all links and set/remove the active class dynamically
        routerLinks.forEach(link -> {
            if (link.getHref().equals(activeUrl)) {
                link.addClassName("active-link");
            } else {
                link.removeClassName("active-link");
            }
        });
    }
}
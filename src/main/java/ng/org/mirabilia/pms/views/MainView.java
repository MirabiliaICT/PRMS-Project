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
import ng.org.mirabilia.pms.views.modules.dashboard.DashboardView;
import ng.org.mirabilia.pms.views.modules.finances.FinancesView;
import ng.org.mirabilia.pms.views.modules.location.LocationView;
import ng.org.mirabilia.pms.views.modules.properties.PropertiesView;
import ng.org.mirabilia.pms.views.modules.users.UsersView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends AppLayout implements AfterNavigationObserver {

    private final List<RouterLink> routerLinks = new ArrayList<>();

    public MainView() {
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

        VerticalLayout drawerContent = new VerticalLayout(logo, dashboardLink, locationLink, propertiesLink, usersLink, financesLink);
        drawerContent.addClassName("drawer-content");

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
package ng.org.mirabilia.pms.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.components.NavItem;
import ng.org.mirabilia.pms.views.modules.dashboard.DashboardView;
import ng.org.mirabilia.pms.views.modules.finances.admin.FinancesView;
import ng.org.mirabilia.pms.views.modules.finances.client.ClientFinanceView;
import ng.org.mirabilia.pms.views.modules.location.LocationView;
import ng.org.mirabilia.pms.views.modules.logs.LogsView;
import ng.org.mirabilia.pms.views.modules.profile.ProfileView;
import ng.org.mirabilia.pms.views.modules.properties.PropertiesView;
import ng.org.mirabilia.pms.views.modules.support.SupportView;
import ng.org.mirabilia.pms.views.modules.users.UsersView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;



@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@JavaScript(value = "https://code.jquery.com/jquery-3.6.4.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/@vaadin/vaadin-lumo-styles@24.0.0/")

@JavaScript("https://code.jquery.com/jquery-3.6.3.min.js")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.2.1/css/fontawesome.min.css")
@StyleSheet("https://cdnjs.cloudflare.com/ajax/libs/lato-font/3.0.0/css/lato-font.min.css")



public class MainView extends AppLayout implements AfterNavigationObserver {

    private final List<RouterLink> routerLinks = new ArrayList<>();

    @Autowired
    private AuthenticationContext authContext;

    @Autowired
    private UserService userService;

    private User user;

    @Autowired
    private UserImageService userImageService;

    public MainView(AuthenticationContext authContext, UserService userService, UserImageService userImageService) {
        this.authContext = authContext;
        this.userService = userService;
        this.userImageService = userImageService;

        configureHeader();
        configureDrawer();
        configureMainContent();
    }

    private void configureHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("custom-toggle-button");

        Span span = new Span("User Information");

        Div d1 = new Div();
        d1.getStyle().setDisplay(Style.Display.FLEX);
        d1.getStyle().setAlignItems(Style.AlignItems.CENTER);
        d1.getStyle().setAlignItems(Style.AlignItems.CENTER);
        d1.getStyle().setMarginRight("10px");
        d1.getStyle().setJustifyContent(Style.JustifyContent.START);

        Image bell = new Image();
        bell.setWidth("15px");
        bell.setHeight("15px");
        bell.setSrc("/images/bell.png");
        bell.getStyle().setMarginRight("12px");


        Image profileImg = new Image();
        profileImg.setWidth("40px");
        profileImg.setHeight("40px");
        profileImg.getStyle().setBorderRadius("40px");
        profileImg.getStyle().setBackgroundColor("blue");
        profileImg.getStyle().setMarginRight("8px");
        //Set user image depending on authenticated user
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent((userDetails)->{

            user = userService.findByUsername(userDetails.getUsername());
            UserImage userImage = userImageService.getUserImageByNameAndUser("ProfileImage",user);
            if(userImage != null){
                byte[] userImageBytes = userImage.getUserImage();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userImageBytes);
                StreamResource resource = new StreamResource("",()-> byteArrayInputStream);
                profileImg.setSrc(resource);
            }else{
                profileImg.setSrc("/images/john.png");
            }
        });

        Span span1 = new Span(user.getUsername());
        d1.add(bell, profileImg, span1);

        HorizontalLayout header = new HorizontalLayout(toggle, span, d1);
        header.addClassName("custom-header");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(false);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);
        setPrimarySection(Section.DRAWER);
    }

    private void configureDrawer() {
        Image logo = new Image("images/logo.png", "Logo");
        logo.addClassName("drawer-logo");

        VerticalLayout drawerContent = new VerticalLayout(logo);

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_AGENT") || hasRole("ROLE_ACCOUNTANT") || hasRole("ROLE_CLIENT") || hasRole("ROLE_IT_SUPPORT")) {
            RouterLink dashboardLink = createNavItem("Dashboard", VaadinIcon.DASHBOARD, DashboardView.class);
            drawerContent.add(dashboardLink);
        }

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_AGENT") || hasRole("ROLE_ACCOUNTANT") || hasRole("ROLE_CLIENT") || hasRole("ROLE_IT_SUPPORT")) {
            RouterLink profileLink = createNavItem("Profile", VaadinIcon.USER, ProfileView.class);
            drawerContent.add(profileLink);
        }

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER")) {
            RouterLink locationLink = createNavItem("Location", VaadinIcon.LOCATION_ARROW, LocationView.class);
            drawerContent.add(locationLink);
        }

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_IT_SUPPORT")) {
            RouterLink usersLink = createNavItem("Users", VaadinIcon.USERS, UsersView.class);
            drawerContent.add(usersLink);
        }

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_AGENT") || hasRole("ROLE_CLIENT")) {
            RouterLink propertiesLink = createNavItem("Properties", VaadinIcon.WORKPLACE, PropertiesView.class);
            drawerContent.add(propertiesLink);
        }

        if (hasRole("ROLE_CLIENT")) {
            RouterLink financesLink = createNavItem("Finances", VaadinIcon.BAR_CHART, determineFinanceView());
            drawerContent.add(financesLink);
        } else if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_ACCOUNTANT")) {
            RouterLink financesLink = createNavItem("Finances", VaadinIcon.BAR_CHART, determineFinanceView());
            drawerContent.add(financesLink);
        }


        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_AGENT") || hasRole("ROLE_ACCOUNTANT") || hasRole("ROLE_CLIENT") || hasRole("ROLE_IT_SUPPORT")) {
            RouterLink supportLink = createNavItem("Support", VaadinIcon.HEADSET, SupportView.class);
            drawerContent.add(supportLink);
        }

        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_IT_SUPPORT")) {
            RouterLink logsLink = createNavItem("Logs", VaadinIcon.CLIPBOARD_TEXT, LogsView.class);
            drawerContent.add(logsLink);
        }

        drawerContent.addClassName("drawer-content");

        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create(), event -> authContext.logout());
        logoutButton.addClassName("custom-logout-button");
        logoutButton.addClassName("drawer-link");

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

    private boolean hasRole(String role) {
        return authContext.getAuthenticatedUser(UserDetails.class)
                .map(authUser -> authUser.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals(role)))
                .orElse(false);
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


    private Class<? extends com.vaadin.flow.component.Component> determineFinanceView() {
        if (hasRole("ROLE_CLIENT")) {
            return ClientFinanceView.class;
        } else if (hasRole("ROLE_ADMIN") || hasRole("ROLE_MANAGER") || hasRole("ROLE_ACCOUNTANT")) {
            return FinancesView.class;
        }
        return null;
    }




}
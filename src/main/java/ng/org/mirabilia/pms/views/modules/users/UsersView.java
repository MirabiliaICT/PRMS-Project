package ng.org.mirabilia.pms.views.modules.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.users.content.ClientContent;
import ng.org.mirabilia.pms.views.modules.users.content.StaffContent;

@Route(value = "users", layout = MainView.class)
@PageTitle("Users | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
public class UsersView extends VerticalLayout {

    private final VerticalLayout contentLayout;

    private final UserService userService;

    public UsersView(UserService userService) {
        this.userService = userService;

        setSpacing(true);
        setPadding(false);

        contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(false);



        Tab clientTab = new Tab("Client");
        Tab staffTab = new Tab("Staff");

        Tabs tabs = new Tabs(clientTab, staffTab);
        tabs.setWidthFull();
        tabs.addClassName("custom-tabs");
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            updateContent(selectedTab);
        });

        tabs.setSelectedTab(clientTab);
        updateContent(clientTab);

        add(tabs,contentLayout);
    }

    private void updateContent(Tab selectedTab) {
        contentLayout.removeAll();

        if (selectedTab.getLabel().equals("Client")) {
            contentLayout.add(new ClientContent(userService));
        } else if (selectedTab.getLabel().equals("Staff")) {
            contentLayout.add(new StaffContent(userService));

    }
}
}

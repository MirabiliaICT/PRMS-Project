package ng.org.mirabilia.pms.views.modules.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.users.content.UsersContent;

@Route(value = "users", layout = MainView.class)
@PageTitle("Users | Property Management System")
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    public UsersView(UserService userService) {
        setSpacing(true);
        setPadding(false);

        UsersContent userContent = new UsersContent(userService);
        add(userContent);
    }
}


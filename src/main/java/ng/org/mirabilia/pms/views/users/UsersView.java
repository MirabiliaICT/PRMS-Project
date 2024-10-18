package ng.org.mirabilia.pms.views.users;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "users", layout = MainView.class)
@PageTitle("Users | Property Management System")
public class UsersView extends VerticalLayout {
    public UsersView() {
        add("Welcome to the Users View!");
    }
}

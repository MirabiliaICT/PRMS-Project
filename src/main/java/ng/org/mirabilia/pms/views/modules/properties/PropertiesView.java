package ng.org.mirabilia.pms.views.modules.properties;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "properties", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "AGENT", "CLIENT"})
@PageTitle("Properties | Property Management System")
public class PropertiesView extends VerticalLayout {
    public PropertiesView() {
        add("Welcome to the Properties View!");
    }
}

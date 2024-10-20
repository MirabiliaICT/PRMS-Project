package ng.org.mirabilia.pms.views.modules.maintenance;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "maintenance", layout = MainView.class)
@RolesAllowed({"ADMIN", "CUSTOMER_RELATIONS", "CLIENT"})
@PageTitle("Maintenance | Property Management System")
public class MaintenanceView extends VerticalLayout {
    public MaintenanceView() {
        add("Welcome to the Maintenance View!");
    }
}
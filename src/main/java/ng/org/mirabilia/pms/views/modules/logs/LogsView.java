package ng.org.mirabilia.pms.views.modules.logs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "logs", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
@PageTitle("Audit Logs | Property Management System")
public class LogsView extends VerticalLayout {
    public LogsView() {
        add("Welcome to the Logs View!");
    }
}


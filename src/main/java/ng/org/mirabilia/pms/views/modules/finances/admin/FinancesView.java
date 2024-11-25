package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "admin/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class FinancesView extends VerticalLayout {
    public FinancesView() {
        add("Welcome to the Finances!");
    }
}

package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "finance", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminFinancesView extends VerticalLayout {
    public AdminFinancesView() {
        add("Welcome to the Finances!");
    }
}

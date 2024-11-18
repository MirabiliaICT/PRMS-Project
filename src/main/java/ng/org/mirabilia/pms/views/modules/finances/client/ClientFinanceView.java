package ng.org.mirabilia.pms.views.modules.finances.client;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "client/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed("CLIENT")
public class ClientFinanceView extends VerticalLayout {
    public ClientFinanceView() {
        add("Welcome to the Finances for Client!");
    }
}

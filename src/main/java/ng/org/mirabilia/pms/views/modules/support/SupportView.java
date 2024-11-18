package ng.org.mirabilia.pms.views.modules.support;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "support", layout = MainView.class)
@PageTitle("Support | Property Management System")
@AnonymousAllowed
public class SupportView extends VerticalLayout {
    public SupportView() {
        add("Welcome to the Support View!");
    }
}

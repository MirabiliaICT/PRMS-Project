package ng.org.mirabilia.pms.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("unauthorized")
@AnonymousAllowed
public class UnauthorizedView extends VerticalLayout {

    public UnauthorizedView() {
        setClassName("error-view");
        Notification.show("Sorry! You are not AUTHORISED to view this page.", 9000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

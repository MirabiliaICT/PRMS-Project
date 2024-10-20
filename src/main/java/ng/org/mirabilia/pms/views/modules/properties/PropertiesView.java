package ng.org.mirabilia.pms.views.modules.properties;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.properties.content.PropertiesContent;

@Route(value = "properties", layout = MainView.class)
@PageTitle("Properties | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "AGENT", "CLIENT"})
public class PropertiesView extends VerticalLayout {

    public PropertiesView(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService) {
        setSpacing(true);
        setPadding(false);

        PropertiesContent propertiesContent = new PropertiesContent(propertyService, phaseService, cityService, stateService, userService);
        add(propertiesContent);
    }
}

package ng.org.mirabilia.pms.views.modules.properties;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.properties.content.tabs.CardTab;
import ng.org.mirabilia.pms.views.modules.properties.content.tabs.GridTab;

@Route(value = "properties", layout = MainView.class)
@PageTitle("Properties | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "AGENT", "CLIENT"})
public class PropertiesView extends VerticalLayout {

    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;
    private final Tabs tabs;
    private final GridTab gridTab;
    private final CardTab cardTab;

    public PropertiesView(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.userService = userService;

        setSpacing(true);
        setPadding(false);

        tabs = new Tabs();
//        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.setWidthFull();

        gridTab = new GridTab(propertyService, phaseService, cityService, stateService, userService);
        cardTab = new CardTab(propertyService, phaseService, cityService, stateService, userService);

        Tab gridTabItem = new Tab("Grid View");
        Tab cardTabItem = new Tab("Card View");

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == gridTabItem) {
                remove(cardTab);
                gridTab.updateGrid();
                add(gridTab);
            } else {
                remove(gridTab);
                cardTab.updatePropertyLayout();
                add(cardTab);
            }
        });

        tabs.add(gridTabItem, cardTabItem);
        add(tabs, gridTab);
    }

}

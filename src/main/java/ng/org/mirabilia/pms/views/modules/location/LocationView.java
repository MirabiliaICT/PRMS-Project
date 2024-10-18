package ng.org.mirabilia.pms.views.modules.location;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.location.content.CityContent;
import ng.org.mirabilia.pms.views.modules.location.content.PhaseContent;
import ng.org.mirabilia.pms.views.modules.location.content.StateContent;

@Route(value = "location", layout = MainView.class)
@PageTitle("Location | Property Management System")
public class LocationView extends VerticalLayout {

    private final VerticalLayout contentLayout;
    private final StateService stateService;
    private final CityService cityService;
    private final PhaseService phaseService;

    public LocationView(StateService stateService, CityService cityService, PhaseService phaseService) {
        this.stateService = stateService;
        this.cityService = cityService;
        this.phaseService = phaseService;
        setSpacing(true);
        setPadding(false);

        Tab stateTab = new Tab("State");
        Tab cityTab = new Tab("City");
        Tab phaseTab = new Tab("Phase");

        Tabs tabs = new Tabs(stateTab, cityTab, phaseTab);
        tabs.setWidthFull();
        tabs.addClassName("custom-tabs");

        contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(false);
        tabs.setSelectedTab(stateTab);

        updateContent(stateTab);

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            updateContent(selectedTab);
        });

        add(tabs, contentLayout);
    }

    private void updateContent(Tab selectedTab) {
        contentLayout.removeAll();

        if (selectedTab.getLabel().equals("State")) {
            contentLayout.add(new StateContent(stateService));
        } else if (selectedTab.getLabel().equals("City")) {
            contentLayout.add(new CityContent(cityService, stateService));
        } else if (selectedTab.getLabel().equals("Phase")) {
            contentLayout.add(new PhaseContent(phaseService, cityService, stateService));
        }
    }
}

package ng.org.mirabilia.pms.views.location;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.location.content.CityContent;
import ng.org.mirabilia.pms.views.location.content.PhaseContent;
import ng.org.mirabilia.pms.views.location.content.StateContent;

@Route(value = "location", layout = MainView.class)
@PageTitle("Location | Property Management System")
public class LocationView extends VerticalLayout {

    private final VerticalLayout contentLayout; // To hold the content for each tab
    private final StateService stateService;    // Injected StateService
    private final CityService cityService;    // Injected StateService
    private final PhaseService phaseService;    // Injected StateService

    public LocationView(StateService stateService, CityService cityService, PhaseService phaseService) {
        this.stateService = stateService;
        this.cityService = cityService;
        this.phaseService = phaseService;
        setSpacing(true);
        setPadding(false);

        // Create tabs for State, City, and Phase
        Tab stateTab = new Tab("State");
        Tab cityTab = new Tab("City");
        Tab phaseTab = new Tab("Phase");

        Tabs tabs = new Tabs(stateTab, cityTab, phaseTab);
        tabs.setWidthFull();
        tabs.addClassName("custom-tabs");

        // Content layout to display below the tabs
        contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(false);  // Remove padding from the content layout

        // Set the "State" tab as the default selected tab
        tabs.setSelectedTab(stateTab);

        // Default content (State tab content)
        updateContent(stateTab);

        // Handle tab selection changes
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            updateContent(selectedTab);
        });

        // Add tabs and content layout to the view
        add(tabs, contentLayout);
    }

    // Method to update the content based on the selected tab
    private void updateContent(Tab selectedTab) {
        contentLayout.removeAll(); // Clear the existing content

        if (selectedTab.getLabel().equals("State")) {
            contentLayout.add(new StateContent(stateService)); // Use the decoupled StateContent class
        } else if (selectedTab.getLabel().equals("City")) {
            contentLayout.add(new CityContent(cityService, stateService)); // Use the decoupled CityContent class
        } else if (selectedTab.getLabel().equals("Phase")) {
            contentLayout.add(new PhaseContent(phaseService, cityService)); // Use the decoupled PhaseContent class
        }
    }
}

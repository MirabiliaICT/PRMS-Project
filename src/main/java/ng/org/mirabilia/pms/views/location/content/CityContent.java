package ng.org.mirabilia.pms.views.location.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.models.City;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.forms.location.city.AddCityForm;
import ng.org.mirabilia.pms.views.forms.location.city.EditCityForm;

import java.util.List;

public class CityContent extends VerticalLayout {

    private final CityService cityService;
    private final StateService stateService;
    private final Grid<City> cityGrid;      // Grid for displaying cities
    private final TextField searchField;    // Search field for filtering cities

    public CityContent(CityService cityService, StateService stateService) {
        this.cityService = cityService;
        this.stateService = stateService;

        // Disable spacing and padding for the layout
        setSpacing(true);
        setPadding(false);
        addClassName("city-content");

        // Initialize search field
        searchField = new TextField();
        searchField.setPlaceholder("Search City");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);  // Immediately update grid as user types
        searchField.addValueChangeListener(e -> updateGrid());

        // Add City button
        Button addCityButton = new Button("Add City");
        addCityButton.addClassName("custom-button");
        addCityButton.addClassName("custom-add-button");
        addCityButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        // Configure the grid for displaying cities
        cityGrid = new Grid<>(City.class);
        cityGrid.addClassName("custom-grid");


        cityGrid.setColumns("name", "cityCode"); // Assuming State class has fields 'name' and 'stateCode'


        // Populate the grid with all cities
        cityGrid.setItems(cityService.getAllCities());

        // Handle grid row click for editing
        cityGrid.asSingleSelect().addValueChangeListener(event -> {
            City selectedCity = event.getValue();
            if (selectedCity != null) {
                openEditCityDialog(selectedCity);
            }
        });

        // Create a layout to hold the button and search field
        HorizontalLayout toolbar = new HorizontalLayout(searchField, addCityButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        // Add components to the layout
        add(toolbar, cityGrid);

        // Add click listener for Add City button to open the form
        addCityButton.addClickListener(e -> openAddCityDialog());

        // Initially populate the grid
        updateGrid();
    }

    // Method to update the grid based on search input or load all cities
    private void updateGrid() {
        String keyword = searchField.getValue();

        List<City> cities;
        if (keyword == null || keyword.isEmpty()) {
            cities = cityService.getAllCities(); // If no keyword, fetch all cities
        } else {
            cities = cityService.searchCityByKeywords(keyword); // Search based on keywords
        }

        cityGrid.setItems(cities); // Update the grid with the filtered cities
    }

    // Method to open the "Add City" dialog (Assuming AddCityForm exists)
    private void openAddCityDialog() {
        AddCityForm cityForm = new AddCityForm(cityService,stateService, (v) -> updateGrid());
        cityForm.open();
    }

    // Method to open the "Edit City" dialog (Assuming EditCityForm exists)
    private void openEditCityDialog(City city) {
        EditCityForm editCityForm = new EditCityForm(cityService, stateService, city, (v) -> updateGrid());
        editCityForm.open();
    }
}

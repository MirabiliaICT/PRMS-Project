package ng.org.mirabilia.pms.views.modules.properties.content.tabs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.*;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.services.implementations.GltfStorageService;
import ng.org.mirabilia.pms.views.forms.properties.AddPropertyForm;
import ng.org.mirabilia.pms.views.forms.properties.EditPropertyForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GridTab extends VerticalLayout {
    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;

    private final Grid<Property> propertyGrid;
    private final TextField searchField;
    private final ComboBox<String> stateFilter;
    private final ComboBox<String> cityFilter;
    private final ComboBox<String> phaseFilter;
    private final ComboBox<PropertyType> propertyTypeFilter;
    private final ComboBox<PropertyStatus> propertyStatusFilter;
    private final ComboBox<String> agentFilter;
    private final ComboBox<String> clientFilter;

    public GridTab(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.userService = userService;

        setSpacing(true);
        setPadding(false);
        addClassName("properties-content");

        searchField = new TextField();
        searchField.setPlaceholder("Search Properties");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid());
        searchField.addClassNames("custom-search-field custom-toolbar-field col-sm-6 col-xs-6 bg-white");
        searchField.getElement().getStyle().set("background-color", "white");
        searchField.setHeight("3.3rem");
        

        stateFilter = new ComboBox<>("State");
        stateFilter.setItems(stateService.getAllStates().stream().map(State::getName).collect(Collectors.toList()));
        stateFilter.addValueChangeListener(e -> onStateSelected());
        stateFilter.addClassNames("custom-filter col-sm-6 col-xs-6");
        stateFilter.setHeight("3.3rem");

        cityFilter = new ComboBox<>("City");
        cityFilter.setEnabled(false);
        cityFilter.addValueChangeListener(e -> onCitySelected());
        cityFilter.addClassNames("custom-filter col-sm-6 col-xs-6");

        phaseFilter = new ComboBox<>("Phase");
        phaseFilter.setEnabled(false);
        phaseFilter.addValueChangeListener(e -> onPhaseSelected());
        phaseFilter.addClassNames("custom-filter col-sm-6 col-xs-6");

        propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
        propertyTypeFilter.addValueChangeListener(e -> updateGrid());
        propertyTypeFilter .addClassNames("custom-filter col-sm-6 col-xs-6");

        propertyStatusFilter = new ComboBox<>("Status", PropertyStatus.values());
        propertyStatusFilter.addValueChangeListener(e -> updateGrid());
        propertyStatusFilter.addClassNames("custom-filter col-sm-6 col-xs-6");

        agentFilter = new ComboBox<>("Agent");
        agentFilter.setItems(userService.getAgents().stream().map(agent -> agent.getFirstName() + " " + agent.getLastName()).collect(Collectors.toList()));
        agentFilter.addValueChangeListener(e -> updateGrid());
        agentFilter .addClassNames("custom-filter col-sm-6 col-xs-6");

        clientFilter = new ComboBox<>("Client");
        clientFilter.setItems(userService.getClients().stream().map(client -> client.getFirstName() + " " + client.getLastName()).collect(Collectors.toList()));
        clientFilter.addValueChangeListener(e -> updateGrid());
        clientFilter .addClassNames("custom-filter col-sm-6 col-xs-6");

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetFilters());
        resetButton.addClassNames("custom-button custom-reset-button custom-toolbar-button col-sm-6 col-xs-6");

        Button addPropertyButton = new Button("Add Property");
        addPropertyButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        addPropertyButton.addClickListener(e -> openAddPropertyDialog());
        addPropertyButton.addClassNames("custom-button custom-add-button custom-toolbar-button col-sm-6 col-xs-6");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        addPropertyButton.setVisible(authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));


        propertyGrid = new Grid<>(Property.class);
        propertyGrid.getStyle().setBorderTop("none");
        propertyGrid.setColumns();
        propertyGrid.addColumn(property -> property.getPhase().getCity().getState().getName())
                .setHeader("State")
                .setKey("state")
                .setAutoWidth(true)
                .setSortable(true);
        propertyGrid.addColumn(property -> property.getPhase().getCity().getName())
                .setHeader("City")
                .setKey("city")
                .setAutoWidth(true)
                .setSortable(true);
        propertyGrid.addColumn(property -> property.getPhase().getName())
                .setHeader("Phase")
                .setKey("phase")
                .setAutoWidth(true)
                .setSortable(true);

        propertyGrid.addColumn(Property::getPropertyCode)
                .setHeader("Property Code")
                .setKey("propertyCode")
                .setAutoWidth(true)
                .setSortable(true);

        propertyGrid.addColumn(property -> {
            Long agentId = property.getAgentId();
            return agentId!= null? userService.getUserById(agentId).get().getFirstName() +
                    " " + userService.getUserById(agentId).get().getLastName() : "N/A";
        })
                .setHeader("Agent")
                .setKey("agentId")
                .setAutoWidth(true)
                .setSortable(true);
        propertyGrid.addColumn(property -> "₦" + NumberFormat.getNumberInstance(Locale.US).format(property.getPrice()))
                .setHeader("Price")
                .setKey("price")
                .setAutoWidth(true)
                .setSortable(true);;
        propertyGrid.getStyle().setFontSize("14px");

        propertyGrid.addColumn(property -> property.getPropertyType().getDisplayName().replace("_", " "))
                .setHeader("Type")
                .setKey("propertyType")
                .setAutoWidth(true)
                .setSortable(true);

        propertyGrid.addColumn(property -> property.getPropertyStatus().name().replace("_", " "))
                .setHeader("Status")
                .setKey("propertyStatus")
                .setAutoWidth(true)
                .setSortable(true);

        propertyGrid.addColumn(Property::getSize)
                .setHeader("size")
                .setKey("size")
                .setAutoWidth(true)
                .setSortable(true);

        propertyGrid.setItems(propertyService.getAllProperties());

        propertyGrid.addClassName("custom-grid");

        propertyStatusFilter.addValueChangeListener(event -> {
            PropertyStatus selectedStatus = event.getValue();
            if (selectedStatus != null && selectedStatus.equals(PropertyStatus.AVAILABLE)) {
                clientFilter.setVisible(false);
                agentFilter.setVisible(false);
            } else {
                agentFilter.setVisible(true);
                clientFilter.setVisible(true);
            }
        });

        propertyGrid.asSingleSelect().addValueChangeListener(event -> {
            Property selectedProperty = event.getValue();
            if (selectedProperty != null) {
                getUI().ifPresent(ui -> ui.navigate("property-detail/" + selectedProperty.getId()));
            }
        });


        HorizontalLayout firstRowToolbar = new HorizontalLayout(stateFilter, cityFilter, phaseFilter, propertyTypeFilter, propertyStatusFilter, agentFilter, clientFilter, searchField, resetButton, addPropertyButton);
        firstRowToolbar.addClassNames("custom-toolbar row");
        firstRowToolbar.getStyle().setAlignItems(Style.AlignItems.BASELINE);


        add(firstRowToolbar, propertyGrid);

        updateGrid();
    }


    public void updateGrid() {
        String keyword = searchField.getValue();
        String selectedState = stateFilter.getValue();
        String selectedCity = cityFilter.getValue();
        String selectedPhase = phaseFilter.getValue();
        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
        PropertyStatus selectedPropertyStatus = propertyStatusFilter.getValue();
        String selectedAgent = agentFilter.getValue();
        String selectedClient = clientFilter.getValue();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Property> properties;
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            properties = propertyService.searchPropertiesByFilters(keyword, selectedState, selectedCity, selectedPhase, selectedPropertyType, selectedPropertyStatus, selectedAgent, selectedClient);
        } else {
            User user = userService.findByUsername(Application.globalLoggedInUsername);
            properties = propertyService.searchPropertiesByUserId(keyword, selectedState, selectedCity, selectedPhase, selectedPropertyType, selectedPropertyStatus, selectedAgent, selectedClient, user.getId());
        }

        propertyGrid.setItems(properties);
        System.out.println("Properties Length for Grid" + properties.size());
        properties.sort((p1, p2) ->
                p2.getUpdatedAt().compareTo(p1.getUpdatedAt())
        );

    }


    private void resetFilters() {
        searchField.clear();
        stateFilter.clear();
        cityFilter.clear();
        phaseFilter.clear();
        propertyTypeFilter.clear();
        propertyStatusFilter.clear();
        agentFilter.clear();
        clientFilter.clear();
        updateGrid();
    }

    private void onStateSelected() {
        String selectedState = stateFilter.getValue();
        if (selectedState != null) {
            cityFilter.setItems(cityService.getCitiesByState(selectedState).stream().map(City::getName).collect(Collectors.toList()));
            cityFilter.setEnabled(true);
        } else {
            cityFilter.clear();
            cityFilter.setEnabled(false);
        }
        updateGrid();
    }

    private void onCitySelected() {
        String selectedCity = cityFilter.getValue();
        if (selectedCity != null) {
            phaseFilter.setItems(phaseService.getPhasesByCity(selectedCity).stream().map(Phase::getName).collect(Collectors.toList()));
            phaseFilter.setEnabled(true);
        } else {
            phaseFilter.clear();
            phaseFilter.setEnabled(false);
        }
        updateGrid();
    }

    private void onPhaseSelected(){
        String selectedPhase = phaseFilter.getValue();
        if (selectedPhase!= null) {
            phaseFilter.setValue(selectedPhase);
            phaseFilter.setEnabled(true);
        } else {
            phaseFilter.clear();
            phaseFilter.setEnabled(false);
        }
        updateGrid();
    }

    private void openAddPropertyDialog() {
        AddPropertyForm addPropertyForm = new AddPropertyForm(propertyService, phaseService, cityService, stateService, userService, (v) -> updateGrid());
        addPropertyForm.open();
    }
}

package ng.org.mirabilia.pms.views.modules.properties.content.tabs;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.IFrame;
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
import ng.org.mirabilia.pms.config.GoogleMapsConfig;
import ng.org.mirabilia.pms.domain.entities.*;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.services.implementations.GltfStorageService;
import ng.org.mirabilia.pms.views.forms.properties.AddPropertyForm;
import ng.org.mirabilia.pms.views.forms.properties.EditPropertyForm;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CardTab extends  VerticalLayout{
    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;
    private final HorizontalLayout propertyLayout;
    private final TextField searchField;
    private final ComboBox<String> stateFilter;
    private final ComboBox<String> cityFilter;
    private final ComboBox<String> phaseFilter;
    private final ComboBox<PropertyType> propertyTypeFilter;
    private final ComboBox<PropertyStatus> propertyStatusFilter;
    private final ComboBox<String> agentFilter;
    private final ComboBox<String> clientFilter;

//    @Value("${google.maps-api_key}")
//    private String GOOGLE_MAPS_API_KEY;

    private static final String GOOGLE_MAPS_API_KEY = GoogleMapsConfig.getGoogleMapsApiKey();

    public CardTab(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService) {
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
        searchField.addValueChangeListener(e -> updatePropertyLayout());
        searchField.addClassName("custom-search-field");
        searchField.addClassName("custom-toolbar-field");

        stateFilter = new ComboBox<>("State");
        stateFilter.setItems(stateService.getAllStates().stream().map(State::getName).collect(Collectors.toList()));
        stateFilter.addValueChangeListener(e -> onStateSelected());
        stateFilter.addClassName("custom-filter");

        cityFilter = new ComboBox<>("City");
        cityFilter.setEnabled(false);
        cityFilter.addValueChangeListener(e -> onCitySelected());
        cityFilter.addClassName("custom-filter");

        phaseFilter = new ComboBox<>("Phase");
        phaseFilter.setEnabled(false);
        phaseFilter.addValueChangeListener(e -> onPhaseSelected());
        phaseFilter.addClassName("custom-filter");

        propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
        propertyTypeFilter.addValueChangeListener(e -> updatePropertyLayout());
        propertyTypeFilter.addClassName("custom-filter");

        propertyStatusFilter = new ComboBox<>("Status", PropertyStatus.values());
        propertyStatusFilter.addValueChangeListener(e -> updatePropertyLayout());
        propertyStatusFilter.addClassName("custom-filter");

        agentFilter = new ComboBox<>("Agent");
        agentFilter.setItems(userService.getAgents().stream().map(agent -> agent.getFirstName() + " " + agent.getLastName()).collect(Collectors.toList()));
        agentFilter.addValueChangeListener(e -> updatePropertyLayout());
        agentFilter.addClassName("custom-filter");

        clientFilter = new ComboBox<>("Client");
        clientFilter.setItems(userService.getClients().stream().map(client -> client.getFirstName() + " " + client.getLastName()).collect(Collectors.toList()));
        clientFilter.addValueChangeListener(e -> updatePropertyLayout());
        clientFilter.addClassName("custom-filter");

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetFilters());
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");

        Button addPropertyButton = new Button("Add Property");
        addPropertyButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        addPropertyButton.addClickListener(e -> openAddPropertyDialog());
        addPropertyButton.addClassNames("custom-button custom-add-button custom-toolbar-button");
//        addPropertyButton.setWidth("100px");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        addPropertyButton.setVisible(authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));

        propertyLayout = new HorizontalLayout();
        propertyLayout.addClassName("property-layout");

        HorizontalLayout firstRowToolbar = new HorizontalLayout(stateFilter, cityFilter, phaseFilter, propertyTypeFilter, propertyStatusFilter, agentFilter, clientFilter, searchField, resetButton, addPropertyButton);
        firstRowToolbar.addClassName("custom-toolbar");
        firstRowToolbar.getStyle().setDisplay(Style.Display.FLEX).setFlexWrap(Style.FlexWrap.WRAP);
        firstRowToolbar.getStyle().setAlignItems(Style.AlignItems.FLEX_END);

        H4 title = new H4("Property List");
        title.addClassName("property-list-title");


        GoogleMap googleMap = new GoogleMap(GOOGLE_MAPS_API_KEY, null, "english");
        googleMap.setSizeFull();
        googleMap.setHeight("50vh");
        googleMap.setCenter(new LatLon(9.0820, 8.6753));
        googleMap.setZoom(6);
        googleMap.setMapType(GoogleMap.MapType.SATELLITE);

        addPropertyMarkers(googleMap);

        Div mapContainer = new Div(googleMap);
        mapContainer.setClassName("map-container");
        mapContainer.setWidthFull();

        add(firstRowToolbar, mapContainer, title, propertyLayout);

        updatePropertyLayout();
    }

    private void addPropertyMarkers(GoogleMap googleMap) {
        String keyword = searchField.getValue();
        String selectedState = stateFilter.getValue();
        String selectedCity = cityFilter.getValue();
        String selectedPhase = phaseFilter.getValue();
        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
        PropertyStatus selectedPropertyStatus = propertyStatusFilter.getValue();
        String selectedAgent = agentFilter.getValue();
        String selectedClient = clientFilter.getValue();

        List<Property> properties;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            properties = propertyService.searchPropertiesByFilters(keyword, selectedState, selectedCity, selectedPhase, selectedPropertyType, selectedPropertyStatus, selectedAgent, selectedClient);
        } else {
            User user = userService.findByUsername(Application.globalLoggedInUsername);
            properties = propertyService.searchPropertiesByUserId(keyword, selectedState, selectedCity, selectedPhase, selectedPropertyType, selectedPropertyStatus, selectedAgent, selectedClient, user.getId());
        }


        for (Property property : properties) {
            GoogleMapMarker marker = new GoogleMapMarker(
                    property.getTitle(),
                    new LatLon(property.getLatitude(), property.getLongitude()),
                    false,
                    getStatusIcon(property.getPropertyStatus())
            );
            marker.addClickListener( e ->{
                getUI().ifPresent(ui -> ui.navigate("property-detail/" + property.getId()));
            });
            googleMap.addMarker(marker);
        }
    }

    private String getStatusIcon(PropertyStatus status) {
        switch (status) {
            case AVAILABLE:
                return "https://maps.google.com/mapfiles/ms/icons/green-dot.png";
            case UNDER_OFFER:
                return "https://maps.google.com/mapfiles/ms/icons/orange-dot.png";
            case SOLD:
                return "https://maps.google.com/mapfiles/ms/icons/red-dot.png";
            default:
                return "";
        }
    }


    public void updatePropertyLayout() {
        propertyLayout.removeAll();


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

        System.out.println("Properties Length for Card " + properties.size());
        properties.sort((p1, p2) ->
             p2.getUpdatedAt().compareTo(p1.getUpdatedAt())
        );


        for (Property property : properties) {
            propertyLayout.add(createPropertyCard(property));
        }
    }

    private Div createPropertyCard(Property property) {
        Div propertyCard = new Div();
        propertyCard.addClassName("property-card");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassName("property-vertical-layout");
        Image image = createImage(property);
        image.addClassName("property-card-img");
        verticalLayout.setWidth("90%");


        String propertyStatusFormat = property.getPropertyStatus().name().replace("_", " ");
        String propertyTypeFormat = property.getPropertyType().name().replace("_", " ");
        String formattedPrice = NumberFormat.getInstance().format(property.getPrice());
        String location = property.getStreet() + ", " + property.getPhase().getName();
        Div locationText = new Div(location);
        Div propertyType = new Div(propertyTypeFormat);
        Div price = new Div("₦" + formattedPrice);
        Div status = new Div(propertyStatusFormat);
        status.addClassName("property-card-status");
        propertyType.addClassName("property-card-type");
        price.addClassName("property-card-price");


        HorizontalLayout horizontalLayoutTop = new HorizontalLayout(propertyType, price);
        horizontalLayoutTop.addClassName("horizontal-type-price");

        Icon mapPoint = new Icon(VaadinIcon.MAP_MARKER);
        mapPoint.addClassName("property-card-marker");
//        mapPoint.getStyle().setColor("red");
        HorizontalLayout horizontalLayoutNext = new HorizontalLayout(mapPoint, locationText);
        horizontalLayoutNext.getStyle().set("gap", "0");

        verticalLayout.add(status, image, horizontalLayoutTop, horizontalLayoutNext);
        propertyCard.add(verticalLayout);

        if(PropertyStatus.AVAILABLE == property.getPropertyStatus()){
            status.getStyle().setBackground("#34A853");
            status.getStyle().setColor("#FFFFFF");
            status.getStyle().setPadding("5px");
            status.getStyle().setBorderRadius("5px");
            status.getStyle().setFontSize("12px");
            status.getStyle().setFontWeight("500");
        }

        if (PropertyStatus.SOLD == property.getPropertyStatus()){
            status.getStyle().setBackground("#C5221F");
            status.getStyle().setColor("#FFFFFF");
            status.getStyle().setPadding("5px");
            status.getStyle().setBorderRadius("5px");
            status.getStyle().setFontSize("12px");
            status.getStyle().setFontWeight("500");
        }

        if (PropertyStatus.UNDER_OFFER == property.getPropertyStatus()){
            status.getStyle().setBackground("#F4A74B");
            status.getStyle().setColor("#FFFFFF");
            status.getStyle().setPadding("5px");
            status.getStyle().setBorderRadius("5px");
            status.getStyle().setFontSize("12px");
            status.getStyle().setFontWeight("500");
        }

        propertyCard.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("property-detail/" + property.getId()));
        });
        return propertyCard;
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
        updatePropertyLayout();
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
        updatePropertyLayout();
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
        updatePropertyLayout();
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
        updatePropertyLayout();
    }

    private void openAddPropertyDialog() {
        AddPropertyForm addPropertyForm = new AddPropertyForm(propertyService, phaseService, cityService, stateService, userService,  (v) -> updatePropertyLayout());
        addPropertyForm.open();
    }

    private Image createImage(Property property) {
        if (property.getPropertyImages() != null && !property.getPropertyImages().isEmpty()) {
            byte[] imageBytes = property.getPropertyImages().get(0).getPropertyImages();
            StreamResource resource = new StreamResource("property-image-" + property.getId(), () -> new ByteArrayInputStream(imageBytes));
            Image image = new Image(resource, "Property Image");
            image.setWidthFull();
            return image;
        }
        return new Image("placeholder-image-url", "No Image");
    }
}

package ng.org.mirabilia.pms.views.modules.properties.content.tabs;

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
import ng.org.mirabilia.pms.domain.entities.City;
import ng.org.mirabilia.pms.domain.entities.Phase;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.forms.properties.AddPropertyForm;
import ng.org.mirabilia.pms.views.forms.properties.EditPropertyForm;

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

    private final HorizontalLayout propertyLayout; // Changed to HorizontalLayout
    private final TextField searchField;
    private final ComboBox<String> stateFilter;
    private final ComboBox<String> cityFilter;
    private final ComboBox<String> phaseFilter;
    private final ComboBox<PropertyType> propertyTypeFilter;
    private final ComboBox<PropertyStatus> propertyStatusFilter;
//    private final ComboBox<String> agentFilter;
//    private final ComboBox<String> clientFilter;

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
        phaseFilter.addClassName("custom-filter");

        propertyTypeFilter = new ComboBox<>("Type", PropertyType.values());
        propertyTypeFilter.addValueChangeListener(e -> updatePropertyLayout());
        propertyTypeFilter.addClassName("custom-filter");

        propertyStatusFilter = new ComboBox<>("Status", PropertyStatus.values());
        propertyStatusFilter.addValueChangeListener(e -> updatePropertyLayout());
        propertyStatusFilter.addClassName("custom-filter");

//        agentFilter = new ComboBox<>("Agent");
//        agentFilter.setItems(userService.getAgents().stream().map(agent -> agent.getFirstName() + " " + agent.getLastName()).collect(Collectors.toList()));
//        agentFilter.addValueChangeListener(e -> updatePropertyLayout());
//        agentFilter.addClassName("custom-filter");

//        clientFilter = new ComboBox<>("Client");
//        clientFilter.setItems(userService.getClients().stream().map(client -> client.getFirstName() + " " + client.getLastName()).collect(Collectors.toList()));
//        clientFilter.addValueChangeListener(e -> updatePropertyLayout());
//        clientFilter.addClassName("custom-filter");

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetFilters());
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");

        Button addPropertyButton = new Button("Add Property");
        addPropertyButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        addPropertyButton.addClickListener(e -> openAddPropertyDialog());
        addPropertyButton.addClassName("custom-button");
        addPropertyButton.addClassName("custom-add-button");
        addPropertyButton.addClassName("custom-toolbar-button");
        addPropertyButton.setWidth("100px");

        propertyLayout = new HorizontalLayout();
        propertyLayout.addClassName("property-layout");
        propertyLayout.getStyle().setDisplay(Style.Display.FLEX);
        propertyLayout.getStyle().setFlexWrap(Style.FlexWrap.WRAP);
        propertyLayout.getStyle().setMargin("auto");
        propertyLayout.getStyle().setJustifyContent(Style.JustifyContent.CENTER);

        HorizontalLayout firstRowToolbar = new HorizontalLayout(searchField, stateFilter, cityFilter, phaseFilter, propertyTypeFilter, propertyStatusFilter, resetButton, addPropertyButton);
        firstRowToolbar.addClassName("custom-toolbar");
        firstRowToolbar.setWidthFull();
        firstRowToolbar.getStyle().setPosition(Style.Position.ABSOLUTE);
        firstRowToolbar.getStyle().setDisplay(Style.Display.FLEX).setFlexWrap(Style.FlexWrap.WRAP);

        IFrame mapIframe = new IFrame("https://www.google.com/maps/embed?pb=!1m14!1m12!1m3!1d247.63926377306166!2d3.269721726637287!3d6.741992916800436!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!5e0!3m2!1sen!2sng!4v1730238453714!5m2!1sen!2sng\" width=\"100%\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade");
        mapIframe.setWidth("100%");
        mapIframe.setHeight("50vh");
        mapIframe.getElement().getStyle().set("border", "0");
        H4 title = new H4("Property List");
        title.getStyle().setPaddingLeft("50px");
        title.getStyle().setPaddingTop("30px");

        add(firstRowToolbar, mapIframe, title, propertyLayout);

        updatePropertyLayout();
    }

    private void updatePropertyLayout() {
        propertyLayout.removeAll();

        String keyword = searchField.getValue();
        String selectedState = stateFilter.getValue();
        String selectedCity = cityFilter.getValue();
        String selectedPhase = phaseFilter.getValue();
        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
        PropertyStatus selectedPropertyStatus = propertyStatusFilter.getValue();
//        String selectedAgent = agentFilter.getValue();
//        String selectedClient = clientFilter.getValue();

        List<Property> properties = propertyService.searchPropertiesByFiltersWithoutUsers(keyword, selectedState, selectedCity, selectedPhase, selectedPropertyType, selectedPropertyStatus);

        for (Property property : properties) {
            propertyLayout.add(createPropertyCard(property));
        }
    }

    private Div createPropertyCard(Property property) {
        Div propertyCard = new Div();
        propertyCard.addClassName("property-card");
        VerticalLayout verticalLayout = new VerticalLayout();
        Image image = createImage(property);
        verticalLayout.setWidth("90%");
        verticalLayout.getStyle().setPadding("0");
        image.setWidth("330px");
        image.setHeight("162px");
        image.getStyle().setBorderRadius("10px");


        String propertyStatusFormat = property.getPropertyStatus().name().replace("_", " ");
        String propertyTypeFormat = property.getPropertyType().name().replace("_", " ");
        String formattedPrice = NumberFormat.getInstance().format(property.getPrice());
        String location = property.getStreet() + ", " + property.getPhase().getName();
        Div locationText = new Div(location);
        Div propertyType = new Div(propertyTypeFormat);
        Div price = new Div("₦" + formattedPrice);
        Div status = new Div(propertyStatusFormat);
        status.getStyle().setPosition(Style.Position.RELATIVE);
        status.getStyle().setTop("60px");
        status.getStyle().setLeft("190px");
        status.setWidth("90px");
        status.getStyle().setTextAlign(Style.TextAlign.CENTER);


        HorizontalLayout horizontalLayoutTop = new HorizontalLayout(propertyType, price);
        propertyType.getStyle().setFontWeight("600");
        propertyType.getStyle().setColor("#11142D");
        propertyType.getStyle().setFontSize("16px");
        price.getStyle().setFontWeight("300");
        price.getStyle().setColor("#6C5DD3");
        price.getStyle().setBackground("#F0EEFF");
        price.getStyle().setPadding("5px");
        price.getStyle().setBorderRadius("5px");
        horizontalLayoutTop.setWidthFull();
        horizontalLayoutTop.getStyle().setDisplay(Style.Display.FLEX);
        horizontalLayoutTop.setJustifyContentMode(JustifyContentMode.BETWEEN);
        horizontalLayoutTop.getStyle().setAlignItems(Style.AlignItems.CENTER);



        Icon mapPoint = new Icon(VaadinIcon.MAP_MARKER);
        mapPoint.getStyle().setColor("red");
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

    private void setStatusStyle(Div status, PropertyStatus propertyStatus) {
        switch (propertyStatus) {
            case AVAILABLE:
                status.getStyle().setBackground("#34A853");
                break;
            case SOLD:
                status.getStyle().setBackground("#C5221F");
                break;
            case UNDER_OFFER:
                status.getStyle().setBackground("#F4A74B");
                break;
        }
        status.getStyle().setColor("#FFFFFF");
        status.getStyle().setPadding("5px");
        status.getStyle().setBorderRadius("5px");
        status.getStyle().setFontSize("12px");
        status.getStyle().setFontWeight("500");
    }

    private void resetFilters() {
        searchField.clear();
        stateFilter.clear();
        cityFilter.clear();
        phaseFilter.clear();
        propertyTypeFilter.clear();
        propertyStatusFilter.clear();
//        agentFilter.clear();
//        clientFilter.clear();
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

    private void openAddPropertyDialog() {
        AddPropertyForm addPropertyForm = new AddPropertyForm(propertyService, phaseService, userService, (v) -> updatePropertyLayout());
        addPropertyForm.open();
    }

    private void openEditPropertyDialog(Property property) {
        EditPropertyForm editPropertyForm = new EditPropertyForm(propertyService, phaseService, userService, property, (v) -> updatePropertyLayout());
        editPropertyForm.open();
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

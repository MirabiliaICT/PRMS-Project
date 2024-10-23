package ng.org.mirabilia.pms.views.forms.properties;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Style;
import ng.org.mirabilia.pms.entities.Phase;
import ng.org.mirabilia.pms.entities.Property;
import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.PropertyFeatures;
import ng.org.mirabilia.pms.entities.enums.PropertyStatus;
import ng.org.mirabilia.pms.entities.enums.PropertyType;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EditPropertyForm extends Dialog {

    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final UserService userService;
    private final Property property;
    private final Consumer<Void> onSuccess;

    private final TextField streetField = new TextField("Street");
    private final ComboBox<String> phaseComboBox = new ComboBox<>("Phase");
    private final ComboBox<PropertyType> propertyTypeComboBox = new ComboBox<>("Property Type", PropertyType.values());
    private final ComboBox<PropertyStatus> propertyStatusComboBox = new ComboBox<>("Property Status", PropertyStatus.values());
    private final TextArea descriptionField = new TextArea("Description");
    private final NumberField sizeField = new NumberField("Size (sq ft)");
    private final NumberField priceField = new NumberField("Price");
    private final ComboBox<User> agentComboBox = new ComboBox<>("Agent");
    private final ComboBox<User> clientComboBox = new ComboBox<>("Client");
    private final NumberField noOfBedrooms = new NumberField("No of Bedrooms");
    private final NumberField noOfBathrooms = new NumberField("No of Bathrooms");
    public CheckboxGroup<PropertyFeatures> features = new CheckboxGroup<>("Features");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private byte[] uploadedImage;

    public EditPropertyForm(PropertyService propertyService, PhaseService phaseService, UserService userService, Property property, Consumer<Void> onSuccess) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.userService = userService;
        this.property = property;
        this.onSuccess = onSuccess;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        setWidth("95%");
        addClassName("custom-property-form");

        configureFormFields();
        createFormLayout();
        populateFields();
        addPropertyTypeListener();
        addPropertyStatusListener();
    }

    private void configureFormFields() {
        phaseComboBox.setItems(phaseService.getAllPhases().stream().map(Phase::getName).collect(Collectors.toList()));
        phaseComboBox.setRequired(true);
        phaseComboBox.addClassName("custom-combo-box");

        agentComboBox.setItems(userService.getAgents());
        agentComboBox.setItemLabelGenerator(agent -> agent.getFirstName() + " " + agent.getLastName());
        agentComboBox.setRequired(true);
        agentComboBox.addClassName("custom-combo-box");

        clientComboBox.setItems(userService.getClients());
        clientComboBox.setItemLabelGenerator(client -> client.getFirstName() + " " + client.getLastName());
        clientComboBox.addClassName("custom-combo-box");

        descriptionField.setPlaceholder("Enter property description...");
        descriptionField.setMaxLength(1000);
        descriptionField.addClassName("custom-text-area");

        sizeField.setMin(0);
        sizeField.setPlaceholder("Square feet");
        sizeField.addClassName("custom-number-field");

        priceField.setMin(0);
        priceField.setPlaceholder("Price in NGN");
        priceField.addClassName("custom-number-field");

        streetField.addClassName("custom-text-field");

        noOfBedrooms.setMin(0);
        noOfBedrooms.setPlaceholder("No of Bedrooms");
        noOfBedrooms.addClassName("custom-number-field");

        noOfBathrooms.setMin(0);
        noOfBathrooms.setPlaceholder("No of Bathrooms");
        noOfBathrooms.addClassName("custom-number-field");

        features.setItems(PropertyFeatures.values());
        features.setRequired(true);
        features.addClassName("custom-checkbox-group");
    }

    private void createFormLayout() {
        H2 header = new H2("Edit Property");
        getHeader().add(header);
        header.addClassName("custom-form-header");

        H6 location = new H6("LOCATION");
        location.getStyle().set("margin-top", "8px");

        H6 propertyDetails = new H6("PROPERTY DETAILS");
        propertyDetails.getStyle().set("margin-top", "20px");

        FormLayout formLayout = new FormLayout(streetField, phaseComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        FormLayout propertiesDetails = new FormLayout(propertyTypeComboBox, propertyStatusComboBox, sizeField, priceField, agentComboBox, clientComboBox, noOfBathrooms, noOfBedrooms);
        propertiesDetails.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        VerticalLayout verticalLayout = new VerticalLayout(propertiesDetails);

        FormLayout featuresDesc = new FormLayout(features, descriptionField);

        Button saveButton = new Button("Save", e -> saveProperty());
        Button deleteButton = new Button("Delete", e -> deleteProperty());

        HorizontalLayout btn = new HorizontalLayout(saveButton, deleteButton);
        Button discardButton = new Button("Discard", e -> close());

        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);
        deleteButton.addClickShortcut(Key.DELETE);

        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");
        deleteButton.addClassName("custom-button");
        deleteButton.addClassName("custom-delete-button");
        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        deleteButton.getStyle().setMarginLeft("0");

        HorizontalLayout buttonLayout = new HorizontalLayout(discardButton, btn);
        buttonLayout.setWidthFull();
        buttonLayout.addClassName("custom-button-layout");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout uploadLayout = new VerticalLayout(upload);

        VerticalLayout contentLayout = new VerticalLayout(header, location, formLayout, propertyDetails, propertiesDetails, verticalLayout, featuresDesc, uploadLayout, buttonLayout);
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.addClassName("custom-content-layout");
        add(contentLayout);
    }

    private void populateFields() {
        streetField.setValue(property.getStreet());
        phaseComboBox.setValue(property.getPhase() != null ? property.getPhase().getName() : null);
        propertyTypeComboBox.setValue(property.getPropertyType());
        propertyStatusComboBox.setValue(property.getPropertyStatus());
        descriptionField.setValue(property.getDescription() != null ? property.getDescription() : "");
        sizeField.setValue(property.getSize());
        priceField.setValue(property.getPrice() != null ? property.getPrice().doubleValue() : 0);
        noOfBedrooms.setValue(property.getNoOfBedrooms() != 0 ? property.getNoOfBedrooms() : 0);
        noOfBathrooms.setValue(property.getNoOfBathrooms() != 0 ? property.getNoOfBathrooms() : 0);
        features.setValue(property.getFeatures() != null ? features.getValue() : Set.of());

//        agentComboBox.setValue(userService.getUserById(property.getAgentId()).orElse(null));
//        if (property.getAgentId() != null) {
//            agentComboBox.setValue(userService.getUserById(property.getAgentId()).orElse(null));
//        }
//
//        if (property.getClientId() != null) {
//            clientComboBox.setValue(userService.getUserById(property.getClientId()).orElse(null));
//        }

        agentComboBox.setValue(property.getAgentId() != null ? userService.getUserById(property.getAgentId()).orElse(null) : null);
        clientComboBox.setValue(property.getClientId() != null ? userService.getUserById(property.getClientId()).orElse(null) : null);

        if (property.getPropertyStatus() != null && property.getPropertyStatus().equals(PropertyStatus.AVAILABLE)) {
            clientComboBox.setVisible(false);
            agentComboBox.setVisible(false);
        } else {
            clientComboBox.setVisible(true);
            agentComboBox.setVisible(true);
        }

        if (property.getPropertyType() != null && property.getPropertyType().equals(PropertyType.LAND)){
            noOfBathrooms.setVisible(false);
            noOfBedrooms.setVisible(false);
        } else {
            noOfBathrooms.setVisible(true);
            noOfBedrooms.setVisible(true);
        }


    }

    private void saveProperty() {
        if (streetField.getValue() == null || streetField.getValue().isEmpty() ||
                phaseComboBox.getValue() == null ||
                propertyTypeComboBox.getValue() == null ||
                propertyStatusComboBox.getValue() == null ||
                priceField.getValue() == null || priceField.getValue() <= 0 ||
                sizeField.getValue() == null || sizeField.getValue() <= 0 ||
                agentComboBox.getValue() == null) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        property.setStreet(streetField.getValue());
        property.setPhase(phaseService.getPhaseByName(phaseComboBox.getValue()));
        property.setPropertyType(propertyTypeComboBox.getValue());
        property.setPropertyStatus(propertyStatusComboBox.getValue());
        property.setDescription(descriptionField.getValue());
        property.setSize(sizeField.getValue());
        property.setPrice(BigDecimal.valueOf(priceField.getValue()));

        try {
            String selectedAgentName = agentComboBox.getValue().getFirstName() + " " + agentComboBox.getValue().getLastName();
            UUID agentId = userService.getAgentIdByName(selectedAgentName);
            property.setAgentId(agentId);
        } catch (IllegalArgumentException ex) {
            Notification.show("Agent not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (clientComboBox.getValue() != null) {
            try {
                String selectedClientName = clientComboBox.getValue().getFirstName() + " " + clientComboBox.getValue().getLastName();
                UUID clientId = userService.getClientIdByName(selectedClientName);
                property.setClientId(clientId);
            } catch (IllegalArgumentException ex) {
                Notification.show("Client not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        }

        propertyService.saveProperty(property);

        Notification.show("Property updated successfully", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        close();
        onSuccess.accept(null);
    }

    private void deleteProperty() {
        try {
            propertyService.deleteProperty(property.getId());
            this.close();
            onSuccess.accept(null);
        } catch (Exception ex) {
            Notification.show("Unable to delete property: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void addPropertyTypeListener() {
        propertyTypeComboBox.addValueChangeListener(event -> {
            PropertyType selectedType = event.getValue();
            if (selectedType != null && selectedType.equals(PropertyType.LAND)) {
                noOfBedrooms.setVisible(false);
                noOfBathrooms.setVisible(false);
                features.setVisible(false);
            } else {
                noOfBedrooms.setVisible(true);
                noOfBathrooms.setVisible(true);
                features.setVisible(true);
            }
        });
    }

    private void addPropertyStatusListener() {
        propertyStatusComboBox.addValueChangeListener(event -> {
            PropertyStatus selectedStatus = event.getValue();
            if (selectedStatus != null && selectedStatus.equals(PropertyStatus.AVAILABLE)) {
                clientComboBox.setVisible(false);
                agentComboBox.setVisible(false);
            } else {
                clientComboBox.setVisible(true);
                agentComboBox.setVisible(true);
            }
        });
    }
}

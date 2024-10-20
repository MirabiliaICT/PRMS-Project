package ng.org.mirabilia.pms.views.forms.properties;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import ng.org.mirabilia.pms.entities.Phase;
import ng.org.mirabilia.pms.entities.Property;
import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.PropertyStatus;
import ng.org.mirabilia.pms.entities.enums.PropertyType;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AddPropertyForm extends Dialog {

    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final UserService userService;
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

    public AddPropertyForm(PropertyService propertyService, PhaseService phaseService, UserService userService, Consumer<Void> onSuccess) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.userService = userService;
        this.onSuccess = onSuccess;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        setWidth("400px");
        addClassName("custom-form");



        configureFormFields();
        createFormLayout();
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
    }

    private void createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(streetField, phaseComboBox, propertyTypeComboBox, propertyStatusComboBox, descriptionField, sizeField, priceField, agentComboBox, clientComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.addClassName("custom-form-layout");

        H2 header = new H2("New Property");
        header.addClassName("custom-form-header");

        Button saveButton = new Button("Save", e -> saveProperty());
        Button discardButton = new Button("Discard", e -> close());
        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");

        HorizontalLayout buttonLayout = new HorizontalLayout(discardButton, saveButton);
        buttonLayout.setWidthFull();
        buttonLayout.addClassName("custom-button-layout");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        VerticalLayout contentLayout = new VerticalLayout(header,formLayout, buttonLayout);
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.addClassName("custom-content-layout");
        add(contentLayout);
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

        Property newProperty = new Property();
        newProperty.setStreet(streetField.getValue());
        newProperty.setPhase(phaseService.getPhaseByName(phaseComboBox.getValue()));
        newProperty.setPropertyType(propertyTypeComboBox.getValue());
        newProperty.setPropertyStatus(propertyStatusComboBox.getValue());
        newProperty.setDescription(descriptionField.getValue());
        newProperty.setSize(sizeField.getValue());
        newProperty.setPrice(BigDecimal.valueOf(priceField.getValue()));

        try {
            String selectedAgentName = agentComboBox.getValue().getFirstName() + " " + agentComboBox.getValue().getLastName();
            UUID agentId = userService.getAgentIdByName(selectedAgentName);
            newProperty.setAgentId(agentId);
        } catch (IllegalArgumentException ex) {
            Notification.show("Agent not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (clientComboBox.getValue() != null) {
            try {
                String selectedClientName = clientComboBox.getValue().getFirstName() + " " + clientComboBox.getValue().getLastName();
                UUID clientId = userService.getClientIdByName(selectedClientName);
                newProperty.setClientId(clientId);
            } catch (IllegalArgumentException ex) {
                Notification.show("Client not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        }

        propertyService.saveProperty(newProperty);

        Notification.show("Property added successfully", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        close();
        onSuccess.accept(null);
    }
}

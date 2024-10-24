package ng.org.mirabilia.pms.views.forms.properties;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.Phase;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.PropertyFeatures;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
    private final NumberField noOfBedrooms = new NumberField("No of Bedrooms");
    private final NumberField noOfBathrooms = new NumberField("No of Bathrooms");
    public CheckboxGroup<PropertyFeatures> features = new CheckboxGroup<>("Features");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private byte[] uploadedImage;

    private List<byte[]> uploadedImages = new ArrayList<>();
    private final VerticalLayout imagePreviewLayout = new VerticalLayout();
    public AddPropertyForm(PropertyService propertyService, PhaseService phaseService, UserService userService, Consumer<Void> onSuccess) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.userService = userService;
        this.onSuccess = onSuccess;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        setWidth("95%");
        addClassName("custom-property-form");

        configureFormFields();
        createFormLayout();
        addPropertyTypeListener();
        addPropertyStatusListener();
        configureUpload();
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
        H2 header = new H2("New Property");
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
        Button discardButton = new Button("Discard", e -> close());
        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");

        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);

        HorizontalLayout buttonLayout = new HorizontalLayout(discardButton, saveButton);
        buttonLayout.setWidthFull();
        buttonLayout.addClassName("custom-button-layout");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonLayout.getStyle().setMarginTop("30px");
        buttonLayout.getStyle().setPaddingBottom("50px");

        VerticalLayout uploadLayout = new VerticalLayout(upload);

        VerticalLayout contentLayout = new VerticalLayout(header, location, formLayout, propertyDetails, propertiesDetails, verticalLayout, featuresDesc, uploadLayout, buttonLayout);
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
                sizeField.getValue() == null || sizeField.getValue() <= 0) {
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

        if (propertyTypeComboBox.getValue().equals(PropertyType.LAND)) {
            newProperty.setNoOfBedrooms(0);
            newProperty.setNoOfBathrooms(0);
        } else {
            newProperty.setNoOfBedrooms(noOfBedrooms.getValue());
            newProperty.setNoOfBathrooms(noOfBathrooms.getValue());
        }

        newProperty.setFeatures(features.getValue());

        if (!propertyStatusComboBox.getValue().equals(PropertyStatus.AVAILABLE)) {
            try {
                String selectedAgentName = agentComboBox.getValue().getFirstName() + " " + agentComboBox.getValue().getLastName();
                UUID agentId = userService.getAgentIdByName(selectedAgentName);
                newProperty.setAgentId(agentId);
            } catch (Exception e) {
                Notification.show("Failed to set the agent", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            try {
                String selectedClientName = clientComboBox.getValue().getFirstName() + " " + clientComboBox.getValue().getLastName();
                UUID clientId = userService.getClientIdByName(selectedClientName);
                newProperty.setClientId(clientId);
            } catch (Exception e) {
                Notification.show("Failed to set the client", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }

        propertyService.saveProperty(newProperty);



        if (uploadedImage != null) {
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setPropertyImages(uploadedImage);
            newProperty.addPropertyImage(propertyImage);
        }



        if (!uploadedImages.isEmpty()) {
            List<PropertyImage> propertyImages = uploadedImages.stream()
                    .map(imageBytes -> {
                        PropertyImage propertyImage = new PropertyImage();
                        propertyImage.setPropertyImages(imageBytes);
                        propertyImage.setProperty(newProperty);
                        return propertyImage;
                    }).toList();


//            newProperty.getPropertyImages().addAll(propertyImages);
            newProperty.setPropertyImages(propertyImages);
        }

        propertyService.saveProperty(newProperty);
        onSuccess.accept(null);
        close();
    }


    private void configureUpload() {
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/gif");
        upload.setMaxFiles(10);

        upload.addSucceededListener(event -> {
            uploadedImage = readImageFromBuffer();

            if (uploadedImage != null) {
                uploadedImages.add(uploadedImage);
                displayUploadedImage(uploadedImage);
                Notification.show("Image uploaded successfully.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        upload.setWidthFull();
        upload.getStyle().setTextAlign(Style.TextAlign.CENTER);
    }

    private void displayUploadedImage(byte[] imageBytes) {
        Image image = new Image(new StreamResource("uploaded-image", () -> new ByteArrayInputStream(imageBytes)), "Uploaded Image");
        image.setMaxWidth("100px");
        image.setMaxHeight("100px");

        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
        deleteButton.addClickListener(e -> {
            uploadedImages.remove(imageBytes);
            imagePreviewLayout.remove(image, deleteButton);
        });

        imagePreviewLayout.add(new HorizontalLayout(image, deleteButton));
    }

    private byte[] readImageFromBuffer() {
        try (InputStream inputStream = buffer.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            Notification.show("Error reading uploaded image: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
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

package ng.org.mirabilia.pms.views.forms.properties;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.*;
import ng.org.mirabilia.pms.domain.enums.*;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.services.implementations.GltfStorageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddPropertyForm extends Dialog {

    private final PropertyService propertyService;
    private final PhaseService phaseService;

    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;
    private final Consumer<Void> onSuccess;

    private final TextField streetField = new TextField("Street");
    private final ComboBox<String> phaseComboBox = new ComboBox<>("Phase");
    private final ComboBox<String> cityComboBox = new ComboBox<>("City");
    private final ComboBox<String> stateComboBox = new ComboBox<>("State");
    private final ComboBox<PropertyType> propertyTypeComboBox = new ComboBox<>("Property Type", PropertyType.values());
    private final ComboBox<PropertyStatus> propertyStatusComboBox = new ComboBox<>("Property Status", PropertyStatus.values());
    private final TextArea descriptionField = new TextArea("Description");
    private final TextField titleField = new TextField("Title");
    private final NumberField sizeField = new NumberField("Size (sq ft)");
    private final NumberField priceField = new NumberField("Price");
    private final ComboBox<User> agentComboBox = new ComboBox<>("Agent");
    private final ComboBox<User> clientComboBox = new ComboBox<>("Client");
    private final NumberField noOfBedrooms = new NumberField("No of Bedrooms");
    private final NumberField noOfBathrooms = new NumberField("No of Bathrooms");
    public CheckboxGroup<PropertyFeatures> features = new CheckboxGroup<>("Features");

    private final ComboBox<Integer> builtAtComboBox = new ComboBox<>("Year Built");


    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private final Upload uploadGltf = new Upload(buffer);
    private byte[] uploadedImage;
    private byte[] uploadedGlft;

    private List<byte[]> uploadedImages = new ArrayList<>();
    private final VerticalLayout imagePreviewLayout = new VerticalLayout();

    private final VerticalLayout interiorDetailsLayout = new VerticalLayout();
    private final VerticalLayout exteriorDetailsLayout = new VerticalLayout();

    private final Property newProperty = new Property();


    H6 interiorDetailsHeader = new H6("INTERIOR DETAILS");
    H6 exteriorDetailsHeader = new H6("EXTERIOR DETAILS");

    private final VerticalLayout interiorLayoutWithHeader = new VerticalLayout();
    private final VerticalLayout exteriorLayoutWithHeader = new VerticalLayout();

    public AddPropertyForm(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService, Consumer<Void> onSuccess) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.userService = userService;
        this.onSuccess = onSuccess;
//        this.property = property;

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
        configureUploadGltf();

    }

    private void configureFormFields() {
        stateComboBox.setItems(stateService.getAllStates().stream().map(State::getName).collect(Collectors.toList()));
        stateComboBox.addValueChangeListener(event -> onStateSelected());
        stateComboBox.setRequired(true);
        stateComboBox.addClassName("custom-combo-box");

        cityComboBox.setItems(cityService.getAllCities().stream().map(City::getName).collect(Collectors.toList()));
        cityComboBox.addValueChangeListener(event -> onCitySelected());
        cityComboBox.setRequired(true);
        cityComboBox.addClassName("custom-combo-box");

        phaseComboBox.setItems(phaseService.getAllPhases().stream().map(Phase::getName).collect(Collectors.toList()));
        phaseComboBox.setRequired(true);
        phaseComboBox.addValueChangeListener(event -> onPhaseSelected());
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

        titleField.addClassName("custom-text-field");

        noOfBedrooms.setMin(0);
        noOfBedrooms.setPlaceholder("No of Bedrooms");
        noOfBedrooms.addClassName("custom-number-field");

        noOfBathrooms.setMin(0);
        noOfBathrooms.setPlaceholder("No of Bathrooms");
        noOfBathrooms.addClassName("custom-number-field");

        features.setItems(PropertyFeatures.values());
        features.setRequired(true);
        features.addClassName("custom-checkbox-group");

        descriptionField.setWidth("50%");

        propertyTypeComboBox.setItems(PropertyType.values());
        propertyTypeComboBox.setItemLabelGenerator(PropertyType::getDisplayName);

        propertyStatusComboBox.setItems(PropertyStatus.values());
        propertyStatusComboBox.setItemLabelGenerator(PropertyStatus::getDisplayName);

        int currentYear = Year.now().getValue();
        List<Integer> years = IntStream.rangeClosed(2000, currentYear)
                .boxed()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());
        builtAtComboBox.setItems(years);
        builtAtComboBox.setPlaceholder("Select Year Built");
        builtAtComboBox.setRequired(true);

//        DecimalFormat decimalFormat = new DecimalFormat("#,###");
//
//        priceField.setValueChangeMode(ValueChangeMode.EAGER); // Update immediately as user types
//        priceField.addValueChangeListener(event -> {
//            if (event.getValue() != null) {
//                String formattedValue = decimalFormat.format(event.getValue());
//                priceField.setValue(Double.parseDouble(formattedValue.replace(",", ""))); // Keep raw value for calculations
//                priceField.setLabel("Price (e.g., " + formattedValue + ")");
//            }
//        });


        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>(detail.name());
            checkboxGroup.setItems(detail.getItems());  // Use the items from the enum
            checkboxGroup.setLabel(detail.name());
            checkboxGroup.setRequired(true);
            interiorDetailsLayout.add(checkboxGroup);
        }

        for (ExteriorDetails detail : ExteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>(detail.name());
            checkboxGroup.setItems(detail.getItems());  // Use the items from the enum
            checkboxGroup.setLabel(detail.name());
            checkboxGroup.setRequired(true);
            exteriorDetailsLayout.add(checkboxGroup);
        }
    }

    private void createFormLayout() {
        H2 headerTitle = new H2("New Property");
        H4 closeBtn = new H4("X");
        closeBtn.getStyle().setMarginRight("30px");
        closeBtn.addClickListener(e -> close());
        HorizontalLayout header = new HorizontalLayout(headerTitle, closeBtn);
        header.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        header.setWidthFull();
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);
        getHeader().add(header);
        header.addClassName("custom-form-header");

        H6 location = new H6("LOCATION");
        location.getStyle().set("margin-top", "8px");

        H6 propertyDetails = new H6("PROPERTY DETAILS");
        propertyDetails.getStyle().set("margin-top", "20px");

        FormLayout formLayout = new FormLayout(stateComboBox, cityComboBox, phaseComboBox, streetField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        FormLayout propertiesDetails = new FormLayout(titleField, propertyTypeComboBox,
                propertyStatusComboBox, sizeField, priceField, agentComboBox, clientComboBox,
                noOfBathrooms, noOfBedrooms, features, builtAtComboBox);
        propertiesDetails.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button saveButton = new Button("Save", e -> saveProperty());
        Button discardButton = new Button("Discard Charges", e -> close());
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

        descriptionField.setHeight("200px");

        VerticalLayout uploadLayout = new VerticalLayout(new H6("Image upload"), upload);
        VerticalLayout uploadGltfLayout = new VerticalLayout(new H6("3D Model upload"), uploadGltf);

        interiorLayoutWithHeader.add(interiorDetailsHeader, interiorDetailsLayout);
        exteriorLayoutWithHeader.add(exteriorDetailsHeader, exteriorDetailsLayout);

        HorizontalLayout interiorEtExterior = new HorizontalLayout( interiorLayoutWithHeader, exteriorLayoutWithHeader);

        VerticalLayout contentLayout = new VerticalLayout(header, location, formLayout, propertyDetails, propertiesDetails, interiorEtExterior, descriptionField, uploadLayout, uploadGltfLayout, buttonLayout);
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.addClassName("custom-content-layout");
        add(contentLayout);
    }

    private void saveProperty() {
        if (streetField.getValue() == null || streetField.getValue().isEmpty() ||
                titleField.getValue() == null || titleField.getValue().isEmpty() ||
                phaseComboBox.getValue() == null ||
                propertyTypeComboBox.getValue() == null ||
                propertyStatusComboBox.getValue() == null ||
                priceField.getValue() == null || priceField.getValue() <= 0 ||
                sizeField.getValue() == null || sizeField.getValue() <= 0 ) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }else if (uploadedImages.isEmpty()){
            Notification.show("Please upload at least one property image", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (propertyTypeComboBox.getValue() != PropertyType.LAND){
            if (builtAtComboBox.isEmpty() || builtAtComboBox.getValue() == null){
                Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        }


        newProperty.setStreet(streetField.getValue());
        newProperty.setPhase(phaseService.getPhaseByName(phaseComboBox.getValue()));
        newProperty.setTitle(titleField.getValue());
        newProperty.setPropertyType(propertyTypeComboBox.getValue());
        newProperty.setPropertyStatus(propertyStatusComboBox.getValue());
        newProperty.setDescription(descriptionField.getValue());
        newProperty.setSize(sizeField.getValue());
        newProperty.setPrice(BigDecimal.valueOf(priceField.getValue()));

        if (propertyTypeComboBox.getValue().equals(PropertyType.LAND)) {
            newProperty.setNoOfBedrooms(0);
            newProperty.setNoOfBathrooms(0);
            newProperty.setLaundryItems(Set.of());
            newProperty.setKitchenItems(Set.of());
            newProperty.setInteriorFlooringItems(Set.of());
            newProperty.setBuiltAt(0);
            newProperty.setFeatures(Set.of());

        } else {
            newProperty.setNoOfBedrooms(noOfBedrooms.getValue());
            newProperty.setNoOfBathrooms(noOfBathrooms.getValue());
            newProperty.setLaundryItems(newProperty.getLaundryItems());
            newProperty.setKitchenItems(newProperty.getKitchenItems());
            newProperty.setInteriorFlooringItems(newProperty.getInteriorFlooringItems());
            newProperty.setBuiltAt(builtAtComboBox.getValue());
            newProperty.setFeatures(features.getValue());

        }

        if (!propertyStatusComboBox.getValue().equals(PropertyStatus.AVAILABLE)) {
            try {
                String selectedAgentName = agentComboBox.getValue().getFirstName() + " " + agentComboBox.getValue().getLastName();
                Long agentId = userService.getAgentIdByName(selectedAgentName);
                newProperty.setAgentId(agentId);
            } catch (Exception e) {
                Notification.show("Failed to set the agent", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            try {
                String selectedClientName = clientComboBox.getValue().getFirstName() + " " + clientComboBox.getValue().getLastName();
                Long clientId = userService.getClientIdByName(selectedClientName);
                newProperty.setClientId(clientId);
            } catch (Exception e) {
                Notification.show("Failed to set the client", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }



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


            newProperty.setPropertyImages(propertyImages);
        }

        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) interiorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                Set<String> selectedItems = checkboxGroup.getValue();
                System.out.println(detail.name() + " selected items: " + selectedItems);
                switch (detail) {
                    case Laundry:
                        newProperty.setLaundryItems(selectedItems);
                        break;
                    case Kitchen:
                        newProperty.setKitchenItems(selectedItems);
                        break;
                    case Flooring:
                        newProperty.setInteriorFlooringItems(selectedItems);
                        break;
                }
            }
        }


        for (ExteriorDetails detail : ExteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) exteriorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                Set<String> selectedItems = checkboxGroup.getValue();
                System.out.println(detail.name() + " selected items: " + selectedItems);
                switch (detail) {
                    case Security:
                        newProperty.setSecurityItems(selectedItems);
                        break;
                    case Flooring:
                        newProperty.setExteriorFlooringItems(selectedItems);
                        break;
                }
            }
        }


        if (uploadGltf != null) {
            try {
                byte[] gltfData = buffer.getInputStream().readAllBytes();
                GltfModel gltfModel = new GltfModel();
                gltfModel.setData(gltfData);

                gltfModel.setProperty(newProperty);
                newProperty.setModel(gltfModel);


            } catch (IOException e) {
                Notification.show("Failed to upload GLTF model", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }



        propertyService.saveProperty(newProperty);
        onSuccess.accept(null);
        Notification.show("Property saved successfully", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

    public void configureUploadGltf() {
        uploadGltf.setMaxFiles(1);
        uploadGltf.setAcceptedFileTypes("model/gltf+json", "model/gltf-binary", ".gltf", ".glb");

        uploadGltf.addSucceededListener(event -> {
            try {

                byte[] gltfData = buffer.getInputStream().readAllBytes();


                GltfModel model = new GltfModel();
                model.setData(gltfData);
                model.setName(event.getFileName());

                model.setProperty(newProperty);
                newProperty.setModel(model);

                Notification.show("3D Model uploaded successfully", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                System.out.println(" Model uploadesd" + newProperty.getModel().getProperty().getId());
            } catch (IOException e) {
                Notification.show("Failed to upload GLTF model", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }



    private void addPropertyTypeListener() {
        propertyTypeComboBox.addValueChangeListener(event -> {
            PropertyType selectedType = event.getValue();
            if (selectedType != null && selectedType.equals(PropertyType.LAND)) {
                noOfBedrooms.setVisible(false);
                noOfBathrooms.setVisible(false);
                features.setVisible(false);
                interiorDetailsHeader.setVisible(false);
                exteriorLayoutWithHeader.setVisible(false);
                interiorLayoutWithHeader.setVisible(false);
                exteriorLayoutWithHeader.setVisible(false);
                builtAtComboBox.setVisible(false);

                clearInteriorDetails();
                clearExteriorDetails();
            } else {
                noOfBedrooms.setVisible(true);
                noOfBathrooms.setVisible(true);
                features.setVisible(true);
                interiorDetailsHeader.setVisible(true);
                exteriorLayoutWithHeader.setVisible(true);
                interiorLayoutWithHeader.setVisible(true);
                exteriorLayoutWithHeader.setVisible(true);
                builtAtComboBox.setVisible(true);
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

    private void clearInteriorDetails() {
        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) interiorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                checkboxGroup.clear();
            }
        }
    }

    private void clearExteriorDetails() {
        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) exteriorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                checkboxGroup.clear();
            }
        }
    }


    private void onStateSelected() {
        String selectedState = stateComboBox.getValue();
        if (selectedState != null) {
            cityComboBox.setItems(cityService.getCitiesByState(selectedState).stream().map(City::getName).collect(Collectors.toList()));
            cityComboBox.setEnabled(true);
        } else {
            cityComboBox.clear();
            cityComboBox.setEnabled(false);
        }
    }

    private void onCitySelected() {
        String selectedCity = cityComboBox.getValue();
        if (selectedCity != null) {
            phaseComboBox.setItems(phaseService.getPhasesByCity(selectedCity).stream().map(Phase::getName).collect(Collectors.toList()));
            phaseComboBox.setEnabled(true);
        } else {
            phaseComboBox.clear();
            phaseComboBox.setEnabled(false);
        }
    }

    private void onPhaseSelected(){
        String selectedPhase = phaseComboBox.getValue();
        if (selectedPhase!= null) {
            phaseComboBox.setValue(selectedPhase);
            phaseComboBox.setEnabled(true);
        } else {
            phaseComboBox.clear();
            phaseComboBox.setEnabled(false);
        }
    }

}

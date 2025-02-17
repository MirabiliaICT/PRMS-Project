package ng.org.mirabilia.pms.views.forms.properties;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.*;
import ng.org.mirabilia.pms.domain.enums.*;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.services.implementations.GltfStorageService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditPropertyForm extends Dialog {

    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;

    private final LogService logService;
    private final Property property;
    private final Consumer<Void> onSuccess;

    private final TextField titleField = new TextField("Title");
    private final TextField streetField = new TextField("Street");
    private final ComboBox<String> phaseComboBox = new ComboBox<>("Phase");
    private final ComboBox<String> cityComboBox = new ComboBox<>("City");
    private final ComboBox<String> stateComboBox = new ComboBox<>("State");
    private final ComboBox<PropertyType> propertyTypeComboBox = new ComboBox<>("Property Type", PropertyType.values());
    private final ComboBox<PropertyStatus> propertyStatusComboBox = new ComboBox<>("Property Status", PropertyStatus.values());
    private final ComboBox<InstallmentalPayments> installmentalPaymentComboBox = new ComboBox<>("Installment Plan", InstallmentalPayments.values());
    private final TextArea descriptionField = new TextArea("Description");

    private final NumberField plotField = new NumberField("Plot");
    private final NumberField unitField = new NumberField("Unit");
    private final NumberField sizeField = new NumberField("Size (sq ft)");
    private final NumberField priceField = new NumberField("Price");

    private final NumberField latitudeField = new NumberField("Latitude");
    private final NumberField longitudeField = new NumberField("Longitude");
    private final ComboBox<User> agentComboBox = new ComboBox<>("Agent");
    private final ComboBox<User> clientComboBox = new ComboBox<>("Client");
    private final NumberField noOfBedrooms = new NumberField("No of Bedrooms");
    private final NumberField noOfBathrooms = new NumberField("No of Bathrooms");
    public CheckboxGroup<PropertyFeatures> features = new CheckboxGroup<>("Features");
    private final ComboBox<Integer> builtAtComboBox = new ComboBox<>("Year Built");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private final Upload uploadGltf = new Upload(buffer);
    private byte[] uploadedImage = new byte[0];
    private String uploadedGltfName;
    private final HorizontalLayout gltfModelLayout = new HorizontalLayout();

    private final FlexLayout imageContainer = new FlexLayout();

    private List<byte[]> uploadedImages = new ArrayList<>();

    private final VerticalLayout interiorDetailsLayout = new VerticalLayout();
    private final VerticalLayout exteriorDetailsLayout = new VerticalLayout();

    H6 interiorDetailsHeader = new H6("INTERIOR DETAILS");
    H6 exteriorDetailsHeader = new H6("EXTERIOR DETAILS");
    private final VerticalLayout interiorLayoutWithHeader = new VerticalLayout();
    private final VerticalLayout exteriorLayoutWithHeader = new VerticalLayout();
    private final GltfStorageService gltfStorageService;

    public EditPropertyForm(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService,LogService logService, Property property, GltfStorageService gltfStorageService, Consumer<Void> onSuccess) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.userService = userService;
        this.logService = logService;
        this.property = property;
        this.onSuccess = onSuccess;
        this.gltfStorageService = gltfStorageService;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        setWidth("85%");
        addClassName("custom-property-form");

        configureFormFields();
        createFormLayout();
        populateFields();
        addPropertyTypeListener();
        addPropertyStatusListener();
        configureGltfUpload();


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
        clientComboBox.setRequired(true);
        clientComboBox.setItemLabelGenerator(client -> client.getFirstName() + " " + client.getLastName());
        clientComboBox.addClassName("custom-combo-box");

        descriptionField.setPlaceholder("Enter property description...");
        descriptionField.setMaxLength(1000);
        descriptionField.addClassName("custom-text-area");
        descriptionField.setWidth("50%");
        descriptionField.setHeight("200px");

        plotField.setMin(0);
        plotField.setPlaceholder("Plot no");
        plotField.setRequired(true);
        plotField.addClassName("custom-number-field");

        unitField.setMin(0);
        unitField.setPlaceholder("Unit no");
        unitField.setRequired(true);
        unitField.addClassName("custom-number-field");

        sizeField.setMin(0);
        sizeField.setRequired(true);
        sizeField.setPlaceholder("Square feet");
        sizeField.addClassName("custom-number-field");

        priceField.setMin(0);
        priceField.setRequired(true);
        priceField.setPlaceholder("Price in NGN");
        priceField.addClassName("custom-number-field");

        streetField.addClassName("custom-text-field");
        streetField.setRequired(true);

        titleField.addClassName("custom-text-field");
        titleField.setRequired(true);

        latitudeField.addClassName("custom-text-field");
        latitudeField.setRequired(true);

        longitudeField.addClassName("custom-text-field");
        longitudeField.setRequired(true);

        noOfBedrooms.setMin(0);
        noOfBedrooms.setRequired(true);
        noOfBedrooms.setPlaceholder("No of Bedrooms");
        noOfBedrooms.addClassName("custom-number-field");

        noOfBathrooms.setMin(0);
        noOfBathrooms.setRequired(true);
        noOfBathrooms.setPlaceholder("No of Bathrooms");
        noOfBathrooms.addClassName("custom-number-field");

        features.setItems(PropertyFeatures.values());
        features.setRequired(true);
        features.addClassName("custom-checkbox-group");

        propertyTypeComboBox.setItems(PropertyType.values());
        propertyTypeComboBox.setRequired(true);
        propertyTypeComboBox.setItemLabelGenerator(PropertyType::getDisplayName);

        propertyStatusComboBox.setItems(PropertyStatus.values());
        propertyStatusComboBox.setRequired(true);
        propertyStatusComboBox.setItemLabelGenerator(PropertyStatus::getDisplayName);

        installmentalPaymentComboBox.setItems(InstallmentalPayments.values());
        installmentalPaymentComboBox.setRequired(true);
        installmentalPaymentComboBox.setItemLabelGenerator(InstallmentalPayments::getDisplayName);


        int currentYear = Year.now().getValue();
        List<Integer> years = IntStream.rangeClosed(2000, currentYear)
                .boxed()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());

        builtAtComboBox.setItems(years);
        builtAtComboBox.setRequired(true);
        builtAtComboBox.addClassName("custom-combo-box");

        upload.addSucceededListener(event -> {
            try {
                uploadedImage = buffer.getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            upload.setMaxFiles(10);
            uploadedImages.add(uploadedImage);
            displayImages();
        });

        uploadedImages.sort((a, b) -> uploadedImages.indexOf(b) - uploadedImages.indexOf(a));
        upload.addFileRemovedListener(event -> {
            try {
                uploadedImage = buffer.getInputStream().readAllBytes();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < uploadedImages.size(); i++){
                byte[] imageData = uploadedImages.get(i);
                uploadedImages.remove(imageData);
            }
            uploadedImages.remove(uploadedImage);
            displayImages();
        });

//        upload.setWidthFull();
        upload.getStyle().setTextAlign(Style.TextAlign.CENTER);
        upload.setMaxFiles(10);
        // Configure image container layout
        imageContainer.getStyle().setFlexWrap(Style.FlexWrap.WRAP);
        imageContainer.setWidthFull();
        imageContainer.setClassName("image-container");


        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>(detail.name());
            checkboxGroup.setItems(detail.getItems());
            checkboxGroup.setLabel(detail.name());
            interiorDetailsLayout.add(checkboxGroup);
        }

        for (ExteriorDetails detail : ExteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>(detail.name());
            checkboxGroup.setItems(detail.getItems());
            checkboxGroup.setLabel(detail.name());
            exteriorDetailsLayout.add(checkboxGroup);
        }
    }

    private void displayImages() {
        imageContainer.removeAll(); // Clear current images
        uploadedImages.sort((a, b) -> uploadedImages.indexOf(b) - uploadedImages.indexOf(a));


        for (int i = 0; i < uploadedImages.size(); i++) {
            byte[] imageData = uploadedImages.get(i);
            Image image = new Image();
            image.setSrc(new StreamResource("uploaded-image", () -> new ByteArrayInputStream(imageData)));
            image.setHeight("12rem");
            image.setWidth("300px");
            image.getStyle().set("object-fit", "contain");
            image.getStyle().setBorderRadius("15px");


            Button deleteButton = new Button(new Icon("lumo", "cross"));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> {
                uploadedImages.remove(imageData);
                displayImages();
            });
            deleteButton.getStyle().setPosition(Style.Position.RELATIVE);
            deleteButton.getStyle().setPosition(Style.Position.RELATIVE);
            deleteButton.getStyle().setBottom("40px");
            deleteButton.getStyle().setRight("70px");
            deleteButton.getStyle().setBackground("grey");

            HorizontalLayout imageLayout = new HorizontalLayout(image, deleteButton);
            imageLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            imageContainer.add(imageLayout);
        }
    }

    private void createFormLayout() {
        H2 headerTitle = new H2("Edit Property");
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

        FormLayout propertiesDetails = new FormLayout(titleField,  propertyTypeComboBox, latitudeField,
                longitudeField, propertyStatusComboBox, installmentalPaymentComboBox,  agentComboBox, clientComboBox,
                plotField, unitField, sizeField, priceField, noOfBathrooms, noOfBedrooms, builtAtComboBox, features);
        propertiesDetails.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));


        Button saveButton = new Button("Save",
                e ->{
                    if(saveProperty()){
                        String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                        Log log = new Log();
                        log.setAction(Action.EDIT);
                        log.setModuleOfAction(Module.PROPERTIES);
                        log.setInitiator(loggedInInitiator);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        log.setTimestamp(timestamp);
                        Application.logService.addLog(log);
                    }
                }
        );
        Button deleteButton = new Button("Delete", e -> confirmationDialog());
        Button discardButton = new Button("Discard", e -> close());
        HorizontalLayout btn = new HorizontalLayout(saveButton, discardButton);


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

        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, btn);
        buttonLayout.setWidthFull();
        buttonLayout.addClassName("custom-button-layout");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout uploadLayout = new VerticalLayout(new H6("Image upload"), upload, imageContainer);
        VerticalLayout uploadGltfLayout = new VerticalLayout(new H6("3D Model upload"), uploadGltf, gltfModelLayout);




        interiorLayoutWithHeader.add(interiorDetailsHeader, interiorDetailsLayout);
        exteriorLayoutWithHeader.add(exteriorDetailsHeader, exteriorDetailsLayout);

        HorizontalLayout interiorEtExterior = new HorizontalLayout( interiorLayoutWithHeader, exteriorLayoutWithHeader);


        VerticalLayout contentLayout = new VerticalLayout(header, location, formLayout, propertyDetails, propertiesDetails, interiorEtExterior, descriptionField, uploadLayout, uploadGltfLayout, buttonLayout);
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.addClassName("custom-content-layout");
        add(contentLayout);
    }

    private void populateFields() {
        titleField.setValue(property.getTitle());
        streetField.setValue(property.getStreet());
        stateComboBox.setValue(property.getPhase().getCity().getState().getName());
        cityComboBox.setValue(property.getPhase().getCity().getName());
        phaseComboBox.setValue(property.getPhase() != null ? property.getPhase().getName() : null);
        propertyTypeComboBox.setValue(property.getPropertyType());
        propertyStatusComboBox.setValue(property.getPropertyStatus());
        descriptionField.setValue(property.getDescription() != null ? property.getDescription() : "");
        plotField.setValue(property.getPlot().doubleValue());
        unitField.setValue(property.getUnit().doubleValue() != 0 ? property.getUnit().doubleValue() : 0);
        sizeField.setValue(property.getSize());
        priceField.setValue(property.getPrice() != null ? property.getPrice().doubleValue() : 0);
        latitudeField.setValue(property.getLatitude());
        longitudeField.setValue(property.getLongitude());
        noOfBedrooms.setValue(property.getNoOfBedrooms() != 0 ? property.getNoOfBedrooms() : 0);
        noOfBathrooms.setValue(property.getNoOfBathrooms() != 0 ? property.getNoOfBathrooms() : 0);
        builtAtComboBox.setValue(property.getBuiltAt()!= null? property.getBuiltAt() : null);
        agentComboBox.setValue(property.getAgentId() != null ? userService.getUserById(property.getAgentId()).orElse(null) : null);
        clientComboBox.setValue(property.getClientId() != null ? userService.getUserById(property.getClientId()).orElse(null) : null);
        installmentalPaymentComboBox.setValue(property.getInstallmentalPayments());

        features.setValue(property.getFeatures() != null ? new HashSet<>(property.getFeatures()) : Set.of());

        if (property.getPropertyStatus() != null && property.getPropertyStatus().equals(PropertyStatus.AVAILABLE)) {
            clientComboBox.setVisible(false);
            agentComboBox.setVisible(false);
            installmentalPaymentComboBox.setVisible(false);
        } else {
            clientComboBox.setVisible(true);
            agentComboBox.setVisible(true);
            installmentalPaymentComboBox.setVisible(true);
        }

        installmentalPaymentComboBox.setVisible(property.getPropertyStatus() != null && property.getPropertyStatus().equals(PropertyStatus.UNDER_OFFER));


        if (property.getPropertyType() != null && property.getPropertyType().equals(PropertyType.LAND)){
            noOfBathrooms.setVisible(false);
            noOfBedrooms.setVisible(false);
            interiorLayoutWithHeader.setVisible(false);
            exteriorLayoutWithHeader.setVisible(false);
            features.setVisible(false);
            unitField.setVisible(false);
            builtAtComboBox.setVisible(false);
        } else {
            noOfBathrooms.setVisible(true);
            noOfBedrooms.setVisible(true);
            interiorLayoutWithHeader.setVisible(true);
            exteriorLayoutWithHeader.setVisible(true);
            features.setVisible(true);
            unitField.setVisible(true);
        }

        uploadedImages = property.getPropertyImages().stream()
                .map(PropertyImage::getPropertyImages)
                .collect(Collectors.toList());

        displayImages();



        // Populate interior details checkboxes
        for (InteriorDetails detail : InteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) interiorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                Set<String> selectedItems = getInteriorFeatureValues(detail); // Method to fetch stored values for the feature
                checkboxGroup.setValue(selectedItems);
            }
        }

        // Populate exterior details checkboxes
        for (ExteriorDetails detail : ExteriorDetails.values()) {
            CheckboxGroup<String> checkboxGroup = (CheckboxGroup<String>) exteriorDetailsLayout
                    .getChildren()
                    .filter(component -> component instanceof CheckboxGroup)
                    .filter(component -> ((CheckboxGroup<String>) component).getLabel().equals(detail.name()))
                    .findFirst()
                    .orElse(null);

            if (checkboxGroup != null) {
                Set<String> selectedItems = getExteriorFeatureValues(detail);
                checkboxGroup.setValue(selectedItems);
            }
        }
    }

    private Set<String> getInteriorFeatureValues(InteriorDetails detail) {
        switch (detail) {
            case Laundry:
                return new HashSet<>(property.getLaundryItems());
            case Kitchen:
                return new HashSet<>(property.getKitchenItems());
            case Flooring:
                return new HashSet<>(property.getInteriorFlooringItems());
            default:
                return new HashSet<>();
        }
    }

    private Set<String> getExteriorFeatureValues(ExteriorDetails detail) {
        switch (detail) {
            case Security:
                return new HashSet<>(property.getSecurityItems());
            case Flooring:
                return new HashSet<>(property.getExteriorFlooringItems());
            default:
                return new HashSet<>();
        }
    }

    private boolean saveProperty() {
        if (property.getPropertyStatus() == PropertyStatus.UNDER_OFFER || property.getPropertyStatus() == PropertyStatus.SOLD){
            Notification.show("Property status cannot be updated, this property is already either sold or under offer", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }


        if (streetField.getValue() == null || streetField.getValue().isEmpty() || titleField.getValue() == null
                || titleField.getValue().isEmpty() ||
                phaseComboBox.getValue() == null || stateComboBox.getValue() == null || stateComboBox.isEmpty() ||
                cityComboBox.getValue() == null || cityComboBox.isEmpty() ||
                propertyTypeComboBox.getValue() == null ||
                propertyStatusComboBox.getValue() == null ||
                priceField.getValue() == null || priceField.getValue() <= 0 ||
                sizeField.getValue() == null || sizeField.getValue() <= 0 ||
                latitudeField == null || longitudeField == null) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        } else if (uploadedImages.isEmpty()){
            Notification.show("Please upload at least one property image", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }


        if (!propertyStatusComboBox.getValue().equals(PropertyStatus.AVAILABLE)) {
            if (agentComboBox.getValue() == null) {
                Notification.show("Please select an agent", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }

            try {
                String selectedAgentName = agentComboBox.getValue().getFirstName() + " " + agentComboBox.getValue().getLastName();
                Long agentId = userService.getAgentIdByName(selectedAgentName);
                property.setAgentId(agentId);
            } catch (IllegalArgumentException ex) {
                Notification.show("Agent not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }

            if (clientComboBox.getValue() != null) {
                try {
                    String selectedClientName = clientComboBox.getValue().getFirstName() + " " + clientComboBox.getValue().getLastName();
                    Long clientId = userService.getClientIdByName(selectedClientName);
                    property.setClientId(clientId);
                } catch (IllegalArgumentException ex) {
                    Notification.show("Client not found: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return false;
                }
            }
        }else{
            property.setAgentId(null);
            property.setClientId(null);
        }

        if (propertyStatusComboBox.getValue().equals(PropertyStatus.UNDER_OFFER)){
            property.setInstallmentalPayments(installmentalPaymentComboBox.getValue());
        }


        List<PropertyImage> propertyImages = uploadedImages.stream()
                .map(imageData -> {
                    PropertyImage propertyImage = new PropertyImage();
                    propertyImage.setPropertyImages(imageData);
                    propertyImage.setProperty(property);
                    return propertyImage;
                })
                .collect(Collectors.toList());
        property.setPropertyImages(propertyImages);

        property.setTitle(titleField.getValue());
        property.setStreet(streetField.getValue());
        property.setPhase(phaseService.getPhaseByName(phaseComboBox.getValue()));
        property.setFeatures(features.getValue());
        property.setPropertyType(propertyTypeComboBox.getValue());
        property.setPropertyStatus(propertyStatusComboBox.getValue());
        property.setDescription(descriptionField.getValue());
        property.setSize(sizeField.getValue());
        property.setPrice(BigDecimal.valueOf(priceField.getValue()));
        property.setLatitude(latitudeField.getValue());
        property.setLongitude(longitudeField.getValue());
        property.setUnit(unitField.getValue().intValue());
        property.setNoOfBedrooms(noOfBedrooms.getValue());
        property.setNoOfBathrooms(noOfBathrooms.getValue());
        property.setBuiltAt(builtAtComboBox.getValue());
        property.setPlot(plotField.getValue().intValue());
        property.setPropertyCode(generatePropertyCode());




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
                        property.setLaundryItems(selectedItems);
                        break;
                    case Kitchen:
                        property.setKitchenItems(selectedItems);
                        break;
                    case Flooring:
                        property.setInteriorFlooringItems(selectedItems);
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
                        property.setSecurityItems(selectedItems);
                        break;
                    case Flooring:
                        property.setExteriorFlooringItems(selectedItems);
                        break;
                }
            }
        }

        propertyService.saveProperty(property);

        Notification.show("Property updated successfully", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        //Log
        String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
        Log log = new Log();
        log.setAction(Action.EDIT);
        log.setModuleOfAction(Module.PROPERTIES);
        log.setInitiator(loggedInInitiator);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        log.setTimestamp(timestamp);
        logService.addLog(log);

        close();
        onSuccess.accept(null);
        return true;
    }

    private void confirmationDialog() {
        Dialog confirmationDialog = new Dialog();

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        H6 message = new H6("Are you sure you want to delete this property?");
        content.add(message);

        Button confirmButton = new Button("Yes", event -> {
            confirmationDialog.close();
            if(deleteProperty()){
                String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.DELETE);
                log.setModuleOfAction(Module.PROPERTIES);
                log.setInitiator(loggedInInitiator);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                Application.logService.addLog(log);
            };
        });

        Button cancelButton = new Button("Cancel", event -> {
            confirmationDialog.close();
        });
        confirmButton.getStyle().setColor("red");
        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonsLayout.getStyle().setJustifyContent(Style.JustifyContent.END);
        buttonsLayout.setWidthFull();
        content.add(buttonsLayout);

        confirmationDialog.add(content);
        confirmationDialog.open();
    }



    private boolean deleteProperty() {
        try {
            propertyService.deleteProperty(property.getId());
            this.close();
            UI.getCurrent().getPage().getHistory().back();
            onSuccess.accept(null);
            return true;
        } catch (Exception ex) {
            Notification.show("Unable to delete property: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
    }

    private void addPropertyTypeListener() {
        propertyTypeComboBox.addValueChangeListener(event -> {
            PropertyType selectedType = event.getValue();
            if (selectedType != null && selectedType.equals(PropertyType.LAND)) {
                noOfBedrooms.setVisible(false);
                noOfBathrooms.setVisible(false);
                features.setVisible(false);
                interiorDetailsLayout.setVisible(false);
                exteriorDetailsLayout.setVisible(false);
                interiorLayoutWithHeader.setVisible(false);
                exteriorLayoutWithHeader.setVisible(false);
                interiorDetailsHeader.setVisible(false);
                exteriorDetailsHeader.setVisible(false);
                builtAtComboBox.setVisible(false);


                clearInteriorDetails();
                clearExteriorDetails();
            } else {
                noOfBedrooms.setVisible(true);
                noOfBathrooms.setVisible(true);
                features.setVisible(true);
                interiorDetailsLayout.setVisible(true);
                exteriorDetailsLayout.setVisible(true);
                interiorLayoutWithHeader.setVisible(true);
                exteriorLayoutWithHeader.setVisible(true);
                interiorDetailsHeader.setVisible(true);
                exteriorDetailsHeader.setVisible(true);
                builtAtComboBox.setVisible(true);
                unitField.setVisible(true);
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

            if (selectedStatus!= null && selectedStatus.equals(PropertyStatus.UNDER_OFFER)) {
                installmentalPaymentComboBox.setVisible(true);
            } else {
                installmentalPaymentComboBox.setVisible(false);
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

    private void configureGltfUpload() {
        uploadGltf.setMaxFiles(1);
        uploadGltf.setAcceptedFileTypes("model/gltf+json", "model/gltf-binary", ".gltf", ".glb");
        uploadGltf.addSucceededListener(event -> {
            uploadedGltfName = event.getFileName();
            try {
                GltfModel existingModel = property.getModel();
                if(existingModel != null){
                    property.setModel(null);
                    existingModel.setProperty(null);
                    gltfStorageService.deleteExistingModel(existingModel);
                }

                byte[] gltfData = buffer.getInputStream().readAllBytes();


                GltfModel model = new GltfModel();
                model.setData(gltfData);
                model.setName(event.getFileName());

                model.setProperty(property);
                property.setModel(model);

                Notification.show("3D Model uploaded successfully", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                System.out.println(" Model uploadesd" + property.getModel().getProperty().getId());
            } catch (IOException e) {
                Notification.show("Failed to upload GLTF model", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    public String generatePropertyCode() {
        String title = titleField.getValue();
        String phaseLocation = phaseComboBox.getValue();
        Integer plotNumber = property.getPlot();
        Integer unitNumber = property.getUnit();

        String titlePrefix = title != null && title.length() >= 2 ? title.substring(0, 2).toUpperCase() : "";

        String phasePrefix = phaseLocation != null && phaseLocation.length() >= 2 ? phaseLocation.substring(0, 2).toUpperCase() : "";

        if (propertyTypeComboBox.getValue() == PropertyType.LAND) {
            return titlePrefix + plotNumber + phasePrefix;
        } else {
            return titlePrefix + plotNumber + phasePrefix + unitNumber;
        }


    }


}

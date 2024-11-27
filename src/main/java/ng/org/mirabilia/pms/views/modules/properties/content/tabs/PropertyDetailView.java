package ng.org.mirabilia.pms.views.modules.properties.content.tabs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyDocument;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.UnauthorizedView;
import ng.org.mirabilia.pms.views.forms.properties.EditPropertyForm;
import ng.org.mirabilia.pms.views.modules.properties.content.tabs.modelView.GltfViewer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.Set;

@Route(value = "property-detail/:propertyId", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "AGENT", "CLIENT"})
public class PropertyDetailView extends VerticalLayout implements BeforeEnterObserver {
    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final UserService userService;

    private final VerticalLayout interiorDetailsLayout = new VerticalLayout();
    private final VerticalLayout exteriorDetailsLayout = new VerticalLayout();

    private final H6 interiorDetailsHeader = new H6("INTERIOR DETAILS");
    private final H6 exteriorDetailsHeader = new H6("EXTERIOR DETAILS");

    private Div featureDiv = new Div();

    private Div dateBuilt = new Div();

    private Image mainImage = new Image();

    public PropertyDetailView(PropertyService propertyService, PhaseService phaseService, CityService cityService, StateService stateService, UserService userService) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.userService = userService;

        getStyle().setPadding("0");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String propertyIdString = event.getRouteParameters().get("propertyId").orElse("");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            Notification.show("Unauthorized access", 3000, Notification.Position.MIDDLE);
            event.forwardTo("");
            return;
        }

        try {
            Long propertyId = Long.valueOf(propertyIdString);
            Optional<Property> propertyOpt = propertyService.getPropertyById(propertyId);

            propertyOpt.ifPresentOrElse(property -> {
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                Long loggedInUserId = userService.getUserIdByUsername(authentication.getName());

//                User user = userService.findByUsername(Application.globalLoggedInUsername);

                if (property.getClientId() == null ||property.getAgentId()==null){
                    UI.getCurrent().navigate(UnauthorizedView.class);
                    return;
                }

                if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
                    setProperty(property);
                } else if (property.getAgentId().equals(loggedInUserId) || property.getClientId().equals(loggedInUserId)) {
                    setProperty(property);
                } else {
                    Notification.show("You are not authorized to view this property", 9000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }, () -> {
                Notification.show("Property not found", 3000, Notification.Position.MIDDLE);
                event.forwardTo("");
            });

        } catch (NumberFormatException e) {
            Notification.show("Invalid property ID", 3000, Notification.Position.MIDDLE);
            event.forwardTo("");
        }
    }


    public void setProperty(Property property) {
        removeAll();

        Text back = new Text("Back");
        Div arrowLeft = new Div(new Icon(VaadinIcon.ARROW_LEFT), back);
        arrowLeft.addClassName("back-div");
        add(arrowLeft);

        arrowLeft.addClickListener(e -> close());

        if (property.getPropertyImages() != null && !property.getPropertyImages().isEmpty()) {
            PropertyImage firstImage = property.getPropertyImages().get(0);
            mainImage = new Image(createImageResource(firstImage), "Property Image");
            mainImage.setWidthFull();
            mainImage.setHeight("60vh");
            add(mainImage);

            // Thumbnail image layout
            HorizontalLayout thumbnailsLayout = new HorizontalLayout();
            for (PropertyImage propertyImage : property.getPropertyImages()) {
                Image thumbnail = new Image(createImageResource(propertyImage), "Thumbnail");
                thumbnail.setWidth("100px");
                thumbnail.setHeight("80px");
                thumbnail.getStyle().set("cursor", "pointer");


                thumbnail.addClickListener(event -> mainImage.setSrc(createImageResource(propertyImage)));

                thumbnailsLayout.getStyle().setMargin("auto");
                thumbnailsLayout.add(thumbnail);
            }

            add(thumbnailsLayout);
        } else {
            Notification.show("No images available for this property", 3000, Notification.Position.MIDDLE);
        }

        // Property details
        Div status;
        Span circle = new Span();
        circle.getStyle().setBorderRadius("100%");
        circle.setWidth("10px");
        circle.setHeight("10px");
        if (property.getPropertyStatus().name().equals("AVAILABLE")){
            status = new Div("For sale");
            circle.getStyle().setBackgroundColor("green");
        } else if(property.getPropertyStatus().name().equals("SOLD")) {
            status = new Div("Not Available");
            circle.getStyle().setBackgroundColor("red");
        } else {
            status =new Div("Under Offer");
            circle.getStyle().setBackgroundColor("yellow");
        }
        status.getStyle().setFontSize("12px");
        status.getStyle().setPaddingTop("3px");

        HorizontalLayout btnLike = new HorizontalLayout();
        btnLike.add(circle, status);
        btnLike.setAlignItems(Alignment.CENTER);
        btnLike.getStyle().setBackground("#D9D9D9");
        btnLike.getStyle().setPaddingTop("5px");
        btnLike.getStyle().setPaddingBottom("5px");
        btnLike.getStyle().setPaddingLeft("10px");
        btnLike.getStyle().setPaddingRight("10px");
        btnLike.getStyle().setBorderRadius("5px");
        btnLike.getStyle().set("gap","5px");

        H4 price = new H4("₦" + NumberFormat.getInstance().format(property.getPrice()));
        price.getStyle().setFontSize("18px");

        Div type = new Div(property.getPropertyType().name().replace("_", " "));
        type.getStyle().setMarginTop("10px");

        HorizontalLayout priceStatus = new HorizontalLayout();
        priceStatus.add(price, btnLike);
        priceStatus.setAlignItems(Alignment.CENTER);

        //Location
        Span location = new Span( property.getStreet() + ", "
                + property.getPhase().getName() + ", "
                + property.getPhase().getCity().getName() + ", "
                + property.getPhase().getCity().getState().getName());
        Icon mapIcon = new Icon(VaadinIcon.MAP_MARKER );
        mapIcon.getStyle().setColor("red");
        HorizontalLayout locationMap = new HorizontalLayout(mapIcon, location);


        //Edit Button
        Button editButton = new Button("Edit", e -> openEditPropertyDialog(property));
        editButton.getStyle().setBackground("#1434A4");
        editButton.getStyle().setColor("#FFFFFF");

        //Inspect Button
        Button inspectBtn = new Button("Inspect", e -> openInspectPropertyDialog(property));
        inspectBtn.getStyle().setColor("#1434A4");
        inspectBtn.getStyle().setBorder("#1434A4");

        // Add Download Button in setProperty Method
        Button downloadButton = new Button("Download Docs");
        downloadButton.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        downloadButton.addClickListener(event -> initiateDownload(property));

        //Inspect and edit Button Horizontal
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(inspectBtn, editButton, downloadButton);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            editButton.setVisible(true);

        } else {
            editButton.setVisible(false);
            downloadButton.getStyle().setBackground("#1434A4");
            downloadButton.getStyle().setColor("#FFFFFF");
        }


        //Features
        String featuresString = property.getFeatures().toString().replace("[", "").replace("]", "").trim();
        String[] featuresArray = featuresString.split(", ");

        HorizontalLayout featuresLayout = new HorizontalLayout();
        Div squareFeet = new Div(String.valueOf(property.getSize()) + "sqft");
        squareFeet.addClassName("features");

        dateBuilt = new Div("Built in " + property.getBuiltAt().toString());
        dateBuilt.addClassName("features");
        featuresLayout.add(squareFeet, dateBuilt);

        if(!property.getFeatures().isEmpty()){
            for (String feature : featuresArray) {
                featureDiv = new Div(feature.trim());
                featureDiv.addClassName("features");
//            switch (feature.trim().toLowerCase()) {
//                case "garage":
//
//                    break;
//                case "garden":
//                    break;
//                case "swimmingpool":
//                    break;
//                default:
//                    break;
//            }
                featuresLayout.add(featureDiv);
                featuresLayout.addClassName("features-layout");
            }
        } else {
            featureDiv.setVisible(false);
        }

        if(property.getFeatures().isEmpty()){
            featureDiv.setVisible(false);
        }

        HorizontalLayout propertyDetails = new HorizontalLayout();
        Div priceStatusType = new Div(priceStatus, type, locationMap, featuresLayout);
        propertyDetails.add(priceStatusType, horizontalLayout);
        propertyDetails.getStyle().setPaddingLeft("80px");
        propertyDetails.getStyle().setPaddingRight("80px");
        propertyDetails.setWidthFull();
        propertyDetails.setJustifyContentMode(JustifyContentMode.BETWEEN);
        priceStatusType.setWidth("50%");


        displayPropertyDetails(property);

        interiorDetailsLayout.getStyle().setPaddingLeft("0");
        exteriorDetailsLayout.getStyle().setPaddingLeft("0");


        VerticalLayout interiorLayoutWithHeader = new VerticalLayout(interiorDetailsHeader, interiorDetailsLayout);
        VerticalLayout exteriorLayoutWithHeader = new VerticalLayout(exteriorDetailsHeader, exteriorDetailsLayout);
        interiorLayoutWithHeader.getStyle().set("gap", "0");
        exteriorLayoutWithHeader.getStyle().set("gap", "0");


        HorizontalLayout interiorEtExterior = new HorizontalLayout( interiorLayoutWithHeader, exteriorLayoutWithHeader);
        interiorEtExterior.setWidthFull();

        add(propertyDetails, interiorEtExterior);

    }

    private void openInspectPropertyDialog(Property property) {
        if (property.getModel() != null && property.getModel().getData() != null) {
            byte[] gltfData = property.getModel().getData();
            System.out.println("GLTF Data Length: " + gltfData.length);

            Dialog dialog = new Dialog();
            dialog.setWidth("80vw");
            dialog.setHeight("80vh");

            GltfViewer viewer = new GltfViewer(gltfData);
            viewer.getElement().getStyle().set("width", "100%").set("height", "100%");
            viewer.addClassName("full-size-canvas");
            dialog.add(viewer);

            H3 closebtn = new H3("X");
            closebtn.getStyle().setColor("white");
            Button closeButton = new Button(closebtn, event -> dialog.close());
            closeButton.getStyle().set("position", "absolute");
            closeButton.getStyle().set("top", "30px");
            closeButton.getStyle().set("right", "50px");
            dialog.add(closeButton);

            dialog.open();
        } else {
            Notification.show("No 3D model available for this property", 3000, Notification.Position.MIDDLE);
            System.out.println("GLTF Model or data is null");
        }
    }


    private void openEditPropertyDialog(Property property) {
        EditPropertyForm editPropertyForm = new EditPropertyForm(
                propertyService,
                phaseService,
                cityService,
                stateService,
                userService,
                property,
                event -> {
                    Optional<Property> updatedPropertyOpt = propertyService.getPropertyById(property.getId());
                    updatedPropertyOpt.ifPresentOrElse(
                            this::setProperty,
                            () -> Notification.show("Property not found", 3000, Notification.Position.MIDDLE)
                    );
                }
        );
        editPropertyForm.open();
    }

    private StreamResource createImageResource(PropertyImage propertyImage) {
        byte[] imageBytes = propertyImage.getPropertyImages();
        return new StreamResource("property-image-" + propertyImage.getId(), () -> new ByteArrayInputStream(imageBytes));
    }

    public void close(){
        UI.getCurrent().getPage().getHistory().back();
    }

    private void displayPropertyDetails(Property property) {
        if (!PropertyType.LAND.equals(property.getPropertyType())) {
            populateInteriorDetails(property);
            populateExteriorDetails(property);
            interiorDetailsHeader.setVisible(true);
            exteriorDetailsHeader.setVisible(true);
            if(property.getFeatures().isEmpty()){
                featureDiv.setVisible(false);
            }
            featureDiv.setVisible(true);
            dateBuilt.setVisible(true);

        } else {
            interiorDetailsLayout.removeAll();
            exteriorDetailsLayout.removeAll();
            interiorDetailsHeader.setVisible(false);
            exteriorDetailsHeader.setVisible(false);
            featureDiv.setVisible(false);
            dateBuilt.setVisible(false);
        }
    }

    private void populateInteriorDetails(Property property) {
        interiorDetailsLayout.removeAll();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.getStyle().set("background", "#F1F1F1");
        horizontalLayout.getStyle().set("border", "1px solid #F1F1F1");
        horizontalLayout.getStyle().set("borderRadius", "5px");
        horizontalLayout.setWidthFull();

        addCategoryLayout(horizontalLayout, "Interior Flooring", property.getInteriorFlooringItems());


        addCategoryLayout(horizontalLayout, "Kitchen Items", property.getKitchenItems());
        addCategoryLayout(horizontalLayout, "Laundry Items", property.getLaundryItems());

        interiorDetailsLayout.add(horizontalLayout);
    }

    private void populateExteriorDetails(Property property) {
        exteriorDetailsLayout.removeAll();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.getStyle().set("background", "#F1F1F1");
        horizontalLayout.getStyle().set("border", "1px solid #F1F1F1");
        horizontalLayout.getStyle().set("borderRadius", "5px");
        horizontalLayout.setWidthFull();

        // Add exterior flooring items
        addCategoryLayout(horizontalLayout, "Exterior Flooring", property.getExteriorFlooringItems());

        // Add security items
        addCategoryLayout(horizontalLayout, "Security Items", property.getSecurityItems());

        exteriorDetailsLayout.add(horizontalLayout);
    }

    private void addCategoryLayout(HorizontalLayout layout, String categoryName, Set<String> items) {
        VerticalLayout categoryLayout = new VerticalLayout();

        // Header
        H6 categoryHeader = new H6(categoryName);
        categoryLayout.add(categoryHeader);

        // Items list
        UnorderedList itemList = new UnorderedList();
        itemList.getStyle().set("paddingLeft", "12px");
        itemList.getStyle().set("margin", "0");

        for (String item : items) {
            itemList.add(new ListItem(item));
        }

        categoryLayout.add(categoryHeader, itemList);
        layout.add(categoryLayout);
    }

    private void initiateDownload(Property property) {
        PropertyDocument propertyDocument = property.getDocuments().get(0);
        byte[] documentData = propertyDocument.getFileData();

        if (documentData != null && documentData.length > 0) {
            StreamResource resource = new StreamResource("document.docx", () -> new ByteArrayInputStream(documentData));

            Anchor downloadLink = new Anchor(resource, "Download");
            downloadLink.getElement().setAttribute("download", true);

            UI.getCurrent().add(downloadLink);
            downloadLink.getElement().callJsFunction("click");
        } else {
            Notification.show("No document available for download", 3000, Notification.Position.MIDDLE);
        }
    }
}
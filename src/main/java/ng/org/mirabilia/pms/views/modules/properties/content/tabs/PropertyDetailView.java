package ng.org.mirabilia.pms.views.modules.properties.content.tabs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.properties.EditPropertyForm;
import ng.org.mirabilia.pms.views.modules.properties.PropertiesView;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Route("property-detail/:propertyId")
@RolesAllowed({"ADMIN", "MANAGER", "AGENT", "CLIENT"})
public class PropertyDetailView extends VerticalLayout implements BeforeEnterObserver {
    private final PropertyService propertyService;
    private final PhaseService phaseService;
    private final UserService userService;

    private Image mainImage;

    public PropertyDetailView(PropertyService propertyService, PhaseService phaseService, UserService userService) {
        this.propertyService = propertyService;
        this.phaseService = phaseService;
        this.userService = userService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        String propertyIdString = event.getRouteParameters().get("propertyId").orElse("");
        try {
            Long propertyId = Long.valueOf(propertyIdString);
            Optional<Property> propertyOpt = propertyService.getPropertyById(propertyId);
            propertyOpt.ifPresentOrElse(this::setProperty, () -> Notification.show("Property not found", 3000, Notification.Position.MIDDLE));
        } catch (NumberFormatException e) {
            Notification.show("Invalid property ID", 3000, Notification.Position.MIDDLE);
        }
    }

    public void setProperty(Property property) {
        removeAll();

        Text back = new Text("Back");
        Div div = new Div(new Icon(VaadinIcon.ARROW_LEFT), back);
        div.getStyle().setPosition(Style.Position.ABSOLUTE);
        div.getStyle().setTop("45px");
        div.getStyle().setLeft("50px");
        div.setWidth("60px");
        div.getStyle().setDisplay(Style.Display.FLEX);
        div.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        add(div);

        div.addClickListener(e -> close());


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
        H4 type = new H4("Type: " + property.getPropertyType().name().replace("_", " "));
        H4 status = new H4("Status: " + property.getPropertyStatus().name().replace("_", " "));
        H4 price = new H4("Price: ₦" + property.getPrice());

        add(type, status, price);

        // Edit button
        Button editButton = new Button("Edit", e -> openEditPropertyDialog(property));
        add(editButton);
    }

    private void openEditPropertyDialog(Property property) {
        EditPropertyForm editPropertyForm = new EditPropertyForm(
                propertyService,
                phaseService,
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
        UI.getCurrent().navigate(PropertiesView.class);
    }
}

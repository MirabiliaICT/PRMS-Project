package ng.org.mirabilia.pms.views.modules.profile.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.settings.EditProfileForm;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ProfileContent extends VerticalLayout {

    private final UserService userService;
    private final AuthenticationContext authContext;

    private final UserImageService userImageService;
     private final PropertyService propertyService;

    public ProfileContent(UserService userService, AuthenticationContext authContext, UserImageService userImageService, PropertyService propertyService) {
        this.userService = userService;
        this.authContext = authContext;
        this.userImageService = userImageService;
        this.propertyService = propertyService;

        setSpacing(true);
        setPadding(true);
        setWidthFull();
        addClassName("module-content");

        H3 profileTitle = new H3("Profile Information");
        H3 propertyListingTitle = new H3("Property Listings");
        H3 paymentHistoryTitle = new H3("Payment History");
        profileTitle.addClassName("profile-header");

        Div userProfileCard = getUserProfileCard();
        Div paymentHistoryCard = getPaymentHistoryCard();

        Div formCard = new Div();
        formCard.getStyle().setPadding("8px");
        formCard.getStyle().setBorderRadius("8px");
        formCard.getStyle().setBackgroundColor("white");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        populateUserProfile(formLayout);

        Button updateButton = new Button("Update Information");
        updateButton.addClassName("custom-button");
        updateButton.addClassName("custom-update-button");
        updateButton.addClickListener(e -> openEditProfileDialog());
        formCard.add(formLayout, updateButton);

        Div propertyListing = getUserPropertyList();

        add(userProfileCard, profileTitle, formCard, propertyListingTitle, propertyListing, paymentHistoryTitle, paymentHistoryCard);

        applyCustomStyling();
    }

    private Div getPaymentHistoryCard() {
        Div parent  = new Div();
        parent.setWidthFull();
        parent.setHeight("250px");
        parent.getStyle().setBackgroundColor("white");
        parent.getStyle().setBorderRadius("8px");
        return parent;
    }

    private Div getUserProfileCard() {
        Div d1 = new Div();

        Optional<UserDetails> option = authContext.getAuthenticatedUser(UserDetails.class);
        option.ifPresent((userDetails)->{
            User user = userService.findByUsername(userDetails.getUsername());
            String loggedInUsername = user.getUsername();

            byte []  userImageBytes = null;
            UserImage userImage = userImageService.getUserImageByNameAndUser("ProfileImage", user);
            if(userImage != null){
                userImageBytes = userImage.getUserImage();
            }

            d1.getStyle().setDisplay(Style.Display.FLEX);
            d1.getStyle().setAlignItems(Style.AlignItems.CENTER);
            d1.getStyle().setBackgroundColor("white");
            d1.getStyle().setPadding("15px");
            d1.getStyle().setBorderRadius("10px");
            d1.setWidthFull();
            d1.setHeight("125px");

            Div d2 = new Div();
            d2.getStyle().setDisplay(Style.Display.BLOCK);

            Image profileImg = new Image();
            profileImg.setWidth("80px");
            profileImg.setHeight("80px");
            profileImg.getStyle().setBorderRadius("80px");
            profileImg.getStyle().setMarginRight("8px");

            if(userImageBytes == null){
                profileImg.setSrc("/images/john.png");
            }else {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userImageBytes);
                StreamResource resource = new StreamResource("",()->byteArrayInputStream);
                profileImg.setSrc(resource);
            }



            Paragraph username = new Paragraph(loggedInUsername);
            Paragraph role = new Paragraph(user.getRoles().toArray()[0].toString());
            Paragraph location = new Paragraph("Area 11, Garki");

            d2.add(username,role, location);
            d1.add(profileImg,d2);
        });
        return d1;
    }

    private void populateUserProfile(FormLayout formLayout) {
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent(userDetails -> {
            User loggedInUser = userService.findByUsername(userDetails.getUsername());
            if (loggedInUser != null) {
                String fullName = loggedInUser.getFirstName() + " " + loggedInUser.getMiddleName() + " " + loggedInUser.getLastName();

                String fullAddress = loggedInUser.getHouseNumber() + " " + loggedInUser.getStreet() + ", " +
                        loggedInUser.getCity() + ", " + loggedInUser.getState() + ", " + loggedInUser.getPostalCode();

                String roles = loggedInUser.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));

                TextField nameField = new TextField("Name");
                nameField.setValue(fullName);
                nameField.setReadOnly(true);

                TextField emailField = new TextField("Email");
                emailField.setValue(loggedInUser.getEmail());
                emailField.setReadOnly(true);

                TextField usernameField = new TextField("Username");
                usernameField.setValue(loggedInUser.getUsername());
                usernameField.setReadOnly(true);

                TextField phoneNumberField = new TextField("Phone Number");
                phoneNumberField.setValue(loggedInUser.getPhoneNumber());
                phoneNumberField.setReadOnly(true);

                TextField rolesField = new TextField("Roles");
                rolesField.setValue(roles);
                rolesField.setReadOnly(true);

                TextField addressField = new TextField("Address");
                addressField.setValue(fullAddress);
                addressField.setReadOnly(true);

                formLayout.add(nameField, emailField, usernameField, phoneNumberField, rolesField, addressField);
            }
        });
    }

    private void openEditProfileDialog() {
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent(userDetails -> {
            User loggedInUser = userService.findByUsername(userDetails.getUsername());
            if (loggedInUser != null) {
                EditProfileForm editProfileForm = new EditProfileForm(userService, loggedInUser, updated -> populateUserProfile(new FormLayout()));
                editProfileForm.open();
            }
        });
    }

    private void applyCustomStyling() {
        getStyle().set("background-color", "#f9f9f9");
        getStyle().set("padding", "20px");

        getElement().getThemeList().add("light");
    }

    private Div propertyCard(byte [] imageBytes, String title, String location, String price, String type, String agent){

        Div d1 = new Div();
        d1.getStyle().setAlignItems(Style.AlignItems.CENTER);
        d1.getStyle().setDisplay(Style.Display.FLEX);

        d1.getStyle().setPadding("10px");
        d1.getStyle().setBackgroundColor("white");
        d1.getStyle().setBorderRadius("10px");


        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        StreamResource resource = new StreamResource("",()->byteArrayInputStream);

        Image propertyImage = new Image(resource,"");
        propertyImage.setWidth("55px");
        propertyImage.setHeight("55px");
        propertyImage.getStyle().setBorderRadius("10px");
        propertyImage.getStyle().setMarginRight("4px");
        Paragraph h1 = new Paragraph(title);
        h1.setClassName("plc");
        Paragraph h2 = new Paragraph(location);
        h2.setClassName("plc");
        Paragraph h3 = new Paragraph(price);
        h3.setClassName("plc");
        Paragraph h4 = new Paragraph(type);
        h4.setClassName("plc");
        Paragraph h5 = new Paragraph(agent);
        h4.setClassName("plc");

        Button b1 = new Button("View property");
        b1.getStyle().setMarginRight("8px");
        Image more = new Image("/images/more.png","");


        d1.add(propertyImage,h1,h2,h3,h4,h5,b1, more);
        //propertyImage.getStyle().setFlexGrow("1");
        h1.getStyle().setFlexGrow("2");
        h2.getStyle().setFlexGrow("2");
        h3.getStyle().setFlexGrow("2");
        h4.getStyle().setFlexGrow("2");
        h5.getStyle().setFlexGrow("2");

        return d1;
    }

    private Div getUserPropertyList(){
        Div verticalLayout = new Div();
        verticalLayout.setWidthFull();
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent((userDetails)->{
            User user = userService.findByUsername(userDetails.getUsername());


            List<Property> userProperties = propertyService.getPropertyByUserId(user.getId());


            userProperties.forEach((property -> {
                AtomicReference<String> agentName = new AtomicReference<>();
                 userService.getUserById(property.getAgentId()).ifPresent(
                        (userAgent)-> agentName.set(userAgent.getUsername())
                );
                PropertyImage propertyImage = property.getPropertyImages().get(0);
                Div propertyCard = propertyCard(
                        propertyImage.getPropertyImages(),
                        property.getTitle(),property.getStreet(),property.getPrice().toEngineeringString(),
                        property.getPropertyType().getDisplayName(),agentName.get()
                        );
                verticalLayout.add(propertyCard);
            }));

        });
        return verticalLayout;
    }



}

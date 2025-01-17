package ng.org.mirabilia.pms.views.modules.profile.content;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.PropertyImage;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.settings.EditProfileForm;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ProfileContent extends VerticalLayout {

    private final UserService userService;
    private final AuthenticationContext authContext;

    private final UserImageService userImageService;
    private final PropertyService propertyService;
    final private H3 profileTitle;
    final private H3 propertyListingTitle;
    final private H3 paymentHistoryTitle;

    final private Div userProfileCard;
    final private Div paymentHistoryCard;
    final private Div propertyListing;

    final private FormLayout formCardFormLayout;
    Button formCardupdateButton;
    private final Div formCard;

    public ProfileContent(UserService userService, AuthenticationContext authContext, UserImageService userImageService, PropertyService propertyService) {
        this.userService = userService;
        this.authContext = authContext;
        this.userImageService = userImageService;
        this.propertyService = propertyService;

        setSpacing(true);
        setPadding(true);
        setWidthFull();
        setHeightFull();
        addClassName("module-content");

        profileTitle = new H3("Profile Information");
        propertyListingTitle = new H3("Property Listings");
        paymentHistoryTitle = new H3("Payment History");
        profileTitle.addClassName("profile-header");

        userProfileCard = getUserProfileCard();
        paymentHistoryCard = getPaymentHistoryCard();

        formCard = new Div();
        formCard.getStyle().setPadding("8px");
        formCard.getStyle().setBorderRadius("8px");
        formCard.getStyle().setBackgroundColor("white");

        formCardFormLayout = new FormLayout();
        formCardFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        populateFormLayoutWithUserProfile();

        formCardupdateButton = new Button("Update Information");
        formCardupdateButton.addClassName("custom-button");
        formCardupdateButton.addClassName("custom-update-button");
        formCardupdateButton.addClickListener(e -> openEditProfileDialog());


        formCard.add(formCardFormLayout, formCardupdateButton);

        propertyListing = getUserPropertyList();

        add(userProfileCard, profileTitle, formCard, propertyListingTitle, propertyListing);

        applyCustomStyling();
    }

    private Div getPaymentHistoryCard() {
        Div parent  = new Div();
        parent.setWidthFull();
        parent.setMinHeight("250px");
        parent.getStyle().setBackgroundColor("white");
        parent.getStyle().setBorderRadius("8px");
        return parent;
    }

    private Div getUserProfileCard() {
        Div d1 = new Div();
        Div imageContainer = new Div();
        imageContainer.setWidth("80px");
        imageContainer.setHeight("80px");
        imageContainer.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setBorderRadius("50%")
                .setMarginRight("8px")
                .setAlignItems(Style.AlignItems.CENTER)
                .setJustifyContent(Style.JustifyContent.CENTER);

        User user = userService.findByUsername(Application.globalLoggedInUsername);
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
            //profileImg.getStyle().setBackgroundColor("blue");
            Span h1 = new Span(user.getUsername().substring(0,1).toUpperCase());
            h1.getStyle()
                    .setColor("white")
                    .setMarginTop("8px")
                    .setFontSize("50px");
            imageContainer.add(h1);
            imageContainer.getStyle().setBackgroundColor(" #162868");
        }else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userImageBytes);
            StreamResource resource = new StreamResource("",()->byteArrayInputStream);
            profileImg.setSrc(resource);
            imageContainer.add(profileImg);
        }



        Paragraph username = new Paragraph(loggedInUsername);
        Paragraph role = new Paragraph(user.getRoles().toArray()[0].toString());
        Paragraph location = new Paragraph("Area 11, Garki");

        d2.add(username,role, location);
        d1.add(imageContainer,d2);
        return d1;
    }

    private void populateFormLayoutWithUserProfile() {
        System.out.println("\n\nxxxxdebug"+ Application.globalLoggedInUsername);
        User loggedInUser = userService.findByUsername(Application.globalLoggedInUsername);
        System.out.println(loggedInUser);

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

                System.out.println("\n\nUsername Rendering");
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

                //remove any old content
                formCardFormLayout.removeAll();
                formCardFormLayout.add(nameField, emailField, usernameField, phoneNumberField, rolesField, addressField);
            }
    }

    private void openEditProfileDialog() {

        User loggedInUser = userService.findByUsername(Application.globalLoggedInUsername);
        if (loggedInUser != null) {
            EditProfileForm editProfileForm = new EditProfileForm(userService, loggedInUser, updated -> {
                populateFormLayoutWithUserProfile();
                updateFormCardWithNewFormLayout();
            });
            editProfileForm.open();
        }

    }

    private void updateFormCardWithNewFormLayout(){
        System.out.println("updating UI");
        removeAll();
        formCard.removeAll();
        formCard.add(formCardFormLayout,formCardupdateButton);
        add(userProfileCard, profileTitle, formCard, propertyListingTitle, propertyListing, paymentHistoryTitle, paymentHistoryCard);
        System.out.println("After UI update");
    }

    private void applyCustomStyling() {
        getStyle().set("background-color", "#f9f9f9");
        getStyle().set("padding", "20px");

        getElement().getThemeList().add("light");
    }

    private Div propertyCard(byte [] imageBytes, String title, String location, String price, String type, String agent, Runnable navToPropertyDetails){

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

        Div h1Con = new Div();
        h1Con.getStyle().setDisplay(Style.Display.FLEX);
        h1Con.getStyle().setAlignItems(Style.AlignItems.CENTER);
        h1Con.getStyle().setTextAlign(Style.TextAlign.CENTER);
        h1Con.setWidth("20%");
        Paragraph h1 = new Paragraph(title);
        h1.setClassName("plc");
        h1Con.add(h1);


        Div h2Con = new Div();
        h2Con.getStyle().setDisplay(Style.Display.FLEX);
        h2Con.getStyle().setAlignItems(Style.AlignItems.CENTER);
        h2Con.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
        h2Con.getStyle().setTextAlign(Style.TextAlign.CENTER);
        h2Con.setWidth("20%");
        Paragraph h2 = new Paragraph(location);
        h2.setClassName("plc");
        h2Con.add(h2);


        Div h3Con = new Div();
        h3Con.getStyle().setDisplay(Style.Display.FLEX);
        h3Con.getStyle().setAlignItems(Style.AlignItems.CENTER);
        h3Con.getStyle().setTextAlign(Style.TextAlign.CENTER);
        h3Con.setWidth("20%");
        Paragraph h3 = new Paragraph(price);
        h3.setClassName("plc");
        h3Con.add(h3);

        Div h4Con = new Div();
        h4Con.getStyle().setDisplay(Style.Display.FLEX);
        h4Con.getStyle().setAlignItems(Style.AlignItems.CENTER);
        h4Con.getStyle().setTextAlign(Style.TextAlign.CENTER);
        h4Con.setWidth("20%");
        Paragraph h4 = new Paragraph(type);
        h4.setClassName("plc");
        h4Con.add(h4);

        Div h5Con = new Div();
        h5Con.getStyle().setDisplay(Style.Display.FLEX);
        h5Con.getStyle().setAlignItems(Style.AlignItems.CENTER);
        h5Con.getStyle().setTextAlign(Style.TextAlign.CENTER);
        h5Con.setWidth("20%");
        Paragraph h5 = new Paragraph(agent);
        h5.setClassName("plc");
        h5Con.add(h5);

        Button b1 = new Button("View property");
        b1.getStyle().setMarginRight("8px");
        b1.addClickListener((event)-> navToPropertyDetails.run());
        Image more = new Image("/images/more.png","");


        d1.add(propertyImage,h1Con,h2Con,h3Con,h4Con,h5Con,b1, more);
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
        verticalLayout.setMinHeight("250px");
        verticalLayout.setWidthFull();
        verticalLayout.getStyle().setBackgroundColor("white");
        verticalLayout.getStyle().setBorderRadius("8px");

        User user = userService.findByUsername(Application.globalLoggedInUsername);

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
                    property.getPropertyType().getDisplayName(),agentName.get(),
                    ()-> UI.getCurrent().navigate("property-detail/"+property.getId())
                    );
            propertyCard.getStyle().setMarginBottom("8px");
            verticalLayout.add(propertyCard);
        }));

        return verticalLayout;
    }



}

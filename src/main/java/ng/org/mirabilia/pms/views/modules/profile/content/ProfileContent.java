package ng.org.mirabilia.pms.views.modules.profile.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.settings.EditProfileForm;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProfileContent extends VerticalLayout {

    private final UserService userService;
    private final AuthenticationContext authContext;

    private final UserImageService userImageService;

    public ProfileContent(UserService userService, AuthenticationContext authContext, UserImageService userImageService) {
        this.userService = userService;
        this.authContext = authContext;
        this.userImageService = userImageService;

        setSpacing(true);
        setPadding(true);
        setWidthFull();
        addClassName("module-content");

        H3 profileTitle = new H3("Profile Information");
        profileTitle.addClassName("profile-header");

        Div userProfileCard = getUserProfileCard();

        Div formCard = new Div();
        formCard.getStyle().setPadding("8px");
        formCard.getStyle().setBackgroundColor("white");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        populateUserProfile(formLayout);

        Button updateButton = new Button("Update Information");
        updateButton.addClassName("custom-button");
        updateButton.addClassName("custom-update-button");

        updateButton.addClickListener(e -> openEditProfileDialog());

        formCard.add(formLayout, updateButton);
        add(userProfileCard, profileTitle, formCard);

        applyCustomStyling();
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
            profileImg.getStyle().setBackgroundColor("blue");
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
                        .map(role -> role.name())
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
                EditProfileForm editProfileForm = new EditProfileForm(userService, loggedInUser, updated -> {
                    populateUserProfile(new FormLayout());
                });
                editProfileForm.open();
            }
        });
    }

    private void applyCustomStyling() {
        getStyle().set("background-color", "#f9f9f9");
        getStyle().set("padding", "20px");

        getElement().getThemeList().add("light");
    }

    private Div propertyCard(){
        return null;
    }



}

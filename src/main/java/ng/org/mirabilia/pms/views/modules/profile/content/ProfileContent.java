package ng.org.mirabilia.pms.views.modules.profile.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.spring.security.AuthenticationContext;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.settings.EditProfileForm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

public class ProfileContent extends VerticalLayout {

    private final UserService userService;
    private final AuthenticationContext authContext;

    public ProfileContent(UserService userService, AuthenticationContext authContext) {
        this.userService = userService;
        this.authContext = authContext;

        setSpacing(true);
        setPadding(true);
        setWidthFull();
        addClassName("module-content");

        H2 profileTitle = new H2("Profile Information");
        profileTitle.addClassName("profile-header");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        populateUserProfile(formLayout);

        Button updateButton = new Button("Update Information");
        updateButton.addClassName("custom-button");
        updateButton.addClassName("custom-update-button");

        updateButton.addClickListener(e -> openEditProfileDialog());

        add(profileTitle, formLayout, updateButton);

        applyCustomStyling();
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
}

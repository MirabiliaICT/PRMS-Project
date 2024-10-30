package ng.org.mirabilia.pms.views.forms.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.function.Consumer;

public class EditProfileForm extends Dialog {

    private final UserService userService;
    private final User user;
    private final Consumer<Void> onSuccess;

    private final TextField emailField;
    private final TextField usernameField;
    private final PasswordField passwordField;

    public EditProfileForm(UserService userService, User user, Consumer<Void> onSuccess) {
        this.userService = userService;
        this.user = user;
        this.onSuccess = onSuccess;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        addClassName("custom-form");

        H2 header = new H2("Edit Profile");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();

        emailField = new TextField("Email");
        usernameField = new TextField("Username");
        passwordField = new PasswordField("New Password");

        emailField.setValue(user.getEmail());
        usernameField.setValue(user.getUsername());

        formLayout.add(emailField, usernameField, passwordField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveProfile());

        discardButton.addClassName("custom-button");
        saveButton.addClassName("custom-button");

        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-save-button");

        HorizontalLayout footer = new HorizontalLayout(discardButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    private void saveProfile() {
        String email = emailField.getValue();
        String username = usernameField.getValue();
        String newPassword = passwordField.getValue();

        if (email.isEmpty() || username.isEmpty()) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(newPassword);

        try {
            userService.updateUserWithPassword(user);

            Notification notification = Notification.show("Profile updated successfully", 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            this.close();
            onSuccess.accept(null);

        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getRootCause().getMessage();

            if (errorMessage.contains("email")) {
                emailField.setInvalid(true);
                emailField.setErrorMessage("This email is already in use. Please use a different one.");
            } else if (errorMessage.contains("username")) {
                usernameField.setInvalid(true);
                usernameField.setErrorMessage("This username is already in use. Please choose another.");
            } else {
                Notification.show("An error occurred. Please try again.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }
}

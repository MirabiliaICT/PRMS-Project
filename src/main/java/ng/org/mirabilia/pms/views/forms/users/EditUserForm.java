package ng.org.mirabilia.pms.views.forms.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class EditUserForm extends Dialog {

    private final UserService userService;
    private final UserImageService userImageService;
    private final User user;
    private final Consumer<Void> onSuccess;

    Image userImage;
    private final TextField firstNameField;
    private final TextField middleNameField;
    private final TextField lastNameField;
    private final TextField emailField;
    private final TextField usernameField;
    private final TextField phoneNumberField;
    private final TextField streetField;
    private final TextField cityField;
    private final TextField stateField;
    private final TextField postalCodeField;
    private final TextField houseNumberField;
    private final ComboBox<Role> roleComboBox;
    private final PasswordField passwordField;

    private final ComboBox<String> statusCombobox;

    public EditUserForm(UserService userService, UserImageService userImageService ,User user, Consumer<Void> onSuccess, Role userType) {
        this.userService = userService;
        this.userImageService = userImageService;

        this.user = user;
        this.onSuccess = onSuccess;

        setModal(true);
        setDraggable(false);
        setResizable(false);
        addClassName("custom-form");

        H2 header = new H2("Edit User");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();

        configureUserProfileImage();

        firstNameField = new TextField("First Name");
        middleNameField = new TextField("Middle Name(optional)");
        lastNameField = new TextField("Last Name");
        emailField = new TextField("Email");
        usernameField = new TextField("Username");
        phoneNumberField = new TextField("Phone Number");
        streetField = new TextField("Street");
        cityField = new TextField("City");
        stateField = new TextField("State");
        postalCodeField = new TextField("Postal Code");
        houseNumberField = new TextField("House Number");
        passwordField = new PasswordField("New Password");


        roleComboBox = new ComboBox<>("Role");
        roleComboBox.setItems(Role.values());
        if(userType.equals(Role.ADMIN)){
            roleComboBox.setItems(Role.values());
        }else {
            ArrayList<Role> roles = new ArrayList<>(Arrays.stream(Role.values()).filter((role)->role.equals(Role.CLIENT)).toList());
            roleComboBox.setItems(roles);
        }

        statusCombobox = new ComboBox<>("Status");
        statusCombobox.setItems("Active","Inactive");


        formLayout.add(firstNameField, middleNameField, lastNameField, emailField, usernameField, phoneNumberField,
                houseNumberField, streetField, cityField, stateField, postalCodeField, roleComboBox, passwordField, statusCombobox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        firstNameField.setValue(user.getFirstName());
        middleNameField.setValue(user.getMiddleName() != null ? user.getMiddleName() : "");
        lastNameField.setValue(user.getLastName());
        emailField.setValue(user.getEmail());
        usernameField.setValue(user.getUsername());
        phoneNumberField.setValue(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        streetField.setValue(user.getStreet() != null ? user.getStreet() : "");
        cityField.setValue(user.getCity() != null ? user.getCity() : "");
        stateField.setValue(user.getState() != null ? user.getState() : "");
        postalCodeField.setValue(user.getPostalCode() != null ? user.getPostalCode() : "");
        houseNumberField.setValue(user.getHouseNumber() != null ? user.getHouseNumber() : "");
        roleComboBox.setValue(user.getRoles().stream().findFirst().orElse(null));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveUser());
        Button deleteButton = new Button("Delete", e -> deleteUser());

        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");
        deleteButton.addClassName("custom-button");
        deleteButton.addClassName("custom-delete-button");

        HorizontalLayout footer = new HorizontalLayout(discardButton, deleteButton, saveButton);
//        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, userImage, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    private void configureUserProfileImage() {
        //Configure User Image Display
        byte[] userImageBytes = null;

        //Admin has no UserImage
        if(userImageService.getUserImageByUser(user) != null){
            userImageBytes= userImageService.getUserImageByUser(user).getUserImage();
        }

        if(userImageBytes != null){
            ByteArrayInputStream byteArrayInputStreamForUserImage = new ByteArrayInputStream(userImageBytes);
            StreamResource resource = new StreamResource("",()->byteArrayInputStreamForUserImage);
            userImage = new Image(resource,"");
            userImage.setClassName("image");
            userImage.setHeight("200px");
            userImage.setWidth("200px");
        }else{
            userImage = new Image();
            userImage.setHeight("200px");
            userImage.setWidth("200px");
            userImage.setClassName("image");
        }
    }

    private void saveUser() {
        String firstName = firstNameField.getValue();
        String lastName = lastNameField.getValue();
        String email = emailField.getValue();
        String username = usernameField.getValue();
        String phoneNumber = phoneNumberField.getValue();
        Role selectedRole = roleComboBox.getValue();
        String newPassword = passwordField.getValue();
        boolean statusField = statusCombobox.getValue().equals("Active");

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || selectedRole == null || phoneNumber.isEmpty()) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        user.setFirstName(firstName);
        user.setMiddleName(middleNameField.getValue());
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setStreet(streetField.getValue());
        user.setCity(cityField.getValue());
        user.setState(stateField.getValue());
        user.setPostalCode(postalCodeField.getValue());
        user.setHouseNumber(houseNumberField.getValue());
        user.setRoles(Set.of(selectedRole));
        user.setPassword(newPassword);
        user.setActive(statusField);

        userService.updateUserWithPassword(user);

        Notification notification = Notification.show("User updated successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
    }

    private void deleteUser() {
        try {
            userService.deleteUser(user.getId());
            this.close();
            onSuccess.accept(null);
        } catch (Exception ex) {
            Notification notification = Notification.show("Unable to delete user: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


}

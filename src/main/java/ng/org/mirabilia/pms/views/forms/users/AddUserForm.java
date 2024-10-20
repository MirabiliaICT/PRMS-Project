package ng.org.mirabilia.pms.views.forms.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.Role;
import ng.org.mirabilia.pms.services.UserService;

import java.util.function.Consumer;

public class AddUserForm extends Dialog {

    private final UserService userService;
    private final TextField firstNameField;
    private final TextField middleNameField;
    private final TextField lastNameField;
    private final TextField emailField;
    private final TextField phoneNumberField;
    private final TextField streetField;
    private final TextField cityField;
    private final TextField stateField;
    private final TextField postalCodeField;
    private final TextField houseNumberField;
    private final MultiSelectComboBox<Role> rolesField;
    private final Consumer<Void> onSuccess;

    public AddUserForm(UserService userService, Consumer<Void> onSuccess) {
        this.userService = userService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("New User");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        firstNameField = new TextField("First Name");
        middleNameField = new TextField("Middle Name");
        lastNameField = new TextField("Last Name");
        emailField = new TextField("Email");
        phoneNumberField = new TextField("Phone Number");
        streetField = new TextField("Street");
        cityField = new TextField("City");
        stateField = new TextField("State");
        postalCodeField = new TextField("Postal Code");
        houseNumberField = new TextField("House Number");

        rolesField = new MultiSelectComboBox<>("Roles");
        rolesField.setItems(Role.values());

        formLayout.add(firstNameField, middleNameField, lastNameField, emailField,
                phoneNumberField, houseNumberField, streetField, cityField, stateField, postalCodeField, rolesField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveUser());

        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");

        HorizontalLayout footer = new HorizontalLayout(discardButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);
        add(formContent);
    }

    private void saveUser() {
        String firstName = firstNameField.getValue();
        String middleName = middleNameField.getValue();
        String lastName = lastNameField.getValue();
        String email = emailField.getValue();
        String phoneNumber = phoneNumberField.getValue();
        String street = streetField.getValue();
        String city = cityField.getValue();
        String state = stateField.getValue();
        String postalCode = postalCodeField.getValue();
        String houseNumber = houseNumberField.getValue();
        var roles = rolesField.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || roles.isEmpty()) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        String username = generateUsername(firstName, lastName);
        String defaultPassword = generateDefaultPassword(username, middleName);

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setMiddleName(middleName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(defaultPassword);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setStreet(street);
        newUser.setCity(city);
        newUser.setState(state);
        newUser.setPostalCode(postalCode);
        newUser.setHouseNumber(houseNumber);
        newUser.setRoles(roles);
        userService.addUser(newUser);

        Notification.show("User added successfully. Username: " + username + ", Password: " + defaultPassword,
                        5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
    }

    private String generateUsername(String firstName, String lastName) {
        return (firstName + lastName).toLowerCase().replaceAll("\\s+", "");
    }

    private String generateDefaultPassword(String username, String middleName) {
        return middleName == null || middleName.isEmpty() ? username : (username + middleName.toLowerCase());
    }
}

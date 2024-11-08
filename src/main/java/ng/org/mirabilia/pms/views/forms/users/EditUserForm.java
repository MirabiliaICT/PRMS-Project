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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EditUserForm extends Dialog {

    private final UserService userService;
    private final UserImageService userImageService;
    private final User user;
    private UserImage userImage;
    private final Consumer<Void> onSuccess;

    Image userImagePreview;
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

    private Upload imageUploadComponent;

    private final Binder<User> binder ;

    private byte[] userProfileImageBytes;
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
        if(user.isActive()){
            statusCombobox.setValue("Active");
        }else{
            statusCombobox.setValue("Inactive");
        }

        //configure upload component
        configureImageUploadComponent();

        if(userType.equals(Role.CLIENT)){
            roleComboBox.setValue(Role.CLIENT);
            roleComboBox.setVisible(false);
        }
        formLayout.add(firstNameField, middleNameField, lastNameField, emailField, usernameField, phoneNumberField,
                houseNumberField, streetField, cityField, stateField, postalCodeField, roleComboBox, passwordField, statusCombobox, imageUploadComponent);
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

        //binder config
        binder = new Binder<>();
        binder.forField(emailField).withValidator((email)->{
            User userDb = userService.findByEmail(email);
            //user with email does not exist: validate
            return userDb == null || user.getEmail().equals(userDb.getEmail());
        }, "Email is used by another user").bind(User::getEmail, User::setEmail);
        binder.forField(phoneNumberField).withValidator(
                (phoneNumber)->{
                    User userDb = userService.findByPhoneNumber(phoneNumber);
                    return userDb == null || user.getPhoneNumber().equals(userDb.getPhoneNumber());
                },"Phone number in use by another user"
        ).bind(User::getPhoneNumber, User::setPhoneNumber);

        binder.forField(usernameField).withValidator(
                (username)->{
                    User userDb = userService.findByUsername(username);

                    return userDb == null || user.getUsername().equals(userDb.getUsername());
                },"Username not available for use")
                .bind(User::getUsername, User::setUsername);

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

        VerticalLayout formContent = new VerticalLayout(header, userImagePreview, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    private void configureUserProfileImage() {
        //Configure User Image Display
        byte[] userImageBytes = null;

        //Admin has no UserImage
        userImage = userImageService.getUserImageByUser(user);
        if(userImage != null){
            userImageBytes= userImage.getUserImage();
        }

        if(userImageBytes != null){
            ByteArrayInputStream byteArrayInputStreamForUserImage = new ByteArrayInputStream(userImageBytes);
            StreamResource resource = new StreamResource("",()->byteArrayInputStreamForUserImage);
            userImagePreview = new Image(resource,"");
            userImagePreview.setClassName("image");
            userImagePreview.setHeight("200px");
            userImagePreview.setWidth("200px");
        }else{
            userImagePreview = new Image();
            userImagePreview.setHeight("200px");
            userImagePreview.setWidth("200px");
            userImagePreview.setClassName("image");
        }
    }

    private void configureImageUploadComponent() {
        AtomicReference<Image> imagePreview =  new AtomicReference<>();
        MultiFileMemoryBuffer uploadBuffer = new MultiFileMemoryBuffer();
        imageUploadComponent = new Upload(uploadBuffer);
        imageUploadComponent.setAcceptedFileTypes("image/jpeg", "image/png");
        imageUploadComponent.addSucceededListener((event)->{
            String imageName = event.getFileName();
            System.out.println(this.getClassName()+" 85] File name: "+ imageName);

            InputStream inputStream = uploadBuffer.getInputStream(event.getFileName());
            byte [] imageBytes;
            try {
                imageBytes = inputStream.readAllBytes();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userProfileImageBytes = imageBytes;

            ByteArrayInputStream byteArrayInputStreamForImagePreview = new ByteArrayInputStream(imageBytes);
            StreamResource resource = new StreamResource("",()-> byteArrayInputStreamForImagePreview);
            imagePreview.set(new Image(resource, ""));
            imagePreview.get().setWidth("100px");
            imagePreview.get().setHeight("100px");
            imagePreview.get().getStyle().set("border-radius", "10px");
        });
        imageUploadComponent.addFileRemovedListener((e)->{
            System.out.println("Remove image");
            userProfileImageBytes = null;

        });
        imageUploadComponent.setUploadButton(new Button("Upload Profile Picture"));
        imageUploadComponent.setMaxFiles(1);
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

        try{
            System.out.println("Validating");
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPhoneNumber(phoneNumber);

            binder.writeBean(user);
        } catch (ValidationException e) {
            System.out.println("Validation issues");
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

        {
            if(userProfileImageBytes != null){
                userImage.setUserImage(userProfileImageBytes);
                userImageService.saveUserImage(userImage);
            }
        }

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

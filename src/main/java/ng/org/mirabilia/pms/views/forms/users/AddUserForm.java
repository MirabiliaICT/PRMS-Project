package ng.org.mirabilia.pms.views.forms.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AddUserForm extends Dialog {

    private final UserService userService;
    private final StateService stateService;

    private final UserImageService userImageService;
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

    private final ComboBox<State> stateComboBox;

    private  Upload imageUploadComponent;
    private final FormLayout formLayout;

    private final HorizontalLayout imagePreviewLayout;

    private byte[] userProfileImageBytes;

    private final List<UploadedImage> uploadImagesList;

    private final Consumer<Void> onSuccess;

    private final String serverImageLocation;

    public AddUserForm(UserService userService, StateService stateService,
                       UserImageService userImageService,
                       Consumer<Void> onSuccess) {

        this.userService = userService;
        this.userImageService = userImageService;
        this.onSuccess = onSuccess;
        this.stateService = stateService;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        uploadImagesList = new ArrayList<>();
        imagePreviewLayout = new HorizontalLayout();
        serverImageLocation = "C:\\Users\\atola\\OneDrive\\Desktop\\Mira\\ServerImages\\";


        H2 header = new H2("New User");
        header.addClassName("custom-form-header");

        formLayout = new FormLayout();
        firstNameField = new TextField("First Name");
        middleNameField = new TextField("Middle Name(optional)");
        lastNameField = new TextField("Last Name");
        emailField = new TextField("Email");
        phoneNumberField = new TextField("Phone Number");
        streetField = new TextField("Street");
        cityField = new TextField("City");
        stateField = new TextField("State");
        postalCodeField = new TextField("Postal Code");
        houseNumberField = new TextField("House Number");
        stateComboBox = new ComboBox<>("Manager State");

        rolesField = new MultiSelectComboBox<>("Roles");
        rolesField.setItems(Role.values());
        rolesField.addSelectionListener((x)->{
            if(x.getValue().contains(Role.MANAGER)){
                stateComboBox.setItemLabelGenerator(State::getName);
                stateComboBox.setItems(stateService.getAllStates());
                formLayout.remove(stateComboBox);
                formLayout.add(stateComboBox);
            }else {
                formLayout.remove(stateComboBox);
            }
        });


        configureImageUploadComponent();

        formLayout.add(firstNameField, middleNameField, lastNameField, emailField,
                phoneNumberField, houseNumberField, streetField, cityField,
                stateField, postalCodeField, rolesField,imageUploadComponent);
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

        VerticalLayout formContent = new VerticalLayout(header, formLayout,imagePreviewLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);
        add(formContent);
    }

    private void configureImageUploadComponent() {
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
            Image imagePreview = new Image(resource,"");
            imagePreview.setWidth("100px");
            imagePreview.setHeight("100px");
            imagePreview.getStyle().set("border-radius", "10px");
            imagePreviewLayout.add(imagePreview);

            //For Folder Storage
            uploadImagesList.add(new UploadedImage(imageName, inputStream));

        });
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
        String defaultPassword = generateDefaultPassword();

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
        if(roles.contains(Role.MANAGER)){
            State managerState = stateComboBox.getValue();
            newUser.setStateForManager(managerState);
        }
        User dbUser = userService.addUser(newUser);
        {
            UserImage userImage = new UserImage();
            userImage.setImageName("ProfileImage");
            userImage.setUserImage(userProfileImageBytes);
            userImage.setUser(dbUser);

            userImageService.saveUserImage(userImage);
        }



        Notification.show("User added successfully. Username: " + username + ", Password: " + defaultPassword,
                        5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
    }

    private String generateUsername(String firstName, String lastName) {
        if(firstName.length() < 4){
            firstName = firstName + "xxxx";
        }
        if(lastName.length() < 4){
            lastName = lastName + "xxxx";
        }

        return (firstName.substring(0,4) + lastName.substring(0,4)).toLowerCase().replaceAll("\\s+", "");
    }

    private String generateDefaultPassword() {
        return generateRandomString(5);
    }

    public static String generateRandomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);

        // Append random characters to the string builder
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }




    private String getImageTypeString(int type){
        if(type == 5){
            return "jpeg";
        }
        //6
        return "png";
    }

    /*Saves the uploaded images under the
     username directory of the user in server storage directory*/
    private void saveUserImages(String username){
        System.out.println("[AddUserForm 194]"+"list size: " + uploadImagesList.size());
        for (UploadedImage uploadedImage: uploadImagesList) {
            InputStream imageInputStream = uploadedImage.inputStream;
            String imageFilename = uploadedImage.fileName;
            System.out.println("\n\n[AddUserForm 198]"+"image name: " +imageFilename);
            try {
                BufferedImage image = ImageIO.read(imageInputStream);
                System.out.println("File Type name: "+ image.getType());

                //create user images dir
                File parentDir = new File(serverImageLocation + "\\" + username + "\\");
                //make if does not already exist
                if(parentDir.mkdir()){
                    System.out.println("\n\n[AddUserForm 198] New Directory Creation");
                }

                //Add image under parent dir
                File imageFile = new File(parentDir, imageFilename);
                ImageIO.write(image,getImageTypeString(image.getType()),imageFile);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Data
    @AllArgsConstructor
    static class UploadedImage{
        String fileName;
        InputStream inputStream;
    }

}

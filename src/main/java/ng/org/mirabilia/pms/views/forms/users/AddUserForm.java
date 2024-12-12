package ng.org.mirabilia.pms.views.forms.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import ng.org.mirabilia.pms.domain.entities.NextOfKinDetails;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.entities.UserImage;
import ng.org.mirabilia.pms.domain.enums.*;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AddUserForm extends Dialog {

    private final UserService userService;
    private final StateService stateService;

    private final UserImageService userImageService;
    private final TextField firstNameField;
    private final TextField middleNameField;
    private final TextField lastNameField;

    private final TextField userNameField;
    private final TextField emailField;
    private final TextField phoneNumberField;
    private final TextField streetField;
    private final TextField cityField;
    private final TextField stateField;
    private final TextField postalCodeField;
    private final TextField houseNumberField;
    private final TextField identificationNumberField;
    private final TextField occupationField;

    private final TextField kinNameField;
    private final ComboBox<Relationship> kinRelationshipComboBox;
    private final ComboBox<Gender> kinGenderComboBox;
    private final TextField kinAddressField;

    private final TextField kinEmailField;
    private final TextField kinTelephoneField;


    private final MultiSelectComboBox<Role> rolesField;

    private final ComboBox<State> stateComboBox;
    private final ComboBox<AfricanNationality> nationalityComboBox;

    private final ComboBox<Identification> modeOfIdentificationComboBox;
    private final ComboBox<MaritalStatus> maritalStatusComboBox;
    private final ComboBox<Gender> genderComboBox;

    private final DatePicker dobPicker;

    private  Upload imageUploadComponent;
    private final FormLayout formLayout;
    VerticalLayout formContent;

    private final HorizontalLayout imagePreviewLayout;

    private byte[] userProfileImageBytes;

    private final Consumer<Void> onSuccess;

    private final Binder<User> binder;

    public AddUserForm(UserService userService, StateService stateService,
                       UserImageService userImageService,
                       Consumer<Void> onSuccess, Role userType) {


        this.userService = userService;
        this.userImageService = userImageService;
        this.onSuccess = onSuccess;
        this.stateService = stateService;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        closeDialog.addClassName("user-form-close-button");
        closeDialog.addClickListener((e)->this.close());

        imagePreviewLayout = new HorizontalLayout();

        H2 header = new H2("New User");
        header.addClassName("custom-form-header");

        formLayout = new FormLayout();
        firstNameField = new TextField("First Name");
        middleNameField = new TextField("Middle Name(optional)");
        lastNameField = new TextField("Last Name");
        userNameField = new TextField("Username");
        emailField = new TextField("Email");
        phoneNumberField = new TextField("Phone Number");
        streetField = new TextField("Street");
        cityField = new TextField("City");
        stateField = new TextField("State");
        postalCodeField = new TextField("Postal Code");
        houseNumberField = new TextField("House Number");
        identificationNumberField = new TextField("Identification Number");
        occupationField = new TextField("Occupation");
        kinNameField =  new TextField("Next OF Kin Name");
        kinAddressField =  new TextField("Next Of Kin Address");
        kinEmailField =  new TextField("Next Of Kin Email");
        kinTelephoneField =  new TextField("Next Of Kin Telephone");

        stateComboBox = new ComboBox<>("Manager State");
        nationalityComboBox = new ComboBox<>("Nationality");
        modeOfIdentificationComboBox = new ComboBox<>("Mode Of Identification");
        maritalStatusComboBox = new ComboBox<>("Marital Status");
        genderComboBox = new ComboBox<>("Gender");
        dobPicker = new DatePicker("Date of Birth");
        kinGenderComboBox = new ComboBox<>("Next of Kin Gender");
        kinRelationshipComboBox = new ComboBox<>("Next of Kin Relationship");

        firstNameField.setRequiredIndicatorVisible(true);
        lastNameField.setRequired(true);
        emailField.setRequired(true);
        phoneNumberField.setRequired(true);
        userNameField.setReadOnly(true);
        houseNumberField.setRequired(true);
        streetField.setRequired(true);
        cityField.setRequired(true);
        stateField.setRequired(true);
        streetField.setRequired(true);
        genderComboBox.setRequired(true);
        nationalityComboBox.setRequired(true);
        modeOfIdentificationComboBox.setRequired(true);
        postalCodeField.setRequired(true);

        kinNameField.setRequired(true);
        kinRelationshipComboBox.setRequired(true);
        kinTelephoneField.setRequired(true);
        kinGenderComboBox.setRequired(true);
        kinAddressField.setRequired(true);
        kinEmailField.setRequired(true);


        firstNameField.addValueChangeListener((e)->{
            userNameField.setValue(getProcessedUsername());
        });
        lastNameField.addValueChangeListener((e)->{
            userNameField.setValue(getProcessedUsername());
        });

        //Component Configuration
        rolesField = new MultiSelectComboBox<>("Roles");
        if(userType.equals(Role.ADMIN)){
            ArrayList<Role> roles = new ArrayList<>(Arrays.stream(Role.values()).toList());
            roles.remove(Role.CLIENT);
            rolesField.setItems(roles);
        }

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

        //hide roleField
        if (userType.equals(Role.CLIENT)) {
            rolesField.setItems(Role.values());
            rolesField.setValue(Role.CLIENT);
            rolesField.setVisible(false);
        }
        nationalityComboBox.setItems(
                Arrays.stream(AfricanNationality.values())
                        .toList());
        modeOfIdentificationComboBox.setItems(
                Arrays.stream(Identification.values())
                        .toList());
        maritalStatusComboBox.setItems(
                Arrays.stream(MaritalStatus.values())
                        .toList());
        genderComboBox.setItems(
                Arrays.stream(Gender.values())
                        .toList());
        kinGenderComboBox.setItems(
                Arrays.stream(Gender.values())
                        .toList());
        kinRelationshipComboBox.setItems(
                Arrays.stream(Relationship.values())
                        .toList());

        dobPicker.setMax(LocalDate.now());
        dobPicker.setMin(LocalDate.of(1900, 1, 1)); // For very old dates, adjust as needed

        formLayout.add(firstNameField, middleNameField, lastNameField,userNameField, emailField,
                phoneNumberField, houseNumberField, streetField, cityField,
                stateField,nationalityComboBox,modeOfIdentificationComboBox,maritalStatusComboBox,genderComboBox,dobPicker, postalCodeField, rolesField,
                kinNameField,kinRelationshipComboBox,kinGenderComboBox,kinAddressField,kinEmailField,kinTelephoneField,
                imageUploadComponent);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        binder = new Binder<>();
        configureBinderForValidation(userService);

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveUser());

        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-save-button-user");

        HorizontalLayout footer = new HorizontalLayout(discardButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Div headerContainer = new Div();
        headerContainer.setWidthFull();
        headerContainer.getStyle().setDisplay(Style.Display.FLEX);
        headerContainer.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        headerContainer.add(header,closeDialog);
        formContent = new VerticalLayout(headerContainer, formLayout,imagePreviewLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);
        add(formContent);
    }
    private void configureBinderForValidation(UserService userService) {
        binder.forField(userNameField)
                .withValidator((username)->
                        !userService.userExistsByUsername(username),"Username not available").bind(User::getUsername, User::setUsername);
        binder.forField(emailField).withValidator((email)->
                        !userService.userExistsByEmail(email)
        ,"Email exist").bind(User::getEmail, User::setEmail);
        binder.forField(phoneNumberField).withValidator((phoneNumber)->
                        !userService.userExistsByPhoneNumber(phoneNumber)
        , "Phone Number exist").bind(User::getPhoneNumber, User::setPhoneNumber);
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
            imagePreviewLayout.add(imagePreview.get());
        });
        imageUploadComponent.addFileRemovedListener((e)->{
            System.out.println("Remove image");
            imagePreviewLayout.remove(imagePreview.get());
        });
        imageUploadComponent.setUploadButton(new Button("Upload Profile Picture"));
        imageUploadComponent.setMaxFiles(1);
    }

    private String getProcessedUsername(){
        String firstName = firstNameField.getValue() == null ? "" : firstNameField.getValue();
        String lastName = lastNameField.getValue() == null ? "" : lastNameField.getValue();

        String generatedUsername = generateUsername(firstName, lastName);

        if(userService.userExistsByUsername(generatedUsername)){
            String finalGenerateUsername = generatedUsername;
            int length = userService.getAllUsers().stream().filter((user)->{
                return user.getUsername().equals(finalGenerateUsername);
            }).toList().size();
            generatedUsername = generatedUsername + length;
            System.out.println("\n\n\nuser length: "+length);
        }

        return generatedUsername;
    }

    private void saveUser() {
        String firstName = firstNameField.getValue();
        String middleName = middleNameField.getValue();
        String lastName = lastNameField.getValue();
        String username = userNameField.getValue();
        String email = emailField.getValue();
        String phoneNumber = phoneNumberField.getValue();
        String street = streetField.getValue();
        String city = cityField.getValue();
        String state = stateField.getValue();
        String postalCode = postalCodeField.getValue();
        String houseNumber = houseNumberField.getValue();
        String identificationNumber = identificationNumberField.getValue();
        String occupation = occupationField.getValue();
        AfricanNationality nationality = nationalityComboBox.getValue();
        Gender gender = genderComboBox.getValue();
        Identification identification = modeOfIdentificationComboBox.getValue();
        MaritalStatus maritalStatus = maritalStatusComboBox.getValue();
        String kinName = kinNameField.getValue();
        String kinAddress = kinAddressField.getValue();
        String kinEmail = kinEmailField.getValue();
        String kinTele = kinTelephoneField.getValue();

        Gender kinGender = kinGenderComboBox.getValue();
        Relationship kinRelationship = kinRelationshipComboBox.getValue();
        LocalDate dob = dobPicker.getValue();

        //validation for email,username,phoneNumber
        try{
            System.out.println("validation occuring...");
            User validUser = new User();
            validUser.setEmail(email);
            validUser.setUsername(username);
            binder.writeBean(validUser);
            System.out.println("After validation");
        } catch (ValidationException e) {
            System.out.println("A validation error occurred\n" + e.getBeanValidationErrors());
            return;
        }

        var roles = rolesField.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || roles.isEmpty()
                || phoneNumberField.isEmpty() || houseNumber.isEmpty() || street.isEmpty() || city.isEmpty()
                ||state.isEmpty() || nationalityComboBox.isEmpty() || modeOfIdentificationComboBox.isEmpty()
        ||genderComboBox.isEmpty() || postalCode.isEmpty() || kinName.isEmpty() || kinRelationshipComboBox.isEmpty()
        || kinGenderComboBox.isEmpty() || kinAddress.isEmpty() || kinEmail.isEmpty() || kinTelephoneField.isEmpty()) {
            Notification.show("Please fill out all required fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String defaultPassword = generateDefaultPassword();

        //generate user code
        String userCode = generateUserCode();
        System.out.println("\n\nxxxxxxxxxx---->\n\n\n"+userService.userExistsByUserCode(userCode));
        while (userService.userExistsByUserCode(userCode)){
            System.out.println("\n\nusercode"+userCode);
            userCode = generateUserCode();
        }

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
        newUser.setNationality(nationality);
        newUser.setGender(gender);
        newUser.setDateOfBirth(dob);
        newUser.setModeOfIdentification(identification);
        newUser.setMaritalStatus(maritalStatus);
        newUser.setOccupation(occupation);
        newUser.setIdentificationNumber(identificationNumber);
        newUser.setDateOfBirth(dob);
        newUser.setUserCode(userCode);
        //Next Of Kin Object
        NextOfKinDetails nextOfKinDetails = new NextOfKinDetails();
        nextOfKinDetails.setName(kinName);
        nextOfKinDetails.setRelationship(kinRelationship);
        nextOfKinDetails.setGender(kinGender);
        nextOfKinDetails.setHouseAddress(kinAddress);
        nextOfKinDetails.setEmail(kinEmail);
        nextOfKinDetails.setTelePhone(kinTele);
        nextOfKinDetails.setUser(newUser);

        newUser.setNextOfKinDetails(nextOfKinDetails);


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



       /* Notification.show("User added successfully. Username: " + username + ", Password: " + defaultPassword,
                        5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);*/


        this.close();

        Dialog dialog = new Dialog();
        Div dialogLayout = new Div();

        Div header = new Div();
        header.getStyle().setDisplay(Style.Display.FLEX);
        header.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        header.getStyle().setAlignItems(Style.AlignItems.CENTER);

        Span title = new Span("User Created Successfully");
        title.getStyle().setMarginRight("10px");
        Button close  =  new Button(new Icon(VaadinIcon.CLOSE));
        close.addClickListener((e)-> dialog.close());
        header.add(title,close);

        H4 textbody = new H4("Password:  "+defaultPassword);
        H4 userCodeField = new H4("User Code: "+userCode);
        textbody.getStyle().setMarginTop("10px");
        dialogLayout.add(header,textbody, userCodeField);

        dialog.add(dialogLayout);
        dialog.open();

        onSuccess.accept(null);
    }

    private String generateUserCode(){
        int highestOrdinal = 0;
        Role highestRole = null;
        List<Role> role = rolesField.getValue().stream().toList();
        for(int i = 0; i < role.size(); i++){
            if(role.get(i).ordinal() > highestOrdinal){
                highestOrdinal = role.get(i).ordinal();
                highestRole = role.get(i);
            }
        }
        final Role finalRole = highestRole;
        int usersCount = userService
                .getAllUsers().stream().filter((user)-> user.getRoles().contains(finalRole)).toList().size();
        System.out.println("\n\nuserCountOfRole: "+usersCount);

        return roleShortener(highestRole.name())+"-" +  generateRandomString(6);
    }
    String roleShortener(String role){
        return role.substring(0,2);
    }
    private String generateDefaultPassword() {
        return generateRandomString(5);
    }

    public static String generateRandomString(int length) {
        String CHARACTERS = "A1B2C3D4E5F6G7H7I8JK9L7M6NOP4Q3R3ST2U3V4W5XYZ0123456789";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);

        int charCnt = 0, numberCnt = 0;
        // Append random characters to the string builder
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());

            
            char randomChar = CHARACTERS.charAt(index);
            if(Character.isDigit(randomChar)){
                numberCnt++;
                if(numberCnt < 4){
                    sb.append(randomChar);
                }else{
                    --i;
                }
            }
            else{
                charCnt++;
                if(charCnt  < 4){
                    sb.append(randomChar);
                }else{
                    --i;
                }
            }
        }

        return sb.toString();
    }

    public static String generateUsername(String firstname, String lastname){
       SecureRandom RANDOM = new SecureRandom();
       if(firstname.length() > 3) firstname = firstname.substring(0,3);
       if(lastname.length() > 3) lastname = lastname.substring(0,3);

       String username = firstname + lastname;
       while (username.length() < 6){
           username = username + RANDOM.nextInt(10);
       }
       return username;
    }

    @Data
    @AllArgsConstructor
    static class UploadedImage{
        String fileName;
        InputStream inputStream;
    }

}

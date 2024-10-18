package ng.org.mirabilia.pms.views.forms.location.city;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import ng.org.mirabilia.pms.models.City;
import ng.org.mirabilia.pms.models.State;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.StateService;

import java.util.List;
import java.util.function.Consumer;

public class AddCityForm extends Dialog {

    private final CityService cityService;
    private final StateService stateService;
    private final TextField nameField;
    private final TextField cityCodeField;
    private final ComboBox<State> stateComboBox;
    private final Consumer<Void> onSuccess; // Callback to update the grid

    public AddCityForm(CityService cityService, StateService stateService, Consumer<Void> onSuccess) {
        this.cityService = cityService;
        this.stateService = stateService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");


        // Header with "New City" text
        H2 header = new H2("New City");
        header.addClassName("custom-form-header");

        // Form layout with fields for name, code, and associated state
        FormLayout formLayout = new FormLayout();
        nameField = new TextField("City Name");
        cityCodeField = new TextField("City Code");
        stateComboBox = new ComboBox<>("State");

        // Populate the state dropdown with all states
        List<State> states = stateService.getAllStates();
        stateComboBox.setItems(states);
        stateComboBox.setItemLabelGenerator(State::getName); // Show state names in dropdown

        formLayout.add(nameField, cityCodeField, stateComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2)); // 2 fields per row

        // Footer buttons (Discard and Save)
        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveCity());

        discardButton.addClassName("custom-button");
        saveButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-save-button");

        HorizontalLayout footer = new HorizontalLayout(discardButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Add header, form, and footer to the dialog
        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    // Method to save the city and add it to the service
    private void saveCity() {
        String name = nameField.getValue();
        String cityCode = cityCodeField.getValue();
        State selectedState = stateComboBox.getValue();

        // Validate inputs
        if (selectedState == null || name.isEmpty() || cityCode.isEmpty()) {
            Notification.show("Please fill out all fields.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Create new City and set fields
        City newCity = new City();
        newCity.setName(name);
        newCity.setCityCode(cityCode);
        newCity.setState(selectedState);

        // Save to service
        cityService.addCity(newCity);

        // Show success notification
        Notification notification = Notification.show("City added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close(); // Close the dialog
        onSuccess.accept(null); // Update the grid
    }
}

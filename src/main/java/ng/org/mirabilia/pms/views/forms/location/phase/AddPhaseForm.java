package ng.org.mirabilia.pms.views.forms.location.phase;

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
import ng.org.mirabilia.pms.models.Phase;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.CityService;
import java.util.List;
import java.util.function.Consumer;

public class AddPhaseForm extends Dialog {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final TextField nameField;
    private final TextField phaseCodeField;
    private final ComboBox<City> cityComboBox; // City dropdown
    private final Consumer<Void> onSuccess; // Callback to update the grid

    public AddPhaseForm(PhaseService phaseService, CityService cityService, Consumer<Void> onSuccess) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        // Header with "New Phase" text
        H2 header = new H2("New Phase");
        header.addClassName("custom-form-header");

        // Form layout with fields for name, code, and associated city
        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Phase Name");
        phaseCodeField = new TextField("Phase Code");
        cityComboBox = new ComboBox<>("City");

        // Populate the city dropdown with available cities
        List<City> cities = cityService.getAllCities();
        cityComboBox.setItems(cities);
        cityComboBox.setItemLabelGenerator(City::getName); // Show city names in dropdown

        formLayout.add(nameField, phaseCodeField, cityComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2)); // 2 fields per row

        // Footer buttons (Discard and Save)
        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> savePhase());

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

    // Method to save the phase and add it to the service
    private void savePhase() {
        String name = nameField.getValue();
        String phaseCode = phaseCodeField.getValue();
        City selectedCity = cityComboBox.getValue();

        // Validate inputs
        if (name.isEmpty() || phaseCode.isEmpty() || selectedCity == null) {
            Notification.show("Please fill out all fields, including the city.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Create new Phase and set fields
        Phase newPhase = new Phase();
        newPhase.setName(name);
        newPhase.setPhaseCode(phaseCode);
        newPhase.setCity(selectedCity);  // Associate phase with selected city

        // Save to service
        phaseService.addPhase(newPhase);

        // Show success notification
        Notification notification = Notification.show("Phase added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close(); // Close the dialog
        onSuccess.accept(null); // Update the grid
    }
}

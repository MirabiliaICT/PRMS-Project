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

public class EditPhaseForm extends Dialog {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final TextField nameField;
    private final TextField phaseCodeField;
    private final ComboBox<City> cityComboBox; // City dropdown
    private final Phase phase;
    private final Consumer<Void> onSuccess; // Callback to notify when operation succeeds

    public EditPhaseForm(PhaseService phaseService, CityService cityService, Phase phase, Consumer<Void> onSuccess) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.phase = phase;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        // Header with "Edit Phase" text
        H2 header = new H2("Edit Phase");
        header.addClassName("custom-form-header");

        // Form layout with fields for name, code, and city
        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Phase Name");
        phaseCodeField = new TextField("Phase Code");
        cityComboBox = new ComboBox<>("City");

        // Set the existing phase data
        nameField.setValue(phase.getName() != null ? phase.getName() : "");
        phaseCodeField.setValue(phase.getPhaseCode() != null ? phase.getPhaseCode() : "");

        // Populate the city dropdown with all available cities
        List<City> cities = cityService.getAllCities();
        cityComboBox.setItems(cities);
        cityComboBox.setItemLabelGenerator(City::getName); // Show city names in the dropdown
        cityComboBox.setValue(phase.getCity()); // Pre-select the current city of the phase

        formLayout.add(nameField, phaseCodeField, cityComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2)); // 2 fields per row

        // Footer buttons (Discard, Save, and Delete)
        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> savePhase());
        Button deleteButton = new Button("Delete", e -> deletePhase());

        discardButton.addClassName("custom-button");
        saveButton.addClassName("custom-button");
        deleteButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-save-button");
        deleteButton.addClassName("custom-delete-button");

        HorizontalLayout footer = new HorizontalLayout(discardButton, deleteButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    // Method to save the edited phase and update it in the service
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

        // Update the phase fields
        phase.setName(name);
        phase.setPhaseCode(phaseCode);
        phase.setCity(selectedCity); // Set the selected city

        phaseService.editPhase(phase);

        // Show success notification
        Notification notification = Notification.show("Phase updated successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null); // Notify success and refresh the grid
    }

    // Method to delete the phase
    private void deletePhase() {
        try {
            phaseService.deletePhase(phase.getId());
            this.close();
            onSuccess.accept(null); // Notify success and refresh the grid
        } catch (IllegalStateException ex) {
            Notification notification = Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

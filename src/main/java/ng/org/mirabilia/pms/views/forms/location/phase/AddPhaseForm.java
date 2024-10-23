package ng.org.mirabilia.pms.views.forms.location.phase;

import com.vaadin.flow.component.Key;
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
import ng.org.mirabilia.pms.entities.City;
import ng.org.mirabilia.pms.entities.Phase;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.CityService;
import java.util.List;
import java.util.function.Consumer;

public class AddPhaseForm extends Dialog {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final TextField nameField;
    private final TextField phaseCodeField;
    private final ComboBox<City> cityComboBox;
    private final Consumer<Void> onSuccess;

    public AddPhaseForm(PhaseService phaseService, CityService cityService, Consumer<Void> onSuccess) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("New Phase");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Phase Name");
        phaseCodeField = new TextField("Phase Code");
        cityComboBox = new ComboBox<>("City");

        List<City> cities = cityService.getAllCities();
        cityComboBox.setItems(cities);
        cityComboBox.setItemLabelGenerator(City::getName);

        formLayout.add(cityComboBox, nameField, phaseCodeField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> savePhase());

        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);

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

    private void savePhase() {
        String name = nameField.getValue();
        String phaseCode = phaseCodeField.getValue();
        City selectedCity = cityComboBox.getValue();

        if (name.isEmpty() || phaseCode.isEmpty() || selectedCity == null) {
            Notification.show("Please fill out all fields, including the city.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (phaseService.phaseExists(name, phaseCode)) {
            Notification.show("State with this name or code already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }


        Phase newPhase = new Phase();
        newPhase.setName(name);
        newPhase.setPhaseCode(phaseCode);
        newPhase.setCity(selectedCity);

        phaseService.addPhase(newPhase);

        Notification notification = Notification.show("Phase added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
    }
}

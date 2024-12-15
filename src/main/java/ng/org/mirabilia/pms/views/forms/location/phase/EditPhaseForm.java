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
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.City;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.entities.Phase;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.CityService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Consumer;

public class EditPhaseForm extends Dialog {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final TextField nameField;
    private final TextField phaseCodeField;
    private final ComboBox<City> cityComboBox;
    private final Phase phase;
    private final Consumer<Void> onSuccess;

    public EditPhaseForm(PhaseService phaseService, CityService cityService, Phase phase, Consumer<Void> onSuccess) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.phase = phase;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("Edit Phase");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Phase Name");
        phaseCodeField = new TextField("Phase Code");
        cityComboBox = new ComboBox<>("City");

        nameField.setValue(phase.getName() != null ? phase.getName() : "");
        phaseCodeField.setValue(phase.getPhaseCode() != null ? phase.getPhaseCode() : "");

        List<City> cities = cityService.getAllCities();
        cityComboBox.setItems(cities);
        cityComboBox.setItemLabelGenerator(City::getName);
        cityComboBox.setValue(phase.getCity());

        formLayout.add(nameField, phaseCodeField, cityComboBox);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> {
            if(savePhase()){
                String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.EDIT);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitiator);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                Application.logService.addLog(log);
            }
        });
        Button deleteButton = new Button("Delete", e -> {
            if(deletePhase()){
                String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.DELETE);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitiator);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                Application.logService.addLog(log);
            }
        });

        deleteButton.addClickShortcut(Key.DELETE);
        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);

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

    private boolean savePhase() {
        String name = nameField.getValue();
        String phaseCode = phaseCodeField.getValue();
        City selectedCity = cityComboBox.getValue();

        if (name.isEmpty() || phaseCode.isEmpty() || selectedCity == null) {
            Notification.show("Please fill out all fields, including the city.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (phaseService.phaseExists(name, phaseCode)) {
            Notification.show("Phase with this name or code already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        phase.setName(name);
        phase.setPhaseCode(phaseCode);
        phase.setCity(selectedCity);

        phaseService.editPhase(phase);

        Notification notification = Notification.show("Phase updated successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
        return true;
    }

    private boolean deletePhase() {
        try {
            phaseService.deletePhase(phase.getId());
            this.close();
            onSuccess.accept(null);
            return true;
        } catch (IllegalStateException ex) {
            Notification notification = Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
    }
}

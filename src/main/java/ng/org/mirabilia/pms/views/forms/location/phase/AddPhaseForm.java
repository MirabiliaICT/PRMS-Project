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
import io.netty.util.internal.ThreadLocalRandom;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.City;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.entities.Phase;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.StateService;

import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ng.org.mirabilia.pms.Application.logService;

public class AddPhaseForm extends Dialog {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;
    private final TextField nameField;
    private final ComboBox<City> cityComboBox;
    private final ComboBox<String> stateComboBox;
    private final Consumer<Void> onSuccess;

    public AddPhaseForm(PhaseService phaseService, CityService cityService, StateService stateService, Consumer<Void> onSuccess) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("New Phase");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Phase Name");
        cityComboBox = new ComboBox<>("City");
        stateComboBox = new ComboBox<>("State");

        stateComboBox.setItems(stateService.getAllStates().stream().map(State::getName).collect(Collectors.toList()));
        stateComboBox.addValueChangeListener(e -> onStateSelected());


        cityComboBox.setItemLabelGenerator(City::getName);

        formLayout.add(stateComboBox, cityComboBox, nameField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> {
            if(savePhase()){
                //Log
                String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.ADD);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitiator);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                Application.logService.addLog(log);
            }
        });

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

    private boolean savePhase() {
        String name = nameField.getValue();
        Phase newPhase = new Phase();
        newPhase.setName(name);
        newPhase.setPhaseCode(generatePhaseCode());

        String phaseCode = newPhase.getPhaseCode();
        City selectedCity = cityComboBox.getValue();

        if (name.isEmpty() || phaseCode.isEmpty() || selectedCity == null) {
            Notification.show("Please fill out all fields, including the city.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (phaseService.phaseExists(name)) {
            Notification.show("State with this name already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (phaseService.phaseCodeExists(phaseCode)){
            newPhase.setPhaseCode(generatePhaseCode());
        }


        newPhase.setCity(selectedCity);

        phaseService.addPhase(newPhase);

        Notification notification = Notification.show("Phase added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
        return true;
    }

    private void onStateSelected() {
        String selectedState = stateComboBox.getValue();
        if (selectedState != null) {
            cityComboBox.setItems(cityService.getCitiesByState(selectedState));
            cityComboBox.setEnabled(true);
        } else {
            cityComboBox.clear();
            cityComboBox.setEnabled(false);
        }
    }

    public String generatePhaseCode() {
        String stateCode = null;
        String cityCode = null;

        String selectedStateName = stateComboBox.getValue();
        City selectedCity = cityComboBox.getValue();

        if (selectedStateName != null) {
            State selectedState = stateService.getStateByName(selectedStateName);
            if (selectedState != null) {
                stateCode = selectedState.getStateCode();
            }
        }

        if (selectedCity != null) {
            cityCode = selectedCity.getCityCode();
        }

        String phaseName = nameField.getValue();
        if (stateCode == null || cityCode == null || phaseName == null || phaseName.isEmpty()) {
            return "";
        }

        return  cityCode + phaseName.substring(0, 2).toUpperCase() + ThreadLocalRandom.current().nextInt(1, 99);
    }



}

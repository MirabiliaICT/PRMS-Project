package ng.org.mirabilia.pms.views.forms.location.city;

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
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.StateService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class AddCityForm extends Dialog {

    private final CityService cityService;
    private final StateService stateService;
    private final TextField nameField;
    private final ComboBox<State> stateComboBox;
    private final Consumer<Void> onSuccess;

    public AddCityForm(CityService cityService, StateService stateService, Consumer<Void> onSuccess) {
        this.cityService = cityService;
        this.stateService = stateService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");


        H2 header = new H2("New City");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("City Name");
        stateComboBox = new ComboBox<>("State");

        List<State> states = stateService.getAllStates();
        stateComboBox.setItems(states);
        stateComboBox.setItemLabelGenerator(State::getName);

        formLayout.add(stateComboBox, nameField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e -> saveCity());

        saveButton.addClickShortcut(Key.ENTER);
        discardButton.addClickShortcut(Key.ESCAPE);

        discardButton.addClassName("custom-button");
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

    private void saveCity() {
        String name = nameField.getValue();
        City newCity = new City();
        newCity.setName(name);
        newCity.setCityCode(generateCityCode());

        String cityCode = newCity.getCityCode();
        State selectedState = stateComboBox.getValue();

        if (selectedState == null || name.isEmpty()) {
            Notification.show("Please fill out all fields.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (cityService.cityExists(name)) {
            Notification.show("City with this name already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if(cityService.cityCodeExists(cityCode)){
            newCity.setCityCode(generateCityCode());
        }

        newCity.setState(selectedState);

        cityService.addCity(newCity);

        Notification notification = Notification.show("City added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        //Add Log
        String loggedInInitialtor = SecurityContextHolder.getContext().getAuthentication().getName();
        Log log = new Log();
        log.setAction(Action.ADDED);
        log.setModuleOfAction(Module.LOCATION);
        log.setInitiator(loggedInInitialtor);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        log.setTimestamp(timestamp);
        log.setInfo("City: " + newCity.getName());
        Application.logService.addLog(log);

        this.close();
        onSuccess.accept(null);
    }

    public String generateCityCode() {
        String state = stateComboBox.getValue().getStateCode();
        String city = nameField.getValue();

        return city != null && city.length() >= 2 ? state + nameField.getValue().substring(0, 2).toUpperCase() + ThreadLocalRandom.current().nextInt(1, 99) : "";
    }
}

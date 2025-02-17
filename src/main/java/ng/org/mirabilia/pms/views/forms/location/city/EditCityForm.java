package ng.org.mirabilia.pms.views.forms.location.city;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
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

public class EditCityForm extends Dialog {

    private final CityService cityService;
    private final StateService stateService;
    private final TextField nameField;
    private final TextField cityCodeField;
    private final ComboBox<State> stateComboBox;
    private final City city;
    private final Consumer<Void> onSuccess;
    private String oldCityName;

    public EditCityForm(CityService cityService, StateService stateService, City city, Consumer<Void> onSuccess) {
        this.cityService = cityService;
        this.stateService = stateService;
        this.city = city;
        this.onSuccess = onSuccess;
        oldCityName = city.getName();

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("Edit City");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("City Name");
        cityCodeField = new TextField("City Code");
        stateComboBox = new ComboBox<>("State");

        nameField.setValue(city.getName() != null ? city.getName() : "");
        cityCodeField.setValue(city.getCityCode() != null ? city.getCityCode() : "");

        List<State> states = stateService.getAllStates();
        stateComboBox.setItems(states);
        stateComboBox.setItemLabelGenerator(State::getName);
        stateComboBox.setValue(city.getState());

        formLayout.add(stateComboBox, nameField, cityCodeField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e ->
            {if(saveCity()){
                String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.EDITED);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitiator);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                log.setInfo("City: " + oldCityName + " To " + city.getName());
                Application.logService.addLog(log);
            }}
        );
        Button deleteButton = new Button("Delete", e ->
            {
                if(deleteCity()){
                    String loggedInInitiator = SecurityContextHolder.getContext().getAuthentication().getName();
                    Log log = new Log();
                    log.setAction(Action.DELETED);
                    log.setModuleOfAction(Module.LOCATION);
                    log.setInitiator(loggedInInitiator);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    log.setTimestamp(timestamp);
                    log.setInfo("City: " + oldCityName);
                    Application.logService.addLog(log);
                }
            }
        );

        discardButton.addClickShortcut(Key.ESCAPE);
        saveButton.addClickShortcut(Key.ENTER);
        deleteButton.addClickShortcut(Key.DELETE);

        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-save-button");
        deleteButton.addClassName("custom-delete-button");

        Div rightAlign = new Div();
        rightAlign.getStyle().setDisplay(Style.Display.FLEX);
        deleteButton.getStyle().setMarginRight("12px");
        rightAlign.add(deleteButton,saveButton);

        HorizontalLayout footer = new HorizontalLayout(discardButton, rightAlign);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    private boolean saveCity() {
        String name = nameField.getValue();
        String cityCode = generateCityCode();
        State selectedState = stateComboBox.getValue();

        if (selectedState == null || name.isEmpty() || cityCode.isEmpty()) {
            Notification.show("Please fill out all fields.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (cityService.cityExists(name)) {
            Notification.show("City with this name", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        city.setName(name);
        city.setCityCode(cityCode);
        city.setState(selectedState);

        cityService.editCity(city);

        Notification notification = Notification.show("City updated successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
        return true;
    }
    public String generateCityCode() {
        String state = stateComboBox.getValue().getStateCode();
        String city = nameField.getValue();

        return city != null && city.length() >= 2 ? state + nameField.getValue().substring(0, 2).toUpperCase() + ThreadLocalRandom.current().nextInt(1, 99) : "";
    }

    private boolean deleteCity() {
        try {
            cityService.deleteCity(city.getId());
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

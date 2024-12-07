package ng.org.mirabilia.pms.views.forms.location.state;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
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
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.StateService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.ThreadLocalRandom;
import java.sql.Timestamp;
import java.util.function.Consumer;

public class AddStateForm extends Dialog {

    private final StateService stateService;
    private final TextField nameField;
    private final Consumer<Void> onSuccess;

    public AddStateForm(StateService stateService, Consumer<Void> onSuccess) {
        this.stateService = stateService;
        this.onSuccess = onSuccess;

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("New State");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Name");

        formLayout.add(nameField);
        formLayout.setWidthFull();

        Button discardButton = new Button("Discard Changes", e -> this.close());
        discardButton.addClassName("custom-button");

        Button saveButton = new Button("Save", e -> {
            if(saveState()){
                //Add Log
                String loggedInInitialtor = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.ADD);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitialtor);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                Application.logService.addLog(log);
            }

        });
        saveButton.addClassName("custom-button");

        discardButton.addClassName("custom-button");
        discardButton.addClassName("custom-discard-button");
        saveButton.addClassName("custom-button");
        saveButton.addClassName("custom-save-button");

        discardButton.addClickShortcut(Key.ESCAPE);
        saveButton.addClickShortcut(Key.ENTER);

        HorizontalLayout footer = new HorizontalLayout(discardButton, saveButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout formContent = new VerticalLayout(header, formLayout, footer);
        formContent.setSpacing(true);
        formContent.setPadding(true);

        add(formContent);
    }

    private boolean saveState() {
        String name = nameField.getValue();
        State newState = new State();
        newState.setName(name);
        newState.setStateCode(generateStateCode());


        String stateCode = newState.getStateCode();

        if (name.isEmpty()) {
            Notification.show("Please fill out all fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (stateService.stateExists(name, stateCode)) {
            Notification.show("State with this name or code already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }



        stateService.addState(newState);

        Notification notification = Notification.show("State added successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();

        onSuccess.accept(null);
        return true;

    }

    public String generateStateCode() {
        String state = nameField.getValue();

        String stateCode = state != null && state.length() >= 2 ? state.substring(0, 2).toUpperCase() + ThreadLocalRandom.current().nextInt(1, 100) : "";
        return  stateCode;
    }
}

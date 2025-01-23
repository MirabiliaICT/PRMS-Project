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

import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class EditStateForm extends Dialog {

    private final StateService stateService;
    private final TextField nameField;
    private final TextField stateCodeField;
    private final State state;
    private final Consumer<Void> onSuccess;
    private String oldStateName;

    public EditStateForm(StateService stateService, State state, Consumer<Void> onSuccess) {
        this.stateService = stateService;
        this.state = state;
        this.onSuccess = onSuccess;
        oldStateName = state.getName();

        this.setModal(true);
        this.setDraggable(false);
        this.setResizable(false);
        this.addClassName("custom-form");

        H2 header = new H2("Edit State");
        header.addClassName("custom-form-header");

        FormLayout formLayout = new FormLayout();
        nameField = new TextField("Name");
        stateCodeField = new TextField("State Code");
        stateCodeField.setEnabled(false);

        nameField.setValue(state.getName());
        stateCodeField.setValue(state.getStateCode());

        formLayout.add(nameField, stateCodeField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        Button discardButton = new Button("Discard Changes", e -> this.close());
        Button saveButton = new Button("Save", e ->
        {if(saveState()){
            //Add Log
            String loggedInInitialtor = SecurityContextHolder.getContext().getAuthentication().getName();
            Log log = new Log();
            log.setAction(Action.EDITED);
            log.setModuleOfAction(Module.LOCATION);
            log.setInitiator(loggedInInitialtor);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            log.setTimestamp(timestamp);
            log.setInfo("State: "+ oldStateName + " To " + nameField.getValue());
            Application.logService.addLog(log);
        }});
        Button deleteButton = new Button("Delete", e ->
        {
            if(deleteState()){
                //Add Log
                String loggedInInitialtor = SecurityContextHolder.getContext().getAuthentication().getName();
                Log log = new Log();
                log.setAction(Action.DELETED);
                log.setModuleOfAction(Module.LOCATION);
                log.setInitiator(loggedInInitialtor);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                log.setTimestamp(timestamp);
                log.setInfo("State: "+state.getName());
                Application.logService.addLog(log);
            }
        });

        discardButton.addClickShortcut(Key.ESCAPE);
        saveButton.addClickShortcut(Key.ENTER);
        deleteButton.addClickShortcut(Key.DELETE);

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

    private boolean saveState() {
        String name = nameField.getValue();
        String stateCode = generateStateCode();

        if (name.isEmpty() || stateCode.isEmpty()) {
            Notification.show("Please fill out all fields", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (stateService.stateExists(name)) {
            Notification.show("State with this name already exists", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        state.setName(name);
        state.setStateCode(stateCode);

        stateService.editState(state);

        Notification notification = Notification.show("State updated successfully", 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        this.close();
        onSuccess.accept(null);
        return true;
    }

    public String generateStateCode() {
        String state = nameField.getValue();

        String stateCode = state != null && state.length() >= 2 ? state.substring(0, 2).toUpperCase() + ThreadLocalRandom.current().nextInt(1, 99) : "";
        return  stateCode;
    }

    private boolean deleteState() {
        try {
            stateService.deleteState(state.getId());
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

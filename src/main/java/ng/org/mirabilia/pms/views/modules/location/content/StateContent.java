package ng.org.mirabilia.pms.views.modules.location.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.forms.location.state.AddStateForm;
import ng.org.mirabilia.pms.views.forms.location.state.EditStateForm;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;

public class StateContent extends VerticalLayout {

    private final StateService stateService;
    private final Grid<State> stateGrid;
    private final TextField searchField;

    public StateContent(StateService stateService) {
        this.stateService = stateService;

        setSpacing(true);
        setPadding(false);
        addClassName("state-content");

        searchField = new TextField();
        searchField.setPlaceholder("Search State");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid());

        Button addStateButton = new Button("Add State");
        addStateButton.addClassName("custom-button");
        addStateButton.addClassName("custom-add-button");
        addStateButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        stateGrid = new Grid<>(State.class);
        stateGrid.addClassName("custom-grid");
        stateGrid.setColumns("name", "stateCode");
        stateGrid.setItems(stateService.getAllStates());

        stateGrid.asSingleSelect().addValueChangeListener(event -> {
            State selectedState = event.getValue();
            if (selectedState != null) {
                openEditStateDialog(selectedState);
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addStateButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        add(toolbar, stateGrid);

        addStateButton.addClickListener(e -> openAddStateDialog());

        updateGrid();
    }

    private void updateGrid() {
        String keyword = searchField.getValue();

        List<State> states;
        if (keyword == null || keyword.isEmpty()) {
            states = stateService.getAllStates();
        } else {
            states = stateService.searchStateByKeywords(keyword);
        }

        stateGrid.setItems(states);
    }

    private void openAddStateDialog() {
        AddStateForm stateForm = new AddStateForm(stateService, (v) -> updateGrid());
        stateForm.open();
    }

    private void openEditStateDialog(State state) {
        EditStateForm editStateForm = new EditStateForm(stateService, state, (v) -> {
            //Add Log
            String loggedInInitialtor = SecurityContextHolder.getContext().getAuthentication().getName();
            Log log = new Log();
            log.setAction(Action.ADD);
            log.setModuleOfAction(Module.LOCATION);
            log.setInitiator(loggedInInitialtor);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            log.setTimestamp(timestamp);
            Application.logService.addLog(log);
            System.out.println("Check Val\n\n\n\n\n\n\n\n\n\n" + log);
            updateGrid();
        });
        editStateForm.open();
    }
}

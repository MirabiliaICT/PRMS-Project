package ng.org.mirabilia.pms.views.location.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.models.State;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.forms.location.state.AddStateForm;
import ng.org.mirabilia.pms.views.forms.location.state.EditStateForm;

import java.util.List;

public class StateContent extends VerticalLayout {

    private final StateService stateService;  // Inject StateService
    private final Grid<State> stateGrid;      // Grid for displaying states
    private final TextField searchField;      // Search field for filtering states

    public StateContent(StateService stateService) {
        this.stateService = stateService;

        // Disable spacing and padding for the layout
        setSpacing(true);
        setPadding(false);
        addClassName("state-content"); // Class for StateContent layout styling

        // Initialize search field
        searchField = new TextField();
        searchField.setPlaceholder("Search State");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);  // Immediately update grid as user types
        searchField.addValueChangeListener(e -> updateGrid());

        // Add State button
        Button addStateButton = new Button("Add State");
        addStateButton.addClassName("custom-button"); // Add a class for button styling
        addStateButton.addClassName("custom-add-button");
        addStateButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        // Configure the grid for displaying states
        stateGrid = new Grid<>(State.class);
        stateGrid.addClassName("custom-grid"); // Add a class for grid styling
        stateGrid.setColumns("name", "stateCode"); // Assuming State class has fields 'name' and 'stateCode'
        stateGrid.setItems(stateService.getAllStates()); // Populate the grid with all states

        // Handle grid row click for editing
        stateGrid.asSingleSelect().addValueChangeListener(event -> {
            State selectedState = event.getValue();
            if (selectedState != null) {
                openEditStateDialog(selectedState);
            }
        });

        // Create a layout to hold the button and search field
        HorizontalLayout toolbar = new HorizontalLayout(searchField, addStateButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar"); // Add a class for toolbar layout styling

        // Add components to the layout
        add(toolbar, stateGrid);

        // Add click listener for Add State button to open the form
        addStateButton.addClickListener(e -> openAddStateDialog());

        // Initially populate the grid
        updateGrid();
    }

    // Method to update the grid based on search input or load all states
    private void updateGrid() {
        String keyword = searchField.getValue();

        List<State> states;
        if (keyword == null || keyword.isEmpty()) {
            states = stateService.getAllStates(); // If no keyword, fetch all states
        } else {
            states = stateService.searchStateByKeywords(keyword); // Search based on keywords
        }

        stateGrid.setItems(states); // Update the grid with the filtered states
    }

    // Method to open the "Add State" dialog using the AddStateForm class
    private void openAddStateDialog() {
        AddStateForm stateForm = new AddStateForm(stateService, (v) -> updateGrid());  // Pass a callback to update the grid
        stateForm.open();
    }

    // Method to open the "Edit State" dialog using the EditStateForm class
    private void openEditStateDialog(State state) {
        EditStateForm editStateForm = new EditStateForm(stateService, state, (v) -> updateGrid());
        editStateForm.open();
    }
}

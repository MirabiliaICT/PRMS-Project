package ng.org.mirabilia.pms.views.location.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.models.Phase;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.views.forms.location.phase.AddPhaseForm;
import ng.org.mirabilia.pms.views.forms.location.phase.EditPhaseForm;

import java.util.List;

public class PhaseContent extends VerticalLayout {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final Grid<Phase> phaseGrid;      // Grid for displaying phases
    private final TextField searchField;      // Search field for filtering phases

    public PhaseContent(PhaseService phaseService, CityService cityService) {
        this.phaseService = phaseService;
        this.cityService = cityService;

        // Disable spacing and padding for the layout
        setSpacing(true);
        setPadding(false);
        addClassName("phase-content");

        // Initialize search field
        searchField = new TextField();
        searchField.setPlaceholder("Search Phase");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);  // Immediately update grid as user types
        searchField.addValueChangeListener(e -> updateGrid());

        // Add Phase button
        Button addPhaseButton = new Button("Add Phase");
        addPhaseButton.addClassName("custom-button");
        addPhaseButton.addClassName("custom-add-button");
        addPhaseButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        // Configure the grid for displaying phases
        phaseGrid = new Grid<>(Phase.class);
        phaseGrid.addClassName("custom-grid");

        // Display phase name and phase code in the grid
        phaseGrid.setColumns("name", "phaseCode");

        // Populate the grid with all phases
        phaseGrid.setItems(phaseService.getAllPhases());

        // Handle grid row click for editing
        phaseGrid.asSingleSelect().addValueChangeListener(event -> {
            Phase selectedPhase = event.getValue();
            if (selectedPhase != null) {
                openEditPhaseDialog(selectedPhase);
            }
        });

        // Create a layout to hold the button and search field
        HorizontalLayout toolbar = new HorizontalLayout(searchField, addPhaseButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        // Add components to the layout
        add(toolbar, phaseGrid);

        // Add click listener for Add Phase button to open the form
        addPhaseButton.addClickListener(e -> openAddPhaseDialog());

        // Initially populate the grid
        updateGrid();
    }

    // Method to update the grid based on search input or load all phases
    private void updateGrid() {
        String keyword = searchField.getValue();

        List<Phase> phases;
        if (keyword == null || keyword.isEmpty()) {
            phases = phaseService.getAllPhases(); // If no keyword, fetch all phases
        } else {
            phases = phaseService.searchPhaseByKeywords(keyword); // Search based on keywords
        }

        phaseGrid.setItems(phases); // Update the grid with the filtered phases
    }

    // Method to open the "Add Phase" dialog (Assuming AddPhaseForm exists)
    private void openAddPhaseDialog() {
        AddPhaseForm phaseForm = new AddPhaseForm(phaseService, cityService, (v) -> updateGrid());
        phaseForm.open();
    }

    // Method to open the "Edit Phase" dialog (Assuming EditPhaseForm exists)
    private void openEditPhaseDialog(Phase phase) {
        EditPhaseForm editPhaseForm = new EditPhaseForm(phaseService, cityService, phase, (v) -> updateGrid());
        editPhaseForm.open();
    }
}

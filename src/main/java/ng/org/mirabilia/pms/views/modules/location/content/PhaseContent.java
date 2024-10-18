package ng.org.mirabilia.pms.views.modules.location.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.entity.City;
import ng.org.mirabilia.pms.entity.Phase;
import ng.org.mirabilia.pms.entity.State;
import ng.org.mirabilia.pms.services.CityService;
import ng.org.mirabilia.pms.services.PhaseService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.views.forms.location.phase.AddPhaseForm;
import ng.org.mirabilia.pms.views.forms.location.phase.EditPhaseForm;

import java.util.List;

public class PhaseContent extends VerticalLayout {

    private final PhaseService phaseService;
    private final CityService cityService;
    private final StateService stateService;

    private final Grid<Phase> phaseGrid;
    private final TextField searchField;
    private final ComboBox<State> stateComboBox;
    private final ComboBox<City> cityComboBox;

    public PhaseContent(PhaseService phaseService, CityService cityService, StateService stateService) {
        this.phaseService = phaseService;
        this.cityService = cityService;
        this.stateService = stateService;

        setSpacing(true);
        setPadding(false);
        addClassName("phase-content");

        searchField = new TextField();
        searchField.setPlaceholder("Search Phase");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.addClassName("custom-toolbar-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid());

        stateComboBox = new ComboBox<>("Filter by State");
        stateComboBox.setItemLabelGenerator(State::getName);
        stateComboBox.setItems(stateService.getAllStates());
        stateComboBox.addValueChangeListener(e -> onStateSelected());

        cityComboBox = new ComboBox<>("Filter by City");
        cityComboBox.setItemLabelGenerator(City::getName);
        cityComboBox.setEnabled(false);
        cityComboBox.addValueChangeListener(e -> updateGrid());

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");
        resetButton.addClickListener(e -> resetFilters());

        Button addPhaseButton = new Button("Add Phase");
        addPhaseButton.addClassName("custom-button");
        addPhaseButton.addClassName("custom-add-button");
        addPhaseButton.addClassName("custom-toolbar-button");
        addPhaseButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        phaseGrid = new Grid<>(Phase.class);
        phaseGrid.addClassName("custom-grid");

        phaseGrid.setColumns("name", "phaseCode");

        phaseGrid.setItems(phaseService.getAllPhases());

        phaseGrid.asSingleSelect().addValueChangeListener(event -> {
            Phase selectedPhase = event.getValue();
            if (selectedPhase != null) {
                openEditPhaseDialog(selectedPhase);
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchField, stateComboBox, cityComboBox, resetButton, addPhaseButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        add(toolbar, phaseGrid);

        addPhaseButton.addClickListener(e -> openAddPhaseDialog());

        updateGrid();
    }

    private void updateGrid() {
        String keyword = searchField.getValue();
        List<Phase> phases;

        if (cityComboBox.getValue() != null) {
            phases = phaseService.filterPhasesByCity(cityComboBox.getValue().getId());
        }
        else if (stateComboBox.getValue() != null) {
            phases = phaseService.filterPhasesByState(stateComboBox.getValue().getId());
        }
        else if (keyword != null && !keyword.isEmpty()) {
            phases = phaseService.searchPhaseByKeywords(keyword);
        } else {
            phases = phaseService.getAllPhases();
        }

        phaseGrid.setItems(phases);
    }


    private void resetFilters() {
        searchField.clear();
        stateComboBox.clear();
        cityComboBox.clear();
        cityComboBox.setEnabled(false);
        updateGrid();
    }

    private void openAddPhaseDialog() {
        AddPhaseForm phaseForm = new AddPhaseForm(phaseService, cityService, (v) -> updateGrid());
        phaseForm.open();
    }

    private void openEditPhaseDialog(Phase phase) {
        EditPhaseForm editPhaseForm = new EditPhaseForm(phaseService, cityService, phase, (v) -> updateGrid());
        editPhaseForm.open();
    }

    private void onStateSelected() {
        State selectedState = stateComboBox.getValue();
        if (selectedState != null) {
            List<City> cities = cityService.filterCitiesByState(selectedState.getId());
            cityComboBox.setItems(cities);
            cityComboBox.setEnabled(true);
        } else {
            cityComboBox.clear();
            cityComboBox.setEnabled(false);
        }
        updateGrid();
    }
}

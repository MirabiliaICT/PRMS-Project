package ng.org.mirabilia.pms.views.modules.logs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "logs", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
@PageTitle("Audit Logs | Property Management System")
public class LogsView extends VerticalLayout {


    LogService logService;
    Grid<Log> logGrid;
    List<Log> logList;

    private TextField searchField;
    private ComboBox<Action> actionFilter;
    private ComboBox<Module> moduleFilter;
    private Button resetButton;
    private DatePicker datePicker;

    @Autowired
    public LogsView(LogService logService) {
        this.logService = logService;
        logList = logService.getAllLogs();
        setPadding(false);
        setSpacing(true);
        setHeightFull();
        configureLogGrid();
        configureSearchHeader();
        configureActionFilter();
        configureModuleFilter();
        configureResetButton();
        configureDatePicker();

        HorizontalLayout toolbar = new HorizontalLayout(searchField, actionFilter, moduleFilter,datePicker, resetButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");
        toolbar.getStyle().setMarginTop("8px");

        add(toolbar,logGrid);
    }

    private void configureDatePicker() {
        datePicker = new DatePicker("Date");
        datePicker.addClassName("custom-search-field");
        datePicker.addValueChangeListener((date)->{
            updateGrid();
        });
    }


    private void configureResetButton() {
        resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");
        resetButton.addClickListener(e -> resetFilters());
    }

    private void resetFilters() {
        actionFilter.setValue(null);
        moduleFilter.setValue(null);
        searchField.clear();
        datePicker.clear();
        logGrid.setItems(logList);
    }

    private void configureModuleFilter() {
        moduleFilter = new ComboBox<>("Module", Module.values());
        moduleFilter.addValueChangeListener(e->updateGrid());
        moduleFilter.addClassName("custom-search-field");
    }

    private void configureActionFilter() {
        actionFilter = new ComboBox<>("Action", Action.values());
        actionFilter.addClassName("custom-search-field");
        actionFilter.addValueChangeListener(e-> updateGrid());
    }

    private void configureSearchHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Search by Initiator");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.addClassName("custom-toolbar-field");

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid());
    }

    public void updateGrid(){
        List<Log> listToDisplay = logList.stream().toList();
        if(!actionFilter.isEmpty()){
            listToDisplay = listToDisplay.stream().filter((log)->
                    log.getAction().equals(actionFilter.getValue())
            ).toList();
        }
        if(!moduleFilter.isEmpty()){
            listToDisplay = listToDisplay.stream().filter((log)->
                    log.getModuleOfAction().equals(moduleFilter.getValue())
            ).toList();
        }
        if(!searchField.isEmpty()){
            listToDisplay = listToDisplay.stream().filter((log)->
                    log.getInitiator().toLowerCase().contains(searchField.getValue().toLowerCase())
            ).toList();
        }
        if(!datePicker.isEmpty()){
            listToDisplay = listToDisplay.stream().filter((log)->
            {
                return
                        log.getTimestamp().toLocalDateTime().getYear() == datePicker.getValue().getYear() &&
                                log.getTimestamp().toLocalDateTime().getMonth() == datePicker.getValue().getMonth() &&
                                log.getTimestamp().toLocalDateTime().getDayOfMonth() == datePicker.getValue().getDayOfMonth();


            }).toList();
        }
        logGrid.setItems(listToDisplay);
    }

    void configureLogGrid(){
        logGrid = new Grid<>(Log.class,false);
        logGrid.setItems(logService.getAllLogs());
        logGrid.addClassName("custom-grid");

        Grid.Column<Log> initiator = logGrid.addColumn(Log::getInitiator).setHeader("Initiator");
        Grid.Column<Log> action = logGrid.addColumn(Log::getAction).setHeader("Action");
        Grid.Column<Log> moduleOfAction = logGrid.addColumn(Log::getModuleOfAction).setHeader("Module");
        Grid.Column<Log> timeStamp = logGrid.addColumn(Log::getTimestamp).setHeader("Time");
        timeStamp.setSortable(true);

        logGrid.setColumnOrder(timeStamp, initiator,action,moduleOfAction);

    }
}


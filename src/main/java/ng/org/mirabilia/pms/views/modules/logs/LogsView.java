package ng.org.mirabilia.pms.views.modules.logs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.cdi.Eager;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Route(value = "logs", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
@PageTitle("Audit Logs | Property Management System")
public class LogsView extends VerticalLayout {


    LogService logService;

    List<Log> logList;

    Page<Log> logPage;

    private TextField searchField;
    private ComboBox<Action> actionFilter;
    private ComboBox<Module> moduleFilter;
    private Button resetButton;
    private DatePicker datePicker;
    Grid<Log> logGrid;
    Button next;
    Button prev;
    Paragraph totalPages;
    TextField currentPage;
    HorizontalLayout bottomToolBar;

    @Autowired
    public LogsView(LogService logService) {
        System.out.println("Log View Construction");
        this.logService = logService;

        logPage = logService.getFilteredLogs(null,null,null,null,0,20);
        logList = logPage.getContent();

        setPadding(false);
        setSpacing(true);
        setHeightFull();
        configureLogGrid();
        configureSearchHeader();
        configureActionFilter();
        configureModuleFilter();
        configureResetButton();
        configureDatePicker();
        configureNextButton();
        configurePrevButton();
        configurePageInfo();
        updateNavIU();
        configureBottomToolbar();

        HorizontalLayout toolbar = new HorizontalLayout(searchField, actionFilter, moduleFilter,datePicker, resetButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");
        toolbar.getStyle().setMarginTop("8px");

        add(toolbar,logGrid, bottomToolBar);
    }

    private void configureBottomToolbar() {
        bottomToolBar = new HorizontalLayout();
        bottomToolBar.setAlignItems(Alignment.BASELINE);
        bottomToolBar.setJustifyContentMode(JustifyContentMode.CENTER);
        bottomToolBar.add(prev, next, currentPage, totalPages);
    }

    private void configurePageInfo() {
        currentPage = new TextField();
        currentPage.setPlaceholder("1");
        currentPage.addValueChangeListener((
                e)->{
            if(!e.getValue().isEmpty()){
                goToCurrentPage(Integer.parseInt(e.getValue()) - 1);
                System.out.println("Not empty"+ (Integer.parseInt(e.getValue()) - 1));
            }else{
                currentPage.setPlaceholder("1");
                goToCurrentPage(0);
                System.out.println("Empty field");
            }
        });

        currentPage.setValueChangeMode(ValueChangeMode.EAGER);


        totalPages = new Paragraph(" of " + (logPage.getTotalPages() == 0 ? "1" : logPage.getTotalPages()+""));
    }

    private void goToCurrentPage(Integer integer) {
        System.out.println("log page: "+logPage);
        System.out.println("log pageable: " + logPage.getPageable());
        Timestamp timestamp = null;
        if(!datePicker.isEmpty()){
            timestamp = Timestamp.valueOf(LocalDateTime.of(datePicker.getValue(), LocalTime.of(23,59,59)));
        }
        logPage = logService.getFilteredLogs(searchField.getValue(),actionFilter.getValue(),
                moduleFilter.getValue(),
                timestamp, integer,logPage.getPageable().getPageSize());

        logGrid.setItems(logPage.getContent());

        System.out.println("after log page: "+logPage);
        System.out.println("after log pageable: " + logPage.getPageable());

        updateNavIU();

        totalPages.setText(" of " + logPage.getTotalPages());
        System.out.println("Hello"+ (logPage.getNumber() + 1) + " of " + logPage.getTotalPages());

    }

    private void updateNavIU() {
        //Determine to show Next Button
        if(logPage.isLast()){
            next.setEnabled(false);
            System.out.println("is First true");
        }else{
            next.setEnabled(true);
        }
        //Determine to show Prev Button
        if(logPage.isFirst()){
            prev.setEnabled(false);
            System.out.println("is First true");
        }else{
            prev.setEnabled(true);
        }
    }

    private void configureNextButton() {
        next = new Button("Next");
        next.addClickListener((e)->{
            nextPage();
        });
        next.addClassName("custom-add-button");
        next.addClassName("custom-button");
        next.addClassName("custom-toolbar-button");
        next.getStyle().setMarginLeft("8px");

    }
    private void configurePrevButton() {
        prev = new Button("Prev");
        prev.addClickListener((e)->{
            prevPage();
        });
        prev.addClassName("custom-add-button");
        prev.addClassName("custom-button");
        prev.addClassName("custom-toolbar-button");
        prev.getStyle().setMarginLeft("8px");

     //Determine to make visible ShowMore Button
     if(logPage.isFirst()){
         prev.setEnabled(false);
         System.out.println("is First true");
     }else{
         prev.setEnabled(true);
     }

    }

    private void configureDatePicker() {
        datePicker = new DatePicker("Date");
        datePicker.addClassName("custom-search-field");
        datePicker.addValueChangeListener((date)->{
            updateGrid2();
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
        logGrid.setItems(logPage.getContent());
    }

    private void configureModuleFilter() {
        moduleFilter = new ComboBox<>("Module", Module.values());
        moduleFilter.addValueChangeListener(e->updateGrid2());
        moduleFilter.addClassName("custom-search-field");
    }

    private void configureActionFilter() {
        actionFilter = new ComboBox<>("Action", Action.values());
        actionFilter.addClassName("custom-search-field");
        actionFilter.addValueChangeListener(e-> updateGrid2());
    }

    private void configureSearchHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Search by Initiator");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.addClassName("custom-toolbar-field");

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid2());
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

    public void updateGrid2(){
        currentPage.setPlaceholder("1");
        currentPage.clear();

        //Go to first page
        goToCurrentPage(0);
        //Determine to make visible ShowMore Button
        if(logPage.isLast()){
            next.setVisible(false);
            System.out.println("is Last true");
        }else{
            next.setVisible(true);
        }

        totalPages.setText(logPage.getNumber()+1 + " of " + logPage.getTotalPages());
    }

    public void nextPage(){
        currentPage.setPlaceholder((Integer.parseInt(currentPage.getPlaceholder()) + 1) + "");
        currentPage.clear();
        goToCurrentPage(logPage.getNumber()+1);
        totalPages.setText(" of " + logPage.getTotalPages());
        System.out.println("Hello"+ (logPage.getNumber() + 1) + " of " + logPage.getTotalPages());

    }
    public void prevPage(){
        currentPage.setPlaceholder((Integer.parseInt(currentPage.getPlaceholder()) - 1) + "");
        currentPage.clear();
        goToCurrentPage(logPage.getNumber()-1);
        totalPages.setText(" of " + logPage.getTotalPages());
    }

    void configureLogGrid(){
        logGrid = new Grid<>(Log.class,false);
        //logGrid.setItems(logService.getAllLogs());
        logGrid.setItems(logPage.getContent());
        logGrid.addClassName("custom-grid");

        Grid.Column<Log> initiator = logGrid.addColumn(Log::getInitiator).setHeader("Initiator");
        Grid.Column<Log> action = logGrid.addComponentColumn((log)->{
           Div actionAndInfoDiv = new Div();
           actionAndInfoDiv.getStyle().setDisplay(Style.Display.FLEX);
           actionAndInfoDiv.getStyle().setAlignItems(Style.AlignItems.CENTER);

            Paragraph act = new Paragraph(log.getAction().name());
            Paragraph info = new Paragraph(log.getInfo());
            info.getStyle().setMarginLeft("4px");

            actionAndInfoDiv.add(act,info);
            return actionAndInfoDiv;
        }).setHeader("Action");
        Grid.Column<Log> moduleOfAction = logGrid.addColumn(Log::getModuleOfAction).setHeader("Module");
        Grid.Column<Log> timeStamp = logGrid.addColumn(Log::getTimestamp).setHeader("Time");
        timeStamp.setSortable(true);

        logGrid.setColumnOrder(timeStamp, initiator,action,moduleOfAction);

    }
}


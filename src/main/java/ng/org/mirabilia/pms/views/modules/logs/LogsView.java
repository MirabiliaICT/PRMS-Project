package ng.org.mirabilia.pms.views.modules.logs;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "logs", layout = MainView.class)
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
@PageTitle("Audit Logs | Property Management System")
public class LogsView extends VerticalLayout {

    LogService logService;
    Grid<Log> logGrid;

    @Autowired
    public LogsView(LogService logService) {
        this.logService = logService;
        setPadding(false);
        setHeightFull();
        configureLogGrid();
        add(logGrid);
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


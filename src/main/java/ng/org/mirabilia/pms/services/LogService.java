package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface LogService{
    Log addLog(Log log);
    List<Log> getAllLogs();
    public Page<Log> getFilteredLogs(String initiator, Action action, Module moduleOfAction, Timestamp timestamp, int page, int size);

}

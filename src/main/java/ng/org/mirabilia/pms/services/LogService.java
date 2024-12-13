package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.Log;

import java.util.List;

public interface LogService{
    Log addLog(Log log);
    List<Log> getAllLogs();

}

package ng.org.mirabilia.pms.services.implementations;

import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.repositories.LogRepository;
import ng.org.mirabilia.pms.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    LogRepository logRepository;
    @Autowired
    LogServiceImpl(LogRepository logRepository){
        this.logRepository = logRepository;
    }
    @Override
    public Log addLog(Log log) {
        return logRepository.save(log);
    }
    @Override
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}

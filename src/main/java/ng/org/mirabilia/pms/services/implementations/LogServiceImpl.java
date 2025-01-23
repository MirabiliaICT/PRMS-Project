package ng.org.mirabilia.pms.services.implementations;

import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import ng.org.mirabilia.pms.repositories.LogRepository;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.services.queryspecifications.LogSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    public Page<Log> getFilteredLogs(String initiator, Action action, Module moduleOfAction, Timestamp timestamp, int page, int size) {
        // Create a specification with dynamic filtering
        Specification<Log> spec = Specification
                .where(LogSpecification.hasInitiator(initiator))
                .and(LogSpecification.hasAction(action))
                .and(LogSpecification.hasModuleOfAction(moduleOfAction))
                .and(LogSpecification.hasTimestampAfter(timestamp));

        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Find the data using specification and pagination
        return logRepository.findAll(spec, pageable);
    }


}

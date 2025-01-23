package ng.org.mirabilia.pms.services.queryspecifications;

import ng.org.mirabilia.pms.domain.entities.Log;
import ng.org.mirabilia.pms.domain.enums.Action;
import ng.org.mirabilia.pms.domain.enums.Module;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDate;

public class LogSpecification {

    // Filter by initiator
    public static Specification<Log> hasInitiator(String initiator) {
        return (root, query, criteriaBuilder) ->
            initiator == null || initiator.isEmpty() ? criteriaBuilder.conjunction() :
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("initiator")), "%" + initiator.toLowerCase() + "%");

    }

    // Filter by action (create, update, delete)
    public static Specification<Log> hasAction(Action action) {
        return (root, query, criteriaBuilder) -> 
            action == null ? criteriaBuilder.conjunction() : 
            criteriaBuilder.equal(root.get("action"), action);
    }

    // Filter by moduleOfAction (AUTH, PAYMENTS, etc.)
    public static Specification<Log> hasModuleOfAction(Module moduleOfAction) {
        return (root, query, criteriaBuilder) -> 
            moduleOfAction == null ? criteriaBuilder.conjunction() : 
            criteriaBuilder.equal(root.get("moduleOfAction"), moduleOfAction);
    }

    // Filter by timestamp range (optional)
    public static Specification<Log> hasTimestampAfter(Timestamp timestamp) {
        return (root, query, criteriaBuilder) -> 
            timestamp == null ? criteriaBuilder.conjunction() :
            criteriaBuilder.equal(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("timestamp")), timestamp.toLocalDateTime().toLocalDate()
            );
    }
}

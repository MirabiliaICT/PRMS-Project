package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log,Long> {

}

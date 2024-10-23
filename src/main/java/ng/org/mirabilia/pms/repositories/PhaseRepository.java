package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    @Query("SELECT p FROM Phase p " +
            "JOIN p.city c " +
            "JOIN c.state s " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.phaseCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Phase> findByNameContainingIgnoreCaseOrPhaseCodeContainingIgnoreCaseOrCityNameOrStateName(@Param("keyword") String keyword);
    List<Phase> findByCity_Id(Long cityId);
    List<Phase> findByCity_State_Id(Long stateId);
    boolean existsByName(String name);
    boolean existsByPhaseCode(String phaseCode);
    List<Phase> findByCity_Name(String cityName);
    Optional<Phase> findByName(String name);



}

package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.models.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByNameContainingIgnoreCaseOrPhaseCodeContainingIgnoreCase(String name, String phaseCode);
    List<Phase> findByCity_Id(Long cityId);
    List<Phase> findByCity_State_Id(Long stateId);
    @Modifying
    @Query("DELETE FROM Phase p WHERE p.id = :id")
    void deletePhaseById(@Param("id") Long id);
}

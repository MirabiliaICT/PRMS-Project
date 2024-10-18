package ng.org.mirabilia.pms.services;
import ng.org.mirabilia.pms.entity.Phase;
import java.util.List;

public interface PhaseService {
    Phase addPhase(Phase phase);
    void deletePhase(Long id);
    List<Phase> getAllPhases();
    Phase editPhase(Phase phase);
    List<Phase> searchPhaseByKeywords(String keyword);
    List<Phase> filterPhasesByState(Long stateId);
    List<Phase> filterPhasesByCity(Long cityId);
    boolean phaseExists(String name, String phaseCode);

}

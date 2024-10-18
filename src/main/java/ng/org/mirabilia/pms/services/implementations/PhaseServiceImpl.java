package ng.org.mirabilia.pms.services.implementations;
import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.models.Phase;
import ng.org.mirabilia.pms.repositories.PhaseRepository;
import ng.org.mirabilia.pms.services.PhaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PhaseServiceImpl implements PhaseService {

    @Autowired
    private PhaseRepository phaseRepository;

    @Override
    public Phase addPhase(Phase phase) {
        return phaseRepository.save(phase);
    }

    @Override
    @Transactional
    public void deletePhase(Long id) {
        // Ensure that the phase exists before deleting
        if (!phaseRepository.existsById(id)) {
            throw new IllegalStateException("Phase not found");
        }

        // Delete the phase using the custom query
        phaseRepository.deletePhaseById(id);
    }

    @Override
    public List<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }

    @Override
    public Phase editPhase(Phase phase) {
        return phaseRepository.save(phase);
    }

    @Override
    public List<Phase> searchPhaseByKeywords(String keyword) {
        return phaseRepository.findByNameContainingIgnoreCaseOrPhaseCodeContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Phase> filterPhasesByState(Long stateId) {
        return phaseRepository.findByCity_State_Id(stateId);
    }

    @Override
    public List<Phase> filterPhasesByCity(Long cityId) {
        return phaseRepository.findByCity_Id(cityId);
    }
}


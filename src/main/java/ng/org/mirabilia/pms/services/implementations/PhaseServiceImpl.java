package ng.org.mirabilia.pms.services.implementations;
import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.Phase;
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
    public void deletePhase(Long id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Phase not found"));

        if (phase.getProperties() == null || phase.getProperties().isEmpty()) {
            phaseRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Cannot delete a phase that has properties.");
        }
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
        return phaseRepository.findByNameContainingIgnoreCaseOrPhaseCodeContainingIgnoreCaseOrCityNameOrStateName(keyword);
    }

    @Override
    public List<Phase> filterPhasesByState(Long stateId) {
        return phaseRepository.findByCity_State_Id(stateId);
    }

    @Override
    public List<Phase> filterPhasesByCity(Long cityId) {
        return phaseRepository.findByCity_Id(cityId);
    }

    @Override
    public boolean phaseExists(String name, String stateCode) {
        return phaseRepository.existsByName(name) || phaseRepository.existsByPhaseCode(stateCode);
    }

    @Override
    public List<Phase> getPhasesByCity(String cityName) {
        return phaseRepository.findByCity_Name(cityName);
    }

    @Override
    public Phase getPhaseByName(String name) {
        return phaseRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Phase not found with name: " + name));
    }

}


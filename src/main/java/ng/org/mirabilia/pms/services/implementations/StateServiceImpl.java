package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.repositories.StateRepository;
import ng.org.mirabilia.pms.services.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepository stateRepository;

    @Override
    public State addState(State state) {
        return stateRepository.save(state);
    }
    @Override
    public void deleteState(Long id) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("State not found"));
        if (state.getCities() == null || state.getCities().isEmpty()) {
            stateRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Cannot delete a state that has cities.");
        }
    }

    @Override
    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    @Override
    public State editState(State state) {
        return stateRepository.save(state);
    }

    @Override
    public List<State> searchStateByKeywords(String keyword) {
        return stateRepository.findByNameContainingIgnoreCaseOrStateCodeContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public boolean stateExists(String name, String stateCode) {
        return stateRepository.existsByName(name) || stateRepository.existsByStateCode(stateCode);
    }
}

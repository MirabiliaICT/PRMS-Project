package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.State;
import java.util.List;

public interface StateService {
    State addState(State state);
    void deleteState(Long id);
    List<State> getAllStates();
    State editState(State state);
    List<State> searchStateByKeywords(String keyword);
    boolean stateExists(String name, String stateCode);
    State getStateByName(String name);

}

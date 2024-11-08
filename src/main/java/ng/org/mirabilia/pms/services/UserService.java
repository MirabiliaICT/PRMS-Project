package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User addUser(User user);
    void updateUserWithPassword(User user);
    void deleteUser(Long id);
    User findByUsername(String username);
    boolean userExistsByEmail(String email);
    boolean userExistsByUsername(String username);

    boolean userExistsByPhoneNumber(String username);

    List<User> searchUsersByFilters(String keyword, Role role);
    List<User> getAgents();
    List<User> getClients();
    Long getAgentIdByName(String fullName);
    Long getClientIdByName(String fullName);

}

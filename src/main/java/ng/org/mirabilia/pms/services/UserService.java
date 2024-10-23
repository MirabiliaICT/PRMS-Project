package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(UUID id);
    void addUser(User user);
    void updateUserWithPassword(User user);
    void deleteUser(UUID id);
    User findByUsername(String username);
    boolean userExistsByEmail(String email);
    boolean userExistsByUsername(String username);
    List<User> searchUsersByFilters(String keyword, Role role);
    List<User> getAgents();
    List<User> getClients();
    UUID getAgentIdByName(String fullName);
    UUID getClientIdByName(String fullName);

}

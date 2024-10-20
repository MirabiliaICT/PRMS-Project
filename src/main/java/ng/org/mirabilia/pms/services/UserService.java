package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(UUID id);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(UUID id);
    boolean userExistsByEmail(String email);
    boolean userExistsByUsername(String username);
    List<User> searchUsersByFilters(String keyword, Role role);
}

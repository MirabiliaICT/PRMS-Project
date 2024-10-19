package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    boolean userExistsByEmail(String email);
    boolean userExistsByUsername(String username);
    List<User> searchUsersByFilters(String keyword, Role role);
}

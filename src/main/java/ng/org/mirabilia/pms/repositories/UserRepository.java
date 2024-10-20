package ng.org.mirabilia.pms.repositories;
import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByRolesContaining(Role role);
    List<User> findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String firstName, String middleName, String lastName, String email, String username);
    List<User> findByRolesIn(List<String> roles);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}

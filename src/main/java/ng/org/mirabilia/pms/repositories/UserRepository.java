package ng.org.mirabilia.pms.repositories;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phone);
    boolean existsByRolesContaining(Role role);
    List<User> findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String firstName, String middleName, String lastName, String email, String username);
    List<User> findByRolesIn(List<String> roles);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserCode(String userCode);

    Optional<User> findById(Long id);

//    void deleteById(UUID id);

//    Optional<User> findById(UUID id);
}
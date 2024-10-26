package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.repositories.UserRepository;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void updateUserWithPassword(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(userRepository.findById(user.getId())
                    .map(User::getPassword)
                    .orElseThrow(() -> new IllegalArgumentException("User not found")));
        }
        userRepository.save(user);
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> searchUsersByFilters(String keyword, Role role) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                keyword, keyword, keyword, keyword, keyword);

        if (role != null) {
            return users.stream()
                    .filter(user -> user.getRoles().contains(role))
                    .collect(Collectors.toList());
        }

        return users;
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }


    @Override
    public List<User> getAgents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Role.AGENT))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getClients() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Role.CLIENT))
                .collect(Collectors.toList());
    }

    @Override
    public Long getAgentIdByName(String fullName) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Role.AGENT) &&
                        (user.getFirstName() + " " + user.getLastName()).equals(fullName))
                .map(User::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with name: " + fullName));
    }

    @Override
    public Long getClientIdByName(String fullName) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Role.CLIENT) &&
                        (user.getFirstName() + " " + user.getLastName()).equals(fullName))
                .map(User::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Client not found with name: " + fullName));
    }





}

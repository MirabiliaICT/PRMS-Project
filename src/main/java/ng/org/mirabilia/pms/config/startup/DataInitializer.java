package ng.org.mirabilia.pms.config.startup;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            if (!userRepository.existsByRolesContaining(Role.ADMIN)) {
                User admin = new User();
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setMiddleName("-");
                admin.setEmail("admin@examle.com");
                admin.setUsername("admin");
                admin.setPhoneNumber("1234567890");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles(Set.of(Role.ADMIN));
                userRepository.save(admin);
            }
        };
    }
}


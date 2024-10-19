package ng.org.mirabilia.pms.services.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    private final AuthenticationContext authContext;

    public SecurityService(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    public void logout() {
        authContext.logout();
    }
}

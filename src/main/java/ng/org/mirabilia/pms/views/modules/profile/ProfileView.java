package ng.org.mirabilia.pms.views.modules.profile;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.repositories.UserImageRepository;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.profile.content.ProfileContent;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Route(value = "profile", layout = MainView.class)
@PageTitle("Profile | Property Management System")
@RolesAllowed({"ADMIN","MANAGER","AGENT","ACCOUNTANT", "CLIENT", "IT_SUPPORT"})
public class ProfileView extends VerticalLayout {

    @Autowired
    public ProfileView(UserService userService, AuthenticationContext authContext, UserImageService userImageService) {
        setSpacing(true);
        setPadding(false);

        ProfileContent profileContent = new ProfileContent(userService, authContext,userImageService);
        add(profileContent);
    }
}

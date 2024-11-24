package ng.org.mirabilia.pms;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@PWA(name = "Property Management System",
        shortName = "PrMS",
        description = "A Property Management System, developed by Mirabilia Nigeria Limited",
        iconPath = "icons/pwa-icon.png",
        offlineResources = { "icons/pwa-icon.png" })

@Theme("my-theme")
public class Application implements AppShellConfigurator {

    public static String globalLoggedInUsername = null;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

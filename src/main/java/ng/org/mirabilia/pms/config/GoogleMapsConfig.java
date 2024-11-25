package ng.org.mirabilia.pms.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleMapsConfig {

    @Value("${google.maps-api_key}")
    public String googleMapsApiKey;

    private static String GOOGLE_MAPS_API_KEY;

    @PostConstruct
    private void init() {
        GOOGLE_MAPS_API_KEY = googleMapsApiKey;
    }

    public static String getGoogleMapsApiKey() {
        return GOOGLE_MAPS_API_KEY;
    }
}

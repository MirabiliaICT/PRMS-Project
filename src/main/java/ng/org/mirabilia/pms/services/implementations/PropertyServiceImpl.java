package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.PropertyRepository;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private final PropertyRepository propertyRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final DataSource dataSource;


    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    @Override
    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public List<Property> searchPropertiesByFilters(String keyword, String state, String city, String phase,
                                                    PropertyType propertyType, PropertyStatus propertyStatus,
                                                    String agentName, String clientName) {
        List<Property> properties = propertyRepository.findByStreetContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        if (state != null) {
            properties = properties.stream()
                    .filter(property -> property.getPhase() != null &&
                            property.getPhase().getCity() != null &&
                            property.getPhase().getCity().getState() != null &&
                            property.getPhase().getCity().getState().getName().equalsIgnoreCase(state))
                    .collect(Collectors.toList());
        }

        if (city != null) {
            properties = properties.stream()
                    .filter(property -> property.getPhase() != null &&
                            property.getPhase().getCity() != null &&
                            property.getPhase().getCity().getName().equalsIgnoreCase(city))
                    .collect(Collectors.toList());
        }

        if (phase != null && !phase.isEmpty()) {
            properties = properties.stream()
                    .filter(property -> property.getPhase() != null &&
                            property.getPhase().getName().equalsIgnoreCase(phase))
                    .collect(Collectors.toList());
        }


        if (propertyType != null) {
            properties = properties.stream()
                    .filter(property -> property.getPropertyType() != null &&
                            property.getPropertyType() == propertyType)
                    .collect(Collectors.toList());
        }

        if (propertyStatus != null) {
            properties = properties.stream()
                    .filter(property -> property.getPropertyStatus() != null &&
                            property.getPropertyStatus() == propertyStatus)
                    .collect(Collectors.toList());
        }

        if (agentName != null) {
            Long agentId = getUserIdByName(agentName);
            if (agentId != null) {
                properties = properties.stream()
                        .filter(property -> property.getAgentId() != null &&
                                property.getAgentId().equals(agentId))
                        .collect(Collectors.toList());
            }
        }

        if (clientName != null) {
            Long clientId = getUserIdByName(clientName);
            if (clientId != null) {
                properties = properties.stream()
                        .filter(property -> property.getClientId() != null &&
                                property.getClientId().equals(clientId))
                        .collect(Collectors.toList());
            }
        }

        return properties;
    }

    @Override
    public List<Property> searchPropertiesByFiltersWithoutUsers(String keyword, String state, String city, String phase,
                                                                PropertyType propertyType, PropertyStatus propertyStatus){
        List<Property> properties = propertyRepository.findByStreetContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);

        if (state != null) {
            properties = properties.stream()
                    .filter(property -> property.getPhase().getCity().getState().getName().equalsIgnoreCase(state))
                    .collect(Collectors.toList());
        }

        if (city != null) {
            properties = properties.stream()
                    .filter(property -> property.getPhase().getCity().getName().equalsIgnoreCase(city))
                    .collect(Collectors.toList());
        }

        if (phase != null) {
            properties = properties.stream()
                    .filter(property -> property.getPhase().getName().equalsIgnoreCase(phase))
                    .collect(Collectors.toList());
        }

        if (propertyType != null) {
            properties = properties.stream()
                    .filter(property -> property.getPropertyType() == propertyType)
                    .collect(Collectors.toList());
        }

        if (propertyStatus != null) {
            properties = properties.stream()
                    .filter(property -> property.getPropertyStatus() == propertyStatus)
                    .collect(Collectors.toList());
        }

        return properties;

    }



    private Long getUserIdByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        List<User> users = userService.getAllUsers();
        return users.stream()
                .filter(user -> (user.getFirstName() + " " + user.getLastName()).equalsIgnoreCase(name))
                .map(User::getId)
                .findFirst()
                .orElse(null);
    }


}

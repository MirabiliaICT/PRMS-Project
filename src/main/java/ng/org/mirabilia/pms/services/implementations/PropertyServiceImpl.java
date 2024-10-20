package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.entities.Property;
import ng.org.mirabilia.pms.entities.User;
import ng.org.mirabilia.pms.entities.enums.PropertyStatus;
import ng.org.mirabilia.pms.entities.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.PropertyRepository;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElse(null);
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

        if (agentName != null) {
            UUID agentId = getUserIdByName(agentName);
            properties = properties.stream()
                    .filter(property -> property.getAgentId().equals(agentId))
                    .collect(Collectors.toList());
        }

        if (clientName != null) {
            UUID clientId = getUserIdByName(clientName);
            properties = properties.stream()
                    .filter(property -> property.getClientId() != null && property.getClientId().equals(clientId))
                    .collect(Collectors.toList());
        }

        return properties;
    }


    private UUID getUserIdByName(String name) {
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

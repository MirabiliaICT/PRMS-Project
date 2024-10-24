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
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
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

//    public String getBase64Image(Long oid) {
//        byte[] imageBytes = getPropertyImage(oid);
//        return Base64.getEncoder().encodeToString(imageBytes);
//    }
//
//    public byte[] getPropertyImage(Long oid) {
//        byte[] imageBytes = null;
//        try (Connection connection = dataSource.getConnection()) {
//
//            LargeObjectManager lobj = ((org.postgresql.jdbc.PgConnection) connection).getLargeObjectAPI();
//            LargeObject obj = lobj.open(oid, LargeObjectManager.READ); // Open for reading
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//
//            while ((bytesRead = obj.read(buffer, 0, buffer.length)) > 0) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            obj.close();
//            imageBytes = outputStream.toByteArray();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return imageBytes;
//    }
}

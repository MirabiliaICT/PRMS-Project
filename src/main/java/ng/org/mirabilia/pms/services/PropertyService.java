package ng.org.mirabilia.pms.services;

import com.github.javaparser.metamodel.OptionalProperty;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getAllProperties();
    Optional<Property> getPropertyById(Long id);
    Property saveProperty(Property property);
    void deleteProperty(Long id);
    List<Property> searchPropertiesByFilters(String keyword, String state, String city, String phase,
                                             PropertyType propertyType, PropertyStatus propertyStatus,
                                             String agentName, String clientName);
    List<Property> searchPropertiesByFiltersWithoutUsers(String keyword, String state, String city, String phase,
                                                         PropertyType propertyType, PropertyStatus propertyStatus);

}

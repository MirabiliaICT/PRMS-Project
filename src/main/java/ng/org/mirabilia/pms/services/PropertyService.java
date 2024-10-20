package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.entities.Property;
import ng.org.mirabilia.pms.entities.enums.PropertyStatus;
import ng.org.mirabilia.pms.entities.enums.PropertyType;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    List<Property> getAllProperties();
    Property getPropertyById(Long id);
    Property saveProperty(Property property);
    void deleteProperty(Long id);
    List<Property> searchPropertiesByFilters(String keyword, String state, String city, String phase,
                                             PropertyType propertyType, PropertyStatus propertyStatus,
                                             String agentName, String clientName);
}

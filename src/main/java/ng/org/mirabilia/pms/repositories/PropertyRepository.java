package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByStreetContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String street, String description);

    List<Property> findByPhase_City_State_NameAndPhase_City_NameAndPhase_NameAndPropertyTypeAndPropertyStatusAndAgentIdAndClientId(
            String stateName, String cityName, String phaseName, PropertyType propertyType, PropertyStatus propertyStatus, Long agentId, Long clientId);

    List<Property> findByPhase_City_State_NameAndPhase_City_NameAndPhase_Name(
            String stateName, String cityName, String phaseName);

    List<Property> findByAgentId(Long agentId);

    List<Property> findByClientId(Long clientId);
}


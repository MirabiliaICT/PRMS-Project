package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.GltfModel;
import ng.org.mirabilia.pms.domain.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GltfModelRepository extends JpaRepository<GltfModel, Long> {
    public GltfModel findByPropertyId(Long propertyId);
    @Modifying
    @Query("DELETE FROM GltfModel g WHERE g.property.id = :propertyId")
    void deleteByPropertyId(@Param("propertyId") Long propertyId);
    @Modifying
    @Query("DELETE FROM GltfModel g WHERE g.id = :id")
    void deleteById(@Param("id") Long id);

}


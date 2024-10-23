package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.entities.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<PropertyImage, Long> {
}

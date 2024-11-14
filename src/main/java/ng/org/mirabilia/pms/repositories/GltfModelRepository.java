package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.GltfModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GltfModelRepository extends JpaRepository<GltfModel, Long> {}


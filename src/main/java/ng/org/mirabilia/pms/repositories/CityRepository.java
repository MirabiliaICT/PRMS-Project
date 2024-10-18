package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.cityCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.state.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<City> findByNameContainingIgnoreCaseOrCityCodeContainingIgnoreCaseOrStateName(String keyword);

    List<City> findByState_Id(Long stateId);

    @Modifying
    @Query("DELETE FROM City c WHERE c.id = :id AND (SELECT COUNT(p) FROM Phase p WHERE p.city.id = :id) = 0")
    int deleteCityIfNoPhases(@Param("id") Long id);
}


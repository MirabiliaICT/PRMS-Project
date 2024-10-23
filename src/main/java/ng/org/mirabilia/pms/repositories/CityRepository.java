package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    boolean existsByName(String name);
    boolean existsByCityCode(String cityCode);
    @Query("SELECT c FROM City c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.cityCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.state.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "c.state.id = :stateId")
    List<City> findByKeywordAndState(String keyword, Long stateId);
    List<City> findByState_Name(String stateName);

}


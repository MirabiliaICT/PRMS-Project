package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.models.City;
import java.util.List;

public interface CityService {
    City addCity(City city);
    void deleteCity(Long id);
    List<City> getAllCities();
    City editCity(City city);
    List<City> searchCityByKeywords(String keyword);
    List<City> filterCitiesByState(Long stateId);
}

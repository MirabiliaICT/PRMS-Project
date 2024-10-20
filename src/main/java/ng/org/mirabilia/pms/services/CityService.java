package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.entities.City;
import java.util.List;

public interface CityService {
    City addCity(City city);
    void deleteCity(Long id);
    List<City> getAllCities();
    City editCity(City city);
    List<City> searchCityByKeywords(String keyword);
    List<City> filterCitiesByState(Long stateId);
    boolean cityExists(String name, String cityCode);
    List<City> searchCityByKeywordsAndState(String keyword, Long stateId);
    List<City> getCitiesByState(String stateName);



}

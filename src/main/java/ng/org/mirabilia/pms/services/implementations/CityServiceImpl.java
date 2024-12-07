package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.City;
import ng.org.mirabilia.pms.domain.entities.State;
import ng.org.mirabilia.pms.repositories.CityRepository;
import ng.org.mirabilia.pms.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public City addCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("City not found"));

        if (city.getPhases() == null || city.getPhases().isEmpty()) {
            cityRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Cannot delete a city that has phases.");
        }
    }




    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City editCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public List<City> searchCityByKeywords(String keyword) {
        return cityRepository.findByNameContainingIgnoreCaseOrCityCodeContainingIgnoreCaseOrStateName(keyword);
    }

    @Override
    public List<City> filterCitiesByState(Long stateId) {
        return cityRepository.findByState_Id(stateId);
    }

    @Override
    public boolean cityExists(String name, String cityCode) {
        return cityRepository.existsByName(name) || cityRepository.existsByCityCode(cityCode);
    }

    @Override
    public boolean cityCodeExists(String cityCode){
        return  cityRepository.existsByCityCode(cityCode);
    }

    @Override
    public List<City> searchCityByKeywordsAndState(String keyword, Long stateId) {
        return cityRepository.findByKeywordAndState(keyword, stateId);
    }

    @Override
    public List<City> getCitiesByState(String stateName) {
        return cityRepository.findByState_Name(stateName);
    }
}

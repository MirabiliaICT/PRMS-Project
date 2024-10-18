package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.models.City;
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

//    @Override
//    public void deleteCity(Long id) {
//        // Safely fetch the city or throw an exception if not found
//        City city = cityRepository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("City not found"));
//
//        // Check if the city has phases
//        if (city.getPhases() == null || city.getPhases().isEmpty()) {
//            cityRepository.deleteById(id);  // Proceed with deletion if no phases
//        } else {
//            throw new IllegalStateException("Cannot delete a city that has phases.");
//        }
//    }

    @Override
    public void deleteCity(Long id) {
        int deletedCount = cityRepository.deleteCityIfNoPhases(id);

        if (deletedCount == 0) {
            throw new IllegalStateException("Cannot delete a city that has phases or city not found.");
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
}

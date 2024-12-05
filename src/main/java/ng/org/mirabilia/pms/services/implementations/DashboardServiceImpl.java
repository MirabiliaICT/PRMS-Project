package ng.org.mirabilia.pms.services.implementations;

import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.DashboardService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    PropertyService propertyService;

    UserService userService;

    @Autowired
    DashboardServiceImpl(PropertyService propertyService, UserService userService){
        this.propertyService = propertyService;
        this.userService = userService;
    }
    public int totalPropertiesForSale(){
        return propertyService.getAllProperties().size();
    }

    public int totalResidentialProperties(){
        List<Property> propertyList = propertyService.getAllProperties().stream().filter(
                (property)->{
                    return property.getPropertyType().equals(PropertyType.LAND);
                }
        ).toList();
        return propertyList.size();
    }

    public int totalLandedProperties(){
        List<Property> propertyList = propertyService.getAllProperties().stream().filter(
                (property)->{
                    return !property.getPropertyType().equals(PropertyType.LAND);
                }
        ).toList();
        return propertyList.size();
    }

    public int totalCustomers(){
        return userService.getAllUsers().stream().filter(
                (user)->{
                    return user.getRoles().contains(Role.CLIENT);
                }
        ).toList().size();
    }


    @Override
    public int totalPropertiesBought() {
        return 0;
    }

    @Override
    public int totalRevenue() {
        return 0;
    }

    @Override
    public int totalPaymentCompleted() {
        return 0;
    }

    @Override
    public int totalPaymentOutstanding() {
        return 0;
    }
}

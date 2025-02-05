package ng.org.mirabilia.pms.services.implementations;

import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.DashboardService;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    PropertyService propertyService;

    UserService userService;

    FinanceService financeService;

    @Autowired
    DashboardServiceImpl(PropertyService propertyService, UserService userService, FinanceService financeService){
        this.propertyService = propertyService;
        this.userService = userService;
        this.financeService = financeService;
    }
    public int totalPropertiesForSale(){
        return propertyService.getAllProperties().stream()
                .filter(property -> property.getPropertyStatus() == PropertyStatus.AVAILABLE)
                .toList().size();
    }

    public int totalResidentialProperties(){
        List<Property> propertyList = propertyService.getAllProperties().stream().filter(
                (property)->{
                    return !property.getPropertyType().equals(PropertyType.LAND);
                }
        ).toList();
        return propertyList.size();
    }

    public int totalLandedProperties(){
        List<Property> propertyList = propertyService.getAllProperties().stream().filter(
                (property)->{
                    return property.getPropertyType().equals(PropertyType.LAND);
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
    public BigDecimal totalPropertiesBought() {
        return propertyService.getAllProperties().stream()
                .filter(property -> property.getPropertyStatus() == PropertyStatus.SOLD || property.getPropertyStatus() == PropertyStatus.UNDER_OFFER)
                .map(Property::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal totalRevenue() {
        return totalPropertiesBought().subtract(totalPaymentOutstanding());
    }

    @Override
    public BigDecimal totalPaymentCompleted() {
        return financeService.getAllFinances().stream()
                .filter(finance -> finance.getPaymentStatus() == FinanceStatus.APPROVED)
                .map(Finance::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal totalPaymentOutstanding() {
        BigDecimal amountPaid = financeService.getAllFinances().stream()
                .filter(finance -> finance.getPaymentStatus() == FinanceStatus.APPROVED)
                .map(Finance::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPropertiesBought().subtract(amountPaid);

    }
}

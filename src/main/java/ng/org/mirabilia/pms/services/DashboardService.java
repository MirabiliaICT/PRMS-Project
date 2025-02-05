package ng.org.mirabilia.pms.services;


import java.math.BigDecimal;

public interface DashboardService {

    int totalPropertiesForSale();
    int totalResidentialProperties();
    int totalLandedProperties();
    int totalCustomers();

    BigDecimal totalPropertiesBought();

    BigDecimal totalRevenue();

    //total cost from invoices approved
    BigDecimal totalPaymentCompleted();

    BigDecimal totalPaymentOutstanding();

}



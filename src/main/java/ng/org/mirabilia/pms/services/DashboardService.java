package ng.org.mirabilia.pms.services;


public interface DashboardService {

    int totalPropertiesForSale();
    int totalResidentialProperties();
    int totalLandedProperties();
    int totalCustomers();

    int totalPropertiesBought();
    int totalRevenue();

    //total cost from invoices approved
    int totalPaymentCompleted();

    int totalPaymentOutstanding();

}



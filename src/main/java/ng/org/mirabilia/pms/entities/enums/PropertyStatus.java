package ng.org.mirabilia.pms.entities.enums;

public enum PropertyStatus {
    AVAILABLE,    // Generic availability status for all property types
    SOLD,         // For Land and Building Sale
    IN_INSTALLMENT, // For Building Sale

    RENTED,        // For Building Rental
    PAYMENT_DUE,   // For Building Rental when rent is overdue
    LEASE_ENDED    // For Building Rental when lease agreement has ended
}

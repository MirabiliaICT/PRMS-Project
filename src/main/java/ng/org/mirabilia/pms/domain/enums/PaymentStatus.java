package ng.org.mirabilia.pms.domain.enums;

public enum PaymentStatus {
    PENDING, PAID, PARTIALLY_PAID;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

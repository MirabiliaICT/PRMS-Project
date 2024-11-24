package ng.org.mirabilia.pms.domain.enums;

public enum PaymentMethod {
    CASH,
    BANK_TRANSFER;
    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

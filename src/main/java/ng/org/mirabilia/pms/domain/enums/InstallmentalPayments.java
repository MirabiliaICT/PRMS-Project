package ng.org.mirabilia.pms.domain.enums;

public enum InstallmentalPayments {

    IMMEDIATE_PAYMENT,
    THREE_MONTHS,
    SIX_MONTHS,
    TWELVE_MONTHS;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

package ng.org.mirabilia.pms.domain.enums;

public enum InstallmentalPayments {

    THREE_MONTHS,
    SIX_MONTHS,
    TWELVE_MONTHS;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

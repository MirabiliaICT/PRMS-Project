package ng.org.mirabilia.pms.domain.enums;

public enum PropertyStatus {
    AVAILABLE,
    SOLD,
    UNDER_OFFER;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

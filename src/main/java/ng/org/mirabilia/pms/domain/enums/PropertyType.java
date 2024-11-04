package ng.org.mirabilia.pms.domain.enums;

public enum PropertyType {
    LAND,
    SEMI_DETACHED_DUPLEX,
    SEMI_DETACHED_BUNGALOW,
    TERRACE_HOUSES,
    THREE_BEDROOM_APARTMENT,
    TWO_BEDROOM_APARTMENT,
    ONE_BEDROOM_APARTMENT,
    FLAT,
    CONDO,
    OTHER;
    public String getDisplayName() {
        return name().replace("_", " ");
    }
}

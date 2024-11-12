package ng.org.mirabilia.pms.domain.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum ExteriorDetails {
    Security(List.of("Surveillance Camera", "Smoke Detector")),
    Flooring(List.of("InterLock", "Carpet grass", "Brick Pavers", "Porcelain Tiles"));
    private final List<String> items;

    ExteriorDetails(List<String> items){
        this.items = items;
    }
}

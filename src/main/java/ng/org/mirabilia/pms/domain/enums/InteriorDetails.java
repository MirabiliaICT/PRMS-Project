package ng.org.mirabilia.pms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public enum InteriorDetails {
    Laundry(List.of("Dryer", "Washing Machine")),
    Kitchen(List.of("Built-in Cooker", "Microwave", "Refrigerator", "Dishwasher", "stove", "Smoke Detector", "Smoke extractor")),
    Flooring(List.of("Marble", "Tiles", "Concrete"));
    private final List<String> items;

    InteriorDetails(List<String> items){
        this.items = items;
    }

    public List<String> getLaundryItems() {
        return Laundry.getItems();
    }

    public List<String> getKitchenItems() {
        return Kitchen.getItems();
    }

    public List<String> getFlooringItems() {
        return Flooring.getItems();
    }

}

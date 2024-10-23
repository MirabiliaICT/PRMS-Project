package ng.org.mirabilia.pms.domain.enums;

public enum PropertyFeatures {
        SwimmingPool("Swimming pool"), Garden("Garden"), Garage("Garage");

        private final String name;

        PropertyFeatures(String name){
            this.name = name;
        }
    }
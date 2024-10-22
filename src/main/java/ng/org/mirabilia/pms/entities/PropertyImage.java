package ng.org.mirabilia.pms.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "property_image")
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
    @ManyToOne
    @JoinColumn(name = "propertyId")
    private Property property;

//    @Column(columnDefinition = "LONGBLOB")
    @Lob
    private byte[] propertyImages;

    public PropertyImage() {
    }

    public PropertyImage(Long id, Property property, byte[] propertyImages) {
        this.id = id;
        this.property = property;
        this.propertyImages = propertyImages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPropertyImages(byte[] propertyImages) {
        this.propertyImages = propertyImages;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}

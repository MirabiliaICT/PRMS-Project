package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity

@Table(name = "property_image")
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "propertyId")
    private Property property;


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

    public Long getId() {
        return id;
    }

    public Property getProperty() {
        return property;
    }

    public byte[] getPropertyImages() {
        return propertyImages;
    }
}

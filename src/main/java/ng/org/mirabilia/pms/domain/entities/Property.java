package ng.org.mirabilia.pms.entities;

import com.vaadin.flow.router.OptionalParameter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.org.mirabilia.pms.entities.enums.PropertyFeatures;
import ng.org.mirabilia.pms.entities.enums.PropertyStatus;
import ng.org.mirabilia.pms.entities.enums.PropertyType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @ManyToOne
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double size;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus propertyStatus;

    private double noOfBedrooms;

    private double noOfBathrooms;

    @Enumerated(EnumType.STRING)
    private Set<PropertyFeatures> features;

    private UUID agentId;

    private UUID clientId;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> propertyImages = new ArrayList<>();

    public void addPropertyImage(PropertyImage propertyImage) {
        propertyImages.add(propertyImage);
        propertyImage.setProperty(this);
    }

    public void removePropertyImage(PropertyImage propertyImage) {
        propertyImages.remove(propertyImage);
        propertyImage.setProperty(null);
    }

}


package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.org.mirabilia.pms.domain.enums.InstallmentalPayments;
import ng.org.mirabilia.pms.domain.enums.PropertyFeatures;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    private String title;

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

    @Column(nullable = false)
    private double size;

    @Column(nullable = false)
    private Integer unit;

    @Column(nullable = false)
    private Integer plot;

    private String propertyCode;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus propertyStatus;

    private double noOfBedrooms;

    private double noOfBathrooms;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer builtAt;

    @Enumerated(EnumType.STRING)
    private Set<PropertyFeatures> features;

    @Enumerated(EnumType.STRING)
    private InstallmentalPayments installmentalPayments;

    private Long agentId;

    private Long clientId;

    private double latitude;

    private double longitude;

    private Set<String> laundryItems = new HashSet<>();

    private Set<String> kitchenItems = new HashSet<>();

    private Set<String> interiorFlooringItems = new HashSet<>();

    private Set<String> securityItems = new HashSet<>();

    private Set<String> exteriorFlooringItems = new HashSet<>();

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GltfModel model;


    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyDocument> documents = new ArrayList<>();




    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addPropertyImage(PropertyImage propertyImage) {
        propertyImages.add(propertyImage);
        propertyImage.setProperty(this);
    }

    public void setModel(GltfModel model) {
        this.model = model;
        if (model != null) {
            model.setProperty(this);
        }
    }

    public void addDocument(PropertyDocument document) {
        documents.add(document);
        document.setProperty(this);
    }

    public void removeDocument(PropertyDocument document) {
        documents.remove(document);
        document.setProperty(null);
    }



}


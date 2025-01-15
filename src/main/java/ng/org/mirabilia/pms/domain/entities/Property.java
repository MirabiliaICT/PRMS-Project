package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
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

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GltfModel model;


    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyDocument> documents = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getPlot() {
        return plot;
    }

    public void setPlot(Integer plot) {
        this.plot = plot;
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PropertyStatus getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(PropertyStatus propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public double getNoOfBedrooms() {
        return noOfBedrooms;
    }

    public void setNoOfBedrooms(double noOfBedrooms) {
        this.noOfBedrooms = noOfBedrooms;
    }

    public double getNoOfBathrooms() {
        return noOfBathrooms;
    }

    public void setNoOfBathrooms(double noOfBathrooms) {
        this.noOfBathrooms = noOfBathrooms;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getBuiltAt() {
        return builtAt;
    }

    public void setBuiltAt(Integer builtAt) {
        this.builtAt = builtAt;
    }

    public Set<PropertyFeatures> getFeatures() {
        return features;
    }

    public void setFeatures(Set<PropertyFeatures> features) {
        this.features = features;
    }

    public InstallmentalPayments getInstallmentalPayments() {
        return installmentalPayments;
    }

    public void setInstallmentalPayments(InstallmentalPayments installmentalPayments) {
        this.installmentalPayments = installmentalPayments;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Set<String> getLaundryItems() {
        return laundryItems;
    }

    public void setLaundryItems(Set<String> laundryItems) {
        this.laundryItems = laundryItems;
    }

    public Set<String> getKitchenItems() {
        return kitchenItems;
    }

    public void setKitchenItems(Set<String> kitchenItems) {
        this.kitchenItems = kitchenItems;
    }

    public Set<String> getInteriorFlooringItems() {
        return interiorFlooringItems;
    }

    public void setInteriorFlooringItems(Set<String> interiorFlooringItems) {
        this.interiorFlooringItems = interiorFlooringItems;
    }

    public Set<String> getSecurityItems() {
        return securityItems;
    }

    public void setSecurityItems(Set<String> securityItems) {
        this.securityItems = securityItems;
    }

    public Set<String> getExteriorFlooringItems() {
        return exteriorFlooringItems;
    }

    public void setExteriorFlooringItems(Set<String> exteriorFlooringItems) {
        this.exteriorFlooringItems = exteriorFlooringItems;
    }

    public GltfModel getModel() {
        return model;
    }

    public List<PropertyImage> getPropertyImages() {
        return propertyImages;
    }

    public void setPropertyImages(List<PropertyImage> propertyImages) {
        this.propertyImages = propertyImages;
    }

    public List<PropertyDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PropertyDocument> documents) {
        this.documents = documents;
    }

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
        if (!(propertyImages instanceof ArrayList)) {
            propertyImages = new ArrayList<>(propertyImages);
        }
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


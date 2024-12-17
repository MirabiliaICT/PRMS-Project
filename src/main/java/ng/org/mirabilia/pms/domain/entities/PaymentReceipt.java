package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PaymentReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "propertyId")
    private Property property;

    @Lob
    private byte[] receiptImage;

    private LocalDateTime localDateTime;

    public PaymentReceipt() {
    }

    public PaymentReceipt(Long id, Property property, byte[] receiptImage, LocalDateTime localDateTime) {
        this.id = id;
        this.property = property;
        this.receiptImage = receiptImage;
        this.localDateTime = localDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public byte[] getReceiptImage() {
        return receiptImage;
    }

    public void setReceiptImage(byte[] receiptImage) {
        this.receiptImage = receiptImage;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}

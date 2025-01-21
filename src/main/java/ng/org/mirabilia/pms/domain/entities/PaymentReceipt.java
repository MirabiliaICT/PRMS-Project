package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
public class PaymentReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "propertyId")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Lob
    private byte[] receiptImage;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "finance_id", nullable = false)
    private Finance finance;

    private LocalDateTime localDateTime;

    public PaymentReceipt() {
    }

    public PaymentReceipt(Long id, Property property, byte[] receiptImage, LocalDateTime localDateTime) {
        this.id = id;
        this.property = property;
        this.receiptImage = receiptImage;
        this.localDateTime = localDateTime;
    }

}

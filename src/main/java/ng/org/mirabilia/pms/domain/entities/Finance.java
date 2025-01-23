package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PaymentMethod;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Data
@Entity
@Getter
@Table(name = "finances")
public class Finance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User owner;

    @Getter
    private FinanceStatus paymentStatus;

    private PaymentMethod paymentMethod;


    private String paidBy;

    @CreationTimestamp
    private LocalDate paymentDate;

    private BigDecimal amountPaid;

    private BigDecimal outstandingAmount;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @OneToOne(mappedBy = "finance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PaymentReceipt receiptImage;

    public Finance(){}

    public Finance(Long id,
                   User owner, PropertyType type, FinanceStatus paymentStatus, PaymentMethod paymentMethod,
                   BigDecimal price, String paidBy,
                   LocalDate date, BigDecimal amountPaid,
                   BigDecimal outstandingAmount, Invoice invoice) {
        this.id = id;
        this.owner = owner;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paidBy = paidBy;
        this.paymentDate = date;
        this.amountPaid = amountPaid;
        this.outstandingAmount = outstandingAmount;

        this.invoice = invoice;
    }

    public String getAmountPaidFormattedToString(){

        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(this.amountPaid);
        return nairaSymbol + formattedAmount;
    }

    public BigDecimal updateOutstandingAmount() {
        outstandingAmount = invoice.getPropertyPrice();
        if (getPaymentStatus() == FinanceStatus.APPROVED) {
            BigDecimal prev;
            prev = invoice.getPropertyPrice().subtract(this.amountPaid);
            outstandingAmount = prev.subtract(amountPaid);
            return outstandingAmount;
        } else if (getPaymentStatus() == FinanceStatus.PENDING) {
            return outstandingAmount;
        }
        return outstandingAmount;
    }

    public String getOutstandingFormattedToString(){
        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        updateOutstandingAmount();
        String formattedAmount = decimalFormat.format(outstandingAmount);
        return nairaSymbol + formattedAmount;
    }



}

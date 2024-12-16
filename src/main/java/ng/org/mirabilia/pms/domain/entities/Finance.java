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
    private Phase phase;

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
    private Invoice invoice;

    @ManyToOne(cascade = CascadeType.ALL)
    private PaymentReceipt receiptImage;


    public Finance(){}

    public Finance(Long id, Phase phase,
                   User owner, PropertyType type, FinanceStatus paymentStatus, PaymentMethod paymentMethod,
                   BigDecimal price, String paidBy,
                   LocalDate date, BigDecimal amountPaid,
                   BigDecimal outstandingAmount, Invoice invoice) {
        this.id = id;
        this.phase = phase;
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
        outstandingAmount = invoice.getPropertyPrice().subtract(this.amountPaid);
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

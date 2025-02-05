package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PaymentMethod;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private LocalDateTime paymentDate;

    private BigDecimal amountPaid;

    private BigDecimal outstandingAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(cascade = CascadeType.ALL)
    private PaymentReceipt receiptImage;

    private BigDecimal price;

    public Finance(){}

    public Finance(Long id, Phase phase,
                   User owner, FinanceStatus paymentStatus, PaymentMethod paymentMethod,
                   String paidBy, LocalDateTime date, BigDecimal amountPaid,
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

        this.price = invoice.getPropertyPrice();
    }

    public String getAmountPaidFormattedToString(){
        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(this.amountPaid);
        return nairaSymbol + formattedAmount;
    }
/*
   public BigDecimal updateOutstandingAmount() {
        if(!outstandingAmount.equals(null)) {
            price = outstandingAmount;
            outstandingAmount = price.subtract(this.amountPaid);
        } else if (outstandingAmount.equals(0)) {
            outstandingAmount.equals(0);
        } else{
            outstandingAmount = price.subtract(this.amountPaid);
        }

        return outstandingAmount;
    }*/

    public BigDecimal updateOutstandingAmounts() {

        if (invoice.isPriceInitialized() == false) {
            // First-time initialization: set price from invoice.
            price = invoice.getPropertyPrice();
            invoice.setPriceInitialized(true);

            BigDecimal paidAmount = this.amountPaid != null ? this.amountPaid : BigDecimal.ZERO;
            outstandingAmount = price.subtract(paidAmount);

            //assigns outstanding amount as the new price
            invoice.setNewPrice(outstandingAmount);

        } else if (invoice.isPriceInitialized() == true) {

            BigDecimal paidAmount = this.amountPaid != null ? this.amountPaid : BigDecimal.ZERO;
            BigDecimal newPrice = invoice.getNewPrice();
            outstandingAmount = newPrice.subtract(paidAmount);

            //assigns outstanding amount as the new price
            invoice.setNewPrice(outstandingAmount);
        }

        // Ensure outstanding amount is not negative.
        if (outstandingAmount.compareTo(BigDecimal.ZERO) < 0) {
            outstandingAmount = BigDecimal.ZERO;
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        }
        return outstandingAmount;
    }


    public String getOutstandingFormattedToString(){
        String nairaSymbol = "₦";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(outstandingAmount);
        return nairaSymbol + formattedAmount;
    }



}

package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;
    private LocalDate dueDate;
    private InvoiceStatus invoiceStatus;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    public Installment(BigDecimal price, LocalDate dueDate, InvoiceStatus invoiceStatus, Invoice invoice) {
        this.price = price;
        this.dueDate = dueDate;
        this.invoiceStatus = invoiceStatus;
        this.invoice = invoice;
    }
}

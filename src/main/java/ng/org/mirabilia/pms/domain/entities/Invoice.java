package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
//@Entity
//@Table(name = "invoices")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"property"})
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false)
    private Long invoiceId;
    @NotNull
    @Column(nullable = false)
    private LocalDate issueDate;
    @NotNull
    @Column(nullable = false)
    private InvoiceStatus status;
    @NotNull
    @Column(nullable = false)
    private Property property;
    @NotNull
    @Column(nullable = false)
    private String issuedBy;
    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;
    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;


}

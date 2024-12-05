package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private String invoiceCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate issueDate; // Default value

    @NotNull
    @Column(nullable = false)
    private InvoiceStatus invoiceStatus;

    @NotNull
    @OneToOne
    private Property propertyCode;

    @NotNull
    @Column(nullable = false)
    private String createdBy;

    @NotNull
    @JoinColumn(name = "clientName", nullable = false)
    @ManyToOne
    private User clientName;

    @NotNull
    @Column(nullable = false)
    private BigDecimal propertyPrice;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @NotNull
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Installment> installmentalPaymentList;

    @NotNull
    @Column(nullable = false)
    private String propertyTitle;

    @NotNull
    @Column(nullable = false)
    private PropertyType propertyType;

}

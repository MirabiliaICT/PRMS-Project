package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ng.org.mirabilia.pms.domain.enums.PaymentStatus;

import java.time.LocalDate;

@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceId;

    private LocalDate issueDate;

    private PaymentStatus status;

    private Property property;

    private String issuedBy;

    private LocalDate dueDate;
}

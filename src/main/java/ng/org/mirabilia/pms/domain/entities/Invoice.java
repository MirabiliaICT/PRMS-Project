package ng.org.mirabilia.pms.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
//@Table(name = "invoices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Property getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(Property propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public User getClientName() {
        return clientName;
    }

    public void setClientName(User clientName) {
        this.clientName = clientName;
    }

    public BigDecimal getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(BigDecimal propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<Installment> getInstallmentalPaymentList() {
        return installmentalPaymentList;
    }

    public void setInstallmentalPaymentList(List<Installment> installmentalPaymentList) {
        this.installmentalPaymentList = installmentalPaymentList;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }
}

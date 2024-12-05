package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE i.issueDate = :issueDate ORDER BY i.issueDate DESC")
    List<Invoice> findInvoicesByDate(@Param("issueDate") LocalDate issueDate);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus = :invoiceStatus")
    List<Invoice> findInvoicesByStatus(@Param("invoiceStatus") InvoiceStatus invoiceStatus);

    boolean existsByPropertyCode(Property propertyCode);


}



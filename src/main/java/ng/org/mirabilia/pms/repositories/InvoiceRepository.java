package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
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

    @Query("SELECT i FROM Invoice i WHERE i.invoiceCode LIKE %:keyword%")
    List<Invoice> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT i FROM Invoice i WHERE " +
            "(:keyword IS NULL OR LOWER(i.propertyTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:date IS NULL OR i.issueDate = :date) " +
            "AND (:status IS NULL OR i.invoiceStatus = :status) " +
            "AND (:propertyType IS NULL OR i.propertyType = :propertyType) " +
            "AND i.userNameOrUserCode.id = :userId")
    List<Invoice> findInvoicesByUserId(@Param("keyword") String keyword,
                                       @Param("date") LocalDate date,
                                       @Param("status") InvoiceStatus status,
                                       @Param("propertyType") PropertyType propertyType,
                                       @Param("userId") Long userId);


    boolean existsByPropertyCode(Property propertyCode);
    @Query("SELECT i FROM Invoice i WHERE i.userNameOrUserCode.id = :userId")
    List<Invoice> findByUserNameOrUserCodeId(@Param("userId") Long userId);
    @Query("SELECT i FROM Invoice i WHERE i.userNameOrUserCode = :userNameOrUserCode")
    List<Invoice> findByUser(@Param("userNameOrUserCode")User userNameOrUserCode);



}



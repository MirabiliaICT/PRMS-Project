package ng.org.mirabilia.pms.repositories;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptImageRepository extends JpaRepository<PaymentReceipt, Long> {
    @Query("SELECT r FROM PaymentReceipt r WHERE r.user = :user")
    PaymentReceipt findByUser( @Param("user") User user);
    @Transactional
    @Modifying
    @Query("DELETE FROM PaymentReceipt p WHERE p.id = :id")
    void deleteById(@Param("id") Long id);
    PaymentReceipt findByFinance(Finance finance);
}

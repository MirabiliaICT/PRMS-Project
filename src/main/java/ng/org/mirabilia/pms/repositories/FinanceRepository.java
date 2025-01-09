package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FinanceRepository extends JpaRepository<Finance, Long> {
    @Query("SELECT f FROM Finance f WHERE f.invoice.userNameOrUserCode = :user")
    List<Finance> findAllByUser(@Param("user") User user);
}

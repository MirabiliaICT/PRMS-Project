package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface FinanceRepository extends JpaRepository<Finance, Long> {

    @Query("SELECT f FROM Finance f WHERE f.invoice.userNameOrUserCode = :user")
    List<Finance> findAllByUser(@Param("user") User user);

    //yearly data
    @Query("SELECT EXTRACT(YEAR FROM f.paymentDate), SUM(f.amountPaid) " +
            "FROM Finance f WHERE f.paymentStatus = :status " +
            "GROUP BY EXTRACT(YEAR FROM f.paymentDate)")
    List<Object[]> getTotalAmountPaidByYear(@Param("status") FinanceStatus status);

    @Query(value = """
    WITH yearly_finances AS (
        SELECT EXTRACT(YEAR FROM f.payment_date) AS year,
               SUM(f.amount_paid) AS total_paid
        FROM finances f
        WHERE f.payment_status = :status
        GROUP BY EXTRACT(YEAR FROM f.payment_date)
    ),
    yearly_properties AS (
        SELECT EXTRACT(YEAR FROM p.created_at) AS year,
               COALESCE(SUM(p.price), 0) AS total_price
        FROM properties p
        WHERE p.property_status IN ('SOLD', 'UNDER_OFFER')
        GROUP BY EXTRACT(YEAR FROM p.created_at)
    )
    SELECT yf.year,
           COALESCE(yp.total_price, 0) - COALESCE(yf.total_paid, 0) AS outstanding
    FROM yearly_finances yf
    LEFT JOIN yearly_properties yp ON yf.year = yp.year
    ORDER BY yf.year
    """, nativeQuery = true)
    List<Object[]> getOutstandingAmountByYear(@Param("status") FinanceStatus status);


    //monthly data
    @Query("SELECT EXTRACT(YEAR FROM f.paymentDate) AS year, EXTRACT(MONTH FROM f.paymentDate) AS month, SUM(f.amountPaid) " +
            "FROM Finance f WHERE f.paymentStatus = :status " +
            "GROUP BY EXTRACT(YEAR FROM f.paymentDate), EXTRACT(MONTH FROM f.paymentDate) " +
            "ORDER BY year, month")
    List<Object[]> getTotalAmountPaidByMonth(@Param("status") FinanceStatus status);


    @Query(value = """
    WITH monthly_finances AS (
        SELECT EXTRACT(YEAR FROM f.payment_date) AS year,
               EXTRACT(MONTH FROM f.payment_date) AS month,
               SUM(f.amount_paid) AS total_paid
        FROM finances f
        WHERE f.payment_status = :status
        GROUP BY EXTRACT(YEAR FROM f.payment_date), EXTRACT(MONTH FROM f.payment_date)
    ),
    monthly_properties AS (
        SELECT EXTRACT(YEAR FROM p.created_at) AS year,
               EXTRACT(MONTH FROM p.created_at) AS month,
               COALESCE(SUM(p.price), 0) AS total_price
        FROM properties p
        WHERE p.property_status IN ('SOLD', 'UNDER_OFFER')
        GROUP BY EXTRACT(YEAR FROM p.created_at), EXTRACT(MONTH FROM p.created_at)
    )
    SELECT mf.year,
           mf.month,
           COALESCE(mp.total_price, 0) - COALESCE(mf.total_paid, 0) AS outstanding
    FROM monthly_finances mf
    LEFT JOIN monthly_properties mp ON mf.year = mp.year AND mf.month = mp.month
    ORDER BY mf.year, mf.month
    """, nativeQuery = true)
    List<Object[]> getOutstandingAmountByMonth(@Param("status") FinanceStatus status);




}

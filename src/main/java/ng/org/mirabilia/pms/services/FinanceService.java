package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;

import java.time.LocalDate;
import java.util.List;

public interface FinanceService {
    Finance saveFinance(Finance finance);
    List<Finance> getAllFinances();

    List<Finance> searchFinanceByUserId(User loggedInUser, String keyword, PropertyType propertyType, FinanceStatus financeStatus, LocalDate date);

    List<Finance> findFinancesByUser(User user);
}

package ng.org.mirabilia.pms.services.implementations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.services.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private FinanceRepository financeRepository;

    //persist an entity
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Finance saveFinance(Finance finance) {

        Invoice invoice = finance.getInvoice();
        if (!entityManager.contains(invoice)) {
            invoice = entityManager.merge(invoice); // Merge the changes done to the invoice entity
            finance.setInvoice(invoice); // Update the invoice entity
        }

        return financeRepository.save(finance);
    }

    @Override
    public List<Finance> getAllFinances() {
        //get the most recent data from the db in descending order
        return financeRepository.findAll(Sort.by(Sort.Direction.DESC, "paymentDate"));
    }

    @Override
    public List<Finance> searchFinanceByUserId(User loggedInUser, String keyword, PropertyType propertyType, FinanceStatus financeStatus, LocalDate date) {
        List<Finance> filteredFinances = financeRepository.findAllByUser(loggedInUser);

//        String searchValue = searchField.getValue();
        if (keyword != null && !keyword.isEmpty()) {
            filteredFinances = filteredFinances.stream()
                    .filter(finance -> finance.getInvoice().getPropertyTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

//        PropertyType selectedPropertyType = propertyTypeFilter.getValue();
        if (propertyType != null) {
            filteredFinances = filteredFinances.stream()
                    .filter(finance -> finance.getInvoice().getPropertyType() == propertyType)
                    .toList();
        }

//        FinanceStatus selectedStatus = financeStatusFilter.getValue();
        if (financeStatus != null) {
            filteredFinances = filteredFinances.stream()
                    .filter(finance -> finance.getPaymentStatus() == financeStatus)
                    .toList();
        }

//        LocalDate selectedDate = dateField.getValue();
        if (date != null) {
            filteredFinances = filteredFinances.stream()
                    .filter(finance -> date.equals(finance.getPaymentDate()))
                    .toList();
        }
        /*return filteredFinances.sort(Sort.Direction.DESC);*/
        return filteredFinances;

    }

    public List<Finance> findFinancesByUser(User user) {
        return financeRepository.findAllByUser(user);
    }


}

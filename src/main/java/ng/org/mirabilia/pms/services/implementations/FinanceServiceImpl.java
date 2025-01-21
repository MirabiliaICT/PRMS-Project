package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.*;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.repositories.ReceiptImageRepository;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@Transactional
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private FinanceRepository financeRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ReceiptImageRepository receiptRepository;

    @Override
    public Finance saveFinance(Finance finance) {
        // Ensure the Invoice is managed
        Invoice invoice = finance.getInvoice();
        if (invoice != null && invoice.getId() != null) {
            invoice = invoiceService.findById(invoice.getId()); // Reload from DB to make it managed
            finance.setInvoice(invoice);  // Set the managed invoice
        }

        return financeRepository.save(finance);  // Save the Finance entity with the managed Invoice
    }


    @Override
    public List<Finance> getAllFinances() {
        return financeRepository.findAll();
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
        return filteredFinances;
    }


    public List<Finance> findFinancesByUser(User user) {
        return financeRepository.findAllByUser(user);
    }
    @Transactional
    @Override
    public void deleteFinance(Long financeId) {
        // Retrieve the finance entity by ID
        Finance finance = financeRepository.findById(financeId)
                .orElseThrow(() -> new IllegalArgumentException("Finance record not found with ID: " + financeId));

        // Check if the finance is linked to a receipt and delete the receipt
        PaymentReceipt receipt = receiptRepository.findByFinance(finance);
        if (receipt != null) {
            receiptRepository.delete(receipt); // Delete the associated receipt
        }

        // Delete the finance entity
        financeRepository.delete(finance);
    }



}

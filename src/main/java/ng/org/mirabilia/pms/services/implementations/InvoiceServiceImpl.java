package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.Application;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.InvoiceRepository;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    UserService userService;

    @Override
    public Invoice addInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> searchInvoicesByFilters(String keyword, LocalDate date, InvoiceStatus invoiceStatus, PropertyType propertyType) {
        List<Invoice> invoices = invoiceRepository.findByKeyword(keyword);
        if (propertyType != null) {
            invoices = invoices.stream()
                    .filter(property -> property.getPropertyType() != null &&
                            property.getPropertyType() == propertyType)
                    .toList();
        }
        if (invoiceStatus != null) {
            invoices = invoices.stream()
                    .filter(property -> property.getInvoiceStatus() != null &&
                            property.getInvoiceStatus() == invoiceStatus)
                    .collect(Collectors.toList());
        }
        if (date != null){
            invoices = invoices.stream().filter(property -> property.getIssueDate() != null &&
                    property.getIssueDate() == date)
                    .collect(Collectors.toList());
        }

        return invoices;
    }

    @Override
    public List<Invoice> searchInvoicesByUserId(String keyword, LocalDate date, InvoiceStatus invoiceStatus, PropertyType propertyType, Long userId) {
        List<Invoice> invoices = invoiceRepository.findByKeyword(keyword);
        if (propertyType != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getPropertyType() != null &&
                            invoice.getPropertyType() == propertyType)
                    .toList();
        }
        if (invoiceStatus != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getInvoiceStatus() != null &&
                            invoice.getInvoiceStatus() == invoiceStatus)
                    .collect(Collectors.toList());
        }
        if (date != null){
            invoices = invoices.stream().filter(property -> property.getIssueDate() != null &&
                            property.getIssueDate() == date)
                    .collect(Collectors.toList());
        }

        if (userId != null) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getUserNameOrUserCode() != null &&
                            invoice.getUserNameOrUserCode().getId().equals(userId))
                    .collect(Collectors.toList());
        }

        return invoices;
    }

    @Override
    public Invoice editInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> searchByDate(LocalDate date) {
        return invoiceRepository.findInvoicesByDate(date);
    }

    @Override
    public List<Invoice> searchByInvoiceStatus(InvoiceStatus invoiceStatus) {
        return invoiceRepository.findInvoicesByStatus(invoiceStatus);
    }

    @Override
    public boolean invoiceExists(Property propertyCode) {
        return invoiceRepository.existsByPropertyCode(propertyCode);
    }

    @Override
    public List<Invoice> getInvoicesByUser(User user) {
        return invoiceRepository.findByUser(user);
    }


}
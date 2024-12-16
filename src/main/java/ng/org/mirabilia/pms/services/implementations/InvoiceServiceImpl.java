package ng.org.mirabilia.pms.services.implementations;

import jakarta.transaction.Transactional;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.repositories.InvoiceRepository;
import ng.org.mirabilia.pms.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Invoice addInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
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

}
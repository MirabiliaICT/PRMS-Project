package ng.org.mirabilia.pms.services;

import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    Invoice addInvoice(Invoice invoice);
    List<Invoice> getAllInvoices();

    List<Invoice> searchInvoicesByFilters(String keyword, LocalDate date, InvoiceStatus invoiceStatus, PropertyType propertyType);
    List<Invoice> searchInvoicesByUserId(String keyword, LocalDate date, InvoiceStatus invoiceStatus, PropertyType propertyType, Long userId);
    Invoice editInvoice(Invoice invoice);
    List<Invoice> searchByDate(LocalDate date);
    List<Invoice> searchByInvoiceStatus(InvoiceStatus invoiceStatus);
    public boolean invoiceExists(Property propertyCode);

    List<Invoice> getInvoicesByUser(User user);


}

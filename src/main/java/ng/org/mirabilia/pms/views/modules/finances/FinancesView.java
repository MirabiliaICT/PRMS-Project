package ng.org.mirabilia.pms.views.modules.finances;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Invoice;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.services.implementations.ReceiptImageService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.finances.Admin.AdminFinanceView;
import ng.org.mirabilia.pms.views.modules.finances.Client.ClientFinanceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT", "CLIENT", "AGENT"})
public class FinancesView extends VerticalLayout implements RouterLayout {
    private final UserService userService;
    private final PropertyService propertyService;
    private final InvoiceService invoiceService;
    private final FinanceService financeService;
    private final ReceiptImageService receiptImageService;

    @Autowired
    public FinancesView(UserService userService, PropertyService propertyService, InvoiceService invoiceService, FinanceService financeService, ReceiptImageService receiptImageService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;
        this.financeService = financeService;
        this.receiptImageService = receiptImageService;

        addClassName("finance-view");
        differentViews();


    }

    public void differentViews(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ClientFinanceView clientFinanceView = new ClientFinanceView(financeService, invoiceService, userService, receiptImageService);
        AdminFinanceView adminFinanceView = new AdminFinanceView(userService, propertyService, invoiceService);

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")) || authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))){
            add(adminFinanceView);
        } else {
            add(clientFinanceView);
        }
    }

}

package ng.org.mirabilia.pms.views.modules.finances;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.services.*;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.finances.admin.AdminFinancesView;
import ng.org.mirabilia.pms.views.modules.finances.Client.ClientFinanceView;
import ng.org.mirabilia.pms.views.modules.finances.Client.FinanceTab;
import ng.org.mirabilia.pms.views.modules.finances.Client.InvoiceTab;
import ng.org.mirabilia.pms.views.modules.finances.admin.AdminMainView;
import ng.org.mirabilia.pms.views.modules.properties.content.tabs.CardTab;
import ng.org.mirabilia.pms.views.modules.properties.content.tabs.GridTab;
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
    private final JakartaMailService mailService;
    FinanceRepository financeRepository;

    @Autowired
    public FinancesView(UserService userService, PropertyService propertyService, InvoiceService invoiceService,
                        FinanceService financeService, JakartaMailService mailService, FinanceRepository financeRepository) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;
        this.financeService = financeService;
        this.mailService = mailService;
        this.financeRepository = financeRepository;
        addClassName("finance-view");
        differentViews();

    }

    public void differentViews(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ClientFinanceView clientFinanceView = new ClientFinanceView(financeService, invoiceService, userService);
        AdminMainView adminMainView = new AdminMainView(userService, propertyService, invoiceService, financeService,
                mailService, financeRepository);

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")) || authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))){
            add(adminMainView);
        } else {
            add(clientFinanceView);
        }
    }

}

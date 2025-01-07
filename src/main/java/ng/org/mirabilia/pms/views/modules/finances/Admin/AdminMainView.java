package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.InvoiceService;
import ng.org.mirabilia.pms.services.PropertyService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;


@Route(value = "admin/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminMainView extends VerticalLayout {
    private final Button financeButton;
    private final Button invoiceButton;

    private final Div contentContainer;
    private final AdminInvoiceView adminFinanceView;
    UserService userService;
    PropertyService propertyService;
    InvoiceService invoiceService;

    public AdminMainView(UserService userService, PropertyService propertyService, InvoiceService invoiceService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.invoiceService = invoiceService;

        setSpacing(true);
        setPadding(false);

        financeButton = new Button("Finances", event -> showFinanceContent());
        invoiceButton = new Button("Invoices", event -> showInvoiceContent());
        adminFinanceView = new AdminInvoiceView(userService, propertyService, invoiceService);
        financeButton.addClassName("admin-finance-button");
        invoiceButton.addClassName("admin-invoice-button");

        financeButton.addClickListener(event -> {
            financeButton.getStyle().setBackground("#ffffff");
            financeButton.getStyle().set("color", "rgba(22, 40, 104, 1)");
            invoiceButton.getStyle().set("color", "#000000");
            invoiceButton.getStyle().setBackground("inherit");
        });

        invoiceButton.addClickListener(event -> {
            invoiceButton.getStyle().setBackground("#ffffff");
            invoiceButton.getStyle().set("color", "rgba(22, 40, 104, 1)");
            financeButton.getStyle().set("color", "#000000");
            financeButton.getStyle().setBackground("inherit");
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(financeButton, invoiceButton);
        horizontalLayout.addClassName("admin-finance-horizontal");
        horizontalLayout.getStyle().setMargin("10px");
        add(horizontalLayout);

        contentContainer = new Div();
        contentContainer.setId("content-container");
        contentContainer.setHeightFull();
        contentContainer.setWidthFull();

        add(contentContainer);
        showFinanceContent();
    }

    private void showFinanceContent() {
        contentContainer.removeAll();
        financeButton.getStyle().setColor("rgba(22, 40, 104, 1)");
        financeButton.getStyle().setBackground("#ffffff");
        financeButton.getStyle().setPadding("8px");

        Div invoiceContent = new Div();
        invoiceContent.setText("Finance History");
        contentContainer.add(invoiceContent);

    }

    private void showInvoiceContent() {
        contentContainer.removeAll();
        invoiceButton.getStyle().set("color", "#000000");
        invoiceButton.getStyle().setBackground("inherit");
        invoiceButton.getStyle().setPadding("8px");

        contentContainer.add(adminFinanceView);
    }
}

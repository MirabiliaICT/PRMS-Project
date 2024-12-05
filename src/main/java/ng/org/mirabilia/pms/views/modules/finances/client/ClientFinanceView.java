package ng.org.mirabilia.pms.views.modules.finances.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Finances;
import ng.org.mirabilia.pms.domain.entities.Property;
import ng.org.mirabilia.pms.views.MainView;

@Route(value = "client/finances", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed("CLIENT")
public class ClientFinanceView extends VerticalLayout {
    private final Button financeButton;
    private final Button invoiceButton;

    private final Grid<Finances> financeGrid = new Grid<>(Finances.class);
    private final Div contentContainer;
    public ClientFinanceView() {

        financeButton = new Button("Finances", event -> showFinanceContent());
        invoiceButton = new Button("Invoices", event -> showInvoiceContent());
        financeButton.addClassName("client-finance-button");
        invoiceButton.addClassName("client-invoice-button");

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
        horizontalLayout.addClassName("client-finance-horizontal");
        add(horizontalLayout);

        contentContainer = new Div();
        contentContainer.setId("content-container");


        add(contentContainer);
        showFinanceContent();
    }

    private void showFinanceContent() {
        contentContainer.removeAll();
        financeButton.getStyle().setColor("rgba(22, 40, 104, 1)");
        financeButton.getStyle().setBackground("#ffffff");
        financeButton.getStyle().setPadding("8px");

        Div financeContent = new Div();
        financeContent.setText("Finance History");
        contentContainer.add(financeContent);
    }

    private void showInvoiceContent() {
        contentContainer.removeAll();
        invoiceButton.getStyle().set("color", "#000000");
        invoiceButton.getStyle().setBackground("inherit");
        invoiceButton.getStyle().setPadding("8px");
        Div invoiceContent = new Div();
        invoiceContent.setText("Invoice History");
        contentContainer.add(invoiceContent);
    }
}

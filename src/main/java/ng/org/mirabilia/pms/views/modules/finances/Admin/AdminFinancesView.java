
package ng.org.mirabilia.pms.views.modules.finances.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.domain.entities.Finance;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.domain.enums.InvoiceStatus;
import ng.org.mirabilia.pms.domain.enums.PropertyType;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import ng.org.mirabilia.pms.services.FinanceService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.forms.finances.invoice.FinancePreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Route(value = "finance", layout = MainView.class)
@PageTitle("Finances | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "ACCOUNTANT"})
public class AdminFinancesView extends VerticalLayout {

    Grid<Finance> adminFinanceGrid;
    FinanceService financeService;
    H4 financeHistoryText;
    FinancePreview financePreviewDialog;
    FinanceRepository financeRepository;
    Button closeDialogButton;

    //filters
    TextField searchField = new TextField();
    DatePicker datePicker = new DatePicker();
    ComboBox<PropertyType> propertyTypeFilter = new ComboBox<PropertyType>();
    ComboBox<InvoiceStatus> statuses = new ComboBox<InvoiceStatus>();

    Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));

    AdminChartView adminChartView;

    @Autowired
    public AdminFinancesView(FinanceService financeService, FinanceRepository financeRepository) {
        this.financeService = financeService;
        this.financeRepository = financeRepository;
        adminFinanceGrid = new Grid<>(Finance.class, false);

        getStyle().set("gap", "0");

        financeHistoryText = new H4("Payment History");
        financePreviewDialog = new FinancePreview((v) -> updateGridItems());

        //populating combo box and also adding functions to the filters
        datePicker.setPlaceholder("Date");
        datePicker.addClassNames("custom-filter col-sm-6 col-xs-6");
        ;

        datePicker.addValueChangeListener(e -> updateGridItems());
        datePicker.setClearButtonVisible(true);

        searchField.setPlaceholder("Search Finance");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGridItems());
        searchField.getElement().getStyle().set("background-color", "white");
        searchField.setClearButtonVisible(true);
        searchField.addClassNames("custom-filter col-sm-6 col-xs-6");
        ;

        propertyTypeFilter.addValueChangeListener(e -> updateGridItems());
        propertyTypeFilter.addClassNames("custom-filter col-sm-6 col-xs-6");
        propertyTypeFilter.setClearButtonVisible(true);
        propertyTypeFilter.setItemLabelGenerator(PropertyType::getDisplayName);
        propertyTypeFilter.setPlaceholder("Property Type");

        statuses.addValueChangeListener(e -> updateGridItems());
        statuses.addClassNames("custom-filter col-sm-6 col-xs-6");
        statuses.setPlaceholder("Status");

        //inline styling
        adminFinanceGrid.addClassName("custom-grid");
        adminFinanceGrid.getStyle().setBorder("none");
        financeHistoryText.getStyle().setMargin("10px");

        //BUTTONS
        closeDialogButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
        closeDialogButton.getStyle().setMarginLeft("auto").setDisplay(Style.Display.INLINE);

        closeDialogButton.addClickListener(e -> {
            financePreviewDialog.financePreviewLayout.removeAll();
            financePreviewDialog.imagePreviewLayout.removeAll();
            financePreviewDialog.close();
        });

        adminChartView = new AdminChartView(financeRepository);

        resetButton.addClickListener(e -> resetFilters());
        resetButton.addClassNames("custom-button custom-reset-button custom-toolbar-button col-sm-6 col-xs-6");

        HorizontalLayout filtersLayout = new HorizontalLayout();
        filtersLayout.add(searchField, datePicker, statuses, propertyTypeFilter, resetButton);
        filtersLayout.getStyle().setAlignSelf(Style.AlignSelf.END);

        financeHistoryText.getStyle().setMarginTop("0px");

        //Display
        configureGrid();
        add(adminChartView, filtersLayout, financeHistoryText, adminFinanceGrid);
        updateGridItems();
    }

    private void resetFilters() {
        searchField.clear();
        propertyTypeFilter.clear();
        statuses.clear();
        datePicker.clear();
        updateGridItems();
    }

    public void configureGrid() {
        adminFinanceGrid.addColumn(finance -> finance.getInvoice().getPropertyTitle())
                .setHeader("Property")
                .setAutoWidth(true)
                .setSortable(true);

        adminFinanceGrid.addColumn(Finance::getPaidBy)
                .setHeader("Paid By")
                .setSortable(true)
                .setAutoWidth(true);

        adminFinanceGrid.addColumn(Finance::getPaymentStatus)
                .setHeader("Payment Status")
                .setSortable(true)
                .setAutoWidth(true);

        adminFinanceGrid.addColumn(finance -> finance.getInvoice().getPropertyType()
                        .toString().replace("_", " ").toLowerCase())
                .setHeader("Property Type")
                .setSortable(true)
                .setAutoWidth(true);

        adminFinanceGrid.addColumn(finance -> finance.getPaymentMethod()
                        .toString().replace("_", " ").toLowerCase())
                .setHeader("Payment Method")
                .setSortable(true)
                .setAutoWidth(true);

        adminFinanceGrid.addColumn(finance -> new DecimalFormat("#,###")
                        .format(finance.getAmountPaid()))
                .setHeader("Amount Paid")
                .setSortable(true)
                .setAutoWidth(true);

        adminFinanceGrid.addColumn(finance -> finance.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .setHeader("Payment Date")
                .setSortable(true)
                .setAutoWidth(true);


        adminFinanceGrid.addItemClickListener(event -> {
            Finance finance = event.getItem();

            //buttons and their actions
            Button approveButton = new Button("APPROVE");
            Button disapproveButton = new Button("DISAPPROVE");

            HorizontalLayout buttonLayout = new HorizontalLayout(disapproveButton, approveButton);

            //open the finance dialog and  disable the buttons once it has been set to approved or disapproved
            if (finance != null && finance.getPaymentStatus().equals(FinanceStatus.APPROVED) || finance.getPaymentStatus().equals(FinanceStatus.DISAPPROVED)) {
                financePreviewDialog.getFooter().removeAll();
            }
            //open the finance dialog and make the buttons clickable only when it's set to pending
            else if (finance != null & finance.getPaymentStatus().equals(FinanceStatus.PENDING)) {
                financePreviewDialog.getFooter().add(buttonLayout);

                approveButton.addClickListener(e -> {
                    finance.updateOutstandingAmounts();
                    finance.setPaymentStatus(FinanceStatus.APPROVED);
                    financeService.saveFinance(finance);
                    financePreviewDialog.close();
                });

                disapproveButton.addClickListener(e -> {
                    if (finance.getInvoice().isPriceInitialized() == true) {
                        finance.setOutstandingAmount(finance.getInvoice().getNewPrice());
                    } else if (finance.getInvoice().isPriceInitialized() == false) {
                        finance.setOutstandingAmount(finance.getInvoice().getPropertyPrice());
                    }
                    finance.getInvoice().setPriceInitialized(true);
                    finance.setPaymentStatus(FinanceStatus.DISAPPROVED);
                    financeService.saveFinance(finance);
                    financePreviewDialog.close();
                });
            }
            // to avoid duplications when one opens a tab
            financePreviewDialog.getHeader().removeAll();

            financePreviewDialog.getHeader().add(new H2("Finance Review"));
            financePreviewDialog.getHeader().add(closeDialogButton);

            financePreviewDialog.updatePreview(finance);

            financePreviewDialog.setModal(true);
            financePreviewDialog.setCloseOnOutsideClick(false);
            financePreviewDialog.setCloseOnEsc(false);

            financePreviewDialog.open();
        });

        updateGridItems();
    }

    private void updateGridItems() {


        List<Finance> financeRecords = financeService.getAllFinances();
        adminFinanceGrid.setItems(financeRecords);
    }

}

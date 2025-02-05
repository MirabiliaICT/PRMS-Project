package ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import ng.org.mirabilia.pms.repositories.FinanceRepository;

public class MonthlyTab extends VerticalLayout {
    MonthlyRevenueChart revenueChart;
    FinanceRepository financeRepository;

    public MonthlyTab(FinanceRepository financeRepository){
        this.financeRepository = financeRepository;
        setWidthFull();
        setHeightFull();

        getStyle().set("gap", "0");

        revenueChart = new MonthlyRevenueChart(financeRepository);
        revenueChart.setWidthFull();
        revenueChart.setHeight("390px");

        revenueChart.yearComboBox.getStyle().setAlignSelf(Style.AlignSelf.END).setMarginBottom("0px").setMarginTop("0px");
        add(revenueChart.yearComboBox, revenueChart);

        addAttachListener(event -> revenueChart.updateChart(revenueChart.yearComboBox.getValue()));
    }

}

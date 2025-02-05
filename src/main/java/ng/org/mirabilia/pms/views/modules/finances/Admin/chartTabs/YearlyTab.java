package ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import ng.org.mirabilia.pms.repositories.FinanceRepository;

public class YearlyTab extends VerticalLayout {
    YearlyRevenueChart revenueChart;
    FinanceRepository financeRepository;

    public YearlyTab(FinanceRepository financeRepository){
        this.financeRepository= financeRepository;
        setWidthFull();
        setHeightFull();

        getStyle().set("gap", "0");

        revenueChart = new YearlyRevenueChart(financeRepository);
        revenueChart.setWidthFull
                ();
        revenueChart.setHeight("390px");
        add(revenueChart);

        revenueChart.getStyle().setAlignSelf(Style.AlignSelf.END).setMarginBottom("0px").setMarginTop("0px");
        addAttachListener(event -> revenueChart.getElement().callJsFunction("initializeChart"));
    }

}

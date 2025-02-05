package ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.UI;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YearlyRevenueChart extends Div {

    @Autowired
    FinanceRepository financeRepository;

    private final String chartId = "yearly-chart";

    @Autowired
    public YearlyRevenueChart(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;

        getStyle().set("gap", "0");

        // Fetch yearly data
        Map<Integer, BigDecimal> yearlyProfitData = fetchAndAggregateProfitByYear();
        Map<Integer, BigDecimal> yearlyOutstandingData = fetchAndAggregateOutstandingByYear();

        // Convert the map to sorted lists for labels and data
        List<Integer> yearsForProfit = new ArrayList<>(yearlyProfitData.keySet());
        Collections.sort(yearsForProfit);

        List<Integer> yearsForOutstanding = new ArrayList<>(yearlyOutstandingData.keySet());
        Collections.sort(yearsForOutstanding);

        List<BigDecimal> profits = yearsForProfit.stream()
                .map(yearlyProfitData::get)
                .collect(Collectors.toList());

        List<BigDecimal> outstanding = yearsForOutstanding.stream()
                .map(yearlyOutstandingData::get)
                .collect(Collectors.toList());

        String yearsJs = yearsForProfit.toString(); // Assuming profit and outstanding share the same years
        String profitsJs = profits.toString();
        String outstandingJs = outstanding.toString();

        // Initialize the chart layout
        setWidth("100%");
        setHeight("450px");
        getStyle()
                .set("padding", "20px")
                .set("box-sizing", "border-box");

        // Add the chart canvas
        getElement().setProperty("innerHTML", "<canvas id='" + chartId + "' style='width: 100%; height: 100%;'></canvas>");

        // Initialize the chart
        initializeChart(yearsJs, profitsJs, outstandingJs);
    }

    private void initializeChart(String yearsJs, String profitsJs, String outstandingJs) {
        String chartConfig = """
             const ctx = document.getElementById('%s').getContext('2d');
                              new Chart(ctx, {
                                  type: 'bar',
                                  data: {
                                      labels: %s,
                                      datasets: [{
                                          label: 'Profit',
                                          data: %s,
                                          backgroundColor: 'rgb(0, 0, 255)',
                                          borderColor: 'rgb(0, 0, 255)',
                                          borderWidth: 1,
                                          borderRadius: 4,
                                          barPercentage: 0.8
                                      },
                                      {
                                          label: 'Outstanding',
                                          data: %s,
                                          backgroundColor: 'rgb(255, 165, 0)',
                                          borderColor: 'rgb(255, 165, 0)',
                                          borderWidth: 1,
                                          borderRadius: 4,
                                          barPercentage: 0.8
                                      }]
                                  },
                                  options: {
                                      responsive: true,
                                      maintainAspectRatio: false,
                                      plugins: {
                                          title: {
                                              display: true,
                                              text: 'Finance Overview',
                                              font: { size: 16 }
                                          },
                                          legend: {
                                              position: 'bottom'
                                          }
                                      },
                                      scales: {
                                          y: {
                                              beginAtZero: true,
                                              title: {
                                                  display: true,
                                                  text: 'Amount (In Millions)'
                                              },
                                              ticks: {
                                                  callback: function(value) {
                                                      return (value / 1000000).toFixed(0);
                                                  }
                                              }
                                          },
                                          x: {
                                              title: {
                                                  display: true,
                                                  text: 'Year'
                                              },
                                              categoryPercentage: 0.8,
                                              barPercentage: 0.8
                                          }
                                      },
                                      barThickness: 40,
                                      categoryPercentage: 0.8
                                  }
                              });
        """.formatted(chartId, yearsJs, profitsJs, outstandingJs);

        // Inject Chart.js library and create chart
        addAttachListener(event -> {
            Page page = UI.getCurrent().getPage();
            page.addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");
            page.executeJs("setTimeout(() => { " + chartConfig + " }, 100);");
        });
    }

    private Map<Integer, BigDecimal> fetchAndAggregateProfitByYear() {
        List<Object[]> results = financeRepository.getTotalAmountPaidByYear(FinanceStatus.APPROVED);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],  // Year
                        row -> (BigDecimal) row[1]  // Amount
                ));
    }

    private Map<Integer, BigDecimal> fetchAndAggregateOutstandingByYear() {
        List<Object[]> results = financeRepository.getOutstandingAmountByYear(FinanceStatus.APPROVED);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(), //Year
                        row -> (BigDecimal) row[1]  //Amount
                ));
    }
}

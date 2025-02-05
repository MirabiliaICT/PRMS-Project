package ng.org.mirabilia.pms.views.modules.dashboard.charts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonthlyDashBoardChart extends VerticalLayout {

    FinanceRepository financeRepository;

    private final String chartId = "dashboardMonthlyChart";
    private Map<String, BigDecimal> profitsByMonth;

    @Autowired
    public MonthlyDashBoardChart(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;

        setWidthFull();
        setHeight("100%");

        // Fetch data for the current year
        profitsByMonth = fetchAndAggregateProfitByMonthForCurrentYear();

        // Add chart canvas
        getElement().setProperty("innerHTML", "<canvas id='" + chartId + "' style='width: 100%; height: 100%;'></canvas>");

        // Ensure Chart.js is loaded
        UI.getCurrent().getPage().addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");

        // Render the chart immediately
        initializeChart();
    }


    public void initializeChart() {
        // Directly use the current year
        Integer currentYear = Year.now().getValue();
        updateChart(currentYear);
    }

    private void updateChart(Integer year) {
        if (year == null) return;

        // Use month names for labels
        List<String> labels = generateMonthLabels();

        // Map data for the selected year using month names directly
        List<BigDecimal> profits = labels.stream()
                .map(month -> profitsByMonth.getOrDefault(year + "-" + month, BigDecimal.ZERO)) // Match "YYYY-Jan"
                .collect(Collectors.toList());

        // Render chart with month names
        renderChart(labels, profits);
    }

    private void renderChart(List<String> labels, List<BigDecimal> profits) {
        String labelsJs = labels.stream().map(l -> "\"" + l + "\"").collect(Collectors.joining(", ", "[", "]"));
        String profitsJs = profits.stream().map(BigDecimal::toString).collect(Collectors.joining(", ", "[", "]"));

        String chartConfig = """
    const ctx = document.getElementById('%s').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: %s,
            datasets: [{
                label: 'Monthly Profit',
                data: %s,
                backgroundColor: 'rgb(0, 0, 255)',
                borderColor: 'rgb(0, 0, 255)',
                borderWidth: 1,
                borderRadius: 4,
                barThickness: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Amount (in Millions)'
                    },
                    ticks: {
                        callback: (value) => {
                            return (value / 1000000).toFixed(0);
                        }
                    },
                    border: {
                        display: false
                    },
                    grid: {
                        display: false
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Month'
                    },
                    border: {
                        display: false
                    },
                    grid: {
                        display: false
                    },
                    categoryPercentage: 0.6,
                    barPercentage: 0.7
                }
            }
        }
    });
""".formatted(chartId, labelsJs, profitsJs);

        UI.getCurrent().getPage().executeJs("setTimeout(() => { " + chartConfig + " }, 100);");
    }

    private List<String> generateMonthLabels() {
        return List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    }

    private Map<String, BigDecimal> fetchAndAggregateProfitByMonthForCurrentYear() {
        int currentYear = Year.now().getValue();
        List<Object[]> results = financeRepository.getTotalAmountPaidByMonth(FinanceStatus.APPROVED);
        return results.stream()
                .filter(row -> ((Number) row[0]).intValue() == currentYear) // Filter data for the current year
                .collect(Collectors.toMap(
                        row -> {
                            String year = String.valueOf(((Number) row[0]).intValue());
                            String month = String.format("%02d", ((Number) row[1]).intValue());
                            return year + "-" + mapMonthNumberToName(month);
                        },
                        row -> (BigDecimal) row[2]
                ));
    }

    private String convertMonth(String input, boolean toName) {
        switch (input) {
            case "01": case "Jan": return toName ? "Jan" : "01";
            case "02": case "Feb": return toName ? "Feb" : "02";
            case "03": case "Mar": return toName ? "Mar" : "03";
            case "04": case "Apr": return toName ? "Apr" : "04";
            case "05": case "May": return toName ? "May" : "05";
            case "06": case "Jun": return toName ? "Jun" : "06";
            case "07": case "Jul": return toName ? "Jul" : "07";
            case "08": case "Aug": return toName ? "Aug" : "08";
            case "09": case "Sep": return toName ? "Sep" : "09";
            case "10": case "Oct": return toName ? "Oct" : "10";
            case "11": case "Nov": return toName ? "Nov" : "11";
            case "12": case "Dec": return toName ? "Dec" : "12";
            default: return input;
        }
    }

    private String mapMonthNumberToName(String numericMonth) {
        return convertMonth(numericMonth, true);
    }
}

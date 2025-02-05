package ng.org.mirabilia.pms.views.modules.finances.admin.chartTabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import ng.org.mirabilia.pms.domain.enums.FinanceStatus;
import ng.org.mirabilia.pms.repositories.FinanceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
public class MonthlyRevenueChart extends VerticalLayout {

    FinanceRepository financeRepository;

    private final String chartId = "financeChart";
    public ComboBox<Integer> yearComboBox;

    private Map<String, BigDecimal> profitsByMonth;
    private Map<String, BigDecimal> outstandingByMonth;

    @Autowired
    public MonthlyRevenueChart(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;

        getStyle().set("gap", "0");

        // Fetch data
        profitsByMonth = fetchAndAggregateProfitByMonth();
        outstandingByMonth = fetchAndAggregateOutstandingByMonth();

        // Create combo box for year selection
        yearComboBox = new ComboBox<>("Year");
        yearComboBox.setItems(getAvailableYears(profitsByMonth, outstandingByMonth));
        yearComboBox.addValueChangeListener(event -> updateChart(event.getValue()));

        // Add chart canvas
        getElement().setProperty("innerHTML", "<canvas id='" + chartId + "' style='width: 100%; height: 400px;'></canvas>");

        addAttachListener(event -> {
            UI.getCurrent().getPage().addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");
            initializeChart(); // Try initializing after attempting to load the script
        });

    }

    private void initializeChart() {
        Integer defaultYear = yearComboBox.getDataProvider()
                .fetch(new com.vaadin.flow.data.provider.Query<>())
                .findFirst()
                .orElse(null);

        if (defaultYear != null) {
            yearComboBox.setValue(defaultYear); // Trigger chart rendering
        }
    }

    public  void updateChart(Integer year) {
        if (year == null) return;

        // Use month names for labels
        List<String> labels = generateMonthLabels();

        // Map data for the selected year using month names directly
        List<BigDecimal> profits = labels.stream()
                .map(month -> profitsByMonth.getOrDefault(year + "-" + month, BigDecimal.ZERO)) // Match "YYYY-Jan"
                .collect(Collectors.toList());

        List<BigDecimal> outstanding = labels.stream()
                .map(month -> outstandingByMonth.getOrDefault(year + "-" + month, BigDecimal.ZERO)) // Match "YYYY-Jan"
                .collect(Collectors.toList());

        // Render chart with month names
        renderChart(labels, profits, outstanding);
    }

    private void renderChart(List<String> labels, List<BigDecimal> profits, List<BigDecimal> outstanding) {
        String labelsJs = labels.stream().map(l -> "\"" + l + "\"").collect(Collectors.joining(", ", "[", "]"));
        String profitsJs = profits.stream().map(BigDecimal::toString).collect(Collectors.joining(", ", "[", "]"));
        String outstandingJs = outstanding.stream().map(BigDecimal::toString).collect(Collectors.joining(", ", "[", "]"));

        String chartConfig = """
                const ctx = document.getElementById('%s').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: %s,
                        datasets: [
                            {
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
                            }
                        ]
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
                                    text: 'Amount (in Millions)'
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
                                    text: 'Month'
                                },
                                categoryPercentage: 0.8,
                                barPercentage: 0.8
                            }
                        },
                               barThickness: 40,
                               categoryPercentage: 0.8
                    }
                });
                """.formatted(chartId, labelsJs, profitsJs, outstandingJs);

        UI.getCurrent().getPage().executeJs("setTimeout(() => { " + chartConfig + " }, 100);");
    }

    private List<Integer> getAvailableYears(Map<String, BigDecimal> profits, Map<String, BigDecimal> outstanding) {
        Set<Integer> years = new HashSet<>();
        profits.keySet().forEach(key -> years.add(Integer.parseInt(key.split("-")[0])));
        outstanding.keySet().forEach(key -> years.add(Integer.parseInt(key.split("-")[0])));
        return years.stream().sorted().collect(Collectors.toList());
    }

    private List<String> generateMonthLabels() {
        return List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    }


    private Map<String, BigDecimal> fetchAndAggregateProfitByMonth() {
        List<Object[]> results = financeRepository.getTotalAmountPaidByMonth(FinanceStatus.APPROVED);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> {
                            String year = String.valueOf(((Number) row[0]).intValue());
                            String month = String.format("%02d", ((Number) row[1]).intValue());
                            return year + "-" + mapMonthNumberToName(month);
                        },
                        row -> (BigDecimal) row[2]
                ));
    }

    private Map<String, BigDecimal> fetchAndAggregateOutstandingByMonth() {
        List<Object[]> results = financeRepository.getOutstandingAmountByMonth(FinanceStatus.APPROVED);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> {
                            String year = String.valueOf(((Number) row[0]).intValue());
                            String month = String.format("%02d", ((Number) row[1]).intValue());
                            return year + "-" + mapMonthNumberToName(month);
                        },
                        row -> (BigDecimal) row[2]
                ));
    }

    //change 01-12 to Jan-Dec for the Month
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


    // Replace previous methods with these:
    private String mapMonthNumberToName(String numericMonth) {
        return convertMonth(numericMonth, true);
    }
}

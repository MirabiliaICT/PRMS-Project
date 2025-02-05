package ng.org.mirabilia.pms.views.modules.dashboard.charts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ng.org.mirabilia.pms.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class PropertyChart extends VerticalLayout {

    private final PropertyService propertyService;

    private final String chartId = "propertyChart";

    @Autowired
    public PropertyChart(PropertyService propertyService) {
        this.propertyService = propertyService;

        setWidthFull();
        setHeight("100%");

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

        // Fetch property data for the current year
        Map<Month, Map<String, Integer>> propertyCounts = propertyService.getPropertyCountsByMonthAndType();

        // Generate labels for months (Jan to Dec)
        List<String> labels = generateMonthLabels();

        // Extract counts for "Land" and "Other"
        List<Integer> landCounts = Arrays.stream(Month.values())
                .map(month -> propertyCounts.getOrDefault(month, Collections.emptyMap()).getOrDefault("Land", 0))
                .collect(Collectors.toList());

        List<Integer> otherCounts = Arrays.stream(Month.values())
                .map(month -> propertyCounts.getOrDefault(month, Collections.emptyMap()).getOrDefault("Other", 0))
                .collect(Collectors.toList());

        // Render the chart
        renderChart(labels, landCounts, otherCounts);
    }

    private void renderChart(List<String> labels, List<Integer> landCounts, List<Integer> otherCounts) {
        String labelsJs = labels.stream().map(l -> "\"" + l + "\"").collect(Collectors.joining(", ", "[", "]"));
        String landCountsJs = landCounts.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
        String otherCountsJs = otherCounts.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));

        String chartConfig = """
    const ctx = document.getElementById('%s').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: %s,
            datasets: [
                {
                    label: 'Apartment',
                    data: %s,
                    backgroundColor: 'rgb(0, 0, 255)', 
                    borderColor: 'rgb(0, 0, 255)',
                    borderWidth: 1,
                    borderRadius: 4,
                    barThickness: 10
                },
                {
                    label: 'Land',
                    data: %s,
                    backgroundColor: 'rgb(255, 165, 0)', 
                    borderColor: 'rgb(255, 165, 0)',
                    borderWidth: 1,
                    borderRadius: 4,
                    barThickness: 10
                }
            ]
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
                        text: 'Property Count'
                    },
                    ticks: {
                        stepSize: 1 // Force increments of 1
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
""".formatted(chartId, labelsJs, landCountsJs, otherCountsJs);

        UI.getCurrent().getPage().executeJs("setTimeout(() => { " + chartConfig + " }, 100);");
    }

    private List<String> generateMonthsLabels() {
        return Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    }

    private List<String> generateMonthLabels() {
        // Generate short month names (Jan, Feb, ...) for display
        return Arrays.stream(Month.values())
                .map(month -> month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.toList());
    }

    private String mapMonthNumberToName(String numericMonth) {
        return switch (numericMonth) {
            case "01" -> "Jan";
            case "02" -> "Feb";
            case "03" -> "Mar";
            case "04" -> "Apr";
            case "05" -> "May";
            case "06" -> "Jun";
            case "07" -> "Jul";
            case "08" -> "Aug";
            case "09" -> "Sep";
            case "10" -> "Oct";
            case "11" -> "Nov";
            case "12" -> "Dec";
            default -> numericMonth; // Fallback for invalid input
        };
    }

}

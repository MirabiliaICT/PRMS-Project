package ng.org.mirabilia.pms.views.chart;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;

public class ChartView extends Div {

    public ChartView() {
        // Create a container to hold the chart
        Div chartContainer = new Div();
        chartContainer.setId("chart-container");
        chartContainer.getElement().setProperty("innerHTML", "<canvas id='myChart' width='50%' height='30vh'></canvas>");
        add(chartContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Inject the Chart.js library via CDN
        Page page = attachEvent.getUI().getPage();
        page.addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");

        // JavaScript to render the chart after Chart.js is loaded
        String chartJsScript =
                "const ctx = document.getElementById('myChart').getContext('2d');"
                        + "const myChart = new Chart(ctx, {"

                        + "    type: 'bar',"
                        + "    data: {"
                        + "        labels: ['Mon', 'Tue', 'Wed', 'Thur', 'Fri', 'Sat'],"
                        + "        datasets: [{"
                        + "            label: 'Votes',"
                        + "            data: [12, 19, 3, 5, 2, 3],"
                        + "            backgroundColor: ["
                        + "                'rgba(255, 99, 132, 0.2)',"
                        + "                'rgba(54, 162, 235, 0.2)',"
                        + "                'rgba(255, 206, 86, 0.2)',"
                        + "                'rgba(75, 192, 192, 0.2)',"
                        + "                'rgba(153, 102, 255, 0.2)',"
                        + "                'rgba(255, 159, 64, 0.2)'"
                        + "            ],"
                        + "            borderColor: ["
                        + "                'rgba(255, 99, 132, 1)',"
                        + "                'rgba(54, 162, 235, 1)',"
                        + "                'rgba(255, 206, 86, 1)',"
                        + "                'rgba(75, 192, 192, 1)',"
                        + "                'rgba(153, 102, 255, 1)',"
                        + "                'rgba(255, 159, 64, 1)'"
                        + "            ],"
                        + "            borderWidth: 1,"
                        + "            barThickness: 6," // bar thickness
                        + "            categoryPercentage: 0.8,"  // Controls the width of the category (space around each bar)
                        + "            barPercentage: 0.9" // Controls the width of each bar within the category
                        + "        }]"
                        + "    },"
                        + "    options: {"
                        + "        scales: {"
                        + "            x: {"
                        + "                categoryPercentage: 0,"  // Adjust category width (lower value = more space)
                        + "                barPercentage: 0"       // Adjust bar width within category (lower = slimmer bars)
                        + "            },"
                        + "            y: {"
                        + "                beginAtZero: true,"
                        + "            }"
                        + "        }"
                        + "    }"
                        + "});";

        // Inject the chart rendering code
        getUI().get().getPage().executeJs(chartJsScript);
    }
}

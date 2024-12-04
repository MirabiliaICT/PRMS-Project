package ng.org.mirabilia.pms.domain.enums;
import com.vaadin.flow.component.html.Span;

public class CreateStatusTag {

    public Span createStatusTag(InvoiceStatus status) {
        Span statusTag = new Span(status.toString()); // Display the status as text

        // Apply styles based on the status
        switch (status) {
            case PAID:
                statusTag.getStyle().set("background", "#34A853"); // Green
                statusTag.getStyle().set("color", "#FFFFFF");
                break;
            case UNPAID:
                statusTag.getStyle().set("background", "#C5221F"); // Red
                statusTag.getStyle().set("color", "#FFFFFF");
                break;
            case PARTIAL:
                statusTag.getStyle().set("background", "#F4A74B"); // Orange
                statusTag.getStyle().set("color", "#FFFFFF");
                break;
            case OVERDUE:
                statusTag.getStyle().set("background", "#B0BEC5"); // Default (Gray)
                statusTag.getStyle().set("color", "#000000");
                break;
        }

        // Common styles for all tags
        statusTag.getStyle().set("padding", "5px");
        statusTag.getStyle().set("border-radius", "10px");
        statusTag.getStyle().set("font-size", "12px");
        statusTag.getStyle().set("font-weight", "500");
        statusTag.getStyle().set("display", "inline-block"); // Ensure it behaves like a tag

        return statusTag;
    }

}

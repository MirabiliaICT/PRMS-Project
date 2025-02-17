package ng.org.mirabilia.pms.domain.enums;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style;

public class CreateStatusTag {

    public Span createStatusTag(InvoiceStatus status) {
        Span statusTag = new Span(status.toString()); // Display the status as text

        // Apply styles based on the status
        switch (status) {
            case PAID:
                statusTag.getStyle().set("color", "rgb(52 168 83)"); //Green
                statusTag.getStyle().setBackground("rgb(52 168 83 / 22%)");
                statusTag.getStyle().setBorder("2px solid rgb(52 168 83)");
                break;
            case UNPAID:
                statusTag.getStyle().setBackground("rgba(236, 170, 96, 0.15)"); // Red
                statusTag.getStyle().setBorder("2px solid rgba(236, 170, 96, 1)"); // Red
                statusTag.getStyle().set("color", "rgba(236, 170, 96, 1)");
                break;
            case PARTIAL:
                statusTag.getStyle().set("background", "#F4A74B"); // Orange
                statusTag.getStyle().set("color", "#FFFFFF");
                break;
            case OVERDUE:
                statusTag.getStyle().set("background", "rgb(234 67 53)"); // Red
                statusTag.getStyle().setBorder("2px solid rgb(234 67 53 / 22%)"); // Red
                statusTag.getStyle().set("color", "rgb(234 67 53)");
                break;
        }

        // Common styles for all tags
        statusTag.getStyle().set("padding", "3px");
        statusTag.getStyle().set("width", "60px");
        statusTag.getStyle().set("border-radius", "14px");
        statusTag.getStyle().set("font-size", "12px");
        statusTag.getStyle().set("font-weight", "600");
        statusTag.getStyle().set("display", "inline-block");// Ensure it behaves like a tag
        statusTag.getStyle().setTextAlign(Style.TextAlign.CENTER);
        return statusTag;
    }

}

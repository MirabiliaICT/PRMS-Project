package ng.org.mirabilia.pms.domain.enums;

import com.vaadin.flow.component.html.Span;

public class CreateFinanceStatusTag {

    public Span createFinanceStatusTag(FinanceStatus status) {
        Span statusTag = new Span(status.toString()); // Display the status as text

        // Apply styles based on the status
        switch (status) {
            case APPROVED:
                statusTag.getStyle().set("background", "#ECAA60"); // Green
                statusTag.getStyle().set("color", "#FFFFFF");
                break;
            case PENDING:
                statusTag.getStyle().set("background", "#34A853"); // Red
                statusTag.getStyle().set("color", "#FFFFFF");
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

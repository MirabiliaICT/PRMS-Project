package ng.org.mirabilia.pms.views.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.icon.Icon;

public class NavItem extends HorizontalLayout {

    public NavItem(Icon icon, String label) {
        icon.setSize("24px");  // Set icon size to 24px
        Span text = new Span(label);  // Create a label span

        add(icon, text);  // Add icon and label to the layout
        setAlignItems(Alignment.CENTER);  // Center align items vertically
        setSpacing(true);  // Enable spacing between icon and text
    }
}

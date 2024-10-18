package ng.org.mirabilia.pms.views.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.icon.Icon;

public class NavItem extends HorizontalLayout {

    public NavItem(Icon icon, String label) {
        icon.setSize("24px");
        Span text = new Span(label);

        add(icon, text);
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
    }
}

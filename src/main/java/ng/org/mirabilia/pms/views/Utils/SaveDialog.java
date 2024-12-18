package ng.org.mirabilia.pms.views.Utils;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;

public class SaveDialog extends Dialog {
    Icon successIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
    H4 successDialogTitle;
    Html successText;
    Button close = new Button("Close", event -> this.close());
    HorizontalLayout buttonLayout = new HorizontalLayout();
    VerticalLayout successDialog = new VerticalLayout();
    public SaveDialog(String userCode, String password){
        successDialogTitle = new H4("User Created Successfully");

        successText = new Html(
                "<div>User Code: "+ userCode+ "<br> Password: "+ password+
                        " </div>"
        );

        buttonLayout.add(close);
        successDialog.add(successIcon, successDialogTitle, successText, buttonLayout);

        //Inline Styling
        successDialog.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        successIcon.getStyle().setMaxWidth("31px").setMaxHeight("31px").setColor("#162868");
        successDialogTitle.getStyle().setColor("#162868").setFontWeight("600");
        successDialog.setMaxHeight("279px");
        successDialog.setMaxWidth("428px");
        successText.getStyle().setColor("#6B6B6B");
        close.getStyle().setBackgroundColor("white").setColor("#FF1400").setBorder("1px solid #FF1400");

        //displaying
        add(successDialog);
    }
}

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


public class LogOutDialog extends Dialog{

    Icon logOutIcon = new Icon(VaadinIcon.SIGN_OUT);
    H4 logOutDialogTitle = new H4("Do you want to log out?");
    Html logOutText = new Html(
            "<div>Are you sure you want to logout from PMS Web <br>" +
                    " Application? All unsaved changes will be lost.</div>"
    );
    public Button logOutButton = new Button("Logout");
    Button cancel = new Button("Cancel",  event -> this.close());

    HorizontalLayout buttonLayout = new HorizontalLayout();
    VerticalLayout logOutDialogLayout = new VerticalLayout();


    public LogOutDialog(){
        buttonLayout.add(logOutButton, cancel);
        logOutDialogLayout.add(logOutIcon, logOutDialogTitle, logOutText, buttonLayout);

        //Inline Styling
        logOutDialogLayout.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        logOutIcon.getStyle().setMaxWidth("31px").setMaxHeight("31px").setColor("#162868");
        logOutDialogTitle.getStyle().setColor("#162868").setFontWeight("600");
        logOutDialogLayout.setMaxHeight("279px");
        logOutDialogLayout.setMaxWidth("428px");
        logOutText.getStyle().setColor("#6B6B6B");
        logOutButton.getStyle().setBackgroundColor("#162868").setColor("white");
        cancel.getStyle().setBackgroundColor("white").setColor("#FF1400").setBorder("1px solid #FF1400");

        //displaying
        add(logOutDialogLayout);
    }
}


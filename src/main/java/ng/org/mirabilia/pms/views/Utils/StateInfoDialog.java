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
import ng.org.mirabilia.pms.domain.entities.State;


public class StateInfoDialog extends Dialog{

    State state;

    Icon stateInfoIcon;
    H4 title;
    Html infoBody ;
    Button edit;
    Button cancel;
    HorizontalLayout buttonLayout = new HorizontalLayout();
    VerticalLayout logOutDialogLayout = new VerticalLayout();


    public StateInfoDialog(State state, Runnable runnable){
        this.state = state;

        stateInfoIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        title = new H4("State Info");
        infoBody = new Html(
                "<div>State: " + state.getName() +" <br>" +
                        " State Code: " + state.getStateCode() + "</div>"
        );

        edit = new Button("Edit",  event -> {runnable.run();this.close();});
        edit.addClassName("custom-save-button");
        cancel = new Button("Cancel",  event -> this.close());

        buttonLayout.add(cancel, edit);
        logOutDialogLayout.add(stateInfoIcon, title, infoBody, buttonLayout);

        //Inline Styling
        logOutDialogLayout.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        stateInfoIcon.getStyle().setMaxWidth("31px").setMaxHeight("31px").setColor("#162868");
        title.getStyle().setColor("#162868").setFontWeight("600");
        logOutDialogLayout.setMaxHeight("279px");
        logOutDialogLayout.setMaxWidth("428px");
        logOutDialogLayout.setPadding(false);
        infoBody.getStyle().setColor("#6B6B6B");
        cancel.getStyle().setBackgroundColor("white").setColor("#FF1400").setBorder("1px solid #FF1400");

        //displaying
        add(logOutDialogLayout);
    }
}


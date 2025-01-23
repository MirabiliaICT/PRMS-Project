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
import ng.org.mirabilia.pms.domain.entities.Phase;


public class PhaseInfoDialog extends Dialog{

    Phase phase;

    Icon infoIcon;
    H4 title;
    Html infoBody ;
    Button edit;
    Button cancel;

    HorizontalLayout buttonLayout = new HorizontalLayout();
    VerticalLayout dialogLayout = new VerticalLayout();


    public PhaseInfoDialog(Phase phase, Runnable runnable){
        this.phase = phase;


        infoIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        title = new H4("Phase Info");
        infoBody = new Html(
                "<div>Phase: " + phase.getName() +" <br>" +
                        "Phase Code: " + phase.getPhaseCode() + "<br>" +
                        "City: " + phase.getCity().getName() + "<br>" +
                        "State: " + phase.getCity().getState().getName() +  " </div>"
        );

        edit = new Button("Edit",  event -> {runnable.run(); this.close();});
        edit.addClassName("custom-save-button");
        cancel = new Button("Cancel",  event -> this.close());

        buttonLayout.add(cancel, edit);
        dialogLayout.add(infoIcon, title, infoBody, buttonLayout);

        //Inline Styling
        dialogLayout.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        infoIcon.getStyle().setMaxWidth("31px").setMaxHeight("31px").setColor("#162868");
        title.getStyle().setColor("#162868").setFontWeight("600");
        dialogLayout.setMaxHeight("279px");
        dialogLayout.setMaxWidth("428px");
        dialogLayout.setPadding(false);
        infoBody.getStyle().setColor("#6B6B6B");
        cancel.getStyle().setBackgroundColor("white").setColor("#FF1400").setBorder("1px solid #FF1400");

        //displaying
        add(dialogLayout);
    }
}


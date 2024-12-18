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

public class DeleteDialog extends Dialog {
    Icon deleteIcon = new Icon(VaadinIcon.TRASH);
    H4 deleteDialogTitle = new H4("Do you want to delete this user?");
    Html deleteText = new Html(
            "<div>Are you sure you want to delete this user <br>" +
                    " You won't be able to retrieve the user again.</div>"
    );
    public Button deleteButton = new Button("Delete");
    Button cancel = new Button("Cancel",  event -> this.close());
    HorizontalLayout buttonLayout = new HorizontalLayout();
    VerticalLayout deleteDialogLayout = new VerticalLayout();


    public DeleteDialog(Runnable deleteRunnable){

        deleteButton.addClickListener((e)->{
            deleteRunnable.run();
            close();
        });
        buttonLayout.add(deleteButton, cancel);
        deleteDialogLayout.add(deleteIcon, deleteDialogTitle, deleteText, buttonLayout);

        //Inline Styling
        deleteDialogLayout.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        deleteIcon.getStyle().setMaxWidth("31px").setMaxHeight("31px").setColor("#162868");
        deleteDialogTitle.getStyle().setColor("#162868").setFontWeight("600");
        deleteDialogLayout.setMaxHeight("279px");
        deleteDialogLayout.setMaxWidth("428px");
        deleteText.getStyle().setColor("#6B6B6B");
        deleteButton.getStyle().setBackgroundColor("red ").setColor("white");
        cancel.getStyle().setBackgroundColor("white").setColor("#FF1400").setBorder("1px solid #FF1400");

        //displaying
        add(deleteDialogLayout);
    }
}

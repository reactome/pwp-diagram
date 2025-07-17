package org.reactome.web.diagram.search.results.scopebar;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ScopeButton extends Button {
    private FlowPanel fp;
    private Image buttonImg;
    private Label buttonLbl;

    private String text;
    private int total;
    private int current;

    public ScopeButton(String text, String tooltip, ImageResource imageResource, ClickHandler handler) {
        this.text = text;
        buttonImg = new Image(imageResource);
        buttonLbl = new Label(text);
        buttonLbl.setTitle(tooltip);

        fp = new FlowPanel();
        fp.add(buttonImg);
        fp.add(buttonLbl);
        addClickHandler(handler);
        update();
    }

    public void setTotal(int total) {
        this.total = total;
        updatetNumbers();
    }

    public void setCurrent(int current) {
        this.current = current;
        updatetNumbers();
    }

    public void updatetNumbers() {
        String msg = text;
        if (total != 0) {
            msg += " (" + (current != total && current != 0 ? current + " of ": "") + total + ")";
        }
        buttonLbl.setText(msg);
        update();
    }

    private void update() {
        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        setHTML(safeHtml);
    }
}
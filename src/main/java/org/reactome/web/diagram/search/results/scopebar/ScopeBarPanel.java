package org.reactome.web.diagram.search.results.scopebar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * A panel that can accommodate one or many buttons ({@link ScopeButton})
 * acting as a selector and enabling the user to change scope.
 *
 * On click of a button, the {@link Handler} is notified with the
 * index of that button.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ScopeBarPanel extends FlowPanel implements ClickHandler {
    private List<ScopeButton> btns = new LinkedList<>();
    private ScopeButton activeButton;
    private Handler handler;

    public interface Handler {
        void onScopeChanged(int selected);
    }

    public ScopeBarPanel(Handler handler) {
        this.handler = handler;
        setStyleName(RESOURCES.getCSS().container());
    }

    public void addButton(String text, String tooltip, ImageResource imageResource) {
        ScopeButton btn = new ScopeButton(text, tooltip, imageResource, this);
        btns.add(btn);
        if (btns.size() == 1) {
            setActiveButton(btn);
        }
        add(btn);
    }

    @Override
    public void onClick(ClickEvent event) {
        ScopeButton button = (ScopeButton) event.getSource();
        if (button.equals(activeButton)) return;
        setActiveButton(button);

        handler.onScopeChanged(btns.indexOf(button));
    }


    public void setTotalResultsNumber(int buttonIndex, int number) {
        ScopeButton btn = btns.get(buttonIndex);
        if (btn != null) {
            btn.setTotal(number);
        }
    }

    public void setCurrentResultsNumber(int buttonIndex, int number) {
        ScopeButton btn = btns.get(buttonIndex);
        if (btn != null) {
            btn.setCurrent(number);
        }
    }

    private void setActiveButton(ScopeButton button) {
        if (activeButton!=null) activeButton.removeStyleName(RESOURCES.getCSS().buttonSelected());
        button.addStyleName(RESOURCES.getCSS().buttonSelected());
        activeButton = button;
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../../images/scope_global.png")
        ImageResource scopeGlobal();

        @Source("../../images/scope_local.png")
        ImageResource scopeLocal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ScopeBarPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/results/scopebar/ScopeBarPanel.css";

        String container();

        String buttonSelected();
    }
}

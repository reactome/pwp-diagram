package org.reactome.web.diagram.controls.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.OptionalWidget;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.controls.settings.tabs.AboutTabPanel;
import org.reactome.web.diagram.controls.settings.tabs.InteractorsTabPanel;
import org.reactome.web.diagram.controls.settings.tabs.ProfilesTabPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class HideableContainerPanel extends FlowPanel implements ClickHandler, OptionalWidget.Handler {
    private boolean isExpanded = false;
    private final List<Button> btns = new LinkedList<>();
    protected Button profilesBtn;
    protected Button interactorsBtn;
    protected Button aboutBtn;
    private PwpButton showBtn;

    private DeckLayoutPanel container;

    public HideableContainerPanel(EventBus eventBus) {
        setStyleName(RESOURCES.getCSS().wrapper());
        addStyleName(RESOURCES.getCSS().wrapperInitial());

        FlowPanel buttonsPanel = new FlowPanel();               // Tab buttons panel
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.addStyleName(RESOURCES.getCSS().unselectable());

        this.container = new DeckLayoutPanel();                 // Main tab container
        this.container.setStyleName(RESOURCES.getCSS().container());

        if (OptionalWidget.COLOUR_PROFILE.isVisible()) {
            buttonsPanel.add(this.profilesBtn = getButton("Colour Profiles", RESOURCES.profilesTabIcon()));
            ProfilesTabPanel profilesTabPanel = new ProfilesTabPanel(eventBus);
            this.container.add(profilesTabPanel);
        }

        if (OptionalWidget.INTERACTORS.isVisible()) {
            buttonsPanel.add(this.interactorsBtn = getButton("Interactors", RESOURCES.interactorsTabIcon()));
            InteractorsTabPanel interactorsTabPanel = new InteractorsTabPanel(eventBus);
            this.container.add(interactorsTabPanel);
        }

        if (OptionalWidget.ABOUT.isVisible()) {
            buttonsPanel.add(this.aboutBtn = getButton("About", RESOURCES.aboutTabIcon()));
            AboutTabPanel aboutTabPanel = new AboutTabPanel("About the Pathway Diagram", RESOURCES.aboutThis());
            this.container.add(aboutTabPanel);
        }
        this.btns.get(0).addStyleName(RESOURCES.getCSS().buttonSelected());
        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);

        FlowPanel outerPanel = new FlowPanel();                 // Vertical tab Panel and buttons container
        outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
        outerPanel.add(buttonsPanel);
        outerPanel.add(this.container);

        showBtn = new PwpButton("Show/Hide settings", RESOURCES.getCSS().showHide(), clickEvent -> HideableContainerPanel.this.toggle());

        InlineLabel header = new InlineLabel("Settings");
        header.setStyleName(RESOURCES.getCSS().headerLabel());
        FlowPanel mainPanel = new FlowPanel();                         // Main panel
        mainPanel.add(showBtn);
        mainPanel.add(header);
        mainPanel.add(outerPanel);
        add(mainPanel);
    }

    private void collapse() {
        removeStyleName(RESOURCES.getCSS().wrapperInitial());
        if (isExpanded) {
            removeStyleName(RESOURCES.getCSS().wrapperExpanded());
            showBtn.removeStyleName(RESOURCES.getCSS().showHideRight());
            isExpanded = false;
        }
    }

    private void expand() {
        if (!isExpanded) {
            addStyleName(RESOURCES.getCSS().wrapperExpanded());
            showBtn.addStyleName(RESOURCES.getCSS().showHideRight());
            isExpanded = true;
        }
    }

    private void toggle() {
        if (!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    public Button getButton(String text, ImageResource imageResource) {
        FlowPanel fp = new FlowPanel();
        Image image = new Image(imageResource);
        image.addStyleName(RESOURCES.getCSS().undraggable());
        fp.add(image);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        btn.setTitle(text);
        this.btns.add(btn);
        return btn;
    }

    @Override
    public void onClick(ClickEvent event) {
        Button selectedBtn = (Button) event.getSource();
        for (Button btn : btns) {
            btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
        }
        selectedBtn.addStyleName(RESOURCES.getCSS().buttonSelected());
        if (!isExpanded) {
            expand();
            container.showWidget(btns.indexOf(selectedBtn));
        } else if (btns.indexOf(selectedBtn) != container.getVisibleWidgetIndex()) {
            container.showWidget(btns.indexOf(selectedBtn));
        } else {
            // The user has clicked on the already selected tab button
            collapse();
        }
    }



    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("tabs/aboutDiagram.html")
        TextResource aboutThis();

        @Source("images/profiles.png")
        ImageResource profilesTabIcon();

        @Source("images/interactors.png")
        ImageResource interactorsTabIcon();

        @Source("images/about.png")
        ImageResource aboutTabIcon();

        @Source("images/showHide.png")
        ImageResource showHideIcon();

        @Source("images/showHide_blue.png")
        ImageResource showHideHoveredIcon();
    }

    @CssResource.ImportedWithPrefix("diagram-HideableContainerPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/HideableContainerPanel.css";

        String wrapper();

        String wrapperInitial();

        String wrapperExpanded();

        String outerPanel();

        String buttonsPanel();

        String buttonSelected();

        String unselectable();

        String undraggable();

        String container();

        String showHide();

        String showHideRight();

        String headerLabel();

    }
}

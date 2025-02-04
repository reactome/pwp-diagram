package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ExportDialog extends PopupPanel implements ClickHandler {
    private Button snapshotBtn;
    private Button diagramExportBtn;
    private Button contentExportBtn;
    private List<Button> btns = new LinkedList<>();
    private DeckLayoutPanel tabContainer;

    private final String diagramStId;
    private final Long diagramId;
    private final String selected;
    private final String flagged;
    private final Boolean includeInteractors;
    private final Image snapshot;
    private final AnalysisStatus status;

    private final Content.Type contentType;

    public ExportDialog(final Context context, final String selected, final String flagged, final Boolean includeInteractors, final Image snapshot){
        super();
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);

        this.diagramStId = context.getContent().getStableId();
        this.diagramId = context.getContent().getDbId();
        this.snapshot = snapshot;
        this.status = context.getAnalysisStatus();
        this.selected = selected;
        this.flagged = flagged;
        this.includeInteractors = includeInteractors;
        this.contentType = context.getContent().getType();

        // Initialise the dialog
        initUI();
    }


    @Override
    public void onClick(ClickEvent event) {
        for (Button btn : btns) {
            btn.removeStyleName(RESOURCES.getCSS().tabButtonSelected());
        }
        Button btn = (Button) event.getSource();
        btn.addStyleName(RESOURCES.getCSS().tabButtonSelected());
        if (btn.equals(this.snapshotBtn)) {
            this.tabContainer.showWidget(0);
        } else if(btn.equals(this.diagramExportBtn)) {
            this.tabContainer.showWidget(1);
        } else if(btn.equals(this.contentExportBtn)) {
            this.tabContainer.showWidget(2);
        }
    }

    public void showCentered() {
        super.center();
        setVisible(false);
        // This is necessary to center the panel in Firefox, where getOffsetWidth() and getOffsetHeight()
        // do not return the correct values the first time they are called (even in a deferred call)
        new Timer() {
            @Override
            public void run() {
                int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
                setPopupPosition(Math.max(Window.getScrollLeft() + left, 0),
                        Math.max(Window.getScrollTop() + top, 0));
                setVisible(true);
            }
        }.schedule(20);
    }

    private void initUI() {
        this.addStyleName(RESOURCES.getCSS().popupPanel());

        FlowPanel mainPanel = new FlowPanel();                         // Main panel
        mainPanel.addStyleName(RESOURCES.getCSS().mainPanel());
        mainPanel.add(getTitlePanel());                                // Title panel with label & button
        mainPanel.add(getTabPanel());                                  // A tab container with vertical tab buttons
        this.add(mainPanel);
    }

    private Widget getTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        header.add(image);
        Label title = new Label("Export diagram");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new IconButton(RESOURCES.closeIcon(), RESOURCES.getCSS().close(), "Close", clickEvent -> ExportDialog.this.hide());

        header.add(title);
        header.add(closeBtn);

        return header;
    }

    private Widget getTabPanel() {
        FlowPanel tabButtonsPanel = new FlowPanel();                // Tab buttons panel
        tabButtonsPanel.setStyleName(RESOURCES.getCSS().tabButtonsPanel());
        tabButtonsPanel.addStyleName(RESOURCES.getCSS().unselectable());
        tabButtonsPanel.add(this.snapshotBtn = getTabButton("Get a snapshot", "Download a snapshot of the current view", RESOURCES.cameraIcon()));
        tabButtonsPanel.add(this.diagramExportBtn = getTabButton("Export diagram", "Export a high resolution image of the diagram", RESOURCES.diagramIcon()));
        tabButtonsPanel.add(this.contentExportBtn = getTabButton("Export content", "Export the content of the diagram",RESOURCES.contentIcon()));
        this.snapshotBtn.addStyleName(RESOURCES.getCSS().tabButtonSelected());

        this.tabContainer = new DeckLayoutPanel();                 // Main tab container
        this.tabContainer.setStyleName(RESOURCES.getCSS().tabContainer());

        this.tabContainer.add(new SnapshotTabPanel(diagramStId, snapshot, contentType));
        this.tabContainer.add(new ImageTabPanel(diagramStId, selected, flagged, includeInteractors, status, DiagramColours.get().getSelectedProfileName(), AnalysisColours.get().getSelectedProfileName(), contentType));
        this.tabContainer.add(new ContentTabPanel(diagramStId, diagramId));

        this.tabContainer.showWidget(0);
        this.tabContainer.setAnimationVertical(true);
        this.tabContainer.setAnimationDuration(400);

        FlowPanel outerPanel = new FlowPanel();                     // Vertical tab Panel and buttons container
        outerPanel.setStyleName(RESOURCES.getCSS().tabOuterPanel());
        outerPanel.add(tabButtonsPanel);
        outerPanel.add(this.tabContainer);

        return outerPanel;
    }

    private Button getTabButton(String text, String tooltip, ImageResource imageResource){
        Image image = new Image(imageResource);
        image.addStyleName(RESOURCES.getCSS().undraggable());
        FlowPanel fp = new FlowPanel();
        fp.setTitle(tooltip);
        fp.add(image);
        fp.add(new Label(text));

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        btn.setStyleName(RESOURCES.getCSS().tabButton());
        this.btns.add(btn);
        return btn;
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

        @Source("../images/header_icon.png")
        ImageResource headerIcon();

        @Source("../images/close.png")
        ImageResource closeIcon();

        @Source("../images/camera.png")
        ImageResource cameraIcon();

        @Source("../images/export_diagram.png")
        ImageResource diagramIcon();

        @Source("../images/export_content.png")
        ImageResource contentIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ExportDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/export/ExportDialog.css";

        String popupPanel();

        String mainPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String tabButtonsPanel();

        String tabButton();

        String tabButtonSelected();

        String tabContainer();

        String tabOuterPanel();

    }
}

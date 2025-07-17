package org.reactome.web.diagram.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsEvent;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsHandler;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.BoundFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ContextDialogPanel extends DialogBox implements ClickHandler, GraphObjectSelectedHandler {

	private EventBus eventBus;
	
    private DiagramObject item;
    private GraphObject graphObject;
    private Context context;
    private Widget canvas;

    private boolean pinned = false;
    private boolean displayIds = false;

    private Button changeLabels;
    private Button pin;
    private Button close;
    
    private ContextInfoPanel contextInfoPanel;

    public ContextDialogPanel(EventBus eventBus, DiagramObject item, Context context, Widget canvas) {
        super();
        setAutoHideEnabled(true);
        setModal(false);
        setStyleName(RESOURCES.getCSS().popup());

        this.eventBus = eventBus;
        this.item = item;
        this.graphObject = item.getGraphObject();
        this.context = context;
        this.canvas = canvas;

        FlowPanel fp = new FlowPanel();
        fp.add(this.changeLabels = new PwpButton("Show/hide Identifiers", RESOURCES.getCSS().labels(), this));
        fp.add(this.pin = new PwpButton("Keeps the panel visible", RESOURCES.getCSS().pin(), this));
        fp.add(this.close = new PwpButton("Close", RESOURCES.getCSS().close(), this));
        fp.add(contextInfoPanel = new ContextInfoPanel(this, eventBus, item, context));

        setTitlePanel();
        setWidget(fp);
        this.addStyleName(RESOURCES.getCSS().popupSelected());
        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);

        show(true);
    }

    public HandlerRegistration addChangeLabelsEventHandler(ChangeLabelsHandler handler){
        return addHandler(handler, ChangeLabelsEvent.TYPE);
    }

    public ContextDialogPanel setDiagramObject(DiagramObject item) {
        this.item = item;
        return this;
    }

    @Override
    public void hide(boolean autoClosed) {
        //The idea is to keep the panels open if the pin is pressed
        if(autoClosed && !this.pinned) {
            //noinspection ConstantConditions
            super.hide(autoClosed);
        //Or close them in any case when the hide is not automatically triggered ;)
        }else if(!autoClosed){
            //noinspection ConstantConditions
            super.hide(autoClosed);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if(btn.equals(close)){
            this.pinned = false;
            hide();
        }else if(btn.equals(pin)){
            this.pinned = !this.pinned;
        }else if(btn.equals(changeLabels)){
            this.displayIds = !this.displayIds;
            //Apply the right style here
            if(this.displayIds) {
                changeLabels.setStyleName(RESOURCES.getCSS().labelsActive());
            }else {
                changeLabels.setStyleName(RESOURCES.getCSS().labels());
            }
            fireEvent(new ChangeLabelsEvent(displayIds));
        }
        //Apply the right style here
        if(this.pinned) {
            pin.setStyleName(RESOURCES.getCSS().pinActive());
        }else {
            pin.setStyleName(RESOURCES.getCSS().pin());
        }
    }
    
    public void redraw() {
    	this.contextInfoPanel.redraw();
    }

    public void restore(){
        if(this.pinned) this.show();
    }

    public void show(boolean resetPosition) {
        if(resetPosition){
            setPosition();
        }
        super.show();
    }

    private void setTitlePanel() {
        FlowPanel fp = new FlowPanel();
        Image img = new Image(this.item.getGraphObject().getImageResource());
        fp.add(img);

        InlineLabel title = new InlineLabel(this.item.getDisplayName());
        title.setTitle(this.item.getDisplayName());
        fp.add(title);

        SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(fp.toString());
        getCaption().setHTML(safeHtml);
        getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
    }

    private void setPosition(){
        Coordinate offset = context.getDiagramStatus().getOffset();
        double factor = context.getDiagramStatus().getFactor();

        Coordinate position = null;
        Bound canvasBounds = BoundFactory.get(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop(), canvas.getOffsetWidth(), canvas.getOffsetHeight());
        if(item instanceof NodeCommon) {
            NodeCommon node = (NodeCommon) item;
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            position = ContextShowStrategy.getPosition(330, 170, prop, canvasBounds);
        }else if (item instanceof EdgeCommon){
            EdgeCommon edge = (EdgeCommon) item;
            Shape shape = ShapeFactory.transform(edge.getReactionShape(), factor, offset);
            position = ContextShowStrategy.getPosition(330, 170, shape, canvasBounds);
        }

        if(position!=null) {
            setPopupPosition(position.getX().intValue(), position.getY().intValue());
        }
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        if (graphObject.equals(event.getGraphObject())){
            this.addStyleName(RESOURCES.getCSS().popupSelected());
        } else {
            this.removeStyleName(RESOURCES.getCSS().popupSelected());
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


        @Source("images/id_clicked.png")
        ImageResource idClicked();

        @Source("images/id_normal.png")
        ImageResource idNormal();

        @Source("images/id_hovered.png")
        ImageResource idHovered();

        @Source("images/pin_clicked.png")
        ImageResource pinClicked();

        @Source("images/pin_hovered.png")
        ImageResource pinHovered();

        @Source("images/pin_normal.png")
        ImageResource pinNormal();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

    }

    @CssResource.ImportedWithPrefix("diagram-ContextPopupPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/ContextDialogPanel.css";

        String popup();

        String popupSelected();

        String header();

        String labels();

        String labelsActive();

        String pin();

        String pinActive();

        String close();
        
    }
}

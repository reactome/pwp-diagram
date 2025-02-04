package org.reactome.web.diagram.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramInfo extends AbsolutePanel implements DiagramRenderedHandler,
        LayoutLoadedHandler, GraphLoadedHandler, ContentRequestedHandler, ContentLoadedHandler {

    private EventBus eventBus;

    private String stId = null;

    private InlineLabel renderTime;
    private InlineLabel items;
    private InlineLabel layoutTime;
    private InlineLabel graphTime;

    public DiagramInfo(EventBus eventBus) {
        this.eventBus = eventBus;
        this.setStyle(200, 80);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(getInfoPanel("Visible items", this.items = new InlineLabel("Loading...")));
        verticalPanel.add(getInfoPanel("Rendering time", this.renderTime = new InlineLabel("Loading...")));
        verticalPanel.add(getInfoPanel("Layout time", this.layoutTime = new InlineLabel("Loading...")));
        verticalPanel.add(getInfoPanel("Graph time", this.graphTime = new InlineLabel("Loading...")));
        this.add(verticalPanel);

        this.initHandlers();
    }

    private void initHandlers() {
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRenderedEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        this.eventBus.addHandler(GraphLoadedEvent.TYPE, this);
    }


    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            if (!event.getContext().getContent().getStableId().equals(stId)) {
                this.layoutTime.setText("0 ms");
                this.graphTime.setText("0 ms");
                this.stId = event.getContext().getContent().getStableId();
            }
        }
    }

    @Override
    public void onDiagramRendered(DiagramRenderedEvent event) {
        this.renderTime.setText((int) event.getTime() + " ms");
        this.items.setText(event.getItems() + "");
    }


    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.items.setText("Loading...");
        this.renderTime.setText("Loading...");
        this.layoutTime.setText("Loading...");
        this.graphTime.setText("Loading...");
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.stId = event.getContext().getContent().getStableId();
        this.layoutTime.setText((int) event.getTime() + " ms");
    }

    @Override
    public void onGraphLoaded(GraphLoadedEvent event) {
        this.graphTime.setText((int) event.getTime() + " ms");
    }

    private Widget getInfoPanel(String title, Widget holder) {
        FlowPanel fp = new FlowPanel();
        fp.add(new InlineLabel(title + ": "));
        fp.add(holder);
        return fp;
    }

    private void setStyle(double w, double h) {
        this.setWidth(w + "px"); this.setHeight(h + "px");
        Style style = this.getElement().getStyle();
        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
        style.setRight(0, Style.Unit.PX);

        style.setOverflow(Style.Overflow.SCROLL);
    }
}

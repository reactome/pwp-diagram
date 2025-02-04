package org.reactome.web.diagram.messages;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.DiagramInternalErrorHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LoadingMessage extends MessagesPanel implements ContentRequestedHandler, LayoutLoadedHandler, ContentLoadedHandler, DiagramInternalErrorHandler {

    public LoadingMessage(EventBus eventBus) {
        super(eventBus);

        MessagesPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.loadingMessage());

        InlineLabel lb = new InlineLabel("Loading diagram...");


        FlowPanel fp = new FlowPanel();
        fp.add(new Image(RESOURCES.loader()));
        fp.add(lb);
        this.add(fp);

        this.initHandlers();
    }

    @Override
    public void onDiagramInternalError(DiagramInternalErrorEvent event) {
        if(event.getSource().getClass().equals(LoaderManager.class)) {
            this.setVisible(false);
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setVisible(false);
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramInternalErrorEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }
}

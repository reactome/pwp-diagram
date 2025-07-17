package org.reactome.web.diagram.messages;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.DiagramInternalErrorHandler;
import org.reactome.web.diagram.util.Console;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ErrorMessage extends MessagesPanel implements AnalysisResultRequestedHandler, AnalysisResultLoadedHandler,
        ContentRequestedHandler, DiagramInternalErrorHandler, ClickHandler {

    private Label msgTitle;
//    private Label msgDetails;

    public ErrorMessage(EventBus eventBus) {
        super(eventBus);

        MessagesPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.errorMessage());

        FlowPanel fp = new FlowPanel();
        fp.add(new Image(RESOURCES.error()));

        PwpButton closeBtn = new PwpButton("close", RESOURCES.getCSS().close(), this);
        fp.add(closeBtn);

        msgTitle = new Label("Title placeHolder");
        msgTitle.setStyleName(css.errorMessageTitle());

        FlowPanel textSpace = new FlowPanel();
        textSpace.setStyleName(css.errorMessageText());
        textSpace.add(msgTitle);
        fp.add(textSpace);

        this.add(fp);
        setVisible(false);
        this.initHandlers();
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        this.setVisible(false);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onDiagramInternalError(DiagramInternalErrorEvent event) {
        resetText();
        String title = event.getMessage();
        msgTitle.setText(title);

        String details = event.getDetails();
        if(!details.isEmpty()) {
            Console.error(title);
            Console.error(details);
        }
        this.setVisible(true);
    }

    private void initHandlers() {
        this.eventBus.addHandler(DiagramInternalErrorEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
    }

    private void resetText() {
        msgTitle.setText("");
    }
}

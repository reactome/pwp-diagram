package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FlaggedItemsControl extends LegendPanel implements ClickHandler, ChangeHandler,
        ContentRequestedHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler, DiagramObjectsFlagRequestHandler,
        AnalysisResultLoadedHandler, AnalysisResetHandler {

    protected InlineLabel msgLabel;
    private Button closeBtn;
    protected Image loadingIcon;
    protected InlineLabel interactorsLabel;
    protected ListBox selector;

    protected String term;
    protected Boolean includeInteractors = false;

    public FlaggedItemsControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.flaggedItemsControl());

        loadingIcon = new Image(RESOURCES.loader());
        loadingIcon.setStyleName(css.interactorsControlLoadingIcon());
        this.add(loadingIcon);

        this.msgLabel = new InlineLabel();
        this.msgLabel.setStyleName(RESOURCES.getCSS().flaggedItemsLabel());
        this.add(this.msgLabel);

        this.closeBtn = new PwpButton("Close and un-flag entities", css.close(), this);
        this.add(this.closeBtn);

        this.interactorsLabel = new InlineLabel("Interactors:");
        this.interactorsLabel.setTitle("Allows interactors to be taken into account during flagging");
        this.add(this.interactorsLabel);

        this.selector = new ListBox();
        this.selector.addChangeHandler(this);
        this.selector.addItem("Include", "true");
        this.selector.addItem("Exclude", "false");
        this.add(this.selector);

        this.initHandlers();
        this.setVisible(false);
    }


    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        this.removeStyleName(RESOURCES.getCSS().flaggedItemsControlMovedUp());
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        this.addStyleName(RESOURCES.getCSS().flaggedItemsControlMovedUp());
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if(btn.equals(this.closeBtn)){
            eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        this.includeInteractors = Boolean.valueOf(this.selector.getSelectedValue());
        eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(term, includeInteractors), this);
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        this.term = event.getTerm();
        this.includeInteractors = event.getIncludeInteractors();

        Set<DiagramObject> flaggedItems =  event.getFlaggedItems();
        String msg = " - " + flaggedItems.size() + (flaggedItems.size() == 1 ? " entity" : " entities") + " flagged";
        this.msgLabel.setText(term + msg);
        this.loadingIcon.setVisible(false);
        this.interactorsLabel.setVisible(true);
        this.selector.setVisible(true);
        updateSelectorValue();
        setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        this.term = event.getTerm();
        this.includeInteractors = event.getIncludeInteractors();

        this.interactorsLabel.setVisible(false);
        this.selector.setVisible(false);
        updateSelectorValue();
        this.loadingIcon.setVisible(true);
        this.msgLabel.setText("Flagging entities for " + term + "...");
        this.setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        this.term = null;
        this.setVisible(false);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(false);
    }

    private void initHandlers() {
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
    }

    private void updateSelectorValue() {
        selector.setSelectedIndex(includeInteractors ? 0 : 1);
    }
}

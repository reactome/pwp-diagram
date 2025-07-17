package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.analysis.client.AnalysisClient;
import org.reactome.web.analysis.client.AnalysisHandler;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.AnalysisError;
import org.reactome.web.analysis.client.model.PathwaySummary;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FilterAlertControl extends LegendPanel implements ClickHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler, AnalysisHandler.Summaries,
        ContentRequestedHandler, ContentLoadedHandler, GraphObjectSelectedHandler {

    private static String MSG = "Please keep in mind that the overlaid result may not be accurate as it does not take into account the applied filter, which has excluded###PATHWAY###the following displayed subpathways:";
    private static String MSG_SELECTION = "Please keep in mind that the overlaid result may not be accurate as the selected pathway falls outside the defined filter";

    private Context context;
    private String token;
    private ResultFilter filter;
    private boolean isExpanded;
    private List<String> allSubpathwayIds;
    private GraphObject selected;

    private Image icon;
    private InlineLabel message;
    private Label learnMore;
    private Label hideMe;

    private FlowPanel infoPanel;

    public FilterAlertControl(final EventBus eventBus) {
        super(eventBus);
        allSubpathwayIds = new LinkedList<>();

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.filterAlertControl());

        this.icon = new Image(RESOURCES.warningIcon());
        this.add(icon);

        this.message = new InlineLabel();
        this.add(this.message);

        this.hideMe = new Label("Hide");
        this.hideMe.setStyleName(css.hide());
        this.hideMe.addClickHandler(this);
        this.add(hideMe);

        this.learnMore = new Label("Learn More");
        this.learnMore.setStyleName(css.learnMore());
        this.learnMore.addClickHandler(this);
        this.add(learnMore);

        this.infoPanel = new FlowPanel();
        this.infoPanel.setStyleName(RESOURCES.getCSS().infoPanel());
        this.add(infoPanel);

        this.initHandlers();
        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.learnMore)) {
            toggleExpandedPanel();
        } else if (source.equals(this.hideMe)) {
            hide();
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()) {
            case OVERREPRESENTATION:
            case EXPRESSION:
            case SPECIES_COMPARISON:
            case GSVA:
            case GSA_STATISTICS:
            case GSA_REGULATION: //TODO: This should change to the default behaviour now
                this.token = event.getSummary().getToken();
                this.filter = event.getFilter();
                updateBasedOnFilter(filter);
                break;
            default:
                hide();
        }
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        hide();
    }

    private void hide(){
        if(this.isVisible()) {
            this.message.setText("");
            infoPanel.clear();
            this.setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        hide();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.hide();
        context = null;
    }


    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        selected = event.getGraphObject();
    }

    private void updateBasedOnFilter(ResultFilter filter) {
        if (!isFilterPresent())  {
            hide();
        } else {
            message.setText("A filter has been applied");
            if(isExpanded)
                collapse();
            checkFilteredOutPathways();
        }
    }

    @Override
    public void onPathwaySummariesLoaded(List<PathwaySummary> pathwaySummaries, long time) {
        Set<String> hitPathways = pathwaySummaries.stream().map(summary -> summary.getDbId().toString()).collect(Collectors.toSet());

        boolean displayedIsFilteredOut = false;
        Set<GraphObject> filteredOut = new HashSet<>();
        for (String pId : allSubpathwayIds) {
            GraphObject item = context.getContent().getDatabaseObject(pId);
            if (item instanceof GraphPathway) {
                GraphPathway pathway = (GraphPathway) item;
                if (pathway.isHit() && !hitPathways.contains(item.getDbId().toString())) {
                    filteredOut.add(item);
                }
            } else if (item instanceof GraphSubpathway) {
                GraphSubpathway pathway = (GraphSubpathway) item;
                if (pathway.isHit() && !hitPathways.contains(item.getDbId().toString())) {
                    filteredOut.add(item);
                }
            } else if (pId.equals(context.getContent().getDbId().toString())) {
                displayedIsFilteredOut = (context.getContent().getStatistics() != null && !hitPathways.contains(context.getContent().getDbId().toString()));
            }
        }


        infoPanel.clear();
        FlowPanel details = new FlowPanel();
        if(!filteredOut.isEmpty()) {
            if (selected != null && filteredOut.contains(selected)) {
                details.add(new Label(MSG_SELECTION));
            } else {
                MSG = MSG.replace("###PATHWAY###", displayedIsFilteredOut ? " the displayed pathway and " : " ");
                details.add(new Label(MSG));
                for (GraphObject item : filteredOut) {
                    Label lb = new Label(" \u2022 " + item.getDisplayName());
                    lb.setStyleName(RESOURCES.getCSS().detailsItem());
                    lb.addClickHandler(e -> {
                        if (selected == null || !selected.equals(item)) {
                            eventBus.fireEventFromSource(new GraphObjectSelectedEvent(item, true, true), this);
                        }
                    });
                    lb.addMouseOverHandler(e -> eventBus.fireEventFromSource(new GraphObjectHoveredEvent(item), this));
                    lb.addMouseOutHandler(e -> eventBus.fireEventFromSource(new GraphObjectHoveredEvent(null), this));
                    details.add(lb);
                }

            }

            details.setStyleName(LegendPanel.RESOURCES.getCSS().details());
            infoPanel.add(details);
            makeVisible(200); // Appear with delay
        }

    }

    @Override
    public void onPathwaySummariesNotFound(long time) {

    }

    @Override
    public void onPathwaySummariesError(AnalysisError error) {

    }

    @Override
    public void onAnalysisServerException(String message) {

    }

    private boolean isFilterPresent() {
        boolean rtn = false;
        if (filter != null) {
            rtn = !filter.getResource().equalsIgnoreCase("TOTAL") ||
                    (filter.getpValue() != null && filter.getpValue() < 1d)    ||
                    !filter.getIncludeDisease()                                ||
                    (filter.getMin() != null && filter.getMax() != null);

        }
        return rtn;
    }

    private void checkFilteredOutPathways() {
        if (context == null) return;

        allSubpathwayIds.clear();
        for (GraphObject pathway : context.getContent().getAllInvolvedPathways()) {
            allSubpathwayIds.add(pathway.getDbId().toString());
        }

        allSubpathwayIds.add(context.getContent().getDbId().toString());

        AnalysisClient.getPathwaySummaries(token, filter, allSubpathwayIds, this);
    }


    private void toggleExpandedPanel() {
        if (!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    private void expand() {
        setHeight("100px");
        learnMore.setText("Got It!");
        isExpanded = true;
    }

    private void collapse() {
        setHeight("20px");
        learnMore.setText("Learn More");
        isExpanded = false;
    }

}

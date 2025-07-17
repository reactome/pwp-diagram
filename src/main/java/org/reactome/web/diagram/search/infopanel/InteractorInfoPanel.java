package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.util.MapSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InteractorInfoPanel extends Composite {

    InteractorSearchResult interactor;
    EventBus eventBus;
    FlowPanel mainPanel;

    public InteractorInfoPanel(EventBus eventBus, InteractorSearchResult interactor) {
        this.eventBus = eventBus;
        this.interactor = interactor;
        this.mainPanel = new FlowPanel();

        GraphObjectInfoPanel.SuggestionPanelCSS css = GraphObjectInfoPanel.OBJECT_INFO_RESOURCES.getCSS();
        this.init(css);

        FlowPanel header = new FlowPanel();
        header.setStyleName(css.infoHeader());
        header.add(new InlineLabel(interactor.getDisplayName()));
        this.add(header);
        if(interactor.getAlias()!=null){
            this.add(new Label("Accession: " + interactor.getAccession()));
        }
        this.add(new Label("Type: Interactor"));
        this.add(new Label("Resource: " + interactor.getResource().getName()));
        MapSet<Long, GraphObject> interactsWith = interactor.getInteractsWith();
        for(Long interactionId:interactsWith.keySet()) {
            Set<GraphObject> interactors = new HashSet<>();
            // Identify only those entities that are directly in the
            // diagram and not part of a complex or a set
            for (GraphObject graphObject : interactsWith.getElements(interactionId)) {
                if(!graphObject.getDiagramObjects().isEmpty()){
                    interactors.add(graphObject);
                }
            }
            if (!interactors.isEmpty()) {
                int interactorsSize = interactor.getEvidences(interactionId) != null ? interactor.getEvidences(interactionId) : 0;
                String evidences = interactorsSize == 0 ? "" : (interactorsSize == 1 ? "(" + interactorsSize + " evidence)" : "(" + interactorsSize + " pieces of evidence)");

                Double score = interactor.getInteractionScore(interactionId);
                String title = "Interacts with ";
                title = title + "score: " + (score!=null ? NumberFormat.getFormat("0.000").format(score): "-") + " " + evidences;
                this.add(new DatabaseObjectListPanel(title, interactors, eventBus));
            }
        }
    }
    /**
     * Padding grows wider the div but this is not the expected behaviour here. To make it compatible
     * across all browsers the recommendation is having two divs
     * <div> // defines the object info panel properties
     *     <div> // defines the object info mainPanel (set the padding here)
     *         Info mainPanel
     *     </div>
     * </div>
     */
    private void init(GraphObjectInfoPanel.SuggestionPanelCSS css){
        SimplePanel sp = new SimplePanel();
        sp.setStyleName(css.objectInfoPanel());
        this.mainPanel.setStyleName(css.objectInfoContent());
        sp.add(this.mainPanel);
        initWidget(sp);
    }

    public void add(IsWidget widget){
        this.mainPanel.add(widget);
    }
}

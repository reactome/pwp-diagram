package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphSubpathway extends GraphEvent {

    private EntityStatistics statistics;
    private Set<GraphEvent> containedEvents = new HashSet<>();

    public GraphSubpathway(SubpathwayNode subpathway) {
        super(subpathway);
    }

    public void addContainedEvent(GraphEvent event){
        if(event!=null) this.containedEvents.add(event);
    }

    @Override
    public List<DiagramObject> getDiagramObjects() {
        List<DiagramObject> rtn = new LinkedList<>();
        for (GraphObject event : containedEvents) {
            if (event instanceof GraphSubpathway) {
                rtn.addAll(((GraphSubpathway) event).getSuperDiagramObjects());
            } else {
                rtn.addAll(event.getDiagramObjects());
            }
        }
        return rtn;
    }

    public boolean isHit() {
        return statistics != null;
    }

    public void setIsHit(EntityStatistics statistics){
        this.statistics = statistics;
    }

    public void resetHit(){
        this.statistics = null;
    }

    private List<DiagramObject> getSuperDiagramObjects(){
        return super.getDiagramObjects();
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.pathway();
    }

    public Set<GraphEvent> getContainedEvents() {
        return containedEvents;
    }
}

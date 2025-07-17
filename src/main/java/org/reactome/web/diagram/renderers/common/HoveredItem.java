package org.reactome.web.diagram.renderers.common;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeAttachment;
import org.reactome.web.diagram.data.layout.SummaryItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Some items draw part of other items (e.g. nodes can also render some connectors that belong to the edges).
 * When those parts belonging to other objects are hovered, this will need to be notified by the object that
 * is drawing (and not the "owner" itself).
 *
 * We also have to take into account that there are inner elements in the drawn objects which we need to keep
 * track of. For example the "summary items" or "expression levels" in the nodes.
 *
 *  [X]-------------[A]-[A]----[X]       [A] -> These are node attachments
 *   |                          |        [X] -> These are summary items
 *   |       display name   [C] |        [C] -> Context menu trigger
 *   |                          |
 *  [X]------------------------[X]
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class HoveredItem {

    private Long diagramId;
    private DiagramObject diagramObject;
    private GraphObject graphObject;
    private NodeAttachment attachment;
    private SummaryItem summaryItem;
    private ContextMenuTrigger trigger;

    public HoveredItem(GraphObject graphObject) {
        this.graphObject = graphObject;
    }

    public HoveredItem(Long diagramId) {
        this.diagramId = diagramId;
    }

    public HoveredItem(Long diagramId, NodeAttachment attachment) {
        this.diagramId = diagramId;
        this.attachment = attachment;
    }

    public HoveredItem(Long diagramId, SummaryItem summaryItem) {
        this.diagramId = diagramId;
        this.summaryItem = summaryItem;
    }

    public HoveredItem(Long diagramId, ContextMenuTrigger trigger) {
        this.diagramId = diagramId;
        this.trigger = trigger;
    }

    public Long getDiagramId() {
        return diagramId;
    }

    public List<DiagramObject> getDiagramObjects(){
        if(graphObject!=null){
            return graphObject.getDiagramObjects();
        }
        if(diagramObject!=null){
            return diagramObject.getGraphObject().getDiagramObjects();
        }
        return new LinkedList<>();
    }

    public GraphObject getGraphObject() {
        return graphObject;
    }

    public DiagramObject getHoveredObject() {
        return diagramObject;
    }

    public NodeAttachment getAttachment() {
        return attachment;
    }

    public SummaryItem getSummaryItem() {
        return summaryItem;
    }

    public ContextMenuTrigger getContextMenuTrigger() {
        return trigger;
    }

    public void setDiagramObject(DiagramObject diagramObject) {
        this.diagramId = diagramObject.getId();
        this.diagramObject = diagramObject;
        this.graphObject = this.diagramObject.getGraphObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HoveredItem that = (HoveredItem) o;

        if (trigger != null ? !trigger.equals(that.trigger) : that.trigger != null) return false;
        if (graphObject != null ? !graphObject.equals(that.graphObject) : that.graphObject != null) return false;
        //noinspection SimplifiableIfStatement
        if (attachment != null ? !attachment.equals(that.attachment) : that.attachment != null) return false;
        return summaryItem == that.summaryItem;
    }

    @Override
    public int hashCode() {
        return graphObject != null ? graphObject.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "HoveredItem{" +
                "diagramId=" + diagramId +
                (attachment == null ? "" :  ", attachment=" + attachment.getReactomeId()) +
                (summaryItem == null ? "" : ", summaryItem=" + summaryItem.getType()) +
                '}';
    }
}

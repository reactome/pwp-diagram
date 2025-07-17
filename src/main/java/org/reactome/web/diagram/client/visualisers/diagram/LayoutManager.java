package org.reactome.web.diagram.client.visualisers.diagram;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.EntityDecoratorHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;

import java.util.*;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LayoutManager implements ContentLoadedHandler, ContentRequestedHandler {

    private EventBus eventBus;

    private Context context;
    private HoveredItem hovered = null;
    private GraphObject selected = null;
    private Set<DiagramObject> halo = new HashSet<>();
    private Set<DiagramObject> flagged = new HashSet<>();

    public LayoutManager(EventBus eventBus) {
        this.eventBus = eventBus;
        addHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void addHandlers() {
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    public Set<DiagramObject> getFlagged() {
        return flagged;
    }

    public Set<DiagramObject> getHalo() {
        return halo;
    }

    public HoveredItem getHovered() {
        return hovered;
    }

    /**
     * In every zoom step the way the elements are drawn (even if they are drawn or not) is defined by the
     * renderer assigned. The most accurate and reliable way of finding out the hovered object by the mouse
     * pointer is using the renderer isHovered method.
     */
    public Collection<HoveredItem> getHovered(Coordinate model) {
        List<HoveredItem> rtn = new LinkedList<>();
        if(context==null) return rtn;
        Collection<DiagramObject> target = this.context.getContent().getHoveredTarget(model, context.getDiagramStatus().getFactor());
        for (DiagramObject item : target) {
            Renderer renderer = RendererManager.get().getRenderer(item);
            if (renderer != null) {
                HoveredItem hovered = renderer.getHovered(item, model);
                if (hovered != null) {
                    rtn.add(hovered);
                }
            }
        }
        return rtn;
    }

    public DiagramObject getHoveredDiagramObject() {
        return hovered == null ? null : hovered.getDiagramObjects().get(0);
    }

    public GraphObject getSelected() {
        return selected;
    }

    public List<DiagramObject> getSelectedDiagramObjects() {
        return selected != null ? selected.getDiagramObjects() : new LinkedList<DiagramObject>();
    }

    public boolean isHighlighted(HoveredItem item) {
        return Objects.equals(hovered, item);
    }

    public boolean isSelected(GraphObject graphObject) {
        return Objects.equals(graphObject, this.selected);
    }

    public boolean isHoveredVisible() {
        return hovered != null && RendererManager.get().getRenderer(hovered.getHoveredObject()).isVisible(hovered.getHoveredObject());
    }

    public boolean resetHovered() {
        if (hovered != null) {
            hovered = null;
            return true;
        }
        return false;
    }

    public boolean resetFlagged() {
        if (this.flagged != null) {
            this.flagged = new HashSet<>();
            return true;
        }
        return false;
    }

    public boolean resetSelected() {
        if (this.selected != null) {
            halo = new HashSet<>();
            this.selected = null;
            return true;
        }
        return false;
    }

    public void setFlagged(Set<DiagramObject> flagged) {
        this.flagged = flagged;
    }

    public GraphObjectHoveredEvent setHovered(HoveredItem hovered) {
        DiagramObject item = null;
        if (hovered != null) {
            item = hovered.getHoveredObject();
            if (hovered.getAttachment() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getAttachment()), this);
            } else if (hovered.getSummaryItem() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getSummaryItem()), this);
            } else if (hovered.getContextMenuTrigger() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getContextMenuTrigger()), this);
            } else {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item), this);
            }
        }

        //Even though at the level of HoveredItem they are different, we only notify if the hovered diagram
        //object is actually different, so we do not take into account attachments or entities summary.
        DiagramObject prev = this.hovered != null ? this.hovered.getHoveredObject() : null;
        this.hovered = hovered;
        if (!Objects.equals(prev, item)) {
            GraphObject graphObject = item != null && item.getIsFadeOut() == null ? item.getGraphObject() : null;
            //we don't rely on the listener of the following event because finer grain of the hovering is lost
            return new GraphObjectHoveredEvent(graphObject, item);
        }
        return null;
    }

    public void setSelected(GraphObject graphObject) {
        if (graphObject == null) {
            resetSelected();
        } else {
            boolean fadeOut = !graphObject.getDiagramObjects().isEmpty();
            for (DiagramObject diagramObject : graphObject.getDiagramObjects()) {
                fadeOut &= diagramObject.getIsFadeOut() != null;
            }
            if (!fadeOut) {
                this.selected = graphObject;
                this.halo = graphObject.getRelatedDiagramObjects();
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (GraphPhysicalEntity parent : pe.getParentLocations()) {
                        this.halo.addAll(parent.getDiagramObjects());   //halo its parents but not the reactions where they participate
                    }
                }
            }
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            context = event.getContext();
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
    }

}

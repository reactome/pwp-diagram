package org.reactome.web.diagram.tooltips;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphSimpleEntity;
import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.Objects;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class TooltipContainer extends AbsolutePanel implements ContentRequestedHandler, ContentLoadedHandler,
        GraphObjectHoveredHandler, EntityDecoratorHoveredHandler, InteractorHoveredHandler, InteractorDraggedHandler,
        DiagramZoomHandler, DiagramPanningHandler {

    private static final int DELAY = 500;
    private static final double ZOOM_THRESHOLD = 1.2;

    private EventBus eventBus;
    private Context context;
    private Object hovered;

    private Timer hoveredTimer;
    private Timer infoTimer; //For messages that don't have to last long on the viewport (like the trigger hovering)

    private int width;
    private int height;

    private NumberFormat numberFormat;

    public TooltipContainer(EventBus eventBus, int width, int height) {
        this.eventBus = eventBus;
        this.hoveredTimer = new Timer() {
            @Override
            public void run() {
                showTooltipExecute();
            }
        };
        setWidth(width);
        setHeight(height);
        numberFormat = NumberFormat.getFormat("0.000");
        initHandlers();
    }

    private void initHandlers() {
        eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        eventBus.addHandler(EntityDecoratorHoveredEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(DiagramPanningEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        eventBus.addHandler(InteractorHoveredEvent.TYPE, this);
        eventBus.addHandler(InteractorDraggedEvent.TYPE, this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void onEntityDecoratorHovered(final EntityDecoratorHoveredEvent event) {
        if (event.getAttachment() != null) {
            setHovered(event.getAttachment());
        } else if (event.getSummaryItem() != null) {
            setHovered(event.getSummaryItem());
        } else if (event.getTrigger() != null) {
            setHovered(event.getTrigger());
        } else {
            setHovered(event.getDiagramObject());
        }
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        boolean isDecorator = (hovered instanceof NodeAttachment || hovered instanceof SummaryItem);
        if(!isDecorator || event.getHoveredObject()==null) {
            setHovered(event.getHoveredObject());
        }
    }

    @Override
    public void onInteractorHovered(InteractorHoveredEvent event) {
        DiagramInteractor interactor = event.getInteractor();
        setHovered(interactor);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            this.context = event.getContext();
        }
    }

    @Override
    public void onDiagramPanningEvent(DiagramPanningEvent event) {
        Tooltip tooltip = Tooltip.getTooltip();
        if (tooltip.isVisible()) {
            tooltip.hide();
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.context = null;
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        showTooltip();
    }

    @Override
    public void onInteractorDragged(InteractorDraggedEvent event) {
        setHovered(null); //This forces the tooltip timer to restart
        setHovered(event.getInteractorEntity()); //Only if the dragging stops for a while the tooltip will be shown
    }

    public void setWidth(int width) {
        setWidth(width + "px");
        this.width = width;
    }

    public void setHeight(int height) {
        setHeight(height + "px");
        this.height = height;
    }

    private void setHovered(Object hovered){
        if(Objects.equals(this.hovered, hovered)) return;
        if (this.hoveredTimer.isRunning()) {
            this.hoveredTimer.cancel();
        }
        this.hovered = hovered;
        if (this.hovered == null) {
            showTooltipExecute(); //this will quickly hide the tooltip ;)
        } else {
            if(this.hovered instanceof ContextMenuTrigger){
                Tooltip.getTooltip().hide(); //Corrects the behaviour for other possible shown tooltips
                this.hoveredTimer.schedule(DELAY * 4);
            }else {
                this.hoveredTimer.schedule(DELAY);
            }
        }
    }

    private void showTooltip() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                showTooltipExecute();
            }
        });
    }

    private void showTooltipExecute() {
        final Tooltip tooltip = Tooltip.getTooltip();

        if(infoTimer !=null) infoTimer.cancel();

        if (hovered == null || context == null) {
            tooltip.hide();
        } else {
            Coordinate offset = context.getDiagramStatus().getOffset();
            double factor = context.getDiagramStatus().getFactor();
            if (hovered instanceof Node) {
                Node node = (Node) hovered;
                if (factor > ZOOM_THRESHOLD || (node.getTrivial()!=null && node.getTrivial() && factor < 0.50)) {
                    tooltip.hide();
                    return;
                }
                NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
                GraphObject obj = node.getGraphObject();
                if (obj instanceof GraphEntityWithAccessionedSequence) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                    tooltip.setText(node.getDisplayName() + (pe.getIdentifier() != null ? " (" + pe.getIdentifier() + ")" : ""));
                }else if (obj instanceof GraphSimpleEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                    tooltip.setText(node.getDisplayName() + (pe.getIdentifier() != null ? " (CHEBI:" + pe.getIdentifier() + ")" : ""));
                } else {
                    tooltip.setText(node.getDisplayName());
                }
                tooltip.setPositionAndShow(this, prop.getX(), prop.getY(), prop.getHeight() + 8.0 * factor);
            } else if (hovered instanceof Edge) {
                Edge edge = (Edge) hovered;
                tooltip.setText(edge.getDisplayName());
                Shape shape = ShapeFactory.transform(edge.getReactionShape(), factor, offset);
                String type = shape.getType();
                if (type.equals("BOX")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getA().getX(),
                            shape.getA().getY(),
                            (shape.getB().getY() - shape.getA().getY()) + 8.0 * factor
                    );
                } else if (type.equals("CIRCLE") || type.equals("DOUBLE_CIRCLE")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getC().getX() - shape.getR(),
                            shape.getC().getY(),
                            shape.getR() + 8.0 * factor
                    );
                }
            } else if (hovered instanceof NodeAttachment){
                NodeAttachment attachment = (NodeAttachment) hovered;
                tooltip.setText(attachment.getDescription());
                Shape shape = ShapeFactory.transform(attachment.getShape(), factor, offset);
                String type = shape.getType();
                if (type.equals("BOX")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getA().getX(),
                            shape.getA().getY(),
                            (shape.getB().getY() - shape.getA().getY()) + 4.0 * factor
                    );
                }
            } else if (hovered instanceof SummaryItem) {
                SummaryItem summaryItem = (SummaryItem) hovered;
                Shape shape = ShapeFactory.transform(summaryItem.getShape(), factor, offset);
                tooltip.setText(summaryItem.getLabel() != null ? summaryItem.getLabel() : summaryItem.getNumber() + " interactions");
                String type = shape.getType();
                if (type.equals("CIRCLE")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getC().getX() - shape.getR(),
                            shape.getC().getY(),
                            shape.getR() + 8.0 * factor
                    );
                }
            } else if (hovered instanceof ContextMenuTrigger){
                ContextMenuTrigger trigger = ((ContextMenuTrigger) hovered).transform(factor, offset);
                tooltip.setText("Click to open the context menu");
                tooltip.setPositionAndShow(
                        TooltipContainer.this,
                        trigger.getB().getX() - 200,
                        trigger.getC().getY(),
                        factor
                );
                infoTimer = new Timer() {
                    @Override
                    public void run() {
                        tooltip.hide();
                    }
                };
                infoTimer.schedule(1500);
            } else if (hovered instanceof InteractorLink) {
                if (factor > ZOOM_THRESHOLD || factor < 0.50) {
                    tooltip.hide();
                    return;
                }
                InteractorLink interactorLink = (InteractorLink) hovered;
                int e = interactorLink.getEvidences() != null ? interactorLink.getEvidences() : 0;
                String evidences = e == 0 ? "" : (e == 1 ? e + " evidence - " : e + " pieces of evidence - ");
                Coordinate centre = interactorLink.transform(factor, offset).getCentre();
                tooltip.setText(evidences + "Score: " + numberFormat.format(interactorLink.getScore()));
                tooltip.setPositionAndShow(
                        TooltipContainer.this,
                        centre.getX(),
                        centre.getY(),
                        0
                );

            } else if (hovered instanceof InteractorEntity) {
                if (factor > ZOOM_THRESHOLD || factor < 0.50) {
                    tooltip.hide();
                    return;
                }
                InteractorEntity interactorEntity = (InteractorEntity) hovered;
                DiagramBox box = new DiagramBox(interactorEntity.transform(factor, offset));
                //Show the alias (if it exists) and the accession in brackets
                String text = interactorEntity.getDisplayName() + (interactorEntity.getAlias()!=null ? " (" + interactorEntity.getAccession() + ")" : "");
                tooltip.setText(text);
                tooltip.setPositionAndShow(
                        TooltipContainer.this,
                        box.getMinX(),
                        box.getMinY(),
                        box.getHeight() + 8.0 * factor
                );
            } else {
                tooltip.hide(); //just in case :)
            }
        }
    }
}

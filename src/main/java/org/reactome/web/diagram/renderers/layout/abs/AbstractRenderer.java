package org.reactome.web.diagram.renderers.layout.abs;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.Console;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public abstract class AbstractRenderer implements Renderer {

    @Override
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        return null;
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        return null;
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset){
        draw(ctx, item, factor, offset); //By default the normal draw method is called
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset){
        GraphObject graphObject = item.getGraphObject();
        setExpressionColour(ctx, graphObject.getExpression(), min, max, t);
        draw(ctx, item, factor, offset); //By default the normal draw method is called
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset){
        GraphObject graphObject = item.getGraphObject();
        setRegulationColour(ctx, graphObject.getExpression(), min, max, t);
        draw(ctx, item, factor, offset); //By default the normal draw method is called
    }

    @Override
    public void drawHitInteractors(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void focus(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){}

    protected void setExpressionColour(AdvancedContext2d ctx, List<Double> expression, Double min, Double max, int t){
        try {
            double value = min;
            if(expression!=null) {
                value = expression.get(t);
            }
            ctx.setFillStyle(AnalysisColours.get().expressionGradient.getColor(value, min, max));
        }catch (Exception e){
            Console.error(e.getMessage(), this);
        }
    }


    protected void setRegulationColour(AdvancedContext2d ctx, List<Double> expression, Double min, Double max, int t){
        try {
            double value = min;
            if(expression!=null) {
                value = expression.get(t);
            }
            ctx.setFillStyle(AnalysisColours.get().regulationColorMap.getColor((int) value));
        }catch (Exception e){
            Console.error(e.getMessage(), this);
        }
    }

    public void drawSegments(AdvancedContext2d ctx, List<Segment> segments, Double factor, Coordinate offset) {
        for(Segment segment : segments) {
            segment = SegmentFactory.transform(segment, factor, offset);
            ctx.beginPath();
            ctx.moveTo(segment.getFrom().getX(), segment.getFrom().getY());
            ctx.lineTo(segment.getTo().getX(), segment.getTo().getY());
            ctx.stroke();
        }
    }

    public void drawConnector(AdvancedContext2d ctx, Connector connector, boolean stoichiometryVisible, Double factor, Coordinate offset) {
        drawSegments(ctx, connector.getSegments(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, connector.getEndShape(), factor, offset);
        if(stoichiometryVisible) {
            Stoichiometry stoichiometry = connector.getStoichiometry();
            if(stoichiometry != null && stoichiometry.getValue() > 1) {
                ShapeAbstractRenderer.draw(ctx, stoichiometry.getShape(), factor, offset);
            }
        }
    }

    public void drawConnectors(AdvancedContext2d ctx,
                               DiagramObject item,
                               List<GraphPhysicalEntity> physicalEntities,
                               Double factor,
                               Coordinate offset) {
        boolean stoichiometryVisible = RendererManager.get().getConnectorRenderer().stoichiometryVisible();
        for(GraphPhysicalEntity pe : physicalEntities) {
            if(!isVisible(pe)) continue;
            for(DiagramObject obj : pe.getDiagramObjects()) {
                Node node = (Node) obj;
                for(Connector connector : node.getConnectors()) {
                    if(connector.getEdgeId().equals(item.getId())) {
                        drawConnector(ctx, connector, stoichiometryVisible, factor, offset);
                    }
                }
            }
        }
    }

    public void drawConnectorsWithAlpha(AdvancedContext2d ctx,
                                        DiagramObject item,
                                        List<GraphPhysicalEntity> physicalEntities,
                                        Double factor,
                                        Coordinate offset) {
        boolean stoichiometryVisible = RendererManager.get().getConnectorRenderer().stoichiometryVisible();
        for(GraphPhysicalEntity pe : physicalEntities) {
            if(!isVisible(pe)) continue;
            for(DiagramObject obj : pe.getDiagramObjects()) {
                Node node = (Node) obj;
                for(Connector connector : node.getConnectors()) {
                    if(connector.getEdgeId().equals(item.getId())) {
                        if (node.getTrivial() == null || !node.getTrivial()) {
                            drawConnector(ctx, connector, stoichiometryVisible, factor, offset);
                        } else {
                            ctx.save();
                            ctx.setGlobalAlpha((factor - 0.5) * 2);
                            drawConnector(ctx, connector, stoichiometryVisible, factor, offset);
                            ctx.restore();
                        }
                    }
                }
            }
        }
    }

    public boolean isVisible(GraphPhysicalEntity pe) {
        for (DiagramObject diagramObject : pe.getDiagramObjects()) {
            Renderer renderer = RendererManager.get().getRenderer(diagramObject);
            if (renderer == null || !renderer.isVisible(diagramObject)) {
                return false;
            }
        }
        return true;
    }
}

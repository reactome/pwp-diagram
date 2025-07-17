package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.graph.model.GraphReactionLikeEvent;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.SegmentCategory;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ReactionAbstractRenderer extends EdgeAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        strokeShape(ctx, (Edge) item, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //TODO
    }

    public void strokeShape(AdvancedContext2d ctx, Edge edge, Double factor, Coordinate offset){
        drawSegments(ctx, edge.getSegments(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, edge.getEndShape(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, edge.getReactionShape(), factor, offset);
        drawSymbol(ctx, edge, factor, offset);
    }

    protected void drawSymbol(AdvancedContext2d ctx, Edge edge, Double factor, Coordinate offset){
        if(edge.getReactionShape().getS()!=null){
            Shape shape = ShapeFactory.transform(edge.getReactionShape(), factor, offset);
            double x = shape.getA().getX() + (shape.getB().getX() - shape.getA().getX())/ 2.0;
            double y = shape.getA().getY() + (shape.getB().getY() - shape.getA().getY())/ 2.0;
            ctx.save();
            ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
            ctx.setTextAlign(Context2d.TextAlign.CENTER);
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
            ctx.setFillStyle(ctx.getStrokeStyle());
            ctx.fillText(shape.getS(),x, y);
            ctx.restore();
        }
    }

    @Override
    public final HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        if(isVisible(item)) {
            try {
                Edge edge = (Edge) item;
                Shape shape = edge.getReactionShape();
                if(shape!=null){
                    if(ShapeCategory.isHovered(shape, pos)){
                        return new HoveredItem(edge.getId());
                    }
                }

                shape = edge.getEndShape();
                if(shape!=null){
                    if(ShapeCategory.isHovered(shape, pos)){
                        return new HoveredItem(edge.getId());
                    }
                }

                for (Segment segment : edge.getSegments()) {
                    if (SegmentCategory.isInSegment(segment, pos)) {
                        return new HoveredItem(edge.getId());
                    }
                }
            }catch (ClassCastException e){ /* Nothing here */ }
        }
        return null;
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        Edge edge = (Edge) item;
        try {
            GraphReactionLikeEvent rle = edge.getGraphObject();
            //It can only happen if the graph hasn't been loaded yet, so no hovering until then :(
            if (rle == null) return;
            drawConnectors(ctx, item, rle.getInputs(), factor, offset);
            drawConnectors(ctx, item, rle.getOutputs(), factor, offset);
            drawConnectors(ctx, item, rle.getCatalysts(), factor, offset);
            drawConnectors(ctx, item, rle.getActivators(), factor, offset);
            drawConnectors(ctx, item, rle.getInhibitors(), factor, offset);
            drawConnectors(ctx, item, rle.getRequirements(), factor, offset);
        } catch (ClassCastException ex) {
            //Nothing here
        }
        strokeShape(ctx, edge, factor, offset);
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getReaction());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getReaction());
    }
}

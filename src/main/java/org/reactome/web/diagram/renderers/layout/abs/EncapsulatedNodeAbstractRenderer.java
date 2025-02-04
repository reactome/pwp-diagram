package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class EncapsulatedNodeAbstractRenderer extends NodeAbstractRenderer {

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset) {
        drawAnalysisResult(ctx, overlay, item, factor, offset);
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset){
        drawAnalysisResult(ctx, overlay, item, factor, offset);
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset){
        drawAnalysisResult(ctx, overlay, item, factor, offset);
    }

    public abstract void drawAnalysisResult(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset);

    @Override
    @SuppressWarnings("Duplicates")
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        ctx.save();
        ctx.setStrokeStyle(ctx.getFillStyle());
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH);
        ctx.setFillStyle("#FEFDFF");
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        if(item.getDisplayName() == null || item.getDisplayName().isEmpty()) { return; }
        TextMetrics metrics = ctx.measureText(item.getDisplayName());

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double padding = RendererProperties.NODE_TEXT_PADDING *  4.5;
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, padding);
        double x = prop.getX() + prop.getWidth() / 2d;
        double y = prop.getY() + prop.getHeight() / 2d;
        if(metrics.getWidth()<=prop.getWidth() - 2 * padding) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), CoordinateFactory.get(x, y));
        }else{
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), prop);
        }
    }

    @Override
    public void focus(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getEncapsulatednode());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getEncapsulatednode());
    }
}

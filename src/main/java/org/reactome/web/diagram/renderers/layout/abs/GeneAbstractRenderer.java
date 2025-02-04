package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.graph.model.GraphObject;
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
public abstract class GeneAbstractRenderer extends NodeAbstractRenderer{
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        fillTextHolder(ctx, prop);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getGene().getStroke());
        fillTextHolder(ctx, prop);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        GraphObject graphObject = item.getGraphObject();
        setExpressionColour(ctx, graphObject.getExpression(), min, max, t);

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getGene().getStroke());
        fillTextHolder(ctx, prop);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        GraphObject graphObject = item.getGraphObject();
        setRegulationColour(ctx, graphObject.getExpression(), min, max, t);

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getGene().getStroke());
        fillTextHolder(ctx, prop);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(isVisible(item) && item.getDisplayName() != null && !item.getDisplayName().isEmpty()) {
            TextMetrics metrics = ctx.measureText(item.getDisplayName());
            Node node = (Node) item;
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            Coordinate centre = CoordinateFactory.get(prop.getX() + prop.getWidth() / 2, prop.getY() + prop.getHeight() * 0.75);
            TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
            if(metrics.getWidth()<=prop.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
                textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), centre);
            }else{
                // Create a smaller placeholder as text is only displayed at the bottom end of the gene node
                NodeProperties nodeProp = NodePropertiesFactory.get(prop.getX(), prop.getY() + prop.getHeight()/2 , prop.getWidth(), prop.getHeight()/2);
                textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), nodeProp);
            }
        }
    }

        @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    public void fillTextHolder(AdvancedContext2d ctx, NodeProperties prop){
        ctx.geneTextHolder(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                prop.getHeight(),
                RendererProperties.GENE_SYMBOL_WIDTH,
                RendererProperties.ROUND_RECT_ARC_WIDTH
        );
        ctx.fill();
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.geneShape(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                RendererProperties.GENE_SYMBOL_PAD,
                RendererProperties.GENE_SYMBOL_WIDTH,
                RendererProperties.ARROW_LENGTH,
                RendererProperties.ARROW_ANGLE
        );
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getGene());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getGene());
    }
}

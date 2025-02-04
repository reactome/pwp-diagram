package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ComplexDrugAbstractRenderer extends NodeAbstractRenderer {
    protected static double COMPLEX_DRUG_RX_FONT = 5;
    protected static double COMPLEX_DRUG_RX_MAX_FONT = 20;

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        fillShape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        drawCross(ctx, node, prop);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.drawText(ctx, item, factor, offset);
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        //Render the Rx inside the bottom right box
        rxText(ctx, prop, factor);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.beginPath();
        if(needsDashed!=null){
            ctx.dashedOctagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        }else {
            ctx.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
        }
    }

    public void rxText(AdvancedContext2d ctx, NodeProperties prop, double factor) {
        double rxX = prop.getX() + prop.getWidth() - 3.5 * RendererProperties.NODE_LINE_WIDTH;
        double rxY = prop.getY() + prop.getHeight() - 3.5 * RendererProperties.NODE_LINE_WIDTH;

        TextRenderer textRenderer = new TextRenderer(COMPLEX_DRUG_RX_FONT * factor, 0);
        Coordinate c = CoordinateFactory.get(rxX , rxY);

        ctx.save();
        ctx.setTextAlign(Context2d.TextAlign.RIGHT);
        double font = COMPLEX_DRUG_RX_FONT * factor;
        ctx.setFont(RendererProperties.getFont(font < COMPLEX_DRUG_RX_MAX_FONT ? font : COMPLEX_DRUG_RX_MAX_FONT));
        textRenderer.drawTextSingleLine(ctx, "Rx", c);
        ctx.restore();
    }

    protected void fillShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed){
        ctx.beginPath();
        if(needsDashed!=null){
            //This is needed since the dashed rounded rectangle will always be filled
            ctx.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
            ctx.fill();
            ctx.dashedOctagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        }else {
            ctx.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
        }
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getComplexdrug());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getComplexdrug());
    }
}
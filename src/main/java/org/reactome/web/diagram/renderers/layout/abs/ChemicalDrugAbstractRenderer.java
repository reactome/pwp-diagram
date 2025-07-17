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
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ChemicalDrugAbstractRenderer extends NodeAbstractRenderer {

    protected static double CHEMICAL_DRUG_RX_FONT = 5;
    public static double CHEMICAL_DRUG_RX_BOX = 7;


    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        ctx.fill();
        ctx.beginPath();
        rx(ctx, prop);
        ctx.stroke();
        ctx.fill();
        drawCross(ctx, node, prop);
//        rxText(ctx, prop, factor);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        if(item.getDisplayName() == null || item.getDisplayName().isEmpty())  return;
        TextMetrics metrics = ctx.measureText(item.getDisplayName());

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double padding = RendererProperties.NODE_TEXT_PADDING * 2;
        padding = (prop.getWidth() - padding * 2 < 0) ? 0 : padding;
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, padding);
        double x = prop.getX() + prop.getWidth() / 2d;
        double y = prop.getY() + prop.getHeight() / 2d;
        if(metrics.getWidth()<=prop.getWidth() - 0.5 * padding) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), CoordinateFactory.get(x, y));
        }else{
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), prop);
        }
        //Render the Rx inside the bottom right box
        rxText(ctx, prop, factor);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        ctx.save();
        ctx.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_OUT);
        ctx.fill();
        ctx.restore();
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        if (!isVisible(item)) return null;

        //TODO: Do not call super but check whether the mouse in on the arrow or the grayish box
        return super.getHovered(item, pos);
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.beginPath();
        ctx.ellipse(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                prop.getHeight()
        );
        rx(ctx, prop);
    }

    public void rx(AdvancedContext2d ctx, NodeProperties prop) {
        double rxX = prop.getX() + prop.getWidth() - RendererProperties.DRUG_RX_BOX * 2;
        double rxY = prop.getY() + prop.getHeight() - RendererProperties.DRUG_RX_BOX;
        ctx.rect(rxX, rxY, RendererProperties.DRUG_RX_BOX * 2, RendererProperties.DRUG_RX_BOX);
    }

    public void rxText(AdvancedContext2d ctx, NodeProperties prop, double factor) {
        double cdrb = CHEMICAL_DRUG_RX_BOX * factor;
        double rxX = prop.getX() + prop.getWidth() - cdrb * 2;
        double rxY = prop.getY() + prop.getHeight() - cdrb;

        TextRenderer textRenderer = new TextRenderer(CHEMICAL_DRUG_RX_FONT * factor, 0);
        Coordinate c = CoordinateFactory.get(rxX + cdrb, rxY + cdrb / 2.0);
        ctx.save();
        ctx.setFont(RendererProperties.getFont(CHEMICAL_DRUG_RX_FONT * factor));
        textRenderer.drawTextSingleLine(ctx, "Rx", c);
        ctx.restore();
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getChemicaldrug());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getChemicaldrug());
    }
}

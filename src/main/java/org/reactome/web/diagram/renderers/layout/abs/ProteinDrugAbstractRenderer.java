package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ProteinDrugAbstractRenderer extends NodeAbstractRenderer {

    protected static double PROTEIN_DRUG_RX_FONT = 5;
    protected static double PROTEIN_DRUG_RX_MAX_FONT = 20;

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

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

    public boolean nodeAttachmentsVisible() {
        return false;
    }

    private void fillShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null && needsDashed) {
            //This is needed since the dashed rounded rectangle will always be filled
            ctx.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
            ctx.fill();
            ctx.dashedRoundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    RendererProperties.ROUND_RECT_ARC_WIDTH,
                    RendererProperties.DASHED_LINE_PATTERN
            );
        } else {
            ctx.roundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    RendererProperties.ROUND_RECT_ARC_WIDTH
            );
        }
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null && needsDashed) {
            ctx.dashedRoundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    RendererProperties.ROUND_RECT_ARC_WIDTH,
                    RendererProperties.DASHED_LINE_PATTERN
            );
        } else {
            ctx.roundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    RendererProperties.ROUND_RECT_ARC_WIDTH
            );
        }
    }

    public void rxText(AdvancedContext2d ctx, NodeProperties prop, double factor) {
        double rxX = prop.getX() + prop.getWidth() - 1.75 * RendererProperties.NODE_LINE_WIDTH;
        double rxY = prop.getY() + prop.getHeight() - 1.75 * RendererProperties.NODE_LINE_WIDTH;

        TextRenderer textRenderer = new TextRenderer(PROTEIN_DRUG_RX_FONT * factor, 0);
        Coordinate c = CoordinateFactory.get(rxX , rxY);

        ctx.save();
        ctx.setTextAlign(Context2d.TextAlign.RIGHT);
        double font = PROTEIN_DRUG_RX_FONT * factor;
        ctx.setFont(RendererProperties.getFont(font < PROTEIN_DRUG_RX_MAX_FONT ? font : PROTEIN_DRUG_RX_MAX_FONT));
        textRenderer.drawTextSingleLine(ctx, "Rx", c);
        ctx.restore();
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getProteindrug());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getProteindrug());
    }

    protected void drawAttachments(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset, boolean fill) {
        if(!nodeAttachmentsVisible()) return;
        List<NodeAttachment> atList = node.getNodeAttachments();
        if (atList != null) {
            for (NodeAttachment nodeAttachment : atList) {
                AttachmentAbstractRenderer.draw(ctx, nodeAttachment, factor, offset, fill);
            }
        }
    }
}

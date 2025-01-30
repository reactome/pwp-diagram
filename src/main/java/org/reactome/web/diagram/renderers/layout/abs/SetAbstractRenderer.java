package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.helper.RoundedRectangleHelper;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class SetAbstractRenderer extends NodeAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = new NodeProperties.Builder()
                .copy(((Node) item).getProp())
                .transform(factor, offset)
                .build();

        RoundedRectangleHelper helper = new RoundedRectangleHelper(prop);

        helper.trace(ctx);
        ctx.fill();
        helper.trace(ctx, node.getNeedDashedBorder());
        ctx.stroke();

        helper = helper.setPadding(RendererProperties.SEPARATION);
        helper.trace(ctx, node.getNeedDashedBorder());
        ctx.stroke();

        drawCross(ctx, node, prop);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        innerShape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        drawSummaryItems(ctx, (Node) item, factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        new RoundedRectangleHelper(prop).trace(ctx, needsDashed);
    }

    protected void fillShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        RoundedRectangleHelper helper = new RoundedRectangleHelper(prop);
        helper.trace(ctx);
        ctx.fill();
        helper.trace(ctx, needsDashed);
    }

    protected void innerShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        new RoundedRectangleHelper(prop, RendererProperties.SEPARATION).trace(ctx, needsDashed);
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getEntityset());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getEntityset());
    }
}

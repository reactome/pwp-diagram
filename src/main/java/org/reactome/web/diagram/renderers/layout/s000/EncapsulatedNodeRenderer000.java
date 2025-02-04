package org.reactome.web.diagram.renderers.layout.s000;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.EncapsulatedNodeAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EncapsulatedNodeRenderer000 extends EncapsulatedNodeAbstractRenderer {

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //No text at this level
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH + 4);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.beginPath();
        ctx.hexagonNode(
                prop.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getWidth() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2,
                prop.getHeight() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2,
                RendererProperties.PROCESS_NODE_INSET_WIDTH
        );
    }

    @Override
    public void drawAnalysisResult(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset){
        if(!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        setColourProperties(ctx, ColourProfileType.NORMAL);
        ctx.setStrokeStyle(ctx.getFillStyle());
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH);
        ctx.setFillStyle("#FEFDFF");
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();


        GraphPathway graphPathway = node.getGraphObject();
        double p = graphPathway.getPercentage() < 0.1 ? 0.1 : graphPathway.getPercentage();
        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        setColourProperties(buffer, ColourProfileType.NORMAL);
        buffer.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH);
        buffer.setFillStyle(ctx.getFillStyle());
        buffer.setStrokeStyle(ctx.getFillStyle());
        buffer.fillRect(prop.getX(), prop.getY(), prop.getWidth() * p, prop.getHeight());

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        shape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        ctx.drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }
}

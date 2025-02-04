package org.reactome.web.diagram.thumbnail.diagram.render;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ProcessNodeThumbnailRenderer extends AbstractThumbnailRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties prop = node.getProp();

        Coordinate corner = CoordinateFactory.get(prop.getX(), prop.getY()).add(offset).multiply(factor);
        double w = prop.getWidth() * factor;
        double h = prop.getHeight() * factor;

        ctx.save();
        DiagramProfile profile = DiagramColours.get().PROFILE;
        ctx.setStrokeStyle(profile.getProcessnode().getStroke());
        ctx.setFillStyle(profile.getProcessnode().getFill());
        ctx.beginPath();
        ctx.rect(
                corner.getX(),
                corner.getY(),
                w,
                h
        );
        ctx.stroke();
        ctx.fill();

        ctx.setFillStyle("#FFFFFF");
        ctx.beginPath();
        ctx.rect(
                corner.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH * factor,
                corner.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH * factor,
                w - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2 * factor,
                h - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2 * factor
        );
        ctx.stroke();
        ctx.fill();
        ctx.restore();
    }
}

package org.reactome.web.diagram.thumbnail.diagram.render;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Link;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class FlowlineThumbnailRenderer extends AbstractThumbnailRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Link link = (Link) item;
        drawSegments(ctx, link.getSegments(), factor, offset);
        drawShape(ctx, link.getEndShape(), factor, offset);
    }
}

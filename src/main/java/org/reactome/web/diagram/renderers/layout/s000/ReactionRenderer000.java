package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Edge;
import org.reactome.web.diagram.renderers.layout.abs.ReactionAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.ShapeAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ReactionRenderer000 extends ReactionAbstractRenderer {
    @Override
    public void strokeShape(AdvancedContext2d ctx, Edge edge, Double factor, Coordinate offset){
        drawSegments(ctx, edge.getSegments(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, edge.getEndShape(), factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}

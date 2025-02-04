package org.reactome.web.diagram.renderers.layout.s050;

import org.reactome.web.diagram.data.graph.model.GraphReactionLikeEvent;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Edge;
import org.reactome.web.diagram.renderers.layout.abs.ReactionAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ReactionRenderer050 extends ReactionAbstractRenderer {

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        Edge edge = (Edge) item;
        try {
            GraphReactionLikeEvent rle = edge.getGraphObject();
            //It can only happen if the graph hasn't been loaded yet, so no hovering until then :(
            if (rle == null) return;
            drawConnectorsWithAlpha(ctx, item, rle.getInputs(), factor, offset);
            drawConnectorsWithAlpha(ctx, item, rle.getOutputs(), factor, offset);
            drawConnectorsWithAlpha(ctx, item, rle.getCatalysts(), factor, offset);
            drawConnectorsWithAlpha(ctx, item, rle.getActivators(), factor, offset);
            drawConnectorsWithAlpha(ctx, item, rle.getInhibitors(), factor, offset);
            drawConnectorsWithAlpha(ctx, item, rle.getRequirements(), factor, offset);
        } catch (ClassCastException ex) {
            //Nothing here
        }
        strokeShape(ctx, edge, factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}

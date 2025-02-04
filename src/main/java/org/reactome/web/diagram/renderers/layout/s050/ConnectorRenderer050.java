package org.reactome.web.diagram.renderers.layout.s050;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.renderers.layout.abs.ConnectorAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ConnectorRenderer050 extends ConnectorAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, AdvancedContext2d fadeout, AdvancedContext2d decorator, Node node, Double factor, Coordinate offset) {
        if(!RendererManager.get().getRenderer(node).isVisible(node)) return;

        if(node.getTrivial()!=null && node.getTrivial()){
            double alpha = ctx.getGlobalAlpha();
            ctx.save(); decorator.save();
            ctx.setGlobalAlpha((factor-0.5) * alpha * 2 );
            decorator.setGlobalAlpha((factor-0.5) * alpha * 2 );
            super.draw(ctx, fadeout, decorator, node, factor, offset);
            ctx.restore(); decorator.restore();
        }else{
            super.draw(ctx, fadeout, decorator, node, factor, offset);
        }
    }

    @Override
    public boolean stoichiometryVisible() {
        return false;
    }
}

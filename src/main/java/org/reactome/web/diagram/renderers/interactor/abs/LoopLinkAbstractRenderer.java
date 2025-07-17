package org.reactome.web.diagram.renderers.interactor.abs;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.LoopLink;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class LoopLinkAbstractRenderer extends InteractorAbstractRenderer {

    public void shape(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        LoopLink link = (LoopLink) item;
        Coordinate centre = link.getCentre().transform(factor, offset);
        ctx.beginPath();
        //For next line, take into account that canvas has the coordinates origin in the top-left corner
        ctx.arc(centre.getX(), centre.getY(), LoopLink.RADIUS * factor, -LoopLink.START_ANGLE, -LoopLink.END_ANGLE, true);
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!item.isVisible()) return;
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        draw(ctx, item, factor, offset);
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset) {
        draw(ctx, item, factor, offset);
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset) {
        draw(ctx, item, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }
}

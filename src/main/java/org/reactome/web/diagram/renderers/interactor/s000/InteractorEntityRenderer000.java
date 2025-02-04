package org.reactome.web.diagram.renderers.interactor.s000;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.renderers.interactor.abs.InteractorEntityAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorEntityRenderer000 extends InteractorEntityAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        //Nothing here
    }

    @Override
    public boolean isVisible(DiagramInteractor item) {
        return false;
    }
}

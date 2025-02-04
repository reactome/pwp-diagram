package org.reactome.web.diagram.renderers.interactor.s000;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.renderers.interactor.abs.StaticLinkAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class StaticLinkRenderer000 extends StaticLinkAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
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

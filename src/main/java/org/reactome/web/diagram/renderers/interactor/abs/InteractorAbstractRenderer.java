package org.reactome.web.diagram.renderers.interactor.abs;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.renderers.interactor.InteractorRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class InteractorAbstractRenderer implements InteractorRenderer {

    public abstract void shape(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset);

    @Override
    public boolean isVisible(DiagramInteractor item) {
        return item.isVisible();
    }
}

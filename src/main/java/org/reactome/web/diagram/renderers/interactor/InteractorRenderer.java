package org.reactome.web.diagram.renderers.interactor;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorRenderer {

    void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset);

    void drawEnrichment(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset);

    void drawExpression(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset);

    void drawRegulation(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset);

    void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset);

    void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset);

    boolean isVisible(DiagramInteractor item);
}

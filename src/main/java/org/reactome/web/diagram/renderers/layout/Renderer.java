package org.reactome.web.diagram.renderers.layout;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Renderer {

    void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset);

    void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset);

    void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset);

    void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    void drawHitInteractors(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    void focus(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    /**
     * There are items that draw part of other items (Nodes render also the connectors that
     * belong to the Edges).
     * @return the identifier of the diagram object hovered. Null if the item or delegates are not hovered
     */
    HoveredItem getHovered(DiagramObject item, Coordinate pos);

    Double getExpressionHovered(DiagramObject item, Coordinate pos, int t);

    boolean isVisible(DiagramObject item);

    void setColourProperties(AdvancedContext2d ctx, ColourProfileType type);

    void setTextProperties(AdvancedContext2d ctx, ColourProfileType type);
}

package org.reactome.web.diagram.thumbnail.diagram.render;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ThumbnailRenderer {

    void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);

    void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset);
}

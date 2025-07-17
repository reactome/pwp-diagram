package org.reactome.web.diagram.renderers.layout.s800;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.renderers.layout.s300.ProteinRenderer300;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ProteinRenderer800 extends ProteinRenderer300 {
    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        double w = node.getProp().getWidth();
        double h = node.getProp().getHeight();
        if (h >= 35 && w >= h * 1.5 && node.getIsFadeOut() == null) {
            drawProteinDetails(ctx, node, factor, offset, 6.33 * factor);
        } else if (h >= 15 && w >= h * 1.2 && node.getIsFadeOut() == null) {
            drawProteinDetails(ctx, node, factor, offset, 2.75 * factor);
        } else {
            super.drawText(ctx, item, factor, offset);
        }
    }

    @Override
    public boolean nodeAttachmentsVisible() {
        return true;
    }

}

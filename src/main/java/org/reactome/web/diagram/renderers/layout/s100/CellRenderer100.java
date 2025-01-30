package org.reactome.web.diagram.renderers.layout.s100;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.renderers.layout.abs.CellAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;


public class CellRenderer100 extends CellAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
        Node node = (Node) item;
        drawAttachments(ctx, node, factor, offset, true);
        drawSummaryItems(ctx, node, factor, offset);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.highlight(ctx, item, factor, offset);
        Node node = (Node) item;
        drawAttachments(ctx, node, factor, offset, false);
        drawSummaryItems(ctx, node, factor, offset);
    }
    

    @Override
    public boolean nodeAttachmentsVisible() {
        return true;
    }

}

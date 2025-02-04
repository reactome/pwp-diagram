package org.reactome.web.diagram.renderers.layout.s100;

import java.util.List;

import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.layout.abs.ProteinAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ProteinRenderer100 extends ProteinAbstractRenderer {
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
    @SuppressWarnings("Duplicates")
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        Node node = (Node) item;
        if (node.getNodeAttachments() != null) {
            for (NodeAttachment attachment : node.getNodeAttachments()) {
                if (ShapeCategory.isHovered(attachment.getShape(), pos)) {
                    return new HoveredItem(node.getId(), attachment);
                }
            }
        }

        SummaryItem interactorsSummary = node.getInteractorsSummary();
        if (interactorsSummary != null) {
            if (ShapeCategory.isHovered(interactorsSummary.getShape(), pos)) {
                return new HoveredItem(node.getId(), interactorsSummary);
            }
        }
        if(node.getOtherDecoratorsList() != null) {
	        List<SummaryItem> otherSummaries = node.getOtherDecoratorsList();
	        for(SummaryItem summary : otherSummaries) {
	        	if(summary == null) continue;
	        	if(ShapeCategory.isHovered(summary.getShape(), pos))
	        		return new HoveredItem(node.getId(), summary);
	        }
        }
        return super.getHovered(item, pos);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public boolean nodeAttachmentsVisible() {
        return true;
    }

}

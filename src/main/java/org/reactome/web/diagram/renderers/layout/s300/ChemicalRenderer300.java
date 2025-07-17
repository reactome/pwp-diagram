package org.reactome.web.diagram.renderers.layout.s300;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphSimpleEntity;
import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.ChemicalAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ChemicalRenderer300 extends ChemicalAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
        drawSummaryItems(ctx, (Node) item, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        double w = node.getProp().getWidth();
        double h = node.getProp().getHeight();
        if (h >= 30 && w >= h * 1.25 && node.getIsFadeOut() == null) {
            drawChemicalDetails(ctx, node, factor, offset, RendererProperties.INTERACTOR_FONT_SIZE);
        } else {
            super.drawText(ctx, item, factor, offset);
        }
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        Node node = (Node) item;

        SummaryItem interactorsSummary = node.getInteractorsSummary();
        if (interactorsSummary != null) {
            if (ShapeCategory.isHovered(interactorsSummary.getShape(), pos)) {
                return new HoveredItem(node.getId(), interactorsSummary);
            }
        }
        return super.getHovered(item, pos);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.highlight(ctx, item, factor, offset);
        drawSummaryItems(ctx, (Node) item, factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    protected void drawChemicalDetails(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset, double fontSize){
        ctx.save();
        GraphObject graphObject = node.getGraphObject();
        if(graphObject instanceof GraphSimpleEntity) {
            DiagramBox box = new DiagramBox(node.getProp()).transform(factor, offset);

            double splitBasis =  box.getHeight() < box.getWidth()/2 ? box.getHeight() : box.getWidth()/2;
            GraphSimpleEntity se = (GraphSimpleEntity) graphObject;
            if (se.getChemicalImage() != null) {
                double delta = splitBasis * 0.7; // Shrink the image in order to make it fit into the bubble
                Coordinate centre = box.getCentre();
                // Center the image vertically but keep it more to the left half of the bubble
                ctx.drawImage(se.getChemicalImage(), centre.getX() - delta , centre.getY() - delta/2, delta, delta);
            }

            String displayName = node.getDisplayName();
            DiagramBox textBox = box.splitHorizontally(splitBasis).get(1); //box is now the remaining of item box removing the image
            TextRenderer textRenderer = new TextRenderer(fontSize, RendererProperties.NODE_TEXT_PADDING);
            textRenderer.drawTextMultiLine(ctx, displayName, NodePropertiesFactory.get(textBox));
        }
        ctx.restore();
    }
}

package org.reactome.web.diagram.renderers.interactor.s300;

import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.interactor.abs.InteractorEntityAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;
import java.util.Objects;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntityRenderer300 extends InteractorEntityAbstractRenderer {

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        InteractorEntity node = (InteractorEntity) item;
        if (node.isChemical()) {
            drawChemicalDetails(ctx, node, factor, offset);
        } else if (node.isDisease()) {
            drawDiseaseDetails(ctx, node, factor, offset);
        } else {
            drawProteinDetails(ctx, node, factor, offset);
        }
    }

    private void drawDiseaseDetails(AdvancedContext2d ctx, InteractorEntity node, Double factor, Coordinate offset) {
        ctx.save();

        String displayName = node.getDisplayName();
        String details = node.getDetails();

        DiagramBox box = node.transform(factor, offset);  //The image size is supposed to fit the height of the box (and it is a SQUARE)

        drawDiseaseOrProteinDetails(details, node, ctx, box, displayName, factor);

        ctx.restore();
    }

    private void drawProteinDetails(AdvancedContext2d ctx, InteractorEntity node, Double factor, Coordinate offset) {
        ctx.save();
        if (node.getImage() != null) {
            Coordinate pos = CoordinateFactory.get(node.getMinX(), node.getMinY()).transform(factor, offset);
            double delta = (node.getMaxY() - node.getMinY()) * factor;
            ctx.drawImage(node.getImage(), pos.getX(), pos.getY(), delta, delta);
        }

        String displayName = node.getDisplayName();
        String details = node.getDetails();

        DiagramBox box = node.transform(factor, offset);  //The image size is supposed to fit the height of the box (and it is a SQUARE)
        box = box.splitHorizontally(box.getHeight()).get(1); //box is now the remaining of item box removing the image

        drawDiseaseOrProteinDetails(details, node, ctx, box, displayName, factor);
        ctx.restore();
    }

    private void drawDiseaseOrProteinDetails(String details, InteractorEntity node, AdvancedContext2d ctx, DiagramBox box, String displayName, Double factor) {
        TextRenderer textRenderer = new TextRenderer(RendererProperties.INTERACTOR_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);

        if (details == null) {
            if (node.getAlias() == null) {
                textRenderer.drawTextMultiLine(ctx, node.getAccession(), NodePropertiesFactory.get(box));
            } else if (Objects.equals(node.getAlias(), node.getAccession())) {
                textRenderer.drawTextMultiLine(ctx, node.getAlias(), NodePropertiesFactory.get(box));
            } else {
                List<DiagramBox> vBoxes = box.splitVertically(box.getHeight() * 0.6);
                textRenderer.drawTextMultiLine(ctx, node.getAlias(), NodePropertiesFactory.get(vBoxes.get(0)));
                textRenderer.drawTextMultiLine(ctx, node.getAccession(), NodePropertiesFactory.get(vBoxes.get(1)));
            }
        } else {

            List<DiagramBox> vBoxes = box.splitVertically(box.getHeight() * 0.3, box.getHeight() * 0.5);
            //If there is not details it means that we can use the whole right half of the box to write the alias
            ctx.setFont(RendererProperties.getFont(RendererProperties.INTERACTOR_FONT_SIZE));
            DiagramBox aliasBox = vBoxes.get(0);

            textRenderer.drawTextMultiLine(ctx, displayName, NodePropertiesFactory.get(aliasBox));

            double fontSize = 3 * factor;
            ctx.setFont(RendererProperties.getFont(fontSize));
            textRenderer = new TextRenderer(fontSize, RendererProperties.NODE_TEXT_PADDING);
            textRenderer.drawTextSingleLine(ctx, node.getAccession(), vBoxes.get(1).getCentre());

            DiagramBox detailsBox = vBoxes.get(2);
            textRenderer.drawPreformattedText(ctx, details, NodePropertiesFactory.get(detailsBox), true);

        }
    }

    private void drawChemicalDetails(AdvancedContext2d ctx, InteractorEntity node, Double factor, Coordinate offset) {
        ctx.save();
        DiagramBox box = node.transform(factor, offset);
        if (node.getImage() != null) {
            double delta = box.getHeight() * 0.8; // Shrink the image in order to make it fit into the bubble
            Coordinate centre = box.getCentre();
            // Center the image vertically but keep it more to the left half of the bubble
            ctx.drawImage(node.getImage(), centre.getX() - delta, centre.getY() - delta / 2, delta, delta);
        }
        DiagramBox textBox = box.splitHorizontally(box.getWidth() * 0.5).get(1); //box is now the remaining of item box removing the image
        TextRenderer textRenderer = new TextRenderer(RendererProperties.INTERACTOR_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        if (node.getAlias() == null || node.getAccession().length() < 30) {
            textRenderer.drawTextMultiLine(ctx, node.getAccession(), NodePropertiesFactory.get(textBox));
        } else {
            List<DiagramBox> vBoxes = textBox.splitVertically(box.getHeight() * 0.6);
            textRenderer.drawTextMultiLine(ctx, node.getAlias(), NodePropertiesFactory.get(vBoxes.get(0)));

            double fontSize = 3 * factor;
            ctx.setFont(RendererProperties.getFont(fontSize));
            textRenderer = new TextRenderer(fontSize, RendererProperties.NODE_TEXT_PADDING);
            textRenderer.drawTextSingleLine(ctx, node.getAccession(), vBoxes.get(1).getCentre());
        }
        ctx.restore();
    }
}

package org.reactome.web.diagram.renderers.layout.s100;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.Participant;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.ComplexDrugAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class ComplexDrugRenderer100 extends ComplexDrugAbstractRenderer {
    @Override
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        GraphComplex complex = item.getGraphObject();
        NodeProperties prop = ((Node) item).getProp();

        List<Participant> participantsWithExpression = Participant.asSortedList(complex.getParticipantsExpression(t));
        if (participantsWithExpression.isEmpty()) return null;

        double delta = prop.getWidth() / complex.getParticipants().size();
        double minX = prop.getX();
        for (Participant participant : participantsWithExpression) {
            Double value = participant.getExpression();
            double maxX = minX + delta;
            if (pos.getX() > minX && pos.getX() <= maxX) return value;
            minX = maxX;
        }
        return null;
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset) {
        GraphComplex complex = item.getGraphObject();
        double percentage = complex.getHitParticipants().size() / (double) complex.getParticipants().size();

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        buffer.setFillStyle(ctx.getFillStyle());
        buffer.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
        buffer.fill();
        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_IN);
        buffer.fillRect(prop.getX(), prop.getY(), prop.getWidth() * percentage, prop.getHeight());

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        drawText(buffer, item, factor, offset);

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        GraphComplex complex = item.getGraphObject();
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();

        double delta = prop.getWidth() / complex.getParticipants().size();
        double x = prop.getX();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        for (Participant participant : Participant.asSortedList(complex.getParticipantsExpression(t))) {
            double value = participant.getExpression();
            buffer.setFillStyle(AnalysisColours.get().expressionGradient.getColor(value, min, max));
            buffer.fillRect(x, prop.getY(), delta, prop.getHeight());
            x += delta;
        }

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        setColourProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setLineWidth(ctx.getLineWidth());
        shape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setShadowColor("#000000");
        buffer.setShadowBlur(5.0);
        buffer.setFillStyle(AnalysisColours.get().PROFILE.getExpression().getText());
        drawText(buffer, item, factor, offset);

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        buffer.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
        buffer.fill();

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        GraphComplex complex = item.getGraphObject();
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();

        double delta = prop.getWidth() / complex.getParticipants().size();
        double x = prop.getX();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        for (Participant participant : Participant.asSortedList(complex.getParticipantsExpression(t))) {
            double value = participant.getExpression();
            buffer.setFillStyle(AnalysisColours.get().regulationColorMap.getColor((int)value));
            buffer.fillRect(x, prop.getY(), delta, prop.getHeight());
            x += delta;
        }

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        setColourProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setLineWidth(ctx.getLineWidth());
        shape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setShadowColor("#000000");
        buffer.setShadowBlur(5.0);
        buffer.setFillStyle(AnalysisColours.get().PROFILE.getExpression().getText());
        drawText(buffer, item, factor, offset);

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        buffer.octagon(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.COMPLEX_RECT_ARC_WIDTH);
        buffer.fill();

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0);
        buffer.restore();
    }
}
package org.reactome.web.diagram.renderers.layout.s100;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.graph.model.GraphEntitySet;
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
import org.reactome.web.diagram.renderers.layout.abs.SetDrugAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class SetDrugRenderer100 extends SetDrugAbstractRenderer {
    @Override
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        GraphEntitySet set = item.getGraphObject();
        NodeProperties prop = ((Node) item).getProp();

        List<Participant> participantsWithExpression = Participant.asSortedList(set.getParticipantsExpression(t));
        if (participantsWithExpression.isEmpty()) return null;

        double delta = prop.getWidth() / set.getParticipants().size();
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
        GraphEntitySet set = item.getGraphObject();
        double percentage = set.getHitParticipants().size() / (double) set.getParticipants().size();

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        super.draw(ctx, item, factor, offset);
        ctx.restore();

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        buffer.setLineWidth(ctx.getLineWidth());
        buffer.setFillStyle(ctx.getFillStyle());
        buffer.fillRect(prop.getX(), prop.getY(), prop.getWidth() * percentage, prop.getHeight());

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        buffer.setStrokeStyle(ctx.getStrokeStyle());
        innerShape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        buffer.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        buffer.fill();

        ctx.drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        Node node = (Node) item;

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        super.draw(ctx, item, factor, offset);
        ctx.restore();

        GraphEntitySet set = item.getGraphObject();
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double delta = prop.getWidth() / set.getParticipants().size();
        double x = prop.getX();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        buffer.setLineWidth(ctx.getLineWidth());
        buffer.setStrokeStyle(ctx.getStrokeStyle());
        buffer.setFillStyle(ctx.getFillStyle());
        for (Participant participant : Participant.asSortedList(set.getParticipantsExpression(t))) {
            Double value = participant.getExpression();
            buffer.setFillStyle(AnalysisColours.get().expressionGradient.getColor(value, min, max));
            buffer.fillRect(x, prop.getY(), delta, prop.getHeight());
            x += delta;
        }

        buffer.save();
        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        buffer.setStrokeStyle(ctx.getStrokeStyle());
        innerShape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        setColourProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setLineWidth(ctx.getLineWidth());
        if (node.getNeedDashedBorder() != null) {
            buffer.dashedRoundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        } else {
            buffer.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        }
        buffer.stroke();

        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setShadowColor("#000000");
        buffer.setShadowBlur(5.0);
        buffer.setFillStyle(AnalysisColours.get().PROFILE.getExpression().getText());
        drawText(buffer, item, factor, offset);

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        buffer.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        buffer.fill();

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        Node node = (Node) item;

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        if (item.getIsDisease() != null) ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
        super.draw(ctx, item, factor, offset);
        ctx.restore();

        GraphEntitySet set = item.getGraphObject();
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double delta = prop.getWidth() / set.getParticipants().size();
        double x = prop.getX();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        buffer.setLineWidth(ctx.getLineWidth());
        buffer.setStrokeStyle(ctx.getStrokeStyle());
        buffer.setFillStyle(ctx.getFillStyle());
        for (Participant participant : Participant.asSortedList(set.getParticipantsExpression(t))) {
            Double value = participant.getExpression();
            buffer.setFillStyle(AnalysisColours.get().regulationColorMap.getColor(value.intValue()));
            buffer.fillRect(x, prop.getY(), delta, prop.getHeight());
            x += delta;
        }

        buffer.save();
        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        buffer.setStrokeStyle(ctx.getStrokeStyle());
        innerShape(buffer, prop, node.getNeedDashedBorder());
        buffer.stroke();

        setColourProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setLineWidth(ctx.getLineWidth());
        if (node.getNeedDashedBorder() != null) {
            buffer.dashedRoundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        } else {
            buffer.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        }
        buffer.stroke();

        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setShadowColor("#000000");
        buffer.setShadowBlur(5.0);
        buffer.setFillStyle(AnalysisColours.get().PROFILE.getExpression().getText());
        drawText(buffer, item, factor, offset);

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        buffer.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        buffer.fill();

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0);
        buffer.restore();
    }
}

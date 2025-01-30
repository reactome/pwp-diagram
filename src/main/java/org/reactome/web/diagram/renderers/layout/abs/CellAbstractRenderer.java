package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.graph.model.GraphCell;
import org.reactome.web.diagram.data.graph.model.Participant;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.helper.RoundedRectangleHelper;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;
import java.util.function.Function;

import static org.reactome.web.diagram.renderers.common.RendererProperties.*;


public abstract class CellAbstractRenderer extends NodeAbstractRenderer {

    private String nucleusFill;
    private String nucleusStroke;


    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        ctx.save();
        drawCellBodyAndGetNucleus(ctx, item, factor, offset);
        ctx.restore();
    }

    private RoundedRectangleHelper drawCellBodyAndGetNucleus(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties.Builder builder = new NodeProperties.Builder()
                .copy(node.getProp())
                .transform(factor, offset);


        double arc = 2 * ROUND_RECT_ARC_WIDTH;
        RoundedRectangleHelper helper = new RoundedRectangleHelper(builder.build()).setArc(arc);

        helper.trace(ctx);
        ctx.stroke();
        ctx.fill();

        helper.setPadding(CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();

        ctx.setStrokeStyle(nucleusStroke);
        ctx.setFillStyle(nucleusFill);

        RoundedRectangleHelper nucleusHelper = new RoundedRectangleHelper(builder
                .height((prop) -> prop.getHeight() / 2 + 3 * CELL_SEPARATION)
                .build(), 0d, arc
        );

        nucleusHelper.setPadding(3 * CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();

        nucleusHelper.setPadding(4 * CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();
        return nucleusHelper;
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    public boolean nodeAttachmentsVisible() {
        return false;
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {

        if (item.getDisplayName() == null || item.getDisplayName().isEmpty()) {
            return;
        }
        TextMetrics metrics = ctx.measureText(item.getDisplayName());

        Node node = (Node) item;
        NodeProperties.Builder builder = new NodeProperties.Builder()
                .copy(node.getProp())
                .transform(factor, offset)
                .y(prop -> prop.getY() + prop.getHeight() / 2)
                .height(prop -> prop.getHeight() / 2)
                .padding(3 * CELL_SEPARATION);

        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);

        if (metrics.getWidth() <= builder.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), builder.getCenter());
        } else {
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), builder.build());
        }
    }


    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        new RoundedRectangleHelper(prop)
                .setArc(2 * ROUND_RECT_ARC_WIDTH)
                .trace(ctx, needsDashed);
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getCell());
        switch (type) {
            case NORMAL:
                this.nucleusFill = DiagramColours.get().PROFILE.getCellNucleus().getFill();
                this.nucleusStroke = DiagramColours.get().PROFILE.getCellNucleus().getStroke();
                break;
            case FADE_OUT:
                this.nucleusFill = DiagramColours.get().PROFILE.getCellNucleus().getFadeOutFill();
                this.nucleusStroke = DiagramColours.get().PROFILE.getCellNucleus().getFadeOutStroke();
                break;
            case ANALYSIS:
                this.nucleusFill = DiagramColours.get().PROFILE.getCellNucleus().getLighterFill();
                this.nucleusStroke = DiagramColours.get().PROFILE.getCellNucleus().getLighterStroke();
                break;
        }
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(getFont(WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getCell());
    }

    protected void drawAttachments(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset, boolean fill) {
        if (!nodeAttachmentsVisible()) return;
        List<NodeAttachment> atList = node.getNodeAttachments();
        if (atList != null) {
            for (NodeAttachment nodeAttachment : atList) {
                AttachmentAbstractRenderer.draw(ctx, nodeAttachment, factor, offset, fill);
            }
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
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        GraphCell cell = item.getGraphObject();
        NodeProperties prop = ((Node) item).getProp();

        List<Participant> participantsWithExpression = Participant.asSortedList(cell.getParticipantsExpression(t));
        if (participantsWithExpression.isEmpty()) return null;

        double delta = prop.getWidth() / cell.getParticipants().size();
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
        drawAggregatedAnalysis(ctx, overlay, item, factor, offset, ctx.getFillStyle().toString());
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        drawCompositeAnalysis(ctx, overlay, item, t, factor, offset, value -> AnalysisColours.get().expressionGradient.getColor(value, min, max));
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        drawCompositeAnalysis(ctx, overlay, item, t, factor, offset, value -> AnalysisColours.get().regulationColorMap.getColor(value.intValue()));
    }

    protected void drawAggregatedAnalysis(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset, String color) {
        GraphCell cell = item.getGraphObject();
        double percentage = cell.getHitParticipants().size() / (double) cell.getParticipants().size();

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        RoundedRectangleHelper nucleus = drawCellBodyAndGetNucleus(ctx, item, factor, offset);
        ctx.restore();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        buffer.setFillStyle(color);
        nucleus.trace(buffer);
        buffer.fill();
        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_IN);
        buffer.fillRect(nucleus.getX(), nucleus.getY(), nucleus.getWidth() * percentage, nucleus.getHeight());

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        drawText(buffer, item, factor, offset);

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }

    protected void drawCompositeAnalysis(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, Double factor, Coordinate offset, Function<Double, String> colorMapper) {
        GraphCell cell = item.getGraphObject();

        ctx.save();
        setColourProperties(ctx, ColourProfileType.ANALYSIS);
        RoundedRectangleHelper nucleus = drawCellBodyAndGetNucleus(ctx, item, factor, offset);
        ctx.restore();

        double delta = nucleus.getWidth() / cell.getParticipants().size();
        double x = nucleus.getX();

        AdvancedContext2d buffer = overlay.getBuffer();
        buffer.save();
        for (Participant participant : Participant.asSortedList(cell.getParticipantsExpression(t))) {
            buffer.setFillStyle(colorMapper.apply(participant.getExpression()));
            buffer.fillRect(x, nucleus.getY(), delta, nucleus.getHeight());
            x += delta;
        }

        buffer.setGlobalCompositeOperation(Context2d.Composite.SOURCE_ATOP);
        setColourProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setLineWidth(ctx.getLineWidth());

        setTextProperties(buffer, ColourProfileType.ANALYSIS);
        buffer.setFillStyle(AnalysisColours.get().PROFILE.getExpression().getText());
        drawText(buffer, item, factor, offset);

        buffer.setGlobalCompositeOperation(Context2d.Composite.DESTINATION_IN);
        nucleus.trace(buffer);
        buffer.fill();

        overlay.getOverlay().drawImage(buffer.getCanvas(), 0, 0); //TODO: Improve this to copy only the region
        buffer.restore();
    }
}

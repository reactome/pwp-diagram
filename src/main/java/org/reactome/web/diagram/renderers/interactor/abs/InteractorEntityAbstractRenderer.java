package org.reactome.web.diagram.renderers.interactor.abs;

import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorEntityAbstractRenderer extends InteractorAbstractRenderer {

    @Override
    public void shape(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        InteractorEntity entity = (InteractorEntity) item;
        DiagramBox box = item.transform(factor, offset);
        if (entity.isChemical()) {
            ctx.bubble(box.getMinX(), box.getMinY(), box.getMaxX(), box.getMaxY());
        } else {
            ctx.beginPath();
            ctx.rect(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
        }
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;

        shape(ctx, item, factor, offset);

        InteractorEntity entity = (InteractorEntity) item;
        ctx.setFillStyle(entity.getProfile().getFill());
        ctx.setStrokeStyle(entity.getProfile().getStroke());
        ctx.fill();
        ctx.stroke();
    }


    @Override
    public void drawEnrichment(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;
        Boolean isHIt = ((InteractorEntity) item).getIsHit();
        shape(ctx, item, factor, offset);
        boolean isHit = isHIt != null && isHIt;
        ctx.save();
        if (isHit) {
            ctx.setFillStyle(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getStroke());
        } else {
            ctx.setFillStyle(((InteractorEntity) item).getProfile().getLighterFill());
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getLighterStroke());
        }
        ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getStroke());
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;
        draw(ctx, item, factor, offset);
        Boolean isHIt = ((InteractorEntity) item).getIsHit();
        shape(ctx, item, factor, offset);
        boolean isHit = isHIt != null && isHIt;
        ctx.save();
        if (isHit) {
            ctx.setFillStyle(AnalysisColours.get().expressionGradient.getColor(((InteractorEntity) item).getExp().get(t), min, max));
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getStroke());
        } else {
            ctx.setFillStyle(((InteractorEntity) item).getProfile().getLighterFill());
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getLighterStroke());
        }
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, DiagramInteractor item, int t, double min, double max, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;
        draw(ctx, item, factor, offset);
        Boolean isHIt = ((InteractorEntity) item).getIsHit();
        shape(ctx, item, factor, offset);
        boolean isHit = isHIt != null && isHIt;
        ctx.save();
        if (isHit) {
            ctx.setFillStyle(AnalysisColours.get().regulationColorMap.getColor(((InteractorEntity) item).getExp().get(t).intValue()));
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getStroke());
        } else {
            ctx.setFillStyle(((InteractorEntity) item).getProfile().getLighterFill());
            ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getLighterStroke());
        }
        ctx.setStrokeStyle(((InteractorEntity) item).getProfile().getStroke());
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;
        InteractorEntity node = (InteractorEntity) item;
        String displayName = node.getDisplayName();
        if (displayName == null) return;
        DiagramBox box = item.transform(factor, offset);
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        TextMetrics metrics = ctx.measureText(displayName);
        if (metrics.getWidth() <= box.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, displayName, box.getCentre());
        } else {
            textRenderer.drawTextMultiLine(ctx, displayName, NodePropertiesFactory.get(box));
        }

    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if (!item.isVisible()) return;
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }
}

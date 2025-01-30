package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.graph.model.GraphCell;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.layout.abs.CellAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.ExpressionUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class CellRenderer000 extends CellAbstractRenderer {

    protected static double getMedianValue(DiagramObject item, int t) {
        GraphCell cell = item.getGraphObject();
        List<Double> expression = new LinkedList<>(cell.getParticipantsExpression(t).values());
        Collections.sort(expression);
        return ExpressionUtil.median(expression);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //No text at this level
    }


    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        drawAggregatedAnalysis(ctx, overlay, item, factor, offset, AnalysisColours.get().expressionGradient.getColor(getMedianValue(item, t), min, max));
    }

    @Override
    public void drawRegulation(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset) {
        drawAggregatedAnalysis(ctx, overlay, item, factor, offset,AnalysisColours.get().regulationColorMap.getColor((int) getMedianValue(item, t)));
    }


}

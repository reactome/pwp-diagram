package org.reactome.web.diagram.renderers.helper;

import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

import static org.reactome.web.diagram.renderers.common.RendererProperties.ROUND_RECT_ARC_WIDTH;

public final class RoundedRectangleHelper implements NodeProperties {
    private final NodeProperties properties;
    private Double padding = 0d;
    private Double arc = ROUND_RECT_ARC_WIDTH;

    public RoundedRectangleHelper(NodeProperties prop) {
        this.properties = prop;
    }

    public RoundedRectangleHelper(NodeProperties prop, Double padding) {
        this(prop);
        this.setPadding(padding);
    }

    public RoundedRectangleHelper(NodeProperties prop, Double padding, Double arc) {
        this(prop);
        this.setPadding(padding);
        this.setArc(arc);
    }


    public RoundedRectangleHelper setPadding(Double padding) {
        this.padding = padding;
        return this;
    }

    public RoundedRectangleHelper setArc(Double arc) {
        this.arc = arc;
        return this;
    }

    public static double limitArc(Double arc, NodeProperties properties) {
        return Math.max(1, Math.min(arc, properties.getHeight() / 2));
    }


    public Double getPadding() {
        return padding;
    }


    @Override
    public Double getX() {
        return properties.getX() + padding;
    }

    @Override
    public Double getY() {
        return properties.getY() + padding;
    }

    @Override
    public Double getWidth() {
        return properties.getWidth() - 2 * padding;
    }

    @Override
    public Double getHeight() {
        return properties.getHeight() - 2 * padding;
    }

    public Double getArc() {
        return limitArc(arc - padding, properties);
    }

    public void trace(AdvancedContext2d ctx) {
        trace(ctx, false);
    }

    public void trace(AdvancedContext2d ctx, Boolean isDashed) {
        trace(ctx, isDashed, RendererProperties.DASHED_LINE_PATTERN);
    }

    public void trace(AdvancedContext2d ctx, Boolean isDashed, double[] dashLinePattern) {
        if (isDashed == null || !isDashed) {
            ctx.roundedRectangle(
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    this.getArc());
        } else {
            ctx.dashedRoundedRectangle(
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    this.getArc(), dashLinePattern);
        }

    }
}
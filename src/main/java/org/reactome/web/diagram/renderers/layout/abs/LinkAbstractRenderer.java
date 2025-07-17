package org.reactome.web.diagram.renderers.layout.abs;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Link;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class LinkAbstractRenderer extends EdgeAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;

        Link link = (Link) item;
        for (Segment segment : link.getSegments()) {
            segment = SegmentFactory.transform(segment, factor, offset);
            ctx.beginPath();
            DashedLineAbstractRenderer.drawDashedLine(
                    ctx,
                    segment.getFrom().getX(),
                    segment.getFrom().getY(),
                    segment.getTo().getX(),
                    segment.getTo().getY(),
                    RendererProperties.DASHED_LINE_PATTERN[0],
                    RendererProperties.DASHED_LINE_PATTERN[1]
            );
            ctx.stroke();
        }
        ShapeAbstractRenderer.draw(ctx, link.getEndShape(), factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getLink());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getLink());
    }
}


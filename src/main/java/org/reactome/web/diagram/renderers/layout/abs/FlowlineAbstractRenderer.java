package org.reactome.web.diagram.renderers.layout.abs;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Link;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class FlowlineAbstractRenderer extends EdgeAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Link link = (Link) item;
        drawSegments(ctx, link.getSegments(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, link.getEndShape(), factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getFlowline());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getFlowline());
//        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
//        ctx.setFillStyle(DiagramColours.get().PROFILE.getChemical().getText());
    }
}

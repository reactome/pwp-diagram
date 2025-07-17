package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeCommon;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class NoteAbstractRenderer extends AbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        // Set the updated font size and measure the text
        ctx.setFont(RendererProperties.getFont(RendererProperties.NOTE_FONT_SIZE));
        TextRenderer textRenderer = new TextRenderer(RendererProperties.NOTE_FONT_SIZE);

        TextMetrics metrics = ctx.measureText(item.getDisplayName());
        NodeCommon node = (NodeCommon) item;
        Coordinate textPos = node.getPosition().transform(factor, offset);
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        if(metrics.getWidth()<=prop.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), textPos);
        }else{
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), prop);
        }
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {

    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getNote());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setFont(RendererProperties.getFont(RendererProperties.NOTE_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getNote());
    }
}

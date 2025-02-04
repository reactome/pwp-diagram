package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;
import org.reactome.web.diagram.renderers.layout.ConnectorRenderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ConnectorAbstractRenderer implements ConnectorRenderer {

    @Override
    public void draw(AdvancedContext2d ctx,AdvancedContext2d fadeout, AdvancedContext2d decorator, Node node, Double factor, Coordinate offset) {
        if(!RendererManager.get().getRenderer(node).isVisible(node)) return;
        DiagramProfile PROFILE = DiagramColours.get().PROFILE;
        for (Connector connector : node.getConnectors()) {
            if(connector.getIsFadeOut()!=null) {
                fadeout.save();
                fadeout.setFillStyle(PROFILE.getReaction().getLighterFill());
                fadeout.setStrokeStyle(PROFILE.getReaction().getLighterStroke());
                drawConnector(fadeout, connector, factor, offset);
                fadeout.restore();

                decorator.save();
                decorator.setFillStyle(PROFILE.getReaction().getLighterFill());
                decorator.setStrokeStyle(PROFILE.getReaction().getLighterStroke());
                drawStoichiometry(fadeout, connector.getStoichiometry(), factor, offset);
                decorator.restore();
            }else if(connector.getIsDisease()!=null){
                ctx.save();
                ctx.setFillStyle(PROFILE.getProperties().getDisease());
                ctx.setStrokeStyle(PROFILE.getProperties().getDisease());
                drawConnector(ctx, connector, factor, offset);
                ctx.restore();

                decorator.save();
                decorator.setFillStyle(PROFILE.getProperties().getDisease());
                decorator.setStrokeStyle(PROFILE.getProperties().getDisease());
                drawStoichiometry(decorator, connector.getStoichiometry(), factor, offset);
                decorator.restore();
            }else{
                drawConnector(ctx, connector, factor, offset);
                drawStoichiometry(decorator, connector.getStoichiometry(), factor, offset);
            }
        }

    }

    private void drawConnector(AdvancedContext2d ctx, Connector connector, Double factor, Coordinate offset){
        ctx.beginPath();
        for (Segment segment : connector.getSegments()) {
            segment = SegmentFactory.transform(segment, factor, offset);
            ctx.moveTo(segment.getFrom().getX(), segment.getFrom().getY());
            ctx.lineTo(segment.getTo().getX(), segment.getTo().getY());
        }
        ctx.stroke();
        ShapeAbstractRenderer.draw(ctx, connector.getEndShape(), factor, offset);
    }

    private void drawStoichiometry(AdvancedContext2d ctx, Stoichiometry stoichiometry, Double factor, Coordinate offset) {
        if(!stoichiometryVisible()) return;
        this.setTextProperties(ctx); //TODO: Check whether this can be done once
        if(stoichiometry!=null){
            if(stoichiometry.getValue()>1){
                Shape stShape = ShapeFactory.transform(stoichiometry.getShape(), factor, offset);
                ctx.beginPath();
                ctx.rect(
                        stShape.getA().getX(),
                        stShape.getA().getY(),
                        stShape.getB().getX() - stShape.getA().getX(),
                        stShape.getB().getY() - stShape.getA().getY()
                );
                ctx.stroke();
                ctx.save();
                ctx.setFillStyle("#FFFFFF");
                ctx.fill();
                ctx.restore();

                Coordinate c = stShape.getB().minus(stShape.getA()).divide(2).add(stShape.getA());
                ctx.fillText(stoichiometry.getValue().toString(),c.getX(), c.getY());
            }
        }
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx) {

    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
    }
}

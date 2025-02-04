package org.reactome.web.diagram.renderers.common;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfileNode;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public enum ColourProfileType{
    NORMAL,
    FADE_OUT,
    ANALYSIS;

    public void setColourProfile(Context2d ctx, DiagramProfileNode profileNode){
        switch (this){
            case NORMAL:
                ctx.setFillStyle(profileNode.getFill());
                ctx.setStrokeStyle(profileNode.getStroke());
                break;
            case FADE_OUT:
                ctx.setFillStyle(profileNode.getFadeOutFill());
                ctx.setStrokeStyle(profileNode.getFadeOutStroke());
                break;
            case ANALYSIS:
                ctx.setFillStyle(profileNode.getLighterFill());
                ctx.setStrokeStyle(profileNode.getLighterStroke());
                break;
        }
    }

    public void setTextProfile(Context2d ctx, DiagramProfileNode profileNode){
        switch (this){
            case NORMAL:
                ctx.setFillStyle(profileNode.getText());
                break;
            case FADE_OUT:
                ctx.setFillStyle(profileNode.getFadeOutText());
                break;
            case ANALYSIS:
                ctx.setFillStyle(profileNode.getLighterText());
                break;
        }
    }
}


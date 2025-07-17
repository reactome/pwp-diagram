package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.ExpressionValueHoveredHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ExpressionValueHoveredEvent extends GwtEvent<ExpressionValueHoveredHandler> {
    public static Type<ExpressionValueHoveredHandler> TYPE = new Type<ExpressionValueHoveredHandler>();

    private Double expressionValue;

    public ExpressionValueHoveredEvent(Double expressionValue) {
        this.expressionValue = expressionValue;
    }

    @Override
    public Type<ExpressionValueHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ExpressionValueHoveredHandler handler) {
        handler.onExpressionValueHovered(this);
    }

    public Double getExpressionValue() {
        return expressionValue;
    }

    @Override
    public String toString() {
        return "ExpressionValueHoveredEvent{" +
                "expressionValue=" + expressionValue +
                '}';
    }
}

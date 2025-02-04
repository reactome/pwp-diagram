package org.reactome.web.diagram.util.actions;

import com.google.gwt.user.client.ui.FocusWidget;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class UserActionsInstaller {

    public static void addUserActionsHandlers(FocusWidget widget, MouseActionsHandlers handler){
        widget.addClickHandler(handler);
        widget.addDoubleClickHandler(handler);

        widget.addMouseDownHandler(handler);
        widget.addMouseMoveHandler(handler);
        widget.addMouseUpHandler(handler);
        widget.addMouseOutHandler(handler);
        widget.addMouseWheelHandler(handler);

        widget.addTouchCancelHandler(handler);
        widget.addTouchEndHandler(handler);
        widget.addTouchMoveHandler(handler);
        widget.addTouchStartHandler(handler);
    }
}

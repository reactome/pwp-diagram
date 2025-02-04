package org.reactome.web.diagram.util.position;

import com.google.gwt.event.dom.client.MouseEvent;

/**
 * Used to get the correct relative mouse position.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class MousePosition {

    public static int getX(MouseEvent event) {
        return event.getRelativeX(event.getRelativeElement());
    }

    public static int getY(MouseEvent event) {
        return event.getRelativeY(event.getRelativeElement());
    }
}

package org.reactome.web.diagram.controls.notifications;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Notification extends IsWidget {

    void display();

    void conceal();

}

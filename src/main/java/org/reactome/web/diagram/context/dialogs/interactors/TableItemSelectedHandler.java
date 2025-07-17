package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface TableItemSelectedHandler extends EventHandler {
    void onTableItemSelected(TableItemSelectedEvent event);
}


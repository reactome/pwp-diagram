package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface MoleculeSelectedHandler extends EventHandler {
    void onMoleculeSelected(MoleculeSelectedEvent event);
}


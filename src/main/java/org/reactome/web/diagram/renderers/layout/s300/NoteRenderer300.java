package org.reactome.web.diagram.renderers.layout.s300;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.layout.abs.NoteAbstractRenderer;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class NoteRenderer300 extends NoteAbstractRenderer {
    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}

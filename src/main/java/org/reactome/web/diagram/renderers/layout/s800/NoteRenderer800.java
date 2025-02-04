package org.reactome.web.diagram.renderers.layout.s800;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.layout.abs.NoteAbstractRenderer;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class NoteRenderer800 extends NoteAbstractRenderer {
    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}

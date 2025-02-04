package org.reactome.web.diagram.renderers.layout.s800;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.layout.abs.ReactionAbstractRenderer;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ReactionRenderer800 extends ReactionAbstractRenderer {
    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}

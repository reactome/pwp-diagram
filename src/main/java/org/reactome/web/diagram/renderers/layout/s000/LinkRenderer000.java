package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.layout.abs.LinkAbstractRenderer;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LinkRenderer000 extends LinkAbstractRenderer {
    @Override
    public boolean isVisible(DiagramObject item) {
        return false;
    }
}


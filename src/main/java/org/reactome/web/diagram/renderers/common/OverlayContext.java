package org.reactome.web.diagram.renderers.common;

import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class OverlayContext {

    private AdvancedContext2d overlay;
    private AdvancedContext2d buffer;

    public OverlayContext(AdvancedContext2d overlay, AdvancedContext2d buffer) {
        this.overlay = overlay;
        this.buffer = buffer;
    }

    public AdvancedContext2d getOverlay() {
        return overlay;
    }

    public AdvancedContext2d getBuffer() {
        return buffer;
    }
}

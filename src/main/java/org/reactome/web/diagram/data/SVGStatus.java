package org.reactome.web.diagram.data;

import org.vectomatic.dom.svg.OMSVGMatrix;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGStatus {
    private OMSVGMatrix ctm = null;

    public OMSVGMatrix getCTM() {
        return ctm;
    }

    public void setCTM(OMSVGMatrix ctm) {
        this.ctm = ctm;
    }

    @Override
    public String toString() {
        return "SVGStatus{" +
                "ctm=" + ctm.getDescription() +
                '}';
    }
}

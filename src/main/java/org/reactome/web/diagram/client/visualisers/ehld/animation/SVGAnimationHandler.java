package org.reactome.web.diagram.client.visualisers.ehld.animation;

import org.vectomatic.dom.svg.OMSVGMatrix;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SVGAnimationHandler {

    void transform(OMSVGMatrix newTM);
}

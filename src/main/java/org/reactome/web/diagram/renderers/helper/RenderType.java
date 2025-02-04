package org.reactome.web.diagram.renderers.helper;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public enum RenderType {
    FADE_OUT,

    NORMAL,
    DISEASE,

    HIT_INTERACTORS,

    NOT_HIT_BY_ANALYSIS_NORMAL,
    NOT_HIT_BY_ANALYSIS_DISEASE,

    HIT_BY_ENRICHMENT_NORMAL,
    HIT_BY_ENRICHMENT_DISEASE,

    HIT_BY_EXPRESSION_NORMAL,
    HIT_BY_EXPRESSION_DISEASE
}

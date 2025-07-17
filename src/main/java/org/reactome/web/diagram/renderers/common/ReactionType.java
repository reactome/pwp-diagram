package org.reactome.web.diagram.renderers.common;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public enum ReactionType {

    TRANSITION("Transition"),   // Default: should be used for types that are not one of the above four.
    ASSOCIATION("Association"),
    DISSOCIATION("Dissociation"),
    OMITTED_PROCESS("Omitted Process"),
    UNCERTAIN_PROCESS("Uncertain Process");

    private String text;

    ReactionType(String text) {
        this.text = text;
    }

    public static ReactionType get(String type){
        for (ReactionType aux : values()) {
            if(aux.text.equals(type)) return aux;
        }
        return TRANSITION;
    }
}

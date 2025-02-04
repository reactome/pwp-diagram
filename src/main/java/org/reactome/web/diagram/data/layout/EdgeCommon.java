package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface EdgeCommon extends DiagramObject {

    String getReactionType();
    String getInteractionType();

    List<Segment> getSegments();

    Shape getEndShape();

    Shape getReactionShape();

    List<ReactionPart> getInputs();
    List<ReactionPart> getOutputs();
    List<ReactionPart> getCatalysts();
    List<ReactionPart> getInhibitors();
    List<ReactionPart> getActivators();

    List<Long> getPrecedingEvents();
    List<Long> getFollowingEvents();

}

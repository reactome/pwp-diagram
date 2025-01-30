package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphComplex extends GraphCompositeEntity {

    @Override
    public String getIdentifier() {
        Set<GraphPhysicalEntity> participants = getParticipants();
        if (participants.size() == 1) {
            for (GraphPhysicalEntity participant: participants) {
                return participant.getIdentifier();
            }
        }
        return identifier;
    }

    public GraphComplex(EntityNode node) {
        super(node);
    }


    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.complex();
    }
}

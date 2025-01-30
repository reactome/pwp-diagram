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
public class GraphPolymer extends GraphPhysicalEntity {

    public GraphPolymer(EntityNode node) {
        super(node);
    }

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


    @Override
    public boolean isHit() {
        for (GraphPhysicalEntity entity : children) {
            if(entity.isHit()) return true;
        }
        return false;
    }

    @Override
    public Set<GraphPhysicalEntity> getParticipants(){
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        for (GraphPhysicalEntity child : children) {
            rtn.addAll(child.getParticipants());
        }
        return rtn;
    }

    @Override
    public Set<GraphPhysicalEntity> getHitParticipants() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        for (GraphPhysicalEntity child : children) {
            rtn.addAll(child.getHitParticipants());
        }
        return rtn;
    }

    @Override
    public Map<String, Double> getParticipantsExpression(int column){
        Map<String, Double> rtn = new HashMap<>();
        for (GraphPhysicalEntity child : children) {
            rtn.putAll(child.getParticipantsExpression(column));
        }
        return rtn;
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.polymer();
    }
}

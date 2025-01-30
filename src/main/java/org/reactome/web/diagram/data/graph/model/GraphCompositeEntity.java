package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GraphCompositeEntity extends GraphPhysicalEntity {

    @Override
    public Set<GraphPhysicalEntity> getHitParticipants() {
        return children.stream().filter(GraphPhysicalEntity::isHit).collect(Collectors.toSet());
    }

    @Override
    public boolean isHit() {
        return children.stream().anyMatch(GraphPhysicalEntity::isHit);
    }

    @Override
    public boolean isInteractorsHit() {
        return children.stream().anyMatch(GraphPhysicalEntity::isInteractorsHit);
    }

    @Override
    public Map<String, Double> getParticipantsExpression(int column) {
        Map<String, Double> rtn = new HashMap<>();
        for (GraphPhysicalEntity child : children) {
            rtn.putAll(child.getParticipantsExpression(column));
        }
        return rtn;
    }

    @Override
    public Set<GraphPhysicalEntity> getParticipants() {
        return new HashSet<>(children);
    }
    public GraphCompositeEntity(EntityNode node) {
        super(node);
    }
}

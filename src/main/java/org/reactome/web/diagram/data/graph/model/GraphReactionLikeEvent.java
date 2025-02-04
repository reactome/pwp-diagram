package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EventNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class GraphReactionLikeEvent extends GraphEvent {

    private List<GraphPhysicalEntity> inputs = new ArrayList<>();
    private List<GraphPhysicalEntity> outputs = new ArrayList<>();
    private List<GraphPhysicalEntity> catalysts = new ArrayList<>();
    private List<GraphPhysicalEntity> activators = new ArrayList<>();
    private List<GraphPhysicalEntity> inhibitors = new ArrayList<>();
    private List<GraphPhysicalEntity> requirements = new ArrayList<>();

    private List<GraphReactionLikeEvent> followingEvents = new ArrayList<>();
    private List<GraphReactionLikeEvent> precedingEvents = new ArrayList<>();

    public GraphReactionLikeEvent(EventNode node) {
        super(node);
    }

    public List<GraphPhysicalEntity> getInputs() {
        return inputs;
    }

    public void setInputs(List<GraphPhysicalEntity> inputs) {
        this.inputs = inputs;
        for (GraphPhysicalEntity input : inputs) {
            input.addInputIn(this);
        }
    }

    public List<GraphPhysicalEntity> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<GraphPhysicalEntity> outputs) {
        this.outputs = outputs;
        for (GraphPhysicalEntity output : outputs) {
            output.addOutputIn(this);
        }
    }

    public List<GraphPhysicalEntity> getCatalysts() {
        return catalysts;
    }

    public void setCatalysts(List<GraphPhysicalEntity> catalysts) {
        this.catalysts = catalysts;
        for (GraphPhysicalEntity catalyst : catalysts) {
            catalyst.addCatalystIn(this);
        }
    }

    public List<GraphPhysicalEntity> getActivators() {
        return activators;
    }

    public void setActivators(List<GraphPhysicalEntity> activators) {
        this.activators = activators;
        for (GraphPhysicalEntity activator : activators) {
            activator.addActivatorIn(this);
        }
    }

    public List<GraphPhysicalEntity> getInhibitors() {
        return inhibitors;
    }

    public void setInhibitors(List<GraphPhysicalEntity> inhibitors) {
        this.inhibitors = inhibitors;
        for (GraphPhysicalEntity inhibitor : inhibitors) {
            inhibitor.addInhibitorIn(this);
        }
    }

    public List<GraphReactionLikeEvent> getFollowingEvents() {
        return followingEvents;
    }

    public void setFollowingEvents(List<GraphReactionLikeEvent> followingEvents) {
        this.followingEvents = followingEvents;
    }

    public List<GraphReactionLikeEvent> getPrecedingEvents() {
        return precedingEvents;
    }

    public void setPrecedingEvents(List<GraphReactionLikeEvent> precedingEvents) {
        this.precedingEvents = precedingEvents;
    }

    public List<GraphPhysicalEntity> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<GraphPhysicalEntity> requirements) {
        this.requirements = requirements;
    }

    public Set<GraphPhysicalEntity> getParticipants(){
        Set<GraphPhysicalEntity> parts = new HashSet<>();
        parts.addAll(inputs);
        parts.addAll(outputs);
        parts.addAll(catalysts);
        parts.addAll(activators);
        parts.addAll(inhibitors);
        parts.addAll(requirements);

        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        for (GraphPhysicalEntity entity : parts) {
            rtn.addAll(entity.getParticipants());
        }
        return rtn;
    }
}

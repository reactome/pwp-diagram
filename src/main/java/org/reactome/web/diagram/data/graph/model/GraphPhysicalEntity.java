package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class GraphPhysicalEntity extends GraphObject {

    protected String identifier;
    protected List<String> geneNames = new ArrayList<>();
    protected String sampleIdentifier;
    protected String referenceType;
    protected List<GraphPhysicalEntity> children = new ArrayList<>();

    private List<GraphReactionLikeEvent> isInputIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isOutputIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isCatalystIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isActivatorIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isInhibitorIn = new ArrayList<>();

    private boolean interactorsHit = false;

    public GraphPhysicalEntity(EntityNode node) {
        super(node);
        this.identifier = node.getIdentifier();
        this.referenceType = node.getReferenceType();
        if (node.getGeneNames() != null) this.geneNames = node.getGeneNames();
    }

    public boolean addParent(List<GraphPhysicalEntity> parents) {
        return this.parents.addAll(parents);
    }

    public boolean addChildren(List<GraphPhysicalEntity> children) {
        return this.children.addAll(children);
    }

    public boolean addInputIn(GraphReactionLikeEvent rle) {
        return isInputIn.add(rle);
    }

    public boolean addOutputIn(GraphReactionLikeEvent rle) {
        return isOutputIn.add(rle);
    }

    public boolean addCatalystIn(GraphReactionLikeEvent rle) {
        return isCatalystIn.add(rle);
    }

    public boolean addActivatorIn(GraphReactionLikeEvent rle) {
        return isActivatorIn.add(rle);
    }

    public boolean addInhibitorIn(GraphReactionLikeEvent rle) {
        return isInhibitorIn.add(rle);
    }


    public boolean isInteractorsHit() {
        return interactorsHit;
    }

    public void setInteractorsHit(boolean interactorsHit) {
        this.interactorsHit = interactorsHit;
    }

    public boolean isHit() {
        return sampleIdentifier != null;
    }

    public void setIsHit(String sampleIdentifier, List<Double> expression) {
        this.sampleIdentifier = sampleIdentifier;
        this.expression = expression;
    }

    public void resetHit() {
        this.sampleIdentifier = null;
        this.expression = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getGeneNames() {
        return geneNames;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public Set<GraphPhysicalEntity> getParticipants() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        rtn.add(this);
        return rtn;
    }

    public Map<String, Double> getParticipantsExpression(int column) {
        Map<String, Double> rtn = new HashMap<>();
        if (this.isHit() && this.getExpression(column) != null) {
            rtn.put(sampleIdentifier, this.getExpression(column));
        }
        return rtn;
    }

    public Set<GraphPhysicalEntity> getHitParticipants() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        if (this.isHit()) {
            rtn.add(this);
        }
        return rtn;
    }

    public Set<GraphPhysicalEntity> getParentLocations() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        for (GraphPhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    private Set<GraphPhysicalEntity> getParentDiagramIds() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        if (!getDiagramObjects().isEmpty()) {
            rtn.add(this);
        }
        for (GraphPhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    public Set<GraphReactionLikeEvent> participatesIn() {
        Set<GraphReactionLikeEvent> rtn = new HashSet<>();
        rtn.addAll(isInputIn);
        rtn.addAll(isOutputIn);
        rtn.addAll(isCatalystIn);
        rtn.addAll(isActivatorIn);
        rtn.addAll(isInhibitorIn);
        return rtn;
    }

    @Override
    protected String getSecondaryDisplayName() {
        StringBuilder rtn = new StringBuilder(super.getSecondaryDisplayName());
        if (identifier != null) {
            rtn.append((rtn.length() == 0) ? "" : " ").append(identifier);
        }
        if (geneNames != null) {
            for (String geneName : geneNames) {
                rtn.append((rtn.length() == 0) ? "" : " ").append(geneName);
            }
        }
        return rtn.toString();
    }

    public static Comparator<GraphPhysicalEntity> getDisplayNameComparator() {
        return (o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 1;
            }
            if (o1.getDisplayName() == null || o2.getDisplayName() == null) {
                return 1;
            }
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        };
    }
}

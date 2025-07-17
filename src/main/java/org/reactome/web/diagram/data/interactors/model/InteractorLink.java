package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.category.SegmentCategory;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.data.loader.LoaderManager;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class InteractorLink extends DiagramInteractor implements Comparable<InteractorLink> {

    final Node from;
    Coordinate fromPoint;

    Long id;
    Integer evidences;
    double score;

    InteractorLink(Node from, Long id, Integer evidences, String url, double score) {
        super(url);
        this.from = from;
        this.id = id;
        this.evidences = evidences;
        this.score = score;
    }

    public Node getNodeFrom() {
        return from;
    }

    public Coordinate getCoordinateFrom() {
        return fromPoint;
    }

    public abstract Coordinate getCoordinateTo();

    public abstract String getToAccession();

    public Long getId() {
        return id;
    }

    public Integer getEvidences() {
        return evidences;
    }

    public double getScore() {
        return score;
    }

    public abstract void setBoundaries();

    @Override
    public boolean isHovered(Coordinate pos) {
        return SegmentCategory.isInSegment(SegmentFactory.get(fromPoint, getCoordinateTo()), pos);
    }

    @Override
    public boolean isVisible() {
        double threshold = InteractorsContent.getInteractorsThreshold(LoaderManager.INTERACTORS_RESOURCE.getIdentifier());
        return score >= threshold;
    }

    @Override
    public int compareTo(InteractorLink o) {
        int n = Double.compare(o.score, score);
        if (n == 0) return getToAccession().compareTo(o.getToAccession());
        return n;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", visible=" + isVisible() +
                ", score=" + score +
                ", id='" + id + '\'' +
                '}';
    }
}

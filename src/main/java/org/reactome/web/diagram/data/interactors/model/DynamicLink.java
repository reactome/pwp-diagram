package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DynamicLink extends InteractorLink {
    
    private final InteractorEntity to;
    private Coordinate toPoint;

    public DynamicLink(Node from, InteractorEntity to, Long id, Integer evidences, String url, double score) {
        super(from, id, evidences, url, score);
        this.to = to;
        setBoundaries();
    }

    public String getAccession(){
        return to.getAccession();
    }

    public InteractorEntity getTo() {
        return to;
    }

    @Override
    public Coordinate getCoordinateTo() {
        return toPoint;
    }

    @Override
    public String getToAccession() {
        return to.getAccession();
    }

    @Override
    public void setBoundaries() {
        Segment link = SegmentFactory.get(InteractorsLayout.getCentre(from.getProp()), to.getCentre());

        fromPoint = InteractorsLayout.getSegmentsIntersectionOut(link, from);
        toPoint = InteractorsLayout.getSegmentsIntersection(link, this.to);

        minX = Math.min(fromPoint.getX(), toPoint.getX());
        maxX = Math.max(fromPoint.getX(), toPoint.getX());
        minY = Math.min(fromPoint.getY(), toPoint.getY());
        maxY = Math.max(fromPoint.getY(), toPoint.getY());
    }

    public InteractorEntity getInteractorEntity(){
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DynamicLink that = (DynamicLink) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return to != null ? to.equals(that.to) : that.to == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}

package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.layout.Coordinate;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class DiagramInteractor implements QuadTreeBox {

    public enum Type {CHEMICAL, PROTEIN, INTERACTION}

    protected Double minX, minY, maxX, maxY;

    private String url;

    DiagramInteractor(String url) {
        this.url = url;
    }

    public abstract String getAccession();

    public String getUrl() {
        return url;
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    public DiagramBox transform(double factor, Coordinate delta) {
        return new DiagramBox(this).transform(factor, delta);
    }

    public abstract boolean isHovered(Coordinate pos);

    public abstract boolean isVisible();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", visible=" + isVisible() +
                '}';
    }
}

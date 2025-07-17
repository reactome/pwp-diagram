package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * This is a specific link for those proteins/chemicals that interact with themselves
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LoopLink extends InteractorLink {

    private Coordinate centre;
    public static final int RADIUS = 15;
    public static final double START_ANGLE = Math.PI / 2.0;
    public static final double END_ANGLE = 2 * Math.PI;

    public LoopLink(Node node, Long id, Integer evidences, String url, double score) {
        super(node, id, evidences, url, score);
        this.id = id;
        this.score = score;
        setBoundaries();
    }

    public Coordinate getCentre() {
        return centre;
    }

    @Override
    public Coordinate getCoordinateTo() {
        throw new RuntimeException("Do not use getTo for LoopLink");
    }

    @Override
    public boolean isHovered(Coordinate pos) {
        Coordinate delta = pos.minus(centre);
        double len = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
        boolean isInCircumference = Math.abs(RADIUS - len) < 2.0;
        if(isInCircumference){
            double angle = Math.atan2(-delta.getY(), delta.getX());
            angle = angle < 0 ? 2 * Math.PI + angle : angle; //Normalisation 0 - 2 PI
            return angle > START_ANGLE && angle < END_ANGLE;
        }
        return false;
    }

    @Override
    public String getAccession() {
        GraphPhysicalEntity pe = from.getGraphObject();
        return pe.getIdentifier();
    }

    @Override
    public String getToAccession() {
        return getAccession(); //In this case is the same :)
    }

    @Override
    public void setBoundaries() {
        NodeProperties prop = from.getProp();
        centre = CoordinateFactory.get(prop.getX(), prop.getY() + prop.getHeight());
        minX = centre.getX() - RADIUS;
        maxX = centre.getX() + RADIUS;
        minY = centre.getY() - RADIUS;
        maxY = centre.getY() + RADIUS;
    }

}

package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Segment;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SegmentFactory implements Segment {

    private Coordinate from;
    private Coordinate to;

    private SegmentFactory(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    public static Segment get(Coordinate from, Coordinate to){
        return new SegmentFactory(from, to);
    }

    @Override
    public Coordinate getFrom() {
        return from;
    }

    @Override
    public Coordinate getTo() {
        return to;
    }

    public static Segment transform(Segment segment, double factor, Coordinate delta) {
        return new SegmentFactory(
                segment.getFrom().multiply(factor).add(delta),
                segment.getTo().multiply(factor).add(delta)
        );
    }

    @Override
    public String toString() {
        return "SegmentFactory{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}

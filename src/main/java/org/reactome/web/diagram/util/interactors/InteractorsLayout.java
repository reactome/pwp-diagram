package org.reactome.web.diagram.util.interactors;

import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.util.Console;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLayout {
    private static final int BOX_WIDTH = 45;
    private static final int BOX_HEIGHT = 20;
    public static final int SEPARATION = Math.round(BOX_HEIGHT * 1.5f);
    public static final int MIN_HEIGHT = 2 * (2 * BOX_HEIGHT + SEPARATION);
    public static final int MIN_WIDTH = 2 * (2 * BOX_WIDTH + SEPARATION);

    private final Node node;
    private final static Map<Integer, LayoutParameter> layoutParameters = new HashMap<>();

    public InteractorsLayout(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public boolean doLayout(InteractorEntity entity, int i, int n) {
        return doLayout(node, entity, i, n, !entity.isVisible());
    }

    public static boolean doLayout(Node node, InteractorEntity entity, int i, int n, boolean force) {
        if (entity == null || n == 0 || i < 0 || i > n) return false;

        if (force || !entity.isLaidOut()) {
            Coordinate center = getCentre(node.getProp());

            LayoutParameter lp = layoutParameters.computeIfAbsent(n, LayoutParameter::new);

            int edgeNodes = lp.nodeOnTop, nodeCount = 0, prevNodeCount = 0, edgeIndex;

            List<Integer> nodePerEdges = Arrays.asList(lp.nodeOnTop, lp.nodeOnRight, lp.nodeOnBottom, lp.nodeOnLeft);
            for (edgeIndex = 0; edgeIndex < nodePerEdges.size(); edgeIndex++) {
                edgeNodes = nodePerEdges.get(edgeIndex);
                prevNodeCount = nodeCount;
                nodeCount += edgeNodes - 1;
                if (i < nodeCount) break;
            }
            int edgePos = i - prevNodeCount;
            Coordinate pos = interpolate(
                    SegmentFactory.get(
                            getSegmentOrigin(edgeIndex, center, lp.width, lp.height),
                            getSegmentOrigin(edgeIndex + 1, center, lp.width, lp.height)
                    ),
                    edgePos,
                    edgeNodes - 1
            );

            entity.setMinX(pos.getX() - BOX_WIDTH);
            entity.setMaxX(pos.getX() + BOX_WIDTH);
            entity.setMinY(pos.getY() - BOX_HEIGHT);
            entity.setMaxY(pos.getY() + BOX_HEIGHT);
            return true;
        }
        return false;
    }

    private static class LayoutParameter {
        final int nodeOnLeft;
        final int nodeOnRight;
        final int nodeOnTop;
        final int nodeOnBottom;
        final int width;
        final int height;

        LayoutParameter(int n) {
            float rationalNodePerEdge = n / 4f + 1;
            int baseNodePerEdge = (int) Math.floor(rationalNodePerEdge);
            int remaining = Math.round((rationalNodePerEdge - baseNodePerEdge) * 4);

            nodeOnLeft = baseNodePerEdge + (remaining > 0 ? 1 : 0);
            nodeOnRight = baseNodePerEdge + (remaining > 1 ? 1 : 0);
            nodeOnTop = baseNodePerEdge + (remaining > 2 ? 1 : 0);
            nodeOnBottom = baseNodePerEdge;
            height = Math.max((2 * BOX_HEIGHT + SEPARATION) * (nodeOnLeft - 1), MIN_HEIGHT);
            width = Math.max((2 * BOX_WIDTH + SEPARATION) * (nodeOnTop - 1), MIN_WIDTH);
        }
    }

    private static Coordinate getSegmentOrigin(int edgeIndex, Coordinate center, float width, float height) {
        edgeIndex = edgeIndex % 4;
        return CoordinateFactory.get(
                center.getX() + (width / 2) * (edgeIndex == 1 || edgeIndex == 2 ? 1 : -1),
                center.getY() + (height / 2) * (edgeIndex > 1 ? 1 : -1)
        );
    }

    public static Coordinate interpolate(Segment segment, int pos, int totalPositions) {
        float r = pos / (float) totalPositions;
        return CoordinateFactory.get(
                segment.getFrom().getX() + r * (segment.getTo().getX() - segment.getFrom().getX()),
                segment.getFrom().getY() + r * (segment.getTo().getY() - segment.getFrom().getY())
        );
    }

    public static Coordinate getCentre(NodeProperties prop) {
        return CoordinateFactory.get(
                prop.getX() + prop.getWidth() / 2.0,
                prop.getY() + prop.getHeight() / 2.0
        );
    }

    public static Coordinate getSegmentsIntersection(Segment interactor, InteractorEntity entity) {
        Coordinate point = interactor.getTo();
        for (Segment segment : getOuterSegments(entity)) {
            Coordinate aux = getSegmentsIntersection(segment, interactor);
            if (aux != null) return aux;
        }
        return point;
    }

    public static Coordinate getSegmentsIntersectionOut(Segment interactor, Node node) {
        return getSegmentsIntersection(interactor, node, true);
    }

    public static Coordinate getSegmentsIntersectionIn(Segment interactor, Node node) {
        return getSegmentsIntersection(interactor, node, false);
    }

    private static Coordinate getSegmentsIntersection(Segment interactor, Node node, boolean outLink) {
        Coordinate point = getSegmentIntersectionWithNode(interactor, node);

        //The following checks the intersection of the interactor line with the summary item
        //Note: When the link is static, it also has to take into account the summary item of the
        //pointed node
        SummaryItem summaryItem = node.getInteractorsSummary();
        if (summaryItem != null) {
            Segment segment = SegmentFactory.get(point, outLink ? interactor.getTo() : interactor.getFrom());
            Shape shape = summaryItem.getShape();
            if (shape != null) {
                List<Coordinate> points = getSegmentCircleIntersection(segment, shape.getC(), shape.getR() + 1);
                if (!points.isEmpty()) point = points.get(points.size() - 1);
            }
        }
        return point;
    }

    private static Coordinate getSegmentIntersectionWithNode(Segment interactor, Node node) {
        Coordinate point = interactor.getTo();
        for (Segment segment : getOuterSegments(node)) {
            Coordinate aux = getSegmentsIntersection(segment, interactor);
            if (aux != null) return aux;
        }
        return point;
    }

    private static Coordinate getSegmentsIntersection(Segment s1, Segment s2) {
        Coordinate from1 = s1.getFrom();
        Coordinate to1 = s1.getTo();
        Coordinate from2 = s2.getFrom();
        Coordinate to2 = s2.getTo();
        return intersection(from1.getX(), from1.getY(), to1.getX(), to1.getY(), from2.getX(), from2.getY(), to2.getX(), to2.getY());
    }

    private static Coordinate intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (!doesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) return null;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0) return null;

        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return CoordinateFactory.get(xi, yi);
    }
    //Code adapted from http://www.java-gaming.org/index.php?topic=22590.0

    public static boolean doesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));

        if (d == 0.0f) return false;

        double ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
        double ub = (((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3))) / d;

        return ((ua >= 0.0d) && (ua <= 1.0d) && (ub >= 0.0d) && (ub <= 1.0d));
    }
//    public static boolean isPointInCircle(Coordinate point, Coordinate center, double radius) {
//        Coordinate delta = point.minus(center);
//        return Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY()) <= radius;

//    }

    private static boolean isPointInSegment(Coordinate point, Segment segment) {
        double dx = segment.getTo().getX() - segment.getFrom().getX();
        double dy = segment.getTo().getY() - segment.getFrom().getY();
        double innerProduct = (point.getX() - segment.getFrom().getX()) * dx + (point.getY() - segment.getFrom().getY()) * dy;
        return 0 <= innerProduct && innerProduct <= dx * dx + dy * dy;
    }
    //Code adapted from http://stackoverflow.com/questions/13053061/circle-line-intersection-points

    private static List<Coordinate> getSegmentCircleIntersection(Segment s, Coordinate center, double radius) {
        double baX = s.getTo().getX() - s.getFrom().getX();
        double baY = s.getTo().getY() - s.getFrom().getY();
        double caX = center.getX() - s.getFrom().getX();
        double caY = center.getY() - s.getFrom().getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) return Collections.emptyList();

        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Coordinate p1 = CoordinateFactory.get(s.getFrom().getX() - baX * abScalingFactor1, s.getFrom().getY() - baY * abScalingFactor1);
        // abScalingFactor1 == abScalingFactor2
        if (disc == 0) {
            if (isPointInSegment(p1, s)) {
                return Collections.singletonList(p1);
            } else {
                return Collections.emptyList();
            }
        }

        Coordinate p2 = CoordinateFactory.get(s.getFrom().getX() - baX * abScalingFactor2, s.getFrom().getY() - baY * abScalingFactor2);

        List<Coordinate> rtn = new ArrayList<>();
        if (isPointInSegment(p1, s)) rtn.add(p1);
        if (isPointInSegment(p2, s)) rtn.add(p2);
        return rtn;
    }

    private static List<Segment> getOuterSegments(Node node) {
        NodeProperties prop = node.getProp();
        Coordinate a = CoordinateFactory.get(prop.getX(), prop.getY());
        Coordinate b = CoordinateFactory.get(prop.getX() + prop.getWidth(), prop.getY());
        Coordinate c = CoordinateFactory.get(prop.getX() + prop.getWidth(), prop.getY() + prop.getHeight());
        Coordinate d = CoordinateFactory.get(prop.getX(), prop.getY() + prop.getHeight());

        List<Segment> segments = new LinkedList<>();
        segments.add(SegmentFactory.get(a, b));
        segments.add(SegmentFactory.get(b, c));
        segments.add(SegmentFactory.get(c, d));
        segments.add(SegmentFactory.get(d, a));
        return segments;
    }

    private static List<Segment> getOuterSegments(InteractorEntity entity) {
        List<Segment> segments = new LinkedList<>();

        if (entity.isChemical()) {
            DiagramBox box = new DiagramBox(entity);
            Coordinate a = CoordinateFactory.get(box.getMinX() + box.getWidth() * 0.4, entity.getMinY());
            Coordinate b = CoordinateFactory.get(box.getMinX() + box.getWidth() * 0.6, entity.getMinY());
            Coordinate c = CoordinateFactory.get(box.getMaxX(), box.getMinY() + box.getHeight() * 0.35);
            Coordinate d = CoordinateFactory.get(box.getMaxX(), box.getMinY() + box.getHeight() * 0.55);
            Coordinate e = CoordinateFactory.get(box.getMinX() + box.getWidth() * 0.6, entity.getMaxY());
            Coordinate f = CoordinateFactory.get(box.getMinX() + box.getWidth() * 0.4, entity.getMaxY());
            Coordinate g = CoordinateFactory.get(box.getMinX(), box.getMinY() + box.getHeight() * 0.55);
            Coordinate h = CoordinateFactory.get(box.getMinX(), box.getMinY() + box.getHeight() * 0.35);

            segments.add(SegmentFactory.get(a, b));
            segments.add(SegmentFactory.get(b, c));
            segments.add(SegmentFactory.get(c, d));
            segments.add(SegmentFactory.get(d, e));
            segments.add(SegmentFactory.get(e, f));
            segments.add(SegmentFactory.get(f, g));
            segments.add(SegmentFactory.get(g, h));
            segments.add(SegmentFactory.get(h, a));
        } else {
            Coordinate a = CoordinateFactory.get(entity.getMinX(), entity.getMinY());
            Coordinate b = CoordinateFactory.get(entity.getMaxX(), entity.getMinY());
            Coordinate c = CoordinateFactory.get(entity.getMaxX(), entity.getMaxY());
            Coordinate d = CoordinateFactory.get(entity.getMinX(), entity.getMaxY());

            segments.add(SegmentFactory.get(a, b));
            segments.add(SegmentFactory.get(b, c));
            segments.add(SegmentFactory.get(c, d));
            segments.add(SegmentFactory.get(d, a));
        }

        return segments;
    }
}

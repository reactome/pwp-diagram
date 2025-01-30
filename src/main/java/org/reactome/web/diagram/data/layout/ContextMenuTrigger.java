package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * This is the small triangle that appears inside a diagram node and
 * triggers the display of the context menu.
 * It is defined by 3 points:
 * <p>
 * [b]\
 * |   \
 * |    \
 * |    [a]
 * |    /
 * |   /
 * [c]/
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContextMenuTrigger {

    private final Coordinate a;
    private final Coordinate b;
    private final Coordinate c;

    ContextMenuTrigger(Coordinate a, Coordinate b, Coordinate c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public ContextMenuTrigger(NodeCommon node) {
        NodeProperties prop = node.getProp();
        double x = prop.getX() + prop.getWidth();
        Double y = prop.getY();
        String renderableClass = node.getRenderableClass();
        if (renderableClass.equals("Gene")) {
            y += prop.getHeight() * 3 / 4;
        } else if (renderableClass.equals("Cell")) {
            y += prop.getHeight() * 3 / 4;
            x -= 2;
        } else {
            y += prop.getHeight() / 2;
        }

        if (renderableClass.equals("EntitySet")) {
            x -= 3;
        }

        this.a = CoordinateFactory.get(x - 2, y);
        this.b = CoordinateFactory.get(x - 7, y - 3);
        this.c = CoordinateFactory.get(x - 7, y + 3);
    }

    public boolean isHovered(Coordinate m) {
        double s = a.getY() * c.getX() - a.getX() * c.getY() + (c.getY() - a.getY()) * m.getX() + (a.getX() - c.getX()) * m.getY();
        double t = a.getX() * b.getY() - a.getY() * b.getX() + (a.getY() - b.getY()) * m.getX() + (b.getX() - a.getX()) * m.getY();

        if ((s < 0) != (t < 0)) return false;

        double aux = -b.getY() * c.getX() + a.getY() * (c.getX() - b.getX()) + a.getX() * (b.getY() - c.getY()) + b.getX() * c.getY();
        if (aux < 0.0) {
            s = -s;
            t = -t;
            aux = -aux;
        }
        return s > 0 && t > 0 && (s + t) < aux;
    }

    public ContextMenuTrigger transform(double factor, Coordinate delta) {
        return new ContextMenuTrigger(
                a.transform(factor, delta),
                b.transform(factor, delta),
                c.transform(factor, delta)
        );
    }

    public Coordinate getA() {
        return a;
    }

    public Coordinate getB() {
        return b;
    }

    public Coordinate getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContextMenuTrigger trigger = (ContextMenuTrigger) o;

        if (a != null ? !a.equals(trigger.a) : trigger.a != null) return false;
        //noinspection SimplifiableIfStatement
        if (b != null ? !b.equals(trigger.b) : trigger.b != null) return false;
        return !(c != null ? !c.equals(trigger.c) : trigger.c != null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContextMenuTrigger{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}

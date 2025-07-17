package org.reactome.web.diagram.data.layout.category;

import com.google.web.bindery.autobean.shared.AutoBean;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeCommon;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
public class DiagramObjectCategory {

    @SuppressWarnings("unchecked")
    public static Coordinate add(AutoBean<Coordinate> rawObjectAutoBean, Coordinate value){
        Coordinate c = rawObjectAutoBean.as();
        return CoordinateFactory.get(c.getX() + value.getX(), c.getY() + value.getY());
    }

    @SuppressWarnings("unchecked")
    public static Coordinate multiply(AutoBean<Coordinate> rawObjectAutoBean, double factor){
        Coordinate c = rawObjectAutoBean.as();
        return CoordinateFactory.get(c.getX() * factor, c.getY() * factor);
    }

    @SuppressWarnings("unchecked")
    public static Coordinate divide(AutoBean<Coordinate> rawObjectAutoBean, double factor){
        Coordinate c = rawObjectAutoBean.as();
        return CoordinateFactory.get(c.getX() / factor, c.getY() / factor);
    }

    @SuppressWarnings("unchecked")
    public static Coordinate minus(AutoBean<Coordinate> rawObjectAutoBean, Coordinate value){
        Coordinate c = rawObjectAutoBean.as();
        return CoordinateFactory.get(c.getX() - value.getX(), c.getY() - value.getY());
    }

    @SuppressWarnings("unchecked")
    public static Coordinate transform(AutoBean<Coordinate> rawObjectAutoBean, double factor, Coordinate delta){
        Coordinate c = rawObjectAutoBean.as();
        return CoordinateFactory.get(c.getX() * factor + delta.getX(), c.getY() * factor + delta.getY());
    }

      //This would be the right way of doing it, but the json loading takes around 90 more milliseconds
      //so for the time being, let's assume there won't be duplicates so we can rely on the pointer to
      //distinguish objects (uncomment the following two methods if that assumption is not longer valid)
//    public static boolean equals(AutoBean<? extends DiagramObject> rawObjectAutoBean, Object o) {
//        DiagramObject r = rawObjectAutoBean.as();
//
//        if (r == o) return true;
//        if (o == null || r.getClass() != o.getClass()) return false;
//
//        DiagramObject that = (DiagramObject) o;
//
//        return !(r.getReactomeId() != null ? !r.getReactomeId().equals(that.getReactomeId()) : that.getReactomeId() != null);
//    }
//
//    public static int hashCode(AutoBean<? extends DiagramObject> rawObjectAutoBean) {
//        DiagramObject r = rawObjectAutoBean.as();
//        return r.getReactomeId() != null ? r.getReactomeId().hashCode() : 0;
//    }

    //Don't use getContextMenuTrigger (because AutoBean only overrides the non-property methods)
    public static ContextMenuTrigger contextMenuTrigger(AutoBean<? extends DiagramObject> rawObjectAutoBean){
        DiagramObject r = rawObjectAutoBean.as();
        if(r instanceof NodeCommon){
            NodeCommon node = (NodeCommon) r;
            return new ContextMenuTrigger(node);
        }
        return null;
    }

    public static String toString(AutoBean<? extends DiagramObject> rawObjectAutoBean){
        DiagramObject r = rawObjectAutoBean.as();
        return  r.getSchemaClass() + "{" +
                "id=" + r.getId() +
                ", dbId=" + r.getReactomeId() +
                ", displayName='" + r.getDisplayName() + '\'' +
                '}';
    }
}

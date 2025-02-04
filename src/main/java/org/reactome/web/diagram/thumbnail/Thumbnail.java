package org.reactome.web.diagram.thumbnail;

import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Thumbnail extends IsWidget {

    void contentRequested();

    void diagramPanningEvent(Box visibleArea);

    void diagramProfileChanged();

    void diagramRendered(Content content, Box visibleArea);

    void diagramZoomEvent(Box visibleArea);

    void graphObjectHovered(GraphObject hovered);

    void graphObjectSelected(GraphObject graphObject);

    void setHoveredItem(String id);

    void setSelectedItem(String id);

    void viewportResized(Box visibleArea);

    default int getWidth() {
        return 0;
    }
}

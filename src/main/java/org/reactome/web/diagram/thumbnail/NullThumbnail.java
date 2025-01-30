package org.reactome.web.diagram.thumbnail;

import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

public class NullThumbnail extends AbsolutePanel implements Thumbnail {
    @Override
    public void contentRequested() {

    }

    @Override
    public void diagramPanningEvent(Box visibleArea) {

    }

    @Override
    public void diagramProfileChanged() {

    }

    @Override
    public void diagramRendered(Content content, Box visibleArea) {

    }

    @Override
    public void diagramZoomEvent(Box visibleArea) {

    }

    @Override
    public void graphObjectHovered(GraphObject hovered) {

    }

    @Override
    public void graphObjectSelected(GraphObject graphObject) {

    }

    @Override
    public void setHoveredItem(String id) {

    }

    @Override
    public void setSelectedItem(String id) {

    }

    @Override
    public void viewportResized(Box visibleArea) {

    }
}

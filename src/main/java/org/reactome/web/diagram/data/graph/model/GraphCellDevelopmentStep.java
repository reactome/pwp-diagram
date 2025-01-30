package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EventNode;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphCellDevelopmentStep extends GraphReactionLikeEvent {

    public GraphCellDevelopmentStep(EventNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.reaction();
    }
}

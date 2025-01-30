package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphEntitySet extends GraphCompositeEntity {

    public GraphEntitySet(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.entitySet();
    }
}

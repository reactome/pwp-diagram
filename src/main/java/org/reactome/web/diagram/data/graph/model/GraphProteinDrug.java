package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.util.pdbe.PDBeLoader;
import org.reactome.web.diagram.util.pdbe.model.PDBObject;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphProteinDrug extends GraphPhysicalEntity implements PDBeLoader.Handler {

    private PDBObject pdbObject;
    private ImageElement proteinImage;

    public GraphProteinDrug(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.proteinDrug();
    }

    public ImageElement getProteinImage() {
        if(proteinImage == null){
            proteinImage = ImageElement.as(PDBeLoader.LOADING.getElement());
            PDBeLoader.get().loadBestStructure(this, getIdentifier());
        }
        return proteinImage;
    }

    @Override
    public void onPDBObjectLoaded(PDBObject pdbObject) {
        this.pdbObject = pdbObject;
    }

    @Override
    public void onImageLoaded(Image image) {
        proteinImage = ImageElement.as(image.getElement());
    }
}

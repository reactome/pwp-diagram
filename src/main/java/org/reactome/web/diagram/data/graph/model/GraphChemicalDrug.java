package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.util.chemical.ChemicalImageLoader;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphChemicalDrug extends GraphPhysicalEntity implements ChemicalImageLoader.Handler {

    private ImageElement chemicalImage;

    public GraphChemicalDrug(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.chemicalDrug();
    }

    public ImageElement getChemicalImage() {
        if(chemicalImage == null){
            chemicalImage = ImageElement.as(ChemicalImageLoader.LOADING.getElement());
            //In Reactome, small molecules identifier are from CHEBI
            ChemicalImageLoader.get().loadImage(this, "CHEBI:" + getIdentifier());
        }
        return chemicalImage;
    }

    @Override
    public void onChemicalImageLoaded(Image image) {
        chemicalImage = ImageElement.as(image.getElement());
    }
}

package org.reactome.web.diagram.data.interactors.model.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface InteractorImages extends ClientBundle {

    InteractorImages INSTANCE = GWT.create(InteractorImages.class);

    @Source("Interactor.png")
    ImageResource interactor();

}


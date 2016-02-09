package org.reactome.web.diagram.util.chemical;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.reactome.web.diagram.events.StructureImageLoadedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class ChEBI_ImageLoader extends Chemical_ImageLoader {

    private static ChEBI_ImageLoader loader;

    private EventBus eventBus;

    private ChEBI_ImageLoader(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void initialise(EventBus eventBus){
        if (loader != null) {
            throw new RuntimeException("ChEBI Image Loader has already been initialised. " +
                    "Only one initialisation is permitted per Diagram Viewer instance.");
        }
        loader = new ChEBI_ImageLoader(eventBus);
    }

    public static ChEBI_ImageLoader get() {
        if (loader == null) {
            throw new RuntimeException("ChEBI Image Loader has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return loader;
    }

    public void loadImage(final Handler handler, String identifier){
        String id = identifier.replaceAll("^CHEBI[-:_]?", "");
        final String url = "http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&chebiId=" + id + "&dimensions=200&transbg=true";
        final Image rtn = new Image(url);
        rtn.setAltText(url);
        //Next line is meant to avoid the "SecurityError" problem when exporting tainted canvases
        rtn.getElement().setAttribute("crossOrigin", "anonymous");
        rtn.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent loadEvent) {
                //It was just added to the DOM to force load so this method is called
                rtn.getElement().removeFromParent();
                rtn.setVisible(true);
                handler.onChemicalImageLoaded(rtn);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(rtn), ChEBI_ImageLoader.this);
            }
        });
        rtn.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent errorEvent) {
                rtn.getElement().removeFromParent();
                handler.onChemicalImageLoaded(NOT_FOUND);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), ChEBI_ImageLoader.this);
            }
        });
        //Making it invisible and attaching it to the DOM forces the loading of the image (so the previous handler is called)
        rtn.setVisible(false);
        RootPanel.get().add(rtn);
    }
}

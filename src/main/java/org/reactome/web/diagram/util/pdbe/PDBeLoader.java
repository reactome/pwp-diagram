package org.reactome.web.diagram.util.pdbe;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.reactome.web.diagram.events.StructureImageLoadedEvent;
import org.reactome.web.diagram.util.pdbe.model.PDBObject;
import org.reactome.web.diagram.util.pdbe.model.QueryResult;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class PDBeLoader {

    private static PDBeLoader loader;

    public static void initialise(EventBus eventBus) {
        if (loader != null) {
            throw new RuntimeException("PDBe Loader has already been initialised. " +
                    "Only one initialisation is permitted per Diagram Viewer instance.");
        }
        loader = new PDBeLoader(eventBus);
    }

    public static PDBeLoader get() {
        if (loader == null) {
            throw new RuntimeException("PDBe Loader has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return loader;
    }

    private EventBus eventBus;

    private PDBeLoader(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public interface Handler {
        void onPDBObjectLoaded(PDBObject pdbObject);

        void onImageLoaded(Image image);
    }

    public void loadBestStructure(final Handler handler, final String acc) {
        String url = "https://www.ebi.ac.uk/pdbe/api/mappings/best_structures/" + acc + "/";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()) {
                        case 200:
                            QueryResult result = QueryResult.buildQueryResult(response.getText());
                            JsArray<PDBObject> pdbs = result.getPDBObject(acc);
                            if (pdbs != null && pdbs.length() > 0) {
                                PDBObject object = pdbs.get(0);
                                handler.onPDBObjectLoaded(object);
                                loadPDBeImage(handler, object);
                            } else {
                                handler.onImageLoaded(NOT_FOUND);
                                eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), PDBeLoader.this);
                            }
                            break;
                        default:
                            handler.onImageLoaded(NOT_FOUND);
                            eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), PDBeLoader.this);
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onImageLoaded(NOT_FOUND);
                    eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), PDBeLoader.this);
                }
            });
        } catch (RequestException e) {
            handler.onImageLoaded(NOT_FOUND);
            eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), PDBeLoader.this);
        }
    }

    private void loadPDBeImage(final Handler handler, final PDBObject object) {
        String url = "https://www.ebi.ac.uk/pdbe/static/entry/" + object.getPdbid() + "_deposited_chain_front_image-800x800.png";
        final Image rtn = new Image(url);
        rtn.setAltText(url);
        //Next line is meant to avoid the "SecurityError" problem when exporting tainted canvases
        rtn.getElement().setAttribute("crossOrigin", "anonymous");
        rtn.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent loadEvent) {
                //It was just added to the DOM to force load so this method is called
                rtn.getElement().removeFromParent();
                rtn.setVisible(true); //Right now the image needs to be visible
                handler.onImageLoaded(rtn);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(rtn), PDBeLoader.this);
            }
        });
        rtn.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent errorEvent) {
                rtn.getElement().removeFromParent();
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), PDBeLoader.this);
                handler.onImageLoaded(NOT_FOUND);
            }
        });
        //Making it invisible and attaching it to the DOM forces the loading of the image (so the previous handler is called)
        rtn.setVisible(false);
        RootPanel.get().add(rtn);
    }


    public static final Image LOADING = new Image(PDBeImages.INSTANCE.loading());
    public static final Image NOT_FOUND = new Image(PDBeImages.INSTANCE.notFound());

    static {
        RootPanel.get().add(LOADING);
        LOADING.setAltText("Loading protein structure");
        LOADING.getElement().removeFromParent();
        LOADING.getElement().setAttribute("crossOrigin", "anonymous");

        RootPanel.get().add(NOT_FOUND);
        NOT_FOUND.setAltText("Protein structure not found");
        NOT_FOUND.getElement().removeFromParent();
        NOT_FOUND.getElement().setAttribute("crossOrigin", "anonymous");
    }

    interface PDBeImages extends ClientBundle {

        PDBeImages INSTANCE = GWT.create(PDBeImages.class);

        @Source("loading.png")
        ImageResource loading();

        @Source("notFound.png")
        ImageResource notFound();
    }
}

package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.data.content.Content;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SnapshotTabPanel extends FlowPanel {
    private static String DISCLAIMER = "Please keep in mind that you can download a high resolution image of the diagram from the second tab panel of this dialog.";


    public SnapshotTabPanel(final String diagramStId, final Image snapshot, final Content.Type contentType) {
        String userAgent = Window.Navigator.getUserAgent().toLowerCase();
        boolean isIE = userAgent.contains("msie") || userAgent.contains("trident");

        String imageFormat = contentType == Content.Type.SVG ? "SVG" : "PNG";

        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(RESOURCES.getCSS().mainTabPanel());

        FlowPanel imagePanel = new FlowPanel();
        imagePanel.add(snapshot);
        imagePanel.setStyleName(RESOURCES.getCSS().imagePanel());
        snapshot.setStyleName(RESOURCES.getCSS().image());
        vp.add(imagePanel);
        this.add(vp);

        FlowPanel buttons = new FlowPanel();
        buttons.setStyleName(RESOURCES.getCSS().buttonsContainer());
        if (isIE) {
            Label infoLabel = new Label("To save the image, simply right-click on the image, and then click \'Save Picture As...\'");
            infoLabel.addStyleName(RESOURCES.getCSS().infoLabel());
            buttons.add(infoLabel);
        } else {
            // For downloading the image
            Button button = new IconButton("Download as " + imageFormat, RESOURCES.downloadNormal());
            button.setStyleName(RESOURCES.getCSS().downloadPNG());
            button.setTitle("Save diagram as " + imageFormat + " image");

            Anchor anchor = new Anchor();
            anchor.setHref(snapshot.getUrl());
            anchor.getElement().setAttribute("download", diagramStId + "." + imageFormat.toLowerCase());
//            anchor.addClickHandler(clickEvent -> closeDialog());
            anchor.getElement().appendChild(button.getElement());
            buttons.add(anchor);
        }
        if(gsUploadByPostAvailable()){
            Button genomespace = new IconButton("Upload to GenomeSpace", RESOURCES.uploadNormal());
            genomespace.addClickHandler(event -> {
                String mimeString = "image/" + imageFormat.toLowerCase();
                String dataURL = snapshot.getUrl();
                String base64 = dataURL.split(",")[1];
                // Convert the base64 string to a blob and send to
                // GenomeSpace using their JavaScript API
                uploadToGenomeSpace(base64, mimeString, imageFormat.toLowerCase(), diagramStId);
//                closeDialog();
            });
            genomespace.setStyleName(RESOURCES.getCSS().genomespace());
            genomespace.setTitle("Upload image to GenomeSpace");
            buttons.add(genomespace);
        }
        vp.add(buttons);

        Label disclaimer = new Label(DISCLAIMER);
        disclaimer.setStyleName(RESOURCES.getCSS().disclaimer());
        vp.add(disclaimer);
    }

//    private void closeDialog() {
//        hostDialog.hide();
//        hostDialog.removeFromParent();
//    }

    private static native boolean gsUploadByPostAvailable() /*-{
        return $wnd.gsUploadByPost;
    }-*/;

    private static native void uploadToGenomeSpace(String base64, String mimeString, String imageFormat, String identifier) /*-{
        if(!$wnd.gsUploadByPost) return;
        var binary = atob(base64);                 //
        //noinspection JSPrimitiveTypeWrapperUsage
        var array  = new Array();                  // Adapted from
        for(var i = 0; i < binary.length; i++) {   // stackoverflow.com/questions/4998908
            array.push(binary.charCodeAt(i));      // and many similar discussions
        }                                          //
        var uarray = new Uint8Array(array);        //
        var blob = new Blob([uarray], {type: mimeString});
        var formData = new FormData();
        var imageName = "Reactome_pathway_" + identifier + "." + imageFormat;
        formData.append("webmasterfile", blob, imageName);
        $wnd.gsUploadByPost(formData);
    }-*/;



    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/download_normal.png")
        ImageResource downloadNormal();

        @Source("../images/upload_normal.png")
        ImageResource uploadNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SnapshotTabPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/export/SnapshotTabPanel.css";

        String mainTabPanel();

        String unselectable();

        String imagePanel();

        String image();

        String buttonsContainer();

        String downloadPNG();

        String infoLabel();

        String genomespace();

        String disclaimer();
    }
}

package org.reactome.web.diagram.util.interactors;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.util.MapSet;

import java.util.Set;

/**
 * This class is used to export the interactors to a file
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InteractorsExporter {

    final static String DOWNLOAD_URL = DiagramFactory.SERVER + "/ContentService/download/";
    final static String SEPARATOR = ",";
    final static String LINE_BREAK = "#NL#";

    /***
     * This method takes as input the name of the file to be saved and
     * a MapSet containing:  DiagramAccession -> Set of RawInteractors
     *
     * If Blob.js and FileSaver.js are present then this class utilises them to
     * store the file. Otherwise, when the widget runs as standalone, the class
     * calls a method in the Content Service to generate the file.
     *
     * @param filename the name of the file to be saved
     * @param interactors the interactors to be exported
     */
    public static void exportInteractors(String filename, MapSet<String, RawInteractor> interactors) {
        if(isFileSaveScriptAvailable()) {
            alertDownload(filename, getFileContent(interactors, SEPARATOR, "\r\n"));
        } else {
            FormPanel form = new FormPanel();
            form.setMethod(FormPanel.METHOD_POST);
            form.setEncoding("text/plain;");
            form.setAction(DOWNLOAD_URL + URL.encode(filename.replaceAll("  *", "_").replaceAll("\\/","-")));

            TextBox tb = new TextBox();
            tb.setName("content");
            tb.setText(getFileContent(interactors, SEPARATOR, LINE_BREAK));
            form.add(tb);
            form.submit();
        }
    }

    public static native boolean isFileSaveScriptAvailable() /*-{
        return !!$wnd.saveAs;
    }-*/;

    /**
     * Downloads a file with the interactors
     *
     * @param text from preview
     */
    public static native void alertDownload(String filename, String text) /*-{
        $wnd.saveAs(
            new Blob(
                [text]
                , {type: "text/plain;charset=ISO-8859-8;"}
            )
            , [filename]
        );
    }-*/;

    private static String getFileContent(MapSet<String, RawInteractor> interactors, String separator, String lineBreak) {
        StringBuilder builder = new StringBuilder();
        // Add Header
        builder.append("ID A").append(separator)
                .append("ID B").append(separator)
                .append("SCORE").append(separator)
                .append("#EVIDENCES").append(lineBreak);
        for (String diagramAcc : interactors.keySet()) {
            Set<RawInteractor> rawInteractors = interactors.getElements(diagramAcc);
            if(rawInteractors != null) {
                for (RawInteractor rawInteractor : rawInteractors) {
                    builder.append(diagramAcc).append(separator)
                            .append(rawInteractor.getAcc()).append(separator)
                            .append(rawInteractor.getScore()).append(separator)
                            .append(rawInteractor.getEvidences()!=null ? rawInteractor.getEvidences() : "N/A").append(lineBreak);
                }
            }
        }
        return builder.toString();
    }
}

package org.reactome.web.diagram.client;

/**
 * Provides a method to instantiate a Diagram Viewer
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class DiagramFactory {

    public static boolean CONSOLE_VERBOSE = false;
    public static boolean EVENT_BUS_VERBOSE = false;

    public static String SERVER = "";
    public static String ILLUSTRATION_SERVER = SERVER;  //This can be set separately (e.g. reactomedev case)

    public static boolean WATERMARK = true;
    public static boolean PHARMGKB = true;
    public static String WATERMARK_BASE_URL = "https://reactome.org/PathwayBrowser/";
    public static String PHARMGKB_BASE_URL = "https://www.pharmgkb.org/";
    public static String PHARMGKB_RESOURCE = "PharmGKB";

    public static boolean RESPOND_TO_SEARCH_SHORTCUT = true; // Listen to ctrl (or cmd) + F and expand the search input

    //It has a value by default but it can be set to a different one so in every load
    //the "user preferred" interactors resource will be selected
    public static String INTERACTORS_INITIAL_RESOURCE = "static"; // -> null here means DO NOT LOAD interactors
    public static String INTERACTORS_INITIAL_RESOURCE_NAME = "IntAct"; // --> it should be null if the one above is null
    public static double INTERACTORS_RESOURCE_INITIAL_SCORE = 0.45;
    public static String DISEASE_RESOURCE = "disgenet";
    public static String DISEASE_RESOURCE_NAME = "DisGeNet";
    public static double DISEASE_RESOURCE_INITIAL_SCORE = 0.33;

    //The Reactome use case does not need to be sensible to SCROLL
    //This variable is meant to set up by DiagramJs or other resources using the GWT widget
    public static int SCROLL_SENSITIVITY = 0;
    
    private static DiagramViewerCreator creator = DiagramViewerImpl::new;
    
    public static void setDiagramViewerCreator(DiagramViewerCreator creator1) {
        creator = creator1;
    }

    // To be set to true in Widgets
    public static boolean WIDGET_JS = false;

    //Added for testing
    public static DiagramViewer createDiagramViewer() {
        return creator.createDiagramView();
    }
}

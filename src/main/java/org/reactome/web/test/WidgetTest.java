package org.reactome.web.test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.client.DiagramViewer;
import org.reactome.web.diagram.util.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetTest implements EntryPoint {

    private final DiagramViewer diagram;
    //    private static String currentPathway = "R-HSA-8878159";//R-HSA-109582";
    //    private static String currentPathway = "R-HSA-5693567"; //Big one with plenty of overlap
    //    private static String currentPathway = "R-HSA-9006115"; //Complex interactors
    private static String currentPathway = "R-HSA-9725554"; //Cell
    private static String currentAnalysis = "MjAyMzA2MTIxMzE4NDRfNA%3D%3D"; // Cell expression

    private TextBox pathwayTB;
    private TextBox analysisTokenTB;

    private static final ResultFilter filterTotal = new ResultFilter("TOTAL", null, true, null, null, null);
    private static final ResultFilter filter = new ResultFilter("TOTAL", null, true, 1, 10, null);


    public WidgetTest() {
//        DiagramFactory.SERVER = "fakeserver.com";
        DiagramFactory.CONSOLE_VERBOSE = true;
        DiagramFactory.EVENT_BUS_VERBOSE = true;
//      DiagramFactory.SHOW_INFO = true;
        diagram = DiagramFactory.createDiagramViewer();
    }

    @Override
    public void onModuleLoad() {
        Scheduler.get().scheduleDeferred(() -> {
            initialise();                 // For normal testing
//                initialiseInScrollablePage();   // For testing in a long page
            Console.info("");
            Scheduler.get().scheduleDeferred(() -> {
                diagram.loadDiagram(currentPathway);
                if (pathwayTB != null) {
                    pathwayTB.setValue(currentPathway);
                }
            });
            diagram.addDiagramLoadedHandler(event -> {
                diagram.selectItem("8951430");
//                        diagram.flagItems("R-HSA-179837", true);
            });
        });
    }

    public void initialise() {
        SplitLayoutPanel slp = new SplitLayoutPanel(10);
        slp.addEast(getDemoLeftPanel(), 83);
        slp.addNorth(getDemoTopPanel(), 25);
//        slp.addNorth(getTourPanel(), 25);
//        slp.addNorth(getDiseasePanel(), 50);
        slp.add(diagram);
        RootLayoutPanel.get().add(slp);
    }

    private void initialiseInScrollablePage() {
        SimpleLayoutPanel diagramContainer = new SimpleLayoutPanel();
        diagramContainer.getElement().getStyle().setHeight(500, Style.Unit.PX);
        diagramContainer.getElement().getStyle().setWidth(700, Style.Unit.PX);
        diagramContainer.getElement().getStyle().setBackgroundColor("white");
        diagramContainer.add(diagram);

        final Element element = Document.get().getElementById("container");
        HTMLPanel container = HTMLPanel.wrap(element);
        container.add(diagramContainer);
    }

    Button getSelectionButton(final String stId, String title) {
        Button button = new Button(stId, (ClickHandler) event -> diagram.selectItem(stId));
        button.addMouseOverHandler(event -> diagram.highlightItem(stId));
        button.addMouseOutHandler(event -> diagram.resetHighlight());
        button.setTitle(title);
        return button;
    }

    private Widget getDemoLeftPanel() {
        FlowPanel fp = new FlowPanel();

        fp.add(new Label("R-HSA-1181150"));
        fp.add(new Label("Reactions"));
        fp.add(getSelectionButton("R-HSA-1181152", "Cleavage of NODAL proprotein"));
        fp.add(getSelectionButton("R-HSA-1535903", "Phospho R-SMAD(SMAD2/3):CO-SMAD(SMAD4):FOXO3 binds FoxO3a-binding elements"));

        fp.add(new Label("--"));
        fp.add(new Label("Sets"));
        fp.add(getSelectionButton("R-HSA-171172", "SMAD2/3 [cytosol]"));
        fp.add(getSelectionButton("R-HSA-171182", "p-2S-SMAD2/3 [cytosol]"));

        fp.add(new Label("--"));
        fp.add(new Label("Complexes"));
        fp.add(getSelectionButton("R-HSA-1225883", "NODAL:p-ACVR1B:ACVR2:EGF-CFC [plasma membrane]"));
        fp.add(getSelectionButton("R-HSA-173511", "p-2S-SMAD2/3:SMAD4 [nucleoplasm]"));

        fp.add(new Label("--"));
        fp.add(new Label("Protein"));
        fp.add(getSelectionButton("R-HSA-1181114", "NODAL(27-347) [extracellular region]"));
        fp.add(getSelectionButton("R-HSA-1225914", "FOXH1 [nucleoplasm]"));
        fp.add(getSelectionButton("R-HSA-1181326", "LEFTY1"));

        fp.add(new Label("--"));
        fp.add(new Label("Chemical"));
        fp.add(getSelectionButton("R-ALL-113592", "ATP [cytosol]"));
        fp.add(getSelectionButton("R-ALL-29370", "ADP [cytosol]"));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("ORA"));
        fp.add(new Button("ORA 1", (ClickHandler) event -> {
//                No interactors: MjAxNzAxMzEwNTEyMDJfMg==
//                Interactors: MjAxNzAyMDcwOTMwMDVfMw==
            diagram.setAnalysisToken("MjAxOTAzMjcxMDMxNTZfOA%253D%253D", filterTotal);
        }));
        fp.add(new Button("ORA 2", (ClickHandler) event -> {
//                No interactors: MjAxNzAxMzEwNTEyMDJfMg==
//                Interactors: MjAxNzAyMDcwOTMwMDVfMw==
            diagram.setAnalysisToken("MjAxOTAzMjcxMDMxNTZfOA%253D%253D", filter);
        }));


        fp.add(new Label(""));
        fp.add(new Label(""));
        fp.add(new Label("--"));
        fp.add(new Label("Expression"));
        fp.add(new Button("Exp 1", (ClickHandler) event -> diagram.setAnalysisToken("MjAxOTA0MDExMjUxMzhfMTI%253D", filterTotal)));

        fp.add(new Button("Exp 2", (ClickHandler) event -> diagram.setAnalysisToken("MjAxOTA0MDExMjUxMzhfMTI%253D", filter)));

        fp.add(getSelectionButton("R-HSA-111465", ""));

        return fp;
    }

    Button getLoadButton(final String stId, String title) {
        Button button;
        button = new Button(stId, (ClickHandler) event -> {
            currentPathway = stId;
            diagram.loadDiagram(currentPathway);
            pathwayTB.setValue(currentPathway);
        });
        button.setTitle(title);
        return button;
    }

    private Widget getDemoTopPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(getPathwaySelectionPanel());
        fp.add(getAnalysisSelectionPanel());
        fp.add(getLoadButton("R-HSA-1181150", ""));
        fp.add(getLoadButton("R-HSA-2990846", ""));
        fp.add(getLoadButton("R-HSA-163841", ""));
        fp.add(getLoadButton("R-HSA-75153", ""));
        fp.add(getLoadButton("R-HSA-71291", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-162909", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-170834", ""));
        fp.add(getLoadButton("R-HSA-3642279", ""));
        fp.add(getLoadButton("R-HSA-3645790", ""));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-109581", "Apoptosis"));
        fp.add(getLoadButton("R-HSA-109582", "Hemostasis"));
        fp.add(new InlineLabel(" "));
        fp.add(getLoadButton("R-HSA-5637815", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("R-HSA-2219530", "Constitutive Signaling by Aberrant PI3K in Cancer"));
//        fp.add(getLoadButton("R-HSA-5362768", ""));
        fp.add(getLoadButton("R-HSA-5579009", ""));
        fp.add(getLoadButton("R-HSA-975871", ""));
        fp.add(getLoadButton("R-HSA-5210891", ""));
        fp.add(getLoadButton("R-HSA-5467345", ""));
        fp.add(getLoadButton("R-HSA-166658", ""));
        fp.add(getLoadButton("R-HSA-391160", ""));
        fp.add(getLoadButton("R-HSA-5693567", ""));
        fp.add(getLoadButton("R-HSA-5205647", ""));
        fp.add(getLoadButton("R-PFA-75153", ""));

        return fp;
    }

    private Widget getPathwaySelectionPanel() {
        FlowPanel fp = new FlowPanel();
        pathwayTB = new TextBox();
        fp.add(new InlineLabel("Enter a pathway: "));
        fp.add(pathwayTB);
        fp.add(new Button("GO", (ClickHandler) event -> {
            currentPathway = pathwayTB.getValue();
            diagram.loadDiagram(currentPathway);
        }));
        return fp;
    }

    private Widget getAnalysisSelectionPanel() {
        FlowPanel fp = new FlowPanel();
        analysisTokenTB = new TextBox();
        analysisTokenTB.setValue(currentAnalysis);
        analysisTokenTB.setWidth("200px");
        fp.add(new InlineLabel(" Overlay analysis token: "));
        fp.add(analysisTokenTB);
        fp.add(new Button("GO", (ClickHandler) event -> {
            currentAnalysis = analysisTokenTB.getValue();
            diagram.setAnalysisToken(currentAnalysis, filterTotal);
        }));
        return fp;
    }

    private Widget getTestPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("R-HSA-1181150", (ClickHandler) event -> diagram.loadDiagram("R-HSA-1181150")));
        fp.add(new Button("Apoptosis", (ClickHandler) event -> diagram.loadDiagram("R-HSA-109581")));
        fp.add(new Button("R-HSA-71291", (ClickHandler) event -> diagram.loadDiagram("R-HSA-71291")));
        fp.add(new Button("Raf/Map", (ClickHandler) event -> diagram.loadDiagram("R-HSA-5673001")));
        fp.add(new Button("R-HSA-5637815", (ClickHandler) event -> diagram.loadDiagram("R-HSA-5637815")));
        fp.add(new Button("R-HSA-2219530", (ClickHandler) event -> diagram.loadDiagram("R-HSA-2219530")));
        fp.add(new Button("R-HSA-1650814", (ClickHandler) event -> diagram.loadDiagram("R-HSA-1650814")));
        fp.add(new Button("R-HSA-170834", (ClickHandler) event -> diagram.loadDiagram("R-HSA-170834")));
        fp.add(new Button("R-HSA-400253", (ClickHandler) event -> diagram.loadDiagram("R-HSA-400253")));
        fp.add(new Button("R-HSA-157579", (ClickHandler) event -> diagram.loadDiagram("R-HSA-157579")));
        fp.add(new Button("R-HSA-1474244", (ClickHandler) event -> diagram.loadDiagram("R-HSA-1474244")));
        fp.add(new InlineLabel("      "));
        fp.add(new Button("R-HSA-162909", (ClickHandler) event -> diagram.loadDiagram("R-HSA-162909")));
        fp.add(new Button("R-HSA-5603041", (ClickHandler) event -> diagram.loadDiagram("R-HSA-5603041")));
        fp.add(new Button("R-HSA-3642279", (ClickHandler) event -> diagram.loadDiagram("R-HSA-3642279")));
        fp.add(new Button("R-HSA-3645790", (ClickHandler) event -> diagram.loadDiagram("R-HSA-3645790")));
        fp.add(new Button("R-HSA-73885", (ClickHandler) event -> diagram.loadDiagram("R-HSA-73885")));
        fp.add(new Button("R-HSA-1169408", (ClickHandler) event -> diagram.loadDiagram("R-HSA-1169408")));
        return fp;
    }


    private Widget getDiseasePanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(getLoadButton("R-HSA-2219530", "Constitutive Signaling by Aberrant PI3K in Cancer"));
        fp.add(getLoadButton("R-HSA-2206296", ""));
        fp.add(getLoadButton("R-HSA-2206292", ""));
        fp.add(getLoadButton("R-HSA-2206308", ""));
        fp.add(getLoadButton("R-HSA-2206302", ""));
        fp.add(getLoadButton("R-HSA-2206291", ""));
        fp.add(getLoadButton("R-HSA-2474795", ""));
        fp.add(getLoadButton("R-HSA-2644602", ""));
        fp.add(getLoadButton("R-HSA-3311021", ""));
        fp.add(getLoadButton("R-HSA-3315487", ""));
        fp.add(getLoadButton("R-HSA-3371599", ""));
        fp.add(getLoadButton("R-HSA-5362768", ""));
        fp.add(getLoadButton("R-HSA-5340573", ""));
        fp.add(getLoadButton("R-HSA-5467340", ""));
        fp.add(getLoadButton("R-HSA-5545619", ""));
        fp.add(getLoadButton("R-HSA-3785653", ""));
        fp.add(getLoadButton("R-HSA-3656237", ""));
        fp.add(getLoadButton("R-HSA-4724289", ""));
        fp.add(getLoadButton("R-HSA-4793954", ""));
        fp.add(getLoadButton("R-HSA-5637815", "Signaling by Ligand-Responsive EGFR Variants in Cancer"));
        fp.add(getLoadButton("R-HSA-3595177", ""));
        fp.add(getLoadButton("R-HSA-4551295", ""));
        fp.add(getLoadButton("R-HSA-5578996", ""));
        fp.add(getLoadButton("R-HSA-4570571", ""));
        fp.add(getLoadButton("R-HSA-4687000", ""));
        fp.add(getLoadButton("R-HSA-5633231", ""));
        fp.add(getLoadButton("R-HSA-5579009", ""));
        fp.add(getLoadButton("R-HSA-4793950", ""));
        fp.add(getLoadButton("R-HSA-4549356", ""));
        fp.add(getLoadButton("R-HSA-5657560", ""));
        fp.add(getLoadButton("R-HSA-5655302", ""));
        fp.add(getLoadButton("R-HSA-5603027", ""));
        fp.add(getLoadButton("R-HSA-5602498", ""));
        fp.add(getLoadButton("R-HSA-5603029", ""));
        fp.add(getLoadButton("R-HSA-5602566", ""));
        fp.add(getLoadButton("R-HSA-5655253", ""));
        fp.add(getLoadButton("R-HSA-5602410", ""));
        fp.add(getLoadButton("R-HSA-162909", ""));
        fp.add(getLoadButton("R-HSA-977225", ""));

        return fp;
    }

    private Widget getTourPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(new Button("R_111057", (ClickHandler) event -> R_HSA_1181150_tour()));
        fp.add(new Button("R_13", (ClickHandler) event -> R_HSA_71921_tour()));
        fp.add(new Button("A Test 1", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA1MjgwNTQyNTNfODgz", filterTotal)));
        fp.add(new Button("A Test 2", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA1MjgwODM1NTRfOTE3", filterTotal)));
        fp.add(new Button("A Test 3", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MDEwOTU4MzdfNTQ0", filterTotal)));
        fp.add(new Button("A Test 4", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MDUwMzM5MzhfOA==", filterTotal)));
        fp.add(new Button("A Test 5", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MDgxMzUxNTZfMzQ2", filterTotal)));
        fp.add(new Button("A Test 6", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MDgxNDA2MjNfMzQ4", filterTotal)));
        fp.add(new Button("A Test 7", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MTExMjUyNTBfMTA5OQ==", filterTotal)));
        fp.add(new Button("A Test 8", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MTExMzM3MDFfMTEzMw==", filterTotal)));
        fp.add(new Button("A Test 9", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MTExNDA2MDVfMTE0Mg==", filterTotal)));
        fp.add(new Button("Exp 1", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNjAxMDQwOTM5NDBfMg==", filterTotal)));
        fp.add(new Button("Exp 2", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MDUwNzI1NTZfMzI=", filterTotal)));
        fp.add(new Button("Exp 3", (ClickHandler) event -> diagram.setAnalysisToken("MjAxNTA2MTAxNDQ4MTJfNzk4", filterTotal)));
        return fp;
    }

    private void schedule(Runnable runnable, int millis) {
        (new Timer() {
            @Override
            public void run() {
                runnable.run();
            }
        }).schedule(millis);
    }

    private void R_HSA_1181150_tour() {
        diagram.selectItem(1181156L);
        schedule(() -> diagram.selectItem(1181355L), 4000);
        schedule(() -> diagram.selectItem(1225914L), 8000);
        schedule(() -> diagram.selectItem(1181156L), 12000);
        schedule(() -> diagram.selectItem(173511L), 16000);
        schedule(() -> diagram.selectItem(171182L), 20000);
    }

    private void R_HSA_71921_tour() {
        diagram.selectItem(209772L);
        schedule(() -> diagram.selectItem(174391L), 4000);
        schedule(() -> diagram.selectItem(372480L), 8000);
        schedule(() -> diagram.selectItem(209772L), 12000);
        schedule(() -> diagram.selectItem(353555L), 16000);
    }
}

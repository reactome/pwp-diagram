package org.reactome.web.diagram.legends;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphEntitySet;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.DynamicLink;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.util.ColorMap;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.ExpressionUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class RegulationLegend extends LegendPanel implements ClickHandler,
        MouseOverHandler, MouseOutHandler, MouseMoveHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler, ContentRequestedHandler,
        ExpressionValueHoveredHandler, AnalysisProfileChangedHandler, ExpressionColumnChangedHandler,
        GraphObjectSelectedHandler, GraphObjectHoveredHandler, InteractorHoveredHandler {

    private static String TOP_LABEL  = "Up-regulated";
    private static String BOTTOM_LABEL = "Down-regulated";

    private static String[] LABELS = {  "Significantly up regulated",
                                        "Non significantly up regulated",
                                        "Not found",
                                        "Non significantly down regulated",
                                        "Significantly down regulated"};
    private static String[] SYMBOLS = { "\u25BC\u25BC",
                                        "\u25BC",
                                        "-",
                                        "\u25B2",
                                        "\u25B2\u25B2"};

    private Canvas gradient;
    private Canvas flag;

    private InlineLabel topLabel;
    private InlineLabel bottomLabel;

    private Double expHovered;
    private GraphObject hovered;
    private GraphObject selected;
    private InteractorEntity interactor;

    private int column;
    private double min;
    private double max;

    private int hoveredScaleIndex;

    private Content.Type type; //This is temporal

    public RegulationLegend(EventBus eventBus) {
        super(eventBus);
        this.gradient = createCanvas(30, 200);
        this.flag = createCanvas(50, 210);
        this.flag.addMouseMoveHandler(this);
        this.flag.setTitle(LABELS[hoveredScaleIndex]);

        //Setting the legend style
        addStyleName(RESOURCES.getCSS().expressionLegend());

        fillPalette();

        this.topLabel = new InlineLabel("");
        this.topLabel.setStyleName(RESOURCES.getCSS().regulationLabel());
        this.add(this.topLabel, 5, 3);

        this.add(this.gradient, 10, 25);
        this.add(this.flag, 0, 20);

        this.bottomLabel = new InlineLabel("");
        this.bottomLabel.setStyleName(RESOURCES.getCSS().regulationLabel());
        this.add(this.bottomLabel, 5, 230);

        this.addHelp();

        initHandlers();

        this.setVisible(false);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()) {
            case GSA_REGULATION:
                ExpressionSummary es = event.getExpressionSummary();
                if (es != null) {
                    this.min = es.getMin();
                    this.max = es.getMax();
                    this.topLabel.setText(TOP_LABEL);
                    this.bottomLabel.setText(BOTTOM_LABEL);
                }
                setVisible(true);
                break;
            default:
                setVisible(false);
        }

    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        Scheduler.get().scheduleDeferred(() -> {
            fillPalette();
            draw();
        });
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        List<DiagramObject> hoveredObjects = event.getHoveredObjects();
        DiagramObject item = hoveredObjects != null && !hoveredObjects.isEmpty() ? hoveredObjects.get(0) : null;
        this.hovered = item != null ? item.getGraphObject() : null;
        draw();
    }

    @Override
    public void onInteractorHovered(InteractorHoveredEvent event) {
        expHovered = null;
        hovered = null;
        interactor = null;
        if (event.getInteractor() != null) {
            DiagramInteractor diagramInteractor = event.getInteractor();
            if (diagramInteractor instanceof InteractorEntity) {
                interactor = (InteractorEntity) diagramInteractor;
            } else if (diagramInteractor instanceof DynamicLink) {
                DynamicLink link = (DynamicLink) diagramInteractor;
                interactor = link.getInteractorEntity();
            } else {
                hovered = ((InteractorLink) diagramInteractor).getNodeFrom().getGraphObject();
                if (hovered.getExpression() != null) {
                    expHovered = hovered.getExpression(column);
                }
            }
            if (interactor != null && interactor.getExp() != null) {
                expHovered = interactor.getExp().get(this.column);
            }
        }
        draw();
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        this.selected = event.getGraphObject();
        draw();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.expHovered = null;
        this.hovered = null;
        this.selected = null;
        draw();
        setVisible(false);
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        this.column = e.getColumn();
        if (interactor != null && interactor.getExp() != null) {
            expHovered = interactor.getExp().get(this.column);
        } else if (hovered != null && hovered.getExpression() != null) {
            expHovered = hovered.getExpression(column);
        } else {
            expHovered = null;
        }
        draw();
    }

    @Override
    public void onExpressionValueHovered(ExpressionValueHoveredEvent event) {
        this.expHovered = event.getExpressionValue();
        draw();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.draw();
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);

        //Set text properties once
        Context2d ctx = canvas.getContext2d();
        ctx.setFont("bold 13px Arial");
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setTextAlign(Context2d.TextAlign.CENTER);

        return canvas;
    }

    private void draw() {
        if (!this.isVisible()) return;

        try {
            Context2d ctx = this.flag.getContext2d();
            ctx.clearRect(0, 0, this.flag.getOffsetWidth(), this.flag.getOffsetHeight());

            List<Double> hoveredValues = getExpressionValues(this.hovered, this.column);
            if (!hoveredValues.isEmpty()) {
                String colour = DiagramColours.get().PROFILE.getProperties().getHovering();
                for (Double value : hoveredValues) {
                    double p = ColorMap.getPercentage(value, this.min, this.max);
                    drawLeftPin(ctx, p, colour, colour);
                }
                if (hoveredValues.size() > 1) {
                    double median = ExpressionUtil.median(hoveredValues);
                    double p = ColorMap.getPercentage(median, this.min, this.max);
                    colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getMedian();
                    drawLeftPin(ctx, p, colour, colour);
                }
            }

            if (this.expHovered != null) {
                double p = ColorMap.getPercentage(this.expHovered, this.min, this.max);
                String colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getHover();
                drawLeftPin(ctx, p, colour, colour);
            }

            List<Double> selectedValues = getExpressionValues(this.selected, this.column);
            if (!selectedValues.isEmpty()) {
                String colour = DiagramColours.get().PROFILE.getProperties().getSelection();
                for (Double value : selectedValues) {
                    double p = ColorMap.getPercentage(value, this.min, this.max);
                    drawRightPin(ctx, p, colour, colour);
                }
                if (selectedValues.size() > 1) {
                    double median = ExpressionUtil.median(selectedValues);
                    double p = ColorMap.getPercentage(median, this.min, this.max);
                    colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getMedian();
                    drawRightPin(ctx, p, colour, colour);
                }
            }
        } catch (Exception e) {
            Console.error(e.getMessage(), this);
        }
    }

    private void drawLeftPin(Context2d ctx, double p, String stroke, String fill) {
        int y = (int) Math.round(200 * p) + 5;
        ctx.setFillStyle(fill);
        ctx.setStrokeStyle(stroke);
        ctx.beginPath();
        ctx.moveTo(5, y - 5);
        ctx.lineTo(10, y);
        ctx.lineTo(5, y + 5);
        ctx.lineTo(5, y - 5);
        ctx.fill();
        ctx.stroke();
        ctx.closePath();

//        ctx.beginPath();
//        ctx.moveTo(10, y);
//        ctx.lineTo(40, y);
//        ctx.stroke();
//        ctx.closePath();
    }

    private void drawRightPin(Context2d ctx, double p, String stroke, String fill) {
        int y = (int) Math.round(200 * p) + 5;
        ctx.setFillStyle(fill);
        ctx.setStrokeStyle(stroke);
        ctx.beginPath();
        ctx.moveTo(45, y - 5);
        ctx.lineTo(40, y);
        ctx.lineTo(45, y + 5);
        ctx.lineTo(45, y - 5);
        ctx.fill();
        ctx.stroke();
        ctx.closePath();

//        ctx.beginPath();
//        ctx.moveTo(10, y);
//        ctx.lineTo(40, y);
//        ctx.stroke();
//        ctx.closePath();
    }

    private void fillPalette() {
        Map<Integer,String> colorMap = AnalysisColours.get().regulationColorMap.getPalette();

        Context2d ctx = this.gradient.getContext2d();
        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        double height = this.gradient.getCoordinateSpaceHeight() / (double) colorMap.size();

        int i = 0;
        for (Integer key : colorMap.keySet()) {
            ctx.setFillStyle(colorMap.get(key));
            ctx.beginPath();
            ctx.fillRect(0, i * height, 30, height);
            ctx.closePath();

            // Labels
            ctx.setShadowColor("rgba(0,0,0,0.5)");
            ctx.setShadowBlur(4);
            ctx.setFillStyle("#FFFFFF");
            ctx.fillText(SYMBOLS[key + 2], 15, i * height + height/2.0);

            i++;
        }
    }

    private List<Double> getExpressionValues(GraphObject graphObject, int column) {
        List<Double> expression = new LinkedList<>();

        //This is temporal: in order to avoid drwing the pins for SVG diagrams (below the pValue threshold)
        if (graphObject instanceof GraphPathway && type.equals(Content.Type.SVG)){
            EntityStatistics es = ((GraphPathway) graphObject).getStatistics();
            if (es != null && es.getpValue() > AnalysisColours.THRESHOLD) return expression; //which is empty
        }

        if (graphObject != null) {
            if (graphObject instanceof GraphComplex) {
                GraphComplex complex = (GraphComplex) graphObject;
                expression = new LinkedList<>(complex.getParticipantsExpression(column).values());
            } else if (graphObject instanceof GraphEntitySet) {
                GraphEntitySet set = (GraphEntitySet) graphObject;
                expression = new LinkedList<>(set.getParticipantsExpression(column).values());
            } else {
                List<Double> aux = graphObject.getExpression();
                if (aux != null && !aux.isEmpty()) {
                    expression.add(aux.get(column));
                }
            }
        }
        Collections.sort(expression);       //Collections.sort(expression, Collections.reverseOrder());
        return expression;
    }


    private void initHandlers() {
        eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
        eventBus.addHandler(InteractorHoveredEvent.TYPE, this);
        eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        eventBus.addHandler(ExpressionValueHoveredEvent.TYPE, this);
        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);

        //This is temporal
        eventBus.addHandler(ContentLoadedEvent.TYPE, event -> type = event.getContext().getContent().getType());
    }

    /*########### HELP INFO ##############*/

    private FlowPanel helpPanel;

    private void addHelp() {
        LegendPanelCSS css = RESOURCES.getCSS();
        PwpButton helpBtn = new PwpButton("Show help", css.help(), this);
        helpBtn.addMouseOverHandler(this);
        helpBtn.addMouseOutHandler(this);
        this.add(helpBtn, 12, 248);

        this.helpPanel = new FlowPanel();
        this.helpPanel.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        HTMLPanel text = new HTMLPanel(RESOURCES.regulationLegendHelp().getText());
        this.helpPanel.add(text);

        Anchor close = new Anchor("Got it!");
        close.addClickHandler(event -> {
            hideHelp();
            hideHelpInTheFuture();
        });
        this.helpPanel.add(close);

        String showLegend = Cookies.getCookie("pwp-diagram-expression-legend");
        if (showLegend == null || showLegend.isEmpty() || showLegend.toLowerCase().equals("true")) {
            this.showHelp();
        } else {
            this.hideHelp();
        }
        this.add(this.helpPanel);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (this.helpPanel.getOffsetWidth() > 0) {
            this.hideHelp();
            hideHelpInTheFuture();
        } else {
            this.showHelp();
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        this.showHelp();
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        this.hideHelp();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int y = event.getRelativeY(gradient.getElement());
        int stepHeight = 40;
        int scaleIndex = (y / stepHeight);
        if (hoveredScaleIndex != scaleIndex) {
            flag.setTitle(LABELS[scaleIndex]);
            hoveredScaleIndex = scaleIndex;
        }
    }

    private void showHelp() {
        LegendPanelCSS css = RESOURCES.getCSS();
        this.helpPanel.setStyleName(css.expressionLegendHelp());
        this.helpPanel.addStyleName(css.expressionLegendHelpVisible());
    }

    private void hideHelp() {
        LegendPanelCSS css = RESOURCES.getCSS();
        this.helpPanel.setStyleName(css.expressionLegendHelp());
        this.helpPanel.addStyleName(css.expressionLegendHelpHidden());
    }

    private void hideHelpInTheFuture() {
        Cookies.setCookie("pwp-diagram-expression-legend", "false");
    }
}

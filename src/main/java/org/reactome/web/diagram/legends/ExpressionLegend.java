package org.reactome.web.diagram.legends;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.DynamicLink;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.ExpressionUtil;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class ExpressionLegend extends LegendPanel implements ClickHandler, MouseOverHandler, MouseOutHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler, ContentRequestedHandler,
        ExpressionValueHoveredHandler, AnalysisProfileChangedHandler, ExpressionColumnChangedHandler,
        GraphObjectSelectedHandler, GraphObjectHoveredHandler, InteractorHoveredHandler {

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

    private Content.Type type; //This is temporal

    public ExpressionLegend(EventBus eventBus) {
        super(eventBus);
        this.gradient = createCanvas(30, 200);
        this.flag = createCanvas(50, 210);

        //Setting the legend style
        addStyleName(RESOURCES.getCSS().expressionLegend());

        fillGradient();

        this.topLabel = new InlineLabel("");
        this.topLabel.setSize("40px", "15px");
        this.topLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.add(this.topLabel, 5, 5);

        this.add(this.gradient, 10, 25);
        this.add(this.flag, 0, 20);

        this.bottomLabel = new InlineLabel("");
        this.bottomLabel.setSize("40px", "15px");
        this.bottomLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        this.add(this.bottomLabel, 5, 230);

        this.addHelp();

        initHandlers();

        this.setVisible(false);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()) {
            case EXPRESSION:
            case GSVA:
            case GSA_STATISTICS:
                ExpressionSummary es = event.getExpressionSummary();
                if (es != null) {
                    this.min = es.getMin();
                    this.max = es.getMax();
                    this.topLabel.setText(NumberFormat.getFormat("#.##E0").format(max));
                    this.bottomLabel.setText(NumberFormat.getFormat("#.##E0").format(min));
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
            fillGradient();
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
                    double p = ThreeColorGradient.getPercentage(value, this.min, this.max);
                    drawLeftPin(ctx, p, colour, colour);
                }
                if (hoveredValues.size() > 1) {
                    double median = ExpressionUtil.median(hoveredValues);
                    double p = ThreeColorGradient.getPercentage(median, this.min, this.max);
                    colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getMedian();
                    drawLeftPin(ctx, p, colour, colour);
                }
            }

            if (this.expHovered != null) {
                double p = ThreeColorGradient.getPercentage(this.expHovered, this.min, this.max);
                String colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getHover();
                drawLeftPin(ctx, p, colour, colour);
            }

            List<Double> selectedValues = getExpressionValues(this.selected, this.column);
            if (!selectedValues.isEmpty()) {
                String colour = DiagramColours.get().PROFILE.getProperties().getSelection();
                for (Double value : selectedValues) {
                    double p = ThreeColorGradient.getPercentage(value, this.min, this.max);
                    drawRightPin(ctx, p, colour, colour);
                }
                if (selectedValues.size() > 1) {
                    double median = ExpressionUtil.median(selectedValues);
                    double p = ThreeColorGradient.getPercentage(median, this.min, this.max);
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

        ctx.beginPath();
        ctx.moveTo(10, y);
        ctx.lineTo(40, y);
        ctx.stroke();
        ctx.closePath();
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

        ctx.beginPath();
        ctx.moveTo(10, y);
        ctx.lineTo(40, y);
        ctx.stroke();
        ctx.closePath();
    }

    private void fillGradient() {
        AnalysisProfile profile = AnalysisColours.get().PROFILE;
        Context2d ctx = this.gradient.getContext2d();
        CanvasGradient grd = ctx.createLinearGradient(0, 0, 30, 200);

        ThreeColorGradient gradient = new ThreeColorGradient(profile.getExpression().getGradient());
        grd.addColorStop(0, gradient.getColor(0));
        grd.addColorStop(0.5, gradient.getColor(0.5));
        grd.addColorStop(1, gradient.getColor(1));

        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        ctx.setFillStyle(grd);
        ctx.beginPath();
        ctx.fillRect(0, 0, 30, 200);
        ctx.closePath();
    }

    private List<Double> getExpressionValues(GraphObject graphObject, int column) {
        List<Double> expression = new LinkedList<>();

        //This is temporal: in order to avoid drwing the pins for SVG diagrams (below the pValue threshold)
        if (graphObject instanceof GraphPathway && type.equals(Content.Type.SVG)) {
            EntityStatistics es = ((GraphPathway) graphObject).getStatistics();
            if (es != null && es.getpValue() > AnalysisColours.THRESHOLD) return expression; //which is empty
        }

        if (graphObject != null) {
            if (graphObject instanceof GraphCompositeEntity) {
                GraphCompositeEntity composite = (GraphCompositeEntity) graphObject;
                expression = new LinkedList<>(composite.getParticipantsExpression(column).values());
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
        HTMLPanel text = new HTMLPanel(RESOURCES.expressionLegendHelp().getText());
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

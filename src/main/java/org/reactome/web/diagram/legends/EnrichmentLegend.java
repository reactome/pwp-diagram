package org.reactome.web.diagram.legends;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;

import java.util.List;

import static org.reactome.web.diagram.data.content.Content.Type.SVG;
import static org.reactome.web.diagram.profiles.analysis.AnalysisColours.THRESHOLD;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class EnrichmentLegend extends LegendPanel implements AnalysisResultLoadedHandler,
        AnalysisProfileChangedHandler, AnalysisResultRequestedHandler, AnalysisResetHandler,
        ContentLoadedHandler, ContentRequestedHandler, GraphObjectHoveredHandler, GraphObjectSelectedHandler {

    private Context context;
    private Canvas gradient;
    private Canvas flag;

    private GraphObject hovered;
    private GraphObject selected;

    public EnrichmentLegend(EventBus eventBus) {
        super(eventBus);
        this.gradient = createCanvas(30, 200);
        this.fillGradient();

        this.flag = createCanvas(50, 210);

        //Setting the legend style
        addStyleName(RESOURCES.getCSS().enrichmentLegend());

        this.add(new InlineLabel("0"), 20, 5);
        this.add(this.gradient, 10, 25);
        this.add(this.flag, 0, 20);
        this.add(new InlineLabel("0.05"), 12, 230);

        initHandlers();

        this.setVisible(false);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()){
            case OVERREPRESENTATION:
            case SPECIES_COMPARISON:
                if(context.getContent().getType() == SVG) {
                    setVisible(true);
                }
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
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.context = null;
        this.hovered = null;
        this.selected = null;
        draw();
        setVisible(false);
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        List<DiagramObject> hoveredObjects = event.getHoveredObjects();
        DiagramObject item = hoveredObjects != null && !hoveredObjects.isEmpty() ? hoveredObjects.get(0) : null;
        this.hovered = item != null ? item.getGraphObject() : null;
        draw();
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        this.selected = event.getGraphObject();
        draw();
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        return canvas;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.draw();
    }

    private void fillGradient(){
        Context2d ctx = this.gradient.getContext2d();
        CanvasGradient grd = ctx.createLinearGradient(0, 0, 30, 200);
        grd.addColorStop(0, AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMin());
        grd.addColorStop(1, AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());

        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        ctx.setFillStyle(grd);
        ctx.beginPath();
        ctx.fillRect(0, 0, 30, 200);
        ctx.closePath();
    }

    private void draw(){
        if(!this.isVisible()) return;

        Context2d ctx = this.flag.getContext2d();
        ctx.clearRect(0, 0, this.flag.getOffsetWidth(), this.flag.getOffsetHeight());

        if(hovered instanceof GraphPathway) {
            EntityStatistics statistics = ((GraphPathway)hovered).getStatistics();
            if (statistics != null) {
                double pValue = statistics.getpValue();
                if (pValue <= THRESHOLD) {
                    String colour = DiagramColours.get().PROFILE.getProperties().getHovering();
                    int y = (int) Math.round(200 * pValue / THRESHOLD) + 5;
                    ctx.setFillStyle(colour);
                    ctx.setStrokeStyle(colour);
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
                    ctx.setStrokeStyle("yellow");
                    ctx.stroke();
                    ctx.closePath();
                }
            }

        }

        if(selected instanceof GraphPathway){
            EntityStatistics statistics = ((GraphPathway)selected).getStatistics();
            if(statistics!=null){
                double pValue = statistics.getpValue();
                if(pValue<= THRESHOLD) {
                    String colour = DiagramColours.get().PROFILE.getProperties().getSelection();
                    int y = (int) Math.round(200 * pValue / THRESHOLD) + 5;
                    ctx.setFillStyle(colour);
                    ctx.setStrokeStyle(colour);
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
            }
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
    }
}

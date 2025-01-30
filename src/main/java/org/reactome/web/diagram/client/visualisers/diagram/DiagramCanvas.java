package org.reactome.web.diagram.client.visualisers.diagram;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.client.OptionalWidget;
import org.reactome.web.diagram.context.popups.export.ExportDialog;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.DiagramStatus;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.events.ExpressionColumnChangedEvent;
import org.reactome.web.diagram.events.ExpressionValueHoveredEvent;
import org.reactome.web.diagram.events.RenderOtherDataEvent;
import org.reactome.web.diagram.handlers.ExpressionColumnChangedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfileProperties;
import org.reactome.web.diagram.profiles.interactors.InteractorColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.interactor.InteractorRenderer;
import org.reactome.web.diagram.renderers.interactor.InteractorRendererManager;
import org.reactome.web.diagram.renderers.layout.ConnectorRenderer;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.renderers.layout.abs.AttachmentAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.ProteinAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.SummaryItemAbstractRenderer;
import org.reactome.web.diagram.thumbnail.NullThumbnail;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.thumbnail.diagram.DiagramThumbnail;
import org.reactome.web.diagram.thumbnail.diagram.StaticIllustrationThumbnail;
import org.reactome.web.diagram.tooltips.TooltipContainer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.actions.MouseActionsHandlers;
import org.reactome.web.diagram.util.actions.UserActionsInstaller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is where the drawing of the classic diagrams takes place.
 * The class features several stacked canvases for teh different layers.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class DiagramCanvas extends AbsolutePanel implements ExpressionColumnChangedHandler, OptionalWidget.Handler {

    private final RendererManager rendererManager;
    private final InteractorRendererManager interactorRendererManager;
    private final EventBus eventBus;

    private AdvancedContext2d compartments;
    private AdvancedContext2d shadows;
    private AdvancedContext2d notes;
    private AdvancedContext2d links;

    private AdvancedContext2d fadeOut;

    private AdvancedContext2d flag;
    private AdvancedContext2d halo;

    private AdvancedContext2d reactionsHighlight;
    private AdvancedContext2d entitiesHighlight;

    private AdvancedContext2d reactions;
    private AdvancedContext2d reactionsSelection;
    private AdvancedContext2d reactionDecorators;

    private AdvancedContext2d entities;
    private AdvancedContext2d entitiesDecorators;
    private AdvancedContext2d text;
    private AdvancedContext2d overlay;
    private AdvancedContext2d entitiesSelection;
    private AdvancedContext2d shadowsText;

    private AdvancedContext2d interactorsHighlight;
    private AdvancedContext2d interactorsSelection;
    private AdvancedContext2d interactors;

    private AdvancedContext2d buffer;

    private TooltipContainer tooltipContainer;
    private final Thumbnail thumbnail;
    private final List<Canvas> canvases = new LinkedList<>();

    private int column = 0;
    private Double hoveredExpression = null;

    private final StaticIllustrationThumbnail staticIllustrationThumbnail;

    public DiagramCanvas(EventBus eventBus) {
        this.getElement().addClassName("pwp-DiagramCanvas");
        this.eventBus = eventBus;

        //This is MANDATORY
        RendererManager.initialise(eventBus);
        this.rendererManager = RendererManager.get();
        InteractorRendererManager.initialise(eventBus);
        this.interactorRendererManager = InteractorRendererManager.get();

        //This is MANDATORY
        DiagramColours.initialise(eventBus);
        AnalysisColours.initialise(eventBus);
        InteractorColours.initialise(eventBus);

        thumbnail = OptionalWidget.THUMBNAIL.isVisible() ? new DiagramThumbnail(eventBus) : new NullThumbnail();

        this.staticIllustrationThumbnail = new StaticIllustrationThumbnail(eventBus);

        this.initHandlers();
    }

    private void initHandlers() {
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
    }

    public void addUserActionsHandlers(MouseActionsHandlers handler) {
        if (this.canvases.isEmpty()) {
            throw new RuntimeException("Multilayer canvas has not been yet initialised.");
        }
        Canvas canvas = this.canvases.get(this.canvases.size() - 1);
        UserActionsInstaller.addUserActionsHandlers(canvas, handler);
    }

    public void flag(Collection<DiagramObject> items, Context context) {
        highlight(items, context, this.flag);
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public StaticIllustrationThumbnail getStaticIllustrationThumbnail() {
        return staticIllustrationThumbnail;
    }

    public void halo(Collection<DiagramObject> items, Context context) {
        highlight(items, context, this.halo);
    }

    private void highlight(Collection<DiagramObject> items, Context context, AdvancedContext2d ctx) {
        cleanCanvas(ctx);
        if (items == null || items.isEmpty()) return;

        DiagramStatus status = context.getDiagramStatus();
        double factor = status.getFactor();
        Coordinate offset = status.getOffset();
        for (DiagramObject item : items) {
            if (item.getIsFadeOut() != null) continue;
            Renderer renderer = this.rendererManager.getRenderer(item);
            if (renderer != null) {
                renderer.highlight(ctx, item, factor, offset);
            }
        }
    }

    public void highlight(HoveredItem hoveredItem, Context context) {
        cleanCanvas(this.entitiesHighlight);
        cleanCanvas(this.reactionsHighlight);
        if (hoveredItem == null) return;
        DiagramStatus status = context.getDiagramStatus();
        for (DiagramObject item : hoveredItem.getDiagramObjects()) {
            if (item.getIsFadeOut() != null) continue;
            Renderer renderer = rendererManager.getRenderer(item);
            if (renderer == null) continue;
            if (item instanceof Node) {
                renderer.highlight(entitiesHighlight, item, status.getFactor(), status.getOffset());
            } else if (item instanceof Edge) {
                renderer.highlight(reactionsHighlight, item, status.getFactor(), status.getOffset());
            }
        }
    }

    public void highlightInteractor(DiagramInteractor item, Context context) {
        cleanCanvas(this.interactorsHighlight);
        if (item == null) return;
        DiagramStatus status = context.getDiagramStatus();
        InteractorRenderer renderer = interactorRendererManager.getRenderer(item);
        if (renderer != null) renderer.highlight(interactorsHighlight, item, status.getFactor(), status.getOffset());
    }

    public void decorators(HoveredItem hoveredItem, Context context) {
        cleanCanvas(this.entitiesDecorators);
        if (hoveredItem == null) return;

        DiagramStatus status = context.getDiagramStatus();

        NodeAttachment attachment = hoveredItem.getAttachment();
        if (attachment != null) {
            ProteinAbstractRenderer proteinRenderer = (ProteinAbstractRenderer) rendererManager.getRenderer("Protein");
            if (proteinRenderer.nodeAttachmentsVisible()) {
                AttachmentAbstractRenderer.draw(entitiesDecorators, attachment, status.getFactor(), status.getOffset(), true);
            }
        }

        SummaryItem summaryItem = hoveredItem.getSummaryItem();
        if (summaryItem != null) {
            SummaryItemAbstractRenderer.draw(entitiesDecorators, summaryItem, status.getFactor(), status.getOffset(), true);
        }

        for (DiagramObject item : hoveredItem.getDiagramObjects()) {
            if (item.getIsFadeOut() != null) continue;
            if (!item.getId().equals(hoveredItem.getDiagramId())) continue;
            Renderer renderer = rendererManager.getRenderer(item);
            if (renderer == null) continue;
            this.entitiesDecorators.save();
            this.entitiesDecorators.setGlobalAlpha(1);
            if (hoveredItem.getContextMenuTrigger() == null) {
                this.entitiesDecorators.setFillStyle(DiagramColours.get().PROFILE.getProperties().getTrigger());
            } else {
                this.entitiesDecorators.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
            }
            renderer.focus(this.entitiesDecorators, item, status.getFactor(), status.getOffset());
            this.entitiesDecorators.restore();
        }
    }

    public void select(List<DiagramObject> items, Context context) {
        DiagramStatus status = context.getDiagramStatus();
        cleanCanvas(this.entitiesSelection);
        cleanCanvas(this.reactionsSelection);
        for (DiagramObject item : items) {
            if (item.getIsFadeOut() != null) continue;
            Renderer renderer = rendererManager.getRenderer(item);
            if (renderer == null) return;
            if (item instanceof Node) {
                renderer.highlight(this.entitiesSelection, item, status.getFactor(), status.getOffset());
            } else if (item instanceof Edge) {
                renderer.highlight(this.reactionsSelection, item, status.getFactor(), status.getOffset());
            }
        }
    }

    public void setCursor(Style.Cursor cursor) {
        this.buffer.getCanvas().getStyle().setCursor(cursor);
    }

    public void setSize(int width, int height) {
        this.setWidth(width + "px");
        this.setHeight(height + "px");

        for (Canvas canvas : canvases) {
            setCanvasProperties(canvas, width, height);
        }
        if (tooltipContainer != null) {
            tooltipContainer.setWidth(width);
            tooltipContainer.setHeight(height);
        }

        if (staticIllustrationThumbnail != null) {
            staticIllustrationThumbnail.resize(width);
        }
    }

    @Override
    public void clear() {
        for (Canvas canvas : canvases) {
            cleanCanvas(canvas.getContext2d());
        }
    }

    public void clearThumbnail() {
        this.thumbnail.contentRequested();
    }

    private void cleanCanvas(Context2d ctx) {
        ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
    }

    public void showExportDialog(final Context context, final GraphObject selected, final String flagTerm, final Boolean includeInteractors) {
        final Context2d ctx = this.canvases.get(this.canvases.size() - 1).getContext2d();
        //This is silly but gives some visual feedback of the picture taking :D
        (new Timer() {
            double alpha = 1.0;

            @Override
            public void run() {
                cleanCanvas(ctx);
                if (alpha > 0) {
                    ctx.save();
                    ctx.setGlobalAlpha(alpha);
                    ctx.setFillStyle("#FFFFFF");
                    ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
                    ctx.restore();
                    alpha -= 0.1;
                } else {
                    this.cancel();
                    for (int i = 0; i < canvases.size() - 1; i++) {
                        ctx.drawImage(canvases.get(i).getCanvasElement(), 0, 0);
                    }
                    Image snapshot = new Image();
                    snapshot.setUrl(ctx.getCanvas().toDataUrl("image/png"));

                    String sel = selected != null ? selected.getStId() : null;
                    final ExportDialog dialog = new ExportDialog(context, sel, flagTerm, includeInteractors, snapshot);
                    dialog.showCentered();
                    cleanCanvas(ctx);
                }
            }
        }).scheduleRepeating(20);
    }

    public void notifyHoveredExpression(DiagramObject item, Coordinate model) {
        Renderer renderer = rendererManager.getRenderer(item);
        Double exp = (renderer != null) ? renderer.getExpressionHovered(item, model, column) : null;
        boolean isChanged = (hoveredExpression != null && exp == null) || (hoveredExpression == null && exp != null);
        if (isChanged || hoveredExpression != null && !hoveredExpression.equals(exp)) {
            hoveredExpression = exp;
            this.eventBus.fireEventFromSource(new ExpressionValueHoveredEvent(hoveredExpression), this);
        }
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        this.column = e.getColumn();
    }

    public void renderInteractors(Collection<DiagramInteractor> items, Context context) {
        cleanCanvas(interactors);

        AnalysisStatus analysisStatus = context.getAnalysisStatus();
        Double minExp = 0.0;
        Double maxExp = 0.0;
        AnalysisType analysisType = AnalysisType.NONE;
        if (analysisStatus != null) {
            analysisType = AnalysisType.getType(analysisStatus.getAnalysisSummary().getType());
            if (analysisStatus.getExpressionSummary() != null) {
                minExp = analysisStatus.getExpressionSummary().getMin();
                maxExp = analysisStatus.getExpressionSummary().getMax();
            }
        }

        Double factor = context.getDiagramStatus().getFactor();
        Coordinate offset = context.getDiagramStatus().getOffset();
        List<InteractorEntity> entities = new LinkedList<>();
        for (DiagramInteractor item : items) {
            if (item instanceof InteractorEntity) {
                entities.add((InteractorEntity) item);
            } else {
                InteractorRenderer renderer = interactorRendererManager.getRenderer(item);
                if (renderer != null) {
                    renderer.draw(interactors, item, factor, offset);
                }
            }
        }
        InteractorRenderer renderer = interactorRendererManager.getRenderer(InteractorEntity.class);
        if (renderer != null) {
            interactors.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
            for (InteractorEntity entity : entities) {
                switch (analysisType) {
                    case NONE:
                    case SPECIES_COMPARISON:
                        renderer.draw(interactors, entity, factor, offset);
                        break;
                    case OVERREPRESENTATION:
                        renderer.drawEnrichment(interactors, entity, factor, offset);
                        break;
                    case EXPRESSION:
                    case GSVA:
                    case GSA_STATISTICS:
                        renderer.drawExpression(interactors, entity, column, minExp, maxExp, factor, offset);
                        break;
                    case GSA_REGULATION:
                        renderer.drawRegulation(interactors, entity, column, minExp, maxExp, factor, offset);
                        break;
                }
                interactors.save();
                interactors.setFillStyle(entity.getProfile().getText());
                renderer.drawText(interactors, entity, factor, offset);
                interactors.restore();
            }
        }
    }

    public void render(Collection<DiagramObject> items, Context context) {
        ColourProfileType colourProfileType = context.getColourProfileType();
        AnalysisStatus analysisStatus = context.getAnalysisStatus();
        double factor = context.getDiagramStatus().getFactor();
        Coordinate offset = context.getDiagramStatus().getOffset();
        setCanvasesProperties(factor);

        Double minExp = 0.0;
        Double maxExp = 0.0;
        AnalysisType analysisType = AnalysisType.NONE;
        if (analysisStatus != null) {
            analysisType = AnalysisType.getType(analysisStatus.getAnalysisSummary().getType());
            if (analysisStatus.getExpressionSummary() != null) {
                minExp = analysisStatus.getExpressionSummary().getMin();
                maxExp = analysisStatus.getExpressionSummary().getMax();
            }
        }

        ItemsDistribution itemsDistribution = new ItemsDistribution(items, analysisType);
        for (String renderableClass : itemsDistribution.keySet()) {
            if (renderableClass.equals("Reaction"))
                continue; //Reactions are drawn at the end (they follow slightly different approach)
            if (renderableClass.equals("Shadow"))
                continue; //Shadows will be rendered at the end because their text has to be on top of everything

            final Renderer renderer = rendererManager.getRenderer(renderableClass);
            if (renderer == null) continue;

            final AdvancedContext2d ctx = this.getContext2d(renderableClass);
            ctx.setLineWidth(RendererProperties.NODE_LINE_WIDTH);

            MapSet<RenderType, DiagramObject> target = itemsDistribution.getItems(renderableClass);

            Set<DiagramObject> fadeOut = target.getElements(RenderType.FADE_OUT);
            if (fadeOut != null) {
                renderFadeoutItems(renderer, fadeOut, factor, offset);
            }

            if (analysisType.equals(AnalysisType.NONE)) {
                //By doing this we avoid changing the context several time (which improves the rendering time)
                renderer.setColourProperties(ctx, ColourProfileType.NORMAL);
                renderer.setTextProperties(text, ColourProfileType.NORMAL);
                Set<DiagramObject> normal = target.getElements(RenderType.NORMAL);
                if (normal != null) {
                    renderItems(renderer, ctx, normal, factor, offset);
                }

                Set<DiagramObject> diseaseObjects = target.getElements(RenderType.DISEASE);
                if (diseaseObjects != null) {
                    ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
                    renderItems(renderer, ctx, diseaseObjects, factor, offset);
                }
            } else {
                Set<DiagramObject> noHitByAnalysisNormal = target.getElements(RenderType.NOT_HIT_BY_ANALYSIS_NORMAL);
                renderer.setColourProperties(ctx, ColourProfileType.ANALYSIS);
                renderer.setTextProperties(text, ColourProfileType.ANALYSIS);
                if (noHitByAnalysisNormal != null) {
                    renderItems(renderer, ctx, noHitByAnalysisNormal, factor, offset);
                }
                Set<DiagramObject> noHitByAnalysisDisease = target.getElements(RenderType.NOT_HIT_BY_ANALYSIS_DISEASE);
                if (noHitByAnalysisDisease != null) {
                    ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
                    renderItems(renderer, ctx, noHitByAnalysisDisease, factor, offset);
                }
                Set<DiagramObject> enrichmentNormal = target.getElements(RenderType.HIT_BY_ENRICHMENT_NORMAL);
                if (enrichmentNormal != null) {
                    ctx.setFillStyle(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());
                    renderer.setTextProperties(text, ColourProfileType.NORMAL);
                    renderEnrichment(renderer, ctx, enrichmentNormal, factor, offset);
                }
                Set<DiagramObject> enrichmentDisease = target.getElements(RenderType.HIT_BY_ENRICHMENT_DISEASE);
                if (enrichmentDisease != null) {
                    ctx.setFillStyle(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());
                    ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
                    renderer.setTextProperties(text, ColourProfileType.NORMAL);
                    renderEnrichment(renderer, ctx, enrichmentDisease, factor, offset);
                }
                Set<DiagramObject> expressionNormal = target.getElements(RenderType.HIT_BY_EXPRESSION_NORMAL);
                if (expressionNormal != null) {
                    renderer.setTextProperties(text, ColourProfileType.NORMAL);
                    if (analysisType == AnalysisType.GSA_REGULATION) {
                        renderRegulation(renderer, ctx, expressionNormal, column, minExp, maxExp, factor, offset);
                    } else {
                        renderExpression(renderer, ctx, expressionNormal, column, minExp, maxExp, factor, offset);
                    }
                }
                Set<DiagramObject> expressionDisease = target.getElements(RenderType.HIT_BY_EXPRESSION_DISEASE);
                if (expressionDisease != null) {
                    renderer.setTextProperties(text, ColourProfileType.NORMAL);
                    ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
                    if (analysisType == AnalysisType.GSA_REGULATION) {
                        renderRegulation(renderer, ctx, expressionDisease, column, minExp, maxExp, factor, offset);
                    } else {
                        renderExpression(renderer, ctx, expressionDisease, column, minExp, maxExp, factor, offset);
                    }
                }
                Set<DiagramObject> hitInteractors = target.getElements(RenderType.HIT_INTERACTORS);
                if (hitInteractors != null) {
                    ctx.setStrokeStyle(AnalysisColours.get().PROFILE.getRibbon());
                    ctx.setLineWidth(16 * factor);
                    renderHitInteractors(renderer, ctx, hitInteractors, factor, offset);
                }
            }
        }

        eventBus.fireEventFromSource(
                new RenderOtherDataEvent(rendererManager,
                        items,
                        entities,
                        new OverlayContext(this.overlay, this.buffer)
                ), this);

        cleanCanvas(this.buffer); //It could have been used for the expression overlay (it is fastest cleaning it once)

        //Reactions are rendered after all the other types. They have special characteristics.
        //renderItems uses "reactions" context2d to draw connectors. It is better to set the colour properties once
        items = itemsDistribution.getAll("Reaction");
        if (!items.isEmpty()) { //No need to check for null here
            Renderer reactionRenderer = rendererManager.getRenderer("Reaction");
            reactionRenderer.setColourProperties(reactions, ColourProfileType.NORMAL);
            reactionRenderer.setColourProperties(this.fadeOut, ColourProfileType.FADE_OUT);
            for (DiagramObject item : items) {
                if (item.getIsFadeOut() != null) {
                    reactionRenderer.draw(this.fadeOut, item, factor, offset);
                } else if (item.getIsDisease() != null) {
                    reactions.save();
                    reactions.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
                    reactionRenderer.draw(reactions, item, factor, offset);
                    reactions.restore();
                } else {
                    reactionRenderer.draw(reactions, item, factor, offset);
                }
            }
        }

        //Shadows to be rendered at the VERY END of it :)
        Renderer shadowRenderer = rendererManager.getRenderer("Shadow");
        shadowRenderer.setTextProperties(shadowsText, ColourProfileType.NORMAL);
        for (DiagramObject item : itemsDistribution.getAll("Shadow")) {
            shadowRenderer.draw(shadows, item, factor, offset);
            shadowRenderer.drawText(shadowsText, item, factor, offset);
        }
    }

    private void renderItems(Renderer renderer, AdvancedContext2d ctx, Set<DiagramObject> objects, double factor, Coordinate offset) {
        ConnectorRenderer connectorRenderer = this.rendererManager.getConnectorRenderer();
        for (DiagramObject item : objects) {
            renderer.draw(ctx, item, factor, offset);
            renderer.drawText(this.text, item, factor, offset);
            if (item instanceof Node) {
                Node node = (Node) item;
                connectorRenderer.draw(this.reactions, this.fadeOut, this.reactionDecorators, node, factor, offset);
            }
        }
    }

    private void renderFadeoutItems(Renderer renderer, Set<DiagramObject> objects, double factor, Coordinate offset) {
        ConnectorRenderer connectorRenderer = this.rendererManager.getConnectorRenderer();
        renderer.setColourProperties(this.fadeOut, ColourProfileType.FADE_OUT);
        for (DiagramObject item : objects) {
            renderer.draw(this.fadeOut, item, factor, offset);
            if (item instanceof Node) {
                Node node = (Node) item;
                connectorRenderer.draw(this.fadeOut, this.fadeOut, this.reactionDecorators, node, factor, offset);
            }
        }
        renderer.setTextProperties(this.fadeOut, ColourProfileType.FADE_OUT);
        this.fadeOut.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getText());
        for (DiagramObject item : objects) {
            renderer.drawText(this.fadeOut, item, factor, offset);
        }
    }

    private void renderEnrichment(Renderer renderer, AdvancedContext2d ctx, Set<DiagramObject> objects, double factor, Coordinate offset) {
        ConnectorRenderer connectorRenderer = this.rendererManager.getConnectorRenderer();
        OverlayContext overlay = new OverlayContext(this.overlay, this.buffer);
        for (DiagramObject item : objects) {
            renderer.drawEnrichment(ctx, overlay, item, factor, offset);
            renderer.drawText(this.text, item, factor, offset);
            if (item instanceof Node) {
                Node node = (Node) item;
                connectorRenderer.draw(this.reactions, this.fadeOut, this.reactionDecorators, node, factor, offset);
            }
        }
    }

    private void renderExpression(Renderer renderer, AdvancedContext2d ctx, Set<DiagramObject> objects, int c, double min, double max, double factor, Coordinate offset) {
        ConnectorRenderer connectorRenderer = this.rendererManager.getConnectorRenderer();
        OverlayContext overlay = new OverlayContext(this.overlay, this.buffer);
        for (DiagramObject item : objects) {
            try {
                renderer.drawExpression(ctx, overlay, item, c, min, max, factor, offset);
            } catch (Exception e) {
                Console.error(e.getMessage(), this);
            }
            renderer.drawText(this.text, item, factor, offset);
            if (item instanceof Node) {
                Node node = (Node) item;
                connectorRenderer.draw(this.reactions, this.fadeOut, this.reactionDecorators, node, factor, offset);
            }
        }
    }

    private void renderRegulation(Renderer renderer, AdvancedContext2d ctx, Set<DiagramObject> objects, int c, double min, double max, double factor, Coordinate offset) {
        ConnectorRenderer connectorRenderer = this.rendererManager.getConnectorRenderer();
        OverlayContext overlay = new OverlayContext(this.overlay, this.buffer);
        for (DiagramObject item : objects) {
            try {
                renderer.drawRegulation(ctx, overlay, item, c, min, max, factor, offset);
            } catch (Exception e) {
                Console.error(e.getMessage(), this);
            }
            renderer.drawText(this.text, item, factor, offset);
            if (item instanceof Node) {
                Node node = (Node) item;
                connectorRenderer.draw(this.reactions, this.fadeOut, this.reactionDecorators, node, factor, offset);
            }
        }
    }

    private void renderHitInteractors(Renderer renderer, AdvancedContext2d ctx, Set<DiagramObject> objects, double factor, Coordinate offset) {
        for (DiagramObject object : objects) {
            renderer.drawHitInteractors(ctx, object, factor, offset);
        }
    }

    private void setCanvasesProperties(double factor) {
        this.fadeOut.setLineWidth(factor);
        this.fadeOut.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));

        this.links.setLineWidth(factor); //(RendererProperties.NODE_LINE_WIDTH);
        this.reactions.setLineWidth(factor);
        this.reactions.setLineWidth(factor); //(RendererProperties.NODE_LINE_WIDTH);

        this.reactionDecorators.setLineWidth(factor);
        this.reactionDecorators.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));

        DiagramProfileProperties profileProperties = DiagramColours.get().PROFILE.getProperties();

        this.entitiesDecorators.setGlobalAlpha(0.5);
        this.entitiesDecorators.setFillStyle(profileProperties.getHighlight());

        this.flag.setLineWidth(factor * 10);
        this.flag.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getFlag());

        this.halo.setLineWidth(factor * 6);
        this.halo.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getHalo());

        this.reactionsSelection.setLineWidth(factor * 3);
        this.reactionsSelection.setStrokeStyle(profileProperties.getSelection());

        this.entitiesHighlight.setLineWidth(factor * 8);
        this.entitiesHighlight.setStrokeStyle(profileProperties.getHighlight());

        this.entitiesSelection.setLineWidth(factor * 3);
        this.entitiesSelection.setStrokeStyle(profileProperties.getSelection());

        this.reactionsHighlight.setLineWidth(factor * 7);
        this.reactionsHighlight.setStrokeStyle(profileProperties.getHighlight());

        this.interactors.setLineWidth(factor * 2);
        this.interactors.setTextAlign(Context2d.TextAlign.CENTER);
        this.interactors.setTextBaseline(Context2d.TextBaseline.MIDDLE);

        this.interactorsSelection.setLineWidth(factor * 3);
        this.interactorsSelection.setStrokeStyle(profileProperties.getSelection());

        this.interactorsHighlight.setLineWidth(factor * 7);
        this.interactorsHighlight.setStrokeStyle(profileProperties.getHighlight());
    }

    public void initialise() {
        //The widget can only be initialised ONCE
        if (this.getWidgetCount() > 0) return;

        int width = getParent().getOffsetWidth();
        int height = getParent().getOffsetHeight();

        this.setWidth(width + "px");
        this.setHeight(height + "px");

        this.compartments = createCanvas(width, height);
        this.shadows = createCanvas(width, height);
        this.notes = createCanvas(width, height);
        this.links = createCanvas(width, height);

        this.fadeOut = createCanvas(width, height);
        this.flag = createCanvas(width, height);
        this.halo = createCanvas(width, height);

        this.reactionsHighlight = createCanvas(width, height);
        this.entitiesHighlight = createCanvas(width, height);

        this.reactions = createCanvas(width, height);
        this.reactionsSelection = createCanvas(width, height);
        this.reactionDecorators = createCanvas(width, height);

        this.entities = createCanvas(width, height);
//        this.entitiesDecorators = createCanvas(width, height);
        this.text = createCanvas(width, height);
        this.overlay = createCanvas(width, height);
        this.entitiesDecorators = createCanvas(width, height);
        this.entitiesSelection = createCanvas(width, height);
        this.shadowsText = createCanvas(width, height);

        this.interactorsHighlight = createCanvas(width, height);
        this.interactorsSelection = createCanvas(width, height);
        this.interactors = createCanvas(width, height);

        this.tooltipContainer = createToolTipContainer(width, height);

        this.buffer = createCanvas(width, height);  //Top-level canvas (mouse ctrl and buffer)

        //Set the LineCap to round to avoid discontinuities in the reaction lines
        //NOTE: Setting the LineJoin cannot work as we draw every line segment separately
        this.reactions.setLineCap(Context2d.LineCap.ROUND);
        this.reactionsHighlight.setLineCap(Context2d.LineCap.ROUND);
        this.reactionsSelection.setLineCap(Context2d.LineCap.ROUND);

        //Thumbnails
        this.add(createThumbnailsContainer());

        this.add(staticIllustrationThumbnail.getStaticIllustrationPanel());
    }

    private FlowPanel createThumbnailsContainer() {
        FlowPanel thumbnailContainer = new FlowPanel();
        thumbnailContainer.getElement().addClassName("pwp-ThumbnailContainer");
        Style style = thumbnailContainer.getElement().getStyle();
        style.setDisplay(Style.Display.FLEX);
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
        style.setProperty("alignItems", "center");

        thumbnailContainer.add(this.thumbnail);
        thumbnailContainer.add(this.staticIllustrationThumbnail);

        return thumbnailContainer;
    }

    private AdvancedContext2d createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        //We need to avoid the default context menu for the canvases
        canvas.addDomHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                event.preventDefault();
                event.stopPropagation();
            }
        }, ContextMenuEvent.getType());
        //Not used in this implementation but useful to apply styles from the outside
        canvas.getElement().addClassName("pwp-DiagramCanvas");
        this.setCanvasProperties(canvas, width, height);
        this.add(canvas, 0, 0);
        this.canvases.add(canvas);
        return canvas.getContext2d().cast();
    }

    //INITIALIZE THE CANVAS taking into account the CanvasProperties
    private void setCanvasProperties(Canvas canvas, int width, int height) {
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
    }

    private TooltipContainer createToolTipContainer(int width, int height) {
        TooltipContainer tooltipContainer = new TooltipContainer(this.eventBus, width, height);
        this.add(tooltipContainer, 0, 0);
        return tooltipContainer;
    }

    private AdvancedContext2d getContext2d(String renderableClass) {
        AdvancedContext2d rtn = null;
        switch (renderableClass) {
            case "Note":
                rtn = this.notes;
                break;
            case "Compartment":
                rtn = this.compartments;
                break;
            case "Protein":
            case "Cell":
            case "Chemical":
            case "ChemicalDrug":
            case "ProteinDrug":
            case "RNADrug":
            case "Complex":
            case "ComplexDrug":
            case "Entity":
            case "EntitySet":
            case "EntitySetDrug":
            case "ProcessNode":
            case "EncapsulatedNode":
            case "FlowLine":
            case "Interaction":
            case "RNA":
            case "Gene":
                rtn = this.entities;
                break;
            case "Reaction":
                rtn = this.reactions;
                break;
            case "Shadow":
                rtn = this.shadows;
                break;
            case "EntitySetAndMemberLink":
            case "EntitySetAndEntitySetLink":
                rtn = this.links;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + renderableClass);
        }
        return rtn;
    }
}

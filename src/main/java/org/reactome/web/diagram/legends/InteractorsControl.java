package org.reactome.web.diagram.legends;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.interactors.InteractorsExporter;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

import java.util.Collections;
import java.util.List;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        ContentRequestedHandler, ContentLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler, InteractorsLayoutUpdatedHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING = "Loading interactors from ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING_PSICQUIC = "Loading PSICQUIC interactors from ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_NO_INTERACTORS_FOUND = "No interactors found in ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_DOWNLOAD_TOOLTIP = "Download all diagram interactors from ";
    private static int DELAY = 5000;

    private OverlayResource currentOverlayResource;

    private Context context;
    private Image loadingIcon;
    private InlineLabel message;
    private InlineLabel summaryLb;
    private FlowPanel controlsFP;
    private Slider slider;
    private PwpButton downloadBtn;
    private PwpButton reloadBtn;
    private PwpButton closeBtn;

    private Timer hideTimer;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        this.initUI();
        this.setVisible(false);

        this.initHandlers();

        hideTimer = new Timer() {
            @Override
            public void run() {
                setVisible(false);
            }
        };
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.closeBtn)) {
            //Is safe to do this here (even if there is not loading in progress because that scenario is checked by the loader)
            setVisible(false);
            hideTimer.cancel();
            if (loadingIcon.isVisible()) {
                eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
            } else {
                eventBus.fireEventFromSource(new InteractorsCollapsedEvent(currentOverlayResource.getIdentifier()), this);
            }
            setVisible(false);
        } else if (source.equals(this.downloadBtn)) {
            if (context != null) {
                MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(currentOverlayResource.getIdentifier());
                if (interactors != null && !interactors.isEmpty()) {
                    String filename = context.getContent().getStableId() + "_Interactors_" + currentOverlayResource.getName() + ".csv";
                    InteractorsExporter.exportInteractors(filename, interactors);
                }
            }
        } else if (source.equals(this.reloadBtn)) {
            // Fire event for a Resource selection to trigger reloading
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(currentOverlayResource), this);
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            context = event.getContext();
            if (currentOverlayResource != null && context.getInteractors().isInteractorResourceCached(currentOverlayResource.getIdentifier())) {
                update();
            }
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
        setVisible(false);
    }

    @Override
    public void onInteractorsLayoutUpdated(InteractorsLayoutUpdatedEvent event) {
        update();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentOverlayResource = event.getResource();

        if (currentOverlayResource.getIdentifier().equals(DiagramFactory.DISEASE_RESOURCE)) {
            this.slider.setMin(0);
            this.slider.setValue(DiagramFactory.DISEASE_RESOURCE_INITIAL_SCORE);
        } else {
            this.slider.setMin(DiagramFactory.INTERACTORS_RESOURCE_INITIAL_SCORE);
            this.slider.setValue(DiagramFactory.INTERACTORS_RESOURCE_INITIAL_SCORE);
        }

        hideTimer.cancel();
        //context is null when the diagram is in the process of loading (loading message is meant to be displayed)
        if (context == null || !context.getInteractors().isInteractorResourceCached(currentOverlayResource.getIdentifier())) {
            setVisible(true);
            displayLoader(true, currentOverlayResource);
        } else {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    update();
                }
            });
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        List<RawInteractorEntity> entities = event.getInteractors().getEntities() == null ? Collections.EMPTY_LIST : event.getInteractors().getEntities();
        int totalInteractorsLoaded = entities.size();
        if (totalInteractorsLoaded == 0) {
            displayWarning(MSG_NO_INTERACTORS_FOUND + currentOverlayResource.getName());
            setTimer(DELAY);
        } else {
            hideTimer.cancel();
            update();
        }

    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        setVisible(true);
        switch (event.getLevel()) {
            case WARNING:
                displayWarning(event.getMessage());
                setTimer(DELAY);
                break;
            case ERROR:
                displayError(event.getMessage());
                setTimer(DELAY);
                break;
            case ERROR_RECOVERABLE:
                if (event.getMessage().equals("Not Found")) {
                    displayError(MSG_NO_INTERACTORS_FOUND + currentOverlayResource.getName());
                } else {
                    displayError(event.getMessage());
                }
                reloadBtn.setTitle("Retry loading interactors from " + currentOverlayResource.getName());
                reloadBtn.setVisible(true);
                break;
        }

    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        double threshold = event.getPercentage();
        InteractorsContent.setInteractorsThreshold(currentOverlayResource.getIdentifier(), threshold);
        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
        updateSummary();
    }

    private void update() {
        int burstEntities = context.getContent().getNumberOfBurstEntities();
        if (burstEntities == 0) {
            setVisible(false);
        } else {
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
            message.addStyleName(RESOURCES.getCSS().interactorsControlMessageShort());
            setVisible(true);
            setMessage(currentOverlayResource.getName());
            controlsFP.setVisible(true);
            reloadBtn.setVisible(false);
            slider.setValue(InteractorsContent.getInteractorsThreshold(currentOverlayResource.getIdentifier()));
            downloadBtn.setTitle(MSG_DOWNLOAD_TOOLTIP + currentOverlayResource.getName());
            updateSummary();
        }
    }

    private void updateSummary() {
        if (context != null) {
            summaryLb.setVisible(true);
            summaryLb.setText("(" + context.getInteractors().getUniqueRawInteractorsCountPerResource(currentOverlayResource.getIdentifier()) + ")");
        }
    }

    private void initUI() {
        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());
        addStyleName(RESOURCES.getCSS().unselectable());

        loadingIcon = new Image(RESOURCES.loader());
        loadingIcon.setStyleName(RESOURCES.getCSS().interactorsControlLoadingIcon());
        message = new InlineLabel("");
        message.setStyleName(RESOURCES.getCSS().interactorsControlMessage());
        summaryLb = new InlineLabel();
        summaryLb.setStyleName(RESOURCES.getCSS().interactorsControlMessage());
        summaryLb.setTitle("Total number of unique interactors present in the diagram");

        closeBtn = new PwpButton("Close and clear interactors", RESOURCES.getCSS().close(), this);
        downloadBtn = new PwpButton(MSG_DOWNLOAD_TOOLTIP, RESOURCES.getCSS().download(), this);
        reloadBtn = new PwpButton("Retry loading interactors", RESOURCES.getCSS().reload(), this);

        slider = new Slider(100, 24, 0, 1, 0.45, true);
        slider.setTooltip("Use this slider to set the confidence threshold");
        slider.addSliderValueChangedHandler(this);
        slider.setStyleName(RESOURCES.getCSS().interactorsControlSlider());

        controlsFP = new FlowPanel();
        controlsFP.setStyleName(RESOURCES.getCSS().interactorsControlControls());
        controlsFP.add(slider);
        controlsFP.add(downloadBtn);

        add(loadingIcon);
        add(message);
        add(summaryLb);
        add(closeBtn);
        add(controlsFP);
        add(reloadBtn);
    }

    private void initHandlers() {
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLayoutUpdatedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
       eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
    }

    private void displayLoader(boolean visible, OverlayResource resource) {
        this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        loadingIcon.setVisible(visible);
        summaryLb.setVisible(false);
        controlsFP.setVisible(!visible);
        reloadBtn.setVisible(!visible);
        if (visible) {
            String msg = "";
            switch (resource.getType()) {
                case PSICQUIC:
                    msg = MSG_LOADING_PSICQUIC;
                    break;
                case STATIC:
                case CUSTOM:
                    msg = MSG_LOADING;
                    break;
            }

            msg += resource.getName() + "...";
            message.removeStyleName(RESOURCES.getCSS().interactorsControlMessageShort());
            message.setText(msg);
        }
    }

    private void displayError(String serverMsg) {
        if (serverMsg == null) {
            displayError(false);
        } else {
            setMessage(serverMsg);
            displayError(true);
        }
    }

    private void displayError(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        }
        summaryLb.setVisible(!visible);
        controlsFP.setVisible(!visible);
        reloadBtn.setVisible(!visible);
    }

    private void displayWarning(String msg) {
        setMessage(msg);
        displayWarning(true);
    }

    private void displayWarning(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlWarning());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        }
        summaryLb.setVisible(!visible);
        controlsFP.setVisible(!visible);
        reloadBtn.setVisible(!visible);
    }

    private void setMessage(String msg) {
        loadingIcon.setVisible(false);
        message.setText(msg);
    }

    public void setTimer(int millis) {
        hideTimer.cancel();
        hideTimer.schedule(millis);
    }

}

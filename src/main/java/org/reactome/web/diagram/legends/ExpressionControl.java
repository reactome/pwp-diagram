package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

import java.util.List;

import static org.reactome.web.analysis.client.model.AnalysisType.*;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ExpressionControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler,
        ContentRequestedHandler, ValueChangeHandler<Boolean> {

    private static Integer MIN_SPEED = 3000;
    private static Integer MAX_SPEED = 500;
    private Integer speed = MIN_SPEED / 2 + MAX_SPEED;

    private InlineLabel message;
    private ExpressionSummary expressionSummary;
    private int currentCol = 0;
    private Timer timer;

    private PwpButton rewindBtn;
    private PwpButton playBtn;
    private PwpButton pauseBtn;
    private PwpButton forwardBtn;
    private SpeedButton speedBtn;
    private PwpButton closeBtn;
    private Button filterBtn;
    private Slider slider;
    private FlowPanel infoPanel;

    private ResultFilter filter;
    private boolean isExpanded;

    public ExpressionControl(EventBus eventBus) {
        super(eventBus);

        //Setting the legend style
        LegendPanelCSS css = RESOURCES.getCSS();
        addStyleName(css.analysisControl());
        addStyleName(css.expressionControl());

        this.rewindBtn = new PwpButton("Show previous", css.rewind(), this);
        this.add(this.rewindBtn);

        this.playBtn = new PwpButton("Play", css.play(), this);
        this.add(this.playBtn);

        this.pauseBtn = new PwpButton("Pause", css.pause(), this);
        this.pauseBtn.setVisible(false);
        this.add(this.pauseBtn);

        this.forwardBtn = new PwpButton("Show next", css.forward(), this);
        this.add(this.forwardBtn);

        this.speedBtn = new SpeedButton(RESOURCES);
        this.speedBtn.setStyleName(css.speed());
        this.speedBtn.addValueChangeHandler(this);
        this.speedBtn.disable();
        this.add(this.speedBtn);

        this.slider = new Slider(100, 24, 0, 1, 0.5, false);
        this.slider.addSliderValueChangedHandler(this);
        this.slider.setVisible(false);
        this.slider.setStyleName(css.slide());
        this.add(this.slider);

        this.message = new InlineLabel();
        this.add(this.message);

        this.closeBtn = new PwpButton("Close", css.close(), this);
        this.add(this.closeBtn);

        this.filterBtn = new IconButton(RESOURCES.filterWarningIcon(), css.filterBtn(), "Analysis results are filtered. Click to find out more.", this);
        this.filterBtn.setVisible(false);
        this.add(this.filterBtn);

        this.infoPanel = new FlowPanel();
        this.infoPanel.setStyleName(RESOURCES.getCSS().infoPanel());
        this.add(infoPanel);

        this.initHandlers();
        this.initTimer();
        this.setVisible(false);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        AnalysisType type = event.getType();
        if (!event.isReset() && (type == EXPRESSION || type == GSA_STATISTICS || type == GSVA || type == GSA_REGULATION)) {
            this.currentCol = 0;
            this.eventBus.fireEventFromSource(new ExpressionColumnChangedEvent(this.currentCol), this);
            this.expressionSummary = event.getExpressionSummary();
            this.setName();

            filter = event.getFilter();
            updateFilterInfo();
            if(isExpanded)
                collapse();


            makeVisible(200); // Appear with delay
        } else {
            this.setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        if (this.isVisible()) {
            if (this.timer.isRunning()) pause();
            this.message.setText("");
            this.expressionSummary = null;
            this.setVisible(false);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();

        if (source.equals(this.closeBtn))
            eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
        else if (source.equals(this.rewindBtn))
            this.moveBackward();
        else if (source.equals(this.playBtn))
            this.play();
        else if (source.equals(this.pauseBtn))
            this.pause();
        else if (source.equals(this.forwardBtn))
            this.moveForward();
        else if (source.equals(this.filterBtn))
            toggleExpandedPanel();
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        SpeedButton source = (SpeedButton) event.getSource();
        if (source.equals(this.speedBtn)) {
            this.slider.setVisible(source.isDown());
        }
    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        this.speed = (int) ((MAX_SPEED - MIN_SPEED) * event.getPercentage()) + MIN_SPEED;
        if (this.timer.isRunning()) {
            this.timer.cancel();
            this.timer.scheduleRepeating(this.speed);
        }
    }

    private void initTimer() {
        this.timer = new Timer() {
            @Override
            public void run() {
                moveForward();
            }
        };
    }

    private void moveBackward() {
        if (this.currentCol == 0) {
            this.currentCol = this.expressionSummary.getColumnNames().size() - 1;
        } else {
            this.currentCol -= 1;
        }
        this.setName();
        this.eventBus.fireEventFromSource(new ExpressionColumnChangedEvent(this.currentCol), this);
    }

    private void moveForward() {
        if (this.currentCol == this.expressionSummary.getColumnNames().size() - 1) {
            this.currentCol = 0;
        } else {
            this.currentCol += 1;
        }
        this.setName();
        this.eventBus.fireEventFromSource(new ExpressionColumnChangedEvent(this.currentCol), this);
    }

    private void pause() {
        this.rewindBtn.setEnabled(true);
        this.forwardBtn.setEnabled(true);
        this.speedBtn.setDown(false);
        this.speedBtn.disable();
        this.slider.setVisible(false);

        this.pauseBtn.setVisible(false);
        this.playBtn.setVisible(true);

        if (this.timer.isRunning()) {
            this.timer.cancel();
        }
    }

    private void play() {
        this.rewindBtn.setEnabled(false);
        this.forwardBtn.setEnabled(false);
        this.speedBtn.enable();

        this.playBtn.setVisible(false);
        this.pauseBtn.setVisible(true);

        this.moveForward();
        this.timer.scheduleRepeating(this.speed);
    }

    private void setName() {
        List<String> cols = this.expressionSummary.getColumnNames();
        String pos = (this.currentCol + 1) + "/" + cols.size();
        String name = cols.get(this.currentCol);
        this.message.setText(pos + " :: " + name);
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        pause();
        this.setVisible(false);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(false);
    }

    private void updateFilterInfo() {
        if (filter == null)  {
            filterBtn.setVisible(false);
        } else {
            String resource = filter.getResource().equalsIgnoreCase("TOTAL") ? "" : filter.getResource();
            double pValue =  filter.getpValue() == null ? 1d : filter.getpValue();
            boolean includeDisease = filter.getIncludeDisease();
            Integer min = filter.getMin();
            Integer max = filter.getMax();

            boolean filterApplied = !resource.isEmpty() || pValue != 1d || !includeDisease || min != null || max != null;

            filterBtn.setVisible(filterApplied);

            infoPanel.clear();
            if (filterApplied) {
                Label title = new Label("Applied filter:");
                title.setStyleName(RESOURCES.getCSS().infoPanelTitle());
                infoPanel.add(title);

                if (!resource.isEmpty()) {
                    addFilterTag(resource, "Selected resource is " + resource);
                }

                if (pValue != 1d) {
                    addFilterTag("p ≤ " + pValue, "p-value is set to " + pValue);
                }

                if (!includeDisease) {
                    addFilterTag("No disease", "Disease pathways are excluded");
                }

                if (min != null && max != null) {
                    addFilterTag(min + "≤ size ≤" + max, "Only pathways with sizes between " + min + " and " + max + " are displayed");
                }
            }

        }
    }

    private void addFilterTag(String text, String tooltip) {
        Label lb = new Label(text);
        lb.setStyleName(RESOURCES.getCSS().infoPanelTag());
        lb.setTitle(tooltip);
        infoPanel.add(lb);
    }

    private void toggleExpandedPanel() {
        if (!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    private void expand() {
        setHeight("58px");
        isExpanded = true;
    }

    private void collapse() {
        setHeight("28px");
        isExpanded = false;
    }
}

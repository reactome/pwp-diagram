package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EnrichmentControl extends LegendPanel implements ClickHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler,
        ContentRequestedHandler {

    private InlineLabel message;
    private Button filterBtn;
    private PwpButton closeBtn;

    private FlowPanel infoPanel;

    private ResultFilter filter;
    private boolean isExpanded;

    public EnrichmentControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.enrichmentControl());

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
        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(this.closeBtn)){
            eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
        } else if (event.getSource().equals(this.filterBtn)) {
            toggleExpandedPanel();
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()) {
            case OVERREPRESENTATION:
            case SPECIES_COMPARISON:
                String message = event.getType().name().replaceAll("_", " ");
                this.message.setText(message.toUpperCase());
                filter = event.getFilter();
                updateFilterInfo();
                if(isExpanded)
                    collapse();
                makeVisible(200); // Appear with delay
                break;
            default:
                hide();
        }
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        hide();
    }

    private void hide(){
        if(this.isVisible()) {
            this.message.setText("");
            this.setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        hide();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.hide();
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

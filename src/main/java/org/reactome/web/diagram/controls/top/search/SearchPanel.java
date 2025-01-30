package org.reactome.web.diagram.controls.top.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.search.SearchLauncher;
import org.reactome.web.diagram.search.autocomplete.AutoCompletePanel;
import org.reactome.web.diagram.search.details.DetailsInfoPanel;
import org.reactome.web.diagram.search.results.ResultsPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPanel extends FlowPanel {
    public final SearchLauncher launcher;

    public SearchPanel(EventBus eventBus) {
        //Setting the legend style
        setStyleName(RESOURCES.getCSS().searchPanel());

        this.launcher = new SearchLauncher(eventBus);
        this.add(launcher);

        AutoCompletePanel autoCompletePanel = new AutoCompletePanel();
        autoCompletePanel.addAutoCompleteSelectedHandler(launcher);
//        launcher.addSearchBoxArrowKeysHandler(autoCompletePanel);
        launcher.addSearchPerformedHandler(autoCompletePanel);
        launcher.addAutoCompleteRequestedHandler(autoCompletePanel);
        launcher.addPanelCollapsedHandler(autoCompletePanel);
        launcher.addPanelExpandedHandler(autoCompletePanel);
        launcher.addOptionsCollapsedHandler(autoCompletePanel);
        launcher.addOptionsExpandedHandler(autoCompletePanel);
        this.add(autoCompletePanel);

        ResultsPanel results = new ResultsPanel(eventBus);
        // Listen to click events on results and return focus on SearchLauncher
        results.addClickHandler(event -> launcher.setFocus(true));
        results.addFacetsLoadedHandler(launcher);
        launcher.addSearchPerformedHandler(results);
        launcher.addAutoCompleteRequestedHandler(results);
        launcher.addPanelCollapsedHandler(results);
        launcher.addPanelExpandedHandler(results);
        this.add(results);

        DetailsInfoPanel details = new DetailsInfoPanel(eventBus);
        results.addResultSelectedHandler(details);
        launcher.addSearchPerformedHandler(details);
        launcher.addAutoCompleteRequestedHandler(details);
        launcher.addPanelCollapsedHandler(details);
        launcher.addPanelExpandedHandler(details);
        this.add(details);
    }


    public static SearchPanelResources RESOURCES;

    static {
        RESOURCES = GWT.create(SearchPanelResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SearchPanelResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SearchPanelCSS.CSS)
        SearchPanelCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SearchPanel")
    public interface SearchPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/top/search/SearchPanel.css";

        String searchPanel();
    }
}

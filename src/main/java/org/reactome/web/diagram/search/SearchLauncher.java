package org.reactome.web.diagram.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.IconToggleButton;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;
import org.reactome.web.diagram.handlers.SearchKeyPressedHandler;
import org.reactome.web.diagram.search.events.*;
import org.reactome.web.diagram.search.facets.FacetsPanel;
import org.reactome.web.diagram.search.handlers.*;
import org.reactome.web.diagram.search.searchbox.*;

import java.util.Date;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchLauncher extends AbsolutePanel implements ClickHandler,
        ContentLoadedHandler, ContentRequestedHandler, LayoutLoadedHandler, SearchBoxUpdatedHandler,
        SearchBoxArrowKeysHandler, SearchKeyPressedHandler,
        FacetsLoadedHandler, FacetsChangedHandler,
        AutoCompleteSelectedHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String OPENING_TEXT = "Search for a term, e.g. pten ...";
    private static int FOCUS_IN_TEXTBOX_DELAY = 300;
    private static String SEARCH_EXPANDED_COOKIE = "pwp-search-expanded-by-default";
    private static int SEARCH_QUERY_MINIMUM_LENGTH = 2;

    private EventBus eventBus;
    private Context context;

    private SearchBox input;
    private IconButton searchBtn;
    private IconButton clearBtn;
    private IconButton executeBtn;
    private IconToggleButton optionsBtn;

    private FlowPanel filtersPanel;
    private FacetsPanel facetsPanel;

    private Boolean isExpanded = false;
    private Boolean isExpandedVertically = false;

    private Timer focusTimer;

    public SearchLauncher(EventBus eventBus) {
        //Setting the search style
        setStyleName(RESOURCES.getCSS().launchPanel());

        this.eventBus = eventBus;

        this.searchBtn = new IconButton(RESOURCES.searchIcon(), RESOURCES.getCSS().launch(),"Search in the diagram",this);
        this.add(searchBtn);

        this.input = new SearchBox();
        this.input.setStyleName(RESOURCES.getCSS().input());
        this.input.getElement().setPropertyString("placeholder", OPENING_TEXT);
        this.input.getElement().setPropertyBoolean("spellcheck", false);
        this.add(input);

        clearBtn = new IconButton("", RESOURCES.clear());
        clearBtn.setStyleName(RESOURCES.getCSS().clearBtn());
        clearBtn.setVisible(false);
        clearBtn.setTitle("Clear search");
        clearBtn.addClickHandler(event -> clearSearch());
        this.add(clearBtn);

        executeBtn = new IconButton("", RESOURCES.searchGo());
        executeBtn.setStyleName(RESOURCES.getCSS().executeBtn());
        executeBtn.setVisible(true);
        executeBtn.setTitle("Search");
        executeBtn.addClickHandler(this);
//        executeBtn.setEnabled(false);
        this.add(executeBtn);

        optionsBtn = new IconToggleButton("", RESOURCES.options(), RESOURCES.optionsClose(),this);
        optionsBtn.setStyleName(RESOURCES.getCSS().optionsBtn());
        optionsBtn.setVisible(true);
        optionsBtn.setTitle("Filter your results");
        optionsBtn.setEnabled(false);
        this.add(optionsBtn);

        facetsPanel = new FacetsPanel();
        filtersPanel = new FlowPanel();
        filtersPanel.add(facetsPanel);
        this.add(filtersPanel);

        focusTimer = new Timer() {
            @Override
            public void run() {
                SearchLauncher.this.input.setFocus(true);
            }
        };

        this.initHandlers();
        this.searchBtn.setEnabled(false);
    }

    public HandlerRegistration addSearchBoxArrowKeysHandler(SearchBoxArrowKeysHandler handler){
        return input.addSearchBoxArrowKeysHandler(handler);
    }

    public HandlerRegistration addPanelCollapsedHandler(PanelCollapsedHandler handler){
        return addHandler(handler, PanelCollapsedEvent.TYPE);
    }

    public HandlerRegistration addPanelExpandedHandler(PanelExpandedHandler handler){
        return addHandler(handler, PanelExpandedEvent.TYPE);
    }

    public HandlerRegistration addOptionsCollapsedHandler(OptionsCollapsedHandler handler){
        return addHandler(handler, OptionsCollapsedEvent.TYPE);
    }

    public HandlerRegistration addOptionsExpandedHandler(OptionsExpandedHandler handler){
        return addHandler(handler, OptionsExpandedEvent.TYPE);
    }

    public HandlerRegistration addAutoCompleteRequestedHandler(AutoCompleteRequestedHandler handler){
        return addHandler(handler, AutoCompleteRequestedEvent.TYPE);
    }

    public HandlerRegistration addSearchPerformedHandler(SearchPerformedHandler handler){
        return addHandler(handler, SearchPerformedEvent.TYPE);
    }

    @Override
    public void onAutoCompleteSelected(AutoCompleteSelectedEvent event) {
       input.setValue(event.getTerm());
       performSearch();
    }

    @Override
    public void onArrowKeysPressed(SearchBoxArrowKeysEvent event) {
        Integer keyPressed = event.getValue();
        if(keyPressed == KeyCodes.KEY_ESCAPE) {
            setFocus(false);
            this.collapsePanel();
            clearSearch();
        } else if (keyPressed == KeyCodes.KEY_ENTER || keyPressed == KeyCodes.KEY_MAC_ENTER) {
            performSearch();
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(this.searchBtn)) {
            if (!isExpanded) {
                expandPanel();
            } else {
                collapsePanel();
                stopExpandingByDefault();
            }
        } else if (event.getSource().equals(this.optionsBtn)) {
            if (!isExpandedVertically) {
                expandPanelVertically();
            } else {
                collapsePanelVertically();
            }
        } else if (event.getSource().equals(this.executeBtn)) {
            performSearch();
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.context = null;
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.searchBtn.setEnabled(true);
        this.context = event.getContext();

        // Expand the panel by default unless the cookie is present
        // Next, focus in the textbox by default unless DiagramFactory.RESPOND_TO_SEARCH_SHORTCUT
        // is set to false (when integrated by third party apps)
        String expandSearch = Cookies.getCookie(SEARCH_EXPANDED_COOKIE);
        if (expandSearch == null || expandSearch.isEmpty()) {
            if(!isExpanded) {
                this.expandPanel(DiagramFactory.RESPOND_TO_SEARCH_SHORTCUT);
            }
        }

        if(!this.input.getText().isEmpty()) {
            performSearch();
        }
    }


    @Override
    public void onFacetsLoaded(FacetsLoadedEvent event) {
        facetsPanel.setFacets(event.getFacets(), event.getSelectedFacets(), event.getScope());
        optionsBtn.setEnabled(!event.getFacets().isEmpty());
    }


    @Override
    public void onSelectedFacetsChanged(FacetsChangedEvent event) {
        performSearch();
    }

    @Override
    public void onSearchBoxUpdated(SearchBoxUpdatedEvent event) {
        getAutoCompleteSuggestions();
        if(input.getText().isEmpty()) {
            optionsBtn.setEnabled(false);
        }
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.searchBtn.setEnabled(false);
    }

    @Override
    public void onSearchKeyPressed(SearchKeyPressedEvent event) {
        // This is to handle the shortcut [ctrl] + [F]
        // Expand only if search is enabled
        if(searchBtn.isEnabled()) {
            if (!isExpanded) {
                expandPanel();
            } else {
                collapsePanel();
                stopExpandingByDefault();
            }
        }
    }

    public void setFocus(boolean focused){
        this.input.setFocus(focused);
    }

    private void clearSearch() {
        if (!input.getValue().isEmpty()) {
            input.setValue("");
            setFocus(true);
            optionsBtn.setEnabled(false);
        }
        eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false), this);
    }

    private void collapsePanel(){
        if(focusTimer.isRunning()){
            focusTimer.cancel();
        }
        removeStyleName(RESOURCES.getCSS().launchPanelExpanded());
        collapsePanelVertically();
        optionsBtn.setActive(false);
        input.removeStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = false;
        fireEvent(new PanelCollapsedEvent());
    }

    private void collapsePanelVertically() {
        removeStyleName(RESOURCES.getCSS().launchPanelExpandedVertically());
        isExpandedVertically = false;
        fireEvent(new OptionsCollapsedEvent());
    }

    private void expandPanel() {
        expandPanel(true);
    }

    private void expandPanel(boolean focus){
        addStyleName(RESOURCES.getCSS().launchPanelExpanded());
        input.addStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = true;
        fireEvent(new PanelExpandedEvent());
        if(focus) {
            focusTimer.schedule(FOCUS_IN_TEXTBOX_DELAY);
        }
    }

    private void expandPanelVertically(){
        addStyleName(RESOURCES.getCSS().launchPanelExpandedVertically());
        isExpandedVertically = true;
        fireEvent(new OptionsExpandedEvent());
    }


    private void initHandlers(){
        this.input.addSearchBoxUpdatedHandler(this);
        this.input.addSearchBoxArrowKeysHandler(this);

        this.facetsPanel.addFacetsChangedHandler(this);

        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        eventBus.addHandler(SearchKeyPressedEvent.TYPE, this);
    }

    private void getAutoCompleteSuggestions() {
        facetsPanel.clearView();
        collapsePanelVertically();
        optionsBtn.setActive(false);


        String term = input.getText().trim();
        fireEvent(new AutoCompleteRequestedEvent(term));

        showHideClearBtn();
    }

    private void performSearch() {
        String query = input.getText().trim();

        if (query.length() < SEARCH_QUERY_MINIMUM_LENGTH) return;

        SearchArguments searchArgs = new SearchArguments(
                query,
                context.getContent().getStableId(),
                context.getContent().getSpeciesName(),
                facetsPanel.getSelectedFacets(),
                facetsPanel.getScope()
        );

        if(searchArgs.hasValidQuery()) {
            fireEvent(new SearchPerformedEvent(searchArgs));
            showHideClearBtn();
        }
    }

    private void showHideClearBtn() {
        clearBtn.setVisible(!input.getText().isEmpty());
    }


    private void stopExpandingByDefault() {
        Date expires = new Date();
        long nowLong = expires.getTime();
        nowLong = nowLong + (1000 * 60 * 60 * 24 * 365L); //One year time
        expires.setTime(nowLong);
        Cookies.setCookie(SEARCH_EXPANDED_COOKIE, "false", expires);
    }

    public static SearchLauncherResources RESOURCES;
    static {
        RESOURCES = GWT.create(SearchLauncherResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SearchLauncherResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SearchLauncherCSS.CSS)
        SearchLauncherCSS getCSS();

        @Source("images/search.png")
        ImageResource searchIcon();

        @Source("images/search_go.png")
        ImageResource searchGo();

        @Source("images/cancel.png")
        ImageResource clear();

        @Source("images/search_options.png")
        ImageResource options();

        @Source("images/search_options_close.png")
        ImageResource optionsClose();

        @Source("images/you_are_here.png")
        ImageResource youAreHere();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SearchLauncher")
    public interface SearchLauncherCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/SearchLauncher.css";

        String launchPanel();

        String launchPanelExpanded();

        String launchPanelExpandedVertically();

        String launch();

        String input();

        String inputActive();

        String clearBtn();

        String executeBtn();

        String optionsBtn();

        String youAreHereIcon();

    }

}

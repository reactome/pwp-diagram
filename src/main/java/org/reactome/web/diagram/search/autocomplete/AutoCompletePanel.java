package org.reactome.web.diagram.search.autocomplete;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.search.autocomplete.cells.AutoCompleteCell;
import org.reactome.web.diagram.search.autocomplete.cells.RecentSearchCell;
import org.reactome.web.diagram.search.common.RegExpUtil;
import org.reactome.web.diagram.search.events.*;
import org.reactome.web.diagram.search.handlers.*;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.searchbox.SearchBoxArrowKeysEvent;
import org.reactome.web.diagram.search.searchbox.SearchBoxArrowKeysHandler;
import org.reactome.web.diagram.util.Console;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class AutoCompletePanel extends AbstractAccordionPanel implements SearchPerformedHandler,
        OptionsExpandedHandler, OptionsCollapsedHandler, SearchBoxArrowKeysHandler,
        AutoCompleteRequestedHandler, AutoCompleteResultsFactory.Handler {

    private final static int AUTOCOMPLETE_SIZE = 10;
    private final static int RECENT_SIZE = 9;

    private List<String> terms;

    private CellList<AutoCompleteResult> autoCompleteList;
    private SingleSelectionModel<AutoCompleteResult> autoCompleteSelectionModel;

    private CellList<String> recentItemsList;

    private Widget recentSearchesPanel;

    public AutoCompletePanel() {
        setStyleName(RESOURCES.getCSS().container());

        FlowPanel main = new FlowPanel();
        add(main);

        // Create a cell to render each value.
        AutoCompleteCell autoCompleteCell = new AutoCompleteCell();
        autoCompleteList = new CellList<>(autoCompleteCell, CUSTOM_STYLE);
        autoCompleteList.setStyleName(RESOURCES.getCSS().autoCompleteList());
        autoCompleteList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        autoCompleteSelectionModel = new SingleSelectionModel<>();
        autoCompleteList.setSelectionModel(autoCompleteSelectionModel);
        autoCompleteSelectionModel.addSelectionChangeHandler(event -> {
            AutoCompleteResult selected = autoCompleteSelectionModel.getSelectedObject();
            if (selected != null) {
                fireEvent(new AutoCompleteSelectedEvent(selected.getResult()));
                makeVisible(false);
            }
        });

        // Set the total row count. This isn't strictly necessary, but it affects
        // paging calculations, so its good habit to keep the row count up to date.
        autoCompleteList.setRowCount(AUTOCOMPLETE_SIZE, true);
        main.add(autoCompleteList);

        recentSearchesPanel = getRecentSearchesPanel();
        showRecentSearches(false);
        main.add(recentSearchesPanel);

        makeVisible(false);
    }


    public HandlerRegistration addAutoCompleteSelectedHandler(AutoCompleteSelectedHandler handler) {
        return addHandler(handler, AutoCompleteSelectedEvent.TYPE);
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        requestAutoCompleteResults(event.getTerm());
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        AutoCompleteResultsFactory.cancel();
        makeVisible(false);
        RecentSearchesManager.get().insert(event.getSearchArguments().getQuery());
    }

    public void requestAutoCompleteResults(String tag) {
        if (tag != null && !tag.isEmpty()) {
            String[] allTerms = tag.toLowerCase().split("  *");
            terms = Arrays.asList(allTerms);
            AutoCompleteResultsFactory.searchForTag(tag, this);
        } else {
            makeVisible(false);
            clearAutoCompleteList();
        }
    }

    @Override
    public void onOptionsCollapsed(OptionsCollapsedEvent event) {
//        makeVisible(true);
    }

    @Override
    public void onOptionsExpanded(OptionsExpandedEvent event) {
        makeVisible(false);
    }

    @Override
    public void onArrowKeysPressed(SearchBoxArrowKeysEvent event) {
        if(isVisible()) {
            if(autoCompleteList.getRowCount() > 0){
                AutoCompleteResult current = autoCompleteSelectionModel.getSelectedObject();
                int currentIndex = current==null ? -1 : autoCompleteList.getVisibleItems().indexOf(current);
                int toIndex = currentIndex;
                if (event.getValue() == KeyCodes.KEY_DOWN) {
                    toIndex = (currentIndex + 1 < autoCompleteList.getVisibleItems().size()) ? currentIndex + 1 : autoCompleteList.getVisibleItems().size() - 1;
                    Console.info(" >> Down" + " [" + autoCompleteList.getVisibleItems().size() + "] - " + toIndex);
                } else if (event.getValue() == KeyCodes.KEY_UP) {
                    toIndex = (currentIndex - 1 > 0 ? currentIndex - 1 : 0);
                    Console.info(" >> Up" + " [" + autoCompleteList.getVisibleItems().size() + "] - " + toIndex);
                }
                if(toIndex!=-1 && toIndex!=currentIndex) {
                    autoCompleteSelectionModel.setSelected(autoCompleteList.getVisibleItems().get(toIndex), true);
//                    Console.info(" >> " + " [" + toIndex + "]" + autoCompleteList.getVisibleItem(toIndex).getResult());
//                    SearchResultObject newSelection = dataProvider.getList().get(toIndex);
//                    suggestions.getRowElement(toIndex).scrollIntoView();
//                    selectionModel.setSelected(newSelection, true);
                }
            }
        }
    }

    @Override
    public void onAutoCompleteSearchResult(List<AutoCompleteResult> results) {
        if (!results.isEmpty()) {
            // Used for highlighting the result
            results.forEach(item -> item.setResultDisplay(RegExpUtil.getHighlightingExpression(terms)));

            makeVisible(true);
            autoCompleteList.setRowData(results);
            updateRecentItemsList();
        } else {
            makeVisible(false);
        }
    }

    @Override
    public void onAutoCompleteError() {
        Console.warn("Error retrieving autocomplete suggestions");
//        makeVisible(false);
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        // Do nothing here
    }

    private Widget getRecentSearchesPanel() {
        Label title = new Label("Recent searches");
        title.setStyleName(RESOURCES.getCSS().dividerTitle());

        Label cleanAllRecent = new Label("Clear history");
        cleanAllRecent.setStyleName(RESOURCES.getCSS().dividerClearAll());
        cleanAllRecent.addClickHandler( event -> {
            RecentSearchesManager.get().clear();
            updateRecentItemsList();
        });

        FlowPanel divider = new FlowPanel();
        divider.setStyleName(RESOURCES.getCSS().divider());
        divider.add(title);
        divider.add(cleanAllRecent);

        RecentSearchCell recentSearchCell = new RecentSearchCell();
        recentItemsList = new CellList<>(recentSearchCell, CUSTOM_STYLE);
        recentItemsList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        recentItemsList.setStyleName(RESOURCES.getCSS().recentSearchesList());
        recentItemsList.setRowCount(RECENT_SIZE, true);

        recentItemsList.addCellPreviewHandler(event -> {
            if(event.getNativeEvent().getType().equals("click")) {
                Element el = Element.as(event.getNativeEvent().getEventTarget());
                String className = el.getParentElement().getClassName();
                if(className!=null && className.equalsIgnoreCase("deleteIcon")) {
                    // Handle the delete icon being clicked
                    RecentSearchesManager.get().removeItemByIndex(event.getIndex());
                    updateRecentItemsList();
                } else {
                    // Select the clicked item
                    String selected = recentItemsList.getVisibleItem(event.getIndex());
                    fireEvent(new AutoCompleteSelectedEvent(selected));
                    makeVisible(false);
                }
            }
        });


        FlowPanel panel = new FlowPanel();
        panel.add(divider);
        panel.add(recentItemsList);
        return panel;
    }

    private void clearAutoCompleteList() {
        autoCompleteList.setRowCount(0);
    }

    private void clearRecentSearchesList() {
        recentItemsList.setRowCount(0);
    }

    private void showRecentSearches(boolean visible) {
        if (visible) {
            recentSearchesPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            recentSearchesPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    private void makeVisible(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    private void updateRecentItemsList(){
        List<String> items = RecentSearchesManager.get().getRecentItems();
        if (!items.isEmpty()) {
            recentItemsList.setRowData(items);
        }
        showRecentSearches(!items.isEmpty());
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/search_suggestion.png")
        ImageResource searchSuggestion();

        @Source("../images/search_recent.png")
        ImageResource searchRecent();

        @Source("../images/deleteItem.png")
        ImageResource deleteItem();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-AutoCompletePanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/autocomplete/AutoCompletePanel.css";

        String container();

        String icon();

        String autoCompleteList();

        String divider();

        String dividerTitle();

        String dividerClearAll();

        String recentSearchesList();

        String deleteItem();
    }

    private static CellListResource CUSTOM_STYLE;
    static {
        CUSTOM_STYLE = GWT.create(CellListResource.class);
        CUSTOM_STYLE.cellListStyle().ensureInjected();
    }

    public interface CellListResource extends CellList.Resources {

        @CssResource.ImportedWithPrefix("diagram-CellListResource")
        interface CustomCellList extends CellList.Style {
            String CSS = "org/reactome/web/diagram/search/autocomplete/CustomCellList.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(CustomCellList.CSS)
        CustomCellList cellListStyle();
    }
}

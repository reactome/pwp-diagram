package org.reactome.web.diagram.search.facets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import org.reactome.web.diagram.search.events.FacetsChangedEvent;
import org.reactome.web.diagram.search.handlers.FacetsChangedHandler;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;
import org.reactome.web.diagram.util.Console;

import java.util.*;

/**
 * A panel that contains all the available facets
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class FacetsPanel extends FlowPanel implements ClickHandler {
    private Map<String, FacetTag> facetsMap;
    private Set<String> selectedFacets;

    private Label titleLabel;
    private FlowPanel tagsContainer;

    public FacetsPanel() {
        setVisible(false);
        facetsMap = new HashMap<>();
        selectedFacets = new HashSet<>();
        init();
    }

    public HandlerRegistration addFacetsChangedHandler(FacetsChangedHandler handler) {
        return addHandler(handler, FacetsChangedEvent.TYPE);
    }

    public void setFacets(List<FacetContainer> facets, Set<String> selectedFacets) {
        facetsMap.clear();
        this.selectedFacets.clear();
        if(facets != null && !facets.isEmpty()) {
            Console.info("FacetsPanel: setting facets: " + facets + " - " + selectedFacets);
            for (final FacetContainer facet : facets) {
                FacetTag facetTag = new FacetTag(facet.getName(), facet.getCount());
                facetTag.addClickHandler(this);

                if (selectedFacets == null || selectedFacets.isEmpty()) {
                    // No filter has been imposed - select all facets
                    facetTag.setSelected(true);
                } else {
                    if (selectedFacets.contains(facetTag.getName())) {
                        facetTag.setSelected(true);
                        this.selectedFacets.add(facetTag.getName());
                    }
                }

                facetsMap.put(facetTag.getName(), facetTag);
            }
        }
        updateView();
    }

    @Override
    public void onClick(ClickEvent event) {
        if(selectedFacets.isEmpty()) {
            // Unselect all facets first
            selectAll(false);
        }

        FacetTag clickedFacet = (FacetTag) event.getSource();
        if(clickedFacet.isSelected()) {
            clickedFacet.setSelected(false);
            selectedFacets.remove(clickedFacet.getName());
        } else {
            clickedFacet.setSelected(true);
            selectedFacets.add(clickedFacet.getName());
        }

        // In case all of the facets have been selected then the user does
        // not require to apply any filters
        if(selectedFacets.size() == facetsMap.keySet().size()) {
            selectedFacets.clear();
        }

        updateView();
        fireEvent(new FacetsChangedEvent());

    }

    public Set<String> getSelectedFacets() {
        // Important: Return a defensive copy to avoid problems.
        return new HashSet<>(selectedFacets);
    }

    private void init() {
        titleLabel = new Label("Filter your results by type:");
        titleLabel.setStyleName(RESOURCES.getCSS().title());

        tagsContainer = new FlowPanel();
        tagsContainer.setStyleName(RESOURCES.getCSS().tagContainer());

        SimplePanel sp = new SimplePanel();
        sp.setStyleName(RESOURCES.getCSS().outerContainer());
        sp.add(tagsContainer);

        add(titleLabel);
        add(sp);
    }

    private void selectAll(boolean selected) {
        if(facetsMap!=null) {
            for (FacetTag facet : facetsMap.values()) {
                facet.setSelected(selected);
            }
        }
    }

    private void updateView(){
        tagsContainer.clear();
        for(FacetTag facet : facetsMap.values()) {
            tagsContainer.add(facet);
        }
        setVisible(!facetsMap.isEmpty());
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
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-FacetPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/facets/FacetPanel.css";

        String title();

        String outerContainer();

        String tagContainer();
    }
}

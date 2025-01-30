package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.loader.LoaderManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphObjectInfoPanel extends Composite {

    EventBus eventBus;
    GraphObject graphObject;
    FlowPanel mainPanel;

    public static final ObjectInfoResources OBJECT_INFO_RESOURCES;

    static {
        OBJECT_INFO_RESOURCES = GWT.create(ObjectInfoResources.class);
        OBJECT_INFO_RESOURCES.getCSS().ensureInjected();
    }

    public GraphObjectInfoPanel(EventBus eventBus, GraphObject graphObject, Context context) {
        this.eventBus = eventBus;
        this.graphObject = graphObject;
        this.mainPanel = new FlowPanel();

        SuggestionPanelCSS css = OBJECT_INFO_RESOURCES.getCSS();
        this.init(css);

        FlowPanel header = new FlowPanel();
        header.setStyleName(css.infoHeader());
//        header.add(new Image(databaseObject.getImageResource()));
        header.add(new InlineLabel(graphObject.getDisplayName()));
        this.add(header);

        this.add(new Label("Type: " + graphObject.getClassName()));

        String identifier = graphObject.getStId();
        GeneNameListPanel geneNameListPanel = null;
        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            if (pe.getIdentifier() != null) { //Always try to put other resources identifiers in front of ours
                identifier = pe.getIdentifier() + " [" + identifier + "]";
            }
            if (pe.getGeneNames() != null && !pe.getGeneNames().isEmpty()) {
                geneNameListPanel = new GeneNameListPanel(pe.getGeneNames(), eventBus);
            }
        }

        //Order matters
        this.add(new Label("Identifier: " + identifier.trim()));
        if (geneNameListPanel != null) {
            this.add(geneNameListPanel);
        }

        Collection<GraphReactionLikeEvent> participatesIn = new HashSet<>();
        if (!graphObject.getDiagramObjects().isEmpty()) {
            String title = "Directly in the diagram:";
            this.add(new DatabaseObjectListPanel(title, Collections.singletonList(graphObject), eventBus));

            if (graphObject instanceof GraphPhysicalEntity) {
                participatesIn = ((GraphPhysicalEntity) graphObject).participatesIn();
            } else if (graphObject instanceof GraphReactionLikeEvent) {
                //TODO encapsulate it into a method in ReactionLikeEvent
                Collection<GraphPhysicalEntity> rleParticipants = new HashSet<>();
                GraphReactionLikeEvent rle = (GraphReactionLikeEvent) graphObject;
                rleParticipants.addAll(rle.getInputs());
                rleParticipants.addAll(rle.getOutputs());
                rleParticipants.addAll(rle.getCatalysts());
                rleParticipants.addAll(rle.getActivators());
                rleParticipants.addAll(rle.getInhibitors());
                rleParticipants.addAll(rle.getRequirements());
                if (!rleParticipants.isEmpty()) {
                    int size = rleParticipants.size();
                    title = "Participant" + (size > 1 ? "s(" : "(") + size + "):";
                    this.add(new DatabaseObjectListPanel(title, rleParticipants, eventBus));
                }
            }

        }

        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            Set<GraphPhysicalEntity> parentLocations = pe.getParentLocations();
            if (!parentLocations.isEmpty()) {
                Set<GraphComplex> complexes = new HashSet<>();
                Set<GraphEntitySet> sets = new HashSet<>();
                Set<GraphCell> cells = new HashSet<>();
                for (GraphPhysicalEntity parentLocation : parentLocations) {
                    participatesIn.addAll(parentLocation.participatesIn());
                    if (parentLocation instanceof GraphCompositeEntity) {
                        complexes.add((GraphComplex) parentLocation);
                    } else if (parentLocation instanceof GraphEntitySet) {
                        sets.add((GraphEntitySet) parentLocation);
                    } else if (parentLocation instanceof GraphCell) {
                        cells.add((GraphCell) parentLocation);
                    }
                }

                if (!complexes.isEmpty()) {
                    int size = complexes.size();
                    String title = "Part of " + size + " complex" + (size > 1 ? "es:" : ":");
                    this.add(new DatabaseObjectListPanel(title, complexes, eventBus));
                }

                if (!sets.isEmpty()) {
                    int size = sets.size();
                    String title = "Part of " + size + " set" + (size > 1 ? "s:" : ":");
                    this.add(new DatabaseObjectListPanel(title, sets, eventBus));
                }

                if (!cells.isEmpty()) {
                    int size = cells.size();
                    String title = "Marker of " + size + " cell" + (size > 1 ? "s:" : ":");
                    this.add(new DatabaseObjectListPanel(title, cells, eventBus));
                }

            }
        }

        if (!participatesIn.isEmpty()) {
            int size = participatesIn.size();
            String title = "Participates in " + size + " reaction" + (size > 1 ? "s:" : ":");
            this.add(new DatabaseObjectListPanel(title, participatesIn, eventBus));
        }

        // Include information about the interactors of this entity
        if (context != null && (graphObject instanceof GraphEntityWithAccessionedSequence || graphObject instanceof GraphSimpleEntity)) {
            String resource = LoaderManager.INTERACTORS_RESOURCE.getName();
            this.add(new InteractorsListPanel("According to " + resource + ", it interacts with:", context, (GraphPhysicalEntity) graphObject, eventBus));
        }
    }

    /**
     * Padding grows wider the div but this is not the expected behaviour here. To make it compatible
     * across all browsers the recommendation is having two divs
     * <div> // defines the object info panel properties
     *     <div> // defines the object info mainPanel (set the padding here)
     *         Info mainPanel
     *     </div>
     * </div>
     */
    private void init(SuggestionPanelCSS css) {
        SimplePanel sp = new SimplePanel();
        sp.setStyleName(css.objectInfoPanel());
        this.mainPanel.setStyleName(css.objectInfoContent());
        sp.add(this.mainPanel);
        initWidget(sp);
    }

    public void add(IsWidget widget) {
        this.mainPanel.add(widget);
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface ObjectInfoResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SuggestionPanelCSS.CSS)
        SuggestionPanelCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ObjectInfoPanel")
    public interface SuggestionPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/infopanel/InfoPanel.css";

        String objectInfoPanel();

        String objectInfoContent();

        String infoHeader();

        String databaseObjectListPanel();

        String databaseObjectListTitle();

        String databaseObjectList();

        String listItem();

        String listItemLink();
    }

}

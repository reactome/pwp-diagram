package org.reactome.web.diagram.search.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.common.IconToggleButton;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.events.DiagramObjectsFlagRequestedEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.results.ResultItem;

/**
 * Creates a title in the DetailsPanel containing various
 * information about the selected item, such as name, accession,
 * type, compartment etc.
 *
 * The view of the title is slightly different depending on whether
 * the displayed item is a {@link ResultItem} or a {@link InteractorSearchResult}
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class TitlePanel extends FlowPanel implements ClickHandler,
        DiagramObjectsFlagResetHandler, DiagramObjectsFlaggedHandler {
    private EventBus eventBus;
    private SearchResultObject selectedItem;
    private String identifier;

    private Label name;
    private IconToggleButton flagBtn;
    private FlowPanel firstLine;

    private String termToFlagBy;
    private Boolean includeInteractors = false;
    private String flaggedTerm;

    public TitlePanel(EventBus eventBus) {
        this.eventBus = eventBus;

        initialise();

        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
    }

    public TitlePanel setSelectedItem(SearchResultObject selectedItem) {
        this.selectedItem = selectedItem;
        this.firstLine.clear();

        if (selectedItem instanceof ResultItem) {
            ResultItem item = (ResultItem) selectedItem;
            identifier = item.getIdentifier();
            termToFlagBy = item.getStId();
            populate(item);
        } else if (selectedItem instanceof InteractorSearchResult) {
            InteractorSearchResult item = (InteractorSearchResult) selectedItem;
            identifier = item.getAccession();
            termToFlagBy = item.getAccession();
            populate(item);
        } else {
            termToFlagBy = null;
        }

        flagBtn.setActive(flaggedTerm!=null && flaggedTerm.equals(termToFlagBy));

        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        termToFlagBy = identifier;
    }

    @Override
    public void onClick(ClickEvent event) {
        if(flagBtn.isActive()) {
            eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
        } else {
            if (selectedItem instanceof ResultItem) {
                eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, true), includeInteractors);
            } else if (selectedItem instanceof InteractorSearchResult) {
                eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, true), includeInteractors);
            }
        }
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        flaggedTerm = null;
        flagBtn.setActive(false);
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        this.flaggedTerm = event.getTerm();
        this.includeInteractors = event.getIncludeInteractors();
        flagBtn.setActive(flaggedTerm!=null && flaggedTerm.equals(termToFlagBy));
    }

    private void initialise() {
        setStyleName(RESOURCES.getCSS().container());

        name = new Label();
        name.setStyleName(RESOURCES.getCSS().name());

        flagBtn = new IconToggleButton("", RESOURCES.flag(), RESOURCES.flagClear(), this);
        flagBtn.setStyleName(RESOURCES.getCSS().flagBtn());
        flagBtn.setVisible(true);
        flagBtn.setTitle("Show where this is in the diagram");

        firstLine = new FlowPanel();
        firstLine.setStyleName(RESOURCES.getCSS().line());

        add(flagBtn);
        add(name);
        add(firstLine);
    }

    private void populate(ResultItem item) {
        name.setText(item.getName());
        name.setTitle(item.getName());

        String type = item.getSchemaClass().name.equalsIgnoreCase("Database Object") ? item.getExactType() : item.getSchemaClass().name;
        createAndAddLabel(type, "Type", RESOURCES.getCSS().type());
        createAndAddLabel(item.getStId(), "Id", RESOURCES.getCSS().id());

        if (item.getDatabaseName()!=null && item.getReferenceIdentifier()!=null) {
            String accession = item.getDatabaseName() + ":" + item.getReferenceIdentifier();
            createAndAddLabel(accession, accession, RESOURCES.getCSS().accession());
        }
        createAndAddLabel(item.getCompartments(), "Compartments", RESOURCES.getCSS().compartments());
        if(item.isDisplayed()) {
            createAndAddLabel("This is the displayed pathway diagram", "", RESOURCES.getCSS().general());
        }
    }

    private void populate(InteractorSearchResult item) {
        String alias = item.getAlias();
        if (alias != null) {
            name.setText(alias);
            name.setTitle(alias);

            createAndAddLabel(item.getAccession(), "Accession", RESOURCES.getCSS().accession());
        } else {
            name.setText(item.getAccession());
            name.setTitle(item.getAccession());
        }

        //TODO check why the following is not working
//        createAndAddLabel(item.getSchemaClass().name, "Type", RESOURCES.getCSS().type());
        createAndAddLabel("Interactor", "Type", RESOURCES.getCSS().type());
        createAndAddLabel(item.getResource().getName(), "Resource", RESOURCES.getCSS().id());
    }

    private void createAndAddLabel(String text, String tooltip, String style) {
        if(text != null && !text.isEmpty()) {
            Label label = new Label();
            label.setStyleName(style);
            label.setText(text);
            label.setTitle(tooltip);
            firstLine.add(label);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/flag.png")
        ImageResource flag();

        @Source("../images/flag_clear.png")
        ImageResource flagClear();
    }

    @CssResource.ImportedWithPrefix("diagram-TitlePanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/search/details/TitlePanel.css";

        String container();

        String name();

        String line();

        String id();

        String compartments();

        String type();

        String genes();

        String accession();

        String general();

        String flagBtn();
    }
}

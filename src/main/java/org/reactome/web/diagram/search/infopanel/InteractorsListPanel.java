package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.model.images.InteractorImages;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.util.MapSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InteractorsListPanel extends FlowPanel {

    public InteractorsListPanel(String title, Context context, GraphPhysicalEntity obj, EventBus eventBus) {
        List<RawInteractor> rawInteractors = context.getInteractors().getRawInteractors(LoaderManager.INTERACTORS_RESOURCE.getIdentifier(), obj.getIdentifier() );
        if(rawInteractors==null || rawInteractors.isEmpty()) return;

        GraphObjectInfoPanel.SuggestionPanelCSS css = GraphObjectInfoPanel.OBJECT_INFO_RESOURCES.getCSS();
        this.setStyleName(css.databaseObjectListPanel());

        Label titleLabel = new Label(title);
        titleLabel.setStyleName(css.databaseObjectListTitle());
        this.add(titleLabel);

        FlowPanel listPanel = new FlowPanel();
        listPanel.setStyleName(css.databaseObjectList());

        Map<String, GraphObject> inDiagramInteractors = new HashMap<>();
        MapSet<String, GraphObject> identifierMap = context.getContent().getIdentifierMap();
        for (RawInteractor rawInteractor : rawInteractors) {
            String accession = rawInteractor.getAcc();
            Set<GraphObject> graphObjects = identifierMap.getElements(accession.replaceAll("^\\w+[-:_]", ""));
            if(graphObjects!=null) {
                for (GraphObject graphObject : graphObjects) {
                    List<DiagramObject> diagramObjectList = graphObject.getDiagramObjects();
                    if(!diagramObjectList.isEmpty()) {
                        inDiagramInteractors.put(accession, graphObject);
                    }
                }
            }
        }

        for (RawInteractor rawInteractor : rawInteractors) {
            FlowPanel listItem = new FlowPanel();
            listItem.setStyleName(css.listItem());

            Image icon = null;

            Anchor listItemLink = new Anchor(rawInteractor.getAlias()!=null ? rawInteractor.getAlias() : rawInteractor.getAcc());
            listItemLink.setStyleName(css.listItemLink());
            listItemLink.setTitle(rawInteractor.getAcc());
            GraphObject graphObject = inDiagramInteractors.get(rawInteractor.getAcc());
            if(graphObject!=null) {
                List<DiagramObject> diagramObjects = graphObject.getDiagramObjects();
                if(diagramObjects!=null) {
                    // The interactor exists in the diagram as a protein or chemical
                    listItemLink.setText(graphObject.getDisplayName());
                    listItemLink.setTitle(graphObject.getDisplayName() + " (" + rawInteractor.getAcc() + ")");
                    listItemLink.addClickHandler(InfoActionsHelper.getLinkClickHandler(graphObject, eventBus, this));
                    listItemLink.addMouseOverHandler(InfoActionsHelper.getLinkMouseOver(graphObject, eventBus, this));
                    listItemLink.addMouseOutHandler(InfoActionsHelper.getLinkMouseOut(eventBus, this));
                    icon = new Image(graphObject.getImageResource());
                }
            } else {
                String url = rawInteractor.getAccURL();
                listItemLink.setHref(url);
                listItemLink.addClickHandler(InfoActionsHelper.getInteractorLinkClickHandler(url, eventBus, this));
                icon = new Image(InteractorImages.INSTANCE.interactor());
//                The following lines are for enabling highlighting of the interactors
//                DiagramInteractor diagramInteractor = context.getInteractors().getDiagramInteractor(LoaderManager.INTERACTORS_RESOURCE, rawInteractor.getAcc());
//                if(diagramInteractor!=null){
//                    listItemLink.addMouseOverHandler(InfoActionsHelper.getInteractorLinkMouseOver(diagramInteractor, eventBus, this));
//                    listItemLink.addMouseOutHandler(InfoActionsHelper.getInteractorLinkMouseOut(eventBus, this));
//                }
            }
            listItem.add(icon);
            listItem.add(listItemLink);

            String url = rawInteractor.getEvidencesURL();
            if(url != null) {
                Anchor idItemLink = new Anchor(" Evidence:" + rawInteractor.getEvidences());
                idItemLink.setHref(url);
                idItemLink.addClickHandler(InfoActionsHelper.getInteractionLinkClickHandler(url, eventBus, this));
                idItemLink.setStyleName(css.listItemLink());

                listItem.add(new InlineLabel(" -"));
                listItem.add(idItemLink);
            }
            listItem.add(new InlineLabel(" - Score: " + NumberFormat.getFormat("0.000").format(rawInteractor.getScore())));

            listPanel.add(listItem);
        }
        this.add(listPanel);
    }
}

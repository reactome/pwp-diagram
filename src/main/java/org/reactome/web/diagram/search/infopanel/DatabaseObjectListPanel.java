package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;

import java.util.Collection;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DatabaseObjectListPanel extends FlowPanel {

    public DatabaseObjectListPanel(String title, Collection<? extends GraphObject> objects, EventBus eventBus) {
        GraphObjectInfoPanel.SuggestionPanelCSS css = GraphObjectInfoPanel.OBJECT_INFO_RESOURCES.getCSS();

        this.setStyleName(css.databaseObjectListPanel());

        Label titleLabel = new Label(title);
        titleLabel.setStyleName(css.databaseObjectListTitle());
        this.add(titleLabel);

        FlowPanel listPanel = new FlowPanel();

        listPanel.setStyleName(css.databaseObjectList());
        for (GraphObject object : objects) {
            FlowPanel listItem = new FlowPanel();
            listItem.setStyleName(css.listItem());

            Image icon = new Image(object.getImageResource());
            listItem.add(icon);

            Anchor listItemLink = new Anchor(object.getDisplayName());
            listItemLink.setStyleName(css.listItemLink());
            listItemLink.setTitle(object.getDisplayName());
            listItemLink.addClickHandler(InfoActionsHelper.getLinkClickHandler(object, eventBus, this));
            listItemLink.addMouseOverHandler(InfoActionsHelper.getLinkMouseOver(object, eventBus, this));
            listItemLink.addMouseOutHandler(InfoActionsHelper.getLinkMouseOut(eventBus, this));
            if(object instanceof GraphSubpathway || object instanceof GraphPathway) {
                listItemLink.addDoubleClickHandler(InfoActionsHelper.getLinkDoubleClickHandler(object, eventBus,this));
            }
            listItem.add(listItemLink);

            listPanel.add(listItem);
        }
        this.add(listPanel);
    }
}

package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.pwp.model.client.classes.Event;

import java.util.Collection;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EnhancedListPanel extends FlowPanel {

    public EnhancedListPanel(String title, Collection<? extends Event> objects, EventBus eventBus, Content content) {
        GraphObjectInfoPanel.SuggestionPanelCSS css = GraphObjectInfoPanel.OBJECT_INFO_RESOURCES.getCSS();

        this.setStyleName(css.databaseObjectListPanel());

        Label titleLabel = new Label(title);
        titleLabel.setStyleName(css.databaseObjectListTitle());
        this.add(titleLabel);

        FlowPanel listPanel = new FlowPanel();

        listPanel.setStyleName(css.databaseObjectList());
        for (Event object : objects) {
            FlowPanel listItem = new FlowPanel();
            listItem.setStyleName(css.listItem());

            Image icon = new Image(object.getImageResource());
            listItem.add(icon);


            if(content.getStableId().equals(object.getStId())) {
                Label lb = new Label(object.getDisplayName());
                lb.setStyleName(css.listItemLink());
                listItem.add(lb);
            } else {
                GraphObject graphObject = content.getDatabaseObject(object.getStId());

                Anchor listItemLink = new Anchor(object.getDisplayName());
                listItemLink.setStyleName(css.listItemLink());
                listItemLink.setTitle("Double-click to go to " + object.getDisplayName());
                if(graphObject == null) {
                    listItemLink.addDoubleClickHandler(InfoActionsHelper.getPathwayLinkDoubleClickHandler(object, eventBus, this));
                } else {
                    listItemLink.addMouseOverHandler(InfoActionsHelper.getLinkMouseOver(graphObject, eventBus, this));
                    listItemLink.addMouseOutHandler(InfoActionsHelper.getLinkMouseOut(eventBus, graphObject));
                    listItemLink.addClickHandler(InfoActionsHelper.getLinkClickHandler(graphObject, eventBus, this));
                    listItemLink.addDoubleClickHandler(InfoActionsHelper.getLinkDoubleClickHandler(graphObject, eventBus, this));
                }
                listItem.add(listItemLink);
            }

            listPanel.add(listItem);
        }
        this.add(listPanel);
    }
}

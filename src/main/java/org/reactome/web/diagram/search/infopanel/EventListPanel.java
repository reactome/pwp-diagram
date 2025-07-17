package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.pwp.model.client.classes.Event;

import java.util.Collection;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EventListPanel extends FlowPanel {

    public EventListPanel(String title, Collection<? extends Event> objects, EventBus eventBus) {
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

            Anchor listItemLink = new Anchor(object.getDisplayName());
            listItemLink.setStyleName(css.listItemLink());
            listItemLink.setTitle("click to go to " + object.getDisplayName());
            listItemLink.addClickHandler(InfoActionsHelper.getPathwayLinkClickHandler(object, eventBus, this));
            listItem.add(listItemLink);

            listPanel.add(listItem);
        }
        this.add(listPanel);
    }
}

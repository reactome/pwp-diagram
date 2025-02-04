package org.reactome.web.diagram.client.visualisers.diagram;

import org.reactome.web.diagram.common.DisplayManager;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
class DiagramManager {

    private DisplayManager displayManager;

    DiagramManager(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    public void cancelDisplayAnimation(){
        displayManager.cancelDisplayAnimation();
    }

    public void fitDiagram(Content c, boolean animation) {
        displayManager.display(c.getMinX(), c.getMinY(), c.getMaxX(), c.getMaxY(), animation);
    }

    public void displayDiagramObjects(GraphObject item) {
        Set<DiagramObject> toDisplay = item.getRelatedDiagramObjects();
        this.displayManager.display(toDisplay, true);
    }

    public void displayDiagramObjects(Set<DiagramObject> items) {
        this.displayManager.display(items, true);
    }
}

package org.reactome.web.diagram.data.content;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * Used to represent the Pathways inside an EHLD.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class EHLDObject implements DiagramObject {
    private Long id;
    private Long reactomeId;
    private String stableId;
    private String displayName;
    private String schemaClass;
    private final String renderableClass = "ProcessNode";
    private GraphObject graphObject;


    public EHLDObject(Long id, String stableId) {
        this.id = id;
        this.stableId = stableId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getReactomeId() {
        return reactomeId;
    }

    public String getStableId() {return stableId;}

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getSchemaClass() {
        return schemaClass;
    }

    @Override
    public String getRenderableClass() {
        return renderableClass;
    }

    @Override
    public Coordinate getPosition() {
        return null;
    }

    @Override
    public Boolean getIsDisease() {
        return null;
    }

    @Override
    public Boolean getIsFadeOut() {
        return null;
    }

    @Override
    public <T extends GraphObject> T getGraphObject() {
        return (T) graphObject;
    }

    @Override
    public <T extends GraphObject> void setGraphObject (T obj) {
        graphObject = obj;
        reactomeId = obj.getDbId(); // Set the proper dbId to the EHLD object
        schemaClass = obj.getSchemaClass().schemaClass;
        displayName = obj.getDisplayName();
    }

    @Override
    public ContextMenuTrigger contextMenuTrigger() {
        return null;
    }

    @Override
    public double getMinX() {
        return 0;
    }

    @Override
    public double getMinY() {
        return 0;
    }

    @Override
    public double getMaxX() {
        return 0;
    }

    @Override
    public double getMaxY() {
        return 0;
    }
}

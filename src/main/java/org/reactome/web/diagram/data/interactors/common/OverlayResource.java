package org.reactome.web.diagram.data.interactors.common;

import org.reactome.web.diagram.client.DiagramFactory;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class OverlayResource {

    public enum ResourceType {
        STATIC,
        PSICQUIC,
        CUSTOM
    }

    private String identifier;
    private String name;
    private String filename;
    private ResourceType type;
    private boolean active;

    public OverlayResource(String identifier, String name, ResourceType type) {
        this(identifier, name, null, type, true);
    }

    public OverlayResource(String identifier, String name, String filename, ResourceType type) {
        this(identifier, name, filename, type, true);
    }

    public OverlayResource(String identifier, String name, String filename, ResourceType type, boolean active) {
        this.identifier = identifier;
        this.type = type;
        this.active = active;
        this.name = format(name);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public ResourceType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "OverlayResource{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", active=" + active +
                '}';
    }

    /**
     * Checks whether the resource is 'static' and applies the proper name for it.
     * In any other case it changes the name by capitalizing the first character
     * only in case all letters are lowercase
     */
    private String format(String originalName){
        String rtn;
        switch (type) {
//            case STATIC:
//                rtn = DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME; // returns the proper static name, e.g. IntAct (Static);
//                break;
            default:
                //Capitalize the first character of the resource in certain cases
                rtn = (originalName.equals(originalName.toLowerCase())) ? originalName.substring(0, 1).toUpperCase() + originalName.substring(1) : originalName;
                break;
        }
        return rtn;
    }
}

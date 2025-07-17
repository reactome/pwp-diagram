package org.reactome.web.diagram.data.interactors.common;

/**
 * This class is used for caching purposes. When displaying the summary item, related to the interactors of a
 * certain diagram entity node (TR), the number and the status (pressed or not) are taken directly from the
 * layout object. Since we want to keep the previous data cached (to avoid reloading 'known data' but also to
 * improve the performance) the best way of doing is 'replicating' it and storing separately in the content.
 *
 * Note 1: This object has been added as an attribute to the 'Node' class (layout data package). The reason
 * why is to avoid having to search for them once a interactor summary item is clicked (Having a pointer is
 * way faster than searching for it in a Set).
 *
 * Note 2: The pointer from the 'Node' class has to be updated when the resource is changed.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsSummary {

    private String accession;
    private Long diagramId;
    private int number;
    private boolean pressed = false;

    public InteractorsSummary(String accession, Long diagramId, int number) {
        this.accession = accession;
        this.diagramId = diagramId;
        this.number = number;
    }

    public String getAccession() {
        return accession;
    }

    public Long getDiagramId() {
        return diagramId;
    }

    public int getNumber() {
        return number;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractorsSummary that = (InteractorsSummary) o;

        return diagramId != null ? diagramId.equals(that.diagramId) : that.diagramId == null;

    }

    @Override
    public int hashCode() {
        return diagramId != null ? diagramId.hashCode() : 0;
    }
}

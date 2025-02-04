package org.reactome.web.diagram.data.layout;



import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Connector {

    Long getEdgeId();

    Boolean getIsDisease();

    String getType();

    Boolean getIsFadeOut();

    List<Segment> getSegments();

    Shape getEndShape();

    Stoichiometry getStoichiometry();
}

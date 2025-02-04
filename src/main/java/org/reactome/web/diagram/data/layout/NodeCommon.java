package org.reactome.web.diagram.data.layout;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface NodeCommon extends DiagramObject {

    NodeProperties getProp();

    NodeProperties getInnerProp();

    Identifier getIdentifier();

    Coordinate getTextPosition();

    Bound getInsets();

    Color getBgColor();

    Color getFgColor();

    Boolean getIsCrossed();

    Boolean getNeedDashedBorder();

}

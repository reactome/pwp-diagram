package org.reactome.web.diagram.profiles.diagram.model;

import java.io.Serializable;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface DiagramProfile extends Serializable {

    String getName();

    DiagramProfileProperties getProperties();

    DiagramProfileNode getAttachment();

    DiagramProfileNode getCell();

    DiagramProfileNode getCellNucleus();

    DiagramProfileNode getChemical();

    DiagramProfileNode getChemicaldrug();

    DiagramProfileNode getCompartment();

    DiagramProfileNode getComplex();

    DiagramProfileNode getComplexdrug();

    @Deprecated
    DiagramProfileNode getEntity();

    DiagramProfileNode getEntityset();

    DiagramProfileNode getEntitysetdrug();

    DiagramProfileNode getFlowline();

    DiagramProfileNode getGene();

    DiagramProfileNode getNote();

    DiagramProfileNode getInteractor();

    DiagramProfileNode getLink();

    DiagramProfileNode getOtherentity();

    DiagramProfileNode getProcessnode();

    DiagramProfileNode getEncapsulatednode();

    DiagramProfileNode getProtein();

    DiagramProfileNode getProteindrug();

    DiagramProfileNode getReaction();

    DiagramProfileNode getRna();

    DiagramProfileNode getRnadrug();

    DiagramProfileNode getStoichiometry();

    DiagramProfileThumbnail getThumbnail();

}

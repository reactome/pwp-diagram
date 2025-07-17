package org.reactome.web.diagram.renderers.layout.s800;

import org.reactome.web.diagram.renderers.layout.s300.ProteinDrugRenderer300;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ProteinDrugRenderer800 extends ProteinDrugRenderer300 {

    @Override
    public boolean nodeAttachmentsVisible() {
        return true;
    }

}

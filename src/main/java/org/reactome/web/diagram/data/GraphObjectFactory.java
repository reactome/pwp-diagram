package org.reactome.web.diagram.data;

import com.google.gwt.core.client.GWT;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.model.factory.ModelFactoryException;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.GraphNode;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

/**
 * This factory is created and kept for every diagram in its context. This is meant to keep previous loaded
 * diagrams in memory for quick revisiting (LRU is recommended though)
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class GraphObjectFactory {
    //LayoutLoader is in charge of update the content when a new
    public static Content content;

    public static GraphObject getOrCreateDatabaseObject(GraphNode node) {
        if (node == null || node.getDbId() == null) {
            throw new RuntimeException("It is not possible to create a DatabaseObject for " + node);
        }

        GraphObject dbObject = content.getDatabaseObject(node.getDbId());
        if (dbObject != null) return dbObject;

        String auxSchemaClass = node.getSchemaClass();
        SchemaClass schemaClass = SchemaClass.getSchemaClass(auxSchemaClass);

        switch (schemaClass) {
            case CANDIDATE_SET:
                dbObject = new GraphCandidateSet((EntityNode) node);
                break;
            case CHEMICAL_DRUG:
                dbObject = new GraphChemicalDrug((EntityNode) node);
                break;
            case COMPLEX:
                dbObject = new GraphComplex((EntityNode) node);
                break;
            case CELL:
                dbObject = new GraphCell((EntityNode) node);
                break;
            case CELL_LINEAGE_PATH:
                dbObject = new GraphCellLineagePath((EntityNode) node);
                break;
            case DEFINED_SET:
                dbObject = new GraphDefinedSet((EntityNode) node);
                break;
            case ENTITY_SET:
                dbObject = new GraphEntitySet((EntityNode) node);
                break;
            case ENTITY_WITH_ACCESSIONED_SEQUENCE:
                dbObject = new GraphEntityWithAccessionedSequence((EntityNode) node);
                break;
            case GENOME_ENCODED_ENTITY:
                dbObject = new GraphGenomeEncodedEntity((EntityNode) node);
                break;
            case OTHER_ENTITY:
                dbObject = new GraphOtherEntity((EntityNode) node);
                break;
            case PATHWAY:
                dbObject = new GraphPathway((EntityNode) node);
                break;
            case TOP_LEVEL_PATHWAY:
                dbObject = new GraphPathway((EntityNode) node);
                break;
            case POLYMER:
                dbObject = new GraphPolymer((EntityNode) node);
                break;
            case SIMPLE_ENTITY:
                dbObject = new GraphSimpleEntity((EntityNode) node);
                break;
            case BLACK_BOX_EVENT:
                dbObject = new GraphBlackBoxEvent((EventNode) node);
                break;
            case DEPOLYMERISATION:
                dbObject = new GraphDepolymerisation((EventNode) node);
                break;
            case FAILED_REACTION:
                dbObject = new GraphFailedReaction((EventNode) node);
                break;
            case POLYMERISATION:
                dbObject = new GraphPolymerisation((EventNode) node);
                break;
            case PROTEIN_DRUG:
                dbObject = new GraphProteinDrug((EntityNode) node);
                break;
            case REACTION:
                dbObject = new GraphReaction((EventNode) node);
                break;
            case CELL_DEVELOPMENT_STEP:
                dbObject = new GraphCellDevelopmentStep((EventNode) node);
                break;
            case RNA_DRUG:
                dbObject = new GraphRNADrug((EntityNode) node);
                break;
            case GO_CELLULAR_COMPONENT:
                dbObject = new GraphGO_CellularComponent((EntityNode) node);
                break;
            case COMPARTMENT:
                dbObject = new GraphCompartment((EntityNode) node);
                break;
            default:
                String msg = "It is not possible to create a DatabaseObject. [dbId:" + node.getDbId() + ", schemaClass:" + node.getSchemaClass() + "]";
                GWT.log(msg);
                throw new ModelFactoryException(msg);
        }

        content.cache(dbObject);
        return dbObject;
    }

    public static GraphSubpathway getOrCreateDatabaseObject(SubpathwayNode subpathway) {
        if (subpathway == null || subpathway.getDbId() == null) {
            throw new RuntimeException("It is not possible to create a DatabaseObject for " + subpathway);
        }

        GraphSubpathway rtn = content.getGraphSubpathway(subpathway.getDbId());
        if (rtn != null) return rtn;

        rtn = new GraphSubpathway(subpathway);
        for (Long id : subpathway.getEvents()) {
            GraphObject graphObject = content.getDatabaseObject(id);
            if (graphObject instanceof GraphEvent) {
                rtn.addContainedEvent((GraphEvent) graphObject);
            }
        }

        content.cache(rtn);
        return rtn;
    }
}

package org.reactome.web.diagram.data.graph.model.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface GraphObjectImages extends ClientBundle {

    GraphObjectImages INSTANCE = GWT.create(GraphObjectImages.class);

    @Source("BlackBoxEvent.png")
    ImageResource blackBoxEvent();

    @Source("CandidateSet.png")
    ImageResource candidateSet();

    @Source("Cell.png")
    ImageResource cell();

    @Source("Complex.png")
    ImageResource complex();

    @Source("ConceptualEvent.png")
    ImageResource conceptualEvent();

    @Source("ChemicalDrug.png")
    ImageResource chemicalDrug();

    @Source("DefinedSet.png")
    ImageResource definedSet();

    @Source("Depolymerization.png")
    ImageResource depolymerization();

    @Source("EntitySet.png")
    ImageResource entitySet();

    @Source("EntityWithAccessionedSequence.png")
    ImageResource entityWithAccessionedSequence();

    @Source("EquivalentEventSet.png")
    ImageResource equivalentEventSet();

    @Source("FailedReaction.gif")
    ImageResource failedReaction();

    @Source("GenomeEncodeEntity.png")
    ImageResource genomeEncodeEntity();

    @Source("isDisease.png")
    ImageResource isDisease();

    @Source("isInferred.png")
    ImageResource isInferred();

    @Source("OpenSet.png")
    ImageResource openSet();

    @Source("OtherEntity.png")
    ImageResource otherEntity();

    @Source("Pathway.png")
    ImageResource pathway();

    @Source("Polymer.png")
    ImageResource polymer();

    @Source("Polymerization.png")
    ImageResource polymerization();

    @Source("ProteinDrug.png")
    ImageResource proteinDrug();

    @Source("Reaction.png")
    ImageResource reaction();

    @Source("ReferenceDNASequence.png")
    ImageResource referenceDNASequence();

    @Source("ReferenceGroup.png")
    ImageResource referenceGroup();

    @Source("ReferenceRNASequence.png")
    ImageResource referenceRNASequence();

    @Source("ReferenceRNASequence.png")
    ImageResource referenceRNADrugSequence();

    @Source("SimpleEntity.png")
    ImageResource simpleEntity();

}

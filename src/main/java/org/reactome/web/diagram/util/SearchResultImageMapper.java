package org.reactome.web.diagram.util;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.interactors.model.images.InteractorImages;
import org.reactome.web.pwp.model.client.factory.DatabaseObjectImages;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SearchResultImageMapper {

    private final static DatabaseObjectImages INSTANCE = DatabaseObjectImages.INSTANCE;
    private final static ImageContainer reaction = new ImageContainer(INSTANCE.reaction(), "Reaction");
    private final static ImageContainer genomeEncodeEntity = new ImageContainer(INSTANCE.genomeEncodeEntity(), "Genome Encoded Entity");
    private final static ImageContainer protein = new ImageContainer(INSTANCE.protein(), "Protein");
    private final static ImageContainer proteinDrug = new ImageContainer(INSTANCE.proteinDrug(), "Protein drug");
    private final static ImageContainer complex = new ImageContainer(INSTANCE.complex(), "Complex");
    private final static ImageContainer set = new ImageContainer(INSTANCE.entitySet(), "Set");
    private final static ImageContainer interactor = new ImageContainer(InteractorImages.INSTANCE.interactor(), "Interactor"); //TODO check the icon for interactors in the model
    private final static ImageContainer pathway = new ImageContainer(INSTANCE.pathway(), "Pathway");
    private final static ImageContainer dnaSequence = new ImageContainer(INSTANCE.referenceDNASequence(), "DNA sequence");
    private final static ImageContainer polymer = new ImageContainer(INSTANCE.polymer(), "Polymer");
    private final static ImageContainer rnaSequence = new ImageContainer(INSTANCE.RNA(), "RNA sequence");
    private final static ImageContainer rnaDrug = new ImageContainer(INSTANCE.RNADrug(), "RNA drug");
    private final static ImageContainer regulation = new ImageContainer(INSTANCE.regulator(), "Regulation");
    private final static ImageContainer simpleEntity = new ImageContainer(INSTANCE.simpleEntity(), "Chemical compound");
    private final static ImageContainer chemicalDrug = new ImageContainer(INSTANCE.chemicalDrug(), "Chemical drug");
    private final static ImageContainer cell = new ImageContainer(INSTANCE.cell(), "Cell");
    private final static ImageContainer otherEntity = new ImageContainer(INSTANCE.otherEntity(), "Other Entity");
    private final static ImageContainer exclamation = new ImageContainer(INSTANCE.exclamation(), "");

    public static class ImageContainer {
        private ImageResource imageResource;
        private String tooltip;

        ImageContainer(ImageResource imageResource, String tooltip) {
            this.imageResource = imageResource;
            this.tooltip = tooltip;
        }

        public ImageResource getImageResource() {
            return imageResource;
        }

        public String getTooltip() {
            return tooltip;
        }
    }

    public static ImageContainer getImage(String type) {
        if (type != null) {
            switch (type.toLowerCase()) {
                case "reaction":
                case "failedreaction":
                case "blackboxevent":
                case "polymerisation":
                case "depolimerisation":
                case "celldevelopmentstep":
                    return reaction;
                case "genomeencodedentity":
                    return genomeEncodeEntity;
                case "protein":
                case "referencegeneproduct":
                    return protein;
                case "complex":
                    return complex;
                case "set":
                case "candidateset":
                case "definedset":
                case "openset":
                    return set;
                case "interactor":
                    return interactor;
                case "pathway":
                case "celllineagepath":
                case "toplevelpathway":
                    return pathway;
                case "genes and transcripts":
                    return genomeEncodeEntity;
                case "dna sequence":
                case "referencednasequence":
                    return dnaSequence;
                case "polymer":
                    return polymer;
                case "rna sequence":
                case "referencernasequence":
                    return rnaSequence;
                case "regulation":
                case "requirement":
                case "positiveregulation":
                case "negativeregulation":
                    return regulation;
                case "chemical compound":
                case "referencemolecule":
                    return simpleEntity;
                case "chemicaldrug":
                    return chemicalDrug;
                case "proteindrug":
                    return proteinDrug;
                case "rnadrug":
                    return rnaDrug;
                case "otherentity":
                    return otherEntity;
                case "cell":
                    return cell;
                default:
                    return exclamation;
            }
        } else {
            return exclamation;
        }
    }
}

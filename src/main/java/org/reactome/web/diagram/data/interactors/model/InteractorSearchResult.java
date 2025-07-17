package org.reactome.web.diagram.data.interactors.model;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.images.InteractorImages;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorSearchResult implements Comparable<InteractorSearchResult>, SearchResultObject {

    private OverlayResource resource;
    private String accession;
    private String alias;
    private int evidences = 0;
    private Map<Long, RawInteractor> interaction;
    private MapSet<Long, GraphObject> interactsWith;

    private String primary;
    private String primaryTooltip;
    private String secondary;
    private String tertiary;

    public InteractorSearchResult(OverlayResource resource, String accession, String alias) {
        this.resource = resource;
        this.accession = accession;
        this.alias = alias;
        this.interactsWith = new MapSet<>();
        this.interaction = new HashMap<>();
    }

    public void addInteractsWith(Long interactionId, Set<GraphObject> interactsWith){
        this.interactsWith.add(interactionId, interactsWith);
    }

    public void addInteraction(RawInteractor rawInteractor){
        this.interaction.put(rawInteractor.getId(), rawInteractor);
        if(rawInteractor.getEvidences()!=null) {
            evidences += rawInteractor.getEvidences();
        }
    }

    /* Important: the term has to be passed in lowercase */
    public boolean containsTerm(String term){
        String alias = this.alias != null ? this.alias : ""; //Alias can be null
        return alias.toLowerCase().contains(term) || accession.toLowerCase().contains(term);
    }

    public OverlayResource getResource() {
        return resource;
    }

    public String getAccession() {
        return accession;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return alias !=null ? alias : accession;
    }

    public Integer getEvidences(Long interactionId) {
        RawInteractor rawInteractor = interaction.get(interactionId);
        if(rawInteractor!=null && rawInteractor.getEvidences() != null) {
            return rawInteractor.getEvidences();
        } else {
            return 0;
        }
    }

    public Double getInteractionScore(Long interactionId) {
        RawInteractor rawInteractor = interaction.get(interactionId);
        return rawInteractor!=null ? rawInteractor.getScore() : null;
    }

    public MapSet<Long, GraphObject> getInteractsWith() {
        return interactsWith;
    }

    @Override
    public ImageResource getImageResource() {
        return InteractorImages.INSTANCE.interactor();
    }

    @Override
    public String getPrimarySearchDisplay() {
        return primary;
    }

    @Override
    public String getPrimaryTooltip() {
        return primaryTooltip;
    }

    @Override
    public String getSecondarySearchDisplay() {
        return secondary;
    }

    @Override
    public String getTertiarySearchDisplay() {
        return tertiary;
    }

    @Override
    public SchemaClass getSchemaClass() {
        return SchemaClass.getSchemaClass("Interactor");
    }

    @Override
    public void setSearchDisplay(SearchArguments arguments) {
        if (alias != null) {
            primary = alias;
            primaryTooltip = alias;
            secondary = accession;
        } else {
            primary = accession;
            primaryTooltip = accession;
        }
        // Adding the number of evidences in the second line of the suggestion
        String evidenceStr = evidences == 0 ? "" : evidences == 1 ? evidences + " evidence" : evidences + " pieces of evidence";
        secondary += ", " + evidenceStr;

        tertiary = resource.getName();
        RegExp regExp = arguments.getHighlightingExpression();
        if (regExp != null) {
            primary = regExp.replace(primary, "<u><strong>$1</strong></u>");
            secondary = regExp.replace(secondary, "<u><strong>$1</strong></u>");
        }
    }

    @Override
    public int compareTo(InteractorSearchResult o) {
        return accession.compareTo(o.accession);
    }


}

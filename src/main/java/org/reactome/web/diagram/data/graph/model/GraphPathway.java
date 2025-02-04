package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphPathway extends GraphEvent {

    private Double percentage;
    private EntityStatistics statistics;

    public GraphPathway(EntityNode node) {
        super(node);
    }

    public Double getPercentage() {
        return percentage;
    }

    public EntityStatistics getStatistics() {
        return statistics;
    }

    public boolean isHit() {
        return percentage!=null && percentage>0.0;
    }

    public void setIsHit(Double percentage, List<Double> expression, EntityStatistics statistics){
        this.percentage = percentage;
        this.expression = expression;
        this.statistics = statistics;
    }

    public void resetHit(){
        this.percentage = null;
        this.expression = null;
        this.statistics = null;
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.pathway();
    }
}

package org.reactome.web.diagram.renderers.helper;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.MapSet;

import java.util.*;

/**
 * Items are distributed at two different levels. The most generic one is at the level of
 * RenderableClass. We want to iterate all of them at the time to minimise the number of
 * times the associated renderer needs to be changed.
 *
 * On the other hand, every RenderableClass group will contain different item types which
 * requires changes in the canvas Context2d. By keeping them in the same set, and drawing
 * one after the other, we minimise the number of times the Context2d properties are set.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ItemsDistribution {

    private Map<String, MapSet<RenderType, DiagramObject>> map;

    public ItemsDistribution(Collection<DiagramObject> items, AnalysisType analysisType) {
        this.map = new HashMap<>();
        for (DiagramObject item : items) {
            add(item, analysisType);
        }
    }

    public void add(DiagramObject item, AnalysisType analysisType) {
        if (item != null) {
            String renderableClass = item.getRenderableClass();
            if (item.getIsFadeOut() != null) {
                getOrCreate(renderableClass).add(RenderType.FADE_OUT, item);
            } else {
                if (analysisType.equals(AnalysisType.NONE)) {
                    if (item.getIsDisease() != null) {
                        getOrCreate(renderableClass).add(RenderType.DISEASE, item);
                    } else {
                        getOrCreate(renderableClass).add(RenderType.NORMAL, item);
                    }
                } else {
                    GraphObject dbObject = item.getGraphObject();
                    boolean isHit = false;
                    if (dbObject != null) {
                        if (dbObject instanceof GraphPhysicalEntity) {
                            GraphPhysicalEntity pe = (GraphPhysicalEntity) dbObject;
                            isHit = pe.isHit();
                            if (pe.isInteractorsHit()) {
                                getOrCreate(renderableClass).add(RenderType.HIT_INTERACTORS, item);
                            }
                        }else if (dbObject instanceof GraphPathway) {
                            isHit = ((GraphPathway) dbObject).isHit();
                        }
                        if (isHit) {
                            switch (analysisType) {
                                case OVERREPRESENTATION:
                                case SPECIES_COMPARISON:
                                    if(item.getIsDisease()!=null) {
                                        getOrCreate(renderableClass).add(RenderType.HIT_BY_ENRICHMENT_DISEASE, item);
                                    }else{
                                        getOrCreate(renderableClass).add(RenderType.HIT_BY_ENRICHMENT_NORMAL, item);
                                    }
                                    break;
                                case EXPRESSION:
                                case GSVA:
                                case GSA_STATISTICS:
                                case GSA_REGULATION:
                                    if(item.getIsDisease()!=null) {
                                        getOrCreate(renderableClass).add(RenderType.HIT_BY_EXPRESSION_DISEASE, item);
                                    }else{
                                        getOrCreate(renderableClass).add(RenderType.HIT_BY_EXPRESSION_NORMAL, item);
                                    }
                                    break;
                            }
                        }else{
                            if(item.getIsDisease()!=null) {
                                getOrCreate(renderableClass).add(RenderType.NOT_HIT_BY_ANALYSIS_DISEASE, item);
                            }else{
                                getOrCreate(renderableClass).add(RenderType.NOT_HIT_BY_ANALYSIS_NORMAL, item);
                            }
                        }
                    } else {
                        if(item.getIsDisease()!=null) {
                            getOrCreate(renderableClass).add(RenderType.NOT_HIT_BY_ANALYSIS_DISEASE, item);
                        }else{
                            getOrCreate(renderableClass).add(RenderType.NOT_HIT_BY_ANALYSIS_NORMAL, item);
                        }
                    }
                }
            }
        }
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public MapSet<RenderType, DiagramObject> getItems(String renderableClass) {
        return map.get(renderableClass);
    }

    public Set<DiagramObject> getAll(String renderableClass) {
        Set<DiagramObject> rtn = new HashSet<>();
        MapSet<RenderType, DiagramObject> target = map.get(renderableClass);
        if (target != null) {
            for (RenderType type : RenderType.values()) {
                Set<DiagramObject> items = target.getElements(type);
                if (items != null) {
                    rtn.addAll(items);
                }
            }
        }
        return rtn;
    }

    private MapSet<RenderType, DiagramObject> getOrCreate(String renderableClass) {
        MapSet<RenderType, DiagramObject> map = this.map.get(renderableClass);
        if (map == null) {
            map = new MapSet<>();
            this.map.put(renderableClass, map);
        }
        return map;
    }
}

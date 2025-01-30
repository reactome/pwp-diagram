package org.reactome.web.diagram.thumbnail.diagram;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.thumbnail.diagram.render.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ThumbnailRendererManager {

    private static final ThumbnailRendererManager manager = new ThumbnailRendererManager();

    private final Map<String, ThumbnailRenderer> thumbnailMap = new HashMap<>();

    public ThumbnailRendererManager() {
        initialiseRenderers();
    }

    public static ThumbnailRendererManager get() {
        return manager;
    }

    public ThumbnailRenderer getRenderer(DiagramObject item){
        if(item==null) return null;
        return thumbnailMap.get(item.getRenderableClass());
    }

    private void initialiseRenderers(){
//        thumbnailMap.put("OrgGkRenderNote", new NoteThumbnailRenderer());
        thumbnailMap.put("Compartment",     new CompartmentThumbnailRenderer());
        thumbnailMap.put("Protein",         new ProteinThumbnailRenderer());
        thumbnailMap.put("Cell",            new CellThumbnailRenderer());
        thumbnailMap.put("ProteinDrug",     new ProteinDrugThumbnailRenderer());
        thumbnailMap.put("Chemical",        new ChemicalThumbnailRenderer());
        thumbnailMap.put("ChemicalDrug",    new ChemicalDrugThumbnailRenderer());
        thumbnailMap.put("Reaction",        new ReactionThumbnailRenderer());
        thumbnailMap.put("Complex",         new ComplexThumbnailRenderer());
        thumbnailMap.put("ComplexDrug",     new ComplexDrugThumbnailRenderer());
        thumbnailMap.put("Entity",          new OtherEntityThumbnailRenderer());
        thumbnailMap.put("EntitySet",       new SetThumbnailRenderer());
        thumbnailMap.put("EntitySetDrug",   new SetDrugThumbnailRenderer());
        thumbnailMap.put("ProcessNode",     new ProcessNodeThumbnailRenderer());
        thumbnailMap.put("FlowLine",        new FlowlineThumbnailRenderer());
        thumbnailMap.put("Gene",            new GeneThumbnailRenderer());
        thumbnailMap.put("RNA",             new RNAThumbnailRenderer());
//        aux = new LinkThumbnailRenderer();
//        thumbnailMap.put("EntitySetAndMemberLink", aux);
//        thumbnailMap.put("EntitySetAndEntitySetLink", aux);
    }
}

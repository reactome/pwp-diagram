package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.pwp.model.client.util.LruCache;

import java.util.Set;

/**
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FlagStatus  {

    private LruCache<String, Set<DiagramObject>> flagged = new LruCache<>(5);

    public void setFlagged(String term, Set<DiagramObject> flagged){
        this.flagged.put(term, flagged);
    }

    public Set<DiagramObject> getFlagged(String term) {
        return flagged.get(term);
    }
}

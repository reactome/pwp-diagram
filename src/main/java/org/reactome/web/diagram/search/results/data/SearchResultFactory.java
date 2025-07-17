package org.reactome.web.diagram.search.results.data;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.search.results.data.model.*;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SearchResultFactory {

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<Entry> entry();
        AutoBean<TargetTerm> targetTerm();
        AutoBean<SearchResult> diagramSearchResult();
        AutoBean<FacetContainer> facets();
        AutoBean<Occurrences> occurrences();
        AutoBean<SearchSummary> summary();
        AutoBean<SearchError> searchError();
    }

    @SuppressWarnings("Duplicates")
    public static <T> T getSearchObject(Class<T> cls, String json) throws SearchException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new SearchException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }
}

package org.reactome.web.diagram.profiles.analysis.model.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;
import org.reactome.web.diagram.profiles.analysis.model.ProfileGradient;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisProfileFactory {

    public static <T> T getModelObject(Class<T> cls, String json) throws AnalysisProfileException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new AnalysisProfileException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<AnalysisProfile> profile();
        AutoBean<ProfileGradient> profileGradient();
    }
}

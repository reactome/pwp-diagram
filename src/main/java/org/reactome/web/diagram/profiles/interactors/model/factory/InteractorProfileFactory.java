package org.reactome.web.diagram.profiles.interactors.model.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.profiles.diagram.model.factory.DiagramProfileException;
import org.reactome.web.diagram.profiles.interactors.model.InteractorProfile;
import org.reactome.web.diagram.profiles.interactors.model.InteractorProfileNode;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorProfileFactory {

    public static <T> T getModelObject(Class<T> cls, String json) throws DiagramProfileException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new DiagramProfileException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<InteractorProfile> profile();
        AutoBean<InteractorProfileNode> profileNode();
    }
}

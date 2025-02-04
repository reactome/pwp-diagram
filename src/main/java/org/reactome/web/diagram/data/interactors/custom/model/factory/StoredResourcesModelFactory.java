package org.reactome.web.diagram.data.interactors.custom.model.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import org.reactome.web.diagram.data.interactors.custom.model.CustomResource;
import org.reactome.web.diagram.data.interactors.custom.model.CustomResources;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class StoredResourcesModelFactory {
    public static <T> T getModelObject(Class<T> cls, String json) throws StoredResourcesModelException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new StoredResourcesModelException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }

    public static <T> String serialiseModelObject(T input) throws StoredResourcesModelException {
        try{
            // Retrieve the AutoBean controller
            AutoBean<T> bean = AutoBeanUtils.getAutoBean(input);
            return AutoBeanCodex.encode(bean).getPayload();
        }catch (Throwable e){
            throw new StoredResourcesModelException("Error serializing to json string for [" + input + "] ", e);
        }
    }

    public static <T> T create(Class<T> cls) throws StoredResourcesModelException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            T myBean = factory.create(cls).as();
            return myBean;
        }catch (Throwable e){
            throw new StoredResourcesModelException("Error creating bean for [" + cls + "] ", e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<CustomResource> customResource();
        AutoBean<CustomResources> customResources();
    }
}

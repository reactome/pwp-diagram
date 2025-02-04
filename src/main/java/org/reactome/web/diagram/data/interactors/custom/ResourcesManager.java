package org.reactome.web.diagram.data.interactors.custom;

import org.reactome.web.diagram.data.interactors.custom.model.CustomResource;
import org.reactome.web.diagram.data.interactors.custom.model.CustomResources;
import org.reactome.web.diagram.data.interactors.custom.model.factory.StoredResourcesModelException;
import org.reactome.web.diagram.data.interactors.custom.model.factory.StoredResourcesModelFactory;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.storage.StorageSolutionFactory;
import org.reactome.web.diagram.util.storage.solutions.StorageSolution;

import java.util.*;

/**
 * This class manages all custom resources and deals with choosing
 * between HTML5 and cookie storage.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class ResourcesManager {

    static {
        instance = new ResourcesManager();
    }

    private static final String CUSTOM_RESOURCES_KEY = "Reactome.Diagram.CustomResources";

    private static ResourcesManager instance;
    private StorageSolution storageSolution;

    private Map<String, CustomResource> resources;  // token -> custom resource

    private ResourcesManager() {
        storageSolution = StorageSolutionFactory.getStorage();
        this.resources = loadItems(CUSTOM_RESOURCES_KEY);
    }

    public static ResourcesManager get() {
        if (instance == null) {
            throw new RuntimeException("Resources Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return instance;
    }

    public Collection<CustomResource> getResources() {
        return resources.values();
    }

    public CustomResource getResource(String token) {
        return resources.get(token);
    }

    public List<String> getResourceNames() {
        List<String> rtn = new LinkedList<>();
        if (!resources.isEmpty()) {
            for (CustomResource resource : resources.values()) {
                rtn.add(resource.getName());
            }
        }
        Collections.sort(rtn);
        return rtn;
    }

    public List<String> getResourceTokens() {
        List<String> rtn;
        if (resources.isEmpty()) {
            rtn = new LinkedList<>();
        } else {
            rtn = new LinkedList<>(resources.keySet());
        }
        return rtn;
    }

    public void createAndAddResource(String name, String token, String filename) {
        try {
            CustomResource cr = StoredResourcesModelFactory.create(CustomResource.class);
            cr.setName(name);
            cr.setToken(token);
            cr.setFilename(filename);

            resources.put(cr.getToken(), cr);
            saveItems(CUSTOM_RESOURCES_KEY, resources.values());
        } catch (StoredResourcesModelException e) {
            e.printStackTrace();
        }
    }

    public void deleteResource(String token) {
        CustomResource toBeDeleted = resources.remove(token);
        if (toBeDeleted != null) {
            saveItems(CUSTOM_RESOURCES_KEY, resources.values());
        }
    }

    public void deleteAllResources() {
        resources = new HashMap<>();
        saveItems(CUSTOM_RESOURCES_KEY, resources.values());
    }

    /***
     * Load all locally stored resources/tuples (if any)
     */
    private Map<String, CustomResource> loadItems(String key) {
        Map<String, CustomResource> rtn = new HashMap<>();

        if (storageSolution != null) {
            String json = storageSolution.read(key);
            if (json != null) {
                try {
                    CustomResources resources = StoredResourcesModelFactory.getModelObject(CustomResources.class, json);
                    for (CustomResource customResource : resources.getCustomResources()) {
                        rtn.put(customResource.getToken(), customResource);
                    }
                } catch (StoredResourcesModelException e) {
                    //TODO propagate the exception upwards
                    Console.info("Failed to load locally stored custom items");
                    e.printStackTrace();
                }
            }
        }
        return rtn;
    }

    /***
     * Persist resources/tuples to local storage
     */
    private void saveItems(String key, Collection<CustomResource> resources) {
        if (storageSolution != null) {
            try {
                CustomResources container = StoredResourcesModelFactory.create(CustomResources.class);
                container.setCustomResources(new LinkedList<>(resources));

                String json = StoredResourcesModelFactory.serialiseModelObject(container);
                storageSolution.write(key, json);
            } catch (StoredResourcesModelException e) {
                e.printStackTrace();
            }
        }
    }
}

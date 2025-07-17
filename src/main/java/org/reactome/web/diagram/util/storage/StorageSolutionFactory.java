package org.reactome.web.diagram.util.storage;

import com.google.gwt.storage.client.Storage;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.storage.solutions.CookieStorage;
import org.reactome.web.diagram.util.storage.solutions.HTML5Storage;
import org.reactome.web.diagram.util.storage.solutions.StorageSolution;

/**
 * This class checks whether there is HTML5 Storage support by the browser
 * and returns the appropriate StorageSolution.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class StorageSolutionFactory {
    public static StorageSolution getStorage(){
        StorageSolution storageSolution = null;
        if(Storage.getLocalStorageIfSupported() != null) {
            storageSolution = new HTML5Storage();              //HTML5 storage supported
        } else {
            Console.info("HTML5 storage is not supported by the browser. Using Cookies instead.");
            storageSolution = new CookieStorage();             //HTML5 storage NOT supported, falling back to cookies
        }
        return storageSolution;
    }
}

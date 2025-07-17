package org.reactome.web.diagram.util.storage.solutions;

import com.google.gwt.storage.client.Storage;

/**
 * HTML5 based storage supporting low lever read/write operations
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class HTML5Storage implements StorageSolution {

    private Storage stockStore = null;

    public HTML5Storage() {
        stockStore = Storage.getLocalStorageIfSupported();
    }

    @Override
    public String read(String key) {
        return stockStore.getItem(key);
    }

    @Override
    public void write(String key, String value) {
        stockStore.setItem(key, value);
    }

    @Override
    public void delete(String key) {
        stockStore.removeItem(key);
    }
}

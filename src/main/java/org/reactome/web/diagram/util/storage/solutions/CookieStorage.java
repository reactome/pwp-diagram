package org.reactome.web.diagram.util.storage.solutions;

import com.google.gwt.user.client.Cookies;

/**
 * Cookie based storage supporting low lever read/write operations
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class CookieStorage implements StorageSolution {
    @Override
    public String read(String key) {
        return Cookies.getCookie(key);
    }

    @Override
    public void write(String key, String value) {
        Cookies.setCookie(key, value);
    }

    @Override
    public void delete(String key) {
        Cookies.removeCookie(key);
    }
}

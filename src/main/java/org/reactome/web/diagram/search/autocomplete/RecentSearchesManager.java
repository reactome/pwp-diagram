package org.reactome.web.diagram.search.autocomplete;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import org.reactome.web.diagram.util.storage.StorageSolutionFactory;
import org.reactome.web.diagram.util.storage.solutions.StorageSolution;

import java.util.LinkedList;
import java.util.List;


/**
 * This class manages all recent searches and deals with choosing
 * between HTML5 and cookie storage.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class RecentSearchesManager {

    static {
        instance = new RecentSearchesManager();
    }

    private static RecentSearchesManager instance;

    private static final String RECENT_SEARCHES_KEY = "pwp-search-recent-searches";

    /**
     * Number of recent search items stored locally
     */
    private int CAPACITY = 5;

    private StorageSolution storageSolution;

    private LinkedList<String> recentItems;

    private RecentSearchesManager() {
        storageSolution = StorageSolutionFactory.getStorage();
        this.recentItems = loadItems(RECENT_SEARCHES_KEY);
    }

    public static RecentSearchesManager get() {
        if (instance == null) {
            throw new RuntimeException("Resources Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return instance;
    }

    public List<String> getRecentItems() {
        return recentItems;
    }

    public void insert(String item) {
        if(item == null || item.isEmpty() || recentItems.contains(item)) return;

        if (recentItems.size() == CAPACITY) {
            recentItems.removeLast();
        }
        recentItems.addFirst(item);
        saveItems(RECENT_SEARCHES_KEY, recentItems);
    }

    public void clear() {
        recentItems = new LinkedList<>();
        storageSolution.delete(RECENT_SEARCHES_KEY);
    }

    public void removeItemByIndex(int index) {
        recentItems.remove(index);
        saveItems(RECENT_SEARCHES_KEY, recentItems);
    }

    /***
     * Load all locally stored searches (if any)
     */
    private LinkedList<String> loadItems(String key) {
        LinkedList<String> rtn = new LinkedList<>();
        if (storageSolution != null) {
            String json = storageSolution.read(key);
            if (json != null) {
                rtn = toList(json);
            }
        }
        return rtn;
    }

    /***
     * Persist recent searches to local storage
     */
    private void saveItems(String key, LinkedList<String> items) {
        if (storageSolution != null) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i <items.size(); i++) {
                JSONString jsonString = new JSONString(items.get(i));
                jsonArray.set(i, jsonString);
            }

            storageSolution.write(key, jsonArray.toString());
        }
    }

    private static LinkedList<String> toList(String jsonStr) {
        JSONValue parsed = JSONParser.parseStrict(jsonStr);
        JSONArray jsonArray = parsed.isArray();

        if (jsonArray == null) {
            return new LinkedList<>();
        }

        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONValue jsonValue = jsonArray.get(i);
            JSONString jsonString = jsonValue.isString();
            String stringValue = (jsonString == null) ? jsonValue.toString() : jsonString.stringValue();
            list.add(stringValue);
        }

        return list;
    }
}

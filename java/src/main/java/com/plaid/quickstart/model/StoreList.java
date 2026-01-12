package com.plaid.quickstart.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

/**
 * Represents a list of stores that will be attributed to
 * the user's expenses. Allows the user to perform operations based
 * on store, such as most frequently purchased, highest-spending, and more.
 */
public class StoreList implements Writable {
    private List<Store> storeList;
    


    // constructs an empty store list
    public StoreList() {
        storeList = new ArrayList<Store>();
    }

    // REQUIRES: store not already in list (maybe throw exception later)
    // MODIFIES: this
    // EFFECTS: adds a Store to the store list
    public void addStore(Store store) {
        if (!storeList.contains(store)) {
            storeList.add(store);
        }
    }


    // MODIFIES: this
    // EFFECTS: removes a Store from the store list
    public void removeStore(Store store) {
        if (storeList.contains(store)) {
            storeList.remove(store);
        }
    }


    // REQUIRES: store list not empty
    // EFFECTS: returns store most frequently bought from. if multiple, return last
    public Store getMostFrequentStore() { 
        Store mostFrequent = null;
        int mostPurchases = 0;
        List<Store> filteredStoreList = new ArrayList<>();
        for (Store s: storeList) {
            if (!s.getName().equals("N/A")) {
                filteredStoreList.add(s);
            }
        }
        for (Store s: filteredStoreList) {
            if (s.getNumPurchases() >= mostPurchases) {
                mostFrequent = s;
                mostPurchases = s.getNumPurchases();
            }
        }
        EventLog.getInstance().logEvent(new Event("Viewed summaries."));
        return mostFrequent;
    }



    // REQUIRES: store list not empty
    // EFFECTS: returns store most money spent at. if multiple, return last 
    public Store getMostSpentStore() { 
        Store mostSpent = null;
        Double totalSpent = 0.0;
        List<Store> filteredStoreList = new ArrayList<>();
        for (Store s: storeList) {
            if (!s.getName().equals("N/A")) {
                filteredStoreList.add(s);
            }
        }
        for (Store s: filteredStoreList) {
            if (s.getTotalSpent() >= totalSpent) {
                mostSpent = s;
                totalSpent = s.getTotalSpent();
            }
        }
        return mostSpent;
    }

    // will add more methods like summaries, averages later

    // MODIFIES: store s
    // EFFECTS: resets number of purchases and total spent on each category to 0
    public void resetStores() {
        for (Store s: storeList) {
            s.resetStore();
        }
    }




    // getters
    public List<Store> getStoreList() {
        return storeList;
    }

    // EFFECTS: returns a Store with the given name, or null if not found
    public Store getStoreUsingName(String name) {
        for (Store s: storeList) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }


    // methods for saving 

    // EFFECTS: returns this as JSON object
    @Override
    public JSONObject toJson() {
        JSONObject jsonSL = new JSONObject();
        jsonSL.put("storeList", storesToJson());

        return jsonSL;
        
    }

    // EFFECTS: returns stores in this storelist as a JSON array
    private JSONArray storesToJson() {
        JSONArray storeArray = new JSONArray();

        for (Store s : storeList) {
            storeArray.put(s.toJson());
        }

        return storeArray;
    }
    

}

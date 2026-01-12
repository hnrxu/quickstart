package com.plaid.quickstart.model;

import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

/**
 * Represents a Store that the user has purchased from.
 * A Store includes:
 * - name
 * - purchase category
 * - store type (ex. online, physical, franchise)
 * - location (city, address, etc.)
 * - an optional budget
 * - number of purchases made
 * - total amount spent at the store
 * This class can be used to track expenses, group them by store and 
 * purchase category, and enforce budgets for specific stores.
 */ 
public class Store implements Writable {
    private String name;
    private Category category;
    private String storetype; // will make into enum later
    private String location;// might change this to be more specific later
    private Double budget;
    private int numPurchases;
    private Double totalSpent;


    // EFFECTS: makes a store with name, category, 0 purchases made and 0 spent
    public Store(String name, Category category) {
        this.name = name;
        this.numPurchases = 0;
        this.totalSpent = 0.0;
        this.category = category;
        
    }


    // REQUIRES: budget >=0
    // MODIFIES: this
    // EFFECTS: changes any fields of the store that are not null except num purchases
    public void updateStore(String name, Category category, String storetype, String city, Double budget) {
        if (name != null) {
            this.name = name;
        }
        if (category != null) {
            this.category = category;
        }
        if (storetype != null) {
            this.storetype = storetype;
        }
        if (city != null) {
            this.location = city;
        }
        if (budget != null) {
            this.budget = budget;
        }
    
    }

    

    // REQUIRES: budget >= 0
    // MODIFIES: this
    // EFFECTS: sets a budget for this store
    public void makeStoreBudget(Double budget) {
        this.budget = budget;
    }

    // MODIFIES: this
    // EFFECTS: adds 1 to number of purchases at the store 
    public void addPurchase() {
        numPurchases++;
        this.category.addPurchase();
        
    }

    // REQUIRES: number of purchases > 0
    // MODIFIES: this
    // EFFECTS: subtracts 1 to number of purchases at the store 
    public void removePurchase() {
        numPurchases--;
        this.category.removePurchase();
    }

    // MODIFIES: this
    // EFFECTS: adds given amount to total spent at the store
    public void increaseTotalSpent(Double amount) {
        if (amount < 0) {
            amount = amount * -1;
        }
        totalSpent += amount;
        this.category.increaseTotalSpent(amount);
    }

    // REQUIRES: total amount >= given amount
    // MODIFIES: this
    // EFFECTS: subtracts given amount to total spent at the store
    public void decreaseTotalSpent(Double amount) {
        totalSpent -= amount;
        this.category.decreaseTotalSpent(amount);
    }

    // REQUIRES: numPurchases > 0
    // EFFECTS: returns average amount spent per purchase
    public Double averageAmount() {
        return totalSpent / numPurchases;
    }

    // MODIFIES: this
    // EFFECTS: sets nuber of purchases and total spent to 0
    public void resetStore() {
        this.totalSpent = 0.0;
        this.numPurchases = 0;
    }



    // helper setters for saving
    // MODIFIES: this
    // EFFECTS: sets nuber of purchases to given 
    public void setNumPurchases(int purchases) {
        this.numPurchases = purchases;
    } 

    public void setTotalSpent(Double total) {
        this.totalSpent = total;
    } 


    // getters
    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getType() {
        return storetype;
    }

    public String getLocation() {
        return location;
    }

    public Double getBudget() {
        return budget;
    }

    public int getNumPurchases() {
        return numPurchases;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }


    // methods for saving

    // EFFECTS: returns this as JSON object
    @Override
    public JSONObject toJson() {
        JSONObject jsonStore = new JSONObject();
        jsonStore.put("name", name);
        jsonStore.put("category", category.toJson());
        jsonStore.put("storeType", storetype);
        jsonStore.put("location", location);
        jsonStore.put("numPurchases", numPurchases);
        jsonStore.put("totalSpent", totalSpent);
        if (budget != null) {
            jsonStore.put("budget", budget);
        }

       
        return jsonStore;

    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Store other = (Store) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        return true;
    }
   


}

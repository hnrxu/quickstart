package com.plaid.quickstart.model;

import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

/**
 * Represents a Category that the user has purchased from.
 * A Category includes:
 * -name
 * -total spent in that category
 * -number of purchases 
 * -optional budget

 * This class can be used to track expenses, group them by 
 * category, and enforce budgets for specific categories.
 */ 
public class Category implements Writable {
    private String name;
    private Double budget;
    private Double totalSpent;
    private int numPurchases;

    // EFFECTS: constructs a category with no purchases and none spent 
    public Category(String name) {
        this.name = name;
        this.totalSpent = 0.0;
        this.numPurchases = 0;

    }


    // MODIFIES: this
    // EFFECTS: adds 1 to number of purchases at the store 
    public void addPurchase() {
        numPurchases++;
    }

    // REQUIRES: number of purchases > 0
    // MODIFIES: this
    // EFFECTS: removes 1 to number of purchases at the store 
    public void removePurchase() {
        numPurchases--;
    }


    // MODIFIES: this
    // EFFECTS: adds given amount to total spent at the store
    public void increaseTotalSpent(Double amount) {
        totalSpent += amount;
    }

    // REQUIRES: total spent >= given amount
    // MODIFIES: this
    // EFFECTS: subtracts given amount to total spent at the store
    public void decreaseTotalSpent(Double amount) {
        totalSpent -= amount;
    }

    // REQUIRES: numPurchases > 0
    // EFFECTS: returns average amount spent per purchase
    public Double averageAmount() {
        return totalSpent / numPurchases;
    }

    // setters

    // REQUIRES: budget >= 0
    // MODIFIES: this
    // EFFECTS: sets a budget for this category
    public void makeCategoryBudget(Double budget) {
        this.budget = budget;
    }

    // MODIFIES: this
    // EFFECTS: sets nuber of purchases and total spent to 0
    public void resetCategory() {
        this.totalSpent = 0.0;
        this.numPurchases = 0;
    }


    // helper setters used for saving
    
    // MODIFIES: this
    // EFFECTS: sets nuber of purchases to given (only used as helper)
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
    
    public Double getBudget() {
        return budget;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public int getNumPurchases() {
        return numPurchases;
    }


    // methods for saving

    // EFFECTS: returns this as JSON object
    @Override
    public JSONObject toJson() {
        JSONObject jsonCategory = new JSONObject();
        jsonCategory.put("name", name);
        jsonCategory.put("numPurchases", numPurchases);
        jsonCategory.put("totalSpent", totalSpent);
        if (budget != null) {
            jsonCategory.put("budget", budget);
        }

       
        return jsonCategory;

    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Category other = (Category) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }


}

package com.plaid.quickstart.model;

import java.time.LocalDate;

import org.json.JSONObject;



/**
 * Represents an expense transaction in the user's finance log.
 * An Expense is a type of Transaction where money is spent,
 * and it includes additional details specific to spending,
 * such as the store where the purchase was made. Each expense 
 * has a negative amount.
 */
public class Expense extends Transaction {
    private Store store;
    private StoreList storeList;
    
    // REQUIRES: amount > 0
    // EFFECTS: Creates an expense transaction with description, date, 
    //negative amount and storename
    // if the store is in storelist, adds 1 purchase to that store, 
    // adds amount to the total amount spent at the store
    // if the store does not exist, create a new store, add to storelist, and 
    // update accordingly
    public Expense(String description, Double amount, Store store, Category category, StoreList storeList, CategoryList categoryList) {
        super(description, amount * -1);
        EventLog.getInstance().logEvent(new Event("Expense added."));
        this.storeList = storeList;
        for (Store s: this.storeList.getStoreList()) {
            if (s.equals(store)) {
                this.store = s;
                categoryList.addCategory(s.getCategory());
                s.addPurchase();
                s.increaseTotalSpent(amount);
                return;  
            }
            
        } 
        this.store = store;
        categoryList.addCategory(store.getCategory());
        this.storeList.addStore(store);
        this.store.addPurchase();
        this.store.increaseTotalSpent(amount); 
    }

    // EFFECTS: Summarizes and returns the info in specific format for this transaction
    @Override
    public String transactionInfo() {
        String summary = description 
                        + " --- " + getFormattedAmount() + "\n" 
                        + "Date: " + getDate() + "\n" 
                        + "From: " + store.getName() + "\n" 
                        + "Type: " + store.getCategory().getName() + "\n" 
                        + "Payment Method: " + getPaymentMethod() + "\n" 
                        + "Additional Information: " + getComments();
        return summary;
    }

    /** 
    MODIFIES: this
    EFFECTS: changes any fields of the transaction that are not null
    if storename & category are null, does nothing
    if storename not null, category null, & store already exists in storelist:
        a) amount null: 
        - add purchase and previous amount to existing store
        - subtract purchase and previous amount from previous store
        b) amount not null: 
        - add purchase and given amount to existing store
        - subtract purchase and previous amount from previous store
    if storename not null, category not null, & store already exists in storelist:
        - this option will never happen because if the store exists, the user 
        will not be prompted for category
    if storename not null, category null, & store does not exist in storeList:
        a) amount null: 
        - make a new store with given name & previous category
        - add new store to storeList
        - add purchase and previous amount to this store
        - subtract purchase and previous amount from previous store
        b) amount not null: 
        - make a new store with given name & previoius category
        - add new store to storeList
        - add purchase and given amount to existing store
        - subtract purchase and previous amount from previous store
    if storename not null, category not null, & store does not exist in storeList:
        a) amount null: 
        - make a new store with given name & category
        - add new store to storeList
        - add purchase and previous amount to this store
        - subtract purchase and previous amount from previous store
        b) amount not null: 
        - make a new store with given name & category
        - add new store to storeList
        - add purchase and given amount to existing store
        - subtract purchase and previous amount from previous store
    */
    
    public void updateExpense(String description, 
                                LocalDate date, 
                                Double amount, 
                                String paymentMethod, 
                                String comments, 
                                String storeName,
                                Category category) {

        Double oldAmount = this.amount;
        Category oldCategory = this.store.getCategory();

        if (description != null) {
            this.description = description;
        }
        if (date != null) {
            this.date = date;
        }
        if (amount != null) {
            this.amount = amount * -1;
        }
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod;
        }
        if (comments != null) {
            this.comments = comments;
        }
        if (storeName != null) {
            updateStoreAction(oldAmount, amount, oldCategory, category, storeName);
        }
    }

    // helpers
    private void updateStoreAction(Double oldAmount,
                                Double newAmount,
                                Category oldCategory,
                                Category newCategory,
                                String newStoreName) {
        // deletes stuff from old store
        deleteOldInfo(oldAmount);

        // behaviour if store alr exists
        for (Store s: storeList.getStoreList()) {
            if (s.getName().equals(newStoreName)) {
                this.store = s; // sets store for expense
                s.addPurchase(); // adds purchase to store
                
                // if amt not null, increase by updated amt
                // if amt is null, increase by previous amt
                selectAmount(newAmount); 
                return;  
            }
        } 

        // behaviour if store doesn't exist
        if (newCategory != null) {
            this.store = new Store(newStoreName, newCategory);
        } else {
            this.store = new Store(newStoreName, oldCategory);
        }
        this.storeList.addStore(this.store);
        this.store.addPurchase();
        
        // if amt not null, increase by updated amt
        // if amt is null, increase by previous amt
        selectAmount(newAmount);
        

    }

    private void deleteOldInfo(Double oldAmount) {
        this.store.removePurchase();
        this.store.decreaseTotalSpent(oldAmount * -1);
    }

    private void selectAmount(Double amount) {
        if (amount != null) {
            this.store.increaseTotalSpent(amount);
        } else {
            this.store.increaseTotalSpent(this.amount * -1);
        }
    }

    
    // helpr setters for saving 
    public void setNegativeAmount() {
        if (this.amount > 0) {
            this.amount = this.amount * -1;
        } 
    }

    // getters
    public Store getStore() {
        return store;
    }

    public StoreList getStoreList() {
        return storeList;
    }

    


    // methods for saving 
    public void toJsonExpense(JSONObject jsonExpense) {
        jsonExpense.put("store", store.toJson());
        jsonExpense.put("storeList", storeList.toJson());
        jsonExpense.put("transactionType", "expense");
    }



    
}

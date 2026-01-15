package com.plaid.quickstart.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Represents a log of all financial transactions made by the user.
 * A TransactionLog stores all Transaction objects, including
 * Expenses and Incomes. Includes a list of stores the user has
 * shopped from and a list of categories the user has shopped from.
 * It allows the user to add, remove, and view transactions, as well as 
 * filter them by date, category, store, and more. It can also
 * calculate financial summaries such as total income, total expenses, 
 * and net balance. It is the central structure for the financial tracker.
 */
public class TransactionLog implements Writable {
    private List<Transaction> transactionLog;
    private Double totalIncome;
    private Double totalExpenses;
    private StoreList storeList;
    private CategoryList categoryList; 

    // creates an empty transaction log with empty storelist and categorylist, total income and expenses 0
    public TransactionLog() {
        transactionLog = new ArrayList<Transaction>();
        this.totalIncome = 0.0;
        this.totalExpenses = 0.0;
        this.storeList = new StoreList();
        this.categoryList = new CategoryList();
        

    }

    // REQUIRES: transaction not already in log (maybe throw exception later)
    // MODIFIES: this
    // EFFECTS: adds a transaction to the transaction log, increases total expenses or income
    // based on transaction amount
    public void addTransaction(Transaction transaction) {
        transactionLog.add(transaction);
        if (transaction instanceof Expense) {
            totalExpenses += transaction.getAmount() * -1; // makes total expenses positive
        } else if (transaction instanceof Income) {
            totalIncome += transaction.getAmount();
        }
        // left as else if because i originally planned to add a savings class as well,
        // but we will see if there's time
    }

    // REQURIES: transaction is in log
    // MODIFIES: this
    // EFFECTS: removes a transaction from the transaction log if it is already there
    public void removeTransaction(Transaction transaction) {
        transactionLog.remove(transaction);

    }


    // REQUIRES: valid category
    // EFFECTS: filters transaction log by store 
    public List<Transaction> filterByStore(Store store) {
        List<Transaction> filteredTransactions = new ArrayList<Transaction>();
        for (Transaction t: transactionLog) {
            if (t instanceof Expense) {
                if (((Expense)t).getStore().equals(store)) {
                    filteredTransactions.add(t);
                }
            }
        }
        EventLog.getInstance().logEvent(new Event("Filtered by store."));
        return filteredTransactions;
    }

    // REQUIRES: valid category
    // EFFECTS: filters transaction log by category 
    public List<Transaction> filterByCategory(Category category) {
        List<Transaction> filteredTransactions = new ArrayList<Transaction>();
        for (Transaction t: transactionLog) {
            if (t instanceof Expense) {
                if (((Expense)t).getStore().getCategory().equals(category)) {
                    filteredTransactions.add(t);
                }
            }
        }
        EventLog.getInstance().logEvent(new Event("Filtered by category."));
        return filteredTransactions;
    }




    // non basic getters
    // may add some more formatting methods later

    // REQUIRES: number of expense transactions > 0, 
    // EFFECTS: returns average spending per transaction
    public Double getAverageSpendingPerTransaction() {
        int numExpenses = 0;
        for (Transaction t: transactionLog) {
            if (t instanceof Expense) {
                numExpenses++;
            }
        }
        return totalExpenses / numExpenses;
    }


    // REQUIRES: number of transactions > 0
    // EFFECTS: returns net balance of account
    public Double getNetBalance() {
        return totalIncome - totalExpenses;
    }

    // REQUIRES: number of expense transactions > 0, storeList not empty
    // EFFECTS: returns store most frequently bought from, if multiple, return last
    public Store getMostFrequentStore() { 
        return storeList.getMostFrequentStore();
    }

    // REQUIRES: number of expense transactions > 0
    // EFFECTS: returns category most frequently bought from, if multiple, return last
    public Category getMostFrequentCategory() { 
        return categoryList.getMostFrequentCategory();
    }

    // REQUIRES: number of expense transactions > 0, storeList not empty
    // EFFECTS: returns store most money spent at, if multiple, return last
    public Store getMostSpentStore() { 
        return storeList.getMostSpentStore();
    }

    // REQUIRES: number of expense transactions > 0, storeList not empty
    // EFFECTS: returns category most money spent at, if multiple, return last
    public Category getMostSpentCategory() { 
        return categoryList.getMostSpentCategory();
    }

    // EFFECTS: returns categories in sotrted list by num purchases
    public List<Category> getSortedFreqCategories() {
       return categoryList.getSortedFreqCategories();
    }

    // EFFECTS: returns categories in sotrted list by total spent
    public List<Category> getSortedAmountCategories() {
       return categoryList.getSortedAmountCategories();
    }

     // EFFECTS: returns categories in sotrted list by num purchases
    public List<Store> getSortedFreqStores() {
       return storeList.getSortedFreqStores();
    }

    // EFFECTS: returns categories in sotrted list by total spent
    public List<Store> getSortedAmountStores() {
       return storeList.getSortedAmountStores();
    }


    // REQUIRES: number of transactions > 0
    // EFFECTS: returns an easily readable form of transactions in the log
    public String getFormattedTransactionLog() {
        String formattedString = "";
        for (Transaction t: transactionLog) {
            formattedString += (t.getDescription() + " ------ " + t.getFormattedAmount() + "\n");
            
        }
        return formattedString;
    }

    // setters (not used outside of helpers for saving)
    public void setStoreList(StoreList storeList) {
        this.storeList = storeList;
    }

    public void setCategoryList(CategoryList categoryList) {
        this.categoryList = categoryList;
    }

    public void setTotalIncome(Double income) {
        this.totalIncome = income;
    }

    public void setTotalExpenses(Double expenses) {
        this.totalExpenses = expenses;
    }


    // getters
    public List<Transaction> getAllTransactions() {
        return transactionLog;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public Double getTotalExpenses() {
        EventLog.getInstance().logEvent(new Event("Viewed totals."));
        return totalExpenses;
    }

    public StoreList getStoreList() {
        return storeList;
    }

    public CategoryList getCategoryList() {
        return categoryList;
    }


    // methods for saving 

    // EFFECTS: returns this as JSON object
    @Override
    public JSONObject toJson() {
        JSONObject jsonTL = new JSONObject();
        jsonTL.put("transactionLog", transactionsToJson());

        jsonTL.put("masterStoreList", storeList.toJson());
        jsonTL.put("masterCategoryList", categoryList.toJson());

        jsonTL.put("totalIncome", totalIncome);
        jsonTL.put("totalExpenses", totalExpenses);

        return jsonTL;
        
    }

    // EFFECTS: returns transactions in this transactionlog as a JSON array
    private JSONArray transactionsToJson() {
        JSONArray transactionArray = new JSONArray();

        for (Transaction t : transactionLog) {
            transactionArray.put(t.toJson());
        }

        return transactionArray;
    }



}

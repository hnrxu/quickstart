package com.plaid.quickstart.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.plaid.quickstart.model.*;

// Represents a reader that reads transaction log from JSON data stored in file
public class JsonReader {
    private String source;

   
    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads transaction log from file and returns it,
    // throws IOException if an error occurs reading data from file
    public TransactionLog readTL() throws IOException {
        String jsonData = readFile(source); // reads the source and resturns as a string
        JSONObject jsonObject = new JSONObject(jsonData); // makes the string a json object
        return parseTransactionLog(jsonObject); // returns a copy of TL with transactions
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) { 
            stream.forEach(s -> contentBuilder.append(s)); // append each stream element to the stringbuilder
        }
        return contentBuilder.toString(); // convert stringbuiilder to string and return
    }

//// ************** parsing entire lists ***************************************////

    // EFFECTS: parses entire transaction log from JSON object and returns it
    // (both the log info and the info for individual transactions)
    private TransactionLog parseTransactionLog(JSONObject jsonTL) {
        //String name = jsonObject.getString("name"); // get the string associated w the key "name"
        JSONObject masterCL = jsonTL.getJSONObject("masterCategoryList"); // gets categorylist as json object
        JSONObject masterSL = jsonTL.getJSONObject("masterStoreList"); // gets storelist as json object

        CategoryList categoryList = parseCategoryList(masterCL); // returns jsonCL as normal CL (all fields ready)
        StoreList storeList = parseStoreList(masterSL, categoryList);  // returns jsonSL as normal SL (all fields ready)

        // makes a transactionlog 
        TransactionLog transactionLog = new TransactionLog(); 
        // adds in SL & CL w correct data
        transactionLog.setCategoryList(categoryList);
        transactionLog.setStoreList(storeList);
        
        // gets transactions and parses them according to their type; adds to transaction log
        JSONArray transactionArray = jsonTL.getJSONArray("transactionLog");
        for (int i = 0; i < transactionArray.length(); i++) {
            JSONObject jsonTransaction = transactionArray.getJSONObject(i);
            Transaction transaction = parseTransaction(jsonTransaction, storeList, categoryList);
            if (transaction instanceof Income) {
                Income income = (Income) transaction;
                transactionLog.addTransaction(income);
            } else {
                Expense expense = (Expense) transaction;
                transactionLog.addTransaction(expense);
            }

        }

        // adds in total income & expenses with correct data
        transactionLog.setTotalIncome(jsonTL.getDouble("totalIncome"));
        transactionLog.setTotalExpenses(jsonTL.getDouble("totalExpenses"));


       
        return transactionLog;
    }

    // EFFECTS: parses entire storelist from JSON object and returns it
    private StoreList parseStoreList(JSONObject jsonSL, CategoryList categoryList) { 
        JSONArray storeArray = jsonSL.getJSONArray("storeList"); // gets the json array associated w key "storeList" 
        StoreList storeList = new StoreList(); // makes a new (empty) storelist 

        // makes each json store in storearray into normal store and adds to normal SL
        for (int i = 0; i < storeArray.length(); i++) {
            JSONObject jsonStore = storeArray.getJSONObject(i);
            Store store = parseStore(jsonStore, categoryList); 
            storeList.addStore(store); 
        }

        return storeList;

    }

    // EFFECTS: parses entire categorylist from JSON object and returns it
    private CategoryList parseCategoryList(JSONObject jsonCL) { 
        JSONArray categoryArray = jsonCL.getJSONArray("categoryList"); 
        CategoryList categoryList = new CategoryList(); 

        // updates fields of each category in CategoryList
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject jsonCategory = categoryArray.getJSONObject(i);
            Category category = parseCategory(jsonCategory);
            categoryList.addCategory(category);
            //parseCategoryFields(jsonCategory, c);
        }
       
        return categoryList;
    }


    //// ******************* parsing items/objects *************** ////
    


    // REQUIRES: transactionType is one of income or expense
    // EFFECTS: parses single transaction from JSON object (using parsed store)
    // ************* note*** it would be nice to throw an exception if the transaction type is not income or expense
    private Transaction parseTransaction(JSONObject jsonTransaction, StoreList storeList, CategoryList categoryList) {
        // gets essential construction fields
        String description = jsonTransaction.getString("description");
        Double amount = jsonTransaction.getDouble("amount");
        String transactionType = jsonTransaction.getString("transactionType");

        // gets fields to be updated
        String id = jsonTransaction.getString("id");
        LocalDate date = LocalDate.parse(jsonTransaction.getString("date"));
    
        String paymentMethod = null;
        String comments = null;
        
         

        // makes income if tTYPE is income
        if (transactionType.equals("income")) {
            // gets income specific fields
            String source = jsonTransaction.getString("source");
            paymentMethod = parsePaymentMethod(jsonTransaction, paymentMethod);
            comments = parseComments(jsonTransaction, comments);

            // makes and updates income
            Income income = new Income(description, amount, source);
            income.setID(id);
            income.updateIncome(null, date, null, paymentMethod, comments, null);
        
            return income;
        
        // makes expense if tTYPE is expense
        } else {
            // gets expense specific fields
            Category category = getCategory(jsonTransaction, categoryList);
            JSONObject jsonStore = jsonTransaction.getJSONObject("store");
            Store store = parseStore(jsonStore, categoryList);
            paymentMethod = parsePaymentMethod(jsonTransaction, paymentMethod);
            comments = parseComments(jsonTransaction, comments);

            // makes and updates expense
            Expense expense = new Expense(description, amount, store, category, storeList, categoryList);
            expense.setID(id);
            expense.updateExpense(null, date, null, paymentMethod, comments, null, null);
            expense.setNegativeAmount(); // must do this because the constructor double flips it back to positive 

            // resets the totals and purchases for categories and stores associated w the expense, because
            // the expense constructor increments these fields
            resetStore(jsonTransaction, expense);
            resetCategory(jsonTransaction, expense);

            return expense;
        }
            
    }

    // EFFECTS: parses single store (using parsed category) and returns it
    private Store parseStore(JSONObject jsonStore, CategoryList categoryList) {
        String name = jsonStore.getString("name"); // gets name of store

        JSONObject jsonCategory = jsonStore.getJSONObject("category"); // gets the jsonstore's jsoncategory object
        String categoryName = jsonCategory.getString("name");
        Category category = categoryList.getCategoryUsingName(categoryName); // gets category of store from CL
        //parseCategoryFields(jsonCategory, category); // updates category fields

        Store store = new Store(name, category); // makes the store
        
        // updates store fields
        String storeType = null;
        String location = null;
        Double budget = null;
        if (jsonStore.has("storeType")) {
            storeType = jsonStore.getString("storeType");
        }
        if (jsonStore.has("location")) {
            location = jsonStore.getString("location");
        }
        if (jsonStore.has("budget")) {
            budget = jsonStore.getDouble("budget");
        }
        store.updateStore(null, null, storeType, location, budget);
        store.setTotalSpent(jsonStore.getDouble("totalSpent"));
        store.setNumPurchases(jsonStore.getInt("numPurchases"));

        return store;

    }


    // EFFECTS: parses category fields based on json data
    private Category parseCategory(JSONObject jsonCategory) {
        String categoryName = jsonCategory.getString("name");
        Category c = new Category(categoryName); // makes the category
        c.setTotalSpent(jsonCategory.getDouble("totalSpent"));
        c.setNumPurchases(jsonCategory.getInt("numPurchases"));
        if (jsonCategory.has("budget")) {
            c.makeCategoryBudget(jsonCategory.getDouble("budget"));
        } else {
            c.makeCategoryBudget(null);
        }

        return c;
    }


    ///****************** helpers ***************************************** */
    
    
    // EFFECTS: helper to retrieve storename from a jsontransaction object
    private Store getStore(JSONObject jsonTransaction, Category category) {
        JSONObject jsonStore = jsonTransaction.getJSONObject("store");
        String storeName = jsonStore.getString("name");
        int numPurchases = jsonStore.getInt("numPurchases");
        Double totalSpent = jsonStore.getDouble("totalSpent");
        Store store = new Store(storeName, category);
        store.setNumPurchases(numPurchases);
        store.increaseTotalSpent(totalSpent);
        return store;
    }

    // EFFECTS: helper to retrieve category from a the jsonstore in a jsontransaction object
    private Category getCategory(JSONObject jsonTransaction, CategoryList categoryList) {
        JSONObject jsonStore = jsonTransaction.getJSONObject("store"); // gets the jsontransaction's jsonstore object
        JSONObject jsonCategory = jsonStore.getJSONObject("category"); // gets the jsonstore's json category object
        String categoryName = jsonCategory.getString("name");
        Category category = categoryList.getCategoryUsingName(categoryName); // gets category of store from CL
        return category;
    }


    // MODIFIES: expense 
    // EFFECTS: helper to reset total spent and num purchases at the expense's store, 
    // since expense creation increments them
    private void resetStore(JSONObject jsonExpense, Expense expense) {
        JSONObject jsonStoreForExpense = jsonExpense.getJSONObject("store");
        Double jsonStoreForExpenseTotal = jsonStoreForExpense.getDouble("totalSpent");
        int jsonStoreForExpensePurchases = jsonStoreForExpense.getInt("numPurchases");
        expense.getStore().setTotalSpent(jsonStoreForExpenseTotal);
        expense.getStore().setNumPurchases(jsonStoreForExpensePurchases);
        
    } 

    // MODIFIES: expense 
    // EFFECTS: helper to reset total spent and num purchases at the expense's store's category, 
    // since expense creation increments them
    private void resetCategory(JSONObject jsonExpense, Expense expense) {
        JSONObject jsonStoreForExpense = jsonExpense.getJSONObject("store");
        JSONObject jsonCategoryForStore = jsonStoreForExpense.getJSONObject("category");
        Double jsonCategoryForStoreTotal = jsonCategoryForStore.getDouble("totalSpent");
        int jsonCategoryForStorePurchases = jsonCategoryForStore.getInt("numPurchases");
        expense.getStore().getCategory().setTotalSpent(jsonCategoryForStoreTotal);
        expense.getStore().getCategory().setNumPurchases(jsonCategoryForStorePurchases);
        
    

    }

    // EFFECTS: helpers to parse optional fields
    private String parsePaymentMethod(JSONObject jsonTransaction, String paymentMethod) {
        if (jsonTransaction.has("paymentMethod")) {
            paymentMethod = jsonTransaction.getString("paymentMethod");
            return paymentMethod;
        }
        return null;     
    }

    private String parseComments(JSONObject jsonTransaction, String comments) {
        if (jsonTransaction.has("comments")) {
            comments = jsonTransaction.getString("comments");
            return comments;
        }
        return null;     
    }




    

}

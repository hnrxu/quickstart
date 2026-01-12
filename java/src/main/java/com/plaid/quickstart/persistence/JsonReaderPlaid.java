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
public class JsonReaderPlaid {
    private String source;

   
    // EFFECTS: constructs reader to read from source file
    public JsonReaderPlaid(String source) {
        this.source = source;
    }

    // EFFECTS: reads transaction log from file and returns it,
    // throws IOException if an error occurs reading data from file
    public TransactionLog readTL() throws IOException {
        //String jsonData = readFile(source); // reads the source and resturns as a string
        JSONObject jsonTransactions = new JSONObject(source); // makes the string a json object
        return parseTransactionLog(jsonTransactions); // returns a copy of TL with transactions
    }


//// ************** parsing entire lists ***************************************////

    // EFFECTS: parses entire transaction log from JSON object and returns it
    // (both the log info and the info for individual transactions)
    private TransactionLog parseTransactionLog(JSONObject JSONtransactions) {
        //String name = jsonObject.getString("name"); // get the string associated w the key "name"
        //JSONObject masterCL = jsonTL.getJSONObject("masterCategoryList"); // gets categorylist as json object
        //JSONObject masterSL = jsonTL.getJSONObject("masterStoreList"); // gets storelist as json object

        //CategoryList categoryList = parseCategoryList(masterCL); // returns jsonCL as normal CL (all fields ready)
        //StoreList storeList = parseStoreList(masterSL, categoryList);  // returns jsonSL as normal SL (all fields ready)

        // makes a transactionlog 
        TransactionLog transactionLog = new TransactionLog(); 
        StoreList storeList = new StoreList();
        CategoryList categoryList = new CategoryList();
        // adds in SL & CL w correct data
        transactionLog.setCategoryList(categoryList);
        transactionLog.setStoreList(storeList);
        
        // get total income and expenses
        Double totalIncome = 0.0;
        Double totalExpenses = 0.0;

        // gets transactions and parses them according to their type; adds to transaction log
        JSONArray transactionArray = JSONtransactions.getJSONArray("latest_transactions");
        for (int i = 0; i < transactionArray.length(); i++) {
            JSONObject jsonTransaction = transactionArray.getJSONObject(i);
            Transaction transaction = parseTransaction(jsonTransaction, storeList, categoryList);
            if (transaction instanceof Income) {
                Income income = (Income) transaction;
                transactionLog.addTransaction(income);
                totalIncome += income.getAmount();
            } else {
                Expense expense = (Expense) transaction;
                transactionLog.addTransaction(expense);
                totalExpenses -= expense.getAmount();
            }

        }

        // adds in total income & expenses with correct data
        transactionLog.setTotalIncome(totalIncome);
        transactionLog.setTotalExpenses(totalExpenses);


       
        return transactionLog;
    }

   

   


    //// ******************* parsing items/objects *************** ////
    


    // REQUIRES: transactionType is one of income or expense
    // EFFECTS: parses single transaction from JSON object (using parsed store)
    // ************* note*** it would be nice to throw an exception if the transaction type is not income or expense
    private Transaction parseTransaction(JSONObject jsonTransaction, StoreList storeList, CategoryList categoryList) {

        // gets transaction detail object
        JSONObject detailsObject = jsonTransaction.getJSONObject("personal_finance_category");
        
        // gets essential construction fields
        String description = jsonTransaction.getString("name");
        Double amount = jsonTransaction.getDouble("amount");

        // gets transaction type
        String transactionType;
        if (amount < 0) {
            transactionType = "income";
        } else {
            transactionType = "expense";
        }
        
        // gets fields to be updated
        String id = jsonTransaction.getString("transaction_id");

        JSONArray dateArray = jsonTransaction.getJSONArray("date");
        LocalDate date = LocalDate.of(dateArray.getInt(0), dateArray.getInt(1), dateArray.getInt(2));

        String paymentMethod = jsonTransaction.getString("payment_channel");
        String comments = null;
        
         

        // makes income if tTYPE is income
        if (transactionType.equals("income")) {
            // gets income specific fields
            String source = jsonTransaction.getString("name");
            //paymentMethod = parsePaymentMethod(jsonTransaction, paymentMethod);
            //comments = parseComments(jsonTransaction, comments);

            // makes and updates income
            Income income = new Income(description, amount * -1, source);
            income.setID(id);
            income.updateIncome(null, date, null, paymentMethod, comments, null);
        
            return income;
        
        // makes expense if tTYPE is expense
        } else {
            // gets expense specific fields
            String storeName = jsonTransaction.optString("merchant_name");
            if (storeName.equals("")) {
                storeName = "N/A";
            }
            String categoryName = detailsObject.getString("primary");
            Category category = new Category(categoryName);
            Store store = new Store(storeName, category);
            
            paymentMethod = parsePaymentMethod(jsonTransaction, paymentMethod);
            comments = parseComments(jsonTransaction, comments);

            // makes and updates expense
            Expense expense = new Expense(description, amount, store, category, storeList, categoryList);
            expense.setID(id);
            expense.updateExpense(null, date, null, paymentMethod, comments, null, null);
            //expense.setNegativeAmount(); // must do this because the constructor double flips it back to positive 

            // resets the totals and purchases for categories and stores associated w the expense, because
            // the expense constructor increments these fields
            //resetStore(jsonTransaction, expense);
            //resetCategory(jsonTransaction, expense);

            return expense;
        }
            
    }

  

   

    ///****************** helpers ***************************************** */
    
    
   

   

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
            paymentMethod = jsonTransaction.getString("payment_channel");
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

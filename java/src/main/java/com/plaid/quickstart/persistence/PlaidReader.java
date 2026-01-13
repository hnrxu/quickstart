package com.plaid.quickstart.persistence;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.plaid.quickstart.model.Category;
import com.plaid.quickstart.model.CategoryList;
import com.plaid.quickstart.model.Expense;
import com.plaid.quickstart.model.Income;
import com.plaid.quickstart.model.Store;
import com.plaid.quickstart.model.StoreList;
import com.plaid.quickstart.model.Transaction;
import com.plaid.quickstart.model.TransactionLog;

public class PlaidReader {
    private List<com.plaid.client.model.Transaction> plaidTransactions;
    
    public PlaidReader(List<com.plaid.client.model.Transaction> plaidTransactions) {
        this.plaidTransactions = plaidTransactions;
    } 

    public TransactionLog parseTransactionLog() {
        TransactionLog transactionLog = new TransactionLog(); 
        StoreList storeList = new StoreList();
        CategoryList categoryList = new CategoryList();

        transactionLog.setCategoryList(categoryList);
        transactionLog.setStoreList(storeList);

         // get total income and expenses
        Double totalIncome = 0.0;
        Double totalExpenses = 0.0;

        for (com.plaid.client.model.Transaction plaidTransaction: plaidTransactions) {
            Transaction transaction = parseTransaction(plaidTransaction, storeList, categoryList);
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


    // REQUIRES: transactionType is one of income or expense
    // EFFECTS: parses single transaction from JSON object (using parsed store)
    // ************* note*** it would be nice to throw an exception if the transaction type is not income or expense
    private Transaction parseTransaction(com.plaid.client.model.Transaction plaidTransaction, StoreList storeList, CategoryList categoryList) {

        
        // gets essential construction fields
        String description = plaidTransaction.getName();
        Double amount = plaidTransaction.getAmount();

        // gets transaction type
        String transactionType;
        if (amount < 0) {
            transactionType = "income";
        } else {
            transactionType = "expense";
        }
        
        // gets fields to be updated
        String id = plaidTransaction.getTransactionId();
        LocalDate date = plaidTransaction.getDate();

        String paymentMethod = plaidTransaction.getPaymentChannel().getValue();
        String comments = null;
        
         

        // makes income if tTYPE is income
        if (transactionType.equals("income")) {
            // gets income specific fields
            String source = plaidTransaction.getName();
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
            String storeName = plaidTransaction.getMerchantName();
            if (storeName == null|| storeName.equals("")) {
                storeName = "N/A";
            }
            String categoryName = plaidTransaction.getPersonalFinanceCategory().getPrimary();
            Category category = new Category(categoryName);
            Store store = new Store(storeName, category);
            

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

}

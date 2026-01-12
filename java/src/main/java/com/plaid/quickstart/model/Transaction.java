package com.plaid.quickstart.model;

import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

/**
 * Represents a financial transaction with a unique id 
 * within user's finance log. A transaction records an 
 * instance where money is moved, 
 * such as income or expenses, and includes details like 
 * a description, amount, and date. Optional
 * fields for the user include payment method and additional comments.
 * This class is abstract and is extended by subclasses such as
 * Income, Expense and Savings. 
 */
public abstract class Transaction implements Writable {
    protected String id;
    protected String description;
    protected LocalDate date;
    protected Double amount;
    protected String paymentMethod; // might change this to enum depending on subclas (ex. cheque vs credit)
    protected String comments;

    // REQUIRES: Amount > 0
    // EFFECTS: Constructs a transaction with random id, description, date
    public Transaction(String description, Double amount) {
        this.description = description;
        this.date = LocalDate.now();
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.amount = amount;
    }
    
    // EFFECTS: returns a clean summary of transaction details
    public abstract String transactionInfo();

    // EFFECTS: formats amount into a nice string with dollar sign
    public String getFormattedAmount() {
        String formattedAmount;
        String decimalAmount = String.format("%.2f", amount);
        if (amount < 0) {
            formattedAmount = ("-$" + decimalAmount.substring(1));
        } else {
            formattedAmount = ("$" + decimalAmount);
        }
        return formattedAmount;

    }


    // basic setters
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void addComments(String comments) {
        this.comments = comments;
    }


    // helper setters for saving
    public void setID(String id) {
        this.id = id;
    }

    public void setDate(LocalDate d) {
        this.date = d;
    }



    // basic getters
    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getComments() {
        return comments;
    }




    // methods for saving

    // EFFECTS: returns this as JSON object, calls specific conversion methods depnding on subtype
    @Override
    public JSONObject toJson() {
        JSONObject jsonTransaction = new JSONObject();
        jsonTransaction.put("id", id);
        jsonTransaction.put("description", description);
        jsonTransaction.put("date", date.toString());
        jsonTransaction.put("amount", amount);
        if (paymentMethod != null) {
            jsonTransaction.put("paymentMethod", paymentMethod);
        }
        if (comments != null) {
            jsonTransaction.put("comments", comments);
        }
        if (this instanceof Income) {
            Income i = (Income) this;
            i.toJsonIncome(jsonTransaction);
        } else {
            Expense e = (Expense) this;
            e.toJsonExpense(jsonTransaction);
        }
        return jsonTransaction;

    }

    

}

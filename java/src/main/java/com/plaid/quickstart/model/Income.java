package com.plaid.quickstart.model;

import java.time.LocalDate;

import org.json.JSONObject;

/**
 * Represents an income transaction in the user's finance log.
 * An Income is a type of Transaction where money is gained,
 * and it includes additional details such as the source.
 * Each Income has a positive amount.
 */
public class Income extends Transaction {
    private String source;
    
    // REQUIRES: amount > 0
    // EFFECTS: Creates an income transaction with description, date, positive amount and source
    public Income(String description, Double amount, String source) {
        super(description, amount);
        this.source = source;
        EventLog.getInstance().logEvent(new Event("Income added."));
        
    }

    // MODIFIES: this
    // EFFECTS: changes any fields of the transaction that are not null
    public void updateIncome(String description, 
                                LocalDate date, 
                                Double amount, 
                                String paymentMethod, 
                                String comments, 
                                String source) {
        if (description != null) {
            this.description = description;
        }
        if (date != null) {
            this.date = date;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod;
        }
        if (comments != null) {
            this.comments = comments;
        }
        if (source != null) {
            this.source = source;
        }
    }

    @Override
    public String transactionInfo() {
        String summary = description + " --- " + getFormattedAmount() + "\n" 
                        + "Date: " + getDate() + "\n" 
                        + "From: " + getSource() + "\n" 
                        + "Payment Method: " + getPaymentMethod() + "\n" 
                        + "Additional Information: " + getComments();
        return summary;
    }   
    

    // getters
    public String getSource() {
        return source;
    }


    // methods for saving 
    public void toJsonIncome(JSONObject jsonIncome) {
        jsonIncome.put("source", source);
        jsonIncome.put("transactionType", "income");
    }
}

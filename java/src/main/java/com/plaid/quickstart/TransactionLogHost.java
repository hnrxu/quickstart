package com.plaid.quickstart;

import com.plaid.quickstart.model.TransactionLog;

public class TransactionLogHost {
    private static TransactionLogHost instance;
    private TransactionLog log;

    private TransactionLogHost() {
        
    }

    public static TransactionLogHost getInstance() {
        if (instance == null) {
            instance = new TransactionLogHost();
        }
        return instance;
    }

    public TransactionLog getLog() {
        return log;
    }

    public void setLog(TransactionLog log) {
        this.log = log;
    }
}

package com.plaid.quickstart.persistence;

import com.plaid.quickstart.model.*;

import org.json.JSONObject;


import java.io.*;

// Represents a writer that writes JSON representation of transactionLog to file
// Referenced from the JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriter {
    private static final int TAB = 4; // indent factor
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened 
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of transactionLog to destination file
    public void write(TransactionLog t) {
        JSONObject json = t.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string into destination file
    private void saveToFile(String json) {
        writer.print(json);
    }
}

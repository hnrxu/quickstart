package com.plaid.quickstart.persistence;

import org.json.JSONObject;

// Represents a writer that writes JSON representation of transactionLog to file
// Referenced from the JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}

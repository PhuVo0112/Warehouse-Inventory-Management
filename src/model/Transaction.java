package model;

import java.util.ArrayList;

public class Transaction {
    private String transactionId;
    private Order order;
    private String timestamp;
    private ArrayList<Batch> affectedBatches;

    public Transaction(String transactionId, Order order, String timestamp) {
        this.transactionId = transactionId;
        this.order = order;
        this.timestamp = timestamp;
        this.affectedBatches = new ArrayList<>();
    }

    public Transaction(String transactionId, Order order, String timestamp, ArrayList<Batch> affectedBatches) {
        this.transactionId = transactionId;
        this.order = order;
        this.timestamp = timestamp;
        this.affectedBatches = affectedBatches;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Order getOrder() {
        return order;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ArrayList<Batch> getAffectedBatches() {
        return affectedBatches;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", order=" + order +
                ", timestamp='" + timestamp + '\'' +
                ", affectedBatches=" + affectedBatches +
                '}';
    }
}
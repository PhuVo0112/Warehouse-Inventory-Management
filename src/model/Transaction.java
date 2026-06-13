package model;

public class Transaction {
    private String transactionId;
    private Order order;
    private String timestamp;

    public Transaction(String transactionId, Order order, String timestamp) {
        this.transactionId = transactionId;
        this.order = order;
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", order=" + order +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
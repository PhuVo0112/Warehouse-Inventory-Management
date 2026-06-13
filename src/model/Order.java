package model;

public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private String type;

    public Order(String orderId, String productId, int quantity, String type) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                '}';
    }
}
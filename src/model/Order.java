package model;

public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private String type;
    private String expiryDate;

    public Order(String orderId, String productId, int quantity, String type, String expiryDate) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
        this.expiryDate = expiryDate;
    }

    public Order(String o01, String p01, int i, String out) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

    public String getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}
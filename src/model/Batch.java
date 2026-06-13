package model;

public class Batch {
    private String batchId;
    private String productId;
    private int quantity;
    private String importDate;
    private String expiryDate;

    public Batch(String batchId, String productId, int quantity, String importDate, String expiryDate) {
        this.batchId = batchId;
        this.productId = productId;
        this.quantity = quantity;
        this.importDate = importDate;
        this.expiryDate = expiryDate;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImportDate() {
        return importDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "batchId='" + batchId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", importDate='" + importDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}
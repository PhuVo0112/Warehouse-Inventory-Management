package model;

public class Batch {
    private String batchId;
    private String productId;
    private int quantity;
    private String importDate;

    public Batch(String batchId, String productId, int quantity, String importDate) {
        this.batchId = batchId;
        this.productId = productId;
        this.quantity = quantity;
        this.importDate = importDate;
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
                '}';
    }
}
package model;

import java.time.LocalDate;

public class Batch implements Comparable<Batch> {
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

    public boolean isExpired() {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate expiry = LocalDate.parse(expiryDate);
            return expiry.isBefore(LocalDate.now());
        } catch (Exception e) {
            try {
                return expiryDate.compareTo(LocalDate.now().toString()) < 0;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    @Override
    public int compareTo(Batch other) {
        if (this.expiryDate == null || this.expiryDate.trim().isEmpty()) {
            if (other.expiryDate == null || other.expiryDate.trim().isEmpty()) {
                return 0;
            }
            return 1;
        }
        if (other.expiryDate == null || other.expiryDate.trim().isEmpty()) {
            return -1;
        }
        try {
            LocalDate d1 = LocalDate.parse(this.expiryDate);
            LocalDate d2 = LocalDate.parse(other.expiryDate);
            return d1.compareTo(d2);
        } catch (Exception e) {
            return this.expiryDate.compareTo(other.expiryDate);
        }
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
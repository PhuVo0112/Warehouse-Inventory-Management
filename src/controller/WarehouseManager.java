package controller;

import datastructure.CustomQueue;
import datastructure.CustomStack;
import model.Product;
import model.Batch;
import model.Order;
import model.Transaction;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class WarehouseManager {
    private HashMap<String, Product> productMap;
    private CustomQueue<Order> orderQueue;
    private CustomStack<Transaction> transactionStack;
    private int transactionCounter;

    public WarehouseManager() {
        this.productMap = new HashMap<>();
        this.orderQueue = new CustomQueue<>();
        this.transactionStack = new CustomStack<>();
        this.transactionCounter = 0;
    }

    private String generateTransactionId() {
        transactionCounter++;
        return String.format("T%03d", transactionCounter);
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().toString();
    }

    public boolean addProduct(Product product) {
        if (productMap.containsKey(product.getProductId())) {
            return false;
        }
        productMap.put(product.getProductId(), product);
        return true;
    }

    public Product searchByProductId(String productId) {
        return productMap.get(productId);
    }

    public boolean updatePrice(String productId, double newPrice) {
        Product product = productMap.get(productId);
        if (product == null) {
            return false;
        }
        product.setPrice(newPrice);
        return true;
    }

    public boolean removeProduct(String productId) {
        if (!productMap.containsKey(productId)) {
            return false;
        }
        productMap.remove(productId);
        return true;
    }

    public boolean processStockIn(Order order) {
        Product product = productMap.get(order.getProductId());
        if (product == null) {
            return false;
        }
        String batchId = "B-" + order.getOrderId();
        String importDate = LocalDate.now().toString();
        String expiryDate = order.getExpiryDate();
        Batch batch = new Batch(batchId, order.getProductId(), order.getQuantity(), importDate, expiryDate);
        product.getBatches().enqueue(batch);
        product.setQuantity(product.getQuantity() + order.getQuantity());
        orderQueue.enqueue(order);
        Transaction transaction = new Transaction(generateTransactionId(), order, getCurrentTimestamp());
        transactionStack.push(transaction);
        return true;
    }

    public boolean processStockOut(Order order) {
        Product product = productMap.get(order.getProductId());
        if (product == null) {
            return false;
        }
        if (product.getQuantity() < order.getQuantity()) {
            return false;
        }
        int remaining = order.getQuantity();
        CustomQueue<Batch> batches = product.getBatches();
        while (remaining > 0) {
            Batch batch = batches.peek();
            if (batch == null) {
                break;
            }
            if (batch.getQuantity() <= remaining) {
                remaining -= batch.getQuantity();
                batches.dequeue();
            } else {
                batch.setQuantity(batch.getQuantity() - remaining);
                remaining = 0;
            }
        }
        product.setQuantity(product.getQuantity() - order.getQuantity());
        orderQueue.enqueue(order);
        Transaction transaction = new Transaction(generateTransactionId(), order, getCurrentTimestamp());
        transactionStack.push(transaction);
        return true;
    }

    public boolean undoLastTransaction() {
        if (transactionStack.isEmpty()) {
            return false;
        }
        Transaction lastTransaction = transactionStack.pop();
        Order order = lastTransaction.getOrder();
        Product product = productMap.get(order.getProductId());
        if (product == null) {
            return false;
        }
        if ("IN".equals(order.getType())) {
            product.setQuantity(product.getQuantity() - order.getQuantity());
            CustomQueue<Batch> batches = product.getBatches();
            CustomQueue<Batch> tempQueue = new CustomQueue<>();
            String targetBatchId = "B-" + order.getOrderId();
            int size = batches.size();
            for (int i = 0; i < size; i++) {
                Batch batch = batches.dequeue();
                if (!targetBatchId.equals(batch.getBatchId())) {
                    tempQueue.enqueue(batch);
                }
            }
            while (!tempQueue.isEmpty()) {
                batches.enqueue(tempQueue.dequeue());
            }
        } else if ("OUT".equals(order.getType())) {
            product.setQuantity(product.getQuantity() + order.getQuantity());
        }
        return true;
    }

    public Product searchByName(String name) {
        if (name == null) {
            return null;
        }
        for (Product p : productMap.values()) {
            if (p.getName() != null && (p.getName().equals(name) || p.getName().contains(name))) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Product> searchByCategory(String category) {
        ArrayList<Product> result = new ArrayList<>();
        if (category == null) {
            return result;
        }
        for (Product p : productMap.values()) {
            if (category.equals(p.getCategory())) {
                result.add(p);
            }
        }
        return result;
    }

    public void checkLowStock(int threshold) {
        for (Product p : productMap.values()) {
            if (p.getQuantity() < threshold) {
                System.out.println(p);
            }
        }
    }
}
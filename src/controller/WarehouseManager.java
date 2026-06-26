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
import java.util.Scanner;

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
        Product product = productMap.get(productId);
        if (product != null) {
            removeExpiredBatchesForProduct(product);
        }
        return product;
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
        product.addBatch(batch);
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
        
        // Auto remove expired batches for this product before checking stock
        removeExpiredBatchesForProduct(product);

        if (product.getQuantity() < order.getQuantity()) {
            return false;
        }
        int remaining = order.getQuantity();
        CustomQueue<Batch> batches = product.getBatches();
        ArrayList<Batch> affected = new ArrayList<>();
        while (remaining > 0) {
            Batch batch = batches.peek();
            if (batch == null) {
                break;
            }
            if (batch.getQuantity() <= remaining) {
                remaining -= batch.getQuantity();
                affected.add(new Batch(batch.getBatchId(), batch.getProductId(), batch.getQuantity(), batch.getImportDate(), batch.getExpiryDate()));
                batches.dequeue();
            } else {
                affected.add(new Batch(batch.getBatchId(), batch.getProductId(), remaining, batch.getImportDate(), batch.getExpiryDate()));
                batch.setQuantity(batch.getQuantity() - remaining);
                remaining = 0;
            }
        }
        product.setQuantity(product.getQuantity() - order.getQuantity());
        orderQueue.enqueue(order);
        Transaction transaction = new Transaction(generateTransactionId(), order, getCurrentTimestamp(), affected);
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
            ArrayList<Batch> affected = lastTransaction.getAffectedBatches();
            if (affected != null) {
                for (int i = affected.size() - 1; i >= 0; i--) {
                    Batch affectedBatch = affected.get(i);
                    boolean exists = addQuantityToBatchIfExists(product.getBatches(), affectedBatch.getBatchId(), affectedBatch.getQuantity());
                    if (!exists) {
                        product.addBatch(affectedBatch);
                    }
                }
            }
        }
        return true;
    }

    private boolean addQuantityToBatchIfExists(CustomQueue<Batch> batches, String batchId, int qtyToAdd) {
        boolean found = false;
        CustomQueue<Batch> temp = new CustomQueue<>();
        while (!batches.isEmpty()) {
            Batch b = batches.dequeue();
            if (b.getBatchId().equals(batchId)) {
                b.setQuantity(b.getQuantity() + qtyToAdd);
                found = true;
            }
            temp.enqueue(b);
        }
        while (!temp.isEmpty()) {
            batches.enqueue(temp.dequeue());
        }
        return found;
    }

    public void removeExpiredBatchesForProduct(Product product) {
        if (product == null) {
            return;
        }
        CustomQueue<Batch> batches = product.getBatches();
        CustomQueue<Batch> tempQueue = new CustomQueue<>();
        int expiredQty = 0;
        while (!batches.isEmpty()) {
            Batch batch = batches.dequeue();
            if (batch.isExpired()) {
                expiredQty += batch.getQuantity();
            } else {
                tempQueue.enqueue(batch);
            }
        }
        while (!tempQueue.isEmpty()) {
            batches.enqueue(tempQueue.dequeue());
        }
        if (expiredQty > 0) {
            product.setQuantity(product.getQuantity() - expiredQty);
        }
    }

    public ArrayList<Product> searchByName(String name) {
        ArrayList<Product> result = new ArrayList<>();
        if (name == null) {
            return result;
        }
        String keyword = name.toLowerCase();
        for (Product p : productMap.values()) {
            removeExpiredBatchesForProduct(p);
            if (p.getName() != null && p.getName().toLowerCase().contains(keyword)) {
                result.add(p);
            }
        }
        return result;
    }

    public ArrayList<Product> searchByCategory(String category) {
        ArrayList<Product> result = new ArrayList<>();
        if (category == null) {
            return result;
        }
        for (Product p : productMap.values()) {
            removeExpiredBatchesForProduct(p);
            if (category.equals(p.getCategory())) {
                result.add(p);
            }
        }
        return result;
    }

    public void checkLowStock(int threshold) {
        for (Product p : productMap.values()) {
            removeExpiredBatchesForProduct(p);
            if (p.getQuantity() < threshold) {
                System.out.println(p);
            }
        }
    }

    public ArrayList<Batch> checkAndRemoveExpired() {
        ArrayList<Batch> expiredBatches = new ArrayList<>();
        for (Product product : productMap.values()) {
            CustomQueue<Batch> batches = product.getBatches();
            CustomQueue<Batch> tempQueue = new CustomQueue<>();
            while (!batches.isEmpty()) {
                Batch batch = batches.dequeue();
                if (batch.isExpired()) {
                    product.setQuantity(product.getQuantity() - batch.getQuantity());
                    expiredBatches.add(batch);
                } else {
                    tempQueue.enqueue(batch);
                }
            }
            while (!tempQueue.isEmpty()) {
                batches.enqueue(tempQueue.dequeue());
            }
        }
        return expiredBatches;
    }

    public void handleAddProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- ADD NEW PRODUCT ---");
        System.out.print("Enter Product ID: ");
        String addId = scanner.nextLine().trim();
        System.out.print("Enter Product Name: ");
        String addName = scanner.nextLine().trim();
        System.out.print("Enter Product Category: ");
        String addCat = scanner.nextLine().trim();
        System.out.print("Enter Product Price: ");
        double addPrice = 0;
        try {
            addPrice = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price. Defaulting to 0.");
        }
        Product newProd = new Product(addId, addName, addCat, 0, addPrice);
        if (this.addProduct(newProd)) {
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to add product (Product ID already exists)!");
        }
    }

    public void handleSearchProductById() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- SEARCH BY PRODUCT ID ---");
        System.out.print("Enter Product ID to search: ");
        String searchId = scanner.nextLine().trim();
        Product foundP = this.searchByProductId(searchId);
        if (foundP != null) {
            System.out.println("Found: " + foundP);
            displayProductBatches(foundP);
        } else {
            System.out.println("Product not found!");
        }
    }

    public void handleSearchProductByName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- SEARCH BY PRODUCT NAME ---");
        System.out.print("Enter name keyword: ");
        String searchName = scanner.nextLine().trim();
        ArrayList<Product> foundListByName = this.searchByName(searchName);
        if (!foundListByName.isEmpty()) {
            System.out.println("Found " + foundListByName.size() + " product(s):");
            for (Product p : foundListByName) {
                System.out.println(p);
                displayProductBatches(p);
            }
        } else {
            System.out.println("No products found matching the keyword!");
        }
    }

    public void handleSearchProductByCategory() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- SEARCH BY CATEGORY ---");
        System.out.print("Enter Category: ");
        String searchCat = scanner.nextLine().trim();
        ArrayList<Product> foundListByCat = this.searchByCategory(searchCat);
        if (!foundListByCat.isEmpty()) {
            System.out.println("Found " + foundListByCat.size() + " product(s) in category '" + searchCat + "':");
            for (Product p : foundListByCat) {
                System.out.println(p);
                displayProductBatches(p);
            }
        } else {
            System.out.println("No products found in this category!");
        }
    }

    public void handleUpdateProductPrice() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- UPDATE PRODUCT PRICE ---");
        System.out.print("Enter Product ID: ");
        String updateId = scanner.nextLine().trim();
        System.out.print("Enter New Price: ");
        double newPrice = 0;
        try {
            newPrice = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price value.");
            return;
        }
        if (this.updatePrice(updateId, newPrice)) {
            System.out.println("Price updated successfully!");
        } else {
            System.out.println("Failed to update price (Product not found)!");
        }
    }

    public void handleRemoveProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- REMOVE PRODUCT ---");
        System.out.print("Enter Product ID to remove: ");
        String removeId = scanner.nextLine().trim();
        if (this.removeProduct(removeId)) {
            System.out.println("Product removed successfully!");
        } else {
            System.out.println("Failed to remove (Product not found)!");
        }
    }

    public void handleStockIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- STOCK IN (IMPORT) ---");
        System.out.print("Enter Order ID: ");
        String inOrderId = scanner.nextLine().trim();
        System.out.print("Enter Product ID: ");
        String inProdId = scanner.nextLine().trim();
        System.out.print("Enter Quantity: ");
        int inQty = 0;
        try {
            inQty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        String expiry = scanner.nextLine().trim();
        
        Order inOrder = new Order(inOrderId, inProdId, inQty, "IN", expiry);
        if (this.processStockIn(inOrder)) {
            System.out.println("Stock in completed successfully!");
        } else {
            System.out.println("Stock in failed (Product might not exist in catalog)!");
        }
    }

    public void handleStockOut() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- STOCK OUT via FEFO (EXPORT) ---");
        System.out.print("Enter Order ID: ");
        String outOrderId = scanner.nextLine().trim();
        System.out.print("Enter Product ID: ");
        String outProdId = scanner.nextLine().trim();
        System.out.print("Enter Quantity to export: ");
        int outQty = 0;
        try {
            outQty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }
        
        Order outOrder = new Order(outOrderId, outProdId, outQty, "OUT", "");
        if (this.processStockOut(outOrder)) {
            System.out.println("Stock out completed successfully (FEFO applied)!");
        } else {
            System.out.println("Stock out failed (Product not found or insufficient stock)!");
        }
    }

    public void handleCheckAndRemoveExpired() {
        System.out.println("\n--- CHECK & REMOVE EXPIRED BATCHES ---");
        ArrayList<Batch> expired = this.checkAndRemoveExpired();
        if (expired.isEmpty()) {
            System.out.println("No expired batches found!");
        } else {
            System.out.println("Detected and removed " + expired.size() + " expired batch(es):");
            for (Batch b : expired) {
                System.out.println(" - Batch: " + b.getBatchId() + " (Product: " + b.getProductId() + "), Expiry: " + b.getExpiryDate() + ", Removed Qty: " + b.getQuantity());
            }
        }
    }

    public void handleUndoLastTransaction() {
        System.out.println("\n--- UNDO LAST TRANSACTION ---");
        if (this.undoLastTransaction()) {
            System.out.println("Undo completed successfully!");
        } else {
            System.out.println("No transactions to undo!");
        }
    }

    public void handleCheckLowStock() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- CHECK LOW STOCK ---");
        System.out.print("Enter low stock threshold: ");
        int threshold = 0;
        try {
            threshold = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid threshold.");
            return;
        }
        System.out.println("Products with stock below " + threshold + ":");
        this.checkLowStock(threshold);
    }

    public void handleDisplayAll() {
        System.out.println("\n--- DISPLAY ALL PRODUCTS & BATCHES ---");
        System.out.println("Current warehouse inventory status:");
        this.checkLowStock(Integer.MAX_VALUE);
    }

    private void displayProductBatches(Product p) {
        if (p.getBatches().isEmpty()) {
            System.out.println("  -> No active batches in stock.");
        } else {
            System.out.println("  -> Active batches (FEFO - soonest expiry first):");
            datastructure.CustomQueue<Batch> batches = p.getBatches();
            datastructure.CustomQueue<Batch> temp = new datastructure.CustomQueue<>();
            while (!batches.isEmpty()) {
                Batch b = batches.dequeue();
                System.out.println("     + Batch: " + b.getBatchId() + " | Qty: " + b.getQuantity() + " | Import: " + b.getImportDate() + " | Expiry: " + b.getExpiryDate());
                temp.enqueue(b);
            }
            while (!temp.isEmpty()) {
                batches.enqueue(temp.dequeue());
            }
        }
    }
}
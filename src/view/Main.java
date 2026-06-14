package view;

import controller.WarehouseManager;
import model.Product;
import model.Order;

public class Main {
    public static void main(String[] args) {
        WarehouseManager wm = new WarehouseManager();

        System.out.println("ADD PRODUCT");
        wm.addProduct(new Product("P01", "Laptop", "Electronics", 10, 1000));
        wm.addProduct(new Product("P02", "Mouse", "Electronics", 20, 50));

        System.out.println("\nSEARCH BY ID");
        Product p = wm.searchByProductId("P01");
        if (p != null) {
            System.out.println(p);
        } else {
            System.out.println("Not found!");
        }

        System.out.println("\nSEARCH BY NAME ");
        Product found = wm.searchByName("Mouse");
        if (found != null) {
            System.out.println(found);
        } else {
            System.out.println("Not found!");
        }

        System.out.println("\n STOCK OUT ");
        Order out = new Order("O01", "P02", 5, "OUT", "2026-06-14");
        boolean outResult = wm.processStockOut(out);
        if (outResult) {
            System.out.println("Stock OUT success");
        } else {
            System.out.println("Stock OUT failed");
        }

        System.out.println("\n STOCK IN ");
        Order in = new Order("O02", "P01", 10, "IN", "2026-06-14");
        boolean inResult = wm.processStockIn(in);
        if (inResult) {
            System.out.println("Stock IN success");
        } else {
            System.out.println("Stock IN failed");
        }

        System.out.println("\n UNDO ");
        boolean undo = wm.undoLastTransaction();
        if (undo) {
            System.out.println("Undo success");
        } else {
            System.out.println("Nothing to undo");
        }

        System.out.println("\n=== LOW STOCK ===");
        wm.checkLowStock(15);
    }
}
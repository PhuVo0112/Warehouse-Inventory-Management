package view;

import controller.WarehouseManager;
import model.Product;
import model.Order;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        WarehouseManager wm = new WarehouseManager();
        Scanner scanner = new Scanner(System.in);

        // Add initial mock data for easier testing
        wm.addProduct(new Product("P01", "Laptop", "Electronics", 0, 1000));
        wm.addProduct(new Product("P02", "Mouse", "Electronics", 0, 50));

        // Stock in some batches with different expiration dates to demonstrate FEFO
        wm.processStockIn(new Order("IN01", "P02", 10, "IN", "2026-06-30")); // Expires later
        wm.processStockIn(new Order("IN02", "P02", 5, "IN", "2026-06-20")); // Expires earlier (FEFO first)
        wm.processStockIn(new Order("IN03", "P02", 8, "IN", "2026-07-15")); // Expires latest

        boolean running = true;
        while (running) {
            System.out.println("\n==================================================");
            System.out.println("     WAREHOUSE INVENTORY MANAGEMENT (FEFO)       ");
            System.out.println("==================================================");
            System.out.println("1.  Add Product");
            System.out.println("2.  Search Product by ID");
            System.out.println("3.  Search Product by Name");
            System.out.println("4.  Search Product by Category");
            System.out.println("5.  Update Product Price");
            System.out.println("6.  Remove Product");
            System.out.println("7.  Stock In (Import Batch with Expiry Date)");
            System.out.println("8.  Stock Out (Export via FEFO)");
            System.out.println("9.  Check & Remove Expired Batches");
            System.out.println("10. Undo Last Transaction");
            System.out.println("11. Check Low Stock");
            System.out.println("12. Display All Products & Batches");
            System.out.println("13. Exit");
            System.out.println("==================================================");
            System.out.print("Please enter your choice (1-13): ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please try again.");
            }
            switch (choice) {
                case 1:
                    wm.handleAddProduct();
                    break;
                case 2:
                    wm.handleSearchProductById();
                    break;
                case 3:
                    wm.handleSearchProductByName();
                    break;
                case 4:
                    wm.handleSearchProductByCategory();
                    break;
                case 5:
                    wm.handleUpdateProductPrice();
                    break;
                case 6:
                    wm.handleRemoveProduct();
                    break;
                case 7:
                    wm.handleStockIn();
                    break;
                case 8:
                    wm.handleStockOut();
                    break;
                case 9:
                    wm.handleCheckAndRemoveExpired();
                    break;
                case 10:
                    wm.handleUndoLastTransaction();
                    break;
                case 11:
                    wm.handleCheckLowStock();
                    break;
                case 12:
                    wm.handleDisplayAll();
                    break;
                case 13:
                    System.out.println("Exiting application. See you again!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please enter a number from 1 to 13.");
            }
        }
        scanner.close();
    }
}
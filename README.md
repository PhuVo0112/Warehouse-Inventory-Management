# Warehouse Inventory Management

## 1. Project Introduction

**Warehouse Inventory Management** is a Java-based inventory management project for the **CSD201** course.

The project simulates the basic functions of a warehouse system, including:

* Product management
* Batch management
* Stock-in operations
* Stock-out operations using FIFO
* Transaction history
* Undo the latest transaction
* Product searching
* Low stock alerts

This project applies custom data structures such as **Queue** and **Stack**.

---

## 2. Team Members

| Member | Responsibilities                                        |
| ------ | ------------------------------------------------------- |
| Bao    | `model`, `datastructure`                                |
| Phu    | `controller/WarehouseManager.java`                      |
| Tran   | `view/Main.java`, search functions, and low stock alert |

---

## 3. Folder Structure

```text id="1ej9vr"
warehouse-inventory/
└── src/
    ├── model/
    │   ├── Product.java
    │   ├── Batch.java
    │   ├── Order.java
    │   └── Transaction.java
    │
    ├── datastructure/
    │   ├── CustomQueue.java
    │   └── CustomStack.java
    │
    ├── controller/
    │   └── WarehouseManager.java
    │
    └── view/
        └── Main.java
```

---

## 4. Package Description

### 4.1. Package `model`

The `model` package contains the main data classes of the system.

#### `Product.java`

Represents a product in the warehouse.

Each product contains:

* `productId`
* `name`
* `category`
* `quantity`
* `price`
* `batches`

The `batches` attribute is a `CustomQueue<Batch>` used to manage product batches using the FIFO rule.

#### `Batch.java`

Represents a batch of products imported into the warehouse.

Each batch contains:

* `batchId`
* `productId`
* `quantity`
* `importDate`

Batches are stored in the product’s queue. When stock is exported, the batch imported first will be used first.

#### `Order.java`

Represents a stock-in or stock-out order.

Each order contains:

* `orderId`
* `productId`
* `quantity`
* `type`

The `type` value can be:

* `"IN"`: stock-in order
* `"OUT"`: stock-out order

#### `Transaction.java`

Represents a processed transaction.

Each transaction contains:

* `transactionId`
* `order`
* `timestamp`

Transactions are stored in a stack to support the undo feature.

---

### 4.2. Package `datastructure`

The `datastructure` package contains custom data structures implemented manually.

The project does not use built-in classes such as:

* `java.util.Queue`
* `java.util.Stack`
* `LinkedList`
* `Deque`

#### `CustomQueue.java`

`CustomQueue<T>` is a FIFO queue implemented using linked nodes.

Main methods:

* `enqueue(T item)`: adds an item to the end of the queue
* `dequeue()`: removes and returns the front item
* `peek()`: returns the front item without removing it
* `isEmpty()`: checks whether the queue is empty
* `size()`: returns the number of items in the queue

The queue is used to manage:

* Orders
* Product batches

#### `CustomStack.java`

`CustomStack<T>` is a LIFO stack implemented using linked nodes.

Main methods:

* `push(T item)`: adds an item to the top of the stack
* `pop()`: removes and returns the top item
* `peek()`: returns the top item without removing it
* `isEmpty()`: checks whether the stack is empty
* `size()`: returns the number of items in the stack

The stack is used to store transaction history and support undo operations.

---

### 4.3. Package `controller`

The `controller` package contains the main business logic of the program.

#### `WarehouseManager.java`

`WarehouseManager` controls all warehouse operations.

Main attributes:

* `HashMap<String, Product> productMap`
* `CustomQueue<Order> orderQueue`
* `CustomStack<Transaction> transactionStack`
* `int transactionCounter`

Main functions:

* Add product
* Search product by ID
* Update product price
* Remove product
* Process stock-in order
* Process stock-out order
* Undo the latest transaction
* Search product by name
* Search product by category
* Check low stock products

---

### 4.4. Package `view`

The `view` package contains the main file used to run and demonstrate the program.

#### `Main.java`

`Main.java` is the entry point of the program.

This file demonstrates the following features:

* Add products
* Search product by ID
* Search product by name
* Search product by category
* Stock out
* Stock in
* Undo transaction
* Low stock alert

---

## 5. Main Features

### 5.1. Add Product

The user can add a new product to the warehouse.

If the `productId` already exists, the system will not allow duplicate products.

---

### 5.2. Search Product

The system supports searching products by:

* Product ID
* Product name
* Product category

---

### 5.3. Stock In

When processing a stock-in order:

1. Find the product by `productId`
2. Create a new batch
3. Add the batch to the product’s batch queue
4. Increase the product quantity
5. Add the order to the order queue
6. Add the transaction to the transaction stack

---

### 5.4. Stock Out

When processing a stock-out order:

1. Find the product by `productId`
2. Check whether the product has enough quantity
3. Export products from batches using FIFO
4. Decrease the product quantity
5. Add the order to the order queue
6. Add the transaction to the transaction stack

FIFO means the batch imported first will be exported first.

---

### 5.5. Undo Transaction

The system supports undoing the latest transaction.

If the latest transaction is a stock-in order, undo will decrease the product quantity.

If the latest transaction is a stock-out order, undo will increase the product quantity.

---

### 5.6. Low Stock Alert

The `checkLowStock(int threshold)` function checks and displays products whose quantity is lower than the given threshold.

Example:

```java id="s2frcx"
wm.checkLowStock(10);
```

This command displays all products with a quantity lower than `10`.

---

## 6. Data Structures Used

| Data Structure | Purpose                                        |
| -------------- | ---------------------------------------------- |
| `HashMap`      | Stores products by `productId` for fast lookup |
| `CustomQueue`  | Manages orders and batches using FIFO          |
| `CustomStack`  | Stores transactions for undo using LIFO        |
| `ArrayList`    | Stores search results by category              |

---

## 7. Technical Rules

The project is allowed to use:

```java id="l1s1wf"
HashMap
ArrayList
```

The project must manually implement:

```java id="1zb2pk"
CustomQueue
CustomStack
```

The project must not use:

```java id="n3fyjn"
java.util.Queue
java.util.Stack
LinkedList
Deque
```

---

## 8. How to Run the Program

### Step 1: Open the project in an IDE

Recommended IDEs:

* NetBeans
* IntelliJ IDEA
* Eclipse
* Visual Studio Code

### Step 2: Check the packages

Make sure all files are placed in the correct packages:

```text id="qmn8ar"
model
datastructure
controller
view
```

### Step 3: Run `Main.java`

Run the following file:

```text id="ng2nxj"
src/view/Main.java
```

---

## 9. Expected Demo

The program should demonstrate the features in this order:

1. Add Products
2. Search by ID
3. Search by Name / Category
4. Stock Out
5. Stock In
6. Undo
7. Low Stock Alert

Example stock-out demo:

```text id="uwpwhr"
Before stock out: P02 quantity = 20
Stock out: P02 quantity = 5
After stock out: P02 quantity = 15
```

---

## 10. Conclusion

The **Warehouse Inventory Management** project simulates a simple warehouse management system using Java.

Through this project, the team applies:

* Object-oriented programming
* Class-based data management
* HashMap for fast product lookup
* Queue for FIFO processing
* Stack for undo operations
* Clear package-based project structure

sequenceDiagram
    actor Staff
    participant Main
    participant WarehouseManager
    participant HashMap
    participant Product
    participant CustomQueue
    participant CustomStack

    Staff->>WarehouseManager: processStockOut(order)
    
    WarehouseManager->>HashMap: get(productId)
    HashMap-->>WarehouseManager: Product
    
    Note over WarehouseManager: 1. Auto cleanup expired stock
    WarehouseManager->>WarehouseManager: removeExpiredBatchesForProduct(Product)
    
    WarehouseManager->>Product: getQuantity()
    Product-->>WarehouseManager: quantity
    
    opt quantity >= order.getQuantity()
        WarehouseManager->>Product: getBatches()
        Product-->>WarehouseManager: CustomQueue
        
        Note over WarehouseManager: 2. Process FEFO Stock Out
        loop while remaining > 0
            WarehouseManager->>CustomQueue: peek()
            CustomQueue-->>WarehouseManager: batch
            
            alt batch.quantity <= remaining
                WarehouseManager->>CustomQueue: dequeue()
                Note over WarehouseManager: Record batch & deducted qty
            else batch.quantity > remaining
                WarehouseManager->>CustomQueue: reduce batch quantity in place
                Note over WarehouseManager: Record batch & deducted qty
            end
        end
        
        WarehouseManager->>Product: setQuantity(newQty)
        
        Note over WarehouseManager: 3. Create Transaction<br/>(includes affected batches for Undo)
        WarehouseManager->>CustomStack: push(transaction)
        
        WarehouseManager-->>Staff: return true
    end
package com.inventory.app.service.threads;

import com.inventory.app.model.Item;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.exception.ReachedMinLimitException;
import com.inventory.app.service.InventoryService;
import com.inventory.app.service.ProductService;
import com.inventory.app.service.ProductionService;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.ErrorLog;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

// One worker runs one subline (a product line producing one product type).
// It takes the tasks of its queue in order and produces them one product at a time:
// reserve the raw materials -> wait the production time -> consume them -> add the product.
// It stops when the queue is empty, when the line is stopped / under maintenance, or when cancelled.
public class ProductionWorker extends SwingWorker<Void, ProductionWorker.Update> {

    private static final long STOCK_WAIT_MS = 2000;

    // what gets published to the UI thread
    public record Update(Task task, String shortageMessage) {}

    private final ProductLine productLine;
    private final ProductType productType;
    private final InventoryService inventoryService;
    private final ProductService productService;
    private final TaskService taskService;
    private final ProductionService productionService;

    public ProductionWorker(ProductLine productLine, ProductType productType, InventoryService inventoryService,
                            ProductService productService, TaskService taskService, ProductionService productionService) {
        this.productLine = productLine;
        this.productType = productType;
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.taskService = taskService;
        this.productionService = productionService;
    }

    public ProductLine getProductLine() {
        return productLine;
    }
    public ProductType getProductType() {
        return productType;
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        Thread.currentThread().setName("Production " + productLine + " / " + productType);
        boolean finishedNormally = false;
        try {
            Queue<Task> tasksQueue = productLine.getTasks(productType);
            Task task;
            while(canContinue() && (task = tasksQueue.peek()) != null) {
                if(taskService.tryStartTask(task)) {
                    publish(new Update(task, null));
                    produce(task);
                }
                TaskStatus status = task.getTaskStatus();
                if(status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
                    tasksQueue.remove(task);
                    publish(new Update(task, null));
                }
            }
            finishedNormally = true;
        } finally {
            productionService.onWorkerFinished(this, finishedNormally);
        }
        return null;
    }

    private boolean canContinue() {
        return !isCancelled() && productLine.isActive();
    }

    private boolean isTaskCancelled(Task task) {
        return task.getTaskStatus() == TaskStatus.CANCELLED;
    }

    private void produce(Task task) throws InterruptedException {
        Map<Item, Double> perProduct = productType.getRequiredItems();
        int produced = task.getProducedAmount();
        while(produced < task.getQuantityRequired()) {
            if(!canContinue() || isTaskCancelled(task)) {
                return;                                     // paused or cancelled, the task stays in the queue
            }
            if(!reserveOrWait(task, perProduct)) {
                return;
            }
            boolean consumed = false;
            try {
                Thread.sleep(productionTimeMs());
                if(isTaskCancelled(task)) {
                    return;                                 // product thrown away, the reservation is released below
                }
                inventoryService.consumeReserved(perProduct);
                consumed = true;
            } catch(ReachedMinLimitException e) {
                // stock was edited by hand while producing, try again after a restock
                continue;
            } finally {
                if(!consumed) {
                    inventoryService.releaseReservation(perProduct);
                }
            }
            productService.add(productType, productLine.getID());
            productLine.incrementTotalProduction();
            produced++;
            if(!taskService.recordProducedUnit(task, produced)) {
                return;
            }
            publish(new Update(task, null));
        }
        // nothing left to produce (e.g. loaded already at 100%), make sure the task gets completed
        taskService.recordProducedUnit(task, produced);
    }

    // reserves the raw materials of one product, if they are under the min limit the thread
    // waits until the inventory changes, returns false if it has to stop while waiting
    private boolean reserveOrWait(Task task, Map<Item, Double> perProduct) throws InterruptedException {
        boolean warned = false;
        while(true) {
            try {
                inventoryService.reserve(perProduct);
                return true;
            } catch(ReachedMinLimitException e) {
                if(!warned) {
                    publish(new Update(task, productLine + " is waiting for raw materials (task #"
                                             + task.getTaskID() + "):\n" + e.getMessage()));
                    warned = true;
                }
                inventoryService.awaitStockChange(STOCK_WAIT_MS);
                if(!canContinue() || isTaskCancelled(task)) {
                    return false;
                }
            }
        }
    }

    // more raw materials = slower, longer line names are faster lines
    private long productionTimeMs() {
        double time = 0;
        for(Double amount : productType.getRequiredItems().values()) {
            time += amount;
        }
        return (long) (time * 5000.0 / productLine.getName().length());
    }

    @Override
    protected void process(List<Update> updates) {
        for(Update update : updates) {
            if(update.shortageMessage() != null) {
                productionService.fireMaterialsShortage(update.shortageMessage());
            }
        }
        productionService.fireProductionUpdated();
    }

    @Override
    protected void done() {
        try {
            get();
        } catch(CancellationException e) {
            // stopped on purpose (app closing)
        } catch(ExecutionException e) {
            ErrorLog.error("Production " + productLine + " / " + productType, e.getCause());
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        productionService.fireProductionUpdated();
    }

}

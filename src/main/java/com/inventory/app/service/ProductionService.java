package com.inventory.app.service;

import com.inventory.app.model.ProductLine;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.service.threads.ProductionWorker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Starts and stops the production threads.
// Every subline (product line + product type) has at most one running ProductionWorker,
// a worker is started when its queue gets a task, when its line becomes active again, or on app start.
public class ProductionService {

    // all callbacks are called on the Swing event thread
    public interface Listener {
        void productionUpdated();
        void materialsShortage(String message);
    }

    private record Subline(ProductLine productLine, ProductType productType) {}

    private final InventoryService inventoryService;
    private final ProductService productService;
    private final TaskService taskService;
    private final ProductLineService productLineService;

    // SwingWorker's own pool is limited to 10 threads, more sublines than that would wait for each other
    private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });
    private final Map<Subline, ProductionWorker> runningWorkers = new ConcurrentHashMap<>();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean shuttingDown = false;

    public ProductionService(InventoryService inventoryService, ProductService productService,
                             TaskService taskService, ProductLineService productLineService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.taskService = taskService;
        this.productLineService = productLineService;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    // called once after the files are loaded, resumes the tasks that were saved in progress
    public void startAll() {
        for(ProductLine pl : productLineService.getLoadedProductLines()) {
            dispatch(pl);
        }
    }

    public void dispatch(ProductLine pl) {
        for(ProductType pt : pl.getProductTypes()) {
            dispatch(pl, pt);
        }
    }

    // starts a worker for the subline if it has work and no worker is running yet
    public void dispatch(ProductLine pl, ProductType pt) {
        if(shuttingDown || !pl.isActive() || pl.getTasks(pt).isEmpty()) {
            return;
        }
        // computeIfAbsent is atomic, so two threads can't create two workers for the same subline,
        // the worker is started only after it is registered so its onWorkerFinished always finds it
        ProductionWorker[] created = new ProductionWorker[1];
        runningWorkers.computeIfAbsent(new Subline(pl, pt), subline ->
                created[0] = new ProductionWorker(pl, pt, inventoryService, productService, taskService, this));
        if(created[0] != null) {
            executor.execute(created[0]);
        }
    }

    public void addTask(Task task, ProductLine pl) {
        taskService.addTask(task);
        pl.addTask(task);
        dispatch(pl, task.getProductType());
    }

    public void cancelTask(int taskId) {
        Task task = taskService.getTaskById(taskId);
        if(task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        boolean wasPending = taskService.isPending(task);
        taskService.cancelTask(taskId);
        ProductLine pl = productLineService.getLoadedProductLines(task.getProductLineID());
        if(wasPending && pl != null) {
            pl.removeTask(task);            // a running task is removed by its worker
        }
    }

    // makes sure the line of the task is working on it (e.g. after it was waiting in a stopped line)
    public void startTask(Task task) {
        ProductLine pl = productLineService.getLoadedProductLines(task.getProductLineID());
        if(pl != null) {
            dispatch(pl, task.getProductType());
        }
    }

    // ACTIVE starts the line's workers again, STOPPED / MAINTENANCE make them stop after the current product
    public void onProductLineStatusChanged(ProductLine pl) {
        dispatch(pl);
    }

    // called by the worker's own thread when it leaves its loop
    public void onWorkerFinished(ProductionWorker worker, boolean finishedNormally) {
        Subline subline = new Subline(worker.getProductLine(), worker.getProductType());
        runningWorkers.remove(subline, worker);
        // a task may have been added after the worker saw an empty queue and before it was removed,
        // a crashed worker is not restarted here so the same error doesn't repeat forever
        if(finishedNormally) {
            dispatch(worker.getProductLine(), worker.getProductType());
        }
    }

    public boolean isRunning(ProductLine pl, ProductType pt) {
        return runningWorkers.containsKey(new Subline(pl, pt));
    }

    // stops every worker (their current product is lost and its materials released) and waits for them
    public void shutdown() throws InterruptedException {
        shuttingDown = true;
        for(ProductionWorker worker : runningWorkers.values()) {
            worker.cancel(true);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    public void fireProductionUpdated() {
        for(Listener listener : listeners) {
            listener.productionUpdated();
        }
    }

    public void fireMaterialsShortage(String message) {
        for(Listener listener : listeners) {
            listener.materialsShortage(message);
        }
    }

}

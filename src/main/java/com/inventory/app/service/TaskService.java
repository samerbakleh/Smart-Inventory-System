package com.inventory.app.service;

import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.service.Threads.TaskThread;

import java.time.LocalDate;
import java.util.ArrayList;

public class TaskService {
    private final TaskRepository taskRepository ;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(Task task){
            if (!task.isTaken()) {
                taskRepository.save(task);
                task.setIsTaken(true);
            }
    }
    public void startTask(int taskID){
        Task task = taskRepository.findById(taskID);
        if (task == null ){
            throw new IllegalArgumentException("Task not found ");
        }
        if (task.getTaskStatus() == TaskStatus.CANCELLED)
            throw new IllegalStateException("Task cannot be started");

        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setStartDate(LocalDate.now());

    }
    public void completeTask (int taskID){
        Task task = taskRepository.findById(taskID);
        if (task == null){
            throw new IllegalArgumentException("Task not found ");
        }
        task.setProgress(100);
        task.setTaskStatus(TaskStatus.COMPLETED);
        //here I have to call the function which will add the products to the inventory
    }

    public void cancelTask (int taskID){
        Task task = taskRepository.findById(taskID);
        if (task == null){
            throw new IllegalArgumentException("Task not found ");
        }
        if (task.getTaskStatus() == TaskStatus.COMPLETED)
            throw new IllegalStateException("Completed task cannot be cancelled");
        //here I have to call the function which return the booked items
        task.setProgress(0);
        task.setTaskStatus(TaskStatus.CANCELLED);
    }
    public void updateProgress(int taskId, int progress){
        Task task = taskRepository.findById(taskId);

        if (task == null)
            throw new IllegalArgumentException("Task not found");

        if (task.getTaskStatus() != TaskStatus.IN_PROGRESS)
            throw new IllegalStateException("Task not in progress");

        if (progress < 0 || progress > 100)
            throw new IllegalArgumentException("Invalid progress value");

        task.setProgress(progress);

        if (progress == 100){
            completeTask(taskId);
            //here we should add the qty of the task to product's qty
        }

    }

    public TaskRepository getTaskRepository(){
        return taskRepository;
    }

    public ArrayList<Task> getAllTasks(){
        return taskRepository.getTasks();
    }

    public Task getTaskById(int taskId){
        return taskRepository.findById (taskId);
    }

    public void updateQuantity(int taskId,int newQuantity){
        Task task = taskRepository.findById (taskId);

        if (task == null)
            throw new IllegalArgumentException("Task not found");

        if (task.getTaskStatus() != TaskStatus.PENDING)
            throw new IllegalStateException("You can not edit task after start");

        task.setQuantityRequired(newQuantity);
    }
    public void updateDeliveryDate (int taskId,LocalDate newDate){
        Task task = taskRepository.findById (taskId);

        if (task == null)
            throw new IllegalArgumentException("Task not found");

        if (task.getTaskStatus() != TaskStatus.PENDING)
            throw new IllegalStateException("You can not edit task after start");

        task.setDeliveryDate(newDate);
    }
    public void updateClientName (int taskId,String newClientName){
        Task task = taskRepository.findById (taskId);

        if (task == null)
            throw new IllegalArgumentException("Task not found");

        if (task.getTaskStatus() != TaskStatus.PENDING)
            throw new IllegalStateException("You can not edit task after start");

        task.setClientName(newClientName);
    }
    public void updateProductName (int taskId,String newProductName){
        Task task = taskRepository.findById(taskId);

        if (task == null)
            throw new IllegalArgumentException("Task not found");

        if (task.getTaskStatus() != TaskStatus.PENDING)
            throw new IllegalStateException("You can not edit task after start");

        task.setProductName(newProductName);
    }
    public void executeTask(Task task){
        if (task.getTaskStatus() == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Task can not be started");
        }
        Thread thread = new Thread(new TaskThread(task, this));
        thread.start();
    }
    public ArrayList<Task> getTasksByStatus(TaskStatus status){
        return taskRepository.getTasksByStatus(status);
    }

}

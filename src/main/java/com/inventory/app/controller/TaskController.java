package com.inventory.app.controller;

import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.model.ProductLine;
import com.inventory.app.service.ProductionService;
import com.inventory.app.service.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskController {

    private final TaskService taskService ;
    private final ProductionService productionService;

    public TaskController(TaskService taskService, ProductionService productionService) {
        this.taskService = taskService;
        this.productionService = productionService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    // adds the task to its product line queue and starts producing it if the line is free
    public void addTask (Task task, ProductLine productLine){
        productionService.addTask(task, productLine);
    }
    public List<String>getProductTypesFullNames()
    {
        return taskService.getProductTypesFullNames();
    }

    public void startTask(Task task){
        productionService.startTask(task);
    }

    public void updateProgress (int id,int progress){
        taskService.updateProgress(id,progress);
    }
    public void cancelTask (int id){
        productionService.cancelTask(id);
    }

    public ArrayList<Task> getAllTasks(){
        return taskService.getAllTasks();
    }

    public Task getTaskById(int taskId){
        return taskService.getTaskById(taskId);
    }

    public void updateDeliveryDate(int taskId, LocalDate newDate){
        taskService.updateDeliveryDate(taskId, newDate);
    }

    public void updateQuantity(int taskId, int newQuantity){
        taskService.updateQuantity(taskId, newQuantity);
    }

    public ArrayList<Task> getTasksByStatus(TaskStatus status){    System.out.println("Controller: filter by " + status);
        return taskService.getTaskRepository().getTasksByStatus(status);}
    public void updateTask(Task task) {
        taskService.updateTask(task);
    }

}

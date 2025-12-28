package com.inventory.app.controller;

import com.inventory.app.model.Task;
import com.inventory.app.service.TaskService;

import java.time.LocalDate;
import java.util.ArrayList;

public class TaskController {

    private final TaskService taskService = new TaskService();

    public void addTask (Task task){
        taskService.addTask(task);
    }

    public void startTask(Task task){
        taskService.executeTask(task);
    }

    public void updateProgress (int id,int progress){
        taskService.updateProgress(id,progress);
    }
    public void cancelTask (int id){
        taskService.cancelTask(id);
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


}

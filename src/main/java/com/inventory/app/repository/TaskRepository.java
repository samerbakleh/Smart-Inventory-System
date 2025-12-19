package com.inventory.app.repository;

import com.inventory.app.model.Task;

import java.util.ArrayList;

public class TaskRepository {
    private ArrayList <Task> tasks = new ArrayList<>();

     public void save (Task task){

         tasks.add(task);
     }

     public ArrayList<Task> getTasks() {
         return tasks;
     }

    public Task findById(int id) {
        for (Task task : tasks) {
            if (task.getTaskID() == id) {
                return task;
            }
        }
        return null;
    }

}

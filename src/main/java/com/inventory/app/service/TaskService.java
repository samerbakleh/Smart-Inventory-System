package com.inventory.app.service;

import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.TaskRepository;

import java.time.LocalDate;

public class TaskService {
    private TaskRepository taskRepository = new TaskRepository();

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
    }

    public void cancelTask (int taskID){
        Task task = taskRepository.findById(taskID);
        if (task == null){
            throw new IllegalArgumentException("Task not found ");
        }
        task.setProgress(0);
        task.setTaskStatus(TaskStatus.CANCELLED);
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }
}

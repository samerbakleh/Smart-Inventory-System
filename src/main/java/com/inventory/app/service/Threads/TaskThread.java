package com.inventory.app.service.threads;

import com.inventory.app.model.Task;
import com.inventory.app.service.TaskService;

public class TaskThread implements Runnable {

    private final Task task ;
    private final TaskService taskService;

    public TaskThread(Task task,TaskService taskService) {
        this.task=task;
        this.taskService=taskService;
    }
    @Override
    public void run(){
        try {
            taskService.startTask(task.getTaskID());
            int producedProducts = 0;
            while (producedProducts < task.getQuantityRequired())
            {
                Thread.sleep(500);
                taskService.updateProgress(task.getTaskID(), 1.0*(100/task.getQuantityRequired()));
                taskService.getTaskRepository().saveTasks();
                System.out.println("1 Product produced ");
                producedProducts++;
            }

        }catch (InterruptedException e){
            taskService.cancelTask(task.getTaskID());
            Thread.currentThread().interrupt();
        }
    }
}

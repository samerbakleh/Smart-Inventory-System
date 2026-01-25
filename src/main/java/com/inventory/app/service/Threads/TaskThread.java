package com.inventory.app.service.Threads;

import com.inventory.app.model.Task;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.ErrorLog;

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

            for (int i= task.getProgress();i<=100;i++){
                Thread.sleep(10*task.getQuantityRequired());//chose your time
                taskService.updateProgress(task.getTaskID(), i);
                taskService.getTaskRepository().saveTasks();
            }
        }catch (InterruptedException e){
            taskService.cancelTask(task.getTaskID());
            Thread.currentThread().interrupt();
            ErrorLog.log(LogLevel.ERROR," Thread ","InterruptedException",e);
        }
    }
}

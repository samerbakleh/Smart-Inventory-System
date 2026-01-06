package com.inventory.app;

import com.inventory.app.controller.TaskController;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.TaskViewerFrame;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void testTasks (){
        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(taskRepository);
        TaskController taskController = new TaskController(taskService);

        Task task1 = new Task(100,"charger","zaher",101,LocalDate.now());
        Task task2 = new Task(140,"Juice","Samer",102,LocalDate.now());
        Task task3 = new Task(10,"lap","Ahmad",103,LocalDate.now());

        taskController.addTask(task1);
        taskController.addTask(task2);
        taskController.addTask(task3);

        taskController.startTask(task1);
        taskController.startTask(task2);
        taskController.startTask(task3);

        view(taskController.getAllTasks());
    }
    public static void view(ArrayList<Task>tasks){

        Thread viewThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean allDone = false;

                while (!allDone) {
                    allDone = true;

                    System.out.println(" ***STATUS CHECK*** ");

                    for (Task task : tasks) {
                        System.out.println(
                                "Task " + task.getTaskID() +
                                        " | Status: " + task.getTaskStatus() +
                                        " | Progress: " + task.getProgress() + "%"
                        );

                        if (task.getTaskStatus() != TaskStatus.COMPLETED) {
                            allDone = false;
                        }
                    }

                    System.out.println();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        });
        viewThread.start();
    }
    public static void TaskUI() {

        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(taskRepository);
        taskRepository.loadTasks();
        TaskController taskController = new TaskController(taskService);

      //  taskController.addTask(new Task(100,"KHUBEZ","zaher",12,LocalDate.now().plusDays(2)));
       // taskController.addTask(new Task(500,"MUJADARA","ahmad",15,LocalDate.now().plusDays(6)));
        //taskController.addTask(new Task(10,"SHISH-BARAK","samer",13,LocalDate.now().plusDays(5)));
       // taskRepository.save(taskController.getTaskById(10000));
        SwingUtilities.invokeLater(() -> {
            TaskViewerFrame frame = new TaskViewerFrame(taskController);
            frame.setVisible(true);
        });

        taskController.startTask(taskController.getTaskById(10000));
        taskController.startTask(taskController.getTaskById(10001));
        taskController.startTask(taskController.getTaskById(10002));
    }
    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        // تشغيل الواجهة في الثريد الخاص بها (Best Practice for Swing)
        SwingUtilities.invokeLater(() -> {
            System.out.println("GUI Thread Started...");
        });
       TaskUI();
    }
}

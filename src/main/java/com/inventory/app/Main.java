package com.inventory.app;

import com.inventory.app.controller.TaskController;
import com.inventory.app.model.ProductType;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.ProductTypeRepository;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.TaskViewerFrame;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import com.inventory.app.util.ErrorLog;
import com.inventory.app.viewer.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

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
        ProductTypeRepository productTypeRepository = new ProductTypeRepository();
        productTypeRepository.add(new ProductType("f","16"));
        productTypeRepository.add(new ProductType("a","16"));
        productTypeRepository.add(new ProductType("z","16"));
        TaskService taskService = new TaskService(taskRepository,productTypeRepository);

        taskRepository.loadTasks();
        TaskController taskController = new TaskController(taskService);

        taskController.addTask(new Task(100,productTypeRepository.findByFullName("f","16"),"zaher",12,LocalDate.now().plusDays(2)));
         taskController.addTask(new Task(500,productTypeRepository.findByFullName("a","16"),"ahmad",15,LocalDate.now().plusDays(6)));
        // taskController.addTask(new Task(10,"SHISH-BARAK","samer",13,LocalDate.now().plusDays(5)));
        // taskRepository.save(taskController.getTaskById(10000));
        SwingUtilities.invokeLater(() -> {
            TaskViewerFrame frame = new TaskViewerFrame(taskController);
            frame.setVisible(true);
        });

        taskController.startTask(taskController.getTaskById(10000));
        taskController.startTask(taskController.getTaskById(10001));
        //taskController.startTask(taskController.getTaskById(10002));
    }
    public static void main(String[] args) {

        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);
        UserRepository.loadUsersFromFile();
        MainFrame.startMainFrame();
        ErrorLog.installGlobalHandlers();

     

        // تشغيل الواجهة في الثريد الخاص بها (Best Practice for Swing)
        SwingUtilities.invokeLater(() -> {
            System.out.println("GUI Thread Started...");
        });
       TaskUI();
    }
}

package com.inventory.app;

import com.inventory.app.model.Person;
import com.inventory.app.model.Product;
import com.inventory.app.model.ProductLine;
import com.inventory.app.model.Task;
import com.inventory.app.model.enums.TaskStatus;
import com.inventory.app.repository.TaskRepository;
import com.inventory.app.service.TaskService;
import com.inventory.app.util.AppConstants;
import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting " + AppConstants.APP_NAME + " v" + AppConstants.VERSION);

        // تشغيل الواجهة في الثريد الخاص بها (Best Practice for Swing)
        SwingUtilities.invokeLater(() -> {
            System.out.println("GUI Thread Started...");
        });
    }
}

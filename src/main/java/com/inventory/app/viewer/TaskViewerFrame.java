package com.inventory.app.viewer;

import com.inventory.app.controller.ProductLineController;
import com.inventory.app.controller.TaskController;

import javax.swing.*;
import java.awt.*;

public class TaskViewerFrame extends JFrame {

    public TaskViewerFrame(TaskController controller, ProductLineController productLineController) {

        setTitle("Task Management");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new TaskListPanel(controller,this,productLineController));

    }
}

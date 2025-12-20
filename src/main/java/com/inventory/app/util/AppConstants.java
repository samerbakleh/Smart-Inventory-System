package com.inventory.app.util;

import java.awt.Color;

public class AppConstants {
    // Basic App Info
    public static final String APP_NAME = "Smart Manufacturing System";
    public static final String VERSION = "1.0.0";

    // File Paths (Relative to project root)
    public static final String DATA_DIR = "src/main/resources/data/";
    public static final String INVENTORY_FILE = DATA_DIR + "initial_inventory.csv";
    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String LOG_FILE = DATA_DIR + "error_log.txt";
    public static final String TASK_FILE = DATA_DIR + "task.txt";

    // Colors (For UI consistency)
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue

    // Private constructor to prevent instantiation
    private AppConstants() {}
}

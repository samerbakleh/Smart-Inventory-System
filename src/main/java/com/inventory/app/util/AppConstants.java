package com.inventory.app.util;

import java.awt.Color;

public class AppConstants {
    // Basic App Info
    public static final String APP_NAME = "Smart Inventory System";
    public static final String VERSION = "1.0.0";

    // File Paths (Relative to project root)
    public static final String DATA_DIR = "src/main/resources/data/";
    public static final String INVENTORY_FILE = DATA_DIR + "initial_inventory.csv";
    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String LOG_FILE = DATA_DIR + "error_log.txt";
    public static final String ITEMS_FILE = DATA_DIR +"items.csv";
    public static final String CATEGORIES_FILE = DATA_DIR +"categories.csv";

    public static final String CSV_DELIMITER = ",";
    // Colors (For UI consistency)
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);



    private AppConstants() {}
}

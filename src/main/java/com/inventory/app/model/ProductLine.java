package com.inventory.app.model;

import java.util.List;

public class ProductLine {
    private  int ID ;
    private List<Task> tasks;
    public int getID()
    {
        return ID;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

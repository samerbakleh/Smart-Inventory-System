package com.inventory.app.viewer;
import javax.swing.*;
import java.awt.*;

public class ProductLineManagerPanel extends JPanel{

    private final MainFrame container;

    public ProductLineManagerPanel(MainFrame container) {
        super(new GridBagLayout());
        this.container = container;
        setName("Manager panel");

        addComponents();
    }

    public void addComponents() {

    }

}

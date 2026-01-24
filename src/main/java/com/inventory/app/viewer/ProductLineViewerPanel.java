package com.inventory.app.viewer;
import com.inventory.app.controller.ProductLineController;

import javax.swing.*;
import java.awt.*;

public class ProductLineViewerPanel extends JPanel {

    private ProductLineController productLineController;

    public ProductLineViewerPanel(ProductLineController productLineController) {
        super(new GridBagLayout());
        this.productLineController = productLineController;
        setName("Product Lines Viewer Panel");
    }

}

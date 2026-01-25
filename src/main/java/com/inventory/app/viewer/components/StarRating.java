package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StarRating extends JPanel {

    private int max;
    private int rating;
    private StarButton[] stars;

    public StarRating(int starRadius, int max) {
        super(new GridBagLayout());
        this.max = max;
        this.rating = 0;
        this.stars = new StarButton[this.max];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 10, 0, 10);
        for(int i = 0; i < max; i++) {
            StarButton s = new StarButton(starRadius, i);
            stars[i] = s;
            add(s, gbc);
        }
    }
    public StarRating(int starRadius) {
        this(starRadius, 5);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        for(int i = 0; i < rating; i++) {
            stars[i].setActive(true);
        }
        for(int i = rating; i < max; i++) {
            stars[i].setActive(false);
        }
    }

    protected class StarButton extends Star {
        public StarButton(int radius, int index) {
            super(radius);
            addActionListener((ActionEvent event) -> {
                setRating(index + 1);
            });
        }

        public void setActive(boolean active) {
            super.active = active;
            repaint();
        }
    }
}

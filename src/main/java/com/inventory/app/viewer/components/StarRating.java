package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class StarRating extends JPanel {

    private int max;
    private int rating;
    private Star[] stars;

    public StarRating(int starRadius, int max) {
        super(new GridBagLayout());
        this.max = max;
        this.rating = 0;
        this.stars = new Star[this.max];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 10, 0, 10);
        for(int i = 0; i < max; i++) {
            Star s = new Star(starRadius, i);
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

    protected class Star extends JButton {
        private int radius;
        private boolean active;
        private Path2D shape;

        public Star(int radius, int index) {
            this.radius = radius;
            this.active = false;
            initStarShape();
            setOpaque(false);
            addActionListener((ActionEvent event) -> {
                setRating(index + 1);
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        private void initStarShape() {
            shape = new Path2D.Double();
            double r = Math.sqrt(Math.pow((1 + radius * Math.sin(Math.PI/10) / Math.tan(Math.PI*17/10)), 2)
                    + Math.pow(radius * Math.sin(Math.PI/10), 2));
            double a = -Math.PI/2;
            shape.moveTo(radius, 0);
            a += Math.PI/5;
            shape.lineTo(radius + r * Math.cos(a), radius + r * Math.sin(a));
            for(int i = 0; i < 4; i++) {
                a += Math.PI/5;
                shape.lineTo(radius + radius * Math.cos(a), radius + radius * Math.sin(a));
                a += Math.PI/5;
                shape.lineTo(radius + r * Math.cos(a), radius + r * Math.sin(a));
            }
            shape.closePath();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(2 * radius, 2 * radius);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(active) {
                g2d.setColor(Color.yellow);
                g2d.fill(shape);
            }
            g2d.setColor(Color.black);
            g2d.draw(shape);
        }

        @Override
        public boolean contains(int x, int y) {
            return shape.contains(new Point(x, y));
        }
    }
}

package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class StarRating extends JPanel {

    private int max;

    protected static class Star extends JButton {
        private Path2D shape;
        private int radius;

        public Star(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        private void drawStarShape() {
            double r = Math.sqrt((1 + radius * Math.sin(Math.PI/10) / Math.tan(Math.PI*17/10)));
            double a = Math.PI/2;
            shape.moveTo(0, radius);
            a += Math.PI/5;
            shape.lineTo(r * Math.cos(a), r * Math.sin(a));
            for(int i = 0; i < 4; i++) {
                a += Math.PI/5;
                shape.lineTo(radius * Math.cos(a), radius * Math.sin(a));
                a += Math.PI/5;
                shape.lineTo(r * Math.cos(a), r * Math.sin(a));
            }
            shape.closePath();
        }

        @Override
        protected void paintComponent(Graphics g) {

        }
    }
}

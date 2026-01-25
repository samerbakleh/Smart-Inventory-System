package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import java.util.List;
import java.util.ArrayList;

public class RemovableLabelPanel extends JPanel {

    private List<RemovableLabel> labels = new ArrayList<>();
    private Color labelColor = getBackground();
    private Color labelBorderColor = Color.gray;


    public List<JComponent> getLabels() {
        return new ArrayList<JComponent>(labels);
    }
    public List<String> getContent() {
        List<String> result = new ArrayList<>();
        for(RemovableLabel label : labels) {
            result.add(label.getText());
        }
        return result;
    }
    public boolean isEmpty() {
        return labels.isEmpty();
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
        for(RemovableLabel l : labels) {
            l.setBackground(labelColor);
        }
    }
    public void setLabelBorderColor(Color labelBorderColor) {
        this.labelBorderColor = labelBorderColor;
        for(RemovableLabel l : labels) {
            l.setBorderColor(labelBorderColor);
        }
    }

    public void addLabel(String text) {
        RemovableLabel label = new RemovableLabel(text, this);
        label.setBorderColor(labelBorderColor);
        label.setBackground(labelColor);
        labels.add(label);
        add(label);
        revalidate();
        repaint();
    }

    public void clearContent() {
        int n = labels.size();
        for(int i = n - 1; i >= 0; i--) {
            removeLabel(labels.get(i));
        }
    }

    protected void removeLabel(RemovableLabel label) {
        labels.remove(label);
        remove(label);
        revalidate();
        repaint();
    }

    protected static class RemovableLabel extends JComponent {

        private String text;
        private Color borderColor;
        private Rectangle2D.Double clickableArea;
        RemovableLabelPanel containerPanel;

        public RemovableLabel(String text, RemovableLabelPanel containerPanel) {
            this.text = text;
            this.containerPanel = containerPanel;
            setOpaque(false);
            var label = this;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if(clickableArea.contains(event.getX(), event.getY())) {
                        containerPanel.removeLabel(label);
                    }
                }
            });
        }

        public String getText() {
            return text;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(text + "  X") + 7, fm.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = getWidth() - 1;
            int labelHeight = getHeight() - 1;
            int textWidth = fm.stringWidth(text + "  X");
            int textHeight = fm.getHeight();
            float x = (labelWidth - textWidth) / 2f;
            float y = (labelHeight - textHeight) / 2f + fm.getAscent();

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, labelWidth, labelHeight, 7, 7);
            g2d.setColor(borderColor);
            g2d.drawRoundRect(0, 0, labelWidth, labelHeight, 7, 7);
            g2d.setColor(getForeground());
            g2d.drawString(text, x, y);
            g2d.setColor(new Color(255, 0, 0, 179));
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
            float xHorizontalPositon = x + textWidth - fm.stringWidth("X");
            g2d.drawString("X", xHorizontalPositon, y);
            clickableArea = new Rectangle2D.Double(xHorizontalPositon, y - fm.getAscent(),
                                                   fm.stringWidth("X"), textHeight);
        }

    }

}

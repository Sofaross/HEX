package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Set;

public class HighlightCellRenderer extends DefaultTableCellRenderer {
    private final Set<Point> highlightedCells;

    public HighlightCellRenderer(Set<Point> highlightedCells) {
        this.highlightedCells = highlightedCells;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Point cellCoordinates = new Point(row, column);

        if (highlightedCells.contains(cellCoordinates)) {
            component.setBackground(Color.YELLOW);
        } else {
            component.setBackground(table.getBackground());
        }

        return component;
    }
}
package ui.Cursor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

    public class HexTableCellRenderer extends DefaultTableCellRenderer {
        private final Position position;
        public HexTableCellRenderer(Position position){
            this.position = position;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (row == position.getRow() && column == position.getColumn()) {
                component.setBackground(Color.YELLOW);
            } else {
                component.setBackground(table.getBackground());  // Установка фонового цвета таблицы для остальных ячеек
            }
            return component;
        }
    }


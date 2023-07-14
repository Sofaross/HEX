package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

    public class HexEditorKeyListener implements KeyListener {
        private JTable table;

        public HexEditorKeyListener(JTable table) {
            this.table = table;
        }


        @Override
        public void keyTyped(KeyEvent e) {
            // использование таблицы через ссылку this.table
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Обработка нажатия клавиш с отпусканием
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                table.setSelectionBackground(Color.YELLOW);
                table.setSelectionForeground(Color.BLACK);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Обработка отпускания клавиш
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                table.setCursor(Cursor.getDefaultCursor());
                table.setSelectionBackground(table.getBackground());
                table.setSelectionForeground(table.getForeground());
            }
        }
    }


package ui;
import controller.DataTableController;
import model.Cursor.HexCursor;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class HexCellEditor extends DefaultCellEditor {
    private Object originalValue;
    private final HexCursor cursor;
    private DataTableController controller;

    public HexCellEditor(DataTableController controller, JTable table) {
        super(new JTextField());
        this.controller = controller;
        this.cursor = controller.getCursor();
        setupTextField((JTextField) getComponent(), table);
    }

    private void setupTextField(JTextField textField, JTable table) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new HexInputDocumentFilter(2));

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(textField, table);
            }
        });
    }

    private void handleKeyReleased(JTextField textField, JTable table) {
        if (textField.getText().length() >= 2) {
            int currentRow = table.getEditingRow();
            int currentColumn = table.getEditingColumn();

            if (currentRow != -1 && currentColumn != -1) {
                if (currentColumn < table.getColumnCount() - 1) {
                    table.editCellAt(currentRow, currentColumn + 1);
                    moveToCell(currentRow, currentColumn + 1, table);
                } else if (currentRow < table.getRowCount() - 1) {
                    table.editCellAt(currentRow + 1, 0);
                }
            }
        }
    }

    private void moveToCell(int row, int column, JTable table) {
        cursor.setCursorPosition(row, column);
        table.repaint();
    }

    @Override
    public boolean stopCellEditing() {
        boolean result = super.stopCellEditing();

        if (result) {
            int currentRow = cursor.getPosition().getRow();
            int currentColumn = cursor.getPosition().getColumn();

            if (currentRow != -1 && currentColumn != -1) {
                byte newValue = (byte) Integer.parseInt((String) getCellEditorValue(), 16);
                controller.setByteAtPosition(currentRow, currentColumn, newValue);
                cursor.setCursorPosition(currentRow, currentColumn);
            }
        }
        return result;
    }

    public void setController(DataTableController controller) {
        this.controller = controller;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JTextField textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        originalValue = value;
        textField.setText("");
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        JTextField textField = (JTextField) editorComponent;
        String newValue = textField.getText();

        if (newValue != null && newValue.length() == 1) {
            newValue = "0" + newValue;
        }

        if (newValue == null || newValue.isEmpty()) {
            return originalValue;
        }
        return newValue;
    }
}

class HexInputDocumentFilter extends DocumentFilter {
    private final int maxLength;

    public HexInputDocumentFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c) || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
                sb.append(c);
            }
        }

        String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + sb;
        if (newText.length() <= maxLength) {
            super.insertString(fb, offset, sb.toString(), attr);
        }
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (length > 0) {
            fb.remove(offset, length);
        }
        insertString(fb, offset, text, attrs);
    }

}

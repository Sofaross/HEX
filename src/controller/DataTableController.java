package controller;

import model.Cursor.HexCursor;
import model.HexEditor;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DataTableController {
    private static final int MAX_BYTE_VALUE = 256;
    private final JTable table;
    private hexEditorListener listener;
    private final HexCursor cursor;
    private HexEditor hexEditor;
    public DataTableController(JTable table) {
        this.table = table;
        cursor = new HexCursor();
    }
    public HexCursor getCursor() {
        return cursor;
    }
    public void setHexEditor(HexEditor hexEditor) {
        this.hexEditor = hexEditor;
    }
    public HexEditor getHexEditor(){
        return hexEditor;
    }
    public int getColumnCount(){
        return table.getModel().getColumnCount();
    }
    public void createNewTable() {
        hexEditor.createEmptyDataArray(getColumnCount());
    }
    public String[] getColumnHeaders() {
        String[] columnHeaders = new String[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            columnHeaders[i] = Integer.toString(i % MAX_BYTE_VALUE);
        }
        return columnHeaders;
    }
    public String[] getRowHeaders() {
        String[] rowHeaders = new String[table.getRowCount()];
        int startAddress = 0;
        int addressStep = 16;
        for (int i = 0; i < table.getRowCount(); i++) {
            int address = startAddress + (i * addressStep);
            rowHeaders[i] = String.format("%08X", address);
        }
        return rowHeaders;
    }
    public void addRow() {
        hexEditor.addRow(getColumnCount());
    }
    public void addColumn() {
        hexEditor.addColumn(getColumnCount()-1);
    }
    public void setListener(hexEditorListener listener) {
        this.listener=listener;
    }

    public ListSelectionListener selectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            int rowIndex = table.getSelectedRow();
            int columnIndex = table.getSelectedColumn();
            if (rowIndex >= 0 && columnIndex >= 0) {
                cursor.getPosition().setRow(rowIndex);
                cursor.getPosition().setColumn(columnIndex);
                table.repaint();
                String cellValue = (String) table.getValueAt(rowIndex, columnIndex);
                if (listener != null) {
                    listener.cellValueSelected(cellValue);
                }
            }
        }
    };
    public void setByteAtPosition(int row, int column, byte value) {
        if (hexEditor != null) {
            hexEditor.setByte(row * getColumnCount() + column, value);
        }
    }
}
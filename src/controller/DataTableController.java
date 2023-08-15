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
        hexEditor.expandRowData(getColumnCount());
    }
    public void addColumn() {
        hexEditor.expandColumnData(getColumnCount()-1);
    }
    public void setListener(hexEditorListener listener) {
        this.listener=listener;
    }

    public ListSelectionListener selectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            handleValueChangedEvent();
        }
    };
    private void handleValueChangedEvent() {
        int rowIndex = table.getSelectedRow();
        int columnIndex = table.getSelectedColumn();
        if (isValidCellIndex(rowIndex, columnIndex)) {
            updateCursorPosition(rowIndex, columnIndex);
            updateCellValue(rowIndex, columnIndex);
        }
    }
    private boolean isValidCellIndex(int row, int column) {
        return row >= 0 && column >= 0;
    }
    private void updateCursorPosition(int row, int column) {
        cursor.getPosition().setRow(row);
        cursor.getPosition().setColumn(column);
        table.repaint();
    }
    private void updateCellValue(int row, int column) {
        String cellValue = (String) table.getValueAt(row, column);
        if (listener != null) {
            listener.cellValueSelected(cellValue);
        }
    }
    public void setByteAtPosition(int row, int column, byte value) {
        if (hexEditor != null) {
            hexEditor.setDataByte(row * getColumnCount() + column, value);
        }
    }
    public void deleteBlock(int startRow, int startColumn, int endRow, int endColumn,boolean offset) {
        int startIndex = startRow*getColumnCount() +startColumn;
        int endIndex = endRow*getColumnCount()+endColumn;
        if (offset){
            hexEditor.deleteRange(startIndex,endIndex);
        }
        else{
            hexEditor.zeroFillRange(startIndex,endIndex);
        }
    }

    public void insertBytesAtPosition(int row, int column, byte[] bytes, boolean replace) {
        if (hexEditor != null) {
            int index = row * getColumnCount() + column;
            if (replace) {
                hexEditor.insertBytes(index, bytes);
            } else {
                hexEditor.replaceBytes(index, bytes);
            }
        }
    }
}
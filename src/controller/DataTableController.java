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
        hexEditor.expandColumnData(getColumnCount() - 1);
    }

    public void setListener(hexEditorListener listener) {
        this.listener = listener;
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
            updateCursorPositionAndCellValue(rowIndex, columnIndex);
        }
    }

    private boolean isValidCellIndex(int row, int column) {
        return row >= 0 && column >= 0;
    }

    private void updateCursorPositionAndCellValue(int row, int column) {
        updateCursorPosition(row, column);
        updateCellValue(row, column);
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
        if (hexEditor == null) {
            throw new IllegalArgumentException("HexEditor is null");
        }
        hexEditor.setDataByte(row * getColumnCount() + column, value);
    }

    public void deleteBlock(int startRow, int startColumn, int endRow, int endColumn, boolean offset) {
        int minRow = Math.min(startRow, endRow);
        int maxRow = Math.max(startRow, endRow);
        int minColumn = Math.min(startColumn, endColumn);
        int maxColumn = Math.max(startColumn, endColumn);

        if (hexEditor == null) {
            throw new IllegalArgumentException("HexEditor is null");
        }

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minColumn; col <= maxColumn; col++) {
                int index = row * getColumnCount() + col;
                if (offset) {
                    hexEditor.deleteRange(index, index);
                } else {
                    hexEditor.zeroFillRange(index, index);
                }
            }
        }
    }

    public void insertBytesAtPosition(int startRow, int startColumn, byte[][] bytes, boolean replace) {
        if (hexEditor == null) {
            throw new IllegalArgumentException("HexEditor is null");
        }

        int numRows = bytes.length;
        int numColumns = bytes[0].length;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                int rowIndex = startRow + row;
                int columnIndex = startColumn + col;

                if (rowIndex >= 0 && rowIndex < table.getRowCount() && columnIndex >= 0 && columnIndex < table.getColumnCount()) {
                    int dataIndex = rowIndex * getColumnCount() + columnIndex;
                    byte byteValue = bytes[row][col];

                    if (replace) {
                        hexEditor.insertBytes(dataIndex, new byte[] { byteValue });
                    } else {
                        hexEditor.replaceBytes(dataIndex, new byte[] { byteValue });

                    }
                }
            }
        }
    }
}

package controller;

import model.ByteSearch;
import model.Cursor.HexCursor;
import model.HexEditor;
import ui.HighlightedCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataTableController {
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
            columnHeaders[i] = Integer.toString(i % 256);
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
    public List<int[]> findCellsWithMask(byte[] mask) {
        List<int[]> foundCells = new ArrayList<>();
        byte[] data = hexEditor.getData();

        int startIndex = 0;
        while (startIndex <= data.length - mask.length) {
            int index = ByteSearch.searchWithMask(data, mask, startIndex);
            if (index != -1) {
                List<int[]> sequenceCells = new ArrayList<>();
                for (int j = 0; j < mask.length; j++) {
                    int row = index / getColumnCount();
                    int column = index % getColumnCount();
                    sequenceCells.add(new int[]{row, column});
                    index++;
                }
                foundCells.addAll(sequenceCells);
                startIndex = index;
            } else {
                break;
            }
        }
        return foundCells;
    }

    public void highlightCells(List<int[]> cells) {
        HighlightedCellRenderer renderer = new HighlightedCellRenderer();
        for (int[] cell : cells) {
            int row = cell[0];
            int column = cell[1];

            Component cellComponent = table.prepareRenderer(renderer, row, column);
            cellComponent.setBackground(Color.GREEN);
            table.revalidate();
            table.repaint();
        }
    }
}
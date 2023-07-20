package controller;

import model.HexEditor;
import model.Cursor.HexCursor;
import ui.HexTableCellRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DataTableController {
    private final JTable table;
    private DefaultTableModel model;
    private hexEditorListener listener;
    private final HexCursor cursor;

    public DataTableController(JTable table) {
        this.table = table;
        model = (DefaultTableModel) table.getModel();
        cursor = new HexCursor();
    }
    public void createNewTable() {
        setColumnHeaders();
        DefaultTableModel newModel = new DefaultTableModel();
        for (int i = 0; i < getColumnCount(); i++) {
            newModel.addColumn(getColumnHeaders()[i]);
        }

        Object[] rowData = new Object[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            rowData[i] = "00";
        }
        newModel.addRow(rowData);
        model=newModel;
        table.setModel(newModel);
    }
    private int getColumnCount(){
        return model.getColumnCount();
    }
    public void setColumnCount(int count) {
        model.setColumnCount(count);
    }
    public String[] getColumnHeaders() {
        String[] columnHeaders = new String[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            columnHeaders[i] = Integer.toString(i % 256);
        }
        return columnHeaders;
    }
    public void setColumnHeaders(){
        String[] columnHeaders = getColumnHeaders();
        model = new DefaultTableModel(null, columnHeaders);
        table.setModel(model);
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
    public void addRow(Object[] rowData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object[] modifiedRowData = new Object[rowData.length];
        for (int i = 0; i < rowData.length; i++) {
            modifiedRowData[i] = "00";
        }
        model.addRow(modifiedRowData);
        table.setModel(model);
    }
    public void addColumn() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        int newColumnCount = tableModel.getColumnCount() + 1;

        tableModel.addColumn(getColumnCount());

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt("00", row, newColumnCount - 1);
        }

        TableColumn newColumn = table.getColumnModel().getColumn(newColumnCount - 1);
        newColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

        setColumnCount(newColumnCount);
    }

    public void updateTableModelHex(HexEditor hexEditor) {
        if (hexEditor.getByteCount() != 0 ) {
            try {
                int numColumns = getColumnCount();
                int numRows = (int) Math.ceil((double) hexEditor.getByteCount()/ numColumns);

                String[][] hexData = hexEditor.updateDataHex(numRows,numColumns);

                DefaultTableModel model = new DefaultTableModel(hexData, getColumnHeaders());
                table.setModel(model);

            } catch (Exception e) {
                ErrorHandler.showError("Error updating table model: " + e.getMessage());            }
        }
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

    public HexCursor getCursor() {
        return cursor;
    }
}

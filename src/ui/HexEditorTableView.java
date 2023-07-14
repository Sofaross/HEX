package ui;

import controller.hexEditorListener;
import model.HexEditor;
import ui.Cursor.HexTableCellRenderer;
import ui.Cursor.Position;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HexEditorTableView extends JTable {

    private final JTable table;
    private final JScrollPane scrollPane;
    private int columnCount = 8 ;
    private hexEditorListener listener;
    private final Position cursor;
    public HexEditorTableView() {
        table = new JTable();
        scrollPane = new JScrollPane(table);
        cursor = new Position();
        initTable();
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(cursor));
    }
    private void initTable() {
        table.setShowGrid(false);
        table.setTableHeader(null);
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 2));
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            int columnIndex = table.getSelectedColumn();
            cursor.setRow(rowIndex);
            cursor.setColumn(columnIndex);
            table.repaint();
            String cellValue = (String) table.getValueAt(rowIndex, columnIndex);
            if (listener != null){
                listener.cellValueSelected(cellValue);
            }
        });
//        table.getCellEditor().addCellEditorListener(new CellEditorListener() {
//            @Override
//            public void editingStopped(ChangeEvent e) {
//                // Получаем индексы измененной ячейки
//                int rowIndex = table.getSelectedRow();
//                int columnIndex = table.getSelectedColumn();
//                // Получаем новое значение из ячейки
//                String newValue = (String) table.getValueAt(rowIndex, columnIndex);
//                // Вызываем метод слушателя и передаем новое значение
//                if (listener != null) {
//                    listener.cellValueChanged(rowIndex, columnIndex, newValue);
//                }
//            }
//            @Override
//            public void editingCanceled(ChangeEvent e) {
//                // Не используется
//            }
//        });
    }

    @Override
    public int getColumnCount() {return columnCount;}
    public void setColumnCount(int count) {columnCount = count;}
    public void createNewTable() {
        DefaultTableModel newModel = new DefaultTableModel();
        for (int i = 0; i < getColumnCount(); i++) newModel.addColumn("");
        newModel.addRow(new Object[getColumnCount()]);
        table.setModel(newModel);
    }
    public void addRow(Object[] rowData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(rowData);
    }
    public void removeRow(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.removeRow(rowIndex);
    }
    public JScrollPane getScrollPane() {return scrollPane;}
    public void setListener(hexEditorListener listener) {this.listener=listener;}
    protected void updateTableModelHex(HexEditor hexEditor) {
        if (hexEditor.getByteCount() != 0 ) {
            try {
                int numColumns = getColumnCount();
                int numRows = (int) Math.ceil((double) hexEditor.getByteCount()/ numColumns);

                String[][] hexData = hexEditor.updateDataHex(numRows,numColumns);

                String[] columnNames = new String[numColumns];
                for (int i = 0; i < numColumns; i++) {columnNames[i] = "Column " + (i + 1);}

                DefaultTableModel model = new DefaultTableModel(hexData, columnNames);
                table.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error updating table model: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    protected void updateTableModelAscii(HexEditor hexEditor) {
        if (hexEditor.getByteCount() != 0) {
            try {
                int numColumns = getColumnCount();
                int numRows = (int) Math.ceil((double) hexEditor.getData().length / numColumns);
                String[][] hexData = hexEditor.updateDataByte(numRows,numColumns);
                String[] columnNames = new String[numColumns];
                for (int i = 0; i < numColumns; i++) {columnNames[i] = "Column " + (i + 1);}
                DefaultTableModel model = new DefaultTableModel(hexData, columnNames);
                table.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error updating table model: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
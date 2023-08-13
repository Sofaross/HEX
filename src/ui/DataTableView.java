package ui;

import controller.DataTableController;
import controller.hexEditorListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DataTableView extends JTable {
    private final JTable table;
    private final DataTableController controller;
    private final JScrollPane scrollPane;
    private final DefaultTableModel model;
    public DataTableView() {
        table = new JTable();
        scrollPane = new JScrollPane(table);
        controller = new DataTableController(table);
        model = (DefaultTableModel) table.getModel();
        initTable();
    }
    private void initTable() {
        table.setShowGrid(false);
        table.setGridColor(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setIntercellSpacing(new Dimension(1, 2));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.setSurrendersFocusOnKeystroke(true);
        HexCellEditor hexCellEditor = new HexCellEditor(controller, table);
        hexCellEditor.setController(controller); // Установите контроллер
        table.setDefaultEditor(Object.class, hexCellEditor);
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(controller.getCursor()));
    }
    public String[] getRowHeaders() {
        return controller.getRowHeaders();
    }
    public void setCustomColumnCount(int count) {
        model.setColumnCount(count);
        table.setModel(model);
    }
    public void createNewTable() {
        controller.createNewTable();
        updateTableModelHex();
    }
    public void addRow() {
        controller.addRow();
        updateTableModelHex();
    }
    public void addColumn() {
        setCustomColumnCount(table.getColumnCount()+1);
        controller.addColumn();
        updateTableModelHex();
    }
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    public void setListener(hexEditorListener listener) {
        controller.setListener(listener);
    }
    protected DataTableController getController(){
        return controller;
    }
    public void updateTableModelHex() {
        if (controller.getHexEditor() == null) {
            throw new IllegalArgumentException("HexEditor is null");
        }

        if (controller.getHexEditor().getByteCount() != 0) {
            try {
                int numColumns = table.getColumnCount();
                int numRows = (int) Math.ceil((double) controller.getHexEditor().getByteCount() / numColumns);

                String[][] hexData = controller.getHexEditor().updateDataHex(numRows, numColumns);

                model.setDataVector(hexData, controller.getColumnHeaders());

            } catch (Exception e) {
                throw new RuntimeException("Error updating table model", e);
            }
        }
    }
}
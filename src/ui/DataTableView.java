package ui;

import controller.DataTableController;
import controller.hexEditorListener;
import model.HexEditor;
import javax.swing.*;
import java.awt.*;

public class DataTableView extends JTable {
    private final JTable table;
    private final DataTableController controller;
    private final JScrollPane scrollPane;
    public DataTableView() {
        table = new JTable();
        scrollPane = new JScrollPane(table);
        controller = new DataTableController(table);
        initTable();
    }

    private void initTable() {
        table.setShowGrid(false);
        table.setGridColor(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setIntercellSpacing(new Dimension(1, 2));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(controller.getCursor()));
    }
    public String[] getRowHeaders() {
        return controller.getRowHeaders();
    }
    public void setColumnCount(int count) {
        controller.setColumnCount(count);
    }
    public void createNewTable() {
       controller.createNewTable();
    }
    public void addRow(Object[] rowData) {
        controller.addRow(rowData);
    }
    public void addColumn() {
       controller.addColumn();
    }
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    public void setListener(hexEditorListener listener) {
        controller.setListener(listener);
    }
    protected void updateTableModelHex(HexEditor hexEditor) {
       controller.updateTableModelHex(hexEditor);
    }

}
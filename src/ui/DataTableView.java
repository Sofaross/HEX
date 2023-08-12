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
    private HexCellEditor hexCellEditor;
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
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.setSurrendersFocusOnKeystroke(true);
        hexCellEditor = new HexCellEditor(controller,table);
        table.setDefaultEditor(Object.class,hexCellEditor);
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(controller.getCursor()));
        }
    public String[] getRowHeaders() {
        return controller.getRowHeaders();
    }
    public void setCustomColumnCount(int count) {
        controller.setCustomColumnCount(count);
    }
    public void createNewTable() {
       controller.createNewTable();
    }
    public void addRow() {
        controller.addRow();
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
    protected DataTableController getController(){
        return controller;
    }
    public void setHex(HexEditor hexEditor){
        controller.setHexEditor(hexEditor);
        hexCellEditor.setHexEditor(hexEditor);
    }
}
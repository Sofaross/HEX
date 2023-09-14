package ui;

import controller.DataTableController;
import controller.ErrorHandler;
import controller.selectionListener;
import controller.hexEditorListener;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class DataTableView extends JTable implements selectionListener {
    private final JTable table;
    private final DataTableController controller;
    private final JScrollPane scrollPane;
    private final DefaultTableModel model;
    private final SelectionManager selectionManager;
    private final DataManipulationHelper manipulationHelper;
    private final Set<Point> highlightedCells = new HashSet<>();

    public DataTableView() {
        table = new JTable();
        scrollPane = new JScrollPane(table);
        controller = new DataTableController(table);
        model = (DefaultTableModel) table.getModel();
        manipulationHelper = new DataManipulationHelper(controller, table);
        new ClipboardManager(table, manipulationHelper);
        selectionManager = new SelectionManager(table);
        initTable();
    }

    private void initTable() {
        configureTableAppearance();
        setupCellRenderersAndEditors();
        attachEventListeners();

    }

    private void configureTableAppearance() {
        table.setShowGrid(false);
        table.setGridColor(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setIntercellSpacing(new Dimension(1, 2));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void setupCellRenderersAndEditors() {
        HexCellEditor hexCellEditor = new HexCellEditor(controller, table);
        hexCellEditor.setController(controller);
        table.setDefaultEditor(Object.class, hexCellEditor);
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(controller.getCursor()));
    }

    private void attachEventListeners() {
        table.getSelectionModel().addListSelectionListener(controller.selectionListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(controller.selectionListener);
        selectionManager.addSelectionListener(this);
        setupContextMenu();
        addEscapeKeyListener();
    }
    @Override
    public void onSelectionChanged(List<int[]> cells) {
        highlightCells(cells);
    }
    private void setupContextMenu(){
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem cutItemWithoutOffsetItem = new JMenuItem("Cut without offset");
        JMenuItem cutItemWithOffsetItem = new JMenuItem("Cut with offset");
        JMenuItem deleteWithOffsetItem = new JMenuItem("Delete with offset");
        JMenuItem deleteWithoutOffsetItem = new JMenuItem("Delete without offset");
        JMenuItem pasteWithOffsetItem = new JMenuItem("Paste with offset");
        JMenuItem pasteWithoutOffsetItem = new JMenuItem("Paste without offset");

        contextMenu.add(cutItemWithOffsetItem);
        contextMenu.add(cutItemWithoutOffsetItem);
        contextMenu.add(pasteWithoutOffsetItem);
        contextMenu.add(pasteWithOffsetItem);
        contextMenu.add(deleteWithoutOffsetItem);
        contextMenu.add(deleteWithOffsetItem);

        deleteWithOffsetItem.addActionListener(e -> delete(true));
        deleteWithoutOffsetItem.addActionListener(e -> delete(false));
        pasteWithOffsetItem.addActionListener(e -> paste(true));
        pasteWithoutOffsetItem.addActionListener(e -> paste(false));
        cutItemWithOffsetItem.addActionListener(e ->cut(true));
        cutItemWithoutOffsetItem.addActionListener(e -> cut(false));

        table.setComponentPopupMenu(contextMenu);
    }

    private void addEscapeKeyListener() {
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    handleEscapeKey();
                }
            }
        });
    }

    private void handleEscapeKey() {
        clearHighlight();
        table.setDefaultRenderer(Object.class, new HexTableCellRenderer(controller.getCursor()));
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setListener(hexEditorListener listener) {
        controller.setListener(listener);
    }

    protected DataTableController getController() {
        return controller;
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
        setCustomColumnCount(table.getColumnCount() + 1);
        controller.addColumn();
        updateTableModelHex();
    }

    public void updateTableModelHex() {
        if (controller.getHexEditor() == null) {
            ErrorHandler.showError("HexEditor is null");
        }

        if (controller.getHexEditor().getByteCount() != 0) {
            try {
                int numColumns = table.getColumnCount();
                int numRows = calculateNumRows(controller.getHexEditor().getByteCount(), numColumns);

                String[][] hexData = calculateHexData(controller, numRows, numColumns);

                updateTableModel(hexData);

            } catch (Exception e) {
                ErrorHandler.showError("Error updating table model: " + e.getMessage());
            }
        }
    }

    private int calculateNumRows(int byteCount, int numColumns) {
        return (int) Math.ceil((double) byteCount / numColumns);
    }

    private String[][] calculateHexData(DataTableController controller, int numRows, int numColumns) {
        return controller.getHexEditor().convertDataToHexMatrix(numRows, numColumns);
    }

    private void updateTableModel(String[][] hexData) {
        model.setDataVector(hexData, controller.getColumnHeaders());
    }

    public void highlightCells(List<int[]> cells) {
        clearHighlight();

        for (int[] cell : cells) {
            int row = cell[0];
            int column = cell[1];

            if (row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()) {
                highlightedCells.add(new Point(row, column));
            }
        }
        table.setDefaultRenderer(Object.class, new HighlightCellRenderer(highlightedCells));
        table.repaint();
    }

    private void clearHighlight() {
        highlightedCells.clear();
        selectionManager.clearHighlight();
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        table.repaint();
    }


    private void delete(boolean offset){
        manipulationHelper.handleDeleteAction(offset, table.getSelectedRows(), table.getSelectedColumns());
        updateTableModelHex();
    }

    private void cut(boolean offset){
        manipulationHelper.cutSelectedCells(offset, table.getSelectedRows(), table.getSelectedColumns());
        updateTableModelHex();
    }

    private void paste(boolean offset){
        manipulationHelper.pasteCellsFromClipboard(offset,table.getSelectedRow(), table.getSelectedColumn());
        updateTableModelHex();
    }
}
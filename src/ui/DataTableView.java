package ui;

import controller.DataTableController;
import controller.hexEditorListener;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DataTableView extends JTable {
    private final JTable table;
    private final DataTableController controller;
    private final JScrollPane scrollPane;
    private final DefaultTableModel model;
    private final Set<Point> highlightedCells = new HashSet<>();

    public DataTableView() {
        table = new JTable();
        scrollPane = new JScrollPane(table);
        controller = new DataTableController(table);
        model = (DefaultTableModel) table.getModel();
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
        table.setSurrendersFocusOnKeystroke(true);
        addEscapeKeyListener();
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


        deleteWithOffsetItem.addActionListener(e -> handleDeleteAction(true));
        deleteWithoutOffsetItem.addActionListener(e -> handleDeleteAction(false));
        pasteWithOffsetItem.addActionListener(e -> pasteCellsFromClipboard(true));
        pasteWithoutOffsetItem.addActionListener(e -> pasteCellsFromClipboard(false));  
        cutItemWithOffsetItem.addActionListener(e -> handleCutAction(true));
        cutItemWithoutOffsetItem.addActionListener(e -> handleCutAction(false));

        table.setComponentPopupMenu(contextMenu);
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
        table.getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedCellsToClipboard();
            }
        });
    }

    private void handleCutAction(boolean offset) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            copySelectedCellsToClipboard();
            handleDeleteAction(offset);
        }
    }

    private void handleDeleteAction(boolean offset) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int startColumn = selectedColumns[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            deleteSelectedBlock(startRow, startColumn, endRow, endColumn, offset);
        }
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
            throw new IllegalArgumentException("HexEditor is null");
        }

        if (controller.getHexEditor().getByteCount() != 0) {
            try {
                int numColumns = table.getColumnCount();
                int numRows = calculateNumRows(controller.getHexEditor().getByteCount(), numColumns);

                String[][] hexData = calculateHexData(controller, numRows, numColumns);

                updateTableModel(hexData);

            } catch (Exception e) {
                throw new RuntimeException("Error updating table model", e);
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

    public void clearHighlight() {
        highlightedCells.clear();
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        table.repaint();
    }
    public void deleteSelectedBlock(int startRow, int startColumn, int endRow, int endColumn, boolean offset) {
        controller.deleteBlock(startRow, startColumn, endRow, endColumn,offset);
        updateTableModelHex();
    }
    private void copySelectedCellsToClipboard() {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            StringBuilder copiedText = new StringBuilder();

            for (int row : selectedRows) {
                for (int column : selectedColumns) {
                    Object cellValue = table.getValueAt(row, column);
                    copiedText.append(cellValue).append('\t');
                }
                copiedText.append('\n');
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(copiedText.toString()), null);
        }
    }
    private void pasteCellsFromClipboard(boolean offset) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                String[] rowStrings = clipboardData.split("[\\s\\t\\n]+");

                byte[] byteArray = new byte[rowStrings.length];
                for (int i = 0; i < rowStrings.length; i++) {
                    try {
                        int decimalValue = Integer.parseInt(rowStrings[i], 16);
                        byteArray[i] = (byte) decimalValue;
                    } catch (NumberFormatException e) {
                        byteArray[i] = 0;
                    }
                }
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();

                if (selectedRow >= 0 && selectedColumn >= 0) {
                    controller.insertBytesAtPosition(selectedRow, selectedColumn, byteArray, offset);
                    updateTableModelHex();
                } else {
                    System.out.println("No cell selected for paste.");
                }
            } catch (NumberFormatException | UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
                System.out.println("Error pasting data from clipboard.");
            }
        }
    }
}
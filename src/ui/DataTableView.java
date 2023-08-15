package ui;

import controller.DataTableController;
import controller.hexEditorListener;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
        JMenuItem deleteWithShiftItem = new JMenuItem("Delete with offset");
        JMenuItem deleteWithoutShiftItem = new JMenuItem("Delete without offset");

        contextMenu.add(deleteWithShiftItem);
        contextMenu.add(deleteWithoutShiftItem);

        deleteWithShiftItem.addActionListener(e -> handleDeleteAction(true));
        deleteWithoutShiftItem.addActionListener(e -> handleDeleteAction(false));

        table.setComponentPopupMenu(contextMenu);
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
        table.getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedCellsToClipboard();
            }
        });

        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "paste");
        table.getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteCellsFromClipboard();
            }
        });
    }

    private void handleDeleteAction(boolean shift) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int startRow = selectedRows[0];
            int startColumn = selectedColumns[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            deleteSelectedBlock(startRow, startColumn, endRow, endColumn, shift);
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
                    copiedText.append(cellValue).append('\t'); // Separate values by tabs
                }
                copiedText.append('\n'); // Separate rows by newlines
            }

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(copiedText.toString()), null);
        }
    }
    private void pasteCellsFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                String[] rows = clipboardData.split("\n");

                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();

                for (int i = 0; i < rows.length; i++) {
                    String[] values = rows[i].split("\t");
                    for (int j = 0; j < values.length; j++) {
                        if (selectedRow + i < table.getRowCount() && selectedColumn + j < table.getColumnCount()) {
                            String trimmedValue = values[j].trim();
                            table.setValueAt(trimmedValue, selectedRow + i, selectedColumn + j);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
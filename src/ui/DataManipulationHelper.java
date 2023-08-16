package ui;

import controller.DataTableController;
import controller.ErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class DataManipulationHelper {
    private final DataTableController controller;
    private final JTable table;

    public DataManipulationHelper(DataTableController controller, JTable table) {
        this.controller = controller;
        this.table = table;
    }


    public void deleteSelectedBlock(int startRow, int startColumn, int endRow, int endColumn, boolean offset) {
        controller.deleteBlock(startRow, startColumn, endRow, endColumn, offset);
    }

    public void copySelectedCellsToClipboard(int[] selectedRows, int[] selectedColumns) {
        if (areValidSelection(selectedRows, selectedColumns)) {
            String copiedText = extractSelectedCellsData(selectedRows, selectedColumns);
            copyToClipboard(copiedText);
        }
    }

    private boolean areValidSelection(int[] selectedRows, int[] selectedColumns) {
        return selectedRows != null && selectedColumns != null && selectedRows.length > 0 && selectedColumns.length > 0;
    }

    private String extractSelectedCellsData(int[] selectedRows, int[] selectedColumns) {
        StringBuilder copiedText = new StringBuilder();

        for (int row : selectedRows) {
            for (int column : selectedColumns) {
                Object cellValue = getCellValue(row, column);
                copiedText.append(cellValue).append(' ');
            }
            copiedText.append('\n');
        }

        return copiedText.toString();
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    private Object getCellValue(int row, int column) {
        return row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()
                ? table.getValueAt(row, column)
                : null;
    }

    public void pasteCellsFromClipboard(boolean offset, int selectedRow, int selectedColumn) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                byte[][] byteArray = parseClipboardDataAsBytesArray(clipboardData);

                if (selectedRow >= 0 && selectedColumn >= 0) {
                    controller.insertBytesAtPosition(selectedRow, selectedColumn, byteArray, offset);
                } else {
                    System.out.println("No cell selected for paste.");
                }
            } catch (NumberFormatException | UnsupportedFlavorException | IOException e) {
                ErrorHandler.showError("Error pasting data from clipboard: " + e.getMessage());
            }
        }
    }
    private byte[][] parseClipboardDataAsBytesArray(String clipboardData) {
        String[] rowStrings = clipboardData.trim().split("\n");
        byte[][] byteArray = new byte[rowStrings.length][];

        for (int i = 0; i < rowStrings.length; i++) {
            String[] columnStrings = rowStrings[i].trim().split("\\s+");
            byteArray[i] = new byte[columnStrings.length];

            for (int j = 0; j < columnStrings.length; j++) {
                try {
                    int decimalValue = Integer.parseInt(columnStrings[j], 16);
                    byteArray[i][j] = (byte) decimalValue;
                } catch (NumberFormatException e) {
                    byteArray[i][j] = 0;
                }
            }
        }
        return byteArray;
    }

    public void cutSelectedCells(boolean offset, int[] selectedRows, int[] selectedColumns) {
        copySelectedCellsToClipboard(selectedRows, selectedColumns);
        handleDeleteAction(offset, selectedRows, selectedColumns);
    }

    public void handleDeleteAction(boolean offset, int[] selectedRows, int[] selectedColumns) {
        if (areValidSelection(selectedRows, selectedColumns)) {
            int startRow = selectedRows[0];
            int startColumn = selectedColumns[0];
            int endRow = selectedRows[selectedRows.length - 1];
            int endColumn = selectedColumns[selectedColumns.length - 1];

            try {
                deleteSelectedBlock(startRow, startColumn, endRow, endColumn, offset);
            } catch (Exception e) {
                ErrorHandler.showError("Error deleting selected block: " + e.getMessage());
            }
        }
    }
}
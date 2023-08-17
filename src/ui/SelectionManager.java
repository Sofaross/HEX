package ui;

import controller.selectionListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class SelectionManager {
    private final JTable table;
    private int[] shiftSelectionStart;
    private int[] shiftSelectionEnd;
    private final List<selectionListener> listeners = new ArrayList<>();

    public SelectionManager(JTable table) {
        this.table = table;
        attachSelectionListeners();
    }
    private void attachSelectionListeners() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleShiftSelection();
            }
        });

        table.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleShiftSelection();
            }
        });
    }

    public void addSelectionListener(selectionListener listener) {
        listeners.add(listener);
    }

    public void handleShiftSelection() {
        int anchorRow = table.getSelectionModel().getAnchorSelectionIndex();
        int anchorColumn = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();

        int leadRow = table.getSelectionModel().getLeadSelectionIndex();
        int leadColumn = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();

        int minRow = Math.min(anchorRow, leadRow);
        int maxRow = Math.max(anchorRow, leadRow);

        int minColumn = Math.min(anchorColumn, leadColumn);
        int maxColumn = Math.max(anchorColumn, leadColumn);

        shiftSelectionStart = new int[]{minRow, minColumn};
        shiftSelectionEnd = new int[]{maxRow, maxColumn};

        updateShiftHighlight();
    }

    private void updateShiftHighlight() {
        List<int[]> cellsToHighlight = new ArrayList<>();
        for (int row = shiftSelectionStart[0]; row <= shiftSelectionEnd[0]; row++) {
            for (int column = shiftSelectionStart[1]; column <= shiftSelectionEnd[1]; column++) {
                cellsToHighlight.add(new int[]{row, column});
            }
        }
        for (selectionListener listener : listeners) {
            listener.onSelectionChanged(cellsToHighlight);
        }
    }

    public void clearHighlight() {
        shiftSelectionStart = null;
        shiftSelectionEnd = null;
    }
}

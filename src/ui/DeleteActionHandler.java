package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeleteActionHandler {
    private final DataTableView dataTableView;
    private int startRow = -1, startColumn = -1;

    public DeleteActionHandler(DataTableView dataTableView) {
        this.dataTableView = dataTableView;
        setupDeleteBlockAction();
    }

    public void setupDeleteBlockAction() {
        dataTableView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && e.isShiftDown()) {
                    if (startRow == -1 && startColumn == -1) {
                        startRow = dataTableView.getSelectedRow();
                        startColumn = dataTableView.getSelectedColumn();
                    } else {
                        int endRow = dataTableView.getSelectedRow();
                        int endColumn = dataTableView.getSelectedColumn();
                        dataTableView.deleteSelectedBlock(startRow, startColumn, endRow, endColumn);
                        startRow = -1;
                        startColumn = -1;
                    }
                }
            }
        });
    }
}
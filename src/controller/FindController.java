package controller;

import java.util.List;

public class FindController {
    private final DataTableController dataTableController;

    public FindController(DataTableController dataTableController) {
        this.dataTableController = dataTableController;
    }

    public void performSearchWithMask(byte[] mask) {
        List<int[]> foundCells = dataTableController.findCellsWithMask(mask);
        dataTableController.highlightCells(foundCells);

    }
}
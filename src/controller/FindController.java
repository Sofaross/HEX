package controller;

import model.ByteSearch;
import ui.DataTableView;
import java.util.ArrayList;
import java.util.List;

public class FindController {
    private final DataTableController controller;
    private final DataTableView tableView;
    private byte[] mask;

    public FindController(DataTableController dataTableController,DataTableView tableView) {
        this.controller = dataTableController;
        this.tableView = tableView;
    }

    public void performSearchWithMask() {
        List<int[]> foundCells = findCellsWithMask();
        tableView.highlightCells(foundCells);
    }
    public void searchSplit(String findText) {
        String[] bytePairs = findText.split(" ");

        mask = new byte[bytePairs.length];
        for (int i = 0; i < bytePairs.length; i++) {
            String bytePair = bytePairs[i];
            if (bytePair.length() != 2) {
                return;
            }
            if (bytePair.equals("??")) {
                mask[i] = -1;
            } else {
                try {
                    int intValue = Integer.parseInt(bytePair, 16);
                    if (intValue < 0 || intValue > 255) {
                        return;
                    }
                    mask[i] = (byte) intValue;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    public List<int[]> findCellsWithMask() {
        List<int[]> foundCells = new ArrayList<>();
        byte[] data = controller.getHexEditor().getData();

        int startIndex = 0;
        while (startIndex <= data.length - mask.length) {
            int index = ByteSearch.searchWithMask(data, mask, startIndex);
            if (index != -1) {
                List<int[]> sequenceCells = new ArrayList<>();
                for (int j = 0; j < mask.length; j++) {
                    int row = index / controller.getColumnCount();
                    int column = index % controller.getColumnCount();
                    sequenceCells.add(new int[]{row, column});
                    index++;
                }
                foundCells.addAll(sequenceCells);
                startIndex = index;
            } else {
                break;
            }
        }
        return foundCells;
    }
}
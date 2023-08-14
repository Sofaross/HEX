package controller;

import model.ByteSearch;
import ui.DataTableView;
import java.util.ArrayList;
import java.util.List;

public class FindController {
    private final DataTableController dataController;
    private final DataTableView tableView;
    private byte[] searchMask;

    public FindController(DataTableController dataTableController,DataTableView tableView) {
        this.dataController = dataTableController;
        this.tableView = tableView;
    }

    public void performSearchWithMask() {
        List<int[]> foundCells = findCellsWithMask();
        tableView.highlightCells(foundCells);
    }
    public void parseSearchText(String findText) {
        String[] bytePairs = findText.split(" ");

        searchMask = new byte[bytePairs.length];
        for (int i = 0; i < bytePairs.length; i++) {
            String bytePair = bytePairs[i];
            if (bytePair.length() != 2) {
                return;
            }
            if (bytePair.equals("??")) {
                searchMask[i] = -1;
            } else {
                try {
                    int intValue = Integer.parseInt(bytePair, 16);
                    if (intValue < 0 || intValue > 255) {
                        return;
                    }
                    searchMask[i] = (byte) intValue;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    public List<int[]> findCellsWithMask() {
        List<int[]> foundCells = new ArrayList<>();
        byte[] data = dataController.getHexEditor().getData();

        int startIndex = 0;
        while (startIndex <= data.length - searchMask.length) {
            int index = ByteSearch.searchWithMask(data, searchMask, startIndex);
            if (index != -1) {
                List<int[]> sequenceCells = new ArrayList<>();
                for (int j = 0; j < searchMask.length; j++) {
                    int row = index / dataController.getColumnCount();
                    int column = index % dataController.getColumnCount();
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
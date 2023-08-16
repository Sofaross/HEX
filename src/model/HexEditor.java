package model;

import controller.ErrorHandler;
import java.io.*;
import java.util.Arrays;

public class HexEditor {
    private  byte[] data;
    private File file;

    public HexEditor(){
        data = new byte[0];
        file = null;
    }

    public HexEditor(String fileName) {
        initializeFromFile(fileName);
    }

    public void createEmptyDataArray(int length) {
        data = new byte[length];
        Arrays.fill(data, (byte) 0);
    }

    public void setDataByte(int index, byte value) {
        if (isDataValid(index)) {
            data[index] = value;
        } else {
            System.out.println("Ошибка: недопустимый индекс");
        }
    }

    public void zeroFillRange(int startIndex, int endIndex) {
        if (isDataValid(startIndex) && isDataValid(endIndex)) {
            for (int i = startIndex; i <= endIndex; i++) {
                data[i] = 0;
            }
        }
    }

    public void deleteRange(int startIndex, int endIndex) {
        if (isDataValid(startIndex) && isDataValid(endIndex)) {
            int rangeLength = endIndex - startIndex + 1;
            byte[] newData = new byte[data.length - rangeLength];

            System.arraycopy(data, 0, newData, 0, startIndex);
            System.arraycopy(data, endIndex + 1, newData, startIndex, data.length - endIndex - 1);

            data = newData;
        }
    }

    public void replaceBytes(int index, byte[] newBytes) {
        if (isDataValid(index) && newBytes != null) {
            int endIndex = index + newBytes.length - 1;
            if (isDataValid(endIndex)) {
                System.arraycopy(newBytes, 0, data, index, newBytes.length);
            } else {
                ErrorHandler.showError("Ошибка: недопустимый диапазон для замены");
            }
        }
    }

    public void insertBytes(int index, byte[] newBytes) {
        if (newBytes != null && index >= 0 && index <= data.length) {
            byte[] newData = new byte[data.length + newBytes.length];
            System.arraycopy(data, 0, newData, 0, index);
            System.arraycopy(newBytes, 0, newData, index, newBytes.length);
            System.arraycopy(data, index, newData, index + newBytes.length, data.length - index);
            data = newData;
        } else {
            ErrorHandler.showError("Ошибка: недопустимые параметры для вставки");
        }
    }


    public byte[] getData() {
        return data;
    }

    public int getByteCount() {
        return data.length;
    }

    public File getFile() {
        return file;
    }

    public void writeToFile(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
            this.file = file;
        } catch (IOException e) {
            ErrorHandler.showError("Ошибка записи файла: " + e.getMessage());
        }
    }

    public String[][] convertDataToHexMatrix(int numRows, int numColumns) {
        if (data == null) {
            return null;
        }

        String[][] hexData = new String[numRows][numColumns];
        int dataSize = data.length;
        int totalCells = numRows * numColumns;
        int remainingCells = totalCells - dataSize;
        expandRowData(remainingCells);

        populateHexDataMatrix(hexData, numColumns);

        return hexData;
    }

    private void initializeFromFile(String fileName) {
        file = new File(fileName);
        data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(data);
            if (bytesRead == -1) {
                ErrorHandler.showError("Файл пустой или достигнут конец файла.");
            }
        } catch (IOException e) {
            ErrorHandler.showError("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private boolean isDataValid(int index) {
        return data != null && index >= 0 && index < data.length;
    }

    private void populateHexDataMatrix(String[][] hexData, int numColumns) {
        for (int i = 0; i < data.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            if (data[i] != 0) {
                hexData[row][column] = String.format("%02X", data[i]);
            } else {
                hexData[row][column] = "00";
            }
        }
    }

    public void expandRowData(int columnCount) {
        byte[] zerosArray = new byte[columnCount];
        Arrays.fill(zerosArray, (byte) 0);

        byte[] combinedArray = new byte[data.length + zerosArray.length];
        System.arraycopy(data, 0, combinedArray, 0, data.length);
        System.arraycopy(zerosArray, 0, combinedArray, data.length, zerosArray.length);
        data = combinedArray;
    }

    public void expandColumnData(int columnCount){
        int insertCount = (data.length) / (columnCount);
        int newDataLength = data.length + insertCount;

        byte[] newArray = new byte[newDataLength];
        int newIndex = 0;

        for (int i = 0; i < data.length; i++) {
            newArray[newIndex++] = data[i];
            if ((i + 1) % columnCount == 0) {
                newArray[newIndex++] = 0;
            }
        }
        data = newArray;
    }
}

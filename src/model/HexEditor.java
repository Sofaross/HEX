package model;

import controller.ErrorHandler;

import java.io.*;
import java.util.Arrays;

public class HexEditor {
    private  byte[] data;
    private File file;
    public HexEditor(){
        data = new byte[0];
        file= null;
    }
    public HexEditor(String fileName) {
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
    public void createEmptyDataArray(int length) {
        data = new byte[length];
        Arrays.fill(data, (byte) 0);
    }
    public void setByte(int index, byte value) {
        if (data != null && index >= 0 && index < data.length) {
            data[index] = value;
        } else {
            System.out.println("Ошибка: недопустимый индекс");
        }
    }
    public byte[] getData() {
        return data;
    }
    public int getByteCount() {
        return data.length;
    }
    public File getFileName() {
        return (file == null) ? null : file;
    }
    public void write(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
            this.file = file;
        } catch (IOException e) {
            ErrorHandler.showError("Ошибка записи файла: " + e.getMessage());
        }
    }
    public String[][] updateDataHex(int numRows, int numColumns) {
        if (data == null) {
            return null;
        }

        String[][] hexData = new String[numRows][numColumns];
        int dataSize = data.length;
        int totalCells = numRows * numColumns;
        int remainingCells = totalCells - dataSize;
        addRow(remainingCells);

        for (int i = 0; i < data.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            if (data[i] != 0) {
                hexData[row][column] = String.format("%02X", data[i]);
            }
            else hexData[row][column] = "00";
        }
        return hexData;
    }

    public void addRow(int columnCount) {
        byte[] zerosArray = new byte[columnCount];
        Arrays.fill(zerosArray, (byte) 0);

        byte[] combinedArray = new byte[data.length + zerosArray.length];
        System.arraycopy(data, 0, combinedArray, 0, data.length);
        System.arraycopy(zerosArray, 0, combinedArray, data.length, zerosArray.length);
        data=combinedArray;
    }
    public void addColumn(int columnCount){
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
        data=newArray;
    }
}

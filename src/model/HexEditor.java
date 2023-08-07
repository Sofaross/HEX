package model;

import controller.ErrorHandler;

import java.io.*;

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
    public void updateData(byte[] newData) {
        this.data = newData;
    }
    public void setByte(int index, byte value) {
        if (data != null && index >= 0 && index < data.length) {
            data[index] = value;
        } else {
            System.out.println("Ошибка: недопустимый индекс");
        }
    }
    public byte getByte(int index) {
        if (index >= 0 && index < data.length) {
            return data[index];
        } else {
            System.out.println("Ошибка: недопустимый индекс");
            return 0;
        }
    }
    public byte[] getData() {
        return data;
    }
    public int getByteCount() {return data.length;}
    public File getFileName() {
        if (file == null) {
            return null;
        }
        return file;
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

        for (int i = 0; i < data.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            hexData[row][column] = String.format("%02X", data[i]);
        }

        if (remainingCells > 0) {
            int lastRow = dataSize / numColumns;
            int lastColumn = dataSize % numColumns;

            for (int i = dataSize; i < totalCells; i++) {
                int row = i / numColumns;
                int column = i % numColumns;

                if (column <= lastColumn && row == lastRow) {
                    hexData[row][column] = String.format("%02X", data[i - lastColumn]);
                } else {
                    hexData[row][column] = "00";
                }
            }
        }

        return hexData;
    }
}

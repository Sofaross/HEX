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

    public void zeroFillRange(int index) {
        if (isDataValid(index)) {
            data[index] = 0;
        }
    }

    public void deleteRange(int index) {
        if (isDataValid(index)) {
            byte[] newData = new byte[data.length - 1];

            System.arraycopy(data, 0, newData, 0, index);
            System.arraycopy(data, index + 1, newData, index, data.length - index - 1);

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
        try (FileOutputStream outputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            int bufferSize = 1024*1024;
            int offset = 0;

            while (offset < data.length) {
                int bytesToWrite = Math.min(bufferSize, data.length - offset);
                bufferedOutputStream.write(data, offset, bytesToWrite);
                offset += bytesToWrite;
            }

            this.file = file;
        } catch (IOException e) {
            ErrorHandler.showError("Ошибка записи файла: " + e.getMessage());
        }
    }


    private void initializeFromFile(String fileName) {
        file = new File(fileName);

        long maxFileSize = 1024 * 1024 * 50;

        int blockSize = 1024 * 1024;

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[blockSize];
            int bytesRead;
            long totalBytesRead = 0;

            while (totalBytesRead < maxFileSize && (bytesRead = bufferedInputStream.read(buffer, 0, blockSize)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            data = outputStream.toByteArray();
        } catch (IOException e) {
            ErrorHandler.showError("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private boolean isDataValid(int index) {
        return data != null && index >= 0 && index < data.length;
    }

    public String[][] convertDataToHexMatrix(int numRows, int numColumns) {
        if (data == null) {
            return null;
        }

        int dataSize = data.length;
        int totalCells = numRows * numColumns;
        int remainingCells = totalCells - dataSize;
        expandRowData(remainingCells);
        String[][] hexData = new String[numRows][numColumns];
        StringBuilder hexStringBuilder = new StringBuilder(data.length*2);

        for (int i = 0; i < data.length; i++) {
            int intValue = data[i] & 0xFF;
            String hexValue = Integer.toHexString(intValue);

            if (hexValue.length() < 2) {
                hexValue = "0" + hexValue;
            }

            hexStringBuilder.append(hexValue);

            hexStringBuilder.append(" ");

            if ((i + 1) % numColumns == 0) {
                hexData[i / numColumns] = hexStringBuilder.toString().split(" ");
                hexStringBuilder.setLength(0);
            }
        }

        return hexData;
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

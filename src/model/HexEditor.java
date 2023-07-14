package model;

import java.io.*;

public class HexEditor {
    private byte[] data;
    private File file;
    public HexEditor(){
        data = new byte[0];
        file= null;
    }
    public HexEditor(String fileName) {
        try {
            file = new File(fileName);
            data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setByte(int index, byte value) {
        if (index >= 0 && index < data.length) {
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
        return file;
    }
    public void write(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytesToWrite = data;
            outputStream.write(bytesToWrite);
            this.file = file;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[][] updateDataHex(int numRows,int numColumns){
        String[][] hexData = new String[numRows][numColumns];
        for (int i = 0; i < data.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            hexData[row][column] = String.format("%02X", data[i]);
        }
        return hexData;
    }
    public String[][] updateDataByte(int numRows,int numColumns){
        String[][] hexData = new String[numRows][numColumns];
        for (int i = 0; i < data.length; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            hexData[row][column] = String.valueOf(data[i]);
        }
        return hexData;
    }
    public String dataToString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(b).append(" ");
        }
        return sb.toString();
    }
    public  String byteArrayToHexString() {
        StringBuilder hexString = new StringBuilder();
        for (byte b : data) hexString.append(String.format("%02X", b));
        return hexString.toString();
    }
    public void hexStringToByteArray(String hexString) {
        int length = hexString.length();
        int dataLength = data.length;
        for (int i = 0; i < length && i/2 < dataLength; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
    }

}

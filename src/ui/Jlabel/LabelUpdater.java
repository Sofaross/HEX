package ui.Jlabel;

import model.HexEditor;

import javax.swing.*;

public class LabelUpdater {
    public static String byteCount(HexEditor hexEditor) {
        try {
            int byteCount = hexEditor.getByteCount();
            return ("Byte count: " + byteCount);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating byte count label: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }

    //    Конвертация hex байта в десятичное значение без знака
    public static String hexToUnsignedDecimal(String hexByte) {
        int decimalValue = Integer.parseInt(hexByte, 16);
        return ("Signed: " + decimalValue);
    }
    //  Конвертация hex байта в десятичное значение со знаком
    public static String hexToSignedDecimal(String hexByte) {
        int decimalValue = Integer.parseInt(hexByte, 16);
        if (decimalValue > 127) {
            decimalValue -= 256;
        }
        return ("Unsigned: " + decimalValue);
    }
    public static String fileName(HexEditor hexEditor){
        if (hexEditor.getFileName() != null ) {
            return ("File: " + hexEditor.getFileName().getName());
        } else return "";
    }

    public static String hexToAscii(String cellValue) {
        if (cellValue != null && !cellValue.isEmpty()) {
            try {
                int decimalValue = Integer.parseInt(cellValue, 16);
                return ("Ascii: " + getAsciiChar(decimalValue));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    private static char getAsciiChar(int decimalValue){
        if (decimalValue >= 0 && decimalValue <=127){
            return (char) decimalValue;
        } else{
            return '.';
        }
    }

}

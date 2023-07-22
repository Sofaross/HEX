package controller;

import model.HexEditor;


public class LabelController {
    public static String byteCount(HexEditor hexEditor) {
        try {
            int byteCount = hexEditor.getByteCount();
            return ("Byte count: " + byteCount);
        } catch (Exception e) {
            ErrorHandler.showError("Error updating byte count label");}
        return "";
    }
    public static String hexToUnsignedDecimal(String hexByte) {
        if (hexByte != null && !hexByte.isEmpty()) {
            int decimalValue = Integer.parseInt(hexByte, 16);
            return ("Signed: " + decimalValue);
        }
        return "";
    }
    public static String hexToSignedDecimal(String hexByte) {
        if (hexByte != null && !hexByte.isEmpty()) {
            int decimalValue = Integer.parseInt(hexByte, 16);
            if (decimalValue > 127) {
                decimalValue -= 256;
            }
            return ("Unsigned: " + decimalValue);
        }
        return "";
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

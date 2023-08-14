package controller;

import model.HexEditor;
public class LabelController {
    private static final int MAX_ASCII_VALUE = 127;
    private static final int ASCII_OFFSET = 256;

    public static String byteCount(HexEditor hexEditor) {
        try {
            int byteCount = hexEditor.getByteCount();
            return ("Byte count: " + byteCount);
        } catch (Exception e) {
            ErrorHandler.showError("Error updating byte count label");
        }
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
            if (decimalValue > MAX_ASCII_VALUE) {
                decimalValue -= ASCII_OFFSET;
            }
            return ("Unsigned: " + decimalValue);
        }
        return "";
    }

    public static String fileName(HexEditor hexEditor) {
        if (hexEditor.getFileName() != null) {
            return ("File: " + hexEditor.getFileName().getName());
        }
        return "";
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

    private static char getAsciiChar(int decimalValue) {
        if (decimalValue >= 0 && decimalValue <= MAX_ASCII_VALUE) {
            return (char) decimalValue;
        } else {
            return '.';
        }
    }
}

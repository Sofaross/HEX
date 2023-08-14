package controller;

import model.HexEditor;
public class LabelController {
    private static final int MAX_ASCII_VALUE = 127;
    private static final int ASCII_OFFSET = 256;

    public static String getByteCountLabel(HexEditor hexEditor) {
        try {
            int byteCount = hexEditor.getByteCount();
            return ("Byte count: " + byteCount);
        } catch (Exception e) {
            ErrorHandler.showError("Error updating byte count label");
        }
        return "";
    }

    public static String hexToUnsignedDecimalString(String hexByte) {
        if (hexByte != null && !hexByte.isEmpty()) {
            int decimalValue = Integer.parseInt(hexByte, 16);
            return ("Signed: " + decimalValue);
        }
        return "";
    }

    public static String hexToSignedDecimalString(String hexByte) {
        if (hexByte != null && !hexByte.isEmpty()) {
            int decimalValue = Integer.parseInt(hexByte, 16);
            if (decimalValue > MAX_ASCII_VALUE) {
                decimalValue -= ASCII_OFFSET;
            }
            return ("Unsigned: " + decimalValue);
        }
        return "";
    }

    public static String getFileNameLabel(HexEditor hexEditor) {
        if (hexEditor.getFile() != null) {
            return ("File: " + hexEditor.getFile().getName());
        }
        return "";
    }

    public static String hexToAsciiString(String cellValue) {
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

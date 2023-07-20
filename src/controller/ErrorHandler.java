package controller;

import javax.swing.JOptionPane;

public class ErrorHandler {
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
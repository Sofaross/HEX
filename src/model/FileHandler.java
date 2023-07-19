package model;

import controller.ErrorHandler;

import javax.swing.*;
import java.io.File;

public class FileHandler {
    public static void saveFile(HexEditor hexEditor) {
        if (hexEditor == null) {
            ErrorHandler.showError("File not create!");
            return;
        }
        try {
            if (hexEditor.getFileName() != null) {
                hexEditor.write(hexEditor.getFileName());
            } else {
                ErrorHandler.showError("No file name specified");
            }
        } catch (Exception e) {
            ErrorHandler.showError("Error saving file: " + e.getMessage());
        }
    }

    public static void saveFileAs(HexEditor hexEditor) {
        try {
            if (hexEditor != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save File");
                int userSelection = fileChooser.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    hexEditor.write(fileToSave);
                }
            } else {
                throw new NullPointerException("File not create!");
            }
        } catch (Exception e) {
            ErrorHandler.showError("Error saving file: " + e.getMessage());
        }
    }

    public static HexEditor openFile(HexEditor hexEditor) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open file");
            int userSelection = fileChooser.showOpenDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();
                if (fileToOpen.length() == 0) {
                    ErrorHandler.showError("Selected file is empty");
                } else {
                    hexEditor = new HexEditor(fileToOpen.getAbsolutePath());
                }
            } else if (userSelection == JFileChooser.CANCEL_OPTION) {
                ErrorHandler.showError("No file selected");
            }

            if (hexEditor == null) {
                throw new NullPointerException("hexEditor is null");
            }

            return hexEditor;
        } catch (Exception e) {
            ErrorHandler.showError("Error opening file: " + e.getMessage());
            return null;
        }
    }
}

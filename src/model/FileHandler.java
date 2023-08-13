package model;
import controller.ErrorHandler;

import javax.swing.*;
import java.io.File;

public class FileHandler {
    public static void saveFile(HexEditor hexEditor) {
        if (hexEditor == null) {
            ErrorHandler.showError("File not created!");
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

    public static File chooseFileForSaving() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save File");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static void saveFileAs(HexEditor hexEditor) {
        try {
            if (hexEditor == null) {
                ErrorHandler.showError("File not created!");
                return;
            }

            File fileToSave = chooseFileForSaving();
            if (fileToSave != null) {
                hexEditor.write(fileToSave);
            }
        } catch (Exception e) {
            ErrorHandler.showError("Error saving file: " + e.getMessage());
        }
    }

    public static HexEditor openFile(HexEditor hexEditor) {
        try {
            File fileToOpen = chooseFileForOpening();
            if (fileToOpen != null && fileToOpen.length() > 0) {
                return new HexEditor(fileToOpen.getAbsolutePath());
            } else {
                ErrorHandler.showError("Selected file is empty");
            }
        } catch (Exception e) {
            ErrorHandler.showError("Error opening file: " + e.getMessage());
        }
        return hexEditor;
    }
    public static File chooseFileForOpening() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
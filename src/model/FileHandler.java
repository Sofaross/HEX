package model;

import javax.swing.*;
import java.io.File;

public class FileHandler {
    public static void saveFile( HexEditor hexEditor) {
        if (hexEditor != null) {
            if (hexEditor.getFileName() != null) {
                hexEditor.write(hexEditor.getFileName());
            } else if (hexEditor.getData() != null) saveFile(hexEditor);
        } else {
            JOptionPane.showMessageDialog(null, "File not loaded. Please open a file first", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    public static void saveFileAs(HexEditor hexEditor) {
        if (hexEditor != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save File");
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                hexEditor.write(fileToSave);
            }
        } else {
            JOptionPane.showMessageDialog(null, "File is empty", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    public static HexEditor openFile(HexEditor hexEditor) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            if (fileToOpen.length() == 0) {
                 JOptionPane.showMessageDialog(null, "Selected file is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                hexEditor = new HexEditor(fileToOpen.getAbsolutePath());
            }
        }
        return hexEditor;
    }
}

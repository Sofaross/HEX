package ui;

import controller.DataTableController;
import controller.FindController;

import javax.swing.*;
import java.awt.*;

public class SearchUi extends JPanel {
    private DataTableController controller;
    public SearchUi(DataTableController controller) {
        setLayout(new GridLayout(3, 1));
        this.controller=controller;
        JTextField findTextField = new JTextField();
        add(new JLabel("Найти:"));
        add(findTextField);

        int result = JOptionPane.showConfirmDialog(null, this, "Найти", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String findText = findTextField.getText();
            String[] bytePairs = findText.split(" ");

            byte[] searchMask = new byte[bytePairs.length];
            for (int i = 0; i < bytePairs.length; i++) {
                String bytePair = bytePairs[i];
                if (bytePair.length() != 2) {
                    return;
                }
                if (bytePair.equals("??")) {
                    searchMask[i] = -1;
                } else {
                    try {
                        int intValue = Integer.parseInt(bytePair, 16);
                        if (intValue < 0 || intValue > 255) {
                            return;
                        }
                        searchMask[i] = (byte) intValue;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            setVisible(false);
            FindController findController = new FindController(this.controller);
            findController.performSearchWithMask(searchMask);

        }
    }
}

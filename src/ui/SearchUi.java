package ui;

import javax.swing.*;
import java.awt.*;

public class SearchUi extends JPanel {
    private String findText;
    private String maskText;
    public SearchUi() {
        setLayout(new GridLayout(3, 1));

        JTextField findTextField = new JTextField();
        add(new JLabel("Найти:"));
        add(findTextField);

        JTextField maskTextField = new JTextField();
        add(new JLabel("Маска:"));
        add(maskTextField);

        int result = JOptionPane.showConfirmDialog(null, this, "Найти и маска", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            findText = findTextField.getText();
            maskText = maskTextField.getText();
        }
    }
    public String getFindText(){
        return findText;
    }
    public String getMaskText(){
        return maskText;
    }
}

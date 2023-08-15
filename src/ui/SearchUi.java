package ui;

import controller.FindController;
import javax.swing.*;
import java.awt.*;

public class SearchUi extends JPanel {

    public SearchUi(FindController controller) {
        setLayout(new GridLayout(3, 1));
        JTextField findTextField = new JTextField();
        add(new JLabel("Найти:"));
        add(findTextField);

        int result = JOptionPane.showConfirmDialog(null, this, "Найти", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            controller.parseSearchText(findTextField.getText());

        }
        setVisible(false);
    }
}
package ui;

import controller.FindController;
import javax.swing.*;
import java.awt.*;

public class SearchUi extends JPanel {

    public SearchUi(FindController controller) {
        setLayout(new GridLayout(3, 1));
        JTextField findTextField = new JTextField();
        add(new JLabel("Find:"));
        add(findTextField);

        int result = JOptionPane.showConfirmDialog(null, this, "Find", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String text = findTextField.getText().trim();
            if (!text.isEmpty()) {
                controller.parseSearchText(findTextField.getText());
            }
        }

        setVisible(false);
    }
}
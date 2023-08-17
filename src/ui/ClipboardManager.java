package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ClipboardManager {
    private final JTable table;
    private final DataManipulationHelper manipulationHelper;

    public ClipboardManager(JTable table, DataManipulationHelper manipulationHelper) {
        this.table = table;
        this.manipulationHelper = manipulationHelper;
        setupCopyKeyBinding();
    }

    private void setupCopyKeyBinding() {
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
        table.getActionMap().put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manipulationHelper.copySelectedCellsToClipboard(table.getSelectedRows(), table.getSelectedColumns());
            }
        });
    }
}

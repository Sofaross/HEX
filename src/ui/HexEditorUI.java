package ui;

import model.FileHandler;
import model.HexEditor;
import ui.Jlabel.LabelUpdater;
import controller.hexEditorListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HexEditorUI extends JFrame implements hexEditorListener {
    private final HexEditorTableView tableView;

    private HexEditor hexEditor;
    private final JLabel signedDecimalLabel;
    private final JLabel unSignedDecimalLabel;
    private final JLabel asciiLabel;

    public HexEditorUI() {
        setTitle("Hex Editor");

        // Создаем таблицу для отображения байтов
        tableView = new HexEditorTableView();
        tableView.setListener(this);
        JScrollPane scrollPane = tableView.getScrollPane();
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Создаем панель для таблицы
        JPanel tablePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        tablePanel.add(scrollPane, gbc);
        add(tablePanel, BorderLayout.CENTER);

        // Создаем панель для отображения информации
        JPanel bottomPanel = new JPanel(new GridLayout(3, 2));
        add(bottomPanel, BorderLayout.SOUTH);

        // Добавляем строку с информацией о количестве байтов
        JLabel byteCountLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(byteCountLabel);

        // Добавляем строку с информацией об открытом файле
        JLabel fileNameLabel = new JLabel();
        fileNameLabel.setPreferredSize(new Dimension(200, 20));
        bottomPanel.add(fileNameLabel);

        // Добавляем строку с информацией о байте в десятичном со знаком
        signedDecimalLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(signedDecimalLabel);

        // Добавляем строку с информацией о байте в десятичном без знака
        unSignedDecimalLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(unSignedDecimalLabel);

        // Добавляем строку с информацией о байте в ASCII
        asciiLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(asciiLabel);

        JMenuBar menuBar = new JMenuBar();

        // Создаем меню "File"
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Добавляем пункт меню "Open"
        JMenuItem openMenuItem = new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                hexEditor = FileHandler.openFile(hexEditor);
                tableView.updateTableModelHex(hexEditor);
                fileNameLabel.setText(LabelUpdater.fileName(hexEditor));
                byteCountLabel.setText(LabelUpdater.byteCount( hexEditor));
                //tableView.setColumnHeaders();
                //tableView.setRowHeaders();
            }
        });
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenu.add(openMenuItem);

        // Добавляем пункт меню "Save"
        JMenuItem saveMenuItem = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFile(hexEditor);
            }
        });
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveMenuItem);

        // Добавляем пункт меню "Save As"
        JMenuItem saveAsMenuItem = new JMenuItem(new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFileAs(hexEditor);
            }
        });
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        fileMenu.add(saveAsMenuItem);

        // Добавляем пункт меню "New"
        JMenuItem newMenuItem = new JMenuItem(new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hexEditor != null) {
                    int choice = JOptionPane.showConfirmDialog(null, "Real", "Confimation", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        hexEditor = new HexEditor();
                        tableView.createNewTable();
                        tableView.updateTableModelHex(hexEditor);
                        fileNameLabel.setText(LabelUpdater.fileName(hexEditor));
                        byteCountLabel.setText(LabelUpdater.byteCount(hexEditor));
                    }
                }
                else {
                    hexEditor = new HexEditor();
                    tableView.createNewTable();
                    tableView.updateTableModelHex(hexEditor);
                    fileNameLabel.setText(LabelUpdater.fileName(hexEditor));
                    byteCountLabel.setText(LabelUpdater.byteCount(hexEditor));}
            }
        });
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenu.add(newMenuItem);

        // Создаем меню "Options"
        JMenu optionsMenu = new JMenu("Options");

        JMenu chooseBytesMenu = new JMenu("Select number of bytes");
        optionsMenu.add(chooseBytesMenu);
        int[] byteValues = {1, 2, 4, 8};
        for (int value : byteValues) {
            JMenuItem menuItem = new JMenuItem(Integer.toString(value));
            menuItem.addActionListener(e -> {
                tableView.setColumnCount(Integer.parseInt(menuItem.getText()));
                tableView.updateTableModelHex(hexEditor);
                tableView.setColumnHeaders();
            });
            chooseBytesMenu.add(menuItem);
        }

        JMenuItem addRow = new JMenuItem("Add Row");
        addRow.addActionListener(e -> {
                if (hexEditor!=null) {
                    tableView.addRow(new Object[tableView.getColumnCount()]);
                    tableView.setRowHeaders();
                }
                else JOptionPane.showMessageDialog(null, "Create table!", "Error", JOptionPane.ERROR_MESSAGE);
        });
        optionsMenu.add(addRow);

        JMenuItem addColumn = new JMenuItem("Add Column");
        addColumn.addActionListener(e -> tableView.addColumn());
        optionsMenu.add(addColumn);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    @Override
    public void cellValueSelected(String cellValue) {
        unSignedDecimalLabel.setText(LabelUpdater.hexToUnsignedDecimal(cellValue));
        signedDecimalLabel.setText(LabelUpdater.hexToSignedDecimal(cellValue));
        asciiLabel.setText(LabelUpdater.hexToAscii(cellValue));
    }
}
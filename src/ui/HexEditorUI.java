package ui;

import model.FileHandler;
import model.HexEditor;
import ui.Jlabel.LabelUpdater;
import controller.hexEditorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HexEditorUI extends JFrame implements hexEditorListener {
    private HexEditorTableView tableView;
    private HexEditorTableView asciiTable;
    private HexEditor hexEditor;
    private JLabel decimalByteInfoLabel;

    public HexEditorUI() {
        setTitle("Hex Editor");

        // Создаем таблицу для отображения байтов
        tableView = new HexEditorTableView();
        tableView.setListener(this);
        JScrollPane scrollPane = tableView.getScrollPane();

// Создаем панель для таблиц
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
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(bottomPanel, BorderLayout.SOUTH);

        // Добавляем строку с информацией о количестве байтов
        JLabel byteCountLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(byteCountLabel);

        // Добавляем строку с информацией об открытом файле
        JLabel fileNameLabel = new JLabel();
        fileNameLabel.setPreferredSize(new Dimension(200, 20));
        bottomPanel.add(fileNameLabel);

        // Добавляем строку с информацией о байте в Ascii
        decimalByteInfoLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(decimalByteInfoLabel);

        // Создаем панель для отображения кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(buttonPanel, BorderLayout.NORTH);

        // Добавляем кнопку добваления строки
        JButton buttonAddRow = new JButton("+");
        buttonAddRow.addActionListener(e -> {
            if (hexEditor!=null) {
                tableView.addRow(new Object[tableView.getColumnCount()]);
                asciiTable.addRow(new Object[tableView.getColumnCount()]);
            }
            else JOptionPane.showMessageDialog(null, "Create table!", "Error", JOptionPane.ERROR_MESSAGE);
        });
        buttonPanel.add(buttonAddRow);

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
                asciiTable.updateTableModelAscii(hexEditor);
                fileNameLabel.setText(LabelUpdater.updateFileName(hexEditor));
                byteCountLabel.setText(LabelUpdater.updateByteCountLabel( hexEditor));
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
                        asciiTable.createNewTable();
                        tableView.updateTableModelHex(hexEditor);
                        asciiTable.updateTableModelAscii(hexEditor);
                        fileNameLabel.setText(LabelUpdater.updateFileName(hexEditor));
                        byteCountLabel.setText(LabelUpdater.updateByteCountLabel(hexEditor));
                    }
                }
                else {
                    hexEditor = new HexEditor();
                    tableView.createNewTable();
                    asciiTable.createNewTable();
                    tableView.updateTableModelHex(hexEditor);
                    asciiTable.updateTableModelAscii(hexEditor);
                    fileNameLabel.setText(LabelUpdater.updateFileName(hexEditor));
                    byteCountLabel.setText(LabelUpdater.updateByteCountLabel(hexEditor));}
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
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tableView.setColumnCount(Integer.parseInt(menuItem.getText()));
                    asciiTable.setColumnCount(Integer.parseInt(menuItem.getText()));
                    tableView.updateTableModelHex( hexEditor);
                    asciiTable.updateTableModelAscii( hexEditor);
                }
            });
            chooseBytesMenu.add(menuItem);
        }

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
        decimalByteInfoLabel.setText(LabelUpdater.updateDecimalHexInfoLabel(cellValue));
    }
    public void initAsciiTable() {
// Создаем таблицу для отображения данных в виде ASCII
        asciiTable = new HexEditorTableView();
        asciiTable.setColumnCount(tableView.getColumnCount()); // Устанавливаем колонки в соответствии с основной таблицей
        JScrollPane asciiScrollPane = asciiTable.getScrollPane();

// Добавляем таблицу в панель
        JPanel tablePanel = (JPanel) getContentPane().getComponent(0);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        tablePanel.add(asciiScrollPane, gbc);
    }

    public void showAsciiTable() {
// Показываем таблицу ASCII
        asciiTable.setVisible(true);
    }

    public void hideAsciiTable() {
// Скрываем таблицу ASCII
        asciiTable.setVisible(false);
    }
}
package ui;

import controller.*;
import model.FileHandler;
import model.HexEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class MainView extends JFrame implements hexEditorListener {
    public static final int INITIAL_COLUMN_COUNT = 8;
    private final DataTableView tableView;
    private final JScrollPane scrollPane;
    private final JLabel signedDecimalLabel;
    private final JLabel unSignedDecimalLabel;
    private final JLabel asciiLabel;
    private final JList<String> rowHeaderList;
    private final DataTableController  controller;

    public MainView() {
        setTitle("Hex Editor");

        // Создаем таблицу для отображения байтов
        tableView = new DataTableView();
        tableView.setListener(this);
        scrollPane = tableView.getScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        controller=tableView.getController();

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

        //Добавляем заголовки к строкам
        rowHeaderList = new JList<>();
        Font font = rowHeaderList.getFont();
        Font smallerFont = font.deriveFont(font.getSize() - 2f);
        rowHeaderList.setFont(smallerFont);
        rowHeaderList.setFixedCellWidth(58);

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
                controller.setHexEditor(FileHandler.openFile(controller.getHexEditor()));
                if (controller.getHexEditor() != null)
                {
                    tableView.setCustomColumnCount(INITIAL_COLUMN_COUNT);
                    tableView.updateTableModelHex();
                    setupRowHeaderList();
                    fileNameLabel.setText(LabelController.fileName(controller.getHexEditor()));
                    byteCountLabel.setText(LabelController.byteCount(controller.getHexEditor()));
                    tableView.requestFocusInWindow();
                }
            }
        });
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenu.add(openMenuItem);

        // Добавляем пункт меню "Save"
        JMenuItem saveMenuItem = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFile(controller.getHexEditor());
            }
        });
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveMenuItem);

        // Добавляем пункт меню "Save As"
        JMenuItem saveAsMenuItem = new JMenuItem(new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFileAs(controller.getHexEditor());
            }
        });
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        fileMenu.add(saveAsMenuItem);

        // Добавляем пункт меню "New"
        JMenuItem newMenuItem = new JMenuItem(new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.getHexEditor() != null) {
                    int choice = JOptionPane.showConfirmDialog(null, "Real", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        controller.setHexEditor(new HexEditor());
                        tableView.createNewTable();
                        tableView.updateTableModelHex();
                        setupRowHeaderList();
                        fileNameLabel.setText(LabelController.fileName(controller.getHexEditor()));
                        byteCountLabel.setText(LabelController.byteCount(controller.getHexEditor()));
                    }
                }
                else {
                    controller.setHexEditor(new HexEditor());
                    tableView.setCustomColumnCount(INITIAL_COLUMN_COUNT);
                    tableView.createNewTable();
                    tableView.updateTableModelHex();
                    setupRowHeaderList();
                    fileNameLabel.setText(LabelController.fileName(controller.getHexEditor()));
                    byteCountLabel.setText(LabelController.byteCount(controller.getHexEditor()));}
            }
        });
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenu.add(newMenuItem);

        // Создаем меню "Options"
        JMenu optionsMenu = new JMenu("Options");

        JMenu chooseBytesMenu = new JMenu("Select number of bytes");
        optionsMenu.add(chooseBytesMenu);
        int[] byteValues = {2, 4, 8};
        for (int value : byteValues) {
            JMenuItem menuItem = new JMenuItem(Integer.toString(value));
            menuItem.addActionListener(e -> {
                try{
                    if (controller.getHexEditor() != null){
                        tableView.setCustomColumnCount(Integer.parseInt(menuItem.getText()));
                        tableView.updateTableModelHex();
                        setupRowHeaderList();
                    } else {
                        ErrorHandler.showError("Create table!");
                    }
                } catch (Exception ex) {
                    ErrorHandler.showError("Byte representation change error");
                }

            });
            chooseBytesMenu.add(menuItem);
        }

        JMenuItem addRow = new JMenuItem("Add Row");
        addRow.addActionListener(e -> {
            try {
                if (controller.getHexEditor() != null) {
                    tableView.addRow();
                    setupRowHeaderList();
                    byteCountLabel.setText(LabelController.byteCount(controller.getHexEditor()));
                } else {
                    ErrorHandler.showError("Create table!");
                }
            } catch (Exception ex) {
                ErrorHandler.showError("Error adding row: " + ex.getMessage());
            }
        });
        optionsMenu.add(addRow);

        JMenuItem addColumn = new JMenuItem("Add Column");
        addColumn.addActionListener(e -> {
            try {
                if (controller.getHexEditor() != null) {
                    tableView.addColumn();
                    byteCountLabel.setText(LabelController.byteCount(controller.getHexEditor()));
                } else {
                    ErrorHandler.showError("Create table!");
                }
            } catch (Exception ex) {
                ErrorHandler.showError("Error adding column: " + ex.getMessage());
            }
        });
        optionsMenu.add(addColumn);

        JMenuItem find = new JMenuItem("Find");
        find.addActionListener(e -> {
            try {
                if (controller.getHexEditor() != null) {
                    FindController findController=new FindController(controller,tableView);
                    SearchUi searchUi = new SearchUi(findController);
                    getContentPane().add(searchUi);
                    findController.performSearchWithMask();
                } else {
                    ErrorHandler.showError("Create table!");
                }
            } catch (Exception ex) {
                ErrorHandler.showError("Error find: " + ex.getMessage());
            }
        });
        optionsMenu.add(find);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void setupRowHeaderList() {
        String[] rowHeaders = tableView.getRowHeaders();
        rowHeaderList.setListData(rowHeaders);
        scrollPane.setRowHeaderView(rowHeaderList);
    }
    @Override
    public void cellValueSelected(String cellValue) {
        unSignedDecimalLabel.setText(LabelController.hexToUnsignedDecimal(cellValue));
        signedDecimalLabel.setText(LabelController.hexToSignedDecimal(cellValue));
        asciiLabel.setText(LabelController.hexToAscii(cellValue));
    }
}
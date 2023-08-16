package ui;

import controller.*;
import model.FileHandler;
import model.HexEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class MainView extends JFrame implements hexEditorListener {
    public static final int INITIAL_COLUMN_COUNT = 8;
    private DataTableView tableView;
    private JScrollPane scrollPane;
    private JLabel signedDecimalLabel;
    private JLabel unSignedDecimalLabel;
    private JLabel byteCountLabel;
    private JLabel fileNameLabel;
    private JLabel asciiLabel;
    private JList<String> rowHeaderList;
    private DataTableController  controller;

    public MainView() {
        setTitle("Hex Editor");
        initUI();
        setupMenuBar();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        initTableView();
        initInfoPanel();
    }

    private void initTableView() {
        // Создаем таблицу для отображения байтов
        tableView = new DataTableView();
        tableView.setListener(this);
        scrollPane = tableView.getScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        controller = tableView.getController();

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
    }

    private void initInfoPanel() {
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
        byteCountLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));
        bottomPanel.add(byteCountLabel);

        // Добавляем строку с информацией об открытом файле
        fileNameLabel = new JLabel();
        fileNameLabel.setPreferredSize(new Dimension(200, 20));
        bottomPanel.add(fileNameLabel);

        // Добавляем строку с информацией о байте в десятичном со знаком
        signedDecimalLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));

        // Добавляем строку с информацией о байте в десятичном без знака
        unSignedDecimalLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));

        // Добавляем строку с информацией о байте в ASCII
        asciiLabel = new JLabel();
        byteCountLabel.setPreferredSize(new Dimension(100, 20));

        bottomPanel.add(signedDecimalLabel);
        bottomPanel.add(unSignedDecimalLabel);
        bottomPanel.add(asciiLabel);

    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu());
        menuBar.add(optionsMenu());
        setJMenuBar(menuBar);
    }

    private JMenu fileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem openMenuItem = new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setHexEditor(FileHandler.openFile(controller.getHexEditor()));
                if (controller.getHexEditor() != null)
                {
                    tableView.setCustomColumnCount(INITIAL_COLUMN_COUNT);
                    tableView.updateTableModelHex();
                    setupRowHeaderList();
                    fileNameLabel.setText(LabelController.getFileNameLabel(controller.getHexEditor()));
                    byteCountLabel.setText(LabelController.getByteCountLabel(controller.getHexEditor()));
                    tableView.requestFocusInWindow();
                }
            }
        });
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFile(controller.getHexEditor());
            }
        });
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem(new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileHandler.saveFileAs(controller.getHexEditor());
            }
        });
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        fileMenu.add(saveAsMenuItem);

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
                        fileNameLabel.setText(LabelController.getFileNameLabel(controller.getHexEditor()));
                        byteCountLabel.setText(LabelController.getByteCountLabel(controller.getHexEditor()));
                    }
                }
                else {
                    controller.setHexEditor(new HexEditor());
                    tableView.setCustomColumnCount(INITIAL_COLUMN_COUNT);
                    tableView.createNewTable();
                    tableView.updateTableModelHex();
                    setupRowHeaderList();
                    fileNameLabel.setText(LabelController.getFileNameLabel(controller.getHexEditor()));
                    byteCountLabel.setText(LabelController.getByteCountLabel(controller.getHexEditor()));}
            }
        });
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenu.add(newMenuItem);

        return fileMenu;
    }

    private JMenu optionsMenu() {
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
                    byteCountLabel.setText(LabelController.getByteCountLabel(controller.getHexEditor()));
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
                    byteCountLabel.setText(LabelController.getByteCountLabel(controller.getHexEditor()));
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

        return optionsMenu;
    }
    private void setupRowHeaderList() {
        String[] rowHeaders = tableView.getRowHeaders();
        rowHeaderList.setListData(rowHeaders);
        scrollPane.setRowHeaderView(rowHeaderList);
    }
    @Override
    public void cellValueSelected(String cellValue) {
        unSignedDecimalLabel.setText(LabelController.hexToUnsignedDecimalString(cellValue));
        signedDecimalLabel.setText(LabelController.hexToSignedDecimalString(cellValue));
        asciiLabel.setText(LabelController.hexToAsciiString(cellValue));
    }
}
package org.example.view.shipment;

import org.example.dao.ApostoliDAO;
import org.example.dao.PelatisDAO;
import org.example.model.Apostoli;
import org.example.model.Pelatis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import org.example.components.CustomDatePicker;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Desktop;
import java.awt.Cursor;
import java.awt.Image;

/**
 * Panel διαχείρισης αποστολών
 */
public class ApostoliManagementPanel extends JPanel {

    private final ApostoliDAO apostoliDAO;
    private final PelatisDAO pelatisDAO;

    // Table components
    private JTable table;
    private ApostoliTableModel apostoliTableModel;
    private ApostoliStatusEditor statusEditor;

    // Search components
    private JTextField txtSearch;
    private JComboBox<String> cmbCourierFilter;
    private JComboBox<String> cmbStatusFilter;
    private JComboBox<String> cmbPelatisFilter;
    private JButton btnSearch;
    private JButton btnClearSearch;
    private JCheckBox chkNotStatus;

    // Action buttons
    private JButton btnAdd;
    private JButton btnExport;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnViewDetails;
    private JButton btnStatusMyPMS;

    // Date components
    private CustomDatePicker txtFromDate;
    private CustomDatePicker txtToDate;
    private JButton btnTodayFilter;

    //currently selected
    private String selectedArithmosApostolis = null;

    public ApostoliManagementPanel() {
        this.apostoliDAO = new ApostoliDAO();
        this.pelatisDAO = new PelatisDAO();
        initComponents();
        setupLayout();
        setupEvents();
        //setupTrackingMouseListener();
        setupTrackingColumnRenderer();
        setupTrackingMouseMotionListener();
        loadData();
    }


    private void initComponents() {
        initSearchComponents();
        initActionButtons();
        initDateComponents();
        initTable();
    }

    private void initSearchComponents() {
        chkNotStatus = new JCheckBox("NOT");
        chkNotStatus.setToolTipText("Αντίστροφη αναζήτηση - εμφάνιση όσων ΔΕΝ είναι το επιλεγμένο status");

        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Αναζήτηση σε αριθμό αποστολής, παραγγελίας, παραλήπτη, πόλη ή τηλέφωνα");

        cmbCourierFilter = new JComboBox<>(new String[]{"όλα", "ACS", "ELTA", "SPEEDEX", "GENIKI"});
        cmbCourierFilter.setPreferredSize(new Dimension(100, 25));

        cmbStatusFilter = new JComboBox<>(new String[]{
                "όλα", "ΣΕ ΜΕΤΑΦΟΡΑ", "ΠΑΡΑΔΟΘΗΚΕ", "ΑΝΑΜΟΝΗ ΠΑΡΑΔΟΣΗΣ", "ΑΚΥΡΩΘΗΚΕ",
                "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ", "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ", "ΕΝΤΟΛΗ ΠΑΡΑΛΑΒΗΣ ΑΠΟ ΓΡΑΦΕΙΟ",
                "ΒΡΙΣΚΕΤΑΙ ΣΤΗΝ ΔΙΑΔΡΟΜΗ ΠΡΟΣ ΤΟ ΚΑΤΑΣΤΗΜΑ ΠΑΡΑΔΟΣΗΣ", "ΠΑΡΑΔΟΣΗ RECEPTION ΕΝΤ.ΑΠΟΣΤ",
                "ΑΡΝΗΣΗ ΧΡΕΩΣΗΣ", "ΑΔΥΝΑΜΙΑ ΠΛΗΡΩΜΗΣ", "ΜΗ ΑΠΟΔΟΧΗ ΑΠΟΣΤΟΛΗΣ", "ΑΠΕΒΙΩΣΕ",
                "ΑΠΩΝ", "ΔΥΣΠΡΟΣΙΤΗ ΠΕΡΙΟΧΗ", "ΕΛΛΙΠΗ ΔΙΚΑΙΟΛΟΓΗΤΙΚΑ", "ΑΓΝΩΣΤΟΣ ΠΑΡΑΛΗΠΤΗΣ",
                "ΑΛΛΑΓΗ ΔΙΕΥΘΥΝΣΗΣ", "ΛΑΝΘΑΣΜΕΝΗ, ΕΛΛΙΠΗΣ ΔΙΕΥΘΥΝΣΗ", "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛΗ ΑΠΟΣΤ",
                "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛ ΠΑΡ/ΠΤΗ", "ΣΥΓΚΕΚΡΙΜΕΝΗ ΗΜ/ΝΙΑ ΠΑΡΑΔΟΣΗΣ ΕΝΤΟΛΗ ΠΑΡΑΛΗΠΤΗ ΑΝΑΚΑΤΕΥΘΥΝΣΗ"
        });
        cmbStatusFilter.setPreferredSize(new Dimension(120, 25));

        cmbPelatisFilter = new JComboBox<>();
        cmbPelatisFilter.addItem("όλοι οι πελάτες");
        loadPelatesIntoComboBox();
        cmbPelatisFilter.setPreferredSize(new Dimension(200, 25));

        btnSearch = new JButton("Αναζήτηση");
        btnClearSearch = new JButton("Καθαρισμός");
    }

    private void initActionButtons() {
        btnAdd = new JButton("Εισαγωγή");
        btnAdd.setToolTipText("Εισαγωγή νέων αποστολών");
        btnAdd.setPreferredSize(new Dimension(100, 30));

        btnExport = new JButton("Εξαγωγή Excel");
        btnExport.setToolTipText("Εξαγωγή τρεχόντων αποτελεσμάτων σε Excel");
        btnExport.setPreferredSize(new Dimension(120, 30));
        btnExport.setBackground(new Color(25, 42, 86));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFont(btnExport.getFont().deriveFont(Font.BOLD));

        btnDelete = new JButton("Διαγραφή");
        btnDelete.setForeground(Color.RED);
        btnDelete.setFont(btnDelete.getFont().deriveFont(Font.BOLD));

        btnRefresh = new JButton("Ανανέωση");
        btnViewDetails = new JButton("Λεπτομέρειες");
        btnStatusMyPMS = new JButton("Status MyPMS");

        // Style action buttons
        btnAdd.setBackground(new Color(25, 42, 86)); // Navy Blue
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(btnAdd.getFont().deriveFont(Font.BOLD));

        btnDelete.setBackground(new Color(161, 14, 27)); // Cherry
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFont(btnDelete.getFont().deriveFont(Font.BOLD));

        btnRefresh.setBackground(new Color(184, 134, 11)); // Dark yellow
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(btnRefresh.getFont().deriveFont(Font.BOLD));

        btnViewDetails.setBackground(new Color(25, 42, 86)); // Navy Blue
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setFont(btnViewDetails.getFont().deriveFont(Font.BOLD));

        btnStatusMyPMS.setBackground(new Color(184, 134, 11)); // Dark yellow
        btnStatusMyPMS.setForeground(Color.BLACK);
        btnStatusMyPMS.setFont(btnStatusMyPMS.getFont().deriveFont(Font.BOLD));



        // Set button icons (handle missing icons gracefully)
        try {
            btnDelete.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));
            btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
            btnViewDetails.setIcon(new ImageIcon(getClass().getResource("/icons/view.png")));
        } catch (Exception e) {
            System.out.println("Icons not found, continuing without them");
        }
    }

    private void initDateComponents() {
        txtFromDate = new CustomDatePicker();
        txtFromDate.setToolTip("Επιλέξτε ημερομηνία 'από'");
        txtFromDate.clear(); // Καθαρισμός για να μην έχει default τιμή

        txtToDate = new CustomDatePicker();
        txtToDate.setToolTip("Επιλέξτε ημερομηνία 'έως'");
        txtToDate.clear(); // Καθαρισμός για να μην έχει default τιμή

        btnTodayFilter = new JButton("Σήμερα");
        btnTodayFilter.setToolTipText("Εμφάνιση αποστολών σήμερα");

        txtFromDate.setSelectedDate(null);
        txtToDate.setSelectedDate(null);
    }

    private void initTable() {
        apostoliTableModel = new ApostoliTableModel(pelatisDAO);
        table = new JTable(apostoliTableModel);
        ApostoliTableSetup.setupTable(table, apostoliTableModel);
        // Override selection model για καλύτερο έλεγχο
        table.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Παίρνουμε το τρέχον keyboard state
                boolean shiftPressed = (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK) !=
                        Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) ||
                        isShiftPressed();

                if (index0 != index1 && !isShiftPressed()) {
                    // Αν έχουμε range αλλά όχι shift, κάνουμε single selection
                    super.setSelectionInterval(index1, index1);
                    return;
                }
                super.setSelectionInterval(index0, index1);
            }

            private boolean isShiftPressed() {
                // Ελέγχουμε αν πατιέται shift μέσω των active modifiers
                try {
                    return (Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() != null);
                } catch (Exception e) {
                    return false;
                }
            }
        });
        statusEditor = new ApostoliStatusEditor(table, apostoliTableModel, apostoliDAO, this);
        statusEditor.setupStatusColumnEditor();
    }

    private void loadPelatesIntoComboBox() {
        List<Pelatis> pelatesList = pelatisDAO.findAll();
        for (Pelatis pelatis : pelatesList) {
            cmbPelatisFilter.addItem(pelatis.getKodikosPelati() + " - " + pelatis.getEponymiaEtairias());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel with two rows - Navy Blue Background
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(25, 42, 86)); // Navy Blue
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Logo και Title panel στο ίδιο ύψος
        JPanel logoAndTitlePanel = new JPanel(new BorderLayout());
        logoAndTitlePanel.setBackground(new Color(25, 42, 86));

        // Logo panel - αριστερά
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        logoPanel.setBackground(new Color(25, 42, 86));
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/icons/MY_PMS.jpg"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledImage);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without it");
        }

        // Title panel - στο κέντρο
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        titlePanel.setBackground(new Color(25, 42, 86));
        JLabel titleLabel = new JLabel("Αναζήτηση & Φίλτρα");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Μεγαλύτερα γράμματα
        titlePanel.add(titleLabel);

        logoAndTitlePanel.add(logoPanel, BorderLayout.WEST);
        logoAndTitlePanel.add(titlePanel, BorderLayout.CENTER);

        // First row - main filters - ΚΕΝΤΡΑΡΙΣΜΕΝΑ
        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        firstRow.setBackground(new Color(25, 42, 86)); // Navy Blue

        // Second row - date filters and buttons - ΚΕΝΤΡΑΡΙΣΜΕΝΑ
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        secondRow.setBackground(new Color(25, 42, 86)); // Navy Blue

        firstRow.add(new JLabel("Αναζήτηση:"));
        firstRow.add(txtSearch);
        firstRow.add(Box.createHorizontalStrut(15));
        firstRow.add(new JLabel("Courier:"));
        firstRow.add(cmbCourierFilter);
        firstRow.add(Box.createHorizontalStrut(10));
        firstRow.add(new JLabel("Status Courier:"));
        firstRow.add(cmbStatusFilter);
        firstRow.add(chkNotStatus);
        firstRow.add(Box.createHorizontalStrut(10));
        firstRow.add(new JLabel("Πελάτης:"));
        firstRow.add(cmbPelatisFilter);

        // Second row - date filters and buttons
        secondRow.add(new JLabel("Από:"));
        secondRow.add(txtFromDate);
        secondRow.add(new JLabel("Έως:"));
        secondRow.add(txtToDate);
        secondRow.add(btnTodayFilter);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(btnSearch);
        secondRow.add(btnClearSearch);

        // Set white text for labels in search panel
        for (Component comp : firstRow.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(Color.WHITE);
                ((JLabel) comp).setFont(comp.getFont().deriveFont(Font.BOLD));
            }
        }
        for (Component comp : secondRow.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(Color.WHITE);
                ((JLabel) comp).setFont(comp.getFont().deriveFont(Font.BOLD));
            }
        }

        // Style search components
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(Color.BLACK);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 2),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        // Style combo boxes
        cmbCourierFilter.setBackground(Color.WHITE);
        cmbCourierFilter.setForeground(Color.BLACK);
        cmbStatusFilter.setBackground(Color.WHITE);
        cmbStatusFilter.setForeground(Color.BLACK);
        cmbPelatisFilter.setBackground(Color.WHITE);
        cmbPelatisFilter.setForeground(Color.BLACK);

        // Style buttons
        btnSearch.setBackground(new Color(184, 134, 11)); // Dark yellow
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFont(btnSearch.getFont().deriveFont(Font.BOLD));
        btnClearSearch.setBackground(new Color(161, 14, 27)); // Cherry
        btnClearSearch.setForeground(Color.BLACK);
        btnClearSearch.setFont(btnClearSearch.getFont().deriveFont(Font.BOLD));
        btnTodayFilter.setBackground(new Color(25, 42, 86)); // Navy
        btnTodayFilter.setForeground(Color.BLACK);
        btnTodayFilter.setFont(btnTodayFilter.getFont().deriveFont(Font.BOLD));

        // Style checkbox
        chkNotStatus.setBackground(new Color(25, 42, 86));
        chkNotStatus.setForeground(Color.WHITE);
        chkNotStatus.setFont(chkNotStatus.getFont().deriveFont(Font.BOLD));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(25, 42, 86));
        contentPanel.add(firstRow, BorderLayout.NORTH);
        contentPanel.add(secondRow, BorderLayout.SOUTH);

        searchPanel.add(logoAndTitlePanel, BorderLayout.NORTH);
        searchPanel.add(contentPanel, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1200, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Group action buttons
        JPanel actionButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        actionButtonGroup.setBackground(new Color(245, 245, 245));
        actionButtonGroup.add(btnAdd);
        actionButtonGroup.add(btnExport);
        actionButtonGroup.add(btnDelete);

        // Group view buttons
        JPanel viewButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        viewButtonGroup.setBackground(new Color(245, 245, 245));
        viewButtonGroup.add(btnViewDetails);
        viewButtonGroup.add(btnRefresh);
        viewButtonGroup.add(btnStatusMyPMS);

        buttonPanel.add(actionButtonGroup);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewButtonGroup);

        // Status area
        JLabel statusLabel = new JLabel(" Εμφανίζονται: 0 αποστολές");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setPreferredSize(new Dimension(getWidth(), 25));
        statusLabel.setBackground(new Color(25, 42, 86)); // Navy Blue
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        statusLabel.setOpaque(true);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        setupSearchEvents();
        setupButtonEvents();
        setupTableEvents();
        setupSelectionEvents();
    }

    private void setupSearchEvents() {
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        chkNotStatus.addActionListener(e -> performSearch());

        // Allow Enter key to trigger search
        txtSearch.addActionListener(e -> performSearch());
        cmbCourierFilter.addActionListener(e -> performSearch());
        cmbStatusFilter.addActionListener(e -> performSearch());
        cmbPelatisFilter.addActionListener(e -> performSearch());

        txtFromDate.addChangeListener(e -> performSearch());
        txtToDate.addChangeListener(e -> performSearch());
        btnTodayFilter.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            txtFromDate.setSelectedDate(today);
            txtToDate.setSelectedDate(today);
            performSearch();
        });
    }

    private void setupButtonEvents() {
        btnAdd.addActionListener(e -> openAddDialog());
        btnExport.addActionListener(e -> exportToExcel());
        btnDelete.addActionListener(e -> deleteSelectedApostoli());
        btnRefresh.addActionListener(e -> loadData());
        btnViewDetails.addActionListener(e -> viewApostoliDetails());
        btnStatusMyPMS.addActionListener(e -> openStatusMyPMSTab());
    }

    private void setupTableEvents() {
        // Single MouseListener that handles all mouse events
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        if (!e.isControlDown() && !e.isShiftDown()) {
                            if (table.getSelectedRowCount() > 1 || !table.isRowSelected(row)) {
                                table.setRowSelectionInterval(row, row);
                            }
                        }
                    }
                } else {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewApostoliDetails();
                    return;
                }

                if (e.getClickCount() == 1) {
                    int column = table.columnAtPoint(e.getPoint());
                    int row = table.rowAtPoint(e.getPoint());

                    // Handle tracking link clicks
                    if (column == 2 && row >= 0) {
                        int modelRow = table.convertRowIndexToModel(row);
                        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);
                        if (arithmosApostolis != null && !arithmosApostolis.equals("-")) {
                            openTrackingURL(arithmosApostolis);
                        }
                    }

                    // Handle single selection
                    if (!e.isControlDown() && !e.isShiftDown()) {
                        if (row >= 0) {
                            table.setRowSelectionInterval(row, row);
                        }
                    }
                }
            }

            private void showContextMenu(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    if (!table.isRowSelected(row)) {
                        table.setRowSelectionInterval(row, row);
                    }

                    JPopupMenu contextMenu = new JPopupMenu();

                    JMenuItem customerCareItem = new JMenuItem("Ιστορικό Customer Care");
                    customerCareItem.addActionListener(event -> openCustomerCareTab());
                    contextMenu.add(customerCareItem);

                    JMenuItem statusMyPMSItem = new JMenuItem("Status MyPMS");
                    statusMyPMSItem.addActionListener(event -> openStatusMyPMSTab());
                    contextMenu.add(statusMyPMSItem);

                    JMenuItem infoItem = new JMenuItem("Στοιχεία Αποστολής");
                    infoItem.addActionListener(event -> openInfoTab());
                    contextMenu.add(infoItem);

                    contextMenu.show(table, e.getX(), e.getY());
                }
            }
        });
    }

    private void setupSelectionEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRowCount = table.getSelectedRowCount();
                boolean hasSelection = selectedRowCount > 0;
                boolean singleSelection = selectedRowCount == 1;

                // Κουμπιά που χρειάζονται μία συγκεκριμένη επιλογή
                btnViewDetails.setEnabled(singleSelection);
                btnStatusMyPMS.setEnabled(singleSelection);

                // Κουμπιά που δουλεύουν με πολλαπλή επιλογή
                btnDelete.setEnabled(hasSelection);
            }
        });

        // Αρχικά όλα disabled
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);
        btnStatusMyPMS.setEnabled(false);
    }

    private void openAddDialog() {
        AddApostoliDialog addDialog = new AddApostoliDialog(
                (Frame) SwingUtilities.getWindowAncestor(this)
        );
        addDialog.setVisible(true);

        addDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData();
            }
        });
    }

    private void loadData() {
        performSearch();
    }

    public void refreshData() {
        performSearch();
    }

    private void loadData(List<Apostoli> apostolesList) {
        apostoliTableModel.loadData(apostolesList);
        updateStatusLabel();
    }

    void performSearch() {
        // Αποθήκευση της επιλεγμένης γραμμής
        saveSelectedRow();

        String searchTerm = txtSearch.getText().trim();
        String courier = (String) cmbCourierFilter.getSelectedItem();
        String status = (String) cmbStatusFilter.getSelectedItem();
        boolean notStatus = chkNotStatus.isSelected();

        // Extract customer code from combo selection
        String selectedPelatis = (String) cmbPelatisFilter.getSelectedItem();
        String kodikosPelati = null;
        if (selectedPelatis != null && !selectedPelatis.equals("όλοι οι πελάτες")) {
            kodikosPelati = selectedPelatis.split(" - ")[0];
        }

        List<Apostoli> results;
        if (kodikosPelati != null) {
            results = apostoliDAO.findByPelatis(kodikosPelati);
            // Apply manual filtering for NOT logic if needed
            if (notStatus && !"όλα".equals(status)) {
                results = results.stream()
                        .filter(a -> !status.equals(a.getStatusApostolis()))
                        .collect(java.util.stream.Collectors.toList());
            }
        } else {
            LocalDate fromDate = txtFromDate.getSelectedDate();
            LocalDate toDate = txtToDate.getSelectedDate();

            if (notStatus && !"όλα".equals(status)) {
                // Get all results and filter out the selected status
                results = apostoliDAO.search(
                        searchTerm.isEmpty() ? null : searchTerm,
                        courier,
                        null, // Don't filter by status in DAO
                        fromDate,
                        toDate
                );
                results = results.stream()
                        .filter(a -> !status.equals(a.getStatusApostolis()))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                results = apostoliDAO.search(
                        searchTerm.isEmpty() ? null : searchTerm,
                        courier,
                        status,
                        fromDate,
                        toDate
                );
            }
        }

        loadData(results);

        // Επαναφορά της επιλεγμένης γραμμής
        restoreSelectedRow();
    }

    private void saveSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            selectedArithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);
        } else {
            selectedArithmosApostolis = null;
        }
    }

    private void restoreSelectedRow() {
        if (selectedArithmosApostolis != null) {
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < apostoliTableModel.getRowCount(); i++) {
                    String arithmosApostolis = (String) apostoliTableModel.getValueAt(i, 2);
                    if (selectedArithmosApostolis != null) {
                        if (arithmosApostolis != null && selectedArithmosApostolis.equals(arithmosApostolis)) {
                            int viewRow = table.convertRowIndexToView(i);
                            table.setRowSelectionInterval(viewRow, viewRow);
                            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                            break;
                        }
                    }
                }
            });
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbCourierFilter.setSelectedIndex(0);
        cmbStatusFilter.setSelectedIndex(0);
        cmbPelatisFilter.setSelectedIndex(0);
        chkNotStatus.setSelected(false);
        txtFromDate.clear();
        txtToDate.clear();
        loadData();
    }

    private void deleteSelectedApostoli() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε αποστολές για διαγραφή.",
                    "Επιλογή Αποστολών", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Είστε σίγουροι ότι θέλετε να διαγράψετε τις παρακάτω ")
                .append(selectedRows.length).append(" αποστολές;\n\n");

        // Συλλογή πληροφοριών για τις επιλεγμένες αποστολές
        for (int i = 0; i < Math.min(selectedRows.length, 5); i++) { // Εμφάνιση μέχρι 5 αποστολών
            int modelRow = table.convertRowIndexToModel(selectedRows[i]);
            String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);
            String paraliptis = (String) apostoliTableModel.getValueAt(modelRow, 6);
            messageBuilder.append("• ").append(arithmosApostolis).append(" - ").append(paraliptis).append("\n");
        }

        if (selectedRows.length > 5) {
            messageBuilder.append("... και ").append(selectedRows.length - 5).append(" ακόμα\n");
        }

        messageBuilder.append("\n⚠️ ΠΡΟΣΟΧΗ: Αυτή η ενέργεια δεν μπορεί να αναιρεθεί!");

        int confirm = JOptionPane.showConfirmDialog(this,
                messageBuilder.toString(),
                "Επιβεβαίωση Μαζικής Διαγραφής",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            int failCount = 0;
            StringBuilder failedItems = new StringBuilder();

            for (int selectedRow : selectedRows) {
                try {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

                    Apostoli apostoli = apostoliDAO.findByArithmosApostolis(arithmosApostolis);
                    if (apostoli != null) {
                        boolean success = apostoliDAO.delete(apostoli.getIdApostolis());
                        if (success) {
                            successCount++;
                        } else {
                            failCount++;
                            failedItems.append("• ").append(arithmosApostolis).append("\n");
                        }
                    } else {
                        failCount++;
                        failedItems.append("• ").append(arithmosApostolis).append(" (δε βρέθηκε)\n");
                    }
                } catch (Exception ex) {
                    failCount++;
                    ex.printStackTrace();
                }
            }

            // Εμφάνιση αποτελεσμάτων
            StringBuilder resultMessage = new StringBuilder();
            if (successCount > 0) {
                resultMessage.append("Επιτυχώς διαγράφηκαν: ").append(successCount).append(" αποστολές\n");
            }
            if (failCount > 0) {
                resultMessage.append("Αποτυχία διαγραφής: ").append(failCount).append(" αποστολές\n");
                if (failedItems.length() > 0) {
                    resultMessage.append("Προβληματικές αποστολές:\n").append(failedItems);
                }
            }

            JOptionPane.showMessageDialog(this,
                    resultMessage.toString(),
                    successCount == selectedRows.length ? "Επιτυχής Διαγραφή" : "Μερικά Προβλήματα",
                    successCount == selectedRows.length ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            loadData(); // Ανανέωση δεδομένων
        }
    }

    private void viewApostoliDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε αποστολή για προβολή λεπτομερειών.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

        ApostoliDetailsDialog detailsDialog = new ApostoliDetailsDialog(null, arithmosApostolis);

        detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData();
            }
        });

        detailsDialog.setVisible(true);
    }

    private void openCustomerCareTab() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε μια αποστολή");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

        ApostoliDetailsDialog detailsDialog = new ApostoliDetailsDialog(null, arithmosApostolis);
        // Ανοίγει στην καρτέλα Customer Care (index 0)
        detailsDialog.getTabbedPane().setSelectedIndex(0);


        detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData();
            }
        });

        detailsDialog.setVisible(true);
    }

    private void openInfoTab() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε μια αποστολή");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

        ApostoliDetailsDialog detailsDialog = new ApostoliDetailsDialog(null, arithmosApostolis);
        // Ανοίγει στην καρτέλα Στοιχεία Αποστολής (index 2)
        detailsDialog.getTabbedPane().setSelectedIndex(2);
        detailsDialog.setVisible(true);

        detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData();
            }
        });
    }

    private void updateStatusLabel() {
        Component southComponent = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (southComponent instanceof JPanel) {
            JPanel southPanel = (JPanel) southComponent;
            Component southComp = ((BorderLayout) southPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
            if (southComp instanceof JLabel) {
                JLabel statusLabel = (JLabel) southComp;

                // Υπολογισμός στατιστικών από τα δεδομένα του πίνακα
                int visibleRows = apostoliTableModel.getRowCount();
                int countACS = 0, countELTA = 0, countSPEEDEX = 0, countGENIKI = 0;
                int countParadothike = 0, countSeMatafera = 0, countSeEpexergasia = 0;
                double totalAntikatavoli = 0.0;

                for (int i = 0; i < visibleRows; i++) {
                    // Μέτρηση couriers
                    String courier = (String) apostoliTableModel.getValueAt(i, 1);
                    if (courier != null) {
                        switch (courier) {
                            case "ACS": countACS++; break;
                            case "ELTA": countELTA++; break;
                            case "SPEEDEX": countSPEEDEX++; break;
                            case "GENIKI": countGENIKI++; break;
                        }
                    }

                    // Μέτρηση status
                    Object statusObj = apostoliTableModel.getValueAt(i, 10);
                    String status = statusObj != null ? statusObj.toString() : null;
                    if (status != null) {
                        switch (status) {
                            case "ΠΑΡΑΔΟΘΗΚΕ": countParadothike++; break;
                            case "ΣΕ ΜΕΤΑΦΟΡΑ": countSeMatafera++; break;
                            case "ΣΕ ΕΠΕΞΕΡΓΑΣΙΑ": countSeEpexergasia++; break;
                        }
                    }

                    // Άθροισμα αντικαταβολής
                    String antikatavoliStr = (String) apostoliTableModel.getValueAt(i, 4);
                    if (antikatavoliStr != null && !antikatavoliStr.equals("0,00€")) {
                        try {
                            String numStr = antikatavoliStr.replace("€", "").replace(",", ".");
                            totalAntikatavoli += Double.parseDouble(numStr);
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                }

                // Δημιουργία του κειμένου με στατιστικά
                StringBuilder sb = new StringBuilder();
                sb.append(" Εμφανίζονται: ").append(visibleRows).append(" αποστολές");

                if (visibleRows > 0) {
                    sb.append(" │ Couriers: ");
                    if (countACS > 0) sb.append("ACS:").append(countACS).append(" ");
                    if (countELTA > 0) sb.append("ELTA:").append(countELTA).append(" ");
                    if (countSPEEDEX > 0) sb.append("SPEEDEX:").append(countSPEEDEX).append(" ");
                    if (countGENIKI > 0) sb.append("GENIKI:").append(countGENIKI).append(" ");

                    sb.append("│ Status: ");
                    if (countParadothike > 0) sb.append("Παραδόθηκε:").append(countParadothike).append(" ");
                    if (countSeMatafera > 0) sb.append("Σε Μεταφορά:").append(countSeMatafera).append(" ");
                    if (countSeEpexergasia > 0) sb.append("Σε Επεξεργασία:").append(countSeEpexergasia).append(" ");

                    if (totalAntikatavoli > 0) {
                        sb.append("│ Συνολική Αντικαταβολή: ").append(Math.round(totalAntikatavoli * 100.0) / 100.0).append("€");
                    }
                }

                statusLabel.setText(sb.toString());
            }
        }
    }

    private void openStatusMyPMSTab() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε μια αποστολή");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

        ApostoliDetailsDialog detailsDialog = new ApostoliDetailsDialog(null, arithmosApostolis);
        // Ανοίγει στην καρτέλα Status MyPMS (index 1)
        detailsDialog.getTabbedPane().setSelectedIndex(1);


        detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData();
            }
        });
        detailsDialog.setVisible(true);
    }

    private void setupTrackingMouseListener() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Ένα κλικ
                    int column = table.columnAtPoint(e.getPoint());
                    int row = table.rowAtPoint(e.getPoint());

                    // Έλεγχος αν το κλικ είναι στη στήλη "Αρ.Αποστολής" (στήλη 2)
                    if (column == 2 && row >= 0) {
                        int modelRow = table.convertRowIndexToModel(row);
                        String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

                        if (arithmosApostolis != null && !arithmosApostolis.equals("-")) {
                            openTrackingURL(arithmosApostolis);
                        }
                    }
                }
            }
        });
    }

    private void openTrackingURL(String trackingNumber) {
        try {
            String url = "https://www.acscourier.net/el/track-and-trace/?trackingNumber=" + trackingNumber;

            // Άνοιγμα του URL στον προεπιλεγμένο browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new java.net.URI(url));
            } else {
                // Εναλλακτική μέθοδος για συστήματα που δεν υποστηρίζουν Desktop
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    Runtime.getRuntime().exec("xdg-open " + url);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά το άνοιγμα του link tracking: " + ex.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void setupTrackingColumnRenderer() {
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value != null && !value.equals("-")) {
                    setText("<html><u><font color='blue'>" + value + "</font></u></html>");
                } else {
                    setText(value != null ? value.toString() : "");
                }

                return c;
            }
        });
    }

    private void setupTrackingMouseMotionListener() {
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                // Έλεγχος αν είμαστε στη στήλη αριθμού αποστολής (στήλη 2)
                if (column == 2 && row >= 0) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

                    if (arithmosApostolis != null && !arithmosApostolis.equals("-")) {
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    private void exportToExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Αποθήκευση Excel αρχείου");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx");
                }

                @Override
                public String getDescription() {
                    return "Excel Files (*.xlsx)";
                }
            });

            String defaultFileName = "apostoles_" +
                    java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                    selectedFile = new java.io.File(selectedFile.getAbsolutePath() + ".xlsx");
                }

                org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Αποστολές");

                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                String[] headers = {
                        "#", "Courier", "Αρ. Αποστολής", "Ημ. Παραλαβής", "Αντικαταβολή",
                        "Αποστολέας", "Παραλήπτης", "Πόλη", "Διεύθυνση", "ΤΚ",
                        "Τηλ. Σταθερό", "Κινητό", "Ημέρες", "Status", "MyPMS Status", "Σχόλια"
                };

                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                List<Apostoli> allApostoles = apostoliDAO.findAll();

                for (int i = 0; i < apostoliTableModel.getRowCount(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);

                    // Παίρνουμε το arithmos apostolis για να βρούμε το σωστό apostoli object
                    String arithmosApostolis = (String) apostoliTableModel.getValueAt(i, 2);
                    Apostoli apostoli = null;

                    // Βρίσκουμε το apostoli object από τη λίστα
                    for (Apostoli a : allApostoles) {
                        if (a.getArithmosApostolis() != null && a.getArithmosApostolis().equals(arithmosApostolis)) {
                            apostoli = a;
                            break;
                        }
                    }

                    // Παίρνουμε τη διεύθυνση και ΤΚ του παραλήπτη από την αποστολή
                    String diefthinsiParalipti = "";
                    String tkParalipti = "";
                    String sxolia = "";
                    if (apostoli != null) {
                        diefthinsiParalipti = apostoli.getDiefthinsi() != null ? apostoli.getDiefthinsi() : "";
                        tkParalipti = apostoli.getTkParalipti() != null ? apostoli.getTkParalipti() : "";
                        sxolia = apostoli.getSxolia() != null ? apostoli.getSxolia() : "";
                    }

                    // Γεμίζουμε τα κελιά μέχρι την πόλη (στήλες 0-7)
                    for (int j = 0; j < 8; j++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
                        Object value = apostoliTableModel.getValueAt(i, j);
                        if (value != null) {
                            if ("TICK_IMAGE".equals(value.toString())) {
                                cell.setCellValue("ΠΑΡΑΔΟΘΗΚΕ");
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                    }

                    // Προσθέτουμε διεύθυνση και ΤΚ παραλήπτη (στήλες 8-9)
                    org.apache.poi.ss.usermodel.Cell cellDiefthinsi = row.createCell(8);
                    cellDiefthinsi.setCellValue(diefthinsiParalipti.isEmpty() ? "-" : diefthinsiParalipti);

                    org.apache.poi.ss.usermodel.Cell cellTk = row.createCell(9);
                    cellTk.setCellValue(tkParalipti.isEmpty() ? "-" : tkParalipti);

                    // Συνεχίζουμε με τα υπόλοιπα κελιά (στήλες 10-14)
                    for (int j = 8; j < apostoliTableModel.getColumnCount() - 1; j++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(j + 2);
                        Object value = apostoliTableModel.getValueAt(i, j);
                        if (value != null) {
                            if ("TICK_IMAGE".equals(value.toString())) {
                                cell.setCellValue("ΠΑΡΑΔΟΘΗΚΕ");
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                    }

                    // Προσθέτουμε τα σχόλια στην τελευταία στήλη (στήλη 15)
                    org.apache.poi.ss.usermodel.Cell cellSxolia = row.createCell(15);
                    cellSxolia.setCellValue(sxolia.isEmpty() ? "-" : sxolia);
                }

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(selectedFile)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                JOptionPane.showMessageDialog(this,
                        "Η εξαγωγή ολοκληρώθηκε επιτυχώς!\n\n" +
                                "Αρχείο: " + selectedFile.getName() + "\n" +
                                "Εγγραφές: " + apostoliTableModel.getRowCount(),
                        "Επιτυχής Εξαγωγή",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την εξαγωγή: " + ex.getMessage(),
                    "Σφάλμα Εξαγωγής",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public ApostoliDAO getApostoliDAO() {
        return apostoliDAO;
    }
}
package org.pms.shipments.ui.panels;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pms.firebase.FirebaseSyncService;
import org.pms.shipments.model.TrackingDetail;
import org.pms.shipments.service.ACSTrackingDetailsService;
import org.pms.shipments.service.TrackingStatusFilter;
import org.pms.shipments.ui.components.CustomDatePicker;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Desktop;
import java.awt.Cursor;
import java.awt.Image;
import java.util.Map;
import java.util.stream.Collectors;

import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.ui.dialogs.*;
import org.pms.constants.UIConstants;

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
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnViewDetails;
    private JButton btnEkremotites;
    private JButton btnAddEkremotitaForApostoli;

    // Date components
    private CustomDatePicker txtFromDate;
    private CustomDatePicker txtToDate;
    private JButton btnTodayFilter;

    //currently selected
    private String selectedArithmosApostolis = null;



    private boolean isRestoring = false;

    private FirebaseSyncService firebaseSyncService;

    private int currentPage = 0;
    private static final int ROWS_PER_PAGE = 250;
    private List<Apostoli> allApostoli;
    private JButton btnFirstPage;
    private JButton btnPreviousPage;
    private JButton btnNextPage;
    private JButton btnLastPage;
    private JLabel lblPageInfo;

    private boolean isAscendingSort = true;
    private int lastSortedColumn = -1;
    private List<? extends RowSorter.SortKey> lastSortKeys = null;

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
                "ΟΛΕΣ ΟΙ ΑΠΟΣΤΟΛΕΣ", "ΣΕ ΔΙΑΚΙΝΗΣΗ" , "ΠΑΡΑΔΟΘΗΚΕ", "ΑΚΥΡΩΘΗΚΕ",
                "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ", "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ", "ΑΔΙΑΚΙΝΗΤΟ",
                "ΔΕΝ ΒΡΕΘΗΚΕ"
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
        btnDelete = new JButton("Διαγραφή");
        btnRefresh = new JButton();
        btnRefresh.setPreferredSize(new Dimension(32, 32));
        btnRefresh.setToolTipText("Ανανέωση δεδομένων (F5)");
        btnRefresh.setBorderPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setOpaque(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));


        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnRefresh.setContentAreaFilled(true);
                btnRefresh.setBackground(new Color(255, 255, 255, 100));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnRefresh.setContentAreaFilled(false);
                btnRefresh.setBackground(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnRefresh.setContentAreaFilled(true);
                btnRefresh.setBackground(new Color(255, 255, 255, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRefresh.setContentAreaFilled(false);
                btnRefresh.setBackground(null);
            }
        });
        btnViewDetails = new JButton("Λεπτομέρειες");

        btnDelete.setBackground(UIConstants.BUTTON_CHERRY_BG);
        btnDelete.setForeground(UIConstants.BUTTON_CHERRY_TEXT);
        btnDelete.setFont(UIConstants.BUTTON_FONT);

        btnRefresh.setBackground(UIConstants.BUTTON_ACCENT_BG);
        btnRefresh.setForeground(UIConstants.BUTTON_ACCENT_TEXT);
        btnRefresh.setFont(UIConstants.BUTTON_FONT);

        btnViewDetails.setBackground(UIConstants.BUTTON_PRIMARY_BG);
        btnViewDetails.setForeground(UIConstants.BUTTON_PRIMARY_TEXT);
        btnViewDetails.setFont(UIConstants.BUTTON_FONT);


        // Set button icons (handle missing icons gracefully)
        try {
            btnDelete.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));

            ImageIcon refreshIcon = new ImageIcon(getClass().getResource("/icons/refresh.png"));
            Image scaledRefresh = refreshIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            btnRefresh.setIcon(new ImageIcon(scaledRefresh));

            btnViewDetails.setIcon(new ImageIcon(getClass().getResource("/icons/view.png")));
        } catch (Exception e) {
            btnRefresh.setText("⟳");
            btnRefresh.setFont(new Font("Arial", Font.BOLD, 18));
            btnRefresh.setForeground(Color.WHITE);
        }

        btnEkremotites = new JButton("Εκκρεμότητες");
        btnEkremotites.setBackground(new Color(251, 224, 0));
        btnEkremotites.setForeground(Color.BLACK);
        btnEkremotites.setFont(btnEkremotites.getFont().deriveFont(Font.BOLD, 12f));
        btnEkremotites.setPreferredSize(new Dimension(150, 35));
        btnEkremotites.setOpaque(true);
        btnEkremotites.setBorderPainted(false);
        btnEkremotites.addActionListener(e -> openEkremotitesPanel());

        btnAddEkremotitaForApostoli = new JButton("+ Εκκρεμότητα");
        btnAddEkremotitaForApostoli.setBackground(new Color(76, 175, 80));
        btnAddEkremotitaForApostoli.setForeground(Color.WHITE);
        btnAddEkremotitaForApostoli.setFont(btnAddEkremotitaForApostoli.getFont().deriveFont(Font.BOLD, 12f));
        btnAddEkremotitaForApostoli.setPreferredSize(new Dimension(150, 35));
        btnAddEkremotitaForApostoli.setOpaque(true);
        btnAddEkremotitaForApostoli.setBorderPainted(false);
        btnAddEkremotitaForApostoli.setEnabled(false);
        btnAddEkremotitaForApostoli.addActionListener(e -> addEkremotitaForSelectedApostoli());
    }

    private void addEkremotitaForSelectedApostoli() {
        if (selectedArithmosApostolis == null) {
            return;
        }

        Apostoli selectedApostoli = apostoliDAO.findByArithmosApostolis(selectedArithmosApostolis);
        if (selectedApostoli == null) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα φόρτωσης αποστολής",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = (window instanceof Frame) ? (Frame) window : null;

        AddEkremotitaForApostoliDialog dialog =
                new AddEkremotitaForApostoliDialog(
                        parentFrame,
                        org.pms.shipments.dao.EkremotitaDAO.class,
                        selectedApostoli
                );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            JOptionPane.showMessageDialog(this,
                    "Η εκκρεμότητα δημιουργήθηκε επιτυχώς!",
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE);
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
        setupCustomTableSorting();
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
    }

    private void loadPelatesIntoComboBox() {
        List<Pelatis> pelatesList = pelatisDAO.findAll();
        for (Pelatis pelatis : pelatesList) {
            cmbPelatisFilter.addItem(pelatis.getKodikosPelati() + " - " + pelatis.getEponymiaEtairias());
        }
    }

    public void performFirebaseSync() {
        if (firebaseSyncService == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Firebase δεν είναι διαθέσιμο.\nΠαρακαλώ ελέγξτε τις ρυθμίσεις.",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }


        SwingWorker<FirebaseSyncService.SyncResult, Void> worker = new SwingWorker<>() {
            @Override
            protected FirebaseSyncService.SyncResult doInBackground() {
                return firebaseSyncService.syncPelionShipments();
            }

            @Override
            protected void done() {
                try {
                    FirebaseSyncService.SyncResult result = get();

                    if (result.success) {
                        JOptionPane.showMessageDialog(
                                ApostoliManagementPanel.this,
                                "Συγχρονισμός ολοκληρώθηκε!\n" +
                                        "Επιτυχείς: " + result.synced + "\n" +
                                        "Σφάλματα: " + result.errors,
                                "Επιτυχία",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                ApostoliManagementPanel.this,
                                "Σφάλμα συγχρονισμού:\n" + result.message,
                                "Σφάλμα",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ApostoliManagementPanel.this,
                            "Σφάλμα: " + e.getMessage(),
                            "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel with two rows - Navy Blue Background
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(UIConstants.SEARCH_PANEL_BG);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(
                UIConstants.SEARCH_PANEL_TOP_PADDING,
                UIConstants.SEARCH_PANEL_SIDE_PADDING,
                UIConstants.SEARCH_PANEL_BOTTOM_PADDING,
                UIConstants.SEARCH_PANEL_SIDE_PADDING));

        // Logo και Title panel στο ίδιο ύψος
        JPanel logoAndTitlePanel = new JPanel(new BorderLayout());
        logoAndTitlePanel.setBackground(UIConstants.SEARCH_PANEL_BG);

        // Logo panel - αριστερά
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        logoPanel.setBackground(UIConstants.SEARCH_PANEL_BG);
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/icons/MY_PMS.jpg"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(
                    UIConstants.LOGO_SIZE.width, UIConstants.LOGO_SIZE.height, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledImage);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without it");
        }

        // Title panel - στο κέντρο
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        titlePanel.setBackground(UIConstants.SEARCH_PANEL_BG);
        JLabel titleLabel = new JLabel("Αναζήτηση & Φίλτρα");
        titleLabel.setForeground(UIConstants.SEARCH_LABEL_COLOR);
        titleLabel.setFont(UIConstants.SEARCH_TITLE_FONT);
        titlePanel.add(titleLabel);

        logoAndTitlePanel.add(logoPanel, BorderLayout.WEST);
        logoAndTitlePanel.add(titlePanel, BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        refreshPanel.setBackground(UIConstants.SEARCH_PANEL_BG);
        refreshPanel.add(btnRefresh);
        refreshPanel.add(Box.createVerticalStrut(20));
        refreshPanel.add(btnEkremotites);
        refreshPanel.add(btnAddEkremotitaForApostoli);
        logoAndTitlePanel.add(refreshPanel, BorderLayout.EAST);

        // First row - main filters - ΚΕΝΤΡΑΡΙΣΜΕΝΑ
        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.COMPONENT_GAP, 5));
        firstRow.setBackground(UIConstants.SEARCH_PANEL_BG);

        // Second row - date filters and buttons - ΚΕΝΤΡΑΡΙΣΜΕΝΑ
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.COMPONENT_GAP, 5));
        secondRow.setBackground(UIConstants.SEARCH_PANEL_BG);

        firstRow.add(new JLabel("Αναζήτηση:"));
        firstRow.add(txtSearch);
        firstRow.add(Box.createHorizontalStrut(UIConstants.SECTION_GAP));
        firstRow.add(new JLabel("Courier:"));
        firstRow.add(cmbCourierFilter);
        firstRow.add(Box.createHorizontalStrut(UIConstants.COMPONENT_GAP));
        firstRow.add(new JLabel("Status Details:"));
        firstRow.add(cmbStatusFilter);
        firstRow.add(chkNotStatus);
        firstRow.add(Box.createHorizontalStrut(UIConstants.COMPONENT_GAP));
        firstRow.add(new JLabel("Πελάτης:"));
        firstRow.add(cmbPelatisFilter);

        // Second row - date filters and buttons
        secondRow.add(new JLabel("Από:"));
        secondRow.add(txtFromDate);
        secondRow.add(new JLabel("Έως:"));
        secondRow.add(txtToDate);
        secondRow.add(btnTodayFilter);
        secondRow.add(Box.createHorizontalStrut(UIConstants.SECTION_GAP));
        secondRow.add(btnSearch);
        secondRow.add(btnClearSearch);

        // Set white text for labels in search panel
        for (Component comp : firstRow.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(UIConstants.SEARCH_LABEL_COLOR);
                ((JLabel) comp).setFont(UIConstants.LABEL_FONT);
            }
        }
        for (Component comp : secondRow.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(UIConstants.SEARCH_LABEL_COLOR);
                ((JLabel) comp).setFont(UIConstants.LABEL_FONT);
            }
        }

        // Style search components
        txtSearch.setBackground(UIConstants.INPUT_BG);
        txtSearch.setForeground(UIConstants.INPUT_TEXT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INPUT_BORDER_FOCUS, UIConstants.BORDER_WIDTH_THICK),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        // Style combo boxes
        cmbCourierFilter.setBackground(UIConstants.COMBO_BG);
        cmbCourierFilter.setForeground(UIConstants.COMBO_TEXT);
        cmbStatusFilter.setBackground(UIConstants.COMBO_BG);
        cmbStatusFilter.setForeground(UIConstants.COMBO_TEXT);
        cmbPelatisFilter.setBackground(UIConstants.COMBO_BG);
        cmbPelatisFilter.setForeground(UIConstants.COMBO_TEXT);

        // Style buttons
        btnSearch.setBackground(UIConstants.BUTTON_ACCENT_BG);
        btnSearch.setForeground(UIConstants.BUTTON_ACCENT_TEXT);
        btnSearch.setFont(UIConstants.BUTTON_FONT);

        btnClearSearch.setBackground(UIConstants.BUTTON_CHERRY_BG);
        btnClearSearch.setForeground(UIConstants.BUTTON_CHERRY_TEXT);
        btnClearSearch.setFont(UIConstants.BUTTON_FONT);

        btnTodayFilter.setBackground(UIConstants.BUTTON_PRIMARY_BG);
        btnTodayFilter.setForeground(UIConstants.BUTTON_PRIMARY_TEXT);
        btnTodayFilter.setFont(UIConstants.BUTTON_FONT);

        // Style checkbox
        chkNotStatus.setBackground(UIConstants.CHECKBOX_BG);
        chkNotStatus.setForeground(UIConstants.CHECKBOX_TEXT);
        chkNotStatus.setFont(UIConstants.BUTTON_FONT);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.SEARCH_PANEL_BG);
        contentPanel.add(firstRow, BorderLayout.NORTH);
        contentPanel.add(secondRow, BorderLayout.SOUTH);

        searchPanel.add(logoAndTitlePanel, BorderLayout.NORTH);
        searchPanel.add(contentPanel, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1200, 400));
        add(scrollPane, BorderLayout.CENTER);

        JPanel paginationPanel = createPaginationPanel();
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(paginationPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.COMPONENT_GAP, UIConstants.BUTTON_PANEL_PADDING));
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PANEL_PADDING, UIConstants.PANEL_PADDING, UIConstants.PANEL_PADDING, UIConstants.PANEL_PADDING));
        buttonPanel.setBackground(UIConstants.BUTTON_PANEL_BG);

        // Group action buttons
        JPanel actionButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.BUTTON_GROUP_GAP, 0));
        actionButtonGroup.setBackground(UIConstants.BUTTON_PANEL_BG);
        actionButtonGroup.add(btnDelete);

        // Group view buttons
        JPanel viewButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, UIConstants.BUTTON_GROUP_GAP, 0));
        viewButtonGroup.setBackground(UIConstants.BUTTON_PANEL_BG);
        viewButtonGroup.add(btnViewDetails);

        buttonPanel.add(actionButtonGroup);
        buttonPanel.add(Box.createHorizontalStrut(UIConstants.BUTTON_SECTION_GAP));
        buttonPanel.add(viewButtonGroup);

        // Status area
        JLabel statusLabel = new JLabel(" Εμφανίζονται: 0 αποστολές");
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setPreferredSize(new Dimension(getWidth(), UIConstants.STATUS_BAR_HEIGHT));
        statusLabel.setBackground(UIConstants.STATUS_BAR_BG);
        statusLabel.setForeground(UIConstants.STATUS_BAR_TEXT);
        statusLabel.setFont(UIConstants.STATUS_FONT);
        statusLabel.setOpaque(true);


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

        // Allow Enter key to trigger search
        txtSearch.addActionListener(e -> performSearch());
        cmbCourierFilter.addActionListener(e -> performSearch());
        cmbStatusFilter.addActionListener(e -> performSearch());
        cmbPelatisFilter.addActionListener(e -> performSearch());
        chkNotStatus.addActionListener(e -> performSearch());

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
        btnDelete.addActionListener(e -> deleteSelectedApostoli());
        btnRefresh.addActionListener(e -> loadData());
        btnViewDetails.addActionListener(e -> viewApostoliDetails());
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

                    JMenuItem infoItem = new JMenuItem("Στοιχεία Αποστολής");
                    infoItem.addActionListener(event -> openInfoTab());
                    contextMenu.add(infoItem);

                    // Προσθήκη επιλογής για Tracking Details (μόνο για ACS αποστολές)
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        String courier = (String) apostoliTableModel.getValueAt(modelRow, 1); // Στήλη courier

                        if ("ACS".equals(courier)) {
                            JMenuItem trackingDetailsItem = new JMenuItem("Λεπτομέρειες Αποστολής");
                            trackingDetailsItem.addActionListener(event -> openTrackingDetailsTab());
                            contextMenu.add(trackingDetailsItem);
                        }
                    }

                    contextMenu.show(table, e.getX(), e.getY());
                }
            }
        });
    }

    private void openTrackingDetailsTab() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String arithmosApostolis = (String) apostoliTableModel.getValueAt(modelRow, 2);

            if (arithmosApostolis != null && !arithmosApostolis.equals("-")) {
                SwingUtilities.invokeLater(() -> {
                    // Χρησιμοποιούμε τον σωστό constructor
                    ApostoliDetailsDialog dialog = new ApostoliDetailsDialog(
                            (Frame) SwingUtilities.getWindowAncestor(this),
                            arithmosApostolis
                    );

                    // Επιλέγουμε την καρτέλα Λεπτομέρειες Αποστολής
                    dialog.selectTrackingDetailsTab();
                    dialog.setVisible(true);
                });
            }
        }
    }

    private void setupSelectionEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !isRestoring) {
                int selectedRowCount = table.getSelectedRowCount();
                boolean hasSelection = selectedRowCount > 0;
                boolean singleSelection = selectedRowCount == 1;

                // Κουμπιά που χρειάζονται μία συγκεκριμένη επιλογή
                btnViewDetails.setEnabled(singleSelection);

                // Κουμπιά που δουλεύουν με πολλαπλή επιλογή
                btnDelete.setEnabled(hasSelection);
                btnAddEkremotitaForApostoli.setEnabled(singleSelection);

                // Ενημέρωση του selectedArithmosApostolis
                if (singleSelection) {
                    int selectedRow = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    setSelectedArithmosApostolis((String) apostoliTableModel.getValueAt(modelRow, 2));
                } else {
                    setSelectedArithmosApostolis(null);
                }
            }
        });

        // Αρχικά όλα disabled
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);
        btnAddEkremotitaForApostoli.setEnabled(false);
    }

    public void openAddDialog() {
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

    private void setupCustomTableSorting() {
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());

                if (column == 3 || column == 10) {
                    if (lastSortedColumn == column) {
                        isAscendingSort = !isAscendingSort;
                    } else {
                        isAscendingSort = true;
                        lastSortedColumn = column;
                    }

                    sortAllDataByColumn(column);
                }
            }
        });
    }

    private void sortAllDataByColumn(int column) {
        if (allApostoli == null || allApostoli.isEmpty()) {
            return;
        }

        if (column == 3) {
            allApostoli.sort((a1, a2) -> {
                LocalDate d1 = a1.getImerominiaParalabis();
                LocalDate d2 = a2.getImerominiaParalabis();

                if (d1 == null) return 1;
                if (d2 == null) return -1;

                int comparison = d1.compareTo(d2);
                return isAscendingSort ? comparison : -comparison;
            });
        } else if (column == 10) {
            for (int i = 0; i < Math.min(10, allApostoli.size()); i++) {
                Apostoli a = allApostoli.get(i);
                Integer p = getDiakinisiPriorityFromObject(a);
            }

            allApostoli.sort((a1, a2) -> {
                Integer p1 = getDiakinisiPriorityFromObject(a1);
                Integer p2 = getDiakinisiPriorityFromObject(a2);

                int comparison = p1.compareTo(p2);
                return isAscendingSort ? comparison : -comparison;
            });

            for (int i = 0; i < Math.min(10, allApostoli.size()); i++) {
                Apostoli a = allApostoli.get(i);
                Integer p = getDiakinisiPriorityFromObject(a);
            }
        }

        currentPage = 0;
        updateTableWithCurrentPage();
        updatePaginationButtons();

    }

    private Integer getDiakinisiPriorityFromObject(Apostoli apostoli) {
        if (apostoli.getImerominiaParalabis() == null) {
            return 10000;
        }

        if (!"ACS".equalsIgnoreCase(apostoli.getCourier())) {
            return 9000;
        }

        String statusDetails = apostoli.getStatusDetails();

        if (statusDetails == null || statusDetails.equals("-") || statusDetails.trim().isEmpty()) {
            return 8000;
        }

        if (statusDetails.equals("ΑΠΟΣΤΟΛΗ ΔΕΝ ΒΡΕΘΗΚΕ")) {
            return 7000;
        }

        if (statusDetails.equals("ΕΠΙΣΤΡΑΦΗΚΕ")) {
            return 6000;
        }

        if (statusDetails.equals("ΠΡΟΣ ΕΠΙΣΤΟΦΗ")) {
            return 5000;
        }

        LocalDate departureDate = apostoli.getImerominiaAnaxorisis();

        if (departureDate == null) {
            return 4000;
        }

        if (statusDetails.equals("ΠΑΡΑΔΟΘΗΚΕ")) {
            return 3000;
        }

        LocalDate today = LocalDate.now();
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(departureDate, today);

        if (days >= 0) {
            return days;
        }

        return 2000;
    }


    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(UIConstants.BUTTON_PANEL_BG);

        btnFirstPage = new JButton("<<");
        btnPreviousPage = new JButton("<");
        lblPageInfo = new JLabel("Σελίδα 1 από 1");
        btnNextPage = new JButton(">");
        btnLastPage = new JButton(">>");

        btnFirstPage.setEnabled(false);
        btnPreviousPage.setEnabled(false);
        btnNextPage.setEnabled(false);
        btnLastPage.setEnabled(false);

        btnFirstPage.addActionListener(e -> goToPage(0));
        btnPreviousPage.addActionListener(e -> goToPage(currentPage - 1));
        btnNextPage.addActionListener(e -> goToPage(currentPage + 1));
        btnLastPage.addActionListener(e -> goToPage(getTotalPages() - 1));

        panel.add(btnFirstPage);
        panel.add(btnPreviousPage);
        panel.add(lblPageInfo);
        panel.add(btnNextPage);
        panel.add(btnLastPage);

        return panel;
    }

    private int getTotalPages() {
        if (allApostoli == null || allApostoli.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) allApostoli.size() / ROWS_PER_PAGE);
    }

    private void goToPage(int page) {
        currentPage = page;
        updateTableWithCurrentPage();
        updatePaginationButtons();
    }

    private void updateTableWithCurrentPage() {
        if (allApostoli == null || allApostoli.isEmpty()) {
            apostoliTableModel.clearData();
            return;
        }

        int start = currentPage * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, allApostoli.size());

        List<Apostoli> pageData = allApostoli.subList(start, end);
        apostoliTableModel.loadData(pageData, start + 1);
        adjustRowHeights();
    }

    private void adjustRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = 40;

            for (int col = 0; col < table.getColumnCount(); col++) {
                if (col == 6 || col == 11 || col == 12) {
                    TableCellRenderer renderer = table.getCellRenderer(row, col);
                    Component comp = table.prepareRenderer(renderer, row, col);
                    int height = comp.getPreferredSize().height;
                    maxHeight = Math.max(maxHeight, height);
                }
            }

            table.setRowHeight(row, maxHeight);
        }
    }

    private void updatePaginationButtons() {
        int totalPages = getTotalPages();

        btnFirstPage.setEnabled(currentPage > 0);
        btnPreviousPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage < totalPages - 1);
        btnLastPage.setEnabled(currentPage < totalPages - 1);

        lblPageInfo.setText(String.format("Σελίδα %d από %d (Σύνολο: %d)",
                currentPage + 1, totalPages, allApostoli != null ? allApostoli.size() : 0));
    }

    public void loadData() {
        performSearch();
    }

    public void refreshData() {
        performSearch();
    }

    void performSearch() {
        String tempSelectedArithmosApostolis = selectedArithmosApostolis;

        saveSelectedRow();

        if (table.getRowSorter() != null) {
            lastSortKeys = new ArrayList<>(table.getRowSorter().getSortKeys());
        }
        String searchTerm = txtSearch.getText().trim();
        String courier = (String) cmbCourierFilter.getSelectedItem();
        String status = (String) cmbStatusFilter.getSelectedItem();
        boolean notStatus = chkNotStatus.isSelected();

        String selectedPelatis = (String) cmbPelatisFilter.getSelectedItem();
        String kodikosPelati = null;
        if (selectedPelatis != null && !selectedPelatis.equals("όλοι οι πελάτες")) {
            kodikosPelati = selectedPelatis.split(" - ")[0];
        }

        LocalDate fromDate = txtFromDate.getSelectedDate();
        LocalDate toDate = txtToDate.getSelectedDate();

        List<Apostoli> results;

        if (kodikosPelati != null) {
            results = apostoliDAO.findByPelatis(kodikosPelati);

            if (!searchTerm.isEmpty()) {
                final String search = searchTerm.toLowerCase();
                results = results.stream()
                        .filter(a ->
                                (a.getArithmosApostolis() != null && a.getArithmosApostolis().toLowerCase().contains(search)) ||
                                        (a.getArithmosParaggelias() != null && a.getArithmosParaggelias().toLowerCase().contains(search)) ||
                                        (a.getParaliptis() != null && a.getParaliptis().toLowerCase().contains(search)) ||
                                        (a.getPoli() != null && a.getPoli().toLowerCase().contains(search)) ||
                                        (a.getTilefonoStathero() != null && a.getTilefonoStathero().contains(search)) ||
                                        (a.getTilefonoKinito() != null && a.getTilefonoKinito().contains(search))
                        )
                        .collect(Collectors.toList());
            }

            if (courier != null && !"όλα".equals(courier)) {
                results = results.stream()
                        .filter(a -> courier.equals(a.getCourier()))
                        .collect(Collectors.toList());
            }

            if (fromDate != null) {
                results = results.stream()
                        .filter(a -> a.getImerominiaParalabis() != null && !a.getImerominiaParalabis().isBefore(fromDate))
                        .collect(Collectors.toList());
            }

            if (toDate != null) {
                results = results.stream()
                        .filter(a -> a.getImerominiaParalabis() != null && !a.getImerominiaParalabis().isAfter(toDate))
                        .collect(Collectors.toList());
            }

        } else {
            results = apostoliDAO.search(
                    searchTerm.isEmpty() ? null : searchTerm,
                    courier,
                    null,
                    fromDate,
                    toDate
            );
        }

        if (status != null && !"ΟΛΕΣ ΟΙ ΑΠΟΣΤΟΛΕΣ".equals(status)) {
            List<Apostoli> filteredResults = new ArrayList<>();

            for (Apostoli apostoli : results) {
                String statusDetails = apostoli.getStatusDetails();

                boolean shouldInclude = false;
                switch (status) {
                    case "ΠΑΡΑΔΟΘΗΚΕ":
                        shouldInclude = "ΠΑΡΑΔΟΘΗΚΕ".equals(statusDetails);
                        break;

                    case "ΑΚΥΡΩΘΗΚΕ":
                        shouldInclude = "ΑΚΥΡΩΘΗΚΕ".equals(statusDetails);
                        break;

                    case "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ":
                        shouldInclude = "ΠΡΟΣ ΕΠΙΣΤΟΦΗ".equals(statusDetails);
                        break;

                    case "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ":
                        shouldInclude = "ΕΠΙΣΤΡΑΦΗΚΕ".equals(statusDetails);
                        break;

                    case "ΑΔΙΑΚΙΝΗΤΟ":
                        shouldInclude = "ΑΔΙΑΚΙΝΗΤΟ".equals(statusDetails);
                        break;
                    case "ΔΕΝ ΒΡΕΘΗΚΕ":
                        shouldInclude = "ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ".equals(statusDetails);
                        break;
                    default:
                        shouldInclude = statusDetails != null &&
                                !statusDetails.equals("ΠΑΡΑΔΟΘΗΚΕ") &&
                                !statusDetails.equals("ΑΚΥΡΩΘΗΚΕ") &&
                                !statusDetails.equals("ΕΠΙΣΤΡΑΦΗΚΕ") &&
                                !statusDetails.equals("ΠΡΟΣ ΕΠΙΣΤΟΦΗ") &&
                                !statusDetails.equals("ΑΔΙΑΚΙΝΗΤΟ") &&
                                !statusDetails.equals("ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ");
                        break;
                }

                if (shouldInclude) {
                    filteredResults.add(apostoli);
                }
            }

            results = filteredResults;
        }

        allApostoli = results;

        int totalPages = getTotalPages();

        if (currentPage >= totalPages) {
            currentPage = 0;
        }

        updateTableWithCurrentPage();

        updatePaginationButtons();


        updateStatusLabel();
        if (tempSelectedArithmosApostolis != null && selectedArithmosApostolis == null) {
            setSelectedArithmosApostolis(tempSelectedArithmosApostolis);
        }


        restoreSelectedRow();
        if (lastSortedColumn == 3 || lastSortedColumn == 10) {
            sortAllDataByColumn(lastSortedColumn);
        }
    }

    private void saveSelectedRow() {
        if (isRestoring) {
            return;
        }

        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            setSelectedArithmosApostolis((String) apostoliTableModel.getValueAt(modelRow, 2));
        } else {
            setSelectedArithmosApostolis(null);
        }
    }

    private void openEkremotitesPanel() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Διαχείριση Εκκρεμοτήτων", false);
        dialog.setContentPane(new EkremotitesManagementPanel());
        dialog.setSize(1400, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void restoreSelectedRow() {

        if (selectedArithmosApostolis != null) {

            SwingUtilities.invokeLater(() -> {
                int foundPage = -1;
                int foundIndex = -1;

                if (allApostoli != null) {
                    for (int i = 0; i < allApostoli.size(); i++) {
                        Apostoli apostoli = allApostoli.get(i);
                        String arithmos = apostoli.getArithmosApostolis();

                        if (arithmos != null && arithmos.equals(selectedArithmosApostolis)) {
                            foundIndex = i;
                            foundPage = i / ROWS_PER_PAGE;
                            break;
                        }
                    }
                }

                if (foundIndex != -1 && foundPage != -1) {

                    if (currentPage != foundPage) {
                        currentPage = foundPage;

                        isRestoring = true;
                        updateTableWithCurrentPage();
                        isRestoring = false;

                        updatePaginationButtons();
                    }

                    int rowInPage = foundIndex % ROWS_PER_PAGE;

                    if (rowInPage < table.getRowCount()) {
                        int viewRow = table.convertRowIndexToView(rowInPage);
                        table.setRowSelectionInterval(viewRow, viewRow);
                        table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
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
        lastSortKeys = null;
        txtFromDate.clear();
        txtToDate.clear();
        currentPage = 0;
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
                .append(selectedRows.length).append(" αποστολές?\n\n");

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
                            case "ΣΕ ΔΙΑΚΙΝΗΣΗ": countSeMatafera++; break;
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
                    sb.append(" ┃ Couriers: ");
                    if (countACS > 0) sb.append("ACS:").append(countACS).append(" ");
                    if (countELTA > 0) sb.append("ELTA:").append(countELTA).append(" ");
                    if (countSPEEDEX > 0) sb.append("SPEEDEX:").append(countSPEEDEX).append(" ");
                    if (countGENIKI > 0) sb.append("GENIKI:").append(countGENIKI).append(" ");

                    sb.append("┃ Status: ");
                    if (countParadothike > 0) sb.append("Παραδόθηκε:").append(countParadothike).append(" ");
                    if (countSeMatafera > 0) sb.append("Σε Μεταφορά:").append(countSeMatafera).append(" ");
                    if (countSeEpexergasia > 0) sb.append("Σε Επεξεργασία:").append(countSeEpexergasia).append(" ");

                    if (totalAntikatavoli > 0) {
                        sb.append("┃ Συνολική Αντικαταβολή: ").append(Math.round(totalAntikatavoli * 100.0) / 100.0).append("€");
                    }
                }

                statusLabel.setText(sb.toString());
            }
        }
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

    public void exportToExcel() {
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
                    LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
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

                for (int i = 0; i < allApostoli.size(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                    Apostoli apostoli = allApostoli.get(i);

                    String diefthinsiParalipti = apostoli.getDiefthinsi() != null ? apostoli.getDiefthinsi() : "";
                    String tkParalipti = apostoli.getTkParalipti() != null ? apostoli.getTkParalipti() : "";
                    String sxolia = apostoli.getSxolia() != null ? apostoli.getSxolia() : "";

                    Pelatis pelatis = pelatisDAO.findById(apostoli.getKodikosPelati());
                    String senderName = pelatis != null ? pelatis.getEponymiaEtairias() : apostoli.getKodikosPelati();

                    row.createCell(0).setCellValue(i + 1);
                    row.createCell(1).setCellValue(apostoli.getCourier() != null ? apostoli.getCourier() : "-");
                    row.createCell(2).setCellValue(apostoli.getArithmosApostolis() != null ? apostoli.getArithmosApostolis() : "-");

                    if (apostoli.getImerominiaParalabis() != null) {
                        row.createCell(3).setCellValue(apostoli.getImerominiaParalabis().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        row.createCell(3).setCellValue("-");
                    }

                    if (apostoli.getAntikatavoli() != null) {
                        row.createCell(4).setCellValue(String.format("%.2f€", apostoli.getAntikatavoli()));
                    } else {
                        row.createCell(4).setCellValue("0,00€");
                    }

                    row.createCell(5).setCellValue(senderName != null ? senderName : "-");
                    row.createCell(6).setCellValue(apostoli.getParaliptis() != null ? apostoli.getParaliptis() : "-");
                    row.createCell(7).setCellValue(apostoli.getPoli() != null ? apostoli.getPoli() : "-");
                    row.createCell(8).setCellValue(diefthinsiParalipti.isEmpty() ? "-" : diefthinsiParalipti);
                    row.createCell(9).setCellValue(tkParalipti.isEmpty() ? "-" : tkParalipti);
                    row.createCell(10).setCellValue(apostoli.getTilefonoStathero() != null ? apostoli.getTilefonoStathero() : "-");
                    row.createCell(11).setCellValue(apostoli.getTilefonoKinito() != null ? apostoli.getTilefonoKinito() : "-");

                    if (apostoli.getImerominiaParalabis() != null) {
                        long days = java.time.temporal.ChronoUnit.DAYS.between(apostoli.getImerominiaParalabis(), LocalDate.now());
                        row.createCell(12).setCellValue(String.valueOf(days));
                    } else {
                        row.createCell(12).setCellValue("-");
                    }

                    row.createCell(13).setCellValue(apostoli.getStatusApostolis() != null ? apostoli.getStatusApostolis() : "-");
                    row.createCell(14).setCellValue(apostoli.getStatusMypms() != null ? apostoli.getStatusMypms() : "-");
                    row.createCell(15).setCellValue(sxolia.isEmpty() ? "-" : sxolia);
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
                                "Εγγραφές: " + allApostoli.size(),
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

    public void setFirebaseSyncService(FirebaseSyncService firebaseSyncService) {
        this.firebaseSyncService = firebaseSyncService;
    }

    public void setSelectedArithmosApostolis(String value) {
        this.selectedArithmosApostolis = value;
    }
}
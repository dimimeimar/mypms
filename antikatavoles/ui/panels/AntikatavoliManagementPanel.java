package org.pms.antikatavoles.ui.panels;

import org.pms.antikatavoles.dao.AntikatavoliDAO;
import org.pms.antikatavoles.service.AntikatavoliService;
import org.pms.antikatavoles.ui.dialogs.AntikatavoliTableModel;
import org.pms.antikatavoles.ui.dialogs.ExportAntikatavolesDialog;
import org.pms.antikatavoles.ui.dialogs.NeoApodosiDialog;
import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.antikatavoles.dao.AntikatavoliFullViewDAO;
import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.shipments.ui.components.CustomDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntikatavoliManagementPanel extends JPanel {
    private final AntikatavoliDAO antikatavoliDAO;
    private final AntikatavoliService antikatavoliService;
    private final PelatisDAO pelatisDAO;
    private final AntikatavoliFullViewDAO fullViewDAO;

    private JTable table;
    private AntikatavoliTableModel tableModel;


    private JTextField txtSearch;
    private JComboBox<String> cmbPelatisFilter;
    private JComboBox<String> cmbApodothikeFilter;
    private JButton btnSearch;
    private JButton btnClearSearch;
    private JButton btnRefresh;
    private JButton btnApodosi;
    private JButton btnExport;
    private JButton btnProApodosi;
    private CustomDatePicker datePickerFrom;
    private CustomDatePicker datePickerTo;

    private List<AntikatavoliFullView> allAntikatavoles;

    public AntikatavoliManagementPanel() {
        this.antikatavoliDAO = new AntikatavoliDAO();
        this.fullViewDAO = new AntikatavoliFullViewDAO();
        this.antikatavoliService = new AntikatavoliService();
        this.pelatisDAO = new PelatisDAO();

        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }

    private void initComponents() {
        txtSearch = new JTextField(20);

        cmbPelatisFilter = new JComboBox<>();
        cmbPelatisFilter.addItem("ΟΛΟΙ");
        List<Pelatis> pelatesList = pelatisDAO.findAll();
        for (Pelatis pelatis : pelatesList) {
            cmbPelatisFilter.addItem(pelatis.getKodikosPelati() + " - " + pelatis.getEponymiaEtairias());
        }

        cmbApodothikeFilter = new JComboBox<>(new String[]{"ΟΛΕΣ", "ΑΠΟΔΟΜΕΝΕΣ", "ΜΗ ΑΠΟΔΟΜΕΝΕΣ"});

        btnSearch = new JButton("Αναζήτηση");
        btnClearSearch = new JButton("Καθαρισμός");
        btnRefresh = new JButton("Ανανέωση");
        btnApodosi = new JButton("Νέα Απόδοση");
        datePickerFrom = new CustomDatePicker(null);
        datePickerTo = new CustomDatePicker(null);

        btnExport = new JButton("Εξαγωγή");
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFont(btnExport.getFont().deriveFont(Font.BOLD));

        btnProApodosi = new JButton("Αντικαταβολές Προς Απόδοση");
        btnProApodosi.setBackground(new Color(255, 140, 0));
        btnProApodosi.setForeground(Color.BLACK);
        btnProApodosi.setFont(btnProApodosi.getFont().deriveFont(Font.BOLD));

        tableModel = new AntikatavoliTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowHeight(35);
        setupTableRenderers();
    }

    private void setupTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // Α/Α
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Ημ/νία Παραλαβής
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Ημ/νία Παράδοσης
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // Courier
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Αρ. Αποστολής
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Αρ. Παραγγελίας
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Αντικαταβολή
        table.getColumnModel().getColumn(7).setPreferredWidth(150);  // Παραλήπτης
        table.getColumnModel().getColumn(8).setPreferredWidth(120);  // Παραστατικό ACS
        table.getColumnModel().getColumn(9).setPreferredWidth(120);  // Παραστατικό MyPMS
        table.getColumnModel().getColumn(10).setPreferredWidth(120); // Ημ/νία Απόδοσης
        table.getColumnModel().getColumn(11).setPreferredWidth(200); // Σχόλια
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setPreferredSize(new Dimension(getWidth(), 120));
        panel.setBorder(BorderFactory.createTitledBorder("Αναζήτηση & Φίλτρα"));

        panel.add(new JLabel("Αναζήτηση:"));
        panel.add(txtSearch);

        panel.add(new JLabel("Πελάτης:"));
        panel.add(cmbPelatisFilter);

        panel.add(new JLabel("Κατάσταση:"));
        panel.add(cmbApodothikeFilter);

        panel.add(new JLabel("Από:"));
        panel.add(datePickerFrom);

        panel.add(new JLabel("Έως:"));
        panel.add(datePickerTo);

        panel.add(btnSearch);
        panel.add(btnClearSearch);
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnApodosi.setBackground(new Color(0, 150, 0));
        btnApodosi.setForeground(Color.BLACK);
        btnApodosi.setFont(btnApodosi.getFont().deriveFont(Font.BOLD));

        panel.add(btnApodosi);
        panel.add(btnExport);
        panel.add(btnProApodosi);


        JLabel statsLabel = new JLabel("Σύνολο: 0 αντικαταβολές | 0,00€");
        statsLabel.setFont(statsLabel.getFont().deriveFont(Font.BOLD));
        panel.add(statsLabel);

        return panel;
    }

    private void setupEvents() {
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());
        btnRefresh.addActionListener(e -> loadData());
        btnApodosi.addActionListener(e -> createApodosi());
        btnExport.addActionListener(e -> openExportDialog());
        btnProApodosi.addActionListener(e -> openProApodosiDialog());
    }

    private void openExportDialog() {
        if (allAntikatavoles == null || allAntikatavoles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Δεν υπάρχουν δεδομένα προς εξαγωγή.",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExportAntikatavolesDialog dialog = new ExportAntikatavolesDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                allAntikatavoles
        );
        dialog.setVisible(true);
    }

    private void loadData() {
        allAntikatavoles = fullViewDAO.findAll();
        tableModel.loadDataFromFullView(allAntikatavoles, 1);
        updateStats();
    }

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();

        String pelatisSelection = (String) cmbPelatisFilter.getSelectedItem();
        String kodikosPelati = null;
        if (pelatisSelection != null && !pelatisSelection.equals("ΟΛΟΙ")) {
            kodikosPelati = pelatisSelection.split(" - ")[0];
        }

        Boolean apodothike = null;
        String apodothikeFilter = (String) cmbApodothikeFilter.getSelectedItem();
        if ("ΑΠΟΔΟΜΕΝΕΣ".equals(apodothikeFilter)) {
            apodothike = true;
        } else if ("ΜΗ ΑΠΟΔΟΜΕΝΕΣ".equals(apodothikeFilter)) {
            apodothike = false;
        }

        LocalDate fromDate = datePickerFrom.getSelectedDate();
        LocalDate toDate = datePickerTo.getSelectedDate();

        allAntikatavoles = fullViewDAO.search(searchTerm, kodikosPelati, apodothike, fromDate, toDate);
        tableModel.loadDataFromFullView(allAntikatavoles, 1);
        updateStats();
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbPelatisFilter.setSelectedIndex(0);
        cmbApodothikeFilter.setSelectedIndex(0);
        datePickerFrom.clear();
        datePickerTo.clear();
        loadData();
    }

    private void createApodosi() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε αντικαταβολές για απόδοση.",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<AntikatavoliFullView> selectedAntikatavoles = new ArrayList<>();
        String firstKodikosPelati = null;

        for (int i = 0; i < selectedRows.length; i++) {
            int modelRow = table.convertRowIndexToModel(selectedRows[i]);
            if (modelRow < allAntikatavoles.size()) {
                AntikatavoliFullView view = allAntikatavoles.get(modelRow);

                if (view.getApodothike()) {
                    JOptionPane.showMessageDialog(this,
                            "Δεν μπορείτε να επιλέξετε αντικαταβολή που έχει ήδη αποδοθεί.",
                            "Προειδοποίηση",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (view.getIdApodosis() != null) {
                    JOptionPane.showMessageDialog(this,
                            "Δεν μπορείτε να επιλέξετε αντικαταβολή που είναι ήδη προς απόδοση.",
                            "Προειδοποίηση",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (firstKodikosPelati == null) {
                    firstKodikosPelati = view.getKodikosPelati();
                } else if (!firstKodikosPelati.equals(view.getKodikosPelati())) {
                    JOptionPane.showMessageDialog(this,
                            "Όλες οι επιλεγμένες αντικαταβολές πρέπει να ανήκουν στον ίδιο πελάτη.",
                            "Προειδοποίηση",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                selectedAntikatavoles.add(view);
            }
        }

        NeoApodosiDialog dialog = new NeoApodosiDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selectedAntikatavoles
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void updateStats() {
        BigDecimal synolo = BigDecimal.ZERO;
        for (AntikatavoliFullView view : allAntikatavoles) {
            if (view.getAntikatavoli() != null) {
                synolo = synolo.add(view.getAntikatavoli());
            }
        }

        Component[] components = ((JPanel) getComponent(2)).getComponents();
        for (Component c : components) {
            if (c instanceof JLabel && ((JLabel) c).getText().startsWith("Σύνολο:")) {
                ((JLabel) c).setText(String.format("Σύνολο: %d αντικαταβολές | %.2f€",
                        allAntikatavoles.size(), synolo));
                break;
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    private void openProApodosiDialog() {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Αντικαταβολές Προς Απόδοση",
                true
        );

        ProApodosiPanel proApodosiPanel = new ProApodosiPanel();

        dialog.setContentPane(proApodosiPanel);
        dialog.setSize(1400, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadData();
            }
        });

        dialog.setVisible(true);
    }
}
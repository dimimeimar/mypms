package org.pms.shipments.ui.panels;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.EkremotitaDAO;
import org.pms.shipments.model.Ekremotita;
import org.pms.shipments.ui.components.EkremotitesTableModel;
import org.pms.shipments.ui.dialogs.AddEditEkremotitaDialog;
import org.pms.shipments.ui.dialogs.ViewEkremotitaDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class EkremotitesManagementPanel extends JPanel {

    private final EkremotitaDAO ekremotitaDAO;
    private final ApostoliDAO apostoliDAO;
    private final EkremotitesTableModel tableModel;
    private final JTable table;

    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JComboBox<String> cmbPriorityFilter;
    private JComboBox<String> cmbLinkFilter;
    private JButton btnSearch;
    private JButton btnClearSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnMarkResolved;
    private JButton btnRefresh;

    private JLabel lblStats;

    public EkremotitesManagementPanel() {
        this.ekremotitaDAO = new EkremotitaDAO();
        this.apostoliDAO = new ApostoliDAO();
        this.tableModel = new EkremotitesTableModel();
        this.table = new JTable(tableModel);

        initComponents();
        setupLayout();
        setupTableProperties();
        loadData();
        updateStats();
    }

    private void initComponents() {
        txtSearch = new JTextField(20);
        cmbStatusFilter = new JComboBox<>(new String[]{"ΟΛΕΣ", "ΕΝΕΡΓΗ", "ΕΠΙΛΥΘΗΚΕ", "ΔΙΕΓΡΑΜΜΕΝΗ"});
        cmbPriorityFilter = new JComboBox<>(new String[]{"ΟΛΕΣ", "ΥΨΗΛΗ", "ΜΕΣΑΙΑ", "ΧΑΜΗΛΗ"});
        cmbLinkFilter = new JComboBox<>(new String[]{"ΟΛΕΣ", "Με Αποστολή", "Γενικές"});

        btnSearch = new JButton("Αναζήτηση");
        btnClearSearch = new JButton("Καθαρισμός");
        btnAdd = new JButton("Νέα Εκκρεμότητα");
        btnEdit = new JButton("Προβολή");
        btnDelete = new JButton("Διαγραφή");
        btnMarkResolved = new JButton("Επίλυση");
        btnRefresh = new JButton("Ανανέωση");

        lblStats = new JLabel();
        lblStats.setFont(lblStats.getFont().deriveFont(Font.BOLD));

        btnSearch.addActionListener(e -> searchEkremotites());
        btnClearSearch.addActionListener(e -> clearSearch());
        btnAdd.addActionListener(e -> addEkremotita());
        btnEdit.addActionListener(e -> editEkremotita());
        btnDelete.addActionListener(e -> deleteEkremotita());
        btnMarkResolved.addActionListener(e -> markAsResolved());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1200, 500));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Διαχείριση Εκκρεμοτήτων");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Φίλτρα & Αναζήτηση"));

        filterPanel.add(new JLabel("Αναζήτηση:"));
        filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cmbStatusFilter);
        filterPanel.add(new JLabel("Προτεραιότητα:"));
        filterPanel.add(cmbPriorityFilter);
        filterPanel.add(new JLabel("Τύπος:"));
        filterPanel.add(cmbLinkFilter);
        filterPanel.add(btnSearch);
        filterPanel.add(btnClearSearch);
        filterPanel.add(btnRefresh);

        panel.add(filterPanel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.add(lblStats);
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAdd.setPreferredSize(new Dimension(180, 40));
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(btnAdd.getFont().deriveFont(Font.BOLD));
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);

        btnEdit.setPreferredSize(new Dimension(140, 40));
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.BLACK);
        btnEdit.setOpaque(true);
        btnEdit.setBorderPainted(false);

        btnMarkResolved.setPreferredSize(new Dimension(140, 40));
        btnMarkResolved.setBackground(new Color(255, 152, 0));
        btnMarkResolved.setForeground(Color.BLACK);
        btnMarkResolved.setOpaque(true);
        btnMarkResolved.setBorderPainted(false);

        btnDelete.setPreferredSize(new Dimension(140, 40));
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(false);

        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnMarkResolved);
        panel.add(btnDelete);

        return panel;
    }

    private void setupTableProperties() {
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected && value != null) {
                    String priority = value.toString();
                    switch (priority) {
                        case "ΥΨΗΛΗ" -> c.setBackground(new Color(255, 224, 178));
                        case "ΜΕΣΑΙΑ" -> c.setBackground(new Color(255, 249, 196));
                        case "ΧΑΜΗΛΗ" -> c.setBackground(new Color(200, 230, 201));
                        default -> c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editEkremotita();
                }
            }
        });
    }

    private void loadData() {
        List<Ekremotita> ekremotites = ekremotitaDAO.findAll();

        // Φιλτράρισμα διεγραμμένων (εκτός αν το status filter είναι ΔΙΕΓΡΑΜΜΕΝΗ)
        String statusFilter = cmbStatusFilter.getSelectedItem().toString();
        if (!"ΔΙΕΓΡΑΜΜΕΝΗ".equals(statusFilter)) {
            ekremotites = ekremotites.stream()
                    .filter(e -> !"ΔΙΕΓΡΑΜΜΕΝΗ".equals(e.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
        }

        tableModel.loadData(ekremotites);
        updateStats();
    }

    private void searchEkremotites() {
        String searchTerm = txtSearch.getText().trim();
        String statusFilter = cmbStatusFilter.getSelectedItem().toString();
        String priorityFilter = cmbPriorityFilter.getSelectedItem().toString();
        String linkFilter = cmbLinkFilter.getSelectedItem().toString();

        if (statusFilter.equals("ΟΛΕΣ")) statusFilter = null;
        if (priorityFilter.equals("ΟΛΕΣ")) priorityFilter = null;

        Boolean linkedOnly = null;
        if (linkFilter.equals("Με Αποστολή")) {
            linkedOnly = true;
        } else if (linkFilter.equals("Γενικές")) {
            linkedOnly = false;
        }

        List<Ekremotita> results = ekremotitaDAO.search(
                searchTerm.isEmpty() ? null : searchTerm,
                statusFilter,
                priorityFilter,
                linkedOnly
        );

        // Φιλτράρισμα διεγραμμένων μόνο αν ΔΕΝ ζητάει συγκεκριμένα διεγραμμένες
        if (statusFilter == null) {
            results = results.stream()
                    .filter(e -> !"ΔΙΕΓΡΑΜΜΕΝΗ".equals(e.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
        }

        tableModel.loadData(results);
        updateStats();
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbStatusFilter.setSelectedIndex(0);
        cmbPriorityFilter.setSelectedIndex(0);
        cmbLinkFilter.setSelectedIndex(0);
        loadData();
    }

    private void addEkremotita() {
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = (window instanceof Frame) ? (Frame) window : null;

        AddEditEkremotitaDialog dialog = new AddEditEkremotitaDialog(
                parentFrame,
                ekremotitaDAO,
                apostoliDAO,
                null
        );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void editEkremotita() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε εκκρεμότητα για προβολή",
                    "Επιλογή Εκκρεμότητας",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String titlos = (String) tableModel.getValueAt(modelRow, 1);

        List<Ekremotita> all = ekremotitaDAO.findAll();
        Ekremotita selected = all.stream()
                .filter(e -> e.getTitlos().equals(titlos))
                .findFirst()
                .orElse(null);

        if (selected != null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            Frame parentFrame = (window instanceof Frame) ? (Frame) window : null;

            ViewEkremotitaDialog dialog = new ViewEkremotitaDialog(
                    parentFrame,
                    ekremotitaDAO,
                    apostoliDAO,
                    selected
            );
            dialog.setVisible(true);

            if (dialog.isDataChanged()) {
                loadData();
            }
        }
    }

    private void deleteEkremotita() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε εκκρεμότητα για διαγραφή",
                    "Επιλογή Εκκρεμότητας",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Είστε σίγουροι ότι θέλετε να διαγράψετε αυτή την εκκρεμότητα;\n" +
                        "Η εκκρεμότητα θα παραμείνει στη βάση με status 'ΔΙΕΓΡΑΜΜΕΝΗ'.",
                "Επιβεβαίωση Διαγραφής",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String titlos = (String) tableModel.getValueAt(modelRow, 1);

        List<Ekremotita> all = ekremotitaDAO.findAll();
        Ekremotita selected = all.stream()
                .filter(e -> e.getTitlos().equals(titlos))
                .findFirst()
                .orElse(null);

        if (selected != null) {
            selected.setStatus("ΔΙΕΓΡΑΜΜΕΝΗ");
            boolean success = ekremotitaDAO.update(selected);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Η εκκρεμότητα διαγράφηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Σφάλμα κατά τη διαγραφή!",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markAsResolved() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε εκκρεμότητα",
                    "Επιλογή Εκκρεμότητας",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String titlos = (String) tableModel.getValueAt(modelRow, 1);

        List<Ekremotita> all = ekremotitaDAO.findAll();
        Ekremotita selected = all.stream()
                .filter(e -> e.getTitlos().equals(titlos))
                .findFirst()
                .orElse(null);

        if (selected != null) {
            boolean success = ekremotitaDAO.markAsResolved(selected.getIdEkremotita());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Η εκκρεμότητα επιλύθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Σφάλμα κατά την επίλυση!",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStats() {
        int active = ekremotitaDAO.countByStatus("ΕΝΕΡΓΗ");
        int inProgress = ekremotitaDAO.countByStatus("ΣΕ ΕΞΕΛΙΞΗ");
        int resolved = ekremotitaDAO.countByStatus("ΕΠΙΛΥΘΗΚΕ");

        lblStats.setText(String.format(
                "📊 Στατιστικά: Ενεργές: %d | Σε Εξέλιξη: %d | Επιλυμένες: %d | Σύνολο: %d",
                active, inProgress, resolved, tableModel.getRowCount()
        ));
    }
}
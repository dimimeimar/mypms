package org.pms.customers.ui.panels;

import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.customers.ui.dialogs.PelatisDetailsDialog;
import org.pms.customers.ui.dialogs.PelatisForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Ενημερωμένο πάνελ διαχείρισης πελατών με απλοποιημένο πίνακα
 */
public class PelatisManagementPanel extends JPanel {

    private final PelatisDAO pelatisDAO;

    // Table components
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Search components
    private JTextField txtSearch;
    private JComboBox<String> cmbKategoriaFilter;
    private JButton btnSearch;
    private JButton btnClearSearch;

    // Action buttons
    private JButton btnNew;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnViewDetails;

    // Simplified table columns with numbering
    private final String[] columnNames = {
            "#", "Κωδικός", "Κατηγορία", "Επωνυμία Εταιρίας", "Τηλέφωνο", "Κινητό", "Email"
    };

    public PelatisManagementPanel() {
        this.pelatisDAO = new PelatisDAO();
        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }

    private void initComponents() {
        // Initialize search components
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Αναζήτηση στην επωνυμία, κωδικό ή email");

        // Bigger ComboBox for category with proper size
        cmbKategoriaFilter = new JComboBox<>(new String[]{"όλες", "A", "B", "C", "D", "E"});
        cmbKategoriaFilter.setPreferredSize(new Dimension(100, 25)); // Fixed size to show content properly

        btnSearch = new JButton("Αναζήτηση");
        btnClearSearch = new JButton("Καθαρισμός");

        // Initialize action buttons
        btnNew = new JButton("Νέος");
        btnEdit = new JButton("Επεξεργασία");
        btnDelete = new JButton("Διαγραφή");
        btnRefresh = new JButton("Ανανέωση");
        btnViewDetails = new JButton("Λεπτομέρειες");

        // Set delete button color to make it more visible
        btnDelete.setForeground(Color.RED);
        btnDelete.setFont(btnDelete.getFont().deriveFont(Font.BOLD));

        // Set button icons (handle missing icons gracefully)
        try {
            btnNew.setIcon(new ImageIcon(getClass().getResource("/icons/new.png")));
            btnEdit.setIcon(new ImageIcon(getClass().getResource("/icons/edit.png")));
            btnDelete.setIcon(new ImageIcon(getClass().getResource("/icons/delete.png")));
            btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
            btnViewDetails.setIcon(new ImageIcon(getClass().getResource("/icons/view.png")));
        } catch (Exception e) {
            // Icons not found, continue without them
            System.out.println("Icons not found, continuing without them");
        }

        // Initialize table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class; // First column for numbering
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Setup table sorter
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Κωδικός
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Κατηγορία
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Επωνυμία
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Τηλέφωνο
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Κινητό
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Email

    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Search panel with better spacing
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Αναζήτηση & Φίλτρα"));

        searchPanel.add(new JLabel("Αναζήτηση:"));
        searchPanel.add(txtSearch);
        searchPanel.add(Box.createHorizontalStrut(15)); // Add space
        searchPanel.add(new JLabel("Κατηγορία:"));
        searchPanel.add(cmbKategoriaFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);

        add(searchPanel, BorderLayout.NORTH);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel with better organization
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Group action buttons
        JPanel actionButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        actionButtonGroup.add(btnNew);
        actionButtonGroup.add(btnEdit);
        actionButtonGroup.add(btnDelete);

        // Group view buttons
        JPanel viewButtonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        viewButtonGroup.add(btnViewDetails);
        viewButtonGroup.add(btnRefresh);

        buttonPanel.add(actionButtonGroup);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(viewButtonGroup);


        // Status area (moved to bottom)
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        // Search events
        btnSearch.addActionListener(e -> performSearch());
        btnClearSearch.addActionListener(e -> clearSearch());

        // Allow Enter key to trigger search
        txtSearch.addActionListener(e -> performSearch());
        cmbKategoriaFilter.addActionListener(e -> performSearch());

        // Button events
        btnNew.addActionListener(e -> showNewPelatisForm());
        btnEdit.addActionListener(e -> editSelectedPelatis());
        btnDelete.addActionListener(e -> deleteSelectedPelatis());
        btnRefresh.addActionListener(e -> loadData());
        btnViewDetails.addActionListener(e -> viewPelatisDetails());

        // Table double-click to view details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPelatisDetails();
                }
            }
        });

        // Enable/disable buttons based on selection
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = table.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
            btnViewDetails.setEnabled(hasSelection);
        });

        // Initially disable selection-dependent buttons
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);
    }

    private void loadData() {
        loadData(pelatisDAO.findAll());
    }

    // Public method for external refresh calls
    public void refreshData() {
        loadData();
    }

    private void loadData(List<Pelatis> pelatesList) {
        tableModel.setRowCount(0); // Clear existing data

        int rowNumber = 1;
        for (Pelatis pelatis : pelatesList) {
            Object[] rowData = {
                    rowNumber++,  // Sequential numbering
                    pelatis.getKodikosPelati(),
                    pelatis.getKategoria(),
                    pelatis.getEponymiaEtairias(),
                    pelatis.getTilefonoStathero() != null ? pelatis.getTilefonoStathero() : "-",
                    pelatis.getTilefonoKinito() != null ? pelatis.getTilefonoKinito() : "-",
                    pelatis.getEmail() != null ? pelatis.getEmail() : "-"
            };
            tableModel.addRow(rowData);
        }

        // Update status
        updateStatusLabel();
    }

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        String kategoria = (String) cmbKategoriaFilter.getSelectedItem();

        List<Pelatis> results = pelatisDAO.search(searchTerm.isEmpty() ? null : searchTerm,
                kategoria);
        loadData(results);
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbKategoriaFilter.setSelectedIndex(0);
        loadData();
    }

    private void showNewPelatisForm() {
        PelatisForm form = new PelatisForm();
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData(); // Refresh data when form closes
            }
        });
    }

    private void editSelectedPelatis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε πελάτη για επεξεργασία.");
            return;
        }

        try {
            // Get the actual row in case of sorting/filtering
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String kodikosPelati = (String) tableModel.getValueAt(modelRow, 1); // Column 1 is kodikos

            Pelatis pelatis = pelatisDAO.findById(kodikosPelati);
            if (pelatis != null) {
                PelatisForm form = new PelatisForm(pelatis);
                form.setVisible(true);
                form.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        loadData(); // Refresh data when form closes
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "Σφάλμα ανάκτησης στοιχείων πελάτη με κωδικό: " + kodikosPelati);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την επεξεργασία: " + ex.getMessage(),
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPelatis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε πελάτη για διαγραφή.",
                    "Επιλογή Πελάτη", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String kodikosPelati = (String) tableModel.getValueAt(modelRow, 1); // Column 1 is kodikos
            String eponymia = (String) tableModel.getValueAt(modelRow, 3);      // Column 3 is eponymia

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Είστε σίγουροι ότι θέλετε να διαγράψετε τον πελάτη:\n\n" +
                            "Κωδικός: " + kodikosPelati + "\n" +
                            "Επωνυμία: " + eponymia + "\n\n" +
                            "⚠️ ΠΡΟΣΟΧΗ: Αυτή η ενέργεια δεν μπορεί να αναιρεθεί!",
                    "Επιβεβαίωση Διαγραφής",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = pelatisDAO.delete(kodikosPelati);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Ο πελάτης διαγράφηκε επιτυχώς!\n\nΚωδικός: " + kodikosPelati,
                            "Επιτυχής Διαγραφή",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Refresh data
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Σφάλμα κατά τη διαγραφή του πελάτη!\n\nΚωδικός: " + kodikosPelati,
                            "Σφάλμα Διαγραφής",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά τη διαγραφή: " + ex.getMessage(),
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPelatisDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε πελάτη για προβολή λεπτομερειών.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String kodikosPelati = (String) tableModel.getValueAt(modelRow, 1); // Column 1 is kodikos

        // Open detailed view window
        PelatisDetailsDialog detailsDialog = new PelatisDetailsDialog(null, kodikosPelati);
        detailsDialog.setVisible(true);

        // Refresh data when details dialog is closed in case of edits
        detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadData(); // Refresh data
            }
        });
    }

    private void updateStatusLabel() {
        JLabel statusLabel = (JLabel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.PAGE_END);
        if (statusLabel != null) {
            int totalRows = tableModel.getRowCount();
            statusLabel.setText(" Σύνολο πελατών: " + totalRows);
        }
    }
}
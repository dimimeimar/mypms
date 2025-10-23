package org.pms.antikatavoles.ui.panels;

import org.pms.antikatavoles.model.ApodosiGroupView;
import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.antikatavoles.service.AntikatavoliService;
import org.pms.antikatavoles.service.AntikatavoliExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProApodosiPanel extends JPanel {
    private final AntikatavoliService antikatavoliService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnProepiskopiisi;
    private JButton btnEktyposi;
    private JButton btnExport;
    private JButton btnOristikopoiisi;
    private JButton btnDiagrafh;
    private JLabel lblStats;

    private List<ApodosiGroupView> allApodoseis;

    public ProApodosiPanel() {
        this.antikatavoliService = new AntikatavoliService();

        initComponents();
        setupLayout();
        setupEvents();
        loadData();
    }

    private void initComponents() {
        btnRefresh = new JButton("Ανανέωση");

        btnProepiskopiisi = new JButton("Προεπισκόπηση");
        btnProepiskopiisi.setBackground(new Color(0, 120, 215));
        btnProepiskopiisi.setForeground(Color.BLACK);

        btnEktyposi = new JButton("Εκτύπωση");
        btnEktyposi.setBackground(new Color(100, 100, 100));
        btnEktyposi.setForeground(Color.BLACK);

        btnExport = new JButton("Εξαγωγή");
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.BLACK);

        btnOristikopoiisi = new JButton("Οριστικοποίηση");
        btnOristikopoiisi.setBackground(new Color(255, 140, 0));
        btnOristikopoiisi.setForeground(Color.BLACK);
        btnOristikopoiisi.setFont(btnOristikopoiisi.getFont().deriveFont(Font.BOLD));

        btnDiagrafh = new JButton("Διαγραφή");
        btnDiagrafh.setBackground(new Color(220, 53, 69));
        btnDiagrafh.setForeground(Color.BLACK);

        lblStats = new JLabel("Σύνολο: 0 αποδόσεις");
        lblStats.setFont(lblStats.getFont().deriveFont(Font.BOLD));

        String[] columnNames = {"Α/Α", "ID Απόδοσης", "Πελάτης", "Επωνυμία",
                "Πλήθος Αντικαταβολών", "Σύνολο", "Ημ/νία Δημιουργίας"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Προς Απόδοση Αντικαταβολές"));
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(btnProepiskopiisi);
        bottomPanel.add(btnEktyposi);
        bottomPanel.add(btnExport);
        bottomPanel.add(btnOristikopoiisi);
        bottomPanel.add(btnDiagrafh);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(lblStats);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnRefresh.addActionListener(e -> loadData());
        btnProepiskopiisi.addActionListener(e -> proepiskopiisiApodosi());
        btnEktyposi.addActionListener(e -> ektyposiApodosi());
        btnExport.addActionListener(e -> exportApodosi());
        btnOristikopoiisi.addActionListener(e -> oristikopoiisiApodosi());
        btnDiagrafh.addActionListener(e -> diagrafiApodosi());
    }

    private void loadData() {
        allApodoseis = antikatavoliService.getGroupedProApodosi();
        tableModel.setRowCount(0);

        int rowNum = 1;
        for (ApodosiGroupView group : allApodoseis) {
            String createdAt = group.getCreatedAt() != null ?
                    group.getCreatedAt().toLocalDate().toString() : "-";

            Object[] row = {
                    rowNum++,
                    "#" + group.getIdApodosis(),
                    group.getKodikosPelati(),
                    group.getEponimiaEtairias(),
                    group.getPlithosAntikatabolon(),
                    String.format("%.2f€", group.getSynoloPoso()),
                    createdAt
            };
            tableModel.addRow(row);
        }

        updateStats();
    }

    private ApodosiGroupView getSelectedApodosi() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε μια απόδοση.",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        return allApodoseis.get(modelRow);
    }

    private void proepiskopiisiApodosi() {
        ApodosiGroupView selected = getSelectedApodosi();
        if (selected == null) return;

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog previewDialog = new JDialog(owner,
                "Προεπισκόπηση Απόδοσης #" + selected.getIdApodosis(),
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Στοιχεία Απόδοσης"));

        JLabel lblIdApodosis = new JLabel("ID Απόδοσης: #" + selected.getIdApodosis());
        lblIdApodosis.setFont(lblIdApodosis.getFont().deriveFont(Font.BOLD, 14f));

        JLabel lblPelatis = new JLabel(String.format("Πελάτης: %s - %s",
                selected.getKodikosPelati(), selected.getEponimiaEtairias()));
        lblPelatis.setFont(lblPelatis.getFont().deriveFont(Font.BOLD, 13f));

        JLabel lblPlithos = new JLabel("Πλήθος Αντικαταβολών: " + selected.getPlithosAntikatabolon());
        lblPlithos.setFont(lblPlithos.getFont().deriveFont(Font.BOLD, 13f));

        JLabel lblSynolo = new JLabel(String.format("Σύνολο: %.2f€", selected.getSynoloPoso()));
        lblSynolo.setFont(lblSynolo.getFont().deriveFont(Font.BOLD, 14f));
        lblSynolo.setForeground(new Color(0, 120, 0));

        headerPanel.add(lblIdApodosis);
        headerPanel.add(lblPelatis);
        headerPanel.add(lblPlithos);
        headerPanel.add(lblSynolo);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {
                "Α/Α", "Ημ/νία Παραλαβής", "Ημ/νία Παράδοσης",
                "Courier", "Αρ. Αποστολής", "Αρ. Παραγγελίας",
                "Αντικαταβολή", "Παραλήπτης"
        };

        DefaultTableModel previewTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int rowNum = 1;
        java.time.format.DateTimeFormatter dateFormatter =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (AntikatavoliFullView view : selected.getAntikatavoles()) {
            String imerominiaParalabis = view.getImerominiaParalabis() != null ?
                    view.getImerominiaParalabis().format(dateFormatter) : "-";

            String imerominiaParadosis = view.getImerominiaParadosis() != null ?
                    view.getImerominiaParadosis().format(dateFormatter) : "-";

            String antikatavoli = view.getAntikatavoli() != null ?
                    String.format("%.2f€", view.getAntikatavoli()) : "0,00€";

            Object[] row = {
                    rowNum++,
                    imerominiaParalabis,
                    imerominiaParadosis,
                    view.getCourier() != null ? view.getCourier() : "-",
                    view.getArithmosApostolis() != null ? view.getArithmosApostolis() : "-",
                    view.getArithmosParaggelias() != null ? view.getArithmosParaggelias() : "-",
                    antikatavoli,
                    view.getParaliptis() != null ? view.getParaliptis() : "-"
            };
            previewTableModel.addRow(row);
        }

        JTable previewTable = new JTable(previewTableModel);
        previewTable.setRowHeight(30);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < previewTable.getColumnCount(); i++) {
            previewTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        previewTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        previewTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        previewTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        previewTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        previewTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        previewTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        previewTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        previewTable.getColumnModel().getColumn(7).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(previewTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnClose = new JButton("Κλείσιμο");
        btnClose.addActionListener(e -> previewDialog.dispose());
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setContentPane(mainPanel);
        previewDialog.setSize(1200, 600);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setVisible(true);
    }


    private void ektyposiApodosi() {
        ApodosiGroupView selected = getSelectedApodosi();
        if (selected == null) return;

        JOptionPane.showMessageDialog(this,
                "Η λειτουργία εκτύπωσης θα υλοποιηθεί σύντομα.",
                "Πληροφορία",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportApodosi() {
        ApodosiGroupView selected = getSelectedApodosi();
        if (selected == null) return;

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Αποθήκευση αρχείου Excel");
            fileChooser.setSelectedFile(new java.io.File(
                    "Apodosi_" + selected.getIdApodosis() + "_" + LocalDate.now() + ".xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                AntikatavoliExcelExporter exporter = new AntikatavoliExcelExporter();
                exporter.exportToExcel(selected.getAntikatavoles(), filePath);

                int response = JOptionPane.showConfirmDialog(this,
                        "Η εξαγωγή ολοκληρώθηκε επιτυχώς!\nΘέλετε να ανοίξετε το αρχείο;",
                        "Επιτυχία",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new java.io.File(filePath));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την εξαγωγή: " + ex.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void oristikopoiisiApodosi() {
        ApodosiGroupView selected = getSelectedApodosi();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Θέλετε να οριστικοποιήσετε την απόδοση #%d με %d αντικαταβολές;\n" +
                                "Σύνολο: %.2f€\n" +
                                "Ημερομηνία απόδοσης: %s",
                        selected.getIdApodosis(),
                        selected.getPlithosAntikatabolon(),
                        selected.getSynoloPoso(),
                        LocalDate.now().toString()),
                "Επιβεβαίωση Οριστικοποίησης",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = antikatavoliService.finalizeApodosi(
                    selected.getAntikatavoles(),
                    LocalDate.now()
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Η απόδοση οριστικοποιήθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Σφάλμα κατά την οριστικοποίηση.",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void diagrafiApodosi() {
        ApodosiGroupView selected = getSelectedApodosi();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("ΠΡΟΣΟΧΗ!\n\n" +
                                "Θέλετε να διαγράψετε την απόδοση #%d;\n" +
                                "Οι %d αντικαταβολές θα επιστρέψουν στην κατάσταση 'Εκκρεμής'.\n\n" +
                                "Αυτή η ενέργεια ΔΕΝ μπορεί να αναιρεθεί!",
                        selected.getIdApodosis(),
                        selected.getPlithosAntikatabolon()),
                "Επιβεβαίωση Διαγραφής",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Η λειτουργία διαγραφής θα υλοποιηθεί σύντομα.",
                    "Πληροφορία",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateStats() {
        BigDecimal totalPoso = BigDecimal.ZERO;
        int totalAntikatavoles = 0;

        for (ApodosiGroupView group : allApodoseis) {
            totalPoso = totalPoso.add(group.getSynoloPoso());
            totalAntikatavoles += group.getPlithosAntikatabolon();
        }

        lblStats.setText(String.format("Σύνολο: %d αποδόσεις | %d αντικαταβολές | %.2f€",
                allApodoseis.size(), totalAntikatavoles, totalPoso));
    }

    public void refreshData() {
        loadData();
    }
}
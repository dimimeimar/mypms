package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.customers.dao.PelatisDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.customers.model.Pelatis;
import org.pms.shipments.ui.components.PhoneErrorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AddApostoliDialog extends JDialog {

    private final ApostoliDAO apostoliDAO;
    private final PelatisDAO pelatisDAO;

    private JTabbedPane tabbedPane;
    private JComboBox<String> cmbPelatis;
    private JTextField txtPelatisSearch;
    private List<Pelatis> pelatesList;
    private List<String[]> csvData = new ArrayList<>();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Νέες σταθερές στηλών βάσει του export format
    private static final int COL_ROWNUM = 0;           // #
    private static final int COL_COURIER = 1;          // Courier
    private static final int COL_AR_APOD = 2;          // Αρ. Αποστολής
    private static final int COL_HM_PARALAB = 3;       // Ημ. Παραλαβής
    private static final int COL_ANTIKATAVOLI = 4;     // Αντικαταβολή
    private static final int COL_APOSTOLEAS = 5;       // Αποστολέας
    private static final int COL_PARALIPTIS = 6;       // Παραλήπτης
    private static final int COL_POLI = 7;             // Πόλη
    private static final int COL_DIEFTHINSI = 8;       // Διεύθυνση
    private static final int COL_TK = 9;               // ΤΚ
    private static final int COL_TILEFONO = 10;        // Τηλ. Σταθερό
    private static final int COL_KINITO = 11;          // Κινητό
    private static final int COL_DAYS = 12;            // Ημέρες (skip)
    private static final int COL_STATUS = 13;          // Status
    private static final int COL_STATUS_MYPMS = 14;    // MyPMS Status
    private static final int COL_SXOLIA = 15;          // Σχόλια

    public AddApostoliDialog(Frame parent) {
        super(parent, "Εισαγωγή Αποστολών", true);
        this.apostoliDAO = new ApostoliDAO();
        this.pelatisDAO = new PelatisDAO();
        this.pelatesList = new ArrayList<>();

        initComponents();
        setupLayout();
        setupEvents();
        loadPelates();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        cmbPelatis = new JComboBox<>();
        cmbPelatis.setPreferredSize(new Dimension(250, 30));

        txtPelatisSearch = new JTextField();
        txtPelatisSearch.setPreferredSize(new Dimension(200, 30));
        txtPelatisSearch.setToolTipText("Αναζήτηση πελάτη...");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel singlePanel = createSingleApostoliPanel();
        JPanel bulkPanel = createBulkApostoliPanel();

        tabbedPane.addTab("Μεμονωμένη Αποστολή", singlePanel);
        tabbedPane.addTab("Μαζική Εισαγωγή", bulkPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        JLabel lblPelatis = new JLabel("Πελάτης:");
        lblPelatis.setFont(lblPelatis.getFont().deriveFont(Font.BOLD));

        JLabel lblSearch = new JLabel("Αναζήτηση:");

        panel.add(lblPelatis);
        panel.add(cmbPelatis);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblSearch);
        panel.add(txtPelatisSearch);

        return panel;
    }

    private JPanel createSingleApostoliPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtCourier = new JTextField();
        JComboBox<String> cmbCourier = new JComboBox<>(new String[]{"ACS", "ELTA", "SPEEDEX", "GENIKI"});
        JTextField txtArithmosApostolis = new JTextField();
        JTextField txtArithmosParaggelias = new JTextField();
        JTextField txtImerominia = new JTextField();
        txtImerominia.setText(LocalDate.now().format(dateFormatter));
        JTextField txtAntikatavolos = new JTextField("0.00");
        JTextField txtParaliptis = new JTextField();
        JComboBox<String> cmbXora = new JComboBox<>(new String[]{"Ελλάδα", "Κύπρος"});
        JTextField txtPoli = new JTextField();
        JTextArea txtDiefthinsi = new JTextArea(3, 20);
        JTextField txtTkParalipti = new JTextField(15); // ΝΕΟ ΠΕΔΙΟ
        JTextField txtTilefonoStathero = new JTextField();
        JTextField txtTilefonoKinito = new JTextField();
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{
                "-", "ΣΕ ΔΙΑΝΟΜΗ", "ΠΑΡΑΔΟΘΗΚΕ", "ΕΠΙΣΤΡΟΦΗ", "ΑΚΥΡΩΣΗ"
        });
        JTextArea txtSxolia = new JTextArea(3, 20);

        int row = 0;

        addFormField(panel, gbc, row++, "Courier:", cmbCourier);
        addFormField(panel, gbc, row++, "Αρ. Αποστολής:", txtArithmosApostolis);
        addFormField(panel, gbc, row++, "Αρ. Παραγγελίας:", txtArithmosParaggelias);
        addFormField(panel, gbc, row++, "Ημερομηνία (dd/MM/yyyy):", txtImerominia);
        addFormField(panel, gbc, row++, "Αντικαταβολή (€):", txtAntikatavolos);
        addFormField(panel, gbc, row++, "Παραλήπτης:", txtParaliptis);
        addFormField(panel, gbc, row++, "Χώρα:", cmbXora);
        addFormField(panel, gbc, row++, "Πόλη:", txtPoli);
        addFormField(panel, gbc, row++, "Διεύθυνση:", new JScrollPane(txtDiefthinsi));
        addFormField(panel, gbc, row++, "ΤΚ Παραλήπτη:", txtTkParalipti); // ΝΕΑ ΓΡΑΜΜΗ
        addFormField(panel, gbc, row++, "Τηλέφωνο Σταθερό:", txtTilefonoStathero);
        addFormField(panel, gbc, row++, "Τηλέφωνο Κινητό:", txtTilefonoKinito);
        addFormField(panel, gbc, row++, "Status:", cmbStatus);
        addFormField(panel, gbc, row++, "Σχόλια:", new JScrollPane(txtSxolia));

        JButton btnSaveSingle = new JButton("Αποθήκευση");
        btnSaveSingle.setPreferredSize(new Dimension(120, 35));
        btnSaveSingle.addActionListener(e -> saveSingleApostoli(
                cmbCourier, txtArithmosApostolis, txtArithmosParaggelias, txtImerominia,
                txtAntikatavolos, txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                txtTkParalipti, txtTilefonoStathero, txtTilefonoKinito, cmbStatus, txtSxolia  // ΠΡΟΣΘΗΚΗ txtTkParalipti
        ));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 8, 8, 8);
        gbc.fill = GridBagConstraints.NONE;
        panel.add(btnSaveSingle, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }

    private JPanel createBulkApostoliPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel instructionsLabel = new JLabel("<html>" +
                "<h3>Μαζική Εισαγωγή από Excel Αρχείο:</h3>" +
                "<p>Επιλέξτε ένα Excel αρχείο (.xlsx ή .xls)</p>" +
                "<p><b>Χρησιμοποιήστε το Excel που εξάγετε από την εφαρμογή ως template</b></p>" +
                "<p><b>Απαραίτητες στήλες:</b> Courier, Αρ. Αποστολής, Ημ. Παραλαβής, Παραλήπτης</p>" +
                "<br><p><b>Βήματα:</b></p>" +
                "<p>1. Πατήστε 'Φόρτωση Excel' για να επιλέξετε αρχείο</p>" +
                "<p>2. Ελέγξτε τα δεδομένα που φορτώθηκαν</p>" +
                "<p>3. Πατήστε 'Ανέβασμα στη Βάση' για οριστική εισαγωγή</p>" +
                "</html>");

        JTextArea previewArea = new JTextArea(12, 50);
        previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        previewArea.setBorder(BorderFactory.createLoweredBevelBorder());
        previewArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton btnLoadExcel = new JButton("Φόρτωση Excel");
        btnLoadExcel.setPreferredSize(new Dimension(130, 35));

        JButton btnUploadData = new JButton("Ανέβασμα στη Βάση");
        btnUploadData.setPreferredSize(new Dimension(150, 35));
        btnUploadData.setEnabled(false);

        btnLoadExcel.addActionListener(e -> loadExcelFile(previewArea, btnUploadData));
        btnUploadData.addActionListener(e -> uploadCsvData(previewArea, btnUploadData));

        panel.add(instructionsLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel csvButtonPanel = new JPanel(new FlowLayout());
        csvButtonPanel.add(btnLoadExcel);
        csvButtonPanel.add(btnUploadData);
        panel.add(csvButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadExcelFile(JTextArea previewArea, JButton btnUploadData) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx") ||
                        f.getName().toLowerCase().endsWith(".xls");
            }

            @Override
            public String getDescription() {
                return "Excel Files (*.xlsx, *.xls)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            org.apache.poi.ss.usermodel.Workbook workbook;
            try (java.io.FileInputStream fis = new java.io.FileInputStream(selectedFile)) {
                if (selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                    workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis);
                } else {
                    workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook(fis);
                }
            }

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            csvData.clear();
            StringBuilder preview = new StringBuilder();
            int previewCount = 0;

            boolean isFirstRow = true;
            for (org.apache.poi.ss.usermodel.Row row : sheet) {
                if (row == null) continue;

                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                String[] fields = new String[16]; // Αλλαγή από 36 σε 16 στήλες
                for (int i = 0; i < 16; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(i);
                    if (cell == null) {
                        fields[i] = "";
                    } else {
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                                    fields[i] = date.format(dateFormatter);
                                } else {
                                    double numValue = cell.getNumericCellValue();
                                    // Ειδικός χειρισμός για την αντικαταβολή
                                    if (i == COL_ANTIKATAVOLI) {
                                        fields[i] = String.valueOf(numValue);
                                    } else if (numValue == (long) numValue) {
                                        fields[i] = String.valueOf((long) numValue);
                                    } else {
                                        fields[i] = String.valueOf(numValue);
                                    }
                                }
                                break;
                            case STRING:
                                String cellValue = cell.getStringCellValue().trim();
                                if (i == COL_HM_PARALAB && cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                                    try {
                                        String[] dateParts = cellValue.split("/");
                                        String day = dateParts[0].length() == 1 ? "0" + dateParts[0] : dateParts[0];
                                        String month = dateParts[1].length() == 1 ? "0" + dateParts[1] : dateParts[1];
                                        String year = dateParts[2];
                                        fields[i] = day + "/" + month + "/" + year;
                                    } catch (Exception e) {
                                        fields[i] = cellValue;
                                    }
                                } else {
                                    fields[i] = cellValue;
                                }
                                break;
                            case BOOLEAN:
                                fields[i] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                try {
                                    double numValue = cell.getNumericCellValue();
                                    if (numValue == (long) numValue) {
                                        fields[i] = String.valueOf((long) numValue);
                                    } else {
                                        fields[i] = String.valueOf(numValue).replace(".", ",");
                                    }
                                } catch (Exception e) {
                                    fields[i] = cell.getStringCellValue().trim();
                                }
                                break;
                            default:
                                fields[i] = "";
                                break;
                        }
                    }
                }

                if (fields[COL_SXOLIA] != null) {
                    fields[COL_SXOLIA] = fields[COL_SXOLIA].replaceAll("[\\r\\n]+", " ").trim();
                    if (fields[COL_SXOLIA].equals("null")) {
                        fields[COL_SXOLIA] = "";
                    }
                }

                csvData.add(fields);

                if (previewCount < 10) {
                    preview.append("Γραμμή ").append(previewCount + 1).append(":\n");
                    preview.append("  Αρ. Αποδεικτικού: ").append(fields[COL_AR_APOD]).append("\n");
                    preview.append("  Ημερομηνία: ").append(fields[COL_HM_PARALAB]).append("\n");
                    preview.append("  Παραλήπτης: ").append(fields[COL_PARALIPTIS]).append("\n");
                    preview.append("  Αντικαταβολή: ").append(fields[COL_ANTIKATAVOLI]).append("\n");
                    preview.append("  Πόλη: ").append(fields[COL_POLI]).append("\n");
                    preview.append("  Τηλέφωνο: ").append(fields[COL_TILEFONO]).append("\n");
                    preview.append("  Κινητό: ").append(fields[COL_KINITO]).append("\n\n");
                    previewCount++;
                }
            }

            workbook.close();

            if (csvData.size() > 10) {
                preview.append("... και ").append(csvData.size() - 10).append(" επιπλέον εγγραφές\n");
            }

            preview.append("\nΣύνολο εγγραφές: ").append(csvData.size());

            previewArea.setText(preview.toString());
            btnUploadData.setEnabled(true);

            JOptionPane.showMessageDialog(this,
                    "Φορτώθηκαν " + csvData.size() + " εγγραφές επιτυχώς από το Excel αρχείο!\nΕλέγξτε τα δεδομένα και πατήστε 'Ανέβασμα στη Βάση'",
                    "Επιτυχής Φόρτωση", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά τη φόρτωση του Excel αρχείου: " + e.getMessage(),
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnClose = new JButton("Κλείσιμο");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());

        panel.add(btnClose);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private void setupEvents() {
        txtPelatisSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterPelates(txtPelatisSearch.getText());
            }
        });
    }

    private void loadPelates() {
        try {
            pelatesList = pelatisDAO.findAll();
            updatePelatisComboBox(pelatesList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα φόρτωσης πελατών: " + e.getMessage());
        }
    }

    private void updatePelatisComboBox(List<Pelatis> filteredList) {
        cmbPelatis.removeAllItems();
        cmbPelatis.addItem("-- Επιλέξτε Πελάτη --");

        for (Pelatis pelatis : filteredList) {
            String displayText = pelatis.getKodikosPelati() + " - " +
                    (pelatis.getEponymiaEtairias() != null ? pelatis.getEponymiaEtairias() : "");
            cmbPelatis.addItem(displayText);
        }
    }

    private void filterPelates(String searchText) {
        if (searchText.trim().isEmpty()) {
            updatePelatisComboBox(pelatesList);
            return;
        }

        List<Pelatis> filtered = new ArrayList<>();
        String search = searchText.toLowerCase();

        for (Pelatis pelatis : pelatesList) {
            if ((pelatis.getKodikosPelati() != null && pelatis.getKodikosPelati().toLowerCase().contains(search)) ||
                    (pelatis.getEponymiaEtairias() != null && pelatis.getEponymiaEtairias().toLowerCase().contains(search))) {
                filtered.add(pelatis);
            }
        }

        updatePelatisComboBox(filtered);
    }

    private String getSelectedPelatisCode() {
        String selected = (String) cmbPelatis.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            return null;
        }
        return selected.split(" - ")[0];
    }


    private void saveSingleApostoli(JComboBox<String> cmbCourier, JTextField txtArithmosApostolis,
                                    JTextField txtArithmosParaggelias, JTextField txtImerominia, JTextField txtAntikatavolos,
                                    JTextField txtParaliptis, JComboBox<String> cmbXora, JTextField txtPoli,
                                    JTextArea txtDiefthinsi, JTextField txtTkParalipti, JTextField txtTilefonoStathero,
                                    JTextField txtTilefonoKinito, JComboBox<String> cmbStatus, JTextArea txtSxolia) {

        try {
            String kodikosPelati = getSelectedPelatisCode();
            if (kodikosPelati == null) {
                JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε πελάτη");
                return;
            }

            if (txtArithmosApostolis.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Παρακαλώ εισάγετε αριθμό αποστολής");
                return;
            }

            Apostoli apostoli = new Apostoli();
            apostoli.setKodikosPelati(kodikosPelati);
            apostoli.setCourier((String) cmbCourier.getSelectedItem());
            apostoli.setArithmosApostolis(txtArithmosApostolis.getText().trim());
            apostoli.setArithmosParaggelias(txtArithmosParaggelias.getText().trim());

            try {
                LocalDate date = LocalDate.parse(txtImerominia.getText().trim(), dateFormatter);
                apostoli.setImerominiaParalabis(date);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Λάθος μορφή ημερομηνίας. Χρησιμοποιήστε dd/MM/yyyy");
                return;
            }

            try {
                BigDecimal antikatavolos = new BigDecimal(txtAntikatavolos.getText().trim().replace(",", "."));
                apostoli.setAntikatavoli(antikatavolos);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Λάθος μορφή αντικαταβολής");
                return;
            }

            apostoli.setParaliptis(txtParaliptis.getText().trim());
            apostoli.setXora((String) cmbXora.getSelectedItem());
            apostoli.setPoli(txtPoli.getText().trim());
            apostoli.setDiefthinsi(txtDiefthinsi.getText().trim());
            apostoli.setTkParalipti(txtTkParalipti.getText().trim()); // ΝΕΑ ΓΡΑΜΜΗ
            apostoli.setTilefonoStathero(txtTilefonoStathero.getText().trim());
            apostoli.setTilefonoKinito(txtTilefonoKinito.getText().trim());
            apostoli.setStatusApostolis((String) cmbStatus.getSelectedItem());
            apostoli.setSxolia(txtSxolia.getText().trim());
            apostoli.setIstoriko("");

            boolean success = apostoliDAO.create(apostoli);

            if (success) {
                JOptionPane.showMessageDialog(this, "Η αποστολή αποθηκεύτηκε επιτυχώς!");
                clearSingleForm(cmbCourier, txtArithmosApostolis, txtArithmosParaggelias, txtImerominia,
                        txtAntikatavolos, txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                        txtTkParalipti, txtTilefonoStathero, txtTilefonoKinito, cmbStatus, txtSxolia); // ΠΡΟΣΘΗΚΗ txtTkParalipti
            } else {
                JOptionPane.showMessageDialog(this, "Σφάλμα κατά την αποθήκευση της αποστολής!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + e.getMessage());
        }
    }

    private void clearSingleForm(JComboBox<String> cmbCourier, JTextField txtArithmosApostolis,
                                 JTextField txtArithmosParaggelias, JTextField txtImerominia, JTextField txtAntikatavolos,
                                 JTextField txtParaliptis, JComboBox<String> cmbXora, JTextField txtPoli,
                                 JTextArea txtDiefthinsi, JTextField txtTkParalipti, JTextField txtTilefonoStathero,
                                 JTextField txtTilefonoKinito, JComboBox<String> cmbStatus, JTextArea txtSxolia) {

        txtArithmosApostolis.setText("");
        txtArithmosParaggelias.setText("");
        txtImerominia.setText(LocalDate.now().format(dateFormatter));
        txtAntikatavolos.setText("0.00");
        txtParaliptis.setText("");
        txtPoli.setText("");
        txtDiefthinsi.setText("");
        txtTkParalipti.setText(""); // ΝΕΑ ΓΡΑΜΜΗ
        txtTilefonoStathero.setText("");
        txtTilefonoKinito.setText("");
        txtSxolia.setText("");
        cmbCourier.setSelectedIndex(0);
        cmbXora.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }


    private void uploadCsvData(JTextArea previewArea, JButton btnUploadData) {
        if (csvData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν δεδομένα για ανέβασμα!");
            return;
        }

        String kodikosPelati = getSelectedPelatisCode();
        if (kodikosPelati == null) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε πελάτη");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Θα ανέβουν " + csvData.size() + " εγγραφές στη βάση.\nΣυνέχεια;",
                "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int successCount = 0;
        int errorCount = 0;
        int skippedCount = 0;
        StringBuilder errors = new StringBuilder();

        for (int i = 0; i < csvData.size(); i++) {
            String[] fields = csvData.get(i);

            if (apostoliDAO.existsByArithmosApostolis(fields[COL_AR_APOD].trim())) {
                skippedCount++;
                continue;
            }

            try {
                Apostoli apostoli = new Apostoli();
                apostoli.setKodikosPelati(kodikosPelati);
                apostoli.setCourier(fields.length > COL_COURIER ? fields[COL_COURIER].trim() : "ACS");
                apostoli.setArithmosApostolis(fields[COL_AR_APOD].trim());
                apostoli.setArithmosParaggelias(""); // Δεν υπάρχει στο export

                try {
                    String dateStr = fields[COL_HM_PARALAB].trim();
                    if (!dateStr.isEmpty()) {
                        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                        apostoli.setImerominiaParalabis(date);
                    }
                } catch (DateTimeParseException e) {
                    errors.append("Γραμμή ").append(i + 1).append(": Λάθος μορφή ημερομηνίας\n");
                    errorCount++;
                    continue;
                }

                try {
                    String amountStr = "";

                    // Έλεγχος αν το πεδίο υπάρχει και δεν είναι κενό
                    if (fields.length > COL_ANTIKATAVOLI && fields[COL_ANTIKATAVOLI] != null) {
                        amountStr = fields[COL_ANTIKATAVOLI].trim();

                        // Αφαίρεση του € σύμβολου αν υπάρχει
                        amountStr = amountStr.replace("€", "").trim();

                        // Αντικατάσταση κόμματος με τελεία για δεκαδικά
                        amountStr = amountStr.replace(",", ".");

                        // Έλεγχος για κενό string μετά τις αλλαγές
                        if (amountStr.isEmpty() || amountStr.equals("-")) {
                            amountStr = "0";
                        }
                    } else {
                        amountStr = "0";
                    }

                    System.out.println("Debug - Γραμμή " + (i + 1) + ": Αντικαταβολή raw = '" +
                            (fields.length > COL_ANTIKATAVOLI ? fields[COL_ANTIKATAVOLI] : "null") +
                            "', processed = '" + amountStr + "'");

                    BigDecimal antikatavolos = new BigDecimal(amountStr);
                    apostoli.setAntikatavoli(antikatavolos);

                } catch (NumberFormatException e) {
                    System.err.println("Σφάλμα parsing αντικαταβολής για γραμμή " + (i + 1) +
                            ": '" + (fields.length > COL_ANTIKATAVOLI ? fields[COL_ANTIKATAVOLI] : "null") + "'");
                    apostoli.setAntikatavoli(BigDecimal.ZERO);
                }

                apostoli.setParaliptis(fields[COL_PARALIPTIS].trim());
                apostoli.setXora("Ελλάδα"); // Default value
                apostoli.setPoli(fields.length > COL_POLI ? fields[COL_POLI].trim() : "");
                apostoli.setDiefthinsi(fields.length > COL_DIEFTHINSI ? fields[COL_DIEFTHINSI].trim() : "");
                apostoli.setTkParalipti(fields.length > COL_TK ? fields[COL_TK].trim() : "");

                // Έλεγχος για μεγάλα τηλέφωνα και εμφάνιση dialog αν χρειάζεται
                String statheroPhone = fields.length > COL_TILEFONO ? fields[COL_TILEFONO].trim() : "";
                String kinitoPhone = fields.length > COL_KINITO ? fields[COL_KINITO].trim() : "";

                if (PhoneErrorDialog.isPhoneTooLong(statheroPhone) || PhoneErrorDialog.isPhoneTooLong(kinitoPhone)) {
                    PhoneErrorDialog phoneDialog = new PhoneErrorDialog(
                            (Frame) SwingUtilities.getWindowAncestor(this),
                            i + 1,
                            statheroPhone,
                            kinitoPhone
                    );
                    phoneDialog.setVisible(true);

                    if (!phoneDialog.isUserConfirmed()) {
                        // Χρήστης επέλεξε παράλειψη γραμμής
                        continue;
                    }

                    statheroPhone = phoneDialog.getCorrectedStatheroPhone();
                    kinitoPhone = phoneDialog.getCorrectedKinitoPhone();
                }

                apostoli.setTilefonoStathero(statheroPhone);
                apostoli.setTilefonoKinito(kinitoPhone);
                apostoli.setStatusApostolis(fields.length > COL_STATUS ? fields[COL_STATUS].trim() : "-");
                apostoli.setSxolia(fields.length > COL_SXOLIA ? fields[COL_SXOLIA].trim() : "");
                apostoli.setIstoriko("");

                boolean success = apostoliDAO.create(apostoli);
                if (success) {
                    successCount++;
                } else {
                    errors.append("Γραμμή ").append(i + 1).append(": Σφάλμα αποθήκευσης\n");
                    errorCount++;
                }

            } catch (Exception e) {
                errors.append("Γραμμή ").append(i + 1).append(": ").append(e.getMessage()).append("\n");
                errorCount++;
            }
        }

        String message = "Ανέβασμα ολοκληρώθηκε!\n" +
                "Επιτυχείς: " + successCount + "\n" +
                "Kαταχωρημένα: " + skippedCount + "\n" +
                "Σφάλματα: " + errorCount;

        if (errors.length() > 0) {
            message += "\n\nΣφάλματα:\n" + errors.toString();
        }

        JOptionPane.showMessageDialog(this, message);

        if (successCount > 0) {
            csvData.clear();
            previewArea.setText("");
            btnUploadData.setEnabled(false);
            dispose();
        }
    }
}
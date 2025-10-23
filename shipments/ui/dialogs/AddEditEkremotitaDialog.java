package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.EkremotitaDAO;
import org.pms.shipments.model.Ekremotita;

import javax.swing.*;
import java.awt.*;

public class AddEditEkremotitaDialog extends JDialog {

    private final EkremotitaDAO ekremotitaDAO;
    private final ApostoliDAO apostoliDAO;
    private final Ekremotita ekremotita;
    private final boolean isEditMode;

    private JTextField txtTitlos;
    private JTextArea txtPerigrafi;
    private JTextField txtStatus;
    private JComboBox<String> cmbPriority;
    private JLabel lblApostoliHeader;

    private boolean saved = false;
    private Integer selectedApostoliId = null;

    public AddEditEkremotitaDialog(Frame parent, EkremotitaDAO ekremotitaDAO,
                                   ApostoliDAO apostoliDAO, Ekremotita ekremotita) {
        super(parent, ekremotita == null ? "Νέα Εκκρεμότητα" : "Επεξεργασία Εκκρεμότητας", true);
        this.ekremotitaDAO = ekremotitaDAO;
        this.apostoliDAO = apostoliDAO;
        this.ekremotita = ekremotita;
        this.isEditMode = ekremotita != null;

        initComponents();
        setupLayout();

        if (isEditMode) {
            loadData();
        }

        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        txtTitlos = new JTextField(40);
        txtPerigrafi = new JTextArea(8, 40);
        txtPerigrafi.setLineWrap(true);
        txtPerigrafi.setWrapStyleWord(true);

        txtStatus = new JTextField(30);
        txtStatus.setEditable(false);
        txtStatus.setBackground(new Color(240, 240, 240));

        cmbPriority = new JComboBox<>(new String[]{"ΧΑΜΗΛΗ", "ΜΕΣΑΙΑ", "ΥΨΗΛΗ"});

        lblApostoliHeader = new JLabel();
        lblApostoliHeader.setFont(lblApostoliHeader.getFont().deriveFont(Font.BOLD, 14f));
        lblApostoliHeader.setForeground(new Color(0, 100, 0));
        lblApostoliHeader.setComponentPopupMenu(createCopyPopupMenu());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        if (isEditMode && ekremotita.getArithmosApostolis() != null) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 3;
            lblApostoliHeader.setText("Αποστολή: " + ekremotita.getArithmosApostolis());
            mainPanel.add(lblApostoliHeader, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 3;
            mainPanel.add(new JSeparator(), gbc);
            row++;
        }

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Τίτλος:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtTitlos, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Περιγραφή:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(txtPerigrafi), gbc);

        row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Εμφάνιση Status μόνο σε edit mode
        if (isEditMode) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            mainPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            mainPanel.add(txtStatus, gbc);
            row++;
        }

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Προτεραιότητα:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(cmbPriority, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton(isEditMode ? "Ενημέρωση" : "Αποθήκευση");
        JButton btnCancel = new JButton("Ακύρωση");

        btnSave.setPreferredSize(new Dimension(120, 35));
        btnCancel.setPreferredSize(new Dimension(120, 35));

        btnSave.addActionListener(e -> saveEkremotita());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        txtTitlos.setText(ekremotita.getTitlos());
        txtPerigrafi.setText(ekremotita.getPerigrafi());
        txtStatus.setText(ekremotita.getStatus());
        cmbPriority.setSelectedItem(ekremotita.getPriority());

        if (ekremotita.getIdApostolis() != null) {
            selectedApostoliId = ekremotita.getIdApostolis();
        }
    }

    private void saveEkremotita() {
        String titlos = txtTitlos.getText().trim();
        String perigrafi = txtPerigrafi.getText().trim();

        if (titlos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ο τίτλος είναι υποχρεωτικός!",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (perigrafi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Η περιγραφή είναι υποχρεωτική!",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ekremotita toSave = isEditMode ? ekremotita : new Ekremotita();
        toSave.setTitlos(titlos);
        toSave.setPerigrafi(perigrafi);

        // Στην δημιουργία βάζουμε default status "ΕΝΕΡΓΗ"
        if (!isEditMode) {
            toSave.setStatus("ΕΝΕΡΓΗ");
        }
        // Στην επεξεργασία το status δεν αλλάζει (παραμένει ό,τι ήταν)

        toSave.setPriority((String) cmbPriority.getSelectedItem());
        toSave.setIdApostolis(selectedApostoliId);

        if (!isEditMode) {
            toSave.setCreatedBy(System.getProperty("user.name"));
        }

        boolean success = isEditMode ?
                ekremotitaDAO.update(toSave) :
                ekremotitaDAO.create(toSave);

        if (success) {
            saved = true;
            JOptionPane.showMessageDialog(this,
                    isEditMode ? "Η εκκρεμότητα ενημερώθηκε επιτυχώς!" : "Η εκκρεμότητα δημιουργήθηκε επιτυχώς!",
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την αποθήκευση!",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private JPopupMenu createCopyPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Αντιγραφή Αρ. Αποστολής");
        copyItem.addActionListener(e -> {
            if (ekremotita != null && ekremotita.getArithmosApostolis() != null) {
                java.awt.datatransfer.StringSelection stringSelection =
                        new java.awt.datatransfer.StringSelection(ekremotita.getArithmosApostolis());
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
        popupMenu.add(copyItem);
        return popupMenu;
    }
}
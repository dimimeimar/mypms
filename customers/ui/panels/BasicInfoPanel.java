package org.pms.customers.ui.panels;

import org.pms.customers.model.Pelatis;
import org.pms.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Panel για τα βασικά στοιχεία του πελάτη
 */
public class BasicInfoPanel extends JPanel {

    // Components
    private JTextField txtKodikosPelati;
    private JComboBox<String> cmbKategoria;
    private JTextField txtEponymiaEtairias;
    private JTextField txtAfmEtairias;
    private JTextField txtDouEtairias;
    private JTextField txtNomimosEkprospos;
    private JTextField txtAfmNomimuEkprosopu;
    private JTextField txtDiakritikosTitlos;
    private JTextField txtNomikiMorfi;
    private JTextArea txtEpaggelmaAntikimeno;

    private boolean isEditMode = false;

    public BasicInfoPanel(boolean isEditMode) {
        this.isEditMode = isEditMode;
        initComponents();
        setupLayout();
        setupFormatting();
    }

    private void initComponents() {
        txtKodikosPelati = new JTextField(15);

        // Bigger ComboBox for category
        cmbKategoria = new JComboBox<>(new String[]{"A", "B", "C", "D", "E"});
        cmbKategoria.setPreferredSize(new Dimension(80, 25));

        txtEponymiaEtairias = new JTextField(25);
        txtAfmEtairias = new JTextField(15);
        txtDouEtairias = new JTextField(15);
        txtNomimosEkprospos = new JTextField(25);
        txtAfmNomimuEkprosopu = new JTextField(15);
        txtDiakritikosTitlos = new JTextField(25);
        txtNomikiMorfi = new JTextField(15);

        txtEpaggelmaAntikimeno = new JTextArea(3, 25);
        txtEpaggelmaAntikimeno.setLineWrap(true);
        txtEpaggelmaAntikimeno.setWrapStyleWord(true);

        // Disable kodikos field in edit mode
        if (isEditMode) {
            txtKodikosPelati.setEditable(false);
            txtKodikosPelati.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Row 0: Kodikos and Kategoria
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Κωδικός Πελάτη:*"), gbc);
        gbc.gridx = 1;
        add(txtKodikosPelati, gbc);
        gbc.gridx = 2;
        add(new JLabel("Κατηγορία:*"), gbc);
        gbc.gridx = 3;
        add(cmbKategoria, gbc);

        row++;

        // Row 1: Company name
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Επωνυμία Εταιρίας:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtEponymiaEtairias, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Row 2: AFM and DOU
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("ΑΦΜ Εταιρίας:"), gbc);
        gbc.gridx = 1;
        add(txtAfmEtairias, gbc);
        gbc.gridx = 2;
        add(new JLabel("ΔΟΥ Εταιρίας:"), gbc);
        gbc.gridx = 3;
        add(txtDouEtairias, gbc);

        row++;

        // Row 3: Legal representative
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Νόμιμος Εκπρόσωπος:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtNomimosEkprospos, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Row 4: Representative AFM and Legal form
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("ΑΦΜ Εκπροσώπου:"), gbc);
        gbc.gridx = 1;
        add(txtAfmNomimuEkprosopu, gbc);
        gbc.gridx = 2;
        add(new JLabel("Νομική Μορφή:"), gbc);
        gbc.gridx = 3;
        add(txtNomikiMorfi, gbc);

        row++;

        // Row 5: Distinctive title
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Διακριτικός Τίτλος:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtDiakritikosTitlos, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Row 6: Business description
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Επάγγελμα/Αντικείμενο:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        add(new JScrollPane(txtEpaggelmaAntikimeno), gbc);
    }

    private void setupFormatting() {
        // Auto-format AFM fields
        addNumericFormatter(txtAfmEtairias, 9);
        addNumericFormatter(txtAfmNomimuEkprosopu, 9);

        // Smart defaults based on category
        cmbKategoria.addActionListener(e -> applySmartDefaults());
    }

    private void addNumericFormatter(JTextField textField, int maxLength) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
                if (textField.getText().length() >= maxLength && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }

    private void applySmartDefaults() {
        if (isEditMode) return; // Don't apply defaults in edit mode

        String category = (String) cmbKategoria.getSelectedItem();
        if (category != null) {
            switch (category) {
                case "A":
                    if (txtNomikiMorfi.getText().trim().isEmpty()) {
                        txtNomikiMorfi.setText("Α.Ε.");
                    }
                    break;
                case "B":
                    if (txtNomikiMorfi.getText().trim().isEmpty()) {
                        txtNomikiMorfi.setText("Ε.Π.Ε.");
                    }
                    break;
                case "C":
                    if (txtNomikiMorfi.getText().trim().isEmpty()) {
                        txtNomikiMorfi.setText("Ο.Ε.");
                    }
                    break;
            }
        }
    }

    // Validation method
    public boolean validateData() {
        // Required field validation
        if (txtKodikosPelati.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ο κωδικός πελάτη είναι υποχρεωτικός!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtKodikosPelati.requestFocus();
            return false;
        }

        if (txtEponymiaEtairias.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Η επωνυμία εταιρίας είναι υποχρεωτική!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtEponymiaEtairias.requestFocus();
            return false;
        }

        // Validate AFM format (if provided)
        String afmEtairias = txtAfmEtairias.getText().trim();
        if (!afmEtairias.isEmpty() && !ValidationUtils.isValidAFM(afmEtairias)) {
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρος ΑΦΜ εταιρίας!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtAfmEtairias.requestFocus();
            return false;
        }

        String afmEkprosopu = txtAfmNomimuEkprosopu.getText().trim();
        if (!afmEkprosopu.isEmpty() && !ValidationUtils.isValidAFM(afmEkprosopu)) {
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρος ΑΦΜ νόμιμου εκπροσώπου!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtAfmNomimuEkprosopu.requestFocus();
            return false;
        }

        return true;
    }

    // Data loading and saving methods
    public void loadData(Pelatis pelatis) {
        txtKodikosPelati.setText(pelatis.getKodikosPelati());
        cmbKategoria.setSelectedItem(pelatis.getKategoria());
        txtEponymiaEtairias.setText(pelatis.getEponymiaEtairias());
        txtAfmEtairias.setText(pelatis.getAfmEtairias());
        txtDouEtairias.setText(pelatis.getDouEtairias());
        txtNomimosEkprospos.setText(pelatis.getNomimosEkprospos());
        txtAfmNomimuEkprosopu.setText(pelatis.getAfmNomimuEkprosopu());
        txtDiakritikosTitlos.setText(pelatis.getDiakritikosTitlos());
        txtNomikiMorfi.setText(pelatis.getNomikiMorfi());
        txtEpaggelmaAntikimeno.setText(pelatis.getEpaggelmaAntikimeno());
    }

    public void saveData(Pelatis pelatis) {
        pelatis.setKodikosPelati(txtKodikosPelati.getText().trim());
        pelatis.setKategoria((String) cmbKategoria.getSelectedItem());
        pelatis.setEponymiaEtairias(txtEponymiaEtairias.getText().trim());
        pelatis.setAfmEtairias(txtAfmEtairias.getText().trim());
        pelatis.setDouEtairias(txtDouEtairias.getText().trim());
        pelatis.setNomimosEkprospos(txtNomimosEkprospos.getText().trim());
        pelatis.setAfmNomimuEkprosopu(txtAfmNomimuEkprosopu.getText().trim());
        pelatis.setDiakritikosTitlos(txtDiakritikosTitlos.getText().trim());
        pelatis.setNomikiMorfi(txtNomikiMorfi.getText().trim());
        pelatis.setEpaggelmaAntikimeno(txtEpaggelmaAntikimeno.getText().trim());
    }

    public void clearData() {
        if (!isEditMode) {
            txtKodikosPelati.setText("");
        }
        cmbKategoria.setSelectedIndex(0);
        txtEponymiaEtairias.setText("");
        txtAfmEtairias.setText("");
        txtDouEtairias.setText("");
        txtNomimosEkprospos.setText("");
        txtAfmNomimuEkprosopu.setText("");
        txtDiakritikosTitlos.setText("");
        txtNomikiMorfi.setText("");
        txtEpaggelmaAntikimeno.setText("");

        if (!isEditMode) {
            txtKodikosPelati.requestFocus();
        }
    }
}
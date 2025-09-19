package org.example.view.shipment;

import org.example.dao.ApostoliDAO;
import org.example.model.Apostoli;
import javax.swing.*;
import java.awt.*;

public class ApostoliDetailsStatusMyPMSPanel extends JPanel {

    private final ApostoliDetailsDialog parentDialog;
    private final Apostoli apostoli;
    private final ApostoliDAO apostoliDAO;

    private JComboBox<String> cmbStatusMyPMS;
    private JCheckBox chkCustom;
    private JTextField txtCustomStatus;
    private JComboBox<String> cmbStatusCourier;

    public ApostoliDetailsStatusMyPMSPanel(ApostoliDetailsDialog parentDialog,
                                           Apostoli apostoli,
                                           ApostoliDAO apostoliDAO) {
        this.parentDialog = parentDialog;
        this.apostoli = apostoli;
        this.apostoliDAO = apostoliDAO;

        initializePanel();
        loadData();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("Status MyPMS");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setForeground(new Color(0, 100, 200));
        add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Dropdown με περισσότερες επιλογές
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Status MyPMS:"), gbc);

        String[] statusOptions = {
                "-", "ΠΑΚ", "ΜΑΠ", "ΠΔΑ", "ΔΘΠ",
                "ΠΑΡΑΤΑΣΗ", "ΦΡΑΓΗ", "BLOCK",
                "ΕΠΑΝΑΠΟΣΤΟΛΗ", "ΕΠΑΝΑΠΡΟΩΘΗΣΗ",
                "ΕΙΝΑΙ ΕΚΤΟΣ", "EMAIL ΣΕ ΑΠΟΣΤΟΛΕΑ",
                "EMAIL ΣΕ ACS","EMAIL ΕΠΙΛΥΣΗ"
        };
        cmbStatusMyPMS = new JComboBox<>(statusOptions);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbStatusMyPMS, gbc);

        // Checkbox "Άλλο"
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        chkCustom = new JCheckBox("Άλλο:");
        formPanel.add(chkCustom, gbc);

        // Textbox για custom επιλογή
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCustomStatus = new JTextField();
        txtCustomStatus.setEnabled(false);
        formPanel.add(txtCustomStatus, gbc);


        // Status Courier section
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Status Courier:"), gbc);

        String[] courierStatusOptions = {
                "-", "ΣΕ ΜΕΤΑΦΟΡΑ", "ΠΑΡΑΔΟΘΗΚΕ", "ΑΝΑΜΟΝΗ ΠΑΡΑΔΟΣΗΣ", "ΑΚΥΡΩΘΗΚΕ",
                "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ", "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ", "ΕΝΤΟΛΗ ΠΑΡΑΛΑΒΗΣ ΑΠΟ ΓΡΑΦΕΙΟ",
                "ΒΡΙΣΚΕΤΑΙ ΣΤΗΝ ΔΙΑΔΡΟΜΗ ΠΡΟΣ ΤΟ ΚΑΤΑΣΤΗΜΑ ΠΑΡΑΔΟΣΗΣ", "ΠΑΡΑΔΟΣΗ RECEPTION ΕΝΤ.ΑΠΟΣΤ",
                "ΑΡΝΗΣΗ ΧΡΕΩΣΗΣ", "ΑΔΥΝΑΜΙΑ ΠΛΗΡΩΜΗΣ", "ΜΗ ΑΠΟΔΟΧΗ ΑΠΟΣΤΟΛΗΣ", "ΑΠΕΒΙΩΣΕ",
                "ΑΠΩΝ", "ΔΥΣΠΡΟΣΙΤΗ ΠΕΡΙΟΧΗ", "ΕΛΛΙΠΗ ΔΙΚΑΙΟΛΟΓΗΤΙΚΑ", "ΑΓΝΩΣΤΟΣ ΠΑΡΑΛΗΠΤΗΣ",
                "ΑΛΛΑΓΗ ΔΙΕΥΘΥΝΣΗΣ", "ΛΑΝΘΑΣΜΕΝΗ, ΕΛΛΙΠΗΣ ΔΙΕΥΘΥΝΣΗ", "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛΗ ΑΠΟΣΤ",
                "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛ ΠΑΡ/ΠΤΗ", "ΣΥΓΚΕΚΡΙΜΕΝΗ ΗΜ/ΝΙΑ ΠΑΡΑΔΟΣΗΣ ΕΝΤΟΛΗ ΠΑΡΑΛΗΠΤΗ ΑΝΑΚΑΤΕΥΘΥΝΣΗ"
        };

        cmbStatusCourier = new JComboBox<>(courierStatusOptions);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbStatusCourier, gbc);


        // Event handlers
        chkCustom.addActionListener(e -> {
            boolean isCustom = chkCustom.isSelected();
            txtCustomStatus.setEnabled(isCustom);
            cmbStatusMyPMS.setEnabled(!isCustom);

            if (!isCustom) {
                txtCustomStatus.setText("");
                saveStatus((String) cmbStatusMyPMS.getSelectedItem());
            }
        });

        txtCustomStatus.addActionListener(e -> {
            if (chkCustom.isSelected() && !txtCustomStatus.getText().trim().isEmpty()) {
                saveStatus(txtCustomStatus.getText().trim());
            }
        });

        txtCustomStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (chkCustom.isSelected() && !txtCustomStatus.getText().trim().isEmpty()) {
                    saveStatus(txtCustomStatus.getText().trim());
                }
            }
        });

        cmbStatusMyPMS.addActionListener(e -> {
            if (!chkCustom.isSelected()) {
                saveStatus((String) cmbStatusMyPMS.getSelectedItem());
            }
        });

        cmbStatusCourier.addActionListener(e -> {
            String newStatus = (String) cmbStatusCourier.getSelectedItem();
            String currentStatus = apostoli.getStatusApostolis();

            if (!newStatus.equals(currentStatus)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Αλλαγή Status Courier από \"" + currentStatus + "\" σε \"" + newStatus + "\"?",
                        "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    apostoli.setStatusApostolis(newStatus);
                    apostoli.setStatusLocked(Boolean.TRUE);
                    apostoliDAO.update(apostoli);
                } else {
                    cmbStatusCourier.setSelectedItem(currentStatus);
                }
            }
        });


        add(formPanel, BorderLayout.CENTER);
    }

    private void loadData() {

        // Load Status Courier
        if (apostoli.getStatusApostolis() != null) {
            cmbStatusCourier.setSelectedItem(apostoli.getStatusApostolis());
        }


        if (apostoli.getStatusMypms() != null) {
            String status = apostoli.getStatusMypms();
            boolean found = false;

            // Ελέγχουμε αν υπάρχει στο dropdown
            for (int i = 0; i < cmbStatusMyPMS.getItemCount(); i++) {
                if (status.equals(cmbStatusMyPMS.getItemAt(i))) {
                    found = true;
                    cmbStatusMyPMS.setSelectedItem(status);
                    break;
                }
            }

            // Αν δεν υπάρχει, είναι custom τιμή
            if (!found && !status.equals("-")) {
                chkCustom.setSelected(true);
                txtCustomStatus.setEnabled(true);
                txtCustomStatus.setText(status);
                cmbStatusMyPMS.setEnabled(false);
            }
        }
    }

    private void saveStatus(String status) {
        apostoli.setStatusMypms(status);
        apostoliDAO.update(apostoli);
    }
}
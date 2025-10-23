package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.EkremotitaDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.Ekremotita;

import javax.swing.*;
import java.awt.*;

public class AddEkremotitaForApostoliDialog extends JDialog {

    private final EkremotitaDAO ekremotitaDAO;
    private final Apostoli apostoli;

    private JTextField txtTitlos;
    private JTextArea txtPerigrafi;
    private JComboBox<String> cmbPriority;
    private JLabel lblApostoliInfo;

    private boolean saved = false;

    public AddEkremotitaForApostoliDialog(Frame parent, Class<EkremotitaDAO> ekremotitaDAOClass,
                                          Apostoli apostoli) {
        super(parent, "Νέα Εκκρεμότητα για Αποστολή", true);
        this.ekremotitaDAO = new EkremotitaDAO();
        this.apostoli = apostoli;

        initComponents();
        setupLayout();

        setSize(650, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        txtTitlos = new JTextField(40);
        txtPerigrafi = new JTextArea(8, 40);
        txtPerigrafi.setLineWrap(true);
        txtPerigrafi.setWrapStyleWord(true);

        cmbPriority = new JComboBox<>(new String[]{"ΧΑΜΗΛΗ", "ΜΕΣΑΙΑ", "ΥΨΗΛΗ"});

        lblApostoliInfo = new JLabel();
        lblApostoliInfo.setFont(lblApostoliInfo.getFont().deriveFont(Font.BOLD, 13f));
        lblApostoliInfo.setForeground(new Color(0, 100, 0));
        lblApostoliInfo.setComponentPopupMenu(createCopyPopupMenu());

        String apostoliText = String.format("Αποστολή: %s - %s (%s)",
                apostoli.getArithmosApostolis(),
                apostoli.getParaliptis(),
                apostoli.getPoli());
        lblApostoliInfo.setText(apostoliText);
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

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(lblApostoliInfo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        row++;

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Τίτλος:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtTitlos, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Περιγραφή:"), gbc);
        gbc.gridx = 1;
        JScrollPane scrollPerigrafi = new JScrollPane(txtPerigrafi);
        mainPanel.add(scrollPerigrafi, gbc);
        row++;


        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Προτεραιότητα:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(cmbPriority, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnSave = new JButton("Αποθήκευση");
        JButton btnCancel = new JButton("Ακύρωση");

        btnSave.setPreferredSize(new Dimension(130, 35));
        btnCancel.setPreferredSize(new Dimension(130, 35));

        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));

        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFont(btnCancel.getFont().deriveFont(Font.BOLD));

        btnSave.addActionListener(e -> saveEkremotita());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
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

        Ekremotita ekremotita = new Ekremotita();
        ekremotita.setTitlos(titlos);
        ekremotita.setPerigrafi(perigrafi);
        ekremotita.setStatus("ΕΝΕΡΓΗ");
        ekremotita.setPriority((String) cmbPriority.getSelectedItem());
        ekremotita.setIdApostolis(apostoli.getIdApostolis());
        ekremotita.setCreatedBy(System.getProperty("user.name"));

        boolean success = ekremotitaDAO.create(ekremotita);

        if (success) {
            saved = true;
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
            java.awt.datatransfer.StringSelection stringSelection =
                    new java.awt.datatransfer.StringSelection(apostoli.getArithmosApostolis());
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        });
        popupMenu.add(copyItem);
        return popupMenu;
    }
}
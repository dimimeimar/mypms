package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.EkremotitaDAO;
import org.pms.shipments.model.Ekremotita;

import javax.swing.*;
import java.awt.*;

public class ViewEkremotitaDialog extends JDialog {

    private final EkremotitaDAO ekremotitaDAO;
    private final ApostoliDAO apostoliDAO;
    private final Ekremotita ekremotita;

    private JTextField txtTitlos;
    private JTextArea txtPerigrafi;
    private JTextField txtStatus;
    private JTextField txtPriority;
    private JTextField txtCreatedBy;
    private JTextField txtCreatedAt;
    private JTextField txtUpdatedAt;
    private JTextField txtResolvedAt;
    private JLabel lblApostoliHeader;

    private boolean dataChanged = false;

    public ViewEkremotitaDialog(Frame parent, EkremotitaDAO ekremotitaDAO,
                                ApostoliDAO apostoliDAO, Ekremotita ekremotita) {
        super(parent, "Προβολή Εκκρεμότητας", true);
        this.ekremotitaDAO = ekremotitaDAO;
        this.apostoliDAO = apostoliDAO;
        this.ekremotita = ekremotita;

        initComponents();
        setupLayout();
        loadData();

        setSize(750, 650);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        txtTitlos = new JTextField(40);
        txtTitlos.setEditable(false);
        txtTitlos.setBackground(new Color(240, 240, 240));

        txtPerigrafi = new JTextArea(8, 40);
        txtPerigrafi.setLineWrap(true);
        txtPerigrafi.setWrapStyleWord(true);
        txtPerigrafi.setEditable(false);
        txtPerigrafi.setBackground(new Color(240, 240, 240));

        txtStatus = new JTextField(30);
        txtStatus.setEditable(false);
        txtStatus.setBackground(new Color(240, 240, 240));

        txtPriority = new JTextField(30);
        txtPriority.setEditable(false);
        txtPriority.setBackground(new Color(240, 240, 240));

        txtCreatedBy = new JTextField(30);
        txtCreatedBy.setEditable(false);
        txtCreatedBy.setBackground(new Color(240, 240, 240));

        txtCreatedAt = new JTextField(30);
        txtCreatedAt.setEditable(false);
        txtCreatedAt.setBackground(new Color(240, 240, 240));

        txtUpdatedAt = new JTextField(30);
        txtUpdatedAt.setEditable(false);
        txtUpdatedAt.setBackground(new Color(240, 240, 240));

        txtResolvedAt = new JTextField(30);
        txtResolvedAt.setEditable(false);
        txtResolvedAt.setBackground(new Color(240, 240, 240));

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

        if (ekremotita.getArithmosApostolis() != null) {
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
        JLabel lblTitlos = new JLabel("Τίτλος:");
        lblTitlos.setFont(lblTitlos.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblTitlos, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtTitlos, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblPerigrafi = new JLabel("Περιγραφή:");
        lblPerigrafi.setFont(lblPerigrafi.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblPerigrafi, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JScrollPane scrollPerigrafi = new JScrollPane(txtPerigrafi);
        scrollPerigrafi.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPerigrafi, gbc);

        row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblStatus, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtStatus, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblPriority = new JLabel("Προτεραιότητα:");
        lblPriority.setFont(lblPriority.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblPriority, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtPriority, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        mainPanel.add(new JSeparator(), gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblCreatedBy = new JLabel("Δημιουργήθηκε από:");
        lblCreatedBy.setFont(lblCreatedBy.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblCreatedBy, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtCreatedBy, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblCreatedAt = new JLabel("Ημερομηνία Δημιουργίας:");
        lblCreatedAt.setFont(lblCreatedAt.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblCreatedAt, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtCreatedAt, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblUpdatedAt = new JLabel("Τελευταία Ενημέρωση:");
        lblUpdatedAt.setFont(lblUpdatedAt.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblUpdatedAt, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtUpdatedAt, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblResolvedAt = new JLabel("Ημερομηνία Επίλυσης:");
        lblResolvedAt.setFont(lblResolvedAt.getFont().deriveFont(Font.BOLD));
        mainPanel.add(lblResolvedAt, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(txtResolvedAt, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnEdit = new JButton("Επεξεργασία");
        btnEdit.setPreferredSize(new Dimension(140, 35));
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.BLACK);
        btnEdit.setFont(btnEdit.getFont().deriveFont(Font.BOLD));
        btnEdit.addActionListener(e -> openEditDialog());

        JButton btnClose = new JButton("Κλείσιμο");
        btnClose.setPreferredSize(new Dimension(140, 35));
        btnClose.setBackground(new Color(158, 158, 158));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFont(btnClose.getFont().deriveFont(Font.BOLD));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        txtTitlos.setText(ekremotita.getTitlos());
        txtPerigrafi.setText(ekremotita.getPerigrafi());
        txtStatus.setText(ekremotita.getStatus());
        txtPriority.setText(ekremotita.getPriority());
        txtCreatedBy.setText(ekremotita.getCreatedBy() != null ? ekremotita.getCreatedBy() : "-");

        if (ekremotita.getCreatedAt() != null) {
            txtCreatedAt.setText(ekremotita.getCreatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } else {
            txtCreatedAt.setText("-");
        }

        if (ekremotita.getUpdatedAt() != null) {
            txtUpdatedAt.setText(ekremotita.getUpdatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } else {
            txtUpdatedAt.setText("-");
        }

        if (ekremotita.getResolvedAt() != null) {
            txtResolvedAt.setText(ekremotita.getResolvedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } else {
            txtResolvedAt.setText("-");
        }
    }

    private void openEditDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = (window instanceof Frame) ? (Frame) window : null;

        AddEditEkremotitaDialog editDialog = new AddEditEkremotitaDialog(
                parentFrame,
                ekremotitaDAO,
                apostoliDAO,
                ekremotita
        );

        editDialog.setVisible(true);

        if (editDialog.isSaved()) {
            dataChanged = true;

            Ekremotita updated = ekremotitaDAO.findById(ekremotita.getIdEkremotita());
            if (updated != null) {
                ekremotita.setTitlos(updated.getTitlos());
                ekremotita.setPerigrafi(updated.getPerigrafi());
                ekremotita.setStatus(updated.getStatus());
                ekremotita.setPriority(updated.getPriority());
                ekremotita.setUpdatedAt(updated.getUpdatedAt());
                ekremotita.setResolvedAt(updated.getResolvedAt());

                loadData();

                JOptionPane.showMessageDialog(this,
                        "Η εκκρεμότητα ενημερώθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public boolean isDataChanged() {
        return dataChanged;
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
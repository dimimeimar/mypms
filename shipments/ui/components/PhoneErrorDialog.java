package org.pms.shipments.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PhoneErrorDialog extends JDialog {

    private JTextField txtStatheroPhone;
    private JTextField txtKinitoPhone;
    private JLabel lblRowInfo;
    private boolean userConfirmed = false;
    private String correctedStatheroPhone = "";
    private String correctedKinitoPhone = "";

    public PhoneErrorDialog(Frame parent, int rowNumber, String originalStathero, String originalKinito) {
        super(parent, "Σφάλμα Τηλεφώνου - Γραμμή " + rowNumber, true);

        initComponents(rowNumber, originalStathero, originalKinito);
        setupLayout();
        setupEvents();

        setSize(500, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void initComponents(int rowNumber, String originalStathero, String originalKinito) {
        lblRowInfo = new JLabel("Γραμμή " + rowNumber + ": Τα τηλέφωνα είναι πολύ μεγάλα (μέγιστο 20 χαρακτήρες)");
        lblRowInfo.setFont(lblRowInfo.getFont().deriveFont(Font.BOLD));
        lblRowInfo.setForeground(Color.RED);

        txtStatheroPhone = new JTextField(originalStathero);
        txtStatheroPhone.setPreferredSize(new Dimension(200, 25));

        txtKinitoPhone = new JTextField(originalKinito);
        txtKinitoPhone.setPreferredSize(new Dimension(200, 25));

        // Προαιρετική αυτόματη διόρθωση - κρατάμε μόνο αριθμούς
        autoCorrectPhone(txtStatheroPhone);
        autoCorrectPhone(txtKinitoPhone);
    }

    private void autoCorrectPhone(JTextField phoneField) {
        String original = phoneField.getText();
        if (original.length() > 20) {
            // Κρατάμε μόνο αριθμούς και κάποια σύμβολα
            String cleaned = original.replaceAll("[^0-9+\\-\\s()]", "");
            if (cleaned.length() > 20) {
                cleaned = cleaned.substring(0, 20);
            }
            phoneField.setText(cleaned);
            phoneField.setBackground(new Color(255, 255, 200)); // Κίτρινο background για να δείξει ότι άλλαξε
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(lblRowInfo);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Σταθερό Τηλέφωνο
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Σταθερό Τηλέφωνο:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtStatheroPhone, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblStatheroLength = new JLabel("(" + txtStatheroPhone.getText().length() + "/20)");
        formPanel.add(lblStatheroLength, gbc);

        // Κινητό Τηλέφωνο
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Κινητό Τηλέφωνο:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(txtKinitoPhone, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblKinitoLength = new JLabel("(" + txtKinitoPhone.getText().length() + "/20)");
        formPanel.add(lblKinitoLength, gbc);

        // Ενημέρωση των labels όταν αλλάζει το κείμενο
        txtStatheroPhone.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            private void updateLabel() {
                SwingUtilities.invokeLater(() -> {
                    int length = txtStatheroPhone.getText().length();
                    lblStatheroLength.setText("(" + length + "/20)");
                    lblStatheroLength.setForeground(length > 20 ? Color.RED : Color.BLACK);
                });
            }
        });

        txtKinitoPhone.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLabel(); }
            private void updateLabel() {
                SwingUtilities.invokeLater(() -> {
                    int length = txtKinitoPhone.getText().length();
                    lblKinitoLength.setText("(" + length + "/20)");
                    lblKinitoLength.setForeground(length > 20 ? Color.RED : Color.BLACK);
                });
            }
        });

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnSave = new JButton("Καταχώρηση & Συνέχεια");
        btnSave.setBackground(new Color(0, 150, 0));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));
        btnSave.setPreferredSize(new Dimension(160, 35));

        JButton btnSkip = new JButton("Παράλειψη Γραμμής");
        btnSkip.setBackground(new Color(220, 20, 60));
        btnSkip.setForeground(Color.WHITE);
        btnSkip.setFont(btnSkip.getFont().deriveFont(Font.BOLD));
        btnSkip.setPreferredSize(new Dimension(140, 35));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnSkip);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button events
        btnSave.addActionListener(e -> saveAndContinue());
        btnSkip.addActionListener(e -> skipRow());
    }

    private void setupEvents() {
        // ESC key to close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skipRow();
            }
        });

        // Enter key to save
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKeyStroke, "ENTER");
        getRootPane().getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndContinue();
            }
        });
    }

    private void saveAndContinue() {
        String stathero = txtStatheroPhone.getText().trim();
        String kinito = txtKinitoPhone.getText().trim();

        // Έλεγχος μήκους
        if (stathero.length() > 20 || kinito.length() > 20) {
            JOptionPane.showMessageDialog(this,
                    "Τα τηλέφωνα δεν μπορούν να ξεπερνούν τους 20 χαρακτήρες!\n" +
                            "Σταθερό: " + stathero.length() + "/20\n" +
                            "Κινητό: " + kinito.length() + "/20",
                    "Σφάλμα Μήκους", JOptionPane.WARNING_MESSAGE);
            return;
        }

        correctedStatheroPhone = stathero;
        correctedKinitoPhone = kinito;
        userConfirmed = true;
        dispose();
    }

    private void skipRow() {
        correctedStatheroPhone = "";
        correctedKinitoPhone = "";
        userConfirmed = false;
        dispose();
    }

    // Getters
    public boolean isUserConfirmed() {
        return userConfirmed;
    }

    public String getCorrectedStatheroPhone() {
        return correctedStatheroPhone;
    }

    public String getCorrectedKinitoPhone() {
        return correctedKinitoPhone;
    }

    // Utility method για έλεγχο μήκους τηλεφώνου
    public static boolean isPhoneTooLong(String phone) {
        return phone != null && phone.trim().length() > 20;
    }
}
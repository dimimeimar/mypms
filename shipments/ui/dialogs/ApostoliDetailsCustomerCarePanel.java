package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.CustomerCareDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.CustomerCareComment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApostoliDetailsCustomerCarePanel extends JPanel {

    private final ApostoliDetailsDialog parentDialog;
    private final Apostoli apostoli;
    private final CustomerCareDAO customerCareDAO;

    private DefaultListModel<String> listModel;
    private JList<String> commentsList;
    private List<CustomerCareComment> customerCareComments;

    private JTextField txtCustomStatus;
    private JComboBox<String> cmbStatusCourier;

    public ApostoliDetailsCustomerCarePanel(ApostoliDetailsDialog parentDialog,
                                            Apostoli apostoli,
                                            CustomerCareDAO customerCareDAO) {
        this.parentDialog = parentDialog;
        this.apostoli = apostoli;
        this.customerCareDAO = customerCareDAO;

        initializePanel();
        loadCustomerCareComments();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("Ιστορικό Customer Care & Status MyPMS");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setForeground(new Color(0, 100, 200));
        add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel statusMyPMSPanel = createStatusMyPMSPanel();
        centerPanel.add(statusMyPMSPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        commentsList = new JList<>(listModel);
        commentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsList.setBorder(BorderFactory.createLoweredBevelBorder());

        Font listFont = commentsList.getFont();
        commentsList.setFont(new Font(listFont.getName(), listFont.getStyle(), listFont.getSize() + 1));

        JScrollPane scrollPane = new JScrollPane(commentsList);
        scrollPane.setPreferredSize(new Dimension(600, 250));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ιστορικό Σχολίων"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setupEvents();
    }

    private JPanel createStatusMyPMSPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 200), 2),
                "Διαχείριση Status"
        ));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Status MyPMS Section
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblMyPMS = new JLabel("Status MyPMS:");
        lblMyPMS.setFont(lblMyPMS.getFont().deriveFont(Font.BOLD));
        panel.add(lblMyPMS, gbc);

        txtCustomStatus = new JTextField(15);  // Μικρότερο μέγεθος
        gbc.gridx = 1;
        panel.add(txtCustomStatus, gbc);

        JButton btnSaveMyPMS = new JButton("Αποθήκευση");  // Μικρότερο κείμενο
        btnSaveMyPMS.setBackground(new Color(0, 150, 0));
        btnSaveMyPMS.setForeground(Color.BLACK);
        btnSaveMyPMS.setFont(btnSaveMyPMS.getFont().deriveFont(Font.BOLD, 11f));  // Μικρότερο font
        btnSaveMyPMS.setPreferredSize(new Dimension(120, 28));  // Μικρότερο μέγεθος
        gbc.gridx = 2;
        panel.add(btnSaveMyPMS, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(new JSeparator(), gbc);

        // Status Courier Section
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblCourier = new JLabel("Status Courier:");
        lblCourier.setFont(lblCourier.getFont().deriveFont(Font.BOLD));
        panel.add(lblCourier, gbc);

        String[] courierStatusOptions = {
                "ΠΑΡΑΔΟΘΗΚΕ", "ΠΡΟΣ ΕΠΙΣΤΟΦΗ", "ΕΠΙΣΤΡΑΦΗΚΕ",
                "ΑΚΥΡΩΘΗΚΕ", "ΣΕ ΔΙΑΚΙΝΗΣΗ", "ΑΔΙΑΚΙΝΗΤΟ",
                "ΑΠΟΣΤΟΛΗ ΔΕΝ ΒΡΕΘΗΚΕ", "ΜΗ ΠΑΡΑΔΟΣΗ",
                "ΠΑΡΑΔΟΣΗ RECEPTION ΕΝΤ.ΑΠΟΣΤ", "ΑΡΝΗΣΗ ΧΡΕΩΣΗΣ",
                "ΑΔΥΝΑΜΙΑ ΠΛΗΡΩΜΗΣ", "ΜΗ ΑΠΟΔΟΧΗ ΑΠΟΣΤΟΛΗΣ",
                "ΑΠΕΒΙΩΣΕ", "ΑΓΝΩ", "ΔΥΣΠΡΟΣΙΤΗ ΠΕΡΙΟΧΗ",
                "ΕΛΛΙΠΗ ΔΙΚΑΙΟΛΟΓΗΤΙΚΑ", "ΑΓΝΩΣΤΟΣ ΠΑΡΑΛΗΠΤΗΣ",
                "ΑΛΛΑΓΗ ΔΙΕΥΘΥΝΣΗΣ", "ΛΑΝΘΑΣΜΕΝΗ, ΕΛΛΙΠΗΣ ΔΙΕΥΘΥΝΣΗ",
                "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛΗ ΑΠΟΣΤ",
                "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛ ΠΑΡ/ΠΤΗ",
                "ΣΥΓΚΕΚΡΙΜΕΝΗ ΗΜ/ΝΙΑ ΠΑΡΑΔΟΣΗΣ ΕΝΤΟΛΗ ΠΑΡΑΛΗΠΤΗ ΑΝΑΚΑΤΕΥΘΥΝΣΗ"
        };
        cmbStatusCourier = new JComboBox<>(courierStatusOptions);
        cmbStatusCourier.setEnabled(true);
        cmbStatusCourier.setPreferredSize(new Dimension(250, 25));  // Μικρότερο μέγεθος
        gbc.gridx = 1;
        panel.add(cmbStatusCourier, gbc);

        JButton btnSaveCourier = new JButton("Αποθήκευση");  // Μικρότερο κείμενο
        btnSaveCourier.setBackground(new Color(220, 20, 60));
        btnSaveCourier.setForeground(Color.BLACK);
        btnSaveCourier.setFont(btnSaveCourier.getFont().deriveFont(Font.BOLD, 11f));  // Μικρότερο font
        btnSaveCourier.setPreferredSize(new Dimension(120, 28));  // Μικρότερο μέγεθος
        gbc.gridx = 2;
        panel.add(btnSaveCourier, gbc);

        // Event Listeners
        btnSaveMyPMS.addActionListener(e -> saveStatusMyPMS());
        btnSaveCourier.addActionListener(e -> saveStatusCourier());

        loadStatusMyPMS();
        loadStatusCourier();

        return panel;
    }

    private void loadStatusCourier() {
        if (apostoli.getStatusApostolis() != null) {
            cmbStatusCourier.setSelectedItem(apostoli.getStatusApostolis());
        }
    }


    private void saveStatusCourier() {
        String newStatus = (String) cmbStatusCourier.getSelectedItem();
        String currentStatus = apostoli.getStatusApostolis();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Θέλετε να κλειδώσετε το Status Courier σε: '" + newStatus + "';\n\n" +
                        "ΠΡΟΣΟΧΗ: Μετά από αυτό, το status ΔΕΝ θα ενημερώνεται αυτόματα από το tracking!",
                "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            apostoli.setStatusApostolis(newStatus);
            apostoli.setStatusLocked(Boolean.TRUE);

            org.pms.shipments.dao.ApostoliDAO apostoliDAO = new org.pms.shipments.dao.ApostoliDAO();
            if (apostoliDAO.update(apostoli)) {
                String commentText = String.format("Αλλαγή Status Courier: '%s' → '%s' (ΚΛΕΙΔΩΜΕΝΟ)",
                        currentStatus != null ? currentStatus : "-",
                        newStatus);

                CustomerCareComment comment = new CustomerCareComment(apostoli.getIdApostolis(), commentText);
                customerCareDAO.create(comment);
                loadCustomerCareComments();

                JOptionPane.showMessageDialog(this,
                        "Το Status Courier κλειδώθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            cmbStatusCourier.setSelectedItem(currentStatus);
        }
    }

    private void loadStatusMyPMS() {
        String status = apostoli.getStatusMypms();
        if (status != null && !status.equals("-")) {
            txtCustomStatus.setText(status);
        }
    }

    private void saveStatusMyPMS() {
        String newStatus = txtCustomStatus.getText().trim();

        if (newStatus.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ εισάγετε status!",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldStatus = apostoli.getStatusMypms();
        apostoli.setStatusMypms(newStatus);

        org.pms.shipments.dao.ApostoliDAO apostoliDAO = new org.pms.shipments.dao.ApostoliDAO();
        if (apostoliDAO.update(apostoli)) {
            CustomerCareComment comment = new CustomerCareComment(apostoli.getIdApostolis(), newStatus);
            if (customerCareDAO.create(comment)) {
                loadCustomerCareComments();
                JOptionPane.showMessageDialog(this,
                        "Το Status MyPMS ενημερώθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την ενημέρωση του status!",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Προσθήκη");  // Μικρότερο κείμενο
        addButton.setBackground(new Color(0, 150, 0));
        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD, 11f));  // Μικρότερο font
        addButton.setPreferredSize(new Dimension(100, 28));  // Μικρότερο μέγεθος

        JButton editButton = new JButton("Επεξεργασία");
        editButton.setBackground(new Color(255, 165, 0));
        editButton.setFont(editButton.getFont().deriveFont(Font.BOLD, 11f));
        editButton.setPreferredSize(new Dimension(110, 28));

        JButton deleteButton = new JButton("Διαγραφή");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.BOLD, 11f));
        deleteButton.setPreferredSize(new Dimension(100, 28));

        // Add action listeners
        addButton.addActionListener(e -> addComment());
        editButton.addActionListener(e -> editSelectedComment());
        deleteButton.addActionListener(e -> deleteSelectedComment());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private void setupEvents() {
        commentsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedComment();
                }
            }
        });
    }

    private void loadCustomerCareComments() {
        customerCareComments = customerCareDAO.findByApostoli(apostoli.getIdApostolis());

        listModel.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (CustomerCareComment comment : customerCareComments) {
            String dateStr = comment.getCreatedAt() != null ?
                    comment.getCreatedAt().format(formatter) : "";
            String displayText = "[" + dateStr + "] " + comment.getSxolio();
            listModel.addElement(displayText);
        }
    }

    private void addComment() {
        String comment = JOptionPane.showInputDialog(this,
                "Εισάγετε το νέο σχόλιο:",
                "Νέο Σχόλιο",
                JOptionPane.PLAIN_MESSAGE);

        if (comment != null && !comment.trim().isEmpty()) {
            CustomerCareComment newComment = new CustomerCareComment(
                    apostoli.getIdApostolis(), comment.trim());

            if (customerCareDAO.create(newComment)) {
                loadCustomerCareComments();
                JOptionPane.showMessageDialog(this,
                        "Το σχόλιο προστέθηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Σφάλμα κατά την προσθήκη του σχολίου!",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedComment() {
        int selectedIndex = commentsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε ένα σχόλιο για επεξεργασία.",
                    "Δεν έχει επιλεγεί σχόλιο",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedIndex >= 0 && selectedIndex < customerCareComments.size()) {
            CustomerCareComment comment = customerCareComments.get(selectedIndex);

            String newText = JOptionPane.showInputDialog(this,
                    "Επεξεργασία σχολίου:",
                    comment.getSxolio());

            if (newText != null && !newText.trim().isEmpty()) {
                comment.setSxolio(newText.trim());

                if (customerCareDAO.update(comment)) {
                    loadCustomerCareComments();
                    JOptionPane.showMessageDialog(this,
                            "Το σχόλιο ενημερώθηκε επιτυχώς!",
                            "Επιτυχία",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Σφάλμα κατά την ενημέρωση του σχολίου!",
                            "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteSelectedComment() {
        int selectedIndex = commentsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε ένα σχόλιο για διαγραφή.",
                    "Δεν έχει επιλεγεί σχόλιο",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το σχόλιο;",
                "Επιβεβαίωση Διαγραφής",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && selectedIndex >= 0 && selectedIndex < customerCareComments.size()) {
            CustomerCareComment comment = customerCareComments.get(selectedIndex);

            if (customerCareDAO.delete(comment.getId())) {
                loadCustomerCareComments();
                JOptionPane.showMessageDialog(this,
                        "Το σχόλιο διαγράφηκε επιτυχώς!",
                        "Επιτυχία",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Σφάλμα κατά τη διαγραφή του σχολίου!",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
package org.example.ui.dialogs.shipment;

import org.example.dao.CustomerCareDAO;
import org.example.model.Apostoli;
import org.example.model.CustomerCareComment;

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

        JLabel headerLabel = new JLabel("Ιστορικό Customer Care");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setForeground(new Color(0, 100, 200));
        add(headerLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        commentsList = new JList<>(listModel);
        commentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsList.setBorder(BorderFactory.createLoweredBevelBorder());

        Font listFont = commentsList.getFont();
        commentsList.setFont(new Font(listFont.getName(), listFont.getStyle(), listFont.getSize() + 1));

        JScrollPane scrollPane = new JScrollPane(commentsList);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setupEvents();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Προσθήκη Σχολίου");
        addButton.setBackground(new Color(0, 150, 0));
        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD));

        JButton editButton = new JButton("Επεξεργασία");
        editButton.setBackground(new Color(255, 165, 0));
        editButton.setFont(editButton.getFont().deriveFont(Font.BOLD));

        JButton deleteButton = new JButton("Διαγραφή");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.BOLD));

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
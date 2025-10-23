package org.pms.shipments.ui.panels;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.dao.CustomerCareDAO;
import org.pms.shipments.model.CustomerCareComment;
import org.pms.shipments.ui.dialogs.ApostoliTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ApostoliStatusEditor {

    private final JTable table;
    private final ApostoliTableModel tableModel;
    private final ApostoliDAO apostoliDAO;
    private final Component parentComponent;
    private JComboBox<String> statusMyPMSCombo;
    private final Set<String> addedCustomValues = new HashSet<>();
    private final String[] originalOptions = {
            "-", "ΠΑΚ", "ΜΑΠ", "ΠΔΑ", "ΔΘΠ",
            "ΠΑΡΑΤΑΣΗ", "ΦΡΑΓΗ", "BLOCK",
            "ΕΠΑΝΑΠΟΣΤΟΛΗ", "ΕΠΑΝΑΠΡΟΩΘΗΣΗ",
            "ΕΙΝΑΙ ΕΚΤΟΣ", "EMAIL ΣΕ ΑΠΟΣΤΟΛΕΑ",
            "EMAIL ΣΕ ACS","EMAIL ΕΠΙΛΥΣΗ"
    };

    public ApostoliStatusEditor(JTable table, ApostoliTableModel tableModel,
                                ApostoliDAO apostoliDAO, Component parentComponent) {
        this.table = table;
        this.tableModel = tableModel;
        this.apostoliDAO = apostoliDAO;
        this.parentComponent = parentComponent;
    }

    private void cleanupAllCustomValues() {
        for (String customValue : addedCustomValues) {
            statusMyPMSCombo.removeItem(customValue);
        }
        addedCustomValues.clear();
    }

    private void updateStatusMyPMS(String selectedValue) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        if (selectedRow < 0 || selectedRow >= table.getRowCount()) {
            return;
        }

        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int statusMyPMSColumnIndex = getColumnIndex("Status MyPMS");

            String arithmosApostolis = (String) tableModel.getValueAt(modelRow, 2);
            Apostoli apostoli = apostoliDAO.findByArithmosApostolis(arithmosApostolis);

            if (apostoli != null) {
                String oldStatus = apostoli.getStatusMypms();
                apostoli.setStatusMypms(selectedValue);

                if (apostoliDAO.update(apostoli)) {
                    tableModel.setValueAt(selectedValue, modelRow, statusMyPMSColumnIndex);

                    createCustomerCareComment(apostoli.getIdApostolis(), oldStatus, selectedValue);
                } else {
                    showError("Σφάλμα κατά την ενημέρωση!");
                }
            } else {
                showError("Δεν βρέθηκε η αποστολή!");
            }
        } catch (Exception e) {
            showError("Σφάλμα: " + e.getMessage());
        }
    }

    private void createCustomerCareComment(int idApostolis, String oldStatus, String newStatus) {
        try {
            CustomerCareDAO customerCareDAO =
                    new CustomerCareDAO();

            CustomerCareComment comment =
                    new CustomerCareComment(idApostolis, newStatus);

            customerCareDAO.create(comment);
        } catch (Exception e) {
            System.err.println("Σφάλμα δημιουργίας customer care comment: " + e.getMessage());
        }
    }

    private void updateStatusInDatabase(String statusType) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        if (selectedRow >= table.getRowCount()) {
            return;
        }

        try {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int columnIndex = getColumnIndex(statusType);
            Object newStatus = tableModel.getValueAt(modelRow, columnIndex);

            if (newStatus != null) {
                String arithmosApostolis = (String) tableModel.getValueAt(modelRow, 2);
                Apostoli apostoli = apostoliDAO.findByArithmosApostolis(arithmosApostolis);

                if (apostoli != null) {
                    if ("Status Courier".equals(statusType)) {
                        apostoli.setStatusApostolis(newStatus.toString());
                        apostoli.setStatusLocked(Boolean.TRUE);
                    } else if ("Status MyPMS".equals(statusType)) {
                        apostoli.setStatusMypms(newStatus.toString());
                    }

                    if (!apostoliDAO.update(apostoli)) {
                        showError("Σφάλμα κατά την ενημέρωση!");
                    }
                } else {
                    showError("Δεν βρέθηκε η αποστολή!");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            showError("Σφάλμα ευρετηρίου - δοκιμάστε ξανά");
        } catch (Exception ex) {
            showError("Σφάλμα: " + ex.getMessage());
        }
    }

    private boolean containsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (item.equals(comboBox.getItemAt(i))) {
                return true;
            }
        }
        return false;
    }

    private int getColumnIndex(String columnName) {
        String[] columnNames = tableModel.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equals(columnNames[i])) {
                return i;
            }
        }
        return -1;
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parentComponent, message, "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        });
    }
}
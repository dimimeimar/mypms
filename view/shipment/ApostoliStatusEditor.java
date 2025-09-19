package org.example.view.shipment;

import org.example.dao.ApostoliDAO;
import org.example.model.Apostoli;
import javax.swing.*;
import java.awt.*;
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

    public void setupStatusColumnEditor() {
        setupStatusMyPMSActionColumn();
    }

    private void askForDeliveryDate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= table.getRowCount()) {
            return;
        }

        try {
            if (selectedRow >= table.getRowCount() || table.getRowCount() == 0) {
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);
            String arithmosApostolis = (String) tableModel.getValueAt(modelRow, 2);
            Apostoli apostoli = apostoliDAO.findByArithmosApostolis(arithmosApostolis);

            if (apostoli != null) {
                apostoli.setImerominiaParadosis(LocalDate.now());
                apostoli.setStatusApostolis("OK");
                apostoliDAO.update(apostoli);

                SwingUtilities.invokeLater(() -> {
                    if (parentComponent instanceof ApostoliManagementPanel) {
                        ((ApostoliManagementPanel) parentComponent).performSearch();
                    }
                });
            }
        } catch (Exception ex) {
            showError("Σφάλμα κατά την ενημέρωση ημερομηνίας παράδοσης: " + ex.getMessage());
        }
    }

    private void setupStatusMyPMSActionColumn() {
        statusMyPMSCombo = new JComboBox<>(originalOptions);

        DefaultCellEditor editor = new DefaultCellEditor(statusMyPMSCombo) {
            private String originalValue;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                originalValue = value != null ? value.toString() : "-";

                cleanupAllCustomValues();

                if (!originalValue.equals("-") && !containsItem(statusMyPMSCombo, originalValue)) {
                    statusMyPMSCombo.addItem(originalValue);
                    addedCustomValues.add(originalValue);
                }

                statusMyPMSCombo.setSelectedItem(originalValue);
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public Object getCellEditorValue() {
                String selectedValue = (String) statusMyPMSCombo.getSelectedItem();
                if ("-".equals(selectedValue)) {
                    return originalValue;
                }
                return selectedValue;
            }

            @Override
            public boolean stopCellEditing() {
                try {
                    String selectedValue = (String) statusMyPMSCombo.getSelectedItem();
                    if (!"-".equals(selectedValue) && !selectedValue.equals(originalValue)) {
                        updateStatusMyPMS(selectedValue);
                    }
                    return super.stopCellEditing();
                } catch (IndexOutOfBoundsException e) {
                    return false;
                } catch (Exception e) {
                    showError("Σφάλμα κατά την ενημέρωση: " + e.getMessage());
                    return false;
                }
            }

            @Override
            public void cancelCellEditing() {
                statusMyPMSCombo.setSelectedItem(originalValue);
                super.cancelCellEditing();
            }
        };

        table.getColumnModel().getColumn(12).setCellEditor(editor);
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
                apostoli.setStatusMypms(selectedValue);
                if (apostoliDAO.update(apostoli)) {
                    tableModel.setValueAt(selectedValue, modelRow, statusMyPMSColumnIndex);
                } else {
                    showError("Σφάλμα κατά την ενημέρωση!");
                }
            } else {
                showError("Δεν βρέθηκε η αποστολή!");
            }
        } catch (IndexOutOfBoundsException e) {
            showError("Σφάλμα ευρετηρίου - δοκιμάστε ξανά");
        } catch (Exception ex) {
            showError("Σφάλμα: " + ex.getMessage());
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
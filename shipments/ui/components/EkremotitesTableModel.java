package org.pms.shipments.ui.components;

import org.pms.shipments.model.Ekremotita;

import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EkremotitesTableModel extends DefaultTableModel {

    private final String[] columnNames = {
            "A/A", "Τίτλος", "Αρ. Αποστολής", "Προτεραιότητα",
            "Status", "Ημ. Δημιουργίας", "Δημιουργήθηκε από"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EkremotitesTableModel() {
        super();
        setColumnIdentifiers(columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        return String.class;
    }

    public void loadData(List<Ekremotita> ekremotitesList) {
        setRowCount(0);

        int rowNumber = 1;
        for (Ekremotita ekremotita : ekremotitesList) {
            String apostoliStr = ekremotita.getArithmosApostolis() != null
                    ? ekremotita.getArithmosApostolis()
                    : "Γενική";

            String dateStr = ekremotita.getCreatedAt() != null
                    ? ekremotita.getCreatedAt().format(dateFormatter)
                    : "-";

            String createdBy = ekremotita.getCreatedBy() != null
                    ? ekremotita.getCreatedBy()
                    : "-";

            Object[] rowData = {
                    rowNumber++,
                    ekremotita.getTitlos(),
                    apostoliStr,
                    ekremotita.getPriority(),
                    ekremotita.getStatus(),
                    dateStr,
                    createdBy
            };
            addRow(rowData);
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void clearData() {
        setRowCount(0);
    }
}
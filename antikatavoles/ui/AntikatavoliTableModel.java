package org.pms.antikatavoles.ui.dialogs;

import org.pms.antikatavoles.model.Antikatavoli;
import org.pms.antikatavoles.model.AntikatavoliFullView;

import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AntikatavoliTableModel extends DefaultTableModel {
    private final String[] columnNames = {
            "Α/Α", "Ημ/νία Παραλαβής", "Ημ/νία Παράδοσης", "Courier",
            "Αρ. Αποστολής", "Αρ. Παραγγελίας", "Αντικαταβολή", "Παραλήπτης",
            "Παραστατικό ACS", "Παραστατικό MyPMS", "Ημ/νία Απόδοσης", "Σχόλια"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AntikatavoliTableModel() {
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
        if (columnIndex == 6) {
            return Boolean.class;
        }
        return String.class;
    }

    public void loadDataFromFullView(List<AntikatavoliFullView> fullViewList, int startingRowNumber) {
        setRowCount(0);

        int rowNumber = startingRowNumber;
        for (AntikatavoliFullView view : fullViewList) {
            String imerominiaParalabis = "";
            if (view.getImerominiaParalabis() != null) {
                imerominiaParalabis = view.getImerominiaParalabis().format(dateFormatter);
            }

            String imerominiaParadosis = "";
            if (view.getImerominiaParadosis() != null) {
                imerominiaParadosis = view.getImerominiaParadosis().format(dateFormatter);
            }

            String antikatavoli = view.getAntikatavoli() != null ?
                    String.format("%.2f€", view.getAntikatavoli()) : "0,00€";

            String imerominiaApodosis = "";
            if (view.getImerominiaApodosis() != null) {
                imerominiaApodosis = view.getImerominiaApodosis().format(dateFormatter);
            }

            Object[] rowData = {
                    rowNumber++,
                    imerominiaParalabis,
                    imerominiaParadosis,
                    view.getCourier() != null ? view.getCourier() : "-",
                    view.getArithmosApostolis() != null ? view.getArithmosApostolis() : "-",
                    view.getArithmosParaggelias() != null ? view.getArithmosParaggelias() : "-",
                    antikatavoli,
                    view.getParaliptis() != null ? view.getParaliptis() : "-",
                    view.getParastatikoACS() != null ? view.getParastatikoACS() : "-",
                    view.getParastatikoMyPMS() != null ? view.getParastatikoMyPMS() : "-",
                    imerominiaApodosis,
                    view.getSxolia() != null ? view.getSxolia() : ""
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
package org.example.view.shipment;

import org.example.model.Apostoli;
import org.example.model.Pelatis;
import org.example.dao.PelatisDAO;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApostoliTableModel extends DefaultTableModel {

    private final String[] columnNames = {
            "A/A", "Courier", "Αρ. Αποστολής", "Ημερομηνία Παραλαβής", "Αντικαταβολή",
            "Αποστολέας", "Παραλήπτης", "Πόλη", "Τηλέφωνο", "Κινητό", "Σε διακίνηση",
            "Status Courier", "Status MyPMS"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PelatisDAO pelatisDAO;

    public ApostoliTableModel(PelatisDAO pelatisDAO) {
        super();
        this.pelatisDAO = pelatisDAO;
        setColumnIdentifiers(columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 12; // Μόνο η στήλη Status MyPMS
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        if (columnIndex == 10) { // Στήλη "Σε διακίνηση"
            return Integer.class;
        }
        return String.class;
    }

    public void loadData(List<Apostoli> apostolesList) {
        setRowCount(0);

        int rowNumber = 1;
        for (Apostoli apostoli : apostolesList) {
            String senderName = apostoli.getKodikosPelati();
            Pelatis pelatis = pelatisDAO.findById(apostoli.getKodikosPelati());
            if (pelatis != null) {
                senderName = pelatis.getEponymiaEtairias();
            }

            String antikatavolosStr = "0,00€";
            if (apostoli.getAntikatavoli() != null) {
                antikatavolosStr = String.format("%.2f€", apostoli.getAntikatavoli());
            }

            String dateStr = "";
            if (apostoli.getImerominiaParalabis() != null) {
                dateStr = apostoli.getImerominiaParalabis().format(dateFormatter);
            }

            String statheroPhone = "";
            if (apostoli.getTilefonoStathero() != null && !apostoli.getTilefonoStathero().trim().isEmpty()) {
                statheroPhone = apostoli.getTilefonoStathero();
            }

            String phone = "";
            if (apostoli.getTilefonoKinito() != null && !apostoli.getTilefonoKinito().trim().isEmpty()) {
                phone = apostoli.getTilefonoKinito();
            }

            // Υπολογισμός ημερών διακίνησης
            Object diakinisiDays = "";
            if (apostoli.getImerominiaParalabis() != null) {
                // Αν έχει παραδοθεί, δείξε τικ - ΕΚΤΟΣ αν έχει αλλάξει χειροκίνητα
                if ("ΠΑΡΑΔΟΘΗΚΕ".equals(apostoli.getStatusApostolis())) {
                    diakinisiDays = "TICK_IMAGE";
                } else {
                    // Αν δεν έχει παραδοθεί ακόμη ή έχει αλλάξει χειροκίνητα από ΠΑΡΑΔΟΘΗΚΕ, υπολόγισε τις ημέρες
                    LocalDate startDate = apostoli.getImerominiaParalabis();
                    LocalDate endDate = LocalDate.now();

                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysBetween >= 0) {
                        diakinisiDays = String.valueOf(daysBetween);
                    }
                }
            }

            Object[] rowData = {
                    (Object) rowNumber++,
                    apostoli.getCourier() != null ? apostoli.getCourier() : "-",
                    apostoli.getArithmosApostolis() != null ? apostoli.getArithmosApostolis() : "-",
                    dateStr,
                    antikatavolosStr,
                    senderName,
                    apostoli.getParaliptis() != null ? apostoli.getParaliptis() : "-",
                    apostoli.getPoli() != null ? apostoli.getPoli() : "-",
                    statheroPhone,
                    phone,
                    ("TICK_IMAGE".equals(diakinisiDays)) ? Integer.MAX_VALUE :
                            (diakinisiDays.toString().isEmpty() ? -1 : Integer.parseInt(diakinisiDays.toString())),
                    apostoli.getStatusApostolis() != null ? apostoli.getStatusApostolis() : "-",
                    apostoli.getStatusMypms() != null ? apostoli.getStatusMypms() : "-"
            };
            addRow(rowData);
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
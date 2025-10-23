package org.pms.shipments.ui.dialogs;


import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.pms.shipments.model.Apostoli;
import org.pms.customers.model.Pelatis;
import org.pms.customers.dao.PelatisDAO;
import org.pms.shipments.model.TrackingDetail;
import org.pms.shipments.service.ACSTrackingDetailsService;
import org.pms.shipments.service.TrackingStatusFilter;

import static org.pms.utils.ValidationUtils.isNumeric;

public class ApostoliTableModel extends DefaultTableModel {

    private final String[] columnNames = {
            "A/A", "Courier", "Αρ. Αποστολής", "Ημερομηνία Παραλαβής", "Αντικαταβολή",
            "Αποστολέας", "Παραλήπτης", "Πόλη", "Τηλέφωνο", "Κινητό", "Σε διακίνηση",
            "Status Courier", "Status Details", "Status MyPMS"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PelatisDAO pelatisDAO;
    private final ACSTrackingDetailsService trackingService;

    public ApostoliTableModel(PelatisDAO pelatisDAO) {
        super();
        this.pelatisDAO = pelatisDAO;
        this.trackingService = new ACSTrackingDetailsService();
        setColumnIdentifiers(columnNames);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        if (columnIndex == 10) { // Στήλη "Σε διακίνηση"
            return Object.class; // Αλλάζουμε σε Object για να δεχτεί και strings
        }
        return String.class;
    }

    public void loadData(List<Apostoli> apostolesList, int startingRowNumber) {
        setRowCount(0);

        int rowNumber = startingRowNumber;
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
            if (apostoli.getImerominiaParalabis() != null)
                dateStr = apostoli.getImerominiaParalabis().format(dateFormatter);


            String statheroPhone = "";
            if (apostoli.getTilefonoStathero() != null && !apostoli.getTilefonoStathero().trim().isEmpty()) {
                statheroPhone = apostoli.getTilefonoStathero();
            }

            String phone = "";
            if (apostoli.getTilefonoKinito() != null && !apostoli.getTilefonoKinito().trim().isEmpty()) {
                phone = apostoli.getTilefonoKinito();
            }

            String statusDetails = apostoli.getStatusDetails();
            if (statusDetails == null || statusDetails.trim().isEmpty()) {
                statusDetails = "-";
            }

            if (statusDetails.startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
                try {
                    List<TrackingDetail> trackingDetails = trackingService.getTrackingDetails(apostoli.getArithmosApostolis());
                    String nonDeliveryNote = TrackingStatusFilter.getNonDeliveryNote(trackingDetails);
                    if (nonDeliveryNote != null && !nonDeliveryNote.trim().isEmpty()) {
                        statusDetails = nonDeliveryNote;
                    }
                } catch (Exception e) {
                    // Κρατάμε το status details όπως είναι
                }
            }

            // Υπολογισμός ημερών διακίνησης βάσει ημερομηνίας αναχώρησης
            Object diakinisiDays = "";

            boolean hasTrackingData = trackingService.hasTrackingDetails(apostoli.getArithmosApostolis());

            if (hasTrackingData) {
                boolean hasNotFoundDetail = trackingService.hasNotFoundTrackingDetail(apostoli.getArithmosApostolis());

                if (hasNotFoundDetail) {
                    diakinisiDays = "ΑΠΟΣΤΟΛΗ ΔΕΝ ΒΡΕΘΗΚΕ";
                } else if ("ΕΠΙΣΤΡΑΦΗΚΕ".equals(statusDetails)) {
                    diakinisiDays = "TIK_IMG_PURPLE";
                }
                else if ("ΠΡΟΣ ΕΠΙΣΤΟΦΗ".equals(statusDetails)) {
                    diakinisiDays = "RETURN_IMAGE";
                }
                else {
                    LocalDate departureDate = trackingService.getDepartureDate(apostoli.getArithmosApostolis());

                    if (departureDate != null) {
                        if ("ΠΑΡΑΔΟΘΗΚΕ".equals(statusDetails)) {
                            diakinisiDays = "TICK_IMAGE";
                        } else {
                            LocalDate endDate = LocalDate.now();
                            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(departureDate, endDate);
                            if (daysBetween >= 0) {
                                diakinisiDays = String.valueOf(daysBetween);
                            }
                        }
                    } else {
                        diakinisiDays = "!";
                        statusDetails = "ΑΔΙΑΚΙΝΗΤΟ";
                    }
                }
            }
            // Αν δεν έχει γίνει συγχρονισμός, το diakinisiDays μένει κενό ("")
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
                    ("TICK_IMAGE".equals(diakinisiDays)) ? diakinisiDays :
                            ("TIK_IMG_PURPLE".equals(diakinisiDays)) ? diakinisiDays :
                                    ("RETURN_IMAGE".equals(diakinisiDays)) ? diakinisiDays :
                                            ("ΑΠΟΣΤΟΛΗ ΔΕΝ ΒΡΕΘΗΚΕ".equals(diakinisiDays.toString())) ? "X_IMAGE" :
                                                    ("!".equals(diakinisiDays.toString())) ? "EXCL_IMAGE" :
                                                            (diakinisiDays.toString().isEmpty()) ? "" :
                                                                    (isNumeric(diakinisiDays.toString()) ? Integer.parseInt(diakinisiDays.toString()) : ""),
                    apostoli.getStatusApostolis() != null ? apostoli.getStatusApostolis() : "-",
                    statusDetails,
                    apostoli.getStatusMypms() != null ? apostoli.getStatusMypms() : "-"
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

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Καμία στήλη δεν είναι editable
    }
}
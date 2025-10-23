package org.pms.firebase;

import com.google.cloud.firestore.*;
import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.CustomerCareDAO;
import org.pms.shipments.dao.TrackingDetailDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.CustomerCareComment;
import org.pms.shipments.model.TrackingDetail;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FirebaseSyncService {

    private final Firestore firestore;
    private final ApostoliDAO apostoliDAO;
    private final PelatisDAO pelatisDAO;
    private final CustomerCareDAO customerCareDAO;

    public FirebaseSyncService() {
        this.firestore = FirebaseConfig.getInstance().getFirestore();
        this.apostoliDAO = new ApostoliDAO();
        this.pelatisDAO = new PelatisDAO();
        this.customerCareDAO = new CustomerCareDAO();
    }

    /**
     * Συγχρονισμός όλων των αποστολών του PELION
     */
    public SyncResult syncPelionShipments() {
        System.out.println("=".repeat(50));
        System.out.println("Έναρξη συγχρονισμού PELION - " + new Date());
        System.out.println("=".repeat(50));

        try {
            Pelatis pelion = findPelionCustomer();
            if (pelion == null) {
                return new SyncResult(false, 0, 0, "Δεν βρέθηκε πελάτης PELION");
            }

            // ΒΕΛΤΙΩΣΗ: Παίρνουμε ΜΟΝΟ τις μη συγχρονισμένες αποστολές
            List<Apostoli> unsyncedApostoles = apostoliDAO.findUnsyncedApostoli()
                    .stream()
                    .filter(a -> pelion.getKodikosPelati().equals(a.getKodikosPelati()))
                    .collect(java.util.stream.Collectors.toList());

            System.out.println("Βρέθηκαν " + unsyncedApostoles.size() + " μη συγχρονισμένες αποστολές PELION");

            if (unsyncedApostoles.isEmpty()) {
                return new SyncResult(true, 0, 0, "Όλες οι αποστολές είναι ήδη συγχρονισμένες");
            }

            WriteBatch batch = firestore.batch();
            int synced = 0;
            int errors = 0;
            int batchCount = 0;

            for (Apostoli apostoli : unsyncedApostoles) {
                try {
                    Map<String, Object> shipmentData = convertApostoliToFirebaseDocument(apostoli);
                    DocumentReference docRef = firestore.collection("shipments")
                            .document(apostoli.getArithmosApostolis());

                    batch.set(docRef, shipmentData, SetOptions.merge());
                    batchCount++;

                    if (batchCount >= 500) {
                        batch.commit().get();

                        // Μαρκάρισμα ως synced
                        for (int i = synced; i < synced + batchCount; i++) {
                            apostoliDAO.markAsSynced(unsyncedApostoles.get(i).getIdApostolis());
                        }

                        synced += batchCount;
                        System.out.println("  → Συγχρονίστηκαν " + synced + "/" + unsyncedApostoles.size());
                        batch = firestore.batch();
                        batchCount = 0;
                    }

                } catch (Exception e) {
                    errors++;
                    System.err.println("Σφάλμα στην αποστολή " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
                }
            }

            if (batchCount > 0) {
                batch.commit().get();

                // Μαρκάρισμα των τελευταίων ως synced
                for (int i = synced; i < synced + batchCount; i++) {
                    apostoliDAO.markAsSynced(unsyncedApostoles.get(i).getIdApostolis());
                }

                synced += batchCount;
            }

            System.out.println("=".repeat(50));
            System.out.println("Ολοκληρώθηκε συγχρονισμός!");
            System.out.println("  ✓ Επιτυχείς: " + synced);
            System.out.println("  ✗ Σφάλματα: " + errors);
            System.out.println("=".repeat(50));

            return new SyncResult(true, synced, errors, "Επιτυχής συγχρονισμός");

        } catch (Exception e) {
            System.err.println("Κρίσιμο σφάλμα: " + e.getMessage());
            e.printStackTrace();
            return new SyncResult(false, 0, 0, "Σφάλμα: " + e.getMessage());
        }
    }

    /**
     * Μετατροπή Apostoli σε Firebase Document
     */
    private Map<String, Object> convertApostoliToFirebaseDocument(Apostoli apostoli) {
        Map<String, Object> data = new HashMap<>();

        // Βασικές πληροφορίες
        data.put("trackingNumber", apostoli.getArithmosApostolis());
        data.put("courier", apostoli.getCourier() != null ? apostoli.getCourier() : "ACS");
        data.put("orderNumber", apostoli.getArithmosParaggelias() != null ? apostoli.getArithmosParaggelias() : "");
        data.put("receiptDate", apostoli.getImerominiaParalabis() != null ? apostoli.getImerominiaParalabis().toString() : null);
        data.put("issueDate", apostoli.getImerominiaEkdosis() != null ? apostoli.getImerominiaEkdosis().toString() : null);
        data.put("departureDate", apostoli.getImerominiaAnaxorisis() != null ? apostoli.getImerominiaAnaxorisis().toString() : null);
        data.put("cashOnDelivery", apostoli.getAntikatavoli() != null ? apostoli.getAntikatavoli().doubleValue() : 0.0);

        // Αποστολέας
        Pelatis pelatis = pelatisDAO.findById(apostoli.getKodikosPelati());
        data.put("sender", pelatis != null ? pelatis.getEponymiaEtairias() : "PELION");

        // Παραλήπτης
        data.put("recipient", apostoli.getParaliptis() != null ? apostoli.getParaliptis() : "");
        data.put("city", apostoli.getPoli() != null ? apostoli.getPoli() : "");
        data.put("address", apostoli.getDiefthinsi() != null ? apostoli.getDiefthinsi() : "");
        data.put("postalCode", apostoli.getTkParalipti() != null ? apostoli.getTkParalipti() : "");
        data.put("country", apostoli.getXora() != null ? apostoli.getXora() : "Ελλάδα");
        data.put("landline", apostoli.getTilefonoStathero() != null ? apostoli.getTilefonoStathero() : "");
        data.put("mobile", apostoli.getTilefonoKinito() != null ? apostoli.getTilefonoKinito() : "");

        // Υπολογισμός ημερών σε διακίνηση
        int daysInTransit = 0;
        if (apostoli.getImerominiaAnaxorisis() != null) {
            LocalDate today = LocalDate.now();
            daysInTransit = (int) ChronoUnit.DAYS.between(apostoli.getImerominiaAnaxorisis(), today);
        }
        data.put("daysInTransit", daysInTransit);

        String courierStatus = "-";
        try {
            TrackingDetailDAO trackingDetailDAO = new TrackingDetailDAO();
            List<TrackingDetail> trackingDetails = trackingDetailDAO.findByArithmosApostolis(apostoli.getArithmosApostolis());
            String filteredStatus = org.pms.shipments.service.TrackingStatusFilter.getLatestFilteredStatus(trackingDetails);

            if (filteredStatus != null && !filteredStatus.trim().isEmpty()) {
                if (filteredStatus.startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
                    String nonDeliveryNote = org.pms.shipments.service.TrackingStatusFilter.getNonDeliveryNote(trackingDetails);
                    courierStatus = nonDeliveryNote != null ? nonDeliveryNote : filteredStatus;
                } else {
                    courierStatus = filteredStatus;
                }
            }
        } catch (Exception e) {
            System.err.println("Σφάλμα ανάκτησης filtered status για " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
        }
        data.put("courierStatus", courierStatus);
        data.put("myPMSStatus", apostoli.getStatusMypms() != null ? apostoli.getStatusMypms() : "-");
        data.put("comments", apostoli.getSxolia() != null ? apostoli.getSxolia() : "");
        data.put("history", apostoli.getIstoriko() != null ? apostoli.getIstoriko() : "");
        data.put("statusLocked", apostoli.getStatusLocked() != null ? apostoli.getStatusLocked() : false);

        // Customer Care Comments
        List<Map<String, Object>> comments = getCustomerCareComments(apostoli.getIdApostolis());
        if (!comments.isEmpty()) {
            data.put("customerCareComments", comments);
        }

        // Timestamps
        data.put("syncedAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        return data;
    }

    /**
     * Ανάκτηση customer care comments
     */
    private List<Map<String, Object>> getCustomerCareComments(int idApostolis) {
        List<Map<String, Object>> comments = new ArrayList<>();
        List<CustomerCareComment> dbComments = customerCareDAO.findByApostoli(idApostolis);

        for (CustomerCareComment comment : dbComments) {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("id", comment.getId());
            commentData.put("comment", comment.getSxolio());
            commentData.put("createdAt", comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
            comments.add(commentData);
        }

        return comments;
    }

    /**
     * Εύρεση πελάτη PELION
     */
    private Pelatis findPelionCustomer() {
        List<Pelatis> allCustomers = pelatisDAO.findAll();
        for (Pelatis pelatis : allCustomers) {
            if (pelatis.getEponymiaEtairias() != null &&
                    pelatis.getEponymiaEtairias().toUpperCase().contains("PELION")) {
                return pelatis;
            }
        }
        return null;
    }


    /**
     * Κλάση αποτελέσματος συγχρονισμού
     */
    public static class SyncResult {
        public final boolean success;
        public final int synced;
        public final int errors;
        public final String message;

        public SyncResult(boolean success, int synced, int errors, String message) {
            this.success = success;
            this.synced = synced;
            this.errors = errors;
            this.message = message;
        }
    }
}
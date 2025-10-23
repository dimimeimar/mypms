package org.pms.shipments.service;

import org.pms.shipments.model.TrackingDetail;

import java.util.*;

public class TrackingStatusFilter {

    // Τιμές που θα αγνοούνται εντελώς
    private static final Set<String> IGNORED_STATUSES = new HashSet<>(Arrays.asList(
            "ΑΠΟΣΤΟΛΗ SMS",
            "ΑΠΟΣΤΟΛΗ EMAIL ΣΤΟΝ ΠΑΡΑΛΗΠΤΗ"
    ));

    // Mapping για μετατροπή τιμών
    private static final Map<String, String> STATUS_MAPPING = new HashMap<>();

    /**
     * Φιλτράρει και μετατρέπει status από tracking details
     */
    public static String filterAndMapStatus(String originalStatus) {
        if (originalStatus == null || originalStatus.trim().isEmpty()) {
            return null;
        }

        String trimmedStatus = originalStatus.trim().toUpperCase();

        // Αν είναι στη λίστα των αγνοημένων, επιστρέφουμε null
        if (IGNORED_STATUSES.contains(trimmedStatus)) {
            return null;
        }

        // Ειδικός έλεγχος για ΠΑΡΑΔΟΣΗ που ξεκινάει με αυτή τη λέξη
        // αλλά ΔΕΝ ξεκινάει με "ΜΗ ΠΑΡΑΔΟΣΗ"
        if (trimmedStatus.startsWith("ΠΑΡΑΔΟΣΗ") && !trimmedStatus.startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
            return "ΠΑΡΑΔΟΘΗΚΕ";
        }

        if (trimmedStatus.equals("ΑΚΥΡΩΘΗΚΕ"))
            return "ΑΚΥΡΩΘΗΚΕ";

        if (trimmedStatus.equals("ΕΠΙΣΤΡΟΦΗ"))
            return "ΠΡΟΣ ΕΠΙΣΤΟΦΗ";

        if (trimmedStatus.equals("ΕΠΙΣΤΡΟΦΗ-ΠΑΡΑΔΟΣΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ"))
            return "ΕΠΙΣΤΡΑΦΗΚΕ";

        // Ελέγχουμε αν υπάρχει άλλο mapping (κρατάμε την παλιά λογική για άλλες περιπτώσεις)
        for (Map.Entry<String, String> entry : STATUS_MAPPING.entrySet()) {
            if (trimmedStatus.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Αν δεν βρέθηκε mapping, επιστρέφουμε την αρχική τιμή
        return trimmedStatus;
    }

    public static String getNonDeliveryNote(List<TrackingDetail> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }

        for (TrackingDetail detail : details) {
            String action = detail.getCheckpointAction();
            if (action != null && action.trim().toUpperCase().startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
                String notes = detail.getCheckpointNotes();
                if (notes != null && !notes.trim().isEmpty()) {
                    return notes.trim();
                }
            }
        }

        return null;
    }

    /**
     * Προσθέτει νέο status για αγνόηση
     */
    public static void addIgnoredStatus(String status) {
        IGNORED_STATUSES.add(status.toUpperCase());
    }

    /**
     * Προσθέτει νέο mapping
     */
    public static void addStatusMapping(String from, String to) {
        STATUS_MAPPING.put(from.toUpperCase(), to.toUpperCase());
    }

    /**
     * Παίρνει την τελευταία έγκυρη κατάσταση από tracking details
     */
    public static String getLatestFilteredStatus(List<TrackingDetail> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }

        // Έλεγχος αν υπάρχει ΕΠΙΣΤΡΟΦΗ
        boolean hasReturn = false;
        boolean hasReturnDelivery = false;

        for (TrackingDetail detail : details) {
            String action = detail.getCheckpointAction();
            if (action != null) {
                if (action.trim().toUpperCase().equals("ΕΠΙΣΤΡΟΦΗ")) {
                    hasReturn = true;
                }
                if (action.trim().toUpperCase().equals("ΕΠΙΣΤΡΟΦΗ-ΠΑΡΑΔΟΣΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ")) {
                    hasReturnDelivery = true;
                    break;
                }
            }
        }

        if (hasReturn && !hasReturnDelivery) {
            return "ΠΡΟΣ ΕΠΙΣΤΟΦΗ";
        }

        if (hasReturnDelivery) {
            return "ΕΠΙΣΤΡΑΦΗΚΕ";
        }


        // Έλεγχος αν υπάρχει ΠΑΡΑΔΟΣΗ
        boolean hasDelivery = false;
        for (TrackingDetail detail : details) {
            String action = detail.getCheckpointAction();
            if (action != null) {
                String trimmed = action.trim().toUpperCase();
                if (trimmed.startsWith("ΠΑΡΑΔΟΣΗ") && !trimmed.startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
                    hasDelivery = true;
                    break;
                }
            }
        }

        if (hasDelivery) {
            return "ΠΑΡΑΔΟΘΗΚΕ";
        }

        // Έλεγχος για ΑΝΑΧΩΡΗΣΗ ΑΠΌ ΚΕΝΤΡΟ ΔΙΑΛΟΓΗΣ
        String potentialStatus = null;
        boolean hasDeparture = false;

        for (TrackingDetail detail : details) {
            String filteredStatus = filterAndMapStatus(detail.getCheckpointAction());
            if (filteredStatus != null) {
                if (potentialStatus == null) {
                    potentialStatus = filteredStatus;
                }
                if (filteredStatus.contains("ΑΝΑΧΩΡΗΣΗ") && filteredStatus.contains("ΚΕΝΤΡΟ ΔΙΑΛΟΓΗΣ")) {
                    hasDeparture = true;
                }
            }
        }

        if (potentialStatus != null && potentialStatus.equals("ΚΕΝΤΡΟ ΔΙΑΛΟΓΗΣ") && hasDeparture) {
            return "ΑΝΑΧΩΡΗΣΗ ΑΠΌ ΚΕΝΤΡΟ ΔΙΑΛΟΓΗΣ";
        }

        // Κανονική λογική για τα υπόλοιπα
        for (TrackingDetail detail : details) {
            String filteredStatus = filterAndMapStatus(detail.getCheckpointAction());
            if (filteredStatus != null) {
                return filteredStatus;
            }
        }

        return null;
    }
}
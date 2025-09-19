package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.ApostoliDAO;
import org.example.model.Apostoli;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ACSTrackingService {

    private static final String API_URL = "https://webservices.acscourier.net/ACSRestServices/api/ACSAutoRest";
    private static final String API_KEY = "46bba3531d8a4f33a571ad2a5e916f05";
    private static final String COMPANY_ID = "802437324_acs";
    private static final String COMPANY_PASSWORD = "58m11fe3";
    private static final String USER_ID = "apiparcel";
    private static final String USER_PASSWORD = "qqs9kjc223";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApostoliDAO apostoliDAO;
    private ScheduledExecutorService scheduler;
    private List<Apostoli> pendingUpdates;
    private int currentIndex = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;

    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public ACSTrackingService(ApostoliDAO apostoliDAO) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apostoliDAO = apostoliDAO;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startAutomaticUpdates() {
        if (isRunning) {
            System.out.println("Το σύστημα αυτόματης ενημέρωσης ήδη εκτελείται");
            return;
        }

        // Δημιουργία νέου scheduler αν χρειάζεται
        if (scheduler.isShutdown()) {
            // Δημιούργησε νέο scheduler
            this.scheduler = Executors.newScheduledThreadPool(1);
        }

        isRunning = true;
        isPaused = false;
        System.out.println("Εκκίνηση αυτόματης ενημέρωσης ACS tracking...");

        loadPendingShipments();

        scheduler.scheduleAtFixedRate(this::processNextShipment, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void pauseAutomaticUpdates() {
        if (!isRunning) {
            System.out.println("Το σύστημα δεν εκτελείται");
            return;
        }

        isPaused = true;
        System.out.println("ΠΑΥΣΗ αυτόματης ενημέρωσης ACS tracking...");
    }

    public void resumeAutomaticUpdates() {
        if (!isRunning) {
            System.out.println("Το σύστημα δεν εκτελείται");
            return;
        }

        isPaused = false;
        System.out.println("ΣΥΝΕΧΙΣΗ αυτόματης ενημέρωσης ACS tracking...");
    }

    public void stopAutomaticUpdates() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        isPaused = false;
        System.out.println("Διακοπή αυτόματης ενημέρωσης ACS tracking...");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void loadPendingShipments() {
        System.out.println("\n=== ΦΟΡΤΩΣΗ ΑΠΟΣΤΟΛΩΝ ===");
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        System.out.println("Αναζήτηση ACS αποστολών από: " + thirtyDaysAgo);

        pendingUpdates = apostoliDAO.findACSShipmentsForTracking();
        System.out.println("Βρέθηκαν " + pendingUpdates.size() + " ACS αποστολές στη βάση");

        // Debug: Εμφάνιση λίστας αποστολών
        for (Apostoli apostoli : pendingUpdates) {
            System.out.println("- Αποστολή: " + apostoli.getArithmosApostolis() +
                    " | Status: " + apostoli.getStatusApostolis() +
                    " | Ημ/νία: " + apostoli.getImerominiaParalabis());
        }

        pendingUpdates.removeIf(apostoli ->
                apostoli.getImerominiaParalabis() != null &&
                        apostoli.getImerominiaParalabis().isBefore(thirtyDaysAgo)
        );

        System.out.println("Μετά το φιλτράρισμα (τελευταίες 30 ημέρες): " + pendingUpdates.size() + " αποστολές");

        pendingUpdates.sort((a, b) -> {
            if (a.getImerominiaParalabis() == null && b.getImerominiaParalabis() == null) {
                return 0;
            }
            if (a.getImerominiaParalabis() == null) {
                return 1;
            }
            if (b.getImerominiaParalabis() == null) {
                return -1;
            }
            return b.getImerominiaParalabis().compareTo(a.getImerominiaParalabis());
        });

        currentIndex = 0;
        System.out.println("Φορτώθηκαν " + pendingUpdates.size() + " ACS αποστολές για ενημέρωση");
        System.out.println("===========================\n");
    }

    private void processNextShipment() {
        if (!isRunning || pendingUpdates == null || pendingUpdates.isEmpty() || isPaused) {
            if (isPaused) {
                System.out.println("ΠΑΥΣΗ - Αναμένω συνέχιση...");
            }
            return;
        }

        if (currentIndex >= pendingUpdates.size()) {
            System.out.println("Ολοκληρώθηκε ο κύκλος ενημέρωσης. Επαναφόρτωση λίστας...");
            // Callback για ανανέωση πίνακα
            if (refreshCallback != null) {
                SwingUtilities.invokeLater(refreshCallback);
            }
            loadPendingShipments();

            return;
        }

        Apostoli apostoli = pendingUpdates.get(currentIndex);
        currentIndex++;

        System.out.println("\n--- ΕΠΕΞΕΡΓΑΣΙΑ ΑΠΟΣΤΟΛΗΣ " + currentIndex + "/" + pendingUpdates.size() + " ---");
        System.out.println("Αποστολή: " + apostoli.getArithmosApostolis());
        System.out.println("Τρέχον Status: " + apostoli.getStatusApostolis());

        try {
            updateShipmentTracking(apostoli);
        } catch (Exception e) {
            System.err.println("ΣΦΑΛΜΑ ενημέρωσης αποστολής " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateShipmentTracking(Apostoli apostoli) throws IOException, InterruptedException {
        System.out.println("Δημιουργία API request για: " + apostoli.getArithmosApostolis());
        String requestBody = createRequestBody(apostoli.getArithmosApostolis());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("AcsApiKey", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("Αποστολή API request...");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Status: " + response.statusCode());
        if (response.statusCode() == 200) {
            System.out.println("Response Body: " + response.body().substring(0, Math.min(200, response.body().length())) + "...");
            processTrackingResponse(apostoli, response.body());
        } else {
            System.err.println("API Error για " + apostoli.getArithmosApostolis() + ": " + response.statusCode());
            System.err.println("Response Body: " + response.body());
        }
    }

    private String createRequestBody(String voucherNo) throws IOException {
        var requestData = objectMapper.createObjectNode();
        requestData.put("ACSAlias", "ACS_Trackingsummary");

        var inputParams = objectMapper.createObjectNode();
        inputParams.put("Company_ID", COMPANY_ID);
        inputParams.put("Company_Password", COMPANY_PASSWORD);
        inputParams.put("User_ID", USER_ID);
        inputParams.put("User_Password", USER_PASSWORD);
        inputParams.putNull("Language");
        inputParams.put("Voucher_No", voucherNo);

        requestData.set("ACSInputParameters", inputParams);

        return objectMapper.writeValueAsString(requestData);
    }

    private void processTrackingResponse(Apostoli apostoli, String responseBody) {
        try {
            System.out.println("\n=== ΕΠΕΞΕΡΓΑΣΙΑ RESPONSE ===");
            JsonNode response = objectMapper.readTree(responseBody);

            if (response.get("ACSExecution_HasError").asBoolean()) {
                System.err.println("ACS API Error για " + apostoli.getArithmosApostolis() + ": " +
                        response.get("ACSExecutionErrorMessage").asText());
                return;
            }

            JsonNode tableData = response.path("ACSOutputResponce")
                    .path("ACSTableOutput")
                    .path("Table_Data");

            if (tableData.isArray() && tableData.size() > 0) {
                JsonNode shipmentData = tableData.get(0);

                Integer deliveryFlag = shipmentData.get("delivery_flag").asInt();
                Integer returnedFlag = shipmentData.get("returned_flag").asInt();
                String nonDeliveryReasonCode = shipmentData.get("non_delivery_reason_code").asText();
                Integer shipmentStatus = shipmentData.get("shipment_status").asInt();
                String deliveryInfo = shipmentData.get("delivery_info").asText();

                // ΝΕΑ ΓΡΑΜΜΗ - Παίρνουμε την πόλη παραλήπτη από το API
                String acsStationDestination = shipmentData.path("acs_station_destination_descr").asText();

                System.out.println("API Response Data:");
                System.out.println("- delivery_flag: " + deliveryFlag);
                System.out.println("- returned_flag: " + returnedFlag);
                System.out.println("- non_delivery_reason_code: " + nonDeliveryReasonCode);
                System.out.println("- shipment_status: " + shipmentStatus);
                System.out.println("- delivery_info: " + deliveryInfo);
                System.out.println("- acs_station_destination_descr: " + acsStationDestination); // ΝΕΑ ΓΡΑΜΜΗ

                String oldStatus = apostoli.getStatusApostolis();
                String newStatus = mapStatusToGreek(shipmentStatus, nonDeliveryReasonCode, deliveryFlag, returnedFlag);

                System.out.println("Status Mapping:");
                System.out.println("- Παλιό Status: " + oldStatus);
                System.out.println("- Νέο Status: " + newStatus);

                if (!newStatus.equals(oldStatus) || !acsStationDestination.isEmpty()) { // ΤΡΟΠΟΠΟΙΗΣΗ ΣΥΝΘΗΚΗΣ
                    System.out.println(">>> ΑΛΛΑΓΗ STATUS ή ΕΝΗΜΕΡΩΣΗ ΠΟΛΗΣ!");
                    System.out.println("Ενημέρωση βάσης...");

                    boolean updated = apostoliDAO.updateTrackingData(
                            apostoli.getArithmosApostolis(),
                            deliveryFlag,
                            returnedFlag,
                            nonDeliveryReasonCode,
                            shipmentStatus,
                            deliveryInfo,
                            newStatus,
                            acsStationDestination // ΝΕΑ ΠΑΡΑΜΕΤΡΟΣ
                    );

                    if (updated) {
                        System.out.println("✅ ΕΠΙΤΥΧΗΣ ΕΝΗΜΕΡΩΣΗ: " + apostoli.getArithmosApostolis() +
                                " -> " + oldStatus + " => " + newStatus);
                        if (!acsStationDestination.isEmpty()) {
                            System.out.println("   Πόλη παραλήπτη: " + acsStationDestination);
                        }

                        if (shipmentStatus == 4 || shipmentStatus == 6 || shipmentStatus == 7) {
                            System.out.println("Αφαίρεση από pending list (τελική κατάσταση)");
                            pendingUpdates.remove(apostoli);
                            currentIndex--;
                        }
                    } else {
                        System.err.println("❌ ΑΠΟΤΥΧΙΑ ΕΝΗΜΕΡΩΣΗΣ στη βάση για: " + apostoli.getArithmosApostolis());
                    }
                } else {
                    System.out.println("Κανένα νέο στοιχείο - Status παραμένει: " + oldStatus);
                }
            } else {
                System.out.println("Δεν βρέθηκαν δεδομένα tracking για: " + apostoli.getArithmosApostolis());
            }
            System.out.println("=============================\n");

        } catch (Exception e) {
            System.err.println("Σφάλμα επεξεργασίας response για " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String mapStatusToGreek(Integer shipmentStatus, String nonDeliveryReasonCode,
                                   Integer deliveryFlag, Integer returnedFlag) {

        // Παραδόθηκε
        if (shipmentStatus == 4 && deliveryFlag == 1) {
            return "ΠΑΡΑΔΟΘΗΚΕ";
        }

        // Επιστροφή
        if (shipmentStatus == 6) {
            return "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ";
        }

        if (shipmentStatus == 7 && returnedFlag == 1) {
            return "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ";
        }

        // Κωδικοί μη παράδοσης από ACS
        if (nonDeliveryReasonCode != null) {
            switch (nonDeliveryReasonCode) {
                // ΑΔ codes - shipment_status 5
                case "ΑΔ1":
                    return "ΕΝΤΟΛΗ ΠΑΡΑΛΑΒΗΣ ΑΠΟ ΓΡΑΦΕΙΟ";
                case "ΑΔ3":
                    return "ΒΡΙΣΚΕΤΑΙ ΣΤΗΝ ΔΙΑΔΡΟΜΗ ΠΡΟΣ ΤΟ ΚΑΤΑΣΤΗΜΑ ΠΑΡΑΔΟΣΗΣ";
                case "ΑΔ8":
                    return "ΠΑΡΑΔΟΣΗ RECEPTION ΕΝΤ.ΑΠΟΣΤ";

                // ΑΠ codes - shipment_status 1
                case "ΑΠ1":
                    return "ΑΡΝΗΣΗ ΧΡΕΩΣΗΣ";
                case "ΑΠ2":
                    return "ΑΔΥΝΑΜΙΑ ΠΛΗΡΩΜΗΣ";
                case "ΑΠ3":
                    return "ΜΗ ΑΠΟΔΟΧΗ ΑΠΟΣΤΟΛΗΣ";
                case "ΑΠ4":
                    return "ΑΠΕΒΙΩΣΕ";

                // ΑΣ codes - shipment_status 3
                case "ΑΣ1":
                    return "ΑΠΩΝ";

                // ΔΠ codes - shipment_status 5
                case "ΔΠ1":
                    return "ΔΥΣΠΡΟΣΙΤΗ ΠΕΡΙΟΧΗ";

                // ΕΔ codes - shipment_status 5
                case "ΕΔ1":
                    return "ΕΛΛΙΠΗ ΔΙΚΑΙΟΛΟΓΗΤΙΚΑ";

                // ΛΣ codes - shipment_status 2
                case "ΛΣ1":
                    return "ΑΓΝΩΣΤΟΣ ΠΑΡΑΛΗΠΤΗΣ";
                case "ΛΣ2":
                    return "ΑΛΛΑΓΗ ΔΙΕΥΘΥΝΣΗΣ";
                case "ΛΣ3":
                    return "ΛΑΝΘΑΣΜΕΝΗ, ΕΛΛΙΠΗΣ ΔΙΕΥΘΥΝΣΗ";

                // ΠΑ codes - shipment_status 5
                case "ΠΑ1":
                    return "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛΗ ΑΠΟΣΤ";
                case "ΠΑ2":
                    return "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛ ΠΑΡ/ΠΤΗ";
                case "ΠΑ4":
                    return "ΣΥΓΚΕΚΡΙΜΕΝΗ ΗΜ/ΝΙΑ ΠΑΡΑΔΟΣΗΣ ΕΝΤΟΛΗ ΠΑΡΑΛΗΠΤΗ ΑΝΑΚΑΤΕΥΘΥΝΣΗ";

                default:
                    return " ";
            }
        }

        return "ΑΓΝΩΣΤΟ STATUS";
    }

    public int getPendingShipmentsCount() {
        return pendingUpdates != null ? pendingUpdates.size() - currentIndex : 0;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getCurrentStatus() {
        if (!isRunning) {
            return "Σταματημένο";
        }

        int remaining = getPendingShipmentsCount();
        return "Εκτελείται - Υπόλοιπες αποστολές: " + remaining;
    }
}
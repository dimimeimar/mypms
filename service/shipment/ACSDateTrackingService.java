package org.example.service.shipment;

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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ACSDateTrackingService {

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

    public ACSDateTrackingService(ApostoliDAO apostoliDAO) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apostoliDAO = apostoliDAO;
    }

    public void startAutomaticUpdates() {
        if (isRunning) {
            return;
        }

        System.out.println("Εκκίνηση αυτόματης ενημέρωσης ημερομηνιών έκδοσης ACS...");
        isRunning = true;
        isPaused = false;

        loadPendingShipments();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::processNextShipment, 0, 250, TimeUnit.MILLISECONDS);
    }

    public void pauseAutomaticUpdates() {
        isPaused = !isPaused;
        System.out.println(isPaused ? "ΠΑΥΣΗ ενημέρωσης ημερομηνιών..." : "ΣΥΝΕΧΙΣΗ ενημέρωσης ημερομηνιών...");
    }

    public void stopAutomaticUpdates() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        isPaused = false;
        System.out.println("Διακοπή αυτόματης ενημέρωσης ημερομηνιών έκδοσης ACS...");

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
        System.out.println("\n=== ΦΌΡΤΩΣΗ ΑΠΟΣΤΟΛΩΝ ΓΙΑ ΗΜΕΡΟΜΗΝΙΕΣ ===");
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        System.out.println("Αναζήτηση ACS αποστολών από: " + thirtyDaysAgo);

        pendingUpdates = apostoliDAO.findACSDateShipmentsForTracking();
        System.out.println("Βρέθηκαν " + pendingUpdates.size() + " ACS αποστολές στη βάση");

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
        System.out.println("Φορτώθηκαν " + pendingUpdates.size() + " ACS αποστολές για ενημέρωση ημερομηνιών");
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
            System.out.println("Ολοκληρώθηκε ο κύκλος ενημέρωσης ημερομηνιών.");
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
        System.out.println("Τρέχουσα ημερομηνία έκδοσης: " + apostoli.getImerominiaEkdosis());

        try {
            updateShipmentDate(apostoli);
        } catch (Exception e) {
            System.err.println("ΣΦΑΛΜΑ ενημέρωσης ημερομηνίας αποστολής " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateShipmentDate(Apostoli apostoli) throws IOException, InterruptedException {
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
            processDateResponse(apostoli, response.body());
        } else {
            System.err.println("API Error για " + apostoli.getArithmosApostolis() + ": " + response.statusCode());
            System.err.println("Response Body: " + response.body());
        }
    }

    private String createRequestBody(String voucherNo) throws IOException {
        var requestData = objectMapper.createObjectNode();
        requestData.put("ACSAlias", "ACS_TrackingDetails");

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

    private void processDateResponse(Apostoli apostoli, String responseBody) {
        try {
            System.out.println("\n=== ΕΠΕΞΕΡΓΑΣΙΑ DATE RESPONSE ===");
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
                String checkpointDateTime = shipmentData.path("checkpoint_date_time").asText();

                System.out.println("API Response Data:");
                System.out.println("- checkpoint_date_time: " + checkpointDateTime);

                if (!checkpointDateTime.isEmpty() && !checkpointDateTime.equals("null")) {
                    LocalDate ekdosisDate = parseCheckpointDate(checkpointDateTime);
                    LocalDate currentEkdosisDate = apostoli.getImerominiaEkdosis();

                    System.out.println("Date Mapping:");
                    System.out.println("- Παλιά ημερομηνία έκδοσης: " + currentEkdosisDate);
                    System.out.println("- Νέα ημερομηνία έκδοσης: " + ekdosisDate);

                    if (ekdosisDate != null && !ekdosisDate.equals(currentEkdosisDate)) {
                        System.out.println(">>> ΑΛΛΑΓΗ ΗΜΕΡΟΜΗΝΙΑΣ ΕΚΔΟΣΗΣ!");
                        System.out.println("Ενημέρωση βάσης...");

                        boolean updated = apostoliDAO.updateIssuanceDate(
                                apostoli.getArithmosApostolis(),
                                ekdosisDate
                        );

                        if (updated) {
                            System.out.println("✅ ΕΠΙΤΥΧΗΣ ΕΝΗΜΕΡΩΣΗ: " + apostoli.getArithmosApostolis() +
                                    " -> " + currentEkdosisDate + " => " + ekdosisDate);
                        } else {
                            System.err.println("❌ ΑΠΟΤΥΧΙΑ ΕΝΗΜΕΡΩΣΗΣ στη βάση για: " + apostoli.getArithmosApostolis());
                        }
                    } else {
                        System.out.println("Καμένα νέο στοιχείο - Ημερομηνία παραμένει: " + currentEkdosisDate);
                    }
                } else {
                    System.out.println("Δεν βρέθηκαν δεδομένα ημερομηνίας για: " + apostoli.getArithmosApostolis());
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

    private LocalDate parseCheckpointDate(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty() || dateTimeString.equals("null")) {
            return null;
        }

        try {
            if (dateTimeString.contains("T")) {
                return LocalDate.parse(dateTimeString.substring(0, 10));
            } else if (dateTimeString.length() >= 10) {
                return LocalDate.parse(dateTimeString.substring(0, 10));
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(dateTimeString, formatter);
            }
        } catch (DateTimeParseException e) {
            System.err.println("Σφάλμα parsing ημερομηνίας: " + dateTimeString + " - " + e.getMessage());
            return null;
        }
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
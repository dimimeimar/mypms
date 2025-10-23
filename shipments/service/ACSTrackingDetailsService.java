package org.pms.shipments.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pms.shipments.dao.ACSSummaryDAO;
import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.TrackingDetailDAO;
import org.pms.shipments.model.ACSSummary;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.TrackingDetail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ACSTrackingDetailsService {
    private static final String API_URL = "https://webservices.acscourier.net/ACSRestServices/api/ACSAutoRest";
    private static final String API_KEY = "46bba3531d8a4f33a571ad2a5e916f05";
    private static final String COMPANY_ID = "802437324_acs";
    private static final String COMPANY_PASSWORD = "58m11fe3";
    private static final String USER_ID = "apiparcel";
    private static final String USER_PASSWORD = "qqs9kjc223";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final TrackingDetailDAO trackingDetailDAO;
    private boolean hasStatusChanged = false;

    public ACSTrackingDetailsService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.trackingDetailDAO = new TrackingDetailDAO();
    }

    public boolean fetchAndSaveTrackingDetails(String voucherNo) {
        try {
            hasStatusChanged = false;
            String requestBody = createRequestBody(voucherNo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("AcsApiKey", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return processResponse(voucherNo, response.body());
            } else {
                System.err.println("API Error - Status Code: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Σφάλμα κλήσης API για tracking details: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String createRequestBody(String voucherNo) {
        return String.format("""
            {
                "ACSAlias": "ACS_TrackingDetails",
                "ACSInputParameters": {
                    "Company_ID": "%s",
                    "Company_Password": "%s",
                    "User_ID": "%s",
                    "User_Password": "%s",
                    "Language": null,
                    "Voucher_No": %s
                }
            }
            """, COMPANY_ID, COMPANY_PASSWORD, USER_ID, USER_PASSWORD, voucherNo);
    }

    private boolean processResponse(String voucherNo, String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            if (rootNode.path("ACSExecution_HasError").asBoolean()) {
                String errorMessage = rootNode.path("ACSExecutionErrorMessage").asText();
                System.err.println("ACS API Error: " + errorMessage);
                return false;
            }

            JsonNode tableData = rootNode.path("ACSOutputResponce")
                    .path("ACSTableOutput")
                    .path("Table_Data");

            if (tableData.isArray() && tableData.size() > 0) {
                List<TrackingDetail> trackingDetails = parseTrackingDetails(voucherNo, tableData);
                ApostoliDAO apostoliDAO = new ApostoliDAO();
                Apostoli currentApostoli = apostoliDAO.findByArithmosApostolis(voucherNo);
                boolean shouldSearchForEkdosis = (currentApostoli == null ||
                        currentApostoli.getImerominiaEkdosis() == null ||
                        currentApostoli.getImerominiaEkdosis().equals(LocalDate.of(1900, 1, 1)));

                if (shouldSearchForEkdosis) {
                    System.out.println("🔍 Αναζήτηση ημερομηνίας έκδοσης για " + voucherNo);
                } else {
                    System.out.println("ℹ️ Ημερομηνία έκδοσης υπάρχει ήδη (" + currentApostoli.getImerominiaEkdosis() + ") - skip");
                }

                if (shouldSearchForEkdosis && !trackingDetails.isEmpty()) {
                    LocalDate ekdosisDate = null;
                    String sourceAction = null;

                    for (TrackingDetail detail : trackingDetails) {
                        String action = detail.getCheckpointAction();
                        if (action != null) {
                            String upperAction = action.toUpperCase();

                            if (upperAction.contains("ΕΚΤΥΠΩΣΗ ΕΤΙΚΕΤΑΣ")) {
                                ekdosisDate = detail.getCheckpointDateTime().toLocalDate();
                                sourceAction = action;
                                System.out.println("📄 Βρέθηκε εκτύπωση ετικέτας: " + action + " στις " + ekdosisDate);
                                break;
                            }
                        }
                    }

                    if (ekdosisDate == null) {
                        for (TrackingDetail detail : trackingDetails) {
                            String action = detail.getCheckpointAction();
                            if (action != null) {
                                String upperAction = action.toUpperCase();

                                if (upperAction.contains("ΠΑΡΑΛΑΒΗ") && upperAction.contains("SCAN")) {
                                    ekdosisDate = detail.getCheckpointDateTime().toLocalDate();
                                    sourceAction = action;
                                    System.out.println("📦 Βρέθηκε παραλαβή (scan): " + action + " στις " + ekdosisDate);
                                    break;
                                }
                            }
                        }
                    }

                    if (ekdosisDate == null) {
                        for (TrackingDetail detail : trackingDetails) {
                            String action = detail.getCheckpointAction();
                            if (action != null) {
                                String upperAction = action.toUpperCase();

                                if (upperAction.contains("ΠΑΡΑΛΑΒΗ ΑΠΟ ΑΠΟΣΤΟΛΕΑ")) {
                                    ekdosisDate = detail.getCheckpointDateTime().toLocalDate();
                                    sourceAction = action;
                                    System.out.println("🚚 Βρέθηκε παραλαβή από αποστολέα: " + action + " στις " + ekdosisDate);
                                    break;
                                }
                            }
                        }
                    }

                    if (ekdosisDate != null) {
                        boolean dateUpdated = updateIssuanceDate(voucherNo, ekdosisDate);
                        if (dateUpdated) {
                            System.out.println("✅ Ενημερώθηκε ημερομηνία έκδοσης για " + voucherNo + ": " + ekdosisDate + " (από: " + sourceAction + ")");
                        }
                    } else {
                        LocalDate specialDate = LocalDate.of(1900, 1, 1);
                        boolean dateUpdated = updateIssuanceDate(voucherNo, specialDate);
                        if (dateUpdated) {
                            System.out.println("⚠️ Δεν βρέθηκε καμία έγκυρη ημερομηνία για " + voucherNo + " - Τέθηκε ειδική τιμή");
                        }
                    }
                }

                List<TrackingDetail> oldDetails = trackingDetailDAO.findByArithmosApostolis(voucherNo);
                String oldFilteredStatus = TrackingStatusFilter.getLatestFilteredStatus(oldDetails);

                boolean saved = trackingDetailDAO.saveTrackingDetails(voucherNo, trackingDetails);

                if (saved) {
                    List<TrackingDetail> newDetails = trackingDetailDAO.findByArithmosApostolis(voucherNo);
                    String newFilteredStatus = TrackingStatusFilter.getLatestFilteredStatus(newDetails);


                    if (newFilteredStatus != null && !newFilteredStatus.trim().isEmpty()) {
                        LocalDate departureDate = trackingDetailDAO.findDepartureDate(voucherNo);
                        if (departureDate == null) {
                            newFilteredStatus = "ΑΔΙΑΚΙΝΗΤΟ";
                        }

                        apostoliDAO = new ApostoliDAO();
                        boolean statusUpdated = apostoliDAO.updateStatusDetails(voucherNo, newFilteredStatus);
                        if (statusUpdated) {
                            System.out.println("✅ Ενημερώθηκε status details: " + voucherNo + " -> " + newFilteredStatus);
                        }
                    }

                    oldFilteredStatus = TrackingStatusFilter.getLatestFilteredStatus(oldDetails);
                    hasStatusChanged = !newFilteredStatus.equals(oldFilteredStatus);

                    if ("ΑΔΙΑΚΙΝΗΤΟ".equals(newFilteredStatus)) {
                        System.out.println("⚠️ Η αποστολή " + voucherNo + " παραμένει ΑΔΙΑΚΙΝΗΤΟ - Έλεγχος summary για ακύρωση");
                    }
                }

                return saved;
            } else {
                System.out.println("Δεν βρέθηκαν tracking details για: " + voucherNo);

                if (!hasNotFoundTrackingDetail(voucherNo)) {
                    boolean trackingCreated = createNotFoundTrackingDetail(voucherNo);
                    ApostoliDAO apostoliDAO = new ApostoliDAO();
                    boolean dateUpdated = apostoliDAO.updateShipmentNotFound(voucherNo);
                    if (trackingCreated && dateUpdated) {
                        System.out.println("📄 Δημιουργήθηκε tracking detail: ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ για " + voucherNo);
                    }
                } else {
                    System.out.println("ℹ️ Υπάρχει ήδη tracking detail 'ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ' για " + voucherNo);
                }
                return false;
            }

        } catch (Exception e) {
            System.err.println("Σφάλμα επεξεργασίας response: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasStatusChanged() {
        return hasStatusChanged;
    }

    private List<TrackingDetail> parseTrackingDetails(String voucherNo, JsonNode tableData) {
        List<TrackingDetail> details = new ArrayList<>();
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")
        };

        for (JsonNode checkpoint : tableData) {
            try {
                String dateTimeStr = checkpoint.path("checkpoint_date_time").asText();
                String action = checkpoint.path("checkpoint_action").asText();
                String location = checkpoint.path("checkpoint_location").asText();
                String notes = checkpoint.path("checkpoint_notes").asText();

                LocalDateTime dateTime = null;
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                        break;
                    } catch (DateTimeParseException ignored) {
                    }
                }

                if (dateTime == null) {
                    System.err.println("Αδυναμία parsing ημερομηνίας: " + dateTimeStr);
                    continue;
                }

                TrackingDetail detail = new TrackingDetail(
                        voucherNo, dateTime, action, location,
                        notes != null && !notes.trim().isEmpty() ? notes.trim() : ""
                );

                details.add(detail);

            } catch (Exception e) {
                System.err.println("Σφάλμα parsing checkpoint: " + e.getMessage());
            }
        }

        return details;
    }

    public List<TrackingDetail> getTrackingDetails(String arithmosApostolis) {
        return trackingDetailDAO.findByArithmosApostolis(arithmosApostolis);
    }

    public LocalDate getDepartureDate(String arithmosApostolis) {
        return trackingDetailDAO.findDepartureDate(arithmosApostolis);
    }

    public boolean hasTrackingDetails(String arithmosApostolis) {
        return trackingDetailDAO.hasTrackingDetails(arithmosApostolis);
    }

    private boolean updateIssuanceDate(String arithmosApostolis, LocalDate ekdosisDate) {
        return trackingDetailDAO.updateIssuanceDate(arithmosApostolis, ekdosisDate);
    }

    public boolean createNotFoundTrackingDetail(String arithmosApostolis) {
        return trackingDetailDAO.createNotFoundTrackingDetail(arithmosApostolis);
    }

    public boolean hasNotFoundTrackingDetail(String arithmosApostolis) {
        return trackingDetailDAO.hasNotFoundTrackingDetail(arithmosApostolis);
    }
}
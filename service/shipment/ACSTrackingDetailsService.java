package org.example.service.shipment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.TrackingDetailDAO;
import org.example.model.TrackingDetail;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    public ACSTrackingDetailsService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.trackingDetailDAO = new TrackingDetailDAO();
    }

    public boolean fetchAndSaveTrackingDetails(String voucherNo) {
        try {
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
                return trackingDetailDAO.saveTrackingDetails(voucherNo, trackingDetails);
            } else {
                System.out.println("Δεν βρέθηκαν tracking details για: " + voucherNo);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Σφάλμα επεξεργασίας response: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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

    public boolean hasTrackingDetails(String arithmosApostolis) {
        return trackingDetailDAO.hasTrackingDetails(arithmosApostolis);
    }
}
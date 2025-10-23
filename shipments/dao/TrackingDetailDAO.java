package org.pms.shipments.dao;

import org.pms.config.DatabaseConfig;
import org.pms.shipments.model.TrackingDetail;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrackingDetailDAO {

    private final Connection connection;

    public TrackingDetailDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean saveTrackingDetails(String arithmosApostolis, List<TrackingDetail> details) {
        String deleteSql = "DELETE FROM apostoles_tracking_details WHERE arithmos_apostolis = ?";
        String insertSql = """
        INSERT INTO apostoles_tracking_details 
        (arithmos_apostolis, checkpoint_date_time, checkpoint_action, checkpoint_location, checkpoint_notes)
        VALUES (?, ?, ?, ?, ?)
    """;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, arithmosApostolis);
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                for (TrackingDetail detail : details) {
                    insertStmt.setString(1, arithmosApostolis);
                    insertStmt.setTimestamp(2, Timestamp.valueOf(detail.getCheckpointDateTime()));
                    insertStmt.setString(3, detail.getCheckpointAction());
                    insertStmt.setString(4, detail.getCheckpointLocation());
                    insertStmt.setString(5, detail.getCheckpointNotes());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            connection.commit();

            // Ενημέρωση status details με ΑΔΙΑΚΙΝΗΤΟ αν δεν υπάρχει departure date
            LocalDate departureDate = findDepartureDate(arithmosApostolis);
            if (departureDate == null && !details.isEmpty()) {
                String updateStatusSql = "UPDATE apostoles SET status_details = 'ΑΔΙΑΚΙΝΗΤΟ' WHERE arithmos_apostolis = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateStatusSql)) {
                    updateStmt.setString(1, arithmosApostolis);
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Σφάλμα ενημέρωσης status details σε ΑΔΙΑΚΙΝΗΤΟ: " + e.getMessage());
                }
            } else if (departureDate != null) {
                // Ενημέρωση ημερομηνίας αναχώρησης αν υπάρχει tracking detail με ΑΝΑΧΩΡΗΣΗ
                String updateSql = """
                    UPDATE apostoles 
                    SET imerominia_anaxorisis = ? 
                    WHERE arithmos_apostolis = ? AND imerominia_anaxorisis IS NULL
                """;

                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDate(1, Date.valueOf(departureDate));
                    updateStmt.setString(2, arithmosApostolis);
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Σφάλμα ενημέρωσης ημερομηνίας αναχώρησης: " + e.getMessage());
                }
            }

            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Σφάλμα rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Σφάλμα αποθήκευσης tracking details: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Σφάλμα επαναφοράς autocommit: " + e.getMessage());
            }
        }
    }

    public List<TrackingDetail> findByArithmosApostolis(String arithmosApostolis) {
        String sql = """
            SELECT id, arithmos_apostolis, checkpoint_date_time, checkpoint_action, 
                   checkpoint_location, checkpoint_notes, created_at
            FROM apostoles_tracking_details 
            WHERE arithmos_apostolis = ?
            ORDER BY checkpoint_date_time DESC
        """;

        List<TrackingDetail> details = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TrackingDetail detail = new TrackingDetail();
                detail.setId(rs.getInt("id"));
                detail.setArithmosApostolis(rs.getString("arithmos_apostolis"));
                detail.setCheckpointDateTime(rs.getTimestamp("checkpoint_date_time").toLocalDateTime());
                detail.setCheckpointAction(rs.getString("checkpoint_action"));
                detail.setCheckpointLocation(rs.getString("checkpoint_location"));
                detail.setCheckpointNotes(rs.getString("checkpoint_notes"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    detail.setCreatedAt(createdAt.toLocalDateTime());
                }

                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης tracking details: " + e.getMessage());
        }

        return details;
    }

    public boolean hasTrackingDetails(String arithmosApostolis) {
        String sql = "SELECT COUNT(*) FROM apostoles_tracking_details WHERE arithmos_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ελέγχου tracking details: " + e.getMessage());
        }

        return false;
    }

    public LocalDate findDepartureDate(String arithmosApostolis) {
        String sql = """
        SELECT checkpoint_date_time 
        FROM apostoles_tracking_details 
        WHERE arithmos_apostolis = ? 
        AND UPPER(checkpoint_action) LIKE '%ΑΝΑΧΩΡΗΣΗ%'
        ORDER BY checkpoint_date_time ASC
        LIMIT 1
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getTimestamp("checkpoint_date_time").toLocalDateTime().toLocalDate();
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης ημερομηνίας αναχώρησης: " + e.getMessage());
        }

        return null;
    }

    public boolean updateIssuanceDate(String arithmosApostolis, LocalDate ekdosisDate) {
        String sql = "UPDATE apostoles SET imerominia_ekdosis = ? WHERE arithmos_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(ekdosisDate));
            stmt.setString(2, arithmosApostolis);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης ημερομηνίας έκδοσης: " + e.getMessage());
            return false;
        }
    }

    /**
     * Δημιουργία tracking detail για αποστολή που δε βρέθηκε
     */
    public boolean createNotFoundTrackingDetail(String arithmosApostolis) {
        String sql = """
        INSERT INTO apostoles_tracking_details 
        (arithmos_apostolis, checkpoint_date_time, checkpoint_action, checkpoint_location, checkpoint_notes)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, "ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ");
            stmt.setString(4, "ΣΥΣΤΗΜΑ");
            stmt.setString(5, "Η αποστολή δεν βρέθηκε στο σύστημα του courier");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας tracking detail για αποστολή που δε βρέθηκε: " + e.getMessage());
            return false;
        }
    }

    /**
     * Έλεγχος αν υπάρχει ήδη tracking detail για αποστολή που δε βρέθηκε
     */
    public boolean hasNotFoundTrackingDetail(String arithmosApostolis) {
        String sql = """
        SELECT COUNT(*) FROM apostoles_tracking_details 
        WHERE arithmos_apostolis = ? 
        AND checkpoint_action = 'ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ'
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ελέγχου tracking detail για αποστολή που δε βρέθηκε: " + e.getMessage());
        }

        return false;
    }

    public boolean createCancelledTrackingDetail(String arithmosApostolis) {
        String sql = """
        INSERT INTO apostoles_tracking_details 
        (arithmos_apostolis, checkpoint_datetime, checkpoint_action, checkpoint_location, checkpoint_notes)
        VALUES (?, NOW(), 'ΑΚΥΡΩΘΗΚΕ', 'ΣΥΣΤΗΜΑ', 'Η αποστολή ακυρώθηκε από τον courier')
        ON DUPLICATE KEY UPDATE 
        checkpoint_action = VALUES(checkpoint_action),
        checkpoint_location = VALUES(checkpoint_location),
        checkpoint_notes = VALUES(checkpoint_notes)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας cancelled tracking detail: " + e.getMessage());
            return false;
        }
    }
}
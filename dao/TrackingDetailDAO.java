package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.TrackingDetail;

import java.sql.*;
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
}
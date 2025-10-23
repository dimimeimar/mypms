package org.pms.shipments.dao;

import org.pms.config.DatabaseConfig;
import org.pms.shipments.model.CustomerCareComment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerCareDAO {
    private final Connection connection;

    public CustomerCareDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean create(CustomerCareComment comment) {
        String sql = "INSERT INTO apostoles_customer_care (id_apostolis, sxolio) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, comment.getIdApostolis());
            stmt.setString(2, comment.getSxolio());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας σχολίου: " + e.getMessage());
            return false;
        }
    }

    public List<CustomerCareComment> findByApostoli(int idApostolis) {
        List<CustomerCareComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM apostoles_customer_care WHERE id_apostolis = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerCareComment comment = new CustomerCareComment();
                comment.setId(rs.getInt("id"));
                comment.setIdApostolis(rs.getInt("id_apostolis"));
                comment.setSxolio(rs.getString("sxolio"));

                Timestamp createdTimestamp = rs.getTimestamp("created_at");
                if (createdTimestamp != null) {
                    comment.setCreatedAt(createdTimestamp.toLocalDateTime());
                }

                Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
                if (updatedTimestamp != null) {
                    comment.setUpdatedAt(updatedTimestamp.toLocalDateTime());
                }

                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης σχολίων: " + e.getMessage());
        }

        return comments;
    }

    public boolean update(CustomerCareComment comment) {
        String sql = "UPDATE apostoles_customer_care SET sxolio = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, comment.getSxolio());
            stmt.setInt(2, comment.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης σχολίου: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM apostoles_customer_care WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής σχολίου: " + e.getMessage());
            return false;
        }
    }
}
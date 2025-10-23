package org.pms.antikatavoles.dao;

import org.pms.config.DatabaseConfig;
import org.pms.antikatavoles.model.AntikatavoliApodosi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntikatavoliApodosiDAO {
    private final Connection connection;

    public AntikatavoliApodosiDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean create(AntikatavoliApodosi apodosi) {
        String sql = """
            INSERT INTO antikatavoli_apodoseis 
            (kodikos_pelati, eponimia_etairias, imerominia_apodosis, 
             synolo_poso, plithos_antikatabolon, sxolia)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, apodosi.getKodikosPelati());
            stmt.setString(2, apodosi.getEponimiaEtairias());
            stmt.setDate(3, apodosi.getImerominiaApodosis() != null ?
                    Date.valueOf(apodosi.getImerominiaApodosis()) : null);
            stmt.setBigDecimal(4, apodosi.getSynoloPoso());
            stmt.setInt(5, apodosi.getPlithosAntikatabolon());
            stmt.setString(6, apodosi.getSxolia());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    apodosi.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας απόδοσης: " + e.getMessage());
        }
        return false;
    }

    public List<AntikatavoliApodosi> findAll() {
        String sql = "SELECT * FROM antikatavoli_apodoseis ORDER BY imerominia_apodosis DESC";
        List<AntikatavoliApodosi> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToApodosi(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποδόσεων: " + e.getMessage());
        }
        return results;
    }

    public List<AntikatavoliApodosi> findByKodikosPelati(String kodikosPelati) {
        String sql = "SELECT * FROM antikatavoli_apodoseis WHERE kodikos_pelati = ? ORDER BY imerominia_apodosis DESC";
        List<AntikatavoliApodosi> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToApodosi(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποδόσεων πελάτη: " + e.getMessage());
        }
        return results;
    }

    private AntikatavoliApodosi mapResultSetToApodosi(ResultSet rs) throws SQLException {
             AntikatavoliApodosi apodosi = new AntikatavoliApodosi();
        apodosi.setId(rs.getInt("id"));
        apodosi.setKodikosPelati(rs.getString("kodikos_pelati"));
        apodosi.setEponimiaEtairias(rs.getString("eponimia_etairias"));
        Date apodosisDate = rs.getDate("imerominia_apodosis");
        if (apodosisDate != null) {
            apodosi.setImerominiaApodosis(apodosisDate.toLocalDate());
        }
        apodosi.setSynoloPoso(rs.getBigDecimal("synolo_poso"));
        apodosi.setPlithosAntikatabolon(rs.getInt("plithos_antikatabolon"));
        apodosi.setSxolia(rs.getString("sxolia"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            apodosi.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            apodosi.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return apodosi;
    }

    public AntikatavoliApodosi findById(int id) {
        String sql = "SELECT * FROM antikatavoli_apodoseis WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToApodosi(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης απόδοσης: " + e.getMessage());
        }
        return null;
    }

    public boolean update(AntikatavoliApodosi apodosi) {
        String sql = """
        UPDATE antikatavoli_apodoseis SET 
            imerominia_apodosis = ?, synolo_poso = ?, 
            plithos_antikatabolon = ?, sxolia = ?
        WHERE id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, apodosi.getImerominiaApodosis() != null ?
                    Date.valueOf(apodosi.getImerominiaApodosis()) : null);
            stmt.setBigDecimal(2, apodosi.getSynoloPoso());
            stmt.setInt(3, apodosi.getPlithosAntikatabolon());
            stmt.setString(4, apodosi.getSxolia());
            stmt.setInt(5, apodosi.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης απόδοσης: " + e.getMessage());
            return false;
        }
    }


}
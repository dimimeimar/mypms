package org.pms.antikatavoles.dao;

import org.pms.config.DatabaseConfig;
import org.pms.antikatavoles.model.Antikatavoli;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntikatavoliDAO {
    private final Connection connection;

    public AntikatavoliDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean create(Antikatavoli antikatavoli) {
        String sql = """
            INSERT INTO antikatavoles 
            (id_apostolis, apodothike, imerominia_apodosis, id_apodosis, 
             parastatikoACS, parastatikoMyPMS, sxolia)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, antikatavoli.getIdApostolis());
            stmt.setBoolean(2, antikatavoli.getApodothike() != null ? antikatavoli.getApodothike() : false);
            stmt.setDate(3, antikatavoli.getImerominiaApodosis() != null ?
                    Date.valueOf(antikatavoli.getImerominiaApodosis()) : null);
            stmt.setObject(4, antikatavoli.getIdApodosis());
            stmt.setString(5, antikatavoli.getParastatikoACS());
            stmt.setString(6, antikatavoli.getParastatikoMyPMS());
            stmt.setString(7, antikatavoli.getSxolia());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    antikatavoli.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας αντικαταβολής: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Antikatavoli antikatavoli) {
        String sql = """
            UPDATE antikatavoles SET 
                apodothike = ?, imerominia_apodosis = ?,
                id_apodosis = ?, parastatikoACS = ?, 
                parastatikoMyPMS = ?, sxolia = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, antikatavoli.getApodothike() != null ? antikatavoli.getApodothike() : false);
            stmt.setDate(2, antikatavoli.getImerominiaApodosis() != null ?
                    Date.valueOf(antikatavoli.getImerominiaApodosis()) : null);
            stmt.setObject(3, antikatavoli.getIdApodosis());
            stmt.setString(4, antikatavoli.getParastatikoACS());
            stmt.setString(5, antikatavoli.getParastatikoMyPMS());
            stmt.setString(6, antikatavoli.getSxolia());
            stmt.setInt(7, antikatavoli.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης αντικαταβολής: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM antikatavoles WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής αντικαταβολής: " + e.getMessage());
            return false;
        }
    }

    public Antikatavoli findById(int id) {
        String sql = "SELECT * FROM antikatavoles WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAntikatavoli(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αντικαταβολής: " + e.getMessage());
        }
        return null;
    }

    public Antikatavoli findByIdApostolis(int idApostolis) {
        String sql = "SELECT * FROM antikatavoles WHERE id_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAntikatavoli(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αντικαταβολής: " + e.getMessage());
        }
        return null;
    }

    public List<Antikatavoli> findAll() {
        String sql = "SELECT * FROM antikatavoles ORDER BY created_at DESC";
        List<Antikatavoli> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToAntikatavoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αντικαταβολών: " + e.getMessage());
        }
        return results;
    }

    public List<Antikatavoli> findPendingByApostoles(List<Integer> idApostolonList) {
        if (idApostolonList == null || idApostolonList.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM antikatavoles WHERE apodothike = FALSE AND id_apostolis IN ("
        );

        for (int i = 0; i < idApostolonList.size(); i++) {
            sql.append("?");
            if (i < idApostolonList.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(") ORDER BY created_at ASC");

        List<Antikatavoli> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < idApostolonList.size(); i++) {
                stmt.setInt(i + 1, idApostolonList.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToAntikatavoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εκκρεμών αντικαταβολών: " + e.getMessage());
        }
        return results;
    }

    private Antikatavoli mapResultSetToAntikatavoli(ResultSet rs) throws SQLException {
        Antikatavoli antikatavoli = new Antikatavoli();
        antikatavoli.setId(rs.getInt("id"));
        antikatavoli.setIdApostolis(rs.getInt("id_apostolis"));
        antikatavoli.setApodothike(rs.getBoolean("apodothike"));

        Date apodosisDate = rs.getDate("imerominia_apodosis");
        if (apodosisDate != null) {
            antikatavoli.setImerominiaApodosis(apodosisDate.toLocalDate());
        }

        antikatavoli.setIdApodosis(rs.getObject("id_apodosis", Integer.class));
        antikatavoli.setParastatikoACS(rs.getString("parastatikoACS"));
        antikatavoli.setParastatikoMyPMS(rs.getString("parastatikoMyPMS"));
        antikatavoli.setSxolia(rs.getString("sxolia"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            antikatavoli.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            antikatavoli.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return antikatavoli;
    }
}
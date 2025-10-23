package org.pms.shipments.dao;

import org.pms.config.DatabaseConfig;
import org.pms.shipments.model.Ekremotita;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EkremotitaDAO {

    private final Connection connection;

    public EkremotitaDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean create(Ekremotita ekremotita) {
        String sql = """
            INSERT INTO ekremotites (id_apostolis, titlos, perigrafi, status, priority, created_by)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, ekremotita.getIdApostolis(), Types.INTEGER);
            stmt.setString(2, ekremotita.getTitlos());
            stmt.setString(3, ekremotita.getPerigrafi());
            stmt.setString(4, ekremotita.getStatus());
            stmt.setString(5, ekremotita.getPriority());
            stmt.setString(6, ekremotita.getCreatedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ekremotita.setIdEkremotita(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας εκκρεμότητας: " + e.getMessage());
        }
        return false;
    }

    public Ekremotita findById(int idEkremotita) {
        String sql = """
            SELECT e.*, a.arithmos_apostolis
            FROM ekremotites e
            LEFT JOIN apostoles a ON e.id_apostolis = a.id_apostolis
            WHERE e.id_ekremotita = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEkremotita);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEkremotita(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εκκρεμότητας: " + e.getMessage());
        }
        return null;
    }

    public List<Ekremotita> findAll() {
        List<Ekremotita> list = new ArrayList<>();
        String sql = """
            SELECT e.*, a.arithmos_apostolis
            FROM ekremotites e
            LEFT JOIN apostoles a ON e.id_apostolis = a.id_apostolis
            ORDER BY e.created_at DESC
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToEkremotita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εκκρεμοτήτων: " + e.getMessage());
        }
        return list;
    }

    public List<Ekremotita> findByStatus(String status) {
        List<Ekremotita> list = new ArrayList<>();
        String sql = """
            SELECT e.*, a.arithmos_apostolis
            FROM ekremotites e
            LEFT JOIN apostoles a ON e.id_apostolis = a.id_apostolis
            WHERE e.status = ?
            ORDER BY e.priority DESC, e.created_at DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToEkremotita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εκκρεμοτήτων με status: " + e.getMessage());
        }
        return list;
    }

    public List<Ekremotita> findByApostoli(int idApostolis) {
        List<Ekremotita> list = new ArrayList<>();
        String sql = """
            SELECT e.*, a.arithmos_apostolis
            FROM ekremotites e
            LEFT JOIN apostoles a ON e.id_apostolis = a.id_apostolis
            WHERE e.id_apostolis = ?
            ORDER BY e.created_at DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToEkremotita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εκκρεμοτήτων αποστολής: " + e.getMessage());
        }
        return list;
    }

    public List<Ekremotita> search(String searchTerm, String statusFilter, String priorityFilter, Boolean linkedOnly) {
        List<Ekremotita> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, a.arithmos_apostolis
            FROM ekremotites e
            LEFT JOIN apostoles a ON e.id_apostolis = a.id_apostolis
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (e.titlos LIKE ? OR e.perigrafi LIKE ? OR a.arithmos_apostolis LIKE ?)");
            String searchPattern = "%" + searchTerm.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (statusFilter != null && !statusFilter.equals("ΟΛΕΣ")) {
            sql.append(" AND e.status = ?");
            params.add(statusFilter);
        }

        if (priorityFilter != null && !priorityFilter.equals("ΟΛΕΣ")) {
            sql.append(" AND e.priority = ?");
            params.add(priorityFilter);
        }

        if (linkedOnly != null) {
            if (linkedOnly) {
                sql.append(" AND e.id_apostolis IS NOT NULL");
            } else {
                sql.append(" AND e.id_apostolis IS NULL");
            }
        }

        sql.append(" ORDER BY e.priority DESC, e.created_at DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEkremotita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης εκκρεμοτήτων: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Ekremotita ekremotita) {
        String sql = """
            UPDATE ekremotites
            SET id_apostolis = ?, titlos = ?, perigrafi = ?, status = ?, priority = ?
            WHERE id_ekremotita = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, ekremotita.getIdApostolis(), Types.INTEGER);
            stmt.setString(2, ekremotita.getTitlos());
            stmt.setString(3, ekremotita.getPerigrafi());
            stmt.setString(4, ekremotita.getStatus());
            stmt.setString(5, ekremotita.getPriority());
            stmt.setInt(6, ekremotita.getIdEkremotita());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης εκκρεμότητας: " + e.getMessage());
            return false;
        }
    }

    public boolean markAsResolved(int idEkremotita) {
        String sql = """
            UPDATE ekremotites
            SET status = 'ΕΠΙΛΥΘΗΚΕ', resolved_at = ?
            WHERE id_ekremotita = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, idEkremotita);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα επίλυσης εκκρεμότητας: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idEkremotita) {
        String sql = "DELETE FROM ekremotites WHERE id_ekremotita = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEkremotita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής εκκρεμότητας: " + e.getMessage());
            return false;
        }
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM ekremotites WHERE status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα μέτρησης εκκρεμοτήτων: " + e.getMessage());
        }
        return 0;
    }

    private Ekremotita mapResultSetToEkremotita(ResultSet rs) throws SQLException {
        Ekremotita ekremotita = new Ekremotita();
        ekremotita.setIdEkremotita(rs.getInt("id_ekremotita"));

        int idApostolis = rs.getInt("id_apostolis");
        ekremotita.setIdApostolis(rs.wasNull() ? null : idApostolis);

        ekremotita.setTitlos(rs.getString("titlos"));
        ekremotita.setPerigrafi(rs.getString("perigrafi"));
        ekremotita.setStatus(rs.getString("status"));
        ekremotita.setPriority(rs.getString("priority"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ekremotita.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            ekremotita.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        Timestamp resolvedAt = rs.getTimestamp("resolved_at");
        if (resolvedAt != null) {
            ekremotita.setResolvedAt(resolvedAt.toLocalDateTime());
        }

        ekremotita.setCreatedBy(rs.getString("created_by"));
        ekremotita.setArithmosApostolis(rs.getString("arithmos_apostolis"));

        return ekremotita;
    }
}
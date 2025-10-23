package org.pms.customers.dao;

import org.pms.config.DatabaseConfig;
import org.pms.customers.model.Ypeuthynos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO κλάση για τον Υπεύθυνο
 */
public class YpeuthynosDAO {

    private final Connection connection;

    public YpeuthynosDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Δημιουργία νέου υπευθύνου
     */
    public boolean create(Ypeuthynos ypeuthynos) {
        String sql = """
            INSERT INTO ypeuthynos (id_etairias, eidos_ypeuthynou, onoma_ypeuthynou, 
            titlos, tilefono, kinito, email, is_master_email) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ypeuthynos.getIdEtairias());
            stmt.setString(2, ypeuthynos.getEidosYpeuthynou().getValue());
            stmt.setString(3, ypeuthynos.getOnomaYpeuthynou());
            stmt.setString(4, ypeuthynos.getTitlos());
            stmt.setString(5, ypeuthynos.getTilefono());
            stmt.setString(6, ypeuthynos.getKinito());
            stmt.setString(7, ypeuthynos.getEmail());
            stmt.setBoolean(8, ypeuthynos.isMasterEmail());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ypeuthynos.setIdYpeuthynou(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας υπευθύνου: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ανάκτηση υπευθύνου βάσει ID
     */
    public Ypeuthynos findById(int idYpeuthynou) {
        String sql = "SELECT * FROM ypeuthynos WHERE id_ypeuthynou = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idYpeuthynou);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToYpeuthynos(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης υπευθύνου: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ανάκτηση όλων των υπευθύνων μιας εταιρίας
     */
    public List<Ypeuthynos> findByEtairia(int idEtairias) {
        List<Ypeuthynos> ypeuthynoiList = new ArrayList<>();
        String sql = "SELECT * FROM ypeuthynos WHERE id_etairias = ? ORDER BY eidos_ypeuthynou";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEtairias);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ypeuthynoiList.add(mapResultSetToYpeuthynos(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης υπευθύνων εταιρίας: " + e.getMessage());
        }
        return ypeuthynoiList;
    }

    /**
     * Ανάκτηση υπευθύνου βάσει εταιρίας και είδους
     */
    public Ypeuthynos findByEtairiaAndEidos(int idEtairias, Ypeuthynos.EidosYpeuthynou eidos) {
        String sql = "SELECT * FROM ypeuthynos WHERE id_etairias = ? AND eidos_ypeuthynou = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEtairias);
            stmt.setString(2, eidos.getValue());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToYpeuthynos(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης υπευθύνου: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ενημέρωση υπευθύνου
     */
    public boolean update(Ypeuthynos ypeuthynos) {
        String sql = """
            UPDATE ypeuthynos SET id_etairias = ?, eidos_ypeuthynou = ?, 
            onoma_ypeuthynou = ?, titlos = ?, tilefono = ?, kinito = ?, 
            email = ?, is_master_email = ? WHERE id_ypeuthynou = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ypeuthynos.getIdEtairias());
            stmt.setString(2, ypeuthynos.getEidosYpeuthynou().getValue());
            stmt.setString(3, ypeuthynos.getOnomaYpeuthynou());
            stmt.setString(4, ypeuthynos.getTitlos());
            stmt.setString(5, ypeuthynos.getTilefono());
            stmt.setString(6, ypeuthynos.getKinito());
            stmt.setString(7, ypeuthynos.getEmail());
            stmt.setBoolean(8, ypeuthynos.isMasterEmail());
            stmt.setInt(9, ypeuthynos.getIdYpeuthynou());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης υπευθύνου: " + e.getMessage());
            return false;
        }
    }

    /**
     * Διαγραφή υπευθύνου
     */
    public boolean delete(int idYpeuthynou) {
        String sql = "DELETE FROM ypeuthynos WHERE id_ypeuthynou = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idYpeuthynou);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής υπευθύνου: " + e.getMessage());
            return false;
        }
    }

    /**
     * Διαγραφή όλων των υπευθύνων μιας εταιρίας
     */
    public boolean deleteByEtairia(int idEtairias) {
        String sql = "DELETE FROM ypeuthynos WHERE id_etairias = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEtairias);
            return stmt.executeUpdate() >= 0; // Μπορεί να είναι 0 αν δεν υπάρχουν υπεύθυνοι
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής υπευθύνων εταιρίας: " + e.getMessage());
            return false;
        }
    }

    /**
     * Δημιουργία ή ενημέρωση υπευθύνου (upsert)
     */
    public boolean createOrUpdate(Ypeuthynos ypeuthynos) {
        // Ελέγχουμε αν υπάρχει ήδη υπεύθυνος αυτού του είδους για την εταιρία
        Ypeuthynos existing = findByEtairiaAndEidos(ypeuthynos.getIdEtairias(), ypeuthynos.getEidosYpeuthynou());

        if (existing != null) {
            // Ενημερώνουμε τον υπάρχοντα
            ypeuthynos.setIdYpeuthynou(existing.getIdYpeuthynou());
            return update(ypeuthynos);
        } else {
            // Δημιουργούμε νέο
            return create(ypeuthynos);
        }
    }

    /**
     * Δημιουργία ή ενημέρωση όλων των υπευθύνων μιας εταιρίας
     */
    public boolean saveAllForEtairia(int idEtairias, List<Ypeuthynos> ypeuthynoiList) {
        try {
            connection.setAutoCommit(false);

            // Διαγράφουμε τους υπάρχοντες υπευθύνους
            deleteByEtairia(idEtairias);

            // Προσθέτουμε τους νέους
            for (Ypeuthynos ypeuthynos : ypeuthynoiList) {
                if (ypeuthynos.getOnomaYpeuthynou() != null && !ypeuthynos.getOnomaYpeuthynou().trim().isEmpty()) {
                    ypeuthynos.setIdEtairias(idEtairias);
                    if (!create(ypeuthynos)) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Σφάλμα rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Σφάλμα αποθήκευσης υπευθύνων: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Σφάλμα επαναφοράς autoCommit: " + e.getMessage());
            }
        }
    }

    /**
     * Μετατροπή ResultSet σε Ypeuthynos object
     */
    private Ypeuthynos mapResultSetToYpeuthynos(ResultSet rs) throws SQLException {
        Ypeuthynos ypeuthynos = new Ypeuthynos();
        ypeuthynos.setIdYpeuthynou(rs.getInt("id_ypeuthynou"));
        ypeuthynos.setIdEtairias(rs.getInt("id_etairias"));

        String eidosStr = rs.getString("eidos_ypeuthynou");
        ypeuthynos.setEidosYpeuthynou(Ypeuthynos.EidosYpeuthynou.fromString(eidosStr));

        ypeuthynos.setOnomaYpeuthynou(rs.getString("onoma_ypeuthynou"));
        ypeuthynos.setTitlos(rs.getString("titlos"));
        ypeuthynos.setTilefono(rs.getString("tilefono"));
        ypeuthynos.setKinito(rs.getString("kinito"));
        ypeuthynos.setEmail(rs.getString("email"));
        ypeuthynos.setMasterEmail(rs.getBoolean("is_master_email"));

        // Handle timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ypeuthynos.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            ypeuthynos.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return ypeuthynos;
    }
}
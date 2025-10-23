package org.pms.customers.dao;

import org.pms.config.DatabaseConfig;
import org.pms.customers.model.Etairia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO κλάση για την Εταιρία
 */
public class EtairiaDAO {

    private final Connection connection;

    public EtairiaDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Δημιουργία νέας εταιρίας
     */
    public boolean create(Etairia etairia) {
        String sql = """
            INSERT INTO etairia (poli, perioxi, odos, arithmos, tk, afm_pelati) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, etairia.getPoli());
            stmt.setString(2, etairia.getPerioxi());
            stmt.setString(3, etairia.getOdos());
            stmt.setString(4, etairia.getArithmos());
            stmt.setString(5, etairia.getTk());
            stmt.setString(6, etairia.getAfmPelati());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    etairia.setIdEtairias(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας εταιρίας: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ανάκτηση εταιρίας βάσει ID
     */
    public Etairia findById(int idEtairias) {
        String sql = "SELECT * FROM etairia WHERE id_etairias = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEtairias);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEtairia(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εταιρίας: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ανάκτηση όλων των εταιριών
     */
    public List<Etairia> findAll() {
        List<Etairia> etairiesList = new ArrayList<>();
        String sql = "SELECT * FROM etairia ORDER BY poli, odos";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                etairiesList.add(mapResultSetToEtairia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εταιριών: " + e.getMessage());
        }
        return etairiesList;
    }

    /**
     * Ανάκτηση εταιριών βάσει κωδικού πελάτη
     */
    public List<Etairia> findByPelatis(String afmPelati) {
        List<Etairia> etairiesList = new ArrayList<>();
        String sql = "SELECT * FROM etairia WHERE afm_pelati = ? ORDER BY poli, odos";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, afmPelati);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                etairiesList.add(mapResultSetToEtairia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης εταιριών πελάτη: " + e.getMessage());
        }
        return etairiesList;
    }

    /**
     * Ενημέρωση εταιρίας
     */
    public boolean update(Etairia etairia) {
        String sql = """
            UPDATE etairia SET poli = ?, perioxi = ?, odos = ?, 
            arithmos = ?, tk = ?, afm_pelati = ? WHERE id_etairias = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, etairia.getPoli());
            stmt.setString(2, etairia.getPerioxi());
            stmt.setString(3, etairia.getOdos());
            stmt.setString(4, etairia.getArithmos());
            stmt.setString(5, etairia.getTk());
            stmt.setString(6, etairia.getAfmPelati());
            stmt.setInt(7, etairia.getIdEtairias());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης εταιρίας: " + e.getMessage());
            return false;
        }
    }

    /**
     * Διαγραφή εταιρίας
     */
    public boolean delete(int idEtairias) {
        String sql = "DELETE FROM etairia WHERE id_etairias = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEtairias);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής εταιρίας: " + e.getMessage());
            return false;
        }
    }

    /**
     * Αναζήτηση εταιριών με φίλτρα
     */
    public List<Etairia> search(String searchTerm, String poli) {
        List<Etairia> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM etairia WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (odos LIKE ? OR arithmos LIKE ? OR tk LIKE ? OR perioxi LIKE ?)");
            String searchPattern = "%" + searchTerm.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (poli != null && !poli.trim().isEmpty() && !poli.equals("Όλες")) {
            sql.append(" AND poli = ?");
            parameters.add(poli);
        }

        sql.append(" ORDER BY poli, odos");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToEtairia(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης εταιριών: " + e.getMessage());
        }
        return results;
    }

    /**
     * Ανάκτηση μοναδικών πόλεων για φίλτρα
     */
    public List<String> getDistinctCities() {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT poli FROM etairia WHERE poli IS NOT NULL ORDER BY poli";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.add(rs.getString("poli"));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης πόλεων: " + e.getMessage());
        }
        return cities;
    }

    /**
     * Μετατροπή ResultSet σε Etairia object
     */
    private Etairia mapResultSetToEtairia(ResultSet rs) throws SQLException {
        Etairia etairia = new Etairia();
        etairia.setIdEtairias(rs.getInt("id_etairias"));
        etairia.setPoli(rs.getString("poli"));
        etairia.setPerioxi(rs.getString("perioxi"));
        etairia.setOdos(rs.getString("odos"));
        etairia.setArithmos(rs.getString("arithmos"));
        etairia.setTk(rs.getString("tk"));
        etairia.setAfmPelati(rs.getString("afm_pelati"));

        // Handle timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            etairia.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            etairia.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return etairia;
    }
}
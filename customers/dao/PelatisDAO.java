package org.pms.customers.dao;

import org.pms.config.DatabaseConfig;
import org.pms.customers.model.Pelatis;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ενημερωμένο DAO κλάση για τον Πελάτη με ενσωματωμένα στοιχεία
 */
public class PelatisDAO {

    private final Connection connection;

    public PelatisDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Δημιουργία νέου πελάτη
     */
    public boolean create(Pelatis pelatis) {
        String sql = """
            INSERT INTO pelatis (
                kodikos_pelati, kategoria, afm_nomimu_ekprosopu, eponymia_etairias, 
                diakritikos_titlos, nomiki_morfi, epaggelma_antikimeno, afm_etairias, 
                dou_etairias, nomimos_ekprospos, email, tilefono_stathero, tilefono_kinito
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pelatis.getKodikosPelati());
            stmt.setString(2, pelatis.getKategoria());
            stmt.setString(3, pelatis.getAfmNomimuEkprosopu());
            stmt.setString(4, pelatis.getEponymiaEtairias());
            stmt.setString(5, pelatis.getDiakritikosTitlos());
            stmt.setString(6, pelatis.getNomikiMorfi());
            stmt.setString(7, pelatis.getEpaggelmaAntikimeno());
            stmt.setString(8, pelatis.getAfmEtairias());
            stmt.setString(9, pelatis.getDouEtairias());
            stmt.setString(10, pelatis.getNomimosEkprospos());
            stmt.setString(11, pelatis.getEmail());
            stmt.setString(12, pelatis.getTilefonoStathero());
            stmt.setString(13, pelatis.getTilefonoKinito());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας πελάτη: " + e.getMessage());
            return false;
        }
    }
    /**
     * Ανάκτηση πελάτη βάσει κωδικού
     */
    public Pelatis findById(String kodikosPelati) {
        String sql = "SELECT * FROM pelatis WHERE kodikos_pelati = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPelatis(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης πελάτη: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ανάκτηση όλων των πελατών
     */
    public List<Pelatis> findAll() {
        List<Pelatis> pelatesList = new ArrayList<>();
        String sql = "SELECT * FROM pelatis ORDER BY eponymia_etairias";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pelatesList.add(mapResultSetToPelatis(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης πελατών: " + e.getMessage());
        }
        return pelatesList;
    }

    /**
     * Ενημέρωση πελάτη
     */
    public boolean update(Pelatis pelatis) {
        String sql = """
            UPDATE pelatis SET 
                kategoria = ?, afm_nomimu_ekprosopu = ?, eponymia_etairias = ?, 
                diakritikos_titlos = ?, nomiki_morfi = ?, epaggelma_antikimeno = ?, 
                afm_etairias = ?, dou_etairias = ?, nomimos_ekprospos = ?,
                email = ?, tilefono_stathero = ?, tilefono_kinito = ?
            WHERE kodikos_pelati = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pelatis.getKategoria());
            stmt.setString(2, pelatis.getAfmNomimuEkprosopu());
            stmt.setString(3, pelatis.getEponymiaEtairias());
            stmt.setString(4, pelatis.getDiakritikosTitlos());
            stmt.setString(5, pelatis.getNomikiMorfi());
            stmt.setString(6, pelatis.getEpaggelmaAntikimeno());
            stmt.setString(7, pelatis.getAfmEtairias());
            stmt.setString(8, pelatis.getDouEtairias());
            stmt.setString(9, pelatis.getNomimosEkprospos());
            stmt.setString(10, pelatis.getEmail());
            stmt.setString(11, pelatis.getTilefonoStathero());
            stmt.setString(12, pelatis.getTilefonoKinito());
            stmt.setString(13, pelatis.getKodikosPelati());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης πελάτη: " + e.getMessage());
            return false;
        }
    }

    /**
     * Διαγραφή πελάτη
     */
    public boolean delete(String kodikosPelati) {
        String sql = "DELETE FROM pelatis WHERE kodikos_pelati = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής πελάτη: " + e.getMessage());
            return false;
        }
    }

    /**
     * Αναζήτηση πελατών με φίλτρα
     */
    public List<Pelatis> search(String searchTerm, String kategoria) {
        List<Pelatis> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pelatis WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (eponymia_etairias LIKE ? OR kodikos_pelati LIKE ? OR nomimos_ekprospos LIKE ? OR email LIKE ?)");
            String searchPattern = "%" + searchTerm.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (kategoria != null && !kategoria.trim().isEmpty() && !kategoria.equals("όλες")) {
            sql.append(" AND kategoria = ?");
            parameters.add(kategoria);
        }

        sql.append(" ORDER BY eponymia_etairias");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToPelatis(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης πελατών: " + e.getMessage());
        }
        return results;
    }

    /**
     * Έλεγχος αν υπάρχει πελάτης με συγκεκριμένο κωδικό
     */
    public boolean exists(String kodikosPelati) {
        String sql = "SELECT COUNT(*) FROM pelatis WHERE kodikos_pelati = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ελέγχου ύπαρξης πελάτη: " + e.getMessage());
        }
        return false;
    }

    /**
     * Μετατροπή ResultSet σε Pelatis object
     */
    private Pelatis mapResultSetToPelatis(ResultSet rs) throws SQLException {
        Pelatis pelatis = new Pelatis();

        // Βασικά στοιχεία
        pelatis.setKodikosPelati(rs.getString("kodikos_pelati"));
        pelatis.setKategoria(rs.getString("kategoria"));
        pelatis.setAfmNomimuEkprosopu(rs.getString("afm_nomimu_ekprosopu"));
        pelatis.setEponymiaEtairias(rs.getString("eponymia_etairias"));
        pelatis.setDiakritikosTitlos(rs.getString("diakritikos_titlos"));
        pelatis.setNomikiMorfi(rs.getString("nomiki_morfi"));
        pelatis.setEpaggelmaAntikimeno(rs.getString("epaggelma_antikimeno"));
        pelatis.setAfmEtairias(rs.getString("afm_etairias"));
        pelatis.setDouEtairias(rs.getString("dou_etairias"));
        pelatis.setNomimosEkprospos(rs.getString("nomimos_ekprospos"));

        // Επικοινωνία
        pelatis.setEmail(rs.getString("email"));
        pelatis.setTilefonoStathero(rs.getString("tilefono_stathero"));
        pelatis.setTilefonoKinito(rs.getString("tilefono_kinito"));

        // Handle timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            pelatis.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            pelatis.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return pelatis;
    }
}
package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.Apostoli;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO κλάση για την Αποστολή
 */
public class ApostoliDAO {

    private final Connection connection;

    public ApostoliDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    /**
     * Δημιουργία νέας αποστολής
     */
    public boolean create(Apostoli apostoli) {
        String sql = """
        INSERT INTO apostoles (
              kodikos_pelati, courier, arithmos_apostolis, arithmos_paraggelias,
              imerominia_paralabis, antikatavoli, paraliptis, xora, poli,
              diefthinsi, tk_paralipti, tilefono_stathero, tilefono_kinito, istoriko,
              status_apostolis, sxolia, status_mypms, imerominia_paradosis,
              delivery_flag, returned_flag, non_delivery_reason_code, shipment_status, delivery_info,status_locked
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, apostoli.getKodikosPelati());
            stmt.setString(2, apostoli.getCourier());
            stmt.setString(3, apostoli.getArithmosApostolis());
            stmt.setString(4, apostoli.getArithmosParaggelias());
            stmt.setDate(5, apostoli.getImerominiaParalabis() != null ?
                    Date.valueOf(apostoli.getImerominiaParalabis()) : null);
            stmt.setBigDecimal(6, apostoli.getAntikatavoli());
            stmt.setString(7, apostoli.getParaliptis());
            stmt.setString(8, apostoli.getXora());
            stmt.setString(9, apostoli.getPoli());
            stmt.setString(10, apostoli.getDiefthinsi());
            stmt.setString(11, apostoli.getTkParalipti());
            stmt.setString(12, apostoli.getTilefonoStathero());
            stmt.setString(13, apostoli.getTilefonoKinito());
            stmt.setString(14, apostoli.getIstoriko());
            stmt.setString(15, apostoli.getStatusApostolis());
            stmt.setString(16, apostoli.getSxolia());
            stmt.setString(17, apostoli.getStatusMypms());
            stmt.setDate(18, apostoli.getImerominiaParadosis() != null ?
                    Date.valueOf(apostoli.getImerominiaParadosis()) : null);
            stmt.setInt(19, apostoli.getDeliveryFlag() != null ? apostoli.getDeliveryFlag() : 0);
            stmt.setInt(20, apostoli.getReturnedFlag() != null ? apostoli.getReturnedFlag() : 0);
            stmt.setString(21, apostoli.getNonDeliveryReasonCode());
            stmt.setObject(22, apostoli.getShipmentStatus());
            stmt.setString(23, apostoli.getDeliveryInfo());
            stmt.setBoolean(24, apostoli.getStatusLocked() != null ? apostoli.getStatusLocked() : false);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    apostoli.setIdApostolis(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα δημιουργίας αποστολής: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ανάκτηση αποστολής βάσει ID
     */
    public Apostoli findById(int idApostolis) {
        String sql = "SELECT * FROM apostoles WHERE id_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToApostoli(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποστολής: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ανάκτηση αποστολής βάσει αριθμού αποστολής
     */
    public Apostoli findByArithmosApostolis(String arithmosApostolis) {
        String sql = "SELECT * FROM apostoles WHERE arithmos_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToApostoli(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποστολής: " + e.getMessage());
        }
        return null;
    }

    /**
     * Ανάκτηση όλων των αποστολών
     */
    public List<Apostoli> findAll() {
        List<Apostoli> apostolesList = new ArrayList<>();
        String sql = "SELECT * FROM apostoles ORDER BY imerominia_paralabis DESC, created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                apostolesList.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποστολών: " + e.getMessage());
        }
        return apostolesList;
    }

    /**
     * Ανάκτηση αποστολών βάσει κωδικού πελάτη
     */
    public List<Apostoli> findByPelatis(String kodikosPelati) {
        List<Apostoli> apostolesList = new ArrayList<>();
        String sql = "SELECT * FROM apostoles WHERE kodikos_pelati = ? ORDER BY imerominia_paralabis DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                apostolesList.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποστολών πελάτη: " + e.getMessage());
        }
        return apostolesList;
    }

    /**
     * Ενημέρωση αποστολής
     */
    public boolean update(Apostoli apostoli) {
        String sql = """
            UPDATE apostoles SET 
                kodikos_pelati = ?, courier = ?, arithmos_apostolis = ?,
                arithmos_paraggelias = ?, imerominia_paralabis = ?,
                antikatavoli = ?, paraliptis = ?, xora = ?, poli = ?,
                diefthinsi = ?, tk_paralipti = ?, tilefono_stathero = ?, tilefono_kinito = ?,
                istoriko = ?, status_apostolis = ?, sxolia = ?, status_mypms = ?,
                imerominia_paradosis = ?, delivery_flag = ?, returned_flag = ?, 
                non_delivery_reason_code = ?, shipment_status = ?, delivery_info = ?,
                status_locked = ?
            WHERE id_apostolis = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, apostoli.getKodikosPelati());
            stmt.setString(2, apostoli.getCourier());
            stmt.setString(3, apostoli.getArithmosApostolis());
            stmt.setString(4, apostoli.getArithmosParaggelias());
            stmt.setDate(5, apostoli.getImerominiaParalabis() != null ?
                    Date.valueOf(apostoli.getImerominiaParalabis()) : null);
            stmt.setBigDecimal(6, apostoli.getAntikatavoli());
            stmt.setString(7, apostoli.getParaliptis());
            stmt.setString(8, apostoli.getXora());
            stmt.setString(9, apostoli.getPoli());
            stmt.setString(10, apostoli.getDiefthinsi());
            stmt.setString(11, apostoli.getTkParalipti());
            stmt.setString(12, apostoli.getTilefonoStathero());
            stmt.setString(13, apostoli.getTilefonoKinito());
            stmt.setString(14, apostoli.getIstoriko());
            stmt.setString(15, apostoli.getStatusApostolis());
            stmt.setString(16, apostoli.getSxolia());
            stmt.setString(17, apostoli.getStatusMypms());
            stmt.setDate(18, apostoli.getImerominiaParadosis() != null ?
                    Date.valueOf(apostoli.getImerominiaParadosis()) : null);
            stmt.setInt(19, apostoli.getDeliveryFlag() != null ? apostoli.getDeliveryFlag() : 0);
            stmt.setInt(20, apostoli.getReturnedFlag() != null ? apostoli.getReturnedFlag() : 0);
            stmt.setString(21, apostoli.getNonDeliveryReasonCode());
            stmt.setObject(22, apostoli.getShipmentStatus());
            stmt.setString(23, apostoli.getDeliveryInfo());
            stmt.setBoolean(24, apostoli.getStatusLocked() != null ? apostoli.getStatusLocked() : false);
            stmt.setInt(25, apostoli.getIdApostolis());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης αποστολής: " + e.getMessage());
            return false;
        }
    }

    /**
     * Διαγραφή αποστολής
     */
    public boolean delete(int idApostolis) {
        String sql = "DELETE FROM apostoles WHERE id_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα διαγραφής αποστολής: " + e.getMessage());
            return false;
        }
    }

    /**
     * Αναζήτηση αποστολών με φίλτρα
     */
    public List<Apostoli> search(String searchTerm, String courier, String status,
                                 LocalDate fromDate, LocalDate toDate) {
        List<Apostoli> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM apostoles WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (arithmos_apostolis LIKE ? OR arithmos_paraggelias LIKE ? OR paraliptis LIKE ? OR poli LIKE ? OR tilefono_stathero LIKE ? OR tilefono_kinito LIKE ?)");
            String searchPattern = "%" + searchTerm.trim() + "%";
            parameters.add(searchPattern);  // arithmos_apostolis
            parameters.add(searchPattern);  // arithmos_paraggelias
            parameters.add(searchPattern);  // paraliptis
            parameters.add(searchPattern);  // poli
            parameters.add(searchPattern);  // tilefono_stathero
            parameters.add(searchPattern);  // tilefono_kinito
        }

        if (courier != null && !courier.trim().isEmpty() && !courier.equals("όλα")) {
            sql.append(" AND courier = ?");
            parameters.add(courier);
        }

        if (status != null && !status.trim().isEmpty() && !status.equals("όλα")) {
            sql.append(" AND status_apostolis = ?");
            parameters.add(status);
        }

        if (fromDate != null) {
            sql.append(" AND imerominia_paralabis >= ?");
            parameters.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append(" AND imerominia_paralabis <= ?");
            parameters.add(Date.valueOf(toDate));
        }

        sql.append(" ORDER BY imerominia_paralabis DESC, created_at DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης αποστολών: " + e.getMessage());
        }
        return results;
    }

    /**
     * Έλεγχος αν υπάρχει αποστολή με συγκεκριμένο αριθμό
     */
    public boolean exists(String arithmosApostolis) {
        String sql = "SELECT COUNT(*) FROM apostoles WHERE arithmos_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ελέγχου ύπαρξης αποστολής: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ανάκτηση αποστολών ACS για ενημέρωση tracking
     */
    public List<Apostoli> findACSShipmentsForTracking() {
        String sql = "SELECT * FROM apostoles WHERE courier = 'ACS' AND status_locked = FALSE AND status_apostolis NOT IN ('ΠΑΡΑΔΟΘΗΚΕ', 'ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ', 'ΑΚΥΡΩΘΗΚΕ')";
        List<Apostoli> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης ACS αποστολών: " + e.getMessage());
        }
        return results;
    }

    /**
     * Ενημέρωση tracking δεδομένων από ACS API
     */
    public boolean updateTrackingData(String arithmosApostolis, Integer deliveryFlag,
                                      Integer returnedFlag, String nonDeliveryReasonCode,
                                      Integer shipmentStatus, String deliveryInfo, String newStatus,
                                      String acsStationDestination) {
        String sql = """
            UPDATE apostoles SET 
                delivery_flag = ?, returned_flag = ?, non_delivery_reason_code = ?,
                shipment_status = ?, delivery_info = ?, status_apostolis = ?,
                poli = COALESCE(NULLIF(?, ''), poli)
                WHERE arithmos_apostolis = ? AND status_locked = FALSE
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, deliveryFlag != null ? deliveryFlag : 0);
            stmt.setInt(2, returnedFlag != null ? returnedFlag : 0);
            stmt.setString(3, nonDeliveryReasonCode);
            stmt.setInt(4, shipmentStatus != null ? shipmentStatus : 0);
            stmt.setString(5, deliveryInfo);
            stmt.setString(6, newStatus);
            stmt.setString(7, acsStationDestination); // ΝΕΑ ΠΑΡΑΜΕΤΡΟΣ
            stmt.setString(8, arithmosApostolis);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης tracking δεδομένων: " + e.getMessage());
            return false;
        }
    }
    /**
     * Μετατροπή ResultSet σε Apostoli object
     */
    private Apostoli mapResultSetToApostoli(ResultSet rs) throws SQLException {
        Apostoli apostoli = new Apostoli();
        apostoli.setIdApostolis(rs.getInt("id_apostolis"));
        apostoli.setKodikosPelati(rs.getString("kodikos_pelati"));
        apostoli.setCourier(rs.getString("courier"));
        apostoli.setArithmosApostolis(rs.getString("arithmos_apostolis"));
        apostoli.setArithmosParaggelias(rs.getString("arithmos_paraggelias"));

        Date paralabisDate = rs.getDate("imerominia_paralabis");
        if (paralabisDate != null) {
            apostoli.setImerominiaParalabis(paralabisDate.toLocalDate());
        }

        Date paradosisDate = rs.getDate("imerominia_paradosis");
        if (paradosisDate != null) {
            apostoli.setImerominiaParadosis(paradosisDate.toLocalDate());
        }

        apostoli.setAntikatavoli(rs.getBigDecimal("antikatavoli"));
        apostoli.setParaliptis(rs.getString("paraliptis"));
        apostoli.setXora(rs.getString("xora"));
        apostoli.setPoli(rs.getString("poli"));
        apostoli.setDiefthinsi(rs.getString("diefthinsi"));
        apostoli.setTkParalipti(rs.getString("tk_paralipti"));
        apostoli.setTilefonoStathero(rs.getString("tilefono_stathero"));
        apostoli.setTilefonoKinito(rs.getString("tilefono_kinito"));
        apostoli.setIstoriko(rs.getString("istoriko"));
        apostoli.setStatusApostolis(rs.getString("status_apostolis"));
        apostoli.setStatusMypms(rs.getString("status_mypms"));
        apostoli.setSxolia(rs.getString("sxolia"));
        apostoli.setDeliveryFlag(rs.getInt("delivery_flag"));
        apostoli.setReturnedFlag(rs.getInt("returned_flag"));
        apostoli.setNonDeliveryReasonCode(rs.getString("non_delivery_reason_code"));
        apostoli.setShipmentStatus(rs.getObject("shipment_status", Integer.class));
        apostoli.setDeliveryInfo(rs.getString("delivery_info"));
        apostoli.setStatusLocked(rs.getBoolean("status_locked"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            apostoli.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            apostoli.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return apostoli;
    }
}
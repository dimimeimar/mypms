package org.pms.shipments.dao;

import org.pms.config.DatabaseConfig;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.TrackingDetail;
import org.pms.shipments.service.TrackingStatusFilter;

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
              imerominia_paralabis, imerominia_ekdosis, imerominia_anaxorisis, antikatavoli, paraliptis, xora, poli,
              diefthinsi, tk_paralipti, tilefono_stathero, tilefono_kinito, istoriko,
              status_apostolis, status_details, sxolia, status_mypms, imerominia_paradosis,
              delivery_flag, returned_flag, non_delivery_reason_code, shipment_status, delivery_info, status_locked
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, apostoli.getKodikosPelati());
            stmt.setString(2, apostoli.getCourier());
            stmt.setString(3, apostoli.getArithmosApostolis());
            stmt.setString(4, apostoli.getArithmosParaggelias());
            stmt.setDate(5, apostoli.getImerominiaParalabis() != null ?
                    Date.valueOf(apostoli.getImerominiaParalabis()) : null);
            stmt.setDate(6, apostoli.getImerominiaEkdosis() != null ?
                    Date.valueOf(apostoli.getImerominiaEkdosis()) : null);
            stmt.setDate(7, apostoli.getImerominiaAnaxorisis() != null ?
                    Date.valueOf(apostoli.getImerominiaAnaxorisis()) : null);
            stmt.setBigDecimal(8, apostoli.getAntikatavoli());
            stmt.setString(9, apostoli.getParaliptis());
            stmt.setString(10, apostoli.getXora());
            stmt.setString(11, apostoli.getPoli());
            stmt.setString(12, apostoli.getDiefthinsi());
            stmt.setString(13, apostoli.getTkParalipti());
            stmt.setString(14, apostoli.getTilefonoStathero());
            stmt.setString(15, apostoli.getTilefonoKinito());
            stmt.setString(16, apostoli.getIstoriko());
            stmt.setString(17, apostoli.getStatusApostolis());
            stmt.setString(18, apostoli.getStatusDetails());
            stmt.setString(19, apostoli.getSxolia());
            stmt.setString(20, apostoli.getStatusMypms());
            stmt.setDate(21, apostoli.getImerominiaParadosis() != null ?
                    Date.valueOf(apostoli.getImerominiaParadosis()) : null);
            stmt.setInt(22, apostoli.getDeliveryFlag() != null ? apostoli.getDeliveryFlag() : 0);
            stmt.setInt(23, apostoli.getReturnedFlag() != null ? apostoli.getReturnedFlag() : 0);
            stmt.setString(24, apostoli.getNonDeliveryReasonCode());
            stmt.setObject(25, apostoli.getShipmentStatus());
            stmt.setString(26, apostoli.getDeliveryInfo());
            stmt.setBoolean(27, apostoli.getStatusLocked() != null ? apostoli.getStatusLocked() : false);

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
            arithmos_paraggelias = ?, imerominia_paralabis = ?, imerominia_anaxorisis = ?,
            antikatavoli = ?, paraliptis = ?, xora = ?, poli = ?,
            diefthinsi = ?, tk_paralipti = ?, tilefono_stathero = ?, tilefono_kinito = ?,
            istoriko = ?, status_apostolis = ?, status_details = ?, sxolia = ?, status_mypms = ?,
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
            stmt.setDate(6, apostoli.getImerominiaAnaxorisis() != null ?
                    Date.valueOf(apostoli.getImerominiaAnaxorisis()) : null);
            stmt.setBigDecimal(7, apostoli.getAntikatavoli());
            stmt.setString(8, apostoli.getParaliptis());
            stmt.setString(9, apostoli.getXora());
            stmt.setString(10, apostoli.getPoli());
            stmt.setString(11, apostoli.getDiefthinsi());
            stmt.setString(12, apostoli.getTkParalipti());
            stmt.setString(13, apostoli.getTilefonoStathero());
            stmt.setString(14, apostoli.getTilefonoKinito());
            stmt.setString(15, apostoli.getIstoriko());
            stmt.setString(16, apostoli.getStatusApostolis());
            stmt.setString(17, apostoli.getStatusDetails());
            stmt.setString(18, apostoli.getSxolia());
            stmt.setString(19, apostoli.getStatusMypms());
            stmt.setDate(20, apostoli.getImerominiaParadosis() != null ?
                    Date.valueOf(apostoli.getImerominiaParadosis()) : null);
            stmt.setInt(21, apostoli.getDeliveryFlag() != null ? apostoli.getDeliveryFlag() : 0);
            stmt.setInt(22, apostoli.getReturnedFlag() != null ? apostoli.getReturnedFlag() : 0);
            stmt.setString(23, apostoli.getNonDeliveryReasonCode());
            stmt.setObject(24, apostoli.getShipmentStatus());
            stmt.setString(25, apostoli.getDeliveryInfo());
            stmt.setBoolean(26, apostoli.getStatusLocked() != null ? apostoli.getStatusLocked() : false);
            stmt.setInt(27, apostoli.getIdApostolis());

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                markAsUnsynced(apostoli.getIdApostolis());
            }
            return success;
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

    public List<Apostoli> findACSShipmentsForTrackingDetails() {
        String sql = "SELECT * FROM apostoles WHERE courier = 'ACS' AND status_locked = FALSE";
        List<Apostoli> allResults = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                allResults.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης ACS αποστολών για tracking details: " + e.getMessage());
            return allResults;
        }

        List<Apostoli> filteredResults = new ArrayList<>();
        TrackingDetailDAO trackingDetailDAO = new TrackingDetailDAO();

        for (Apostoli apostoli : allResults) {
            List<TrackingDetail> details = trackingDetailDAO.findByArithmosApostolis(apostoli.getArithmosApostolis());
            String filteredStatus = TrackingStatusFilter.getLatestFilteredStatus(details);

            if (filteredStatus == null ||
                    (!filteredStatus.equals("ΠΑΡΑΔΟΘΗΚΕ") &&
                            !filteredStatus.equals("ΕΠΙΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ") &&
                            !filteredStatus.equals("ΕΠΙΣΤΡΑΦΗΚΕ") &&
                            !filteredStatus.equals("ΑΚΥΡΩΘΗΚΕ"))) {
                filteredResults.add(apostoli);
            }
        }

        return filteredResults;
    }

    /**
     * Ενημέρωση tracking δεδομένων από ACS API
     */
    public boolean updateTrackingData(String arithmosApostolis, Integer deliveryFlag,
                                      Integer returnedFlag, String nonDeliveryReasonCode,
                                      Integer shipmentStatus, String deliveryInfo, String newStatus,
                                      String acsStationDestination) {

        Apostoli currentApostoli = findByArithmosApostolis(arithmosApostolis);
        if (currentApostoli == null) {
            return false;
        }

        String oldStatus = currentApostoli.getStatusApostolis();
        boolean wasInactive = oldStatus == null || oldStatus.equals("-") ||
                oldStatus.equals("ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ") ||
                oldStatus.isEmpty();
        boolean isNowActive = newStatus != null && !newStatus.equals("-") &&
                !newStatus.equals("ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ") &&
                !newStatus.isEmpty();

        boolean shouldSetDepartureDate = wasInactive && isNowActive && currentApostoli.getImerominiaAnaxorisis() == null;

        String sql = """
        UPDATE apostoles SET 
            delivery_flag = ?, returned_flag = ?, non_delivery_reason_code = ?,
            shipment_status = ?, delivery_info = ?, status_apostolis = ?,
            poli = COALESCE(NULLIF(?, ''), poli)
            WHERE arithmos_apostolis = ? AND status_locked = FALSE
    """;

        if (shouldSetDepartureDate) {
            sql = """
            UPDATE apostoles SET 
                delivery_flag = ?, returned_flag = ?, non_delivery_reason_code = ?,
                shipment_status = ?, delivery_info = ?, status_apostolis = ?,
                poli = COALESCE(NULLIF(?, ''), poli),
                imerominia_anaxorisis = CURDATE()
                WHERE arithmos_apostolis = ? AND status_locked = FALSE
        """;
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, deliveryFlag != null ? deliveryFlag : 0);
            stmt.setInt(2, returnedFlag != null ? returnedFlag : 0);
            stmt.setString(3, nonDeliveryReasonCode);
            stmt.setInt(4, shipmentStatus != null ? shipmentStatus : 0);
            stmt.setString(5, deliveryInfo);
            stmt.setString(6, newStatus);
            stmt.setString(7, acsStationDestination);
            stmt.setString(8, arithmosApostolis);

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                Apostoli apostoli = findByArithmosApostolis(arithmosApostolis);
                if (apostoli != null) {
                    markAsUnsynced(apostoli.getIdApostolis());
                }

                if (shouldSetDepartureDate) {
                    System.out.println("✓ Ημερομηνία αναχώρησης ορίστηκε για: " + arithmosApostolis);
                }
            }
            return success;
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

        apostoli.setImerominiaEkdosis(rs.getDate("imerominia_ekdosis") != null ?
                rs.getDate("imerominia_ekdosis").toLocalDate() : null);

        Date anaxorisisDate = rs.getDate("imerominia_anaxorisis");
        if (anaxorisisDate != null) {
            apostoli.setImerominiaAnaxorisis(anaxorisisDate.toLocalDate());
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
        apostoli.setStatusDetails(rs.getString("status_details"));
        apostoli.setStatusMypms(rs.getString("status_mypms"));
        apostoli.setSxolia(rs.getString("sxolia"));
        apostoli.setDeliveryFlag(rs.getInt("delivery_flag"));
        apostoli.setReturnedFlag(rs.getInt("returned_flag"));
        apostoli.setNonDeliveryReasonCode(rs.getString("non_delivery_reason_code"));
        apostoli.setShipmentStatus(rs.getObject("shipment_status", Integer.class));
        apostoli.setDeliveryInfo(rs.getString("delivery_info"));
        apostoli.setStatusLocked(rs.getBoolean("status_locked"));
        apostoli.setSynced(rs.getBoolean("synced"));

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

    public boolean existsByArithmosApostolis(String arithmosApostolis) {
        String sql = "SELECT COUNT(*) FROM apostoles WHERE arithmos_apostolis = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public TrackingDetailDAO getTrackingDetailDAO() {
        return new TrackingDetailDAO();
    }

    /**
     * Ενημέρωση για αποστολή που δε βρέθηκε στο tracking
     */
    /**
     * Ενημέρωση για αποστολή που δε βρέθηκε στο tracking - μόνο ημερομηνία έκδοσης
     */
    public boolean updateShipmentNotFound(String arithmosApostolis) {
        String sql = """
        UPDATE apostoles SET 
            imerominia_ekdosis = ?
            WHERE arithmos_apostolis = ? AND status_locked = FALSE
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Ειδική ημερομηνία για "ΜΗ ΔΙΑΘΕΣΙΜΗ ΗΜ"
            stmt.setDate(1, Date.valueOf(LocalDate.of(1900, 1, 1)));
            stmt.setString(2, arithmosApostolis);

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                Apostoli apostoli = findByArithmosApostolis(arithmosApostolis);
                if (apostoli != null) {
                    markAsUnsynced(apostoli.getIdApostolis());
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης αποστολής που δε βρέθηκε: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ανάκτηση αποστολών βάσει κωδικού πελάτη
     */
    public List<Apostoli> findByKodikosPelati(String kodikosPelati) {
        List<Apostoli> results = new ArrayList<>();
        String sql = "SELECT * FROM apostoles WHERE kodikos_pelati = ? ORDER BY imerominia_paralabis DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αποστολών πελάτη: " + e.getMessage());
        }

        return results;
    }

    public List<Apostoli> findUnsyncedApostoli() {
        List<Apostoli> apostoles = new ArrayList<>();
        String sql = "SELECT * FROM apostoles WHERE synced = 0 ORDER BY id_apostolis";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                apostoles.add(mapResultSetToApostoli(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης μη συγχρονισμένων αποστολών: " + e.getMessage());
        }

        return apostoles;
    }

    public boolean markAsSynced(int idApostolis) {
        String sql = "UPDATE apostoles SET synced = 1 WHERE id_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα markAsSynced: " + e.getMessage());
            return false;
        }
    }

    public boolean markAsUnsynced(int idApostolis) {
        String sql = "UPDATE apostoles SET synced = 0 WHERE id_apostolis = ?";
        System.out.println("Marking unsynced " + idApostolis);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idApostolis);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα markAsUnsynced: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatusToCancelled(String arithmosApostolis) {
        String sql = """
        UPDATE apostoles 
        SET status_apostolis = 'ΑΚΥΡΩΘΗΚΕ'
        WHERE arithmos_apostolis = ? AND status_locked = FALSE
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης status σε ΑΚΥΡΩΘΗΚΕ: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatusDetails(String arithmosApostolis, String statusDetails) {
        Apostoli apostoli = findByArithmosApostolis(arithmosApostolis);
        if (apostoli == null) {
            return false;
        }

        String oldStatusDetails = apostoli.getStatusDetails();

        String sql = """
            UPDATE apostoles
            SET status_details = ?
            WHERE arithmos_apostolis = ? AND status_locked = FALSE
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, statusDetails);
            stmt.setString(2, arithmosApostolis);
            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                boolean hasChanged = (oldStatusDetails == null && statusDetails != null) ||
                        (oldStatusDetails != null && !oldStatusDetails.equals(statusDetails));
                System.out.println("old :" + oldStatusDetails + " , new :" + statusDetails);

                if (hasChanged) {
                    markAsUnsynced(apostoli.getIdApostolis());
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Σφάλμα ενημέρωσης status details: " + e.getMessage());
            return false;
        }
    }
}
package org.pms.antikatavoles.dao;

import org.pms.config.DatabaseConfig;
import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.shipments.dao.TrackingDetailDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntikatavoliFullViewDAO {
    private final Connection connection;


    public AntikatavoliFullViewDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public List<AntikatavoliFullView> findAll() {
        String sql = """
            SELECT 
                ak.id,
                ak.id_apodosis,
                ak.apodothike,
                ak.imerominia_apodosis,
                ak.sxolia,
                ak.parastatikoACS,
                ak.parastatikoMyPMS,
                ap.id_apostolis,
                ap.kodikos_pelati,
                ap.arithmos_apostolis,
                ap.arithmos_paraggelias,
                ap.imerominia_paralabis,
                ap.imerominia_paradosis,
                ap.courier,
                ap.antikatavoli,
                ap.paraliptis,
                p.eponymia_etairias
            FROM apostoles ap
            LEFT JOIN antikatavoles ak ON ap.id_apostolis = ak.id_apostolis
            LEFT JOIN pelatis p ON ap.kodikos_pelati = p.kodikos_pelati
            WHERE ap.antikatavoli > 0
            ORDER BY ap.imerominia_paralabis DESC
        """;

        List<AntikatavoliFullView> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToFullView(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αντικαταβολών: " + e.getMessage());
        }
        return results;
    }

    public List<AntikatavoliFullView> findByKodikosPelati(String kodikosPelati) {
        String sql = """
            SELECT 
                ak.id,
                ak.id_apodosis,
                ak.apodothike,
                ak.imerominia_apodosis,
                ak.sxolia,
                ak.parastatikoACS,
                ak.parastatikoMyPMS,
                ap.id_apostolis,
                ap.kodikos_pelati,
                ap.arithmos_apostolis,
                ap.arithmos_paraggelias,
                ap.imerominia_paralabis,
                ap.imerominia_paradosis,
                ap.courier,
                ap.antikatavoli,
                ap.paraliptis,
                p.eponymia_etairias
            FROM apostoles ap
            LEFT JOIN antikatavoles ak ON ap.id_apostolis = ak.id_apostolis
            LEFT JOIN pelatis p ON ap.kodikos_pelati = p.kodikos_pelati
            WHERE ap.antikatavoli > 0 AND ap.kodikos_pelati = ?
            ORDER BY ap.imerominia_paralabis DESC
        """;

        List<AntikatavoliFullView> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodikosPelati);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToFullView(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης αντικαταβολών πελάτη: " + e.getMessage());
        }
        return results;
    }

    public List<AntikatavoliFullView> search(String searchTerm, String kodikosPelati,
                                             Boolean apodothike, LocalDate fromDate, LocalDate toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                ak.id,
                ak.id_apodosis,
                ak.apodothike,
                ak.imerominia_apodosis,
                ak.sxolia,
                ak.parastatikoACS,
                ak.parastatikoMyPMS,
                ap.id_apostolis,
                ap.kodikos_pelati,
                ap.arithmos_apostolis,
                ap.arithmos_paraggelias,
                ap.imerominia_paralabis,
                ap.imerominia_paradosis,
                ap.courier,
                ap.antikatavoli,
                ap.paraliptis,
                p.eponymia_etairias
            FROM apostoles ap
            LEFT JOIN antikatavoles ak ON ap.id_apostolis = ak.id_apostolis
            LEFT JOIN pelatis p ON ap.kodikos_pelati = p.kodikos_pelati
            WHERE ap.antikatavoli > 0
        """);

        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (ap.arithmos_apostolis LIKE ? OR ap.arithmos_paraggelias LIKE ? OR ap.paraliptis LIKE ?)");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (kodikosPelati != null && !kodikosPelati.isEmpty() && !kodikosPelati.equals("ΟΛΟΙ")) {
            sql.append(" AND ap.kodikos_pelati = ?");
            params.add(kodikosPelati);
        }

        if (apodothike != null) {
            sql.append(" AND ak.apodothike = ?");
            params.add(apodothike);
        }

        if (fromDate != null) {
            sql.append(" AND ap.imerominia_paradosis >= ?");
            params.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append(" AND ap.imerominia_paradosis <= ?");
            params.add(Date.valueOf(toDate));
        }

        sql.append(" ORDER BY ap.imerominia_paralabis DESC");

        List<AntikatavoliFullView> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToFullView(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης αντικαταβολών: " + e.getMessage());
        }

        return results;
    }

    private AntikatavoliFullView mapResultSetToFullView(ResultSet rs) throws SQLException {
        AntikatavoliFullView view = new AntikatavoliFullView();

        view.setId(rs.getObject("id", Integer.class));
        view.setIdApodosis(rs.getObject("id_apodosis", Integer.class));
        view.setIdApostolis(rs.getInt("id_apostolis"));
        view.setKodikosPelati(rs.getString("kodikos_pelati"));
        view.setEponimiaEtairias(rs.getString("eponymia_etairias"));
        view.setApodothike(rs.getObject("apodothike") != null ? rs.getBoolean("apodothike") : false);

        Date apodosisDate = rs.getDate("imerominia_apodosis");
        if (apodosisDate != null) {
            view.setImerominiaApodosis(apodosisDate.toLocalDate());
        }

        view.setSxolia(rs.getString("sxolia"));
        view.setParastatikoACS(rs.getString("parastatikoACS"));
        view.setParastatikoMyPMS(rs.getString("parastatikoMyPMS"));
        view.setArithmosApostolis(rs.getString("arithmos_apostolis"));
        view.setArithmosParaggelias(rs.getString("arithmos_paraggelias"));

        Date paralabisDate = rs.getDate("imerominia_paralabis");
        if (paralabisDate != null) {
            view.setImerominiaParalabis(paralabisDate.toLocalDate());
        }

        TrackingDetailDAO trackingDetailDAO = new TrackingDetailDAO();
        List<org.pms.shipments.model.TrackingDetail> trackingDetails =
                trackingDetailDAO.findByArithmosApostolis(rs.getString("arithmos_apostolis"));

        LocalDate pragmatikiParadosi = null;
        for (org.pms.shipments.model.TrackingDetail detail : trackingDetails) {
            String action = detail.getCheckpointAction();
            if (action != null && action.trim().toUpperCase().startsWith("ΠΑΡΑΔΟΣΗ")
                    && !action.trim().toUpperCase().startsWith("ΜΗ ΠΑΡΑΔΟΣΗ")) {
                pragmatikiParadosi = detail.getCheckpointDateTime().toLocalDate();
                break;
            }
        }

        if (pragmatikiParadosi != null) {
            view.setImerominiaParadosis(pragmatikiParadosi);
        } else {
            Date paradosisDate = rs.getDate("imerominia_paradosis");
            if (paradosisDate != null) {
                view.setImerominiaParadosis(paradosisDate.toLocalDate());
            }
        }

        view.setCourier(rs.getString("courier"));
        view.setAntikatavoli(rs.getBigDecimal("antikatavoli"));
        view.setParaliptis(rs.getString("paraliptis"));

        return view;
    }

    public List<AntikatavoliFullView> findProApodosi() {
        String sql = """
        SELECT 
            ak.id,
            ak.id_apodosis,
            ak.apodothike,
            ak.imerominia_apodosis,
            ak.sxolia,
            ak.parastatikoACS,
            ak.parastatikoMyPMS,
            ap.id_apostolis,
            ap.kodikos_pelati,
            ap.arithmos_apostolis,
            ap.arithmos_paraggelias,
            ap.imerominia_paralabis,
            ap.imerominia_paradosis,
            ap.courier,
            ap.antikatavoli,
            ap.paraliptis,
            p.eponymia_etairias
        FROM apostoles ap
        INNER JOIN antikatavoles ak ON ap.id_apostolis = ak.id_apostolis
        LEFT JOIN pelatis p ON ap.kodikos_pelati = p.kodikos_pelati
        WHERE ap.antikatavoli > 0 
        AND ak.id_apodosis IS NOT NULL 
        AND ak.apodothike = FALSE
        ORDER BY ap.kodikos_pelati, ap.imerominia_paralabis DESC
    """;

        List<AntikatavoliFullView> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToFullView(rs));
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα ανάκτησης προς απόδοση αντικαταβολών: " + e.getMessage());
        }
        return results;
    }

}
package org.pms.shipments.dao;

import org.pms.config.DatabaseConfig;
import org.pms.shipments.model.ACSSummary;

import java.sql.*;


public class ACSSummaryDAO {

    private final Connection connection;

    public ACSSummaryDAO() {
        this.connection = DatabaseConfig.getInstance().getConnection();
    }

    public boolean saveOrUpdate(ACSSummary summary) {
        String sql = """
            INSERT INTO apostoles_acs_summary 
            (arithmos_apostolis, voucher_no, acs_station_origin, acs_station_origin_descr,
             acs_station_destination, acs_station_destination_descr, pickup_date, delivery_flag,
             returned_flag, delivery_date, consignee, non_delivery_reason_code,
             delivery_date_expected, delivery_info, sender, recipient, recipient_address,
             shipment_status, phone_acs_station_origin, phone_acs_station_destination)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
             voucher_no = VALUES(voucher_no),
             acs_station_origin = VALUES(acs_station_origin),
             acs_station_origin_descr = VALUES(acs_station_origin_descr),
             acs_station_destination = VALUES(acs_station_destination),
             acs_station_destination_descr = VALUES(acs_station_destination_descr),
             pickup_date = VALUES(pickup_date),
             delivery_flag = VALUES(delivery_flag),
             returned_flag = VALUES(returned_flag),
             delivery_date = VALUES(delivery_date),
             consignee = VALUES(consignee),
             non_delivery_reason_code = VALUES(non_delivery_reason_code),
             delivery_date_expected = VALUES(delivery_date_expected),
             delivery_info = VALUES(delivery_info),
             sender = VALUES(sender),
             recipient = VALUES(recipient),
             recipient_address = VALUES(recipient_address),
             shipment_status = VALUES(shipment_status),
             phone_acs_station_origin = VALUES(phone_acs_station_origin),
             phone_acs_station_destination = VALUES(phone_acs_station_destination),
             updated_at = CURRENT_TIMESTAMP
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, summary.getArithmosApostolis());
            stmt.setString(2, summary.getVoucherNo());
            stmt.setString(3, summary.getAcsStationOrigin());
            stmt.setString(4, summary.getAcsStationOriginDescr());
            stmt.setString(5, summary.getAcsStationDestination());
            stmt.setString(6, summary.getAcsStationDestinationDescr());
            stmt.setTimestamp(7, summary.getPickupDate() != null ? Timestamp.valueOf(summary.getPickupDate()) : null);
            stmt.setInt(8, summary.getDeliveryFlag() != null ? summary.getDeliveryFlag() : 0);
            stmt.setInt(9, summary.getReturnedFlag() != null ? summary.getReturnedFlag() : 0);
            stmt.setTimestamp(10, summary.getDeliveryDate() != null ? Timestamp.valueOf(summary.getDeliveryDate()) : null);
            stmt.setString(11, summary.getConsignee());
            stmt.setString(12, summary.getNonDeliveryReasonCode());
            stmt.setTimestamp(13, summary.getDeliveryDateExpected() != null ? Timestamp.valueOf(summary.getDeliveryDateExpected()) : null);
            stmt.setString(14, summary.getDeliveryInfo());
            stmt.setString(15, summary.getSender());
            stmt.setString(16, summary.getRecipient());
            stmt.setString(17, summary.getRecipientAddress());
            stmt.setInt(18, summary.getShipmentStatus() != null ? summary.getShipmentStatus() : 0);
            stmt.setString(19, summary.getPhoneAcsStationOrigin());
            stmt.setString(20, summary.getPhoneAcsStationDestination());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Σφάλμα αποθήκευσης ACS Summary: " + e.getMessage());
            return false;
        }
    }

    public ACSSummary findByArithmosApostolis(String arithmosApostolis) {
        String sql = "SELECT * FROM apostoles_acs_summary WHERE arithmos_apostolis = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, arithmosApostolis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSummary(rs);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα αναζήτησης ACS Summary: " + e.getMessage());
        }

        return null;
    }

    private ACSSummary mapResultSetToSummary(ResultSet rs) throws SQLException {
        ACSSummary summary = new ACSSummary();

        summary.setId(rs.getInt("id"));
        summary.setArithmosApostolis(rs.getString("arithmos_apostolis"));
        summary.setVoucherNo(rs.getString("voucher_no"));
        summary.setAcsStationOrigin(rs.getString("acs_station_origin"));
        summary.setAcsStationOriginDescr(rs.getString("acs_station_origin_descr"));
        summary.setAcsStationDestination(rs.getString("acs_station_destination"));
        summary.setAcsStationDestinationDescr(rs.getString("acs_station_destination_descr"));

        Timestamp pickupTs = rs.getTimestamp("pickup_date");
        if (pickupTs != null) summary.setPickupDate(pickupTs.toLocalDateTime());

        summary.setDeliveryFlag(rs.getInt("delivery_flag"));
        summary.setReturnedFlag(rs.getInt("returned_flag"));

        Timestamp deliveryTs = rs.getTimestamp("delivery_date");
        if (deliveryTs != null) summary.setDeliveryDate(deliveryTs.toLocalDateTime());

        summary.setConsignee(rs.getString("consignee"));
        summary.setNonDeliveryReasonCode(rs.getString("non_delivery_reason_code"));

        Timestamp expectedTs = rs.getTimestamp("delivery_date_expected");
        if (expectedTs != null) summary.setDeliveryDateExpected(expectedTs.toLocalDateTime());

        summary.setDeliveryInfo(rs.getString("delivery_info"));
        summary.setSender(rs.getString("sender"));
        summary.setRecipient(rs.getString("recipient"));
        summary.setRecipientAddress(rs.getString("recipient_address"));
        summary.setShipmentStatus(rs.getInt("shipment_status"));
        summary.setPhoneAcsStationOrigin(rs.getString("phone_acs_station_origin"));
        summary.setPhoneAcsStationDestination(rs.getString("phone_acs_station_destination"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) summary.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) summary.setUpdatedAt(updatedTs.toLocalDateTime());

        return summary;
    }
}
package org.pms.shipments.model;

import java.time.LocalDateTime;

public class ACSSummary {
    private Integer id;
    private String arithmosApostolis;
    private String voucherNo;
    private String acsStationOrigin;
    private String acsStationOriginDescr;
    private String acsStationDestination;
    private String acsStationDestinationDescr;
    private LocalDateTime pickupDate;
    private Integer deliveryFlag;
    private Integer returnedFlag;
    private LocalDateTime deliveryDate;
    private String consignee;
    private String nonDeliveryReasonCode;
    private LocalDateTime deliveryDateExpected;
    private String deliveryInfo;
    private String sender;
    private String recipient;
    private String recipientAddress;
    private Integer shipmentStatus;
    private String phoneAcsStationOrigin;
    private String phoneAcsStationDestination;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ACSSummary() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getArithmosApostolis() { return arithmosApostolis; }
    public void setArithmosApostolis(String arithmosApostolis) { this.arithmosApostolis = arithmosApostolis; }

    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }

    public String getAcsStationOrigin() { return acsStationOrigin; }
    public void setAcsStationOrigin(String acsStationOrigin) { this.acsStationOrigin = acsStationOrigin; }

    public String getAcsStationOriginDescr() { return acsStationOriginDescr; }
    public void setAcsStationOriginDescr(String acsStationOriginDescr) { this.acsStationOriginDescr = acsStationOriginDescr; }

    public String getAcsStationDestination() { return acsStationDestination; }
    public void setAcsStationDestination(String acsStationDestination) { this.acsStationDestination = acsStationDestination; }

    public String getAcsStationDestinationDescr() { return acsStationDestinationDescr; }
    public void setAcsStationDestinationDescr(String acsStationDestinationDescr) { this.acsStationDestinationDescr = acsStationDestinationDescr; }

    public LocalDateTime getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDateTime pickupDate) { this.pickupDate = pickupDate; }

    public Integer getDeliveryFlag() { return deliveryFlag; }
    public void setDeliveryFlag(Integer deliveryFlag) { this.deliveryFlag = deliveryFlag; }

    public Integer getReturnedFlag() { return returnedFlag; }
    public void setReturnedFlag(Integer returnedFlag) { this.returnedFlag = returnedFlag; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getConsignee() { return consignee; }
    public void setConsignee(String consignee) { this.consignee = consignee; }

    public String getNonDeliveryReasonCode() { return nonDeliveryReasonCode; }
    public void setNonDeliveryReasonCode(String nonDeliveryReasonCode) { this.nonDeliveryReasonCode = nonDeliveryReasonCode; }

    public LocalDateTime getDeliveryDateExpected() { return deliveryDateExpected; }
    public void setDeliveryDateExpected(LocalDateTime deliveryDateExpected) { this.deliveryDateExpected = deliveryDateExpected; }

    public String getDeliveryInfo() { return deliveryInfo; }
    public void setDeliveryInfo(String deliveryInfo) { this.deliveryInfo = deliveryInfo; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }

    public Integer getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(Integer shipmentStatus) { this.shipmentStatus = shipmentStatus; }

    public String getPhoneAcsStationOrigin() { return phoneAcsStationOrigin; }
    public void setPhoneAcsStationOrigin(String phoneAcsStationOrigin) { this.phoneAcsStationOrigin = phoneAcsStationOrigin; }

    public String getPhoneAcsStationDestination() { return phoneAcsStationDestination; }
    public void setPhoneAcsStationDestination(String phoneAcsStationDestination) { this.phoneAcsStationDestination = phoneAcsStationDestination; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
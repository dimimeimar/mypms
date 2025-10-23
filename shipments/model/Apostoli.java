package org.pms.shipments.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model κλάση για την Αποστολή
 */
public class Apostoli {

    private int idApostolis;
    private String kodikosPelati;
    private String courier;
    private String arithmosApostolis;
    private String arithmosParaggelias;
    private LocalDate imerominiaParalabis;
    private LocalDate imerominiaParadosis;
    private LocalDate imerominiaEkdosis;
    private LocalDate imerominiaAnaxorisis;
    private BigDecimal antikatavoli;
    private String paraliptis;
    private String xora;
    private String poli;
    private String tkParalipti;
    private String diefthinsi;
    private String tilefonoStathero;
    private String tilefonoKinito;
    private String istoriko;
    private String statusApostolis;
    private String statusDetails;
    private String statusMypms;
    private String sxolia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deliveryFlag;
    private Integer returnedFlag;
    private String nonDeliveryReasonCode;
    private Integer shipmentStatus;
    private String deliveryInfo;
    private Boolean statusLocked = false;
    private Boolean synced;

    // Default constructor
    public Apostoli() {}

    // Constructor without id
    public Apostoli(String kodikosPelati, String courier, String arithmosApostolis,
                    String arithmosParaggelias, LocalDate imerominiaParalabis,
                    BigDecimal antikatavoli, String paraliptis, String xora,
                    String poli, String diefthinsi, String tilefonoStathero,
                    String tilefonoKinito, String istoriko, String statusApostolis,
                    String sxolia) {
        this.kodikosPelati = kodikosPelati;
        this.courier = courier;
        this.arithmosApostolis = arithmosApostolis;
        this.arithmosParaggelias = arithmosParaggelias;
        this.imerominiaParalabis = imerominiaParalabis;
        this.antikatavoli = antikatavoli;
        this.paraliptis = paraliptis;
        this.xora = xora;
        this.poli = poli;
        this.diefthinsi = diefthinsi;
        this.tilefonoStathero = tilefonoStathero;
        this.tilefonoKinito = tilefonoKinito;
        this.istoriko = istoriko;
        this.statusApostolis = statusApostolis;
        this.sxolia = sxolia;
    }

    // Getters and Setters
    public int getIdApostolis() { return idApostolis; }
    public void setIdApostolis(int idApostolis) { this.idApostolis = idApostolis; }

    public String getKodikosPelati() { return kodikosPelati; }
    public void setKodikosPelati(String kodikosPelati) { this.kodikosPelati = kodikosPelati; }

    public String getCourier() { return courier; }
    public void setCourier(String courier) { this.courier = courier; }

    public String getArithmosApostolis() { return arithmosApostolis; }
    public void setArithmosApostolis(String arithmosApostolis) { this.arithmosApostolis = arithmosApostolis; }

    public String getArithmosParaggelias() { return arithmosParaggelias; }
    public void setArithmosParaggelias(String arithmosParaggelias) { this.arithmosParaggelias = arithmosParaggelias; }

    public LocalDate getImerominiaParalabis() { return imerominiaParalabis; }
    public void setImerominiaParalabis(LocalDate imerominiaParalabis) { this.imerominiaParalabis = imerominiaParalabis; }


    public LocalDate getImerominiaEkdosis() { return imerominiaEkdosis; }
    public void setImerominiaEkdosis(LocalDate imerominiaEkdosis) { this.imerominiaEkdosis = imerominiaEkdosis; }
    // Getter
    public LocalDate getImerominiaParadosis() {
        return imerominiaParadosis;
    }

    // Setter
    public void setImerominiaParadosis(LocalDate imerominiaParadosis) {
        this.imerominiaParadosis = imerominiaParadosis;
    }

    public BigDecimal getAntikatavoli() { return antikatavoli; }
    public void setAntikatavoli(BigDecimal antikatavolos) { this.antikatavoli = antikatavolos; }

    public String getParaliptis() { return paraliptis; }
    public void setParaliptis(String paraliptis) { this.paraliptis = paraliptis; }

    public String getXora() { return xora; }
    public void setXora(String xora) { this.xora = xora; }

    public String getPoli() { return poli; }
    public void setPoli(String poli) { this.poli = poli; }

    public String getTkParalipti() { return tkParalipti; }
    public void setTkParalipti(String tkParalipti) { this.tkParalipti = tkParalipti; }

    public String getDiefthinsi() { return diefthinsi; }
    public void setDiefthinsi(String diefthinsi) { this.diefthinsi = diefthinsi; }

    public String getTilefonoStathero() { return tilefonoStathero; }
    public void setTilefonoStathero(String tilefonoStathero) { this.tilefonoStathero = tilefonoStathero; }

    public String getTilefonoKinito() { return tilefonoKinito; }
    public void setTilefonoKinito(String tilefonoKinito) { this.tilefonoKinito = tilefonoKinito; }

    public String getIstoriko() { return istoriko; }
    public void setIstoriko(String istoriko) { this.istoriko = istoriko; }

    public String getStatusApostolis() { return statusApostolis; }
    public void setStatusApostolis(String statusApostolis) { this.statusApostolis = statusApostolis; }

    public String getStatusMypms() {
        return statusMypms;
    }

    public void setStatusMypms(String statusMypms) {
        this.statusMypms = statusMypms;
    }

    public String getSxolia() { return sxolia; }
    public void setSxolia(String sxolia) { this.sxolia = sxolia; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeliveryFlag() { return deliveryFlag; }
    public void setDeliveryFlag(Integer deliveryFlag) { this.deliveryFlag = deliveryFlag; }

    public Integer getReturnedFlag() { return returnedFlag; }
    public void setReturnedFlag(Integer returnedFlag) { this.returnedFlag = returnedFlag; }

    public String getNonDeliveryReasonCode() { return nonDeliveryReasonCode; }
    public void setNonDeliveryReasonCode(String nonDeliveryReasonCode) { this.nonDeliveryReasonCode = nonDeliveryReasonCode; }

    public Integer getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(Integer shipmentStatus) { this.shipmentStatus = shipmentStatus; }

    public String getDeliveryInfo() { return deliveryInfo; }
    public void setDeliveryInfo(String deliveryInfo) { this.deliveryInfo = deliveryInfo; }

    public Boolean getStatusLocked() {
        return statusLocked;
    }

    public void setStatusLocked(Boolean statusLocked) {
        this.statusLocked = statusLocked;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    /**
     * Επιστρέφει την πλήρη διεύθυνση παραλήπτη
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (diefthinsi != null && !diefthinsi.trim().isEmpty()) {
            address.append(diefthinsi);
        }
        if (poli != null && !poli.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(poli);
        }
        if (xora != null && !xora.trim().isEmpty() && !xora.equals("ΕΛΛΑΔΑ")) {
            if (address.length() > 0) address.append(", ");
            address.append(xora);
        }
        return address.toString();
    }


    /**
     * Επιστρέφει το κύριο τηλέφωνο παραλήπτη
     */
    public String getPrimaryPhone() {
        if (tilefonoKinito != null && !tilefonoKinito.trim().isEmpty()) {
            return tilefonoKinito;
        }
        return tilefonoStathero;
    }

    /**
     * Επιστρέφει συνοπτική περιγραφή της αποστολής
     */
    public String getSummary() {
        return String.format("%s - %s (%s)", arithmosApostolis, paraliptis, poli);
    }

    @Override
    public String toString() {
        return getSummary();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apostoli apostoli = (Apostoli) obj;
        return idApostolis == apostoli.idApostolis;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idApostolis);
    }

    public LocalDate getImerominiaAnaxorisis() {
        return imerominiaAnaxorisis;
    }

    public void setImerominiaAnaxorisis(LocalDate imerominiaAnaxorisis) {
        this.imerominiaAnaxorisis = imerominiaAnaxorisis;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(String statusDetails) {
        this.statusDetails = statusDetails;
    }
}
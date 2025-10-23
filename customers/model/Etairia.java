package org.pms.customers.model;

import java.time.LocalDateTime;

/**
 * Model κλάση για την Εταιρία
 */
public class Etairia {

    private int idEtairias;
    private String poli;
    private String perioxi;
    private String odos;
    private String arithmos;
    private String tk;
    private String afmPelati;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Etairia() {}

    // Constructor without id (for new records)
    public Etairia(String poli, String perioxi, String odos, String arithmos,
                   String tk, String afmPelati) {
        this.poli = poli;
        this.perioxi = perioxi;
        this.odos = odos;
        this.arithmos = arithmos;
        this.tk = tk;
        this.afmPelati = afmPelati;
    }

    // Full constructor
    public Etairia(int idEtairias, String poli, String perioxi, String odos,
                   String arithmos, String tk, String afmPelati) {
        this.idEtairias = idEtairias;
        this.poli = poli;
        this.perioxi = perioxi;
        this.odos = odos;
        this.arithmos = arithmos;
        this.tk = tk;
        this.afmPelati = afmPelati;
    }

    // Getters and Setters
    public int getIdEtairias() { return idEtairias; }
    public void setIdEtairias(int idEtairias) { this.idEtairias = idEtairias; }

    public String getPoli() { return poli; }
    public void setPoli(String poli) { this.poli = poli; }

    public String getPerioxi() { return perioxi; }
    public void setPerioxi(String perioxi) { this.perioxi = perioxi; }

    public String getOdos() { return odos; }
    public void setOdos(String odos) { this.odos = odos; }

    public String getArithmos() { return arithmos; }
    public void setArithmos(String arithmos) { this.arithmos = arithmos; }

    public String getTk() { return tk; }
    public void setTk(String tk) { this.tk = tk; }

    public String getAfmPelati() { return afmPelati; }
    public void setAfmPelati(String afmPelati) { this.afmPelati = afmPelati; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Επιστρέφει την πλήρη διεύθυνση
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (odos != null && !odos.trim().isEmpty()) {
            address.append(odos);
        }
        if (arithmos != null && !arithmos.trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(arithmos);
        }
        if (tk != null && !tk.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(tk);
        }
        if (poli != null && !poli.trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(poli);
        }
        if (perioxi != null && !perioxi.trim().isEmpty()) {
            if (address.length() > 0) address.append(" (");
            address.append(perioxi).append(")");
        }
        return address.toString();
    }

    @Override
    public String toString() {
        return "ID: " + idEtairias + " - " + getFullAddress();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Etairia etairia = (Etairia) obj;
        return idEtairias == etairia.idEtairias;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idEtairias);
    }
}
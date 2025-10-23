package org.pms.customers.model;


import java.time.LocalDateTime;

/**
 * Model κλάση για το Τμήμα Αντικαταβολών
 */
public class TmimaAntikatavolon {

    private int idAntikatavolon;
    private int idEtairias;
    private String dikaiuxosLogariasmou;
    private String titlos;
    private String arithmosLogariasmou;
    private String apodosiSeTrapeza;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public TmimaAntikatavolon() {}

    // Constructor without id
    public TmimaAntikatavolon(int idEtairias, String dikaiuxosLogariasmou, String titlos,
                              String arithmosLogariasmou, String apodosiSeTrapeza, String email) {
        this.idEtairias = idEtairias;
        this.dikaiuxosLogariasmou = dikaiuxosLogariasmou;
        this.titlos = titlos;
        this.arithmosLogariasmou = arithmosLogariasmou;
        this.apodosiSeTrapeza = apodosiSeTrapeza;
        this.email = email;
    }

    // Full constructor
    public TmimaAntikatavolon(int idAntikatavolon, int idEtairias, String dikaiuxosLogariasmou,
                              String titlos, String arithmosLogariasmou, String apodosiSeTrapeza, String email) {
        this.idAntikatavolon = idAntikatavolon;
        this.idEtairias = idEtairias;
        this.dikaiuxosLogariasmou = dikaiuxosLogariasmou;
        this.titlos = titlos;
        this.arithmosLogariasmou = arithmosLogariasmou;
        this.apodosiSeTrapeza = apodosiSeTrapeza;
        this.email = email;
    }

    // Getters and Setters
    public int getIdAntikatavolon() { return idAntikatavolon; }
    public void setIdAntikatavolon(int idAntikatavolon) { this.idAntikatavolon = idAntikatavolon; }

    public int getIdEtairias() { return idEtairias; }
    public void setIdEtairias(int idEtairias) { this.idEtairias = idEtairias; }

    public String getDikaiuxosLogariasmou() { return dikaiuxosLogariasmou; }
    public void setDikaiuxosLogariasmou(String dikaiuxosLogariasmou) { this.dikaiuxosLogariasmou = dikaiuxosLogariasmou; }

    public String getTitlos() { return titlos; }
    public void setTitlos(String titlos) { this.titlos = titlos; }

    public String getArithmosLogariasmou() { return arithmosLogariasmou; }
    public void setArithmosLogariasmou(String arithmosLogariasmou) { this.arithmosLogariasmou = arithmosLogariasmou; }

    public String getApodosiSeTrapeza() { return apodosiSeTrapeza; }
    public void setApodosiSeTrapeza(String apodosiSeTrapeza) { this.apodosiSeTrapeza = apodosiSeTrapeza; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return dikaiuxosLogariasmou + " - " + arithmosLogariasmou;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TmimaAntikatavolon that = (TmimaAntikatavolon) obj;
        return idAntikatavolon == that.idAntikatavolon;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idAntikatavolon);
    }
}
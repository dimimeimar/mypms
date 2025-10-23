package org.pms.customers.model;

import java.time.LocalDateTime;

/**
 * Model κλάση για τον Υπεύθυνο
 */
public class Ypeuthynos {

    public enum EidosYpeuthynou {
        ΣΥΜΒΑΣΗΣ("ΣΥΜΒΑΣΗΣ"),
        ΕΠΙΚΟΙΝΩΝΙΑΣ("ΕΠΙΚΟΙΝΩΝΙΑΣ"),
        ΠΛΗΡΩΜΩΝ("ΠΛΗΡΩΜΩΝ"),
        ΛΟΓΙΣΤΗΡΙΟΥ("ΛΟΓΙΣΤΗΡΙΟΥ"),
        ΑΝΤΙΚΑΤΑΒΟΛΩΝ("ΑΝΤΙΚΑΤΑΒΟΛΩΝ");

        private final String value;

        EidosYpeuthynou(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static EidosYpeuthynou fromString(String value) {
            for (EidosYpeuthynou eidos : EidosYpeuthynou.values()) {
                if (eidos.value.equals(value)) {
                    return eidos;
                }
            }
            return null;
        }
    }

    private int idYpeuthynou;
    private int idEtairias;
    private EidosYpeuthynou eidosYpeuthynou;
    private String onomaYpeuthynou;
    private String titlos;
    private String tilefono;
    private String kinito;
    private String email;
    private boolean isMasterEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Ypeuthynos() {}

    // Constructor without id
    public Ypeuthynos(int idEtairias, EidosYpeuthynou eidosYpeuthynou, String onomaYpeuthynou,
                      String titlos, String tilefono, String kinito, String email, boolean isMasterEmail) {
        this.idEtairias = idEtairias;
        this.eidosYpeuthynou = eidosYpeuthynou;
        this.onomaYpeuthynou = onomaYpeuthynou;
        this.titlos = titlos;
        this.tilefono = tilefono;
        this.kinito = kinito;
        this.email = email;
        this.isMasterEmail = isMasterEmail;
    }

    // Full constructor
    public Ypeuthynos(int idYpeuthynou, int idEtairias, EidosYpeuthynou eidosYpeuthynou,
                      String onomaYpeuthynou, String titlos, String tilefono, String kinito,
                      String email, boolean isMasterEmail) {
        this.idYpeuthynou = idYpeuthynou;
        this.idEtairias = idEtairias;
        this.eidosYpeuthynou = eidosYpeuthynou;
        this.onomaYpeuthynou = onomaYpeuthynou;
        this.titlos = titlos;
        this.tilefono = tilefono;
        this.kinito = kinito;
        this.email = email;
        this.isMasterEmail = isMasterEmail;
    }

    // Getters and Setters
    public int getIdYpeuthynou() { return idYpeuthynou; }
    public void setIdYpeuthynou(int idYpeuthynou) { this.idYpeuthynou = idYpeuthynou; }

    public int getIdEtairias() { return idEtairias; }
    public void setIdEtairias(int idEtairias) { this.idEtairias = idEtairias; }

    public EidosYpeuthynou getEidosYpeuthynou() { return eidosYpeuthynou; }
    public void setEidosYpeuthynou(EidosYpeuthynou eidosYpeuthynou) { this.eidosYpeuthynou = eidosYpeuthynou; }

    public String getOnomaYpeuthynou() { return onomaYpeuthynou; }
    public void setOnomaYpeuthynou(String onomaYpeuthynou) { this.onomaYpeuthynou = onomaYpeuthynou; }

    public String getTitlos() { return titlos; }
    public void setTitlos(String titlos) { this.titlos = titlos; }

    public String getTilefono() { return tilefono; }
    public void setTilefono(String tilefono) { this.tilefono = tilefono; }

    public String getKinito() { return kinito; }
    public void setKinito(String kinito) { this.kinito = kinito; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isMasterEmail() { return isMasterEmail; }
    public void setMasterEmail(boolean masterEmail) { isMasterEmail = masterEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return onomaYpeuthynou + " (" + eidosYpeuthynou + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ypeuthynos ypeuthynos = (Ypeuthynos) obj;
        return idYpeuthynou == ypeuthynos.idYpeuthynou;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idYpeuthynou);
    }
}
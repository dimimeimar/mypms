package org.pms.customers.model;

import java.time.LocalDateTime;

/**
 * Ενημερωμένο Model κλάση για τον Πελάτη με ενσωματωμένα στοιχεία εταιρίας και υπευθύνου
 */
public class Pelatis {

    // Βασικά στοιχεία πελάτη
    private String kodikosPelati;
    private String kategoria;
    private String afmNomimuEkprosopu;
    private String eponymiaEtairias;
    private String diakritikosTitlos;
    private String nomikiMorfi;
    private String epaggelmaAntikimeno;
    private String afmEtairias;
    private String douEtairias;
    private String nomimosEkprospos;

    // Νέα πεδία επικοινωνίας
    private String email;
    private String tilefonoStathero;
    private String tilefonoKinito;

    private String poli;
    private String perioxi;
    private String odos;
    private String arithmosDiefthinsis;
    private String tk;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Pelatis() {}

    // Getters and Setters

    // Βασικά στοιχεία
    public String getKodikosPelati() { return kodikosPelati; }
    public void setKodikosPelati(String kodikosPelati) { this.kodikosPelati = kodikosPelati; }

    public String getKategoria() { return kategoria; }
    public void setKategoria(String kategoria) { this.kategoria = kategoria; }

    public String getAfmNomimuEkprosopu() { return afmNomimuEkprosopu; }
    public void setAfmNomimuEkprosopu(String afmNomimuEkprosopu) { this.afmNomimuEkprosopu = afmNomimuEkprosopu; }

    public String getEponymiaEtairias() { return eponymiaEtairias; }
    public void setEponymiaEtairias(String eponymiaEtairias) { this.eponymiaEtairias = eponymiaEtairias; }

    public String getDiakritikosTitlos() { return diakritikosTitlos; }
    public void setDiakritikosTitlos(String diakritikosTitlos) { this.diakritikosTitlos = diakritikosTitlos; }

    public String getNomikiMorfi() { return nomikiMorfi; }
    public void setNomikiMorfi(String nomikiMorfi) { this.nomikiMorfi = nomikiMorfi; }

    public String getEpaggelmaAntikimeno() { return epaggelmaAntikimeno; }
    public void setEpaggelmaAntikimeno(String epaggelmaAntikimeno) { this.epaggelmaAntikimeno = epaggelmaAntikimeno; }

    public String getAfmEtairias() { return afmEtairias; }
    public void setAfmEtairias(String afmEtairias) { this.afmEtairias = afmEtairias; }

    public String getDouEtairias() { return douEtairias; }
    public void setDouEtairias(String douEtairias) { this.douEtairias = douEtairias; }

    public String getNomimosEkprospos() { return nomimosEkprospos; }
    public void setNomimosEkprospos(String nomimosEkprospos) { this.nomimosEkprospos = nomimosEkprospos; }

    // Επικοινωνία
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTilefonoStathero() { return tilefonoStathero; }
    public void setTilefonoStathero(String tilefonoStathero) { this.tilefonoStathero = tilefonoStathero; }

    public String getTilefonoKinito() { return tilefonoKinito; }
    public void setTilefonoKinito(String tilefonoKinito) { this.tilefonoKinito = tilefonoKinito; }

    // Εταιρία
    public String getPoli() { return poli; }
    public void setPoli(String poli) { this.poli = poli; }

    public String getPerioxi() { return perioxi; }
    public void setPerioxi(String perioxi) { this.perioxi = perioxi; }

    public String getOdos() { return odos; }
    public void setOdos(String odos) { this.odos = odos; }

    public String getArithmosDiefthinsis() { return arithmosDiefthinsis; }
    public void setArithmosDiefthinsis(String arithmosDiefthinsis) { this.arithmosDiefthinsis = arithmosDiefthinsis; }

    public String getTk() { return tk; }
    public void setTk(String tk) { this.tk = tk; }

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
        if (arithmosDiefthinsis != null && !arithmosDiefthinsis.trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(arithmosDiefthinsis);
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

    /**
     * Επιστρέφει το κύριο τηλέφωνο (προτεραιότητα: σταθερό, κινητό)
     */
    public String getPrimaryPhone() {
        if (tilefonoStathero != null && !tilefonoStathero.trim().isEmpty()) {
            return tilefonoStathero;
        }
        return tilefonoKinito;
    }

    @Override
    public String toString() {
        return eponymiaEtairias + " (" + kodikosPelati + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pelatis pelatis = (Pelatis) obj;
        return kodikosPelati != null ? kodikosPelati.equals(pelatis.kodikosPelati) : pelatis.kodikosPelati == null;
    }

    @Override
    public int hashCode() {
        return kodikosPelati != null ? kodikosPelati.hashCode() : 0;
    }
}
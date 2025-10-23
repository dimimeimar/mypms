package org.pms.shipments.model;

import java.time.LocalDateTime;

public class Ekremotita {

    private int idEkremotita;
    private Integer idApostolis;
    private String titlos;
    private String perigrafi;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String createdBy;

    private String arithmosApostolis;

    public Ekremotita() {
    }

    public Ekremotita(String titlos, String perigrafi, String status, String priority) {
        this.titlos = titlos;
        this.perigrafi = perigrafi;
        this.status = status;
        this.priority = priority;
    }

    public int getIdEkremotita() {
        return idEkremotita;
    }

    public void setIdEkremotita(int idEkremotita) {
        this.idEkremotita = idEkremotita;
    }

    public Integer getIdApostolis() {
        return idApostolis;
    }

    public void setIdApostolis(Integer idApostolis) {
        this.idApostolis = idApostolis;
    }

    public String getTitlos() {
        return titlos;
    }

    public void setTitlos(String titlos) {
        this.titlos = titlos;
    }

    public String getPerigrafi() {
        return perigrafi;
    }

    public void setPerigrafi(String perigrafi) {
        this.perigrafi = perigrafi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getArithmosApostolis() {
        return arithmosApostolis;
    }

    public void setArithmosApostolis(String arithmosApostolis) {
        this.arithmosApostolis = arithmosApostolis;
    }

    public boolean isLinkedToApostoli() {
        return idApostolis != null;
    }

    public String getPriorityColor() {
        return switch (priority) {
            case "ΚΡΙΤΙΚΗ" -> "#D32F2F";
            case "ΥΨΗΛΗ" -> "#F57C00";
            case "ΜΕΣΑΙΑ" -> "#FBC02D";
            case "ΧΑΜΗΛΗ" -> "#388E3C";
            default -> "#757575";
        };
    }

    @Override
    public String toString() {
        return titlos + " (" + status + ")";
    }
}
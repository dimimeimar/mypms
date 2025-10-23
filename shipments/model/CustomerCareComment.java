package org.pms.shipments.model;

import java.time.LocalDateTime;

public class CustomerCareComment {
    private int id;
    private int idApostolis;
    private String sxolio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerCareComment() {}

    public CustomerCareComment(int idApostolis, String sxolio) {
        this.idApostolis = idApostolis;
        this.sxolio = sxolio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdApostolis() {
        return idApostolis;
    }

    public void setIdApostolis(int idApostolis) {
        this.idApostolis = idApostolis;
    }

    public String getSxolio() {
        return sxolio;
    }

    public void setSxolio(String sxolio) {
        this.sxolio = sxolio;
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
}
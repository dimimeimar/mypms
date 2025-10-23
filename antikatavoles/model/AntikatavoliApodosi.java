package org.pms.antikatavoles.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AntikatavoliApodosi {
    private Integer id;
    private String kodikosPelati;
    private String eponimiaEtairias;
    private LocalDate imerominiaApodosis;
    private BigDecimal synoloPoso;
    private Integer plithosAntikatabolon;
    private String sxolia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AntikatavoliApodosi() {}

    public AntikatavoliApodosi(String kodikosPelati, LocalDate imerominiaApodosis,
                               BigDecimal synoloPoso, Integer plithosAntikatabolon) {
        this.kodikosPelati = kodikosPelati;
        this.imerominiaApodosis = imerominiaApodosis;
        this.synoloPoso = synoloPoso;
        this.plithosAntikatabolon = plithosAntikatabolon;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getKodikosPelati() { return kodikosPelati; }
    public void setKodikosPelati(String kodikosPelati) { this.kodikosPelati = kodikosPelati; }

    public String getEponimiaEtairias() { return eponimiaEtairias; }
    public void setEponimiaEtairias(String eponimiaEtairias) { this.eponimiaEtairias = eponimiaEtairias; }

    public LocalDate getImerominiaApodosis() { return imerominiaApodosis; }
    public void setImerominiaApodosis(LocalDate imerominiaApodosis) { this.imerominiaApodosis = imerominiaApodosis; }

    public BigDecimal getSynoloPoso() { return synoloPoso; }
    public void setSynoloPoso(BigDecimal synoloPoso) { this.synoloPoso = synoloPoso; }

    public Integer getPlithosAntikatabolon() { return plithosAntikatabolon; }
    public void setPlithosAntikatabolon(Integer plithosAntikatabolon) { this.plithosAntikatabolon = plithosAntikatabolon; }

    public String getSxolia() { return sxolia; }
    public void setSxolia(String sxolia) { this.sxolia = sxolia; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
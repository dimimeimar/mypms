package org.pms.antikatavoles.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Antikatavoli {
    private Integer id;
    private Integer idApostolis;
    private Boolean apodothike;
    private LocalDate imerominiaApodosis;
    private Integer idApodosis;
    private String parastatikoACS;
    private String parastatikoMyPMS;
    private String sxolia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Antikatavoli() {}

    public Antikatavoli(Integer idApostolis) {
        this.idApostolis = idApostolis;
        this.apodothike = false;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdApostolis() { return idApostolis; }
    public void setIdApostolis(Integer idApostolis) { this.idApostolis = idApostolis; }

    public Boolean getApodothike() { return apodothike; }
    public void setApodothike(Boolean apodothike) { this.apodothike = apodothike; }

    public LocalDate getImerominiaApodosis() { return imerominiaApodosis; }
    public void setImerominiaApodosis(LocalDate imerominiaApodosis) { this.imerominiaApodosis = imerominiaApodosis; }

    public Integer getIdApodosis() { return idApodosis; }
    public void setIdApodosis(Integer idApodosis) { this.idApodosis = idApodosis; }

    public String getParastatikoACS() { return parastatikoACS; }
    public void setParastatikoACS(String parastatikoACS) { this.parastatikoACS = parastatikoACS; }

    public String getParastatikoMyPMS() { return parastatikoMyPMS; }
    public void setParastatikoMyPMS(String parastatikoMyPMS) { this.parastatikoMyPMS = parastatikoMyPMS; }

    public String getSxolia() { return sxolia; }
    public void setSxolia(String sxolia) { this.sxolia = sxolia; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
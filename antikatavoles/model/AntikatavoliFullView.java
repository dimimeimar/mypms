package org.pms.antikatavoles.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AntikatavoliFullView {
    // Από antikatavoles
    private Integer id;
    private Integer idApodosis;
    private String kodikosPelati;
    private String eponimiaEtairias;
    private Boolean apodothike;
    private LocalDate imerominiaApodosis;
    private String sxolia;
    private String parastatikoACS;
    private String parastatikoMyPMS;

    // Από apostoles
    private Integer idApostolis;
    private String arithmosApostolis;
    private String arithmosParaggelias;
    private LocalDate imerominiaParalabis;
    private LocalDate imerominiaParadosis;
    private String courier;
    private BigDecimal antikatavoli;
    private String paraliptis;

    public AntikatavoliFullView() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getKodikosPelati() { return kodikosPelati; }
    public void setKodikosPelati(String kodikosPelati) { this.kodikosPelati = kodikosPelati; }

    public String getEponimiaEtairias() { return eponimiaEtairias; }
    public void setEponimiaEtairias(String eponimiaEtairias) { this.eponimiaEtairias = eponimiaEtairias; }

    public Boolean getApodothike() { return apodothike; }
    public void setApodothike(Boolean apodothike) { this.apodothike = apodothike; }

    public LocalDate getImerominiaApodosis() { return imerominiaApodosis; }
    public void setImerominiaApodosis(LocalDate imerominiaApodosis) { this.imerominiaApodosis = imerominiaApodosis; }

    public String getSxolia() { return sxolia; }
    public void setSxolia(String sxolia) { this.sxolia = sxolia; }

    public String getParastatikoACS() { return parastatikoACS; }
    public void setParastatikoACS(String parastatikoACS) { this.parastatikoACS = parastatikoACS; }

    public String getParastatikoMyPMS() { return parastatikoMyPMS; }
    public void setParastatikoMyPMS(String parastatikoMyPMS) { this.parastatikoMyPMS = parastatikoMyPMS; }

    public Integer getIdApostolis() { return idApostolis; }
    public void setIdApostolis(Integer idApostolis) { this.idApostolis = idApostolis; }

    public String getArithmosApostolis() { return arithmosApostolis; }
    public void setArithmosApostolis(String arithmosApostolis) { this.arithmosApostolis = arithmosApostolis; }

    public String getArithmosParaggelias() { return arithmosParaggelias; }
    public void setArithmosParaggelias(String arithmosParaggelias) { this.arithmosParaggelias = arithmosParaggelias; }

    public LocalDate getImerominiaParalabis() { return imerominiaParalabis; }
    public void setImerominiaParalabis(LocalDate imerominiaParalabis) { this.imerominiaParalabis = imerominiaParalabis; }

    public LocalDate getImerominiaParadosis() { return imerominiaParadosis; }
    public void setImerominiaParadosis(LocalDate imerominiaParadosis) { this.imerominiaParadosis = imerominiaParadosis; }

    public String getCourier() { return courier; }
    public void setCourier(String courier) { this.courier = courier; }

    public BigDecimal getAntikatavoli() { return antikatavoli; }
    public void setAntikatavoli(BigDecimal antikatavoli) { this.antikatavoli = antikatavoli; }

    public String getParaliptis() { return paraliptis; }
    public void setParaliptis(String paraliptis) { this.paraliptis = paraliptis; }

    public Integer getIdApodosis() { return idApodosis; }
    public void setIdApodosis(Integer idApodosis) { this.idApodosis = idApodosis; }


}
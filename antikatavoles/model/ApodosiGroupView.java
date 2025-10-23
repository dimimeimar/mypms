package org.pms.antikatavoles.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApodosiGroupView {
    private Integer idApodosis;
    private String kodikosPelati;
    private String eponimiaEtairias;
    private Integer plithosAntikatabolon;
    private BigDecimal synoloPoso;
    private LocalDateTime createdAt;
    private List<AntikatavoliFullView> antikatavoles;

    public ApodosiGroupView() {
        this.antikatavoles = new ArrayList<>();
    }

    public ApodosiGroupView(Integer idApodosis, String kodikosPelati, String eponimiaEtairias) {
        this.idApodosis = idApodosis;
        this.kodikosPelati = kodikosPelati;
        this.eponimiaEtairias = eponimiaEtairias;
        this.antikatavoles = new ArrayList<>();
        this.synoloPoso = BigDecimal.ZERO;
        this.plithosAntikatabolon = 0;
    }

    public void addAntikatavoli(AntikatavoliFullView view) {
        this.antikatavoles.add(view);
        if (view.getAntikatavoli() != null) {
            this.synoloPoso = this.synoloPoso.add(view.getAntikatavoli());
        }
        this.plithosAntikatabolon = this.antikatavoles.size();
    }

    public Integer getIdApodosis() { return idApodosis; }
    public void setIdApodosis(Integer idApodosis) { this.idApodosis = idApodosis; }

    public String getKodikosPelati() { return kodikosPelati; }
    public void setKodikosPelati(String kodikosPelati) { this.kodikosPelati = kodikosPelati; }

    public String getEponimiaEtairias() { return eponimiaEtairias; }
    public void setEponimiaEtairias(String eponimiaEtairias) { this.eponimiaEtairias = eponimiaEtairias; }

    public Integer getPlithosAntikatabolon() { return plithosAntikatabolon; }
    public void setPlithosAntikatabolon(Integer plithosAntikatabolon) { this.plithosAntikatabolon = plithosAntikatabolon; }

    public BigDecimal getSynoloPoso() { return synoloPoso; }
    public void setSynoloPoso(BigDecimal synoloPoso) { this.synoloPoso = synoloPoso; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<AntikatavoliFullView> getAntikatavoles() { return antikatavoles; }
    public void setAntikatavoles(List<AntikatavoliFullView> antikatavoles) {
        this.antikatavoles = antikatavoles;
        recalculate();
    }

    private void recalculate() {
        this.synoloPoso = BigDecimal.ZERO;
        for (AntikatavoliFullView view : antikatavoles) {
            if (view.getAntikatavoli() != null) {
                this.synoloPoso = this.synoloPoso.add(view.getAntikatavoli());
            }
        }
        this.plithosAntikatabolon = antikatavoles.size();
    }
}
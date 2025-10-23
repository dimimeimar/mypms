package org.pms.antikatavoles.service;

import org.pms.antikatavoles.dao.AntikatavoliDAO;
import org.pms.antikatavoles.dao.AntikatavoliApodosiDAO;
import org.pms.antikatavoles.dao.AntikatavoliFullViewDAO;
import org.pms.antikatavoles.model.Antikatavoli;
import org.pms.antikatavoles.model.AntikatavoliApodosi;
import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.antikatavoles.model.ApodosiGroupView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntikatavoliService {
    private final AntikatavoliDAO antikatavoliDAO;
    private final AntikatavoliApodosiDAO apodosiDAO;
    private final AntikatavoliFullViewDAO fullViewDAO;

    public AntikatavoliService() {
        this.antikatavoliDAO = new AntikatavoliDAO();
        this.apodosiDAO = new AntikatavoliApodosiDAO();
        this.fullViewDAO = new AntikatavoliFullViewDAO();
    }

    public boolean createApodosiForPelatis(String kodikosPelati, String eponimiaEtairias,
                                           LocalDate imerominiaApodosis,
                                           List<AntikatavoliFullView> antikatavoles) {
        if (antikatavoles == null || antikatavoles.isEmpty()) {
            return false;
        }

        BigDecimal synoloPoso = BigDecimal.ZERO;
        for (AntikatavoliFullView view : antikatavoles) {
            if (view.getAntikatavoli() != null) {
                synoloPoso = synoloPoso.add(view.getAntikatavoli());
            }
        }

        AntikatavoliApodosi apodosi = new AntikatavoliApodosi(
                kodikosPelati,
                imerominiaApodosis,
                synoloPoso,
                antikatavoles.size()
        );
        apodosi.setEponimiaEtairias(eponimiaEtairias);

        if (apodosiDAO.create(apodosi)) {
            for (AntikatavoliFullView view : antikatavoles) {
                Antikatavoli ak = antikatavoliDAO.findByIdApostolis(view.getIdApostolis());

                if (ak == null) {
                    ak = new Antikatavoli(view.getIdApostolis());
                }

                ak.setApodothike(true);
                ak.setImerominiaApodosis(imerominiaApodosis);
                ak.setIdApodosis(apodosi.getId());

                if (ak.getId() == null) {
                    antikatavoliDAO.create(ak);
                } else {
                    antikatavoliDAO.update(ak);
                }
            }
            return true;
        }

        return false;
    }

    public BigDecimal calculateSynoloEkkremon(String kodikosPelati) {
        List<AntikatavoliFullView> pending = fullViewDAO.findByKodikosPelati(kodikosPelati);
        BigDecimal synolo = BigDecimal.ZERO;

        for (AntikatavoliFullView view : pending) {
            if (!view.getApodothike() && view.getAntikatavoli() != null) {
                synolo = synolo.add(view.getAntikatavoli());
            }
        }

        return synolo;
    }

    public int countEkkremes(String kodikosPelati) {
        List<AntikatavoliFullView> all = fullViewDAO.findByKodikosPelati(kodikosPelati);
        int count = 0;

        for (AntikatavoliFullView view : all) {
            if (!view.getApodothike()) {
                count++;
            }
        }

        return count;
    }

    public List<AntikatavoliFullView> getPendingByPelatis(String kodikosPelati) {
        List<AntikatavoliFullView> all = fullViewDAO.findByKodikosPelati(kodikosPelati);
        all.removeIf(AntikatavoliFullView::getApodothike);
        return all;
    }

    public boolean createProApodosiForPelatis(String kodikosPelati, String eponimiaEtairias,
                                              List<AntikatavoliFullView> antikatavoles) {
        if (antikatavoles == null || antikatavoles.isEmpty()) {
            return false;
        }

        BigDecimal synoloPoso = BigDecimal.ZERO;
        for (AntikatavoliFullView view : antikatavoles) {
            if (view.getAntikatavoli() != null) {
                synoloPoso = synoloPoso.add(view.getAntikatavoli());
            }
        }

        AntikatavoliApodosi apodosi = new AntikatavoliApodosi(
                kodikosPelati,
                null,
                synoloPoso,
                antikatavoles.size()
        );
        apodosi.setEponimiaEtairias(eponimiaEtairias);

        if (apodosiDAO.create(apodosi)) {
            for (AntikatavoliFullView view : antikatavoles) {
                Antikatavoli ak = antikatavoliDAO.findByIdApostolis(view.getIdApostolis());

                if (ak == null) {
                    ak = new Antikatavoli(view.getIdApostolis());
                }

                ak.setApodothike(false);
                ak.setImerominiaApodosis(null);
                ak.setIdApodosis(apodosi.getId());

                if (ak.getId() == null) {
                    antikatavoliDAO.create(ak);
                } else {
                    antikatavoliDAO.update(ak);
                }
            }
            return true;
        }

        return false;
    }

    public boolean finalizeApodosi(List<AntikatavoliFullView> antikatavoles, LocalDate imerominiaApodosis) {
        if (antikatavoles == null || antikatavoles.isEmpty()) {
            return false;
        }

        for (AntikatavoliFullView view : antikatavoles) {
            Antikatavoli ak = antikatavoliDAO.findByIdApostolis(view.getIdApostolis());

            if (ak != null) {
                ak.setApodothike(true);
                ak.setImerominiaApodosis(imerominiaApodosis);

                if (!antikatavoliDAO.update(ak)) {
                    return false;
                }

                if (ak.getIdApodosis() != null) {
                    AntikatavoliApodosi apodosi = apodosiDAO.findById(ak.getIdApodosis());
                    if (apodosi != null && apodosi.getImerominiaApodosis() == null) {
                        apodosi.setImerominiaApodosis(imerominiaApodosis);
                        apodosiDAO.update(apodosi);
                    }
                }
            }
        }

        return true;
    }

    public List<ApodosiGroupView> getGroupedProApodosi() {
        List<AntikatavoliFullView> allProApodosi = fullViewDAO.findProApodosi();
        Map<Integer, ApodosiGroupView> groupedMap = new HashMap<>();

        for (AntikatavoliFullView view : allProApodosi) {
            Integer idApodosis = view.getIdApodosis();

            if (!groupedMap.containsKey(idApodosis)) {
                ApodosiGroupView group = new ApodosiGroupView(
                        idApodosis,
                        view.getKodikosPelati(),
                        view.getEponimiaEtairias()
                );
                groupedMap.put(idApodosis, group);
            }

            groupedMap.get(idApodosis).addAntikatavoli(view);
        }

        return new ArrayList<>(groupedMap.values());
    }
}
package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.model.SerieEntity;

import java.util.List;

public interface SerieService {

    List<SerieEntity> findAllByBranchOfficeId(Integer idOficina);

    SerieEntity saveSerie(SerieEntity serieEntity, Integer idOficina, Boolean isEdit, String user);

    void deleteSerie(Integer id);
}

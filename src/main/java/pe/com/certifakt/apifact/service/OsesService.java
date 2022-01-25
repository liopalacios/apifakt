package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.model.OsesEntity;

import java.util.Optional;

public interface OsesService {
    Optional<OsesEntity> findById(Integer id);
}

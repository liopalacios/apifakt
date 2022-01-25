package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.model.UbigeoEntity;

import java.util.List;

public interface UbigeototalService {
    List<UbigeoEntity> findByDescripcionContaining(String contenido);
    List<UbigeoEntity> findAllUbigeo();
}

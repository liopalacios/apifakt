package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.model.UbigeoEntity;
import pe.com.certifakt.apifact.repository.UbigeoRepository;
import pe.com.certifakt.apifact.service.UbigeototalService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UbigeototalServiceImpl implements UbigeototalService {

    @Autowired
    private UbigeoRepository ubigeoRepository;

    @Override
    public List<UbigeoEntity> findByDescripcionContaining(String contenido) {
        return ubigeoRepository.findByDescripcionContaining(contenido);
    }

    @Override
    public List<UbigeoEntity> findAllUbigeo() {
        return ubigeoRepository.findAll();
    }
}

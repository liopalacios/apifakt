package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.certifakt.apifact.model.OsesEntity;
import pe.com.certifakt.apifact.repository.OsesRepository;
import pe.com.certifakt.apifact.service.OsesService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OsesServiceImpl implements OsesService {

    @Autowired
    private OsesRepository osesRepository;

    @Override
    public Optional<OsesEntity> findById(Integer id) {
        return osesRepository.findById(id);
    }
}

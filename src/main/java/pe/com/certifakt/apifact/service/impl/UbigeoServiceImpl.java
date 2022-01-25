package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.model.DepartamentoEntity;
import pe.com.certifakt.apifact.repository.DepartamentoRepository;
import pe.com.certifakt.apifact.service.UbigeoService;

import java.util.List;

@Service
@AllArgsConstructor
public class UbigeoServiceImpl implements UbigeoService {

    private final DepartamentoRepository departamentoRepository;

    public List<DepartamentoEntity> findAllDepartamento() {
        return departamentoRepository.findAllByEstadoOrderByDescripcion(true);
    }

}

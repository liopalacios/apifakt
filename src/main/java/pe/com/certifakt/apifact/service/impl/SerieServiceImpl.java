package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.model.SerieEntity;
import pe.com.certifakt.apifact.repository.BranchOfficeRepository;
import pe.com.certifakt.apifact.repository.SerieRepository;
import pe.com.certifakt.apifact.service.SerieService;

import java.util.List;

@Service
@AllArgsConstructor
public class SerieServiceImpl implements SerieService {

    private final SerieRepository serieRepository;
    private final BranchOfficeRepository branchOfficeRepository;


    @Override
    public List<SerieEntity> findAllByBranchOfficeId(Integer idOficina) {
        return serieRepository.findAllByOficinaId(idOficina);
    }

    @Override
    public SerieEntity saveSerie(SerieEntity serie, Integer idOficina, Boolean isEdit, String user) {

        BranchOfficeEntity oficina = branchOfficeRepository.findById(idOficina).get();

        if (isEdit) {
            SerieEntity serieEntity = serieRepository.findById(serie.getId()).get();
            serieEntity.setSerie(serie.getSerie());
            serieEntity.setTipoDocumento(serie.getTipoDocumento());
            serieEntity.setOficina(oficina);
            serieEntity.setUpdatedBy(user);
            return serieRepository.save(serieEntity);
        } else {
            serie.setCreatedBy(user);
            serie.setOficina(oficina);
            return serieRepository.save(serie);
        }

    }

    @Override
    public void deleteSerie(Integer id) {
        serieRepository.deleteById(id);
    }
}

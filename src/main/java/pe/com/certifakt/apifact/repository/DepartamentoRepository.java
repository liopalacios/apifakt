package pe.com.certifakt.apifact.repository;

import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.model.DepartamentoEntity;

import java.util.List;

public interface DepartamentoRepository extends CrudRepository<DepartamentoEntity, String> {

    List<DepartamentoEntity> findAllByEstadoOrderByDescripcion(Boolean estado);

}

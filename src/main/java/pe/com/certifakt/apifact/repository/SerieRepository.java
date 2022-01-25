package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.SerieEntity;

import java.util.List;

public interface SerieRepository extends JpaRepository<SerieEntity, Integer> {

    List<SerieEntity> findAllByOficinaId(Integer id);

}

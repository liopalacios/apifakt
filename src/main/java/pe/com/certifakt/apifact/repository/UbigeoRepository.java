package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.UbigeoEntity;

import java.util.List;

public interface UbigeoRepository extends JpaRepository<UbigeoEntity, String> {
    List<UbigeoEntity> findByDescripcionContaining(String contenido);
}

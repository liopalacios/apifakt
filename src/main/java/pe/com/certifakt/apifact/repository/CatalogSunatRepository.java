package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.certifakt.apifact.model.CatalogSunatEntity;

import java.util.List;
import java.util.Map;

public interface CatalogSunatRepository extends JpaRepository<CatalogSunatEntity, Long> {

    List<CatalogSunatEntity> findByNumeroOrderByOrden(String numero);
    List<CatalogSunatEntity> findAllByNumero(String numero);
    @Query("select c from CatalogSunatEntity c where c.numero = ?1 and UPPER(c.descripcion) like %?2%")
    Page<CatalogSunatEntity> findByNumeroAndDescripcion(String numero, String descripcion, Pageable pageable);
    List<CatalogSunatEntity> findByNumeroInOrderByNumero(List<String> nums);
}

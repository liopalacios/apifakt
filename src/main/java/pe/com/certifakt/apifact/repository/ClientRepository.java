package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.ClientEntity;

import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

    List<ClientEntity> findAllByCompanyIdAndEstadoIsTrueOrderByNumeroDocumento(Integer id);

    List<ClientEntity> findAllByCompanyIdAndTipoDocumentoAndNumeroDocumentoStartingWithAndEstadoIsTrue(Integer id, String tipo, String numero);

    ClientEntity findByCompanyIdAndTipoDocumentoAndNumeroDocumentoAndEstadoIsTrue(Integer id, String tipo, String numero);

    Page<ClientEntity> findAllByCompanyIdAndEstadoIsTrueOrderByNombreComercial(Integer id, Pageable pageRequest);

    long countByCompanyIdAndEstadoIsTrue(Integer id);

    @Query("select count(c) from ClientEntity as c inner join c.company as cp where cp.id = :id and c.estado = true and ( lower(c.numeroDocumento) like lower(concat('%', :filter,'%')) or lower(c.razonSocial) like lower(concat('%', :filter,'%')))")
    long countSearchByCompanyIdAndEstadoIsTrue(@Param("id") Integer id, @Param("filter") String filter);

    @Query("select c from ClientEntity as c inner join c.company as cp where cp.id = :id and c.estado = true and ( lower(c.numeroDocumento) like lower(concat('%', :filter,'%')) or lower(c.razonSocial) like lower(concat('%', :filter,'%')))")
    Page<ClientEntity> findAllByCompanyIdAndFilterAndEstadoIsTrueOrderByNombreComercial(@Param("id") Integer id, @Param("filter") String filter, Pageable pageable);
}

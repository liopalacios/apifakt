package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.certifakt.apifact.model.MerkdopagoNotifyEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MerkdoPagoRepository extends CrudRepository<MerkdopagoNotifyEntity,Long> {

    @Query("select m from MerkdopagoNotifyEntity m where registro >= :date  and usuario = 1 and reporte is null ")
    List<MerkdopagoNotifyEntity> findByReporte(@Param("date") Date date);

    Optional<MerkdopagoNotifyEntity> findByIdmerkdopago(Long id);

    @Modifying
    @Query("update MerkdopagoNotifyEntity a set a.usuario=2 WHERE a.usuario=1 and a.id <> (SELECT min(b.id) " +
            "FROM  MerkdopagoNotifyEntity b WHERE  a.idmerkdopago = b.idmerkdopago ) ")
    void updateDuplicate();
}

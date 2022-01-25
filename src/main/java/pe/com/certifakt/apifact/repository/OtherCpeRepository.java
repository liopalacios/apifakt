package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.model.OtherCpeEntity;

import java.util.Date;
import java.util.List;

public interface OtherCpeRepository extends CrudRepository<OtherCpeEntity, Long> {

    OtherCpeEntity findByIdOtroCPE(Long id);

    OtherCpeEntity findByNumeroDocumentoIdentidadEmisorAndTipoComprobanteAndSerieAndNumero(String ruc, String tipo, String serie,
                                                                                           Integer numero);
    OtherCpeEntity findByIdOtroCPEAndUuid(Long id, String uuid);

    @Query(value = "select oc.id_otros_cpe as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, " +
            "pvf.tipo_archivo as tipo from register_file_upload u  " +
            "inner join other_cpe_file pvf on pvf.id_register_file_send = u.id_register_file_send   \n" +
            "inner join otros_cpe oc on oc.id_otros_cpe = pvf.id_otros_cpe  \n" +
            "where oc.id_otros_cpe = ? and oc.uuid = ? and pvf.tipo_archivo = ? " +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findByIdOtroCPEAndUuidTipo(Long id, String uuid, String tipo);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update OtherCpeEntity o "
            + "set o.estado = :estadoComprobante "
            + "where o.idOtroCPE = :identificador")
    public void updateEstadoHabilitarOrDeshabiltarRegistro(
            @Param("identificador") Long identificador,
            @Param("estadoComprobante") String estadoComprobante);

    @Modifying
    @Query("update OtherCpeEntity p "
            + "set p.estado = :estadoComprobante, "
            + "p.estadoEnSunat = :estadoEnSunat, "
            + "p.mensajeRespuesta = :mensajeRespuesta "
            + "where p.idOtroCPE = :identificador")
    public void updateEstadoComprobanteByResultadoSunat(
            @Param("identificador") Long identificador,
            @Param("estadoComprobante") String estadoComprobante,
            @Param("estadoEnSunat") String estadoEnSunat,
            @Param("mensajeRespuesta") String mensajeRespuesta);

    @Query("select p from OtherCpeEntity p "
            + "where p.identificadorDocumento = :idDocumento")
    public OtherCpeEntity getIdentificadorDocument(@Param("idDocumento") String idDocumento);

    @Query(value = "SELECT p FROM OtherCpeEntity p "
            + "where p.numeroDocumentoIdentidadEmisor = ?1 and p.fechaEmisionDate between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and (?4 is null or p.tipoComprobante like ?4) and (?5 is null or p.numeroDocumentoIdentidadReceptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 = 0 or p.numero = ?7) "
            + "order by p.fechaEmisionDate desc, p.numero desc")
    Page<OtherCpeEntity> findAllSerchForPage(String rucEmisor,
                                             String fechaEmisionDesde, String fechaEmisionHasta,
                                             String tipoComprobante, String numDocIdentReceptor,
                                             String serie, Integer numero, Pageable pageable);

    OtherCpeEntity findFirst1ByTipoComprobanteAndSerieAndNumeroDocumentoIdentidadEmisorOrderByNumeroDesc(String tipoDocumento, String serie, String ruc);

    List<OtherCpeEntity> findByIdOtroCPEIn(List<Long> ids);


}

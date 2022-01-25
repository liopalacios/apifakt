package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import java.util.Date;
import java.util.List;

public interface GuiaRemisionRepository extends CrudRepository<GuiaRemisionEntity, Long> {

    GuiaRemisionEntity findByIdGuiaRemision(Long id);

    GuiaRemisionEntity findByNumeroDocumentoIdentidadRemitenteAndSerieAndNumero(
            String ruc, String serie, Integer numero);

    GuiaRemisionEntity findByidGuiaRemisionAndUuid(Long idGuiaRemision,String uid);

    @Query(value = "select gr.id_guia_remision as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, " +
            "pvf.tipo_archivo as tipo from register_file_upload u  " +
            "inner join guia_file pvf on pvf.id_register_file_send = u.id_register_file_send  \n" +
            "inner join guia_remision gr on gr.id_guia_remision = pvf.id_guia_remision \n" +
            "where gr.id_guia_remision = ? and gr.uuid = ? and pvf.tipo_archivo = ? \n" +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findByidGuiaRemisionAndUuidTipo(Long idGuiaRemision, String uid, String tipo);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update GuiaRemisionEntity o "
            + "set o.estado = :estadoComprobante "
            + "where o.idGuiaRemision = :identificador")
    public void updateEstadoHabilitarOrDeshabiltarRegistro(
            @Param("identificador") Long identificador,
            @Param("estadoComprobante") String estadoComprobante);

    @Modifying
    @Query("update GuiaRemisionEntity p "
            + "set p.estado = :estadoComprobante, "
            + "p.mensajeRespuesta = :mensajeRespuesta "
            + "where p.idGuiaRemision = :identificador")
    public void updateEstadoComprobanteByResultadoSunat(
            @Param("identificador") Long identificador,
            @Param("estadoComprobante") String estadoComprobante,
            @Param("mensajeRespuesta") String mensajeRespuesta);

    @Query(value = "SELECT g FROM GuiaRemisionEntity g "
            + "left join g.oficina o "
            + "where g.numeroDocumentoIdentidadRemitente = ?1 and g.fechaEmisionDate between ?2 and ?3 "
            + "and (?4 is null or g.serie like ?4) and (?5 = 0 or g.numero = ?5) and (?6 = 0 or o.id = ?6) "
            + "order by g.fechaEmisionDate desc, g.numero desc")
    Page<GuiaRemisionEntity> findAllSerchForPages(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta,
                                                             String serie, Integer numero,Integer idOficina, Pageable pageable);

    @Query("select g from GuiaRemisionEntity g "
            + "where g.identificadorDocumento = :idDocumento")
    public GuiaRemisionEntity getIdentificadorDocument(@Param("idDocumento") String idDocumento);

    List<GuiaRemisionEntity> findByIdGuiaRemisionIn(List<Long> ids);

    @Query("select g from GuiaRemisionEntity g "
            + "where g.fechaEmisionDate between :fechaDesde and :fechaHasta  and (numeroDocumentoIdentidadRemitente = :ruc or :ruc is null) "
            + "and (g.serie = :serie or :serie is null) and (g.numero = :numero or :numero is null)")
    public List<GuiaRemisionEntity> getSearchGuia(@Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta,
                                                  @Param("ruc") String ruc, @Param("serie") String serie, @Param("numero") Integer numero);

    public GuiaRemisionEntity findFirst1BySerieAndNumeroDocumentoIdentidadRemitenteOrderByNumeroDesc(String serie,String ruc);

    @Query("select g from GuiaRemisionEntity g "
            + "where g.fechaEmisionDate between :fechaDesde and :fechaHasta  and (numeroDocumentoIdentidadRemitente = :ruc or :ruc is null) "
            + "and (g.serie = :serie or :serie is null) and (g.numero = :numero or :numero is null) "
            + "order by idGuiaRemision desc")
    public List<GuiaRemisionEntity> findAllGuiaRemision(@Param("fechaDesde") Date fechaDesde, @Param("fechaHasta") Date fechaHasta,
                                                        @Param("ruc") String ruc, @Param("serie") String serie, @Param("numero") Integer numero);

}

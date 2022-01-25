package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.bean.PaymentVoucher;
import pe.com.certifakt.apifact.dto.inter.DetailsPaymentInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterFileDto;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucherEntity, Long> {

    PaymentVoucherEntity findByIdPaymentVoucher(Long id);

    PaymentVoucherEntity findByIdentificadorDocumento(String identificador);

    @Query(value = "select p.id_payment_voucher as id,p.identificador_documento as identificador, p.uuid, " +
            "p.estado,p.estado_sunat as estadoSunat, p.mensaje_respuesta as mensajeRespuesta " +
            "from payment_voucher p " +
            "where p.identificador_documento = ?1 " +
            " ",nativeQuery = true)
    PaymentVoucherInterDto findByIdentificadorDocumentoName(String identificador);

    @Query(value = "select distinct p.ruc_emisor from payment_voucher p " +
            "where p.tipo_comprobante = '03' and p.estado = '01' " +
            "and p.fecha_emision_date <= to_timestamp(?1, 'YYYY-MM-DD') " +
            "and p.fecha_registro <= to_timestamp(?1, 'YYYY-MM-DD') ",nativeQuery = true)
    List<String> getCompaniesWithBoleta01(String diaAnterior);

    @Query(value = "select distinct p.fecha_emision from payment_voucher p " +
            "where p.tipo_comprobante = '03' and p.estado = '01' " +
            "and p.fecha_emision_date <= to_timestamp(?1, 'YYYY-MM-DD') " +
            "and p.fecha_registro <= to_timestamp(?1, 'YYYY-MM-DD') " +
            "and p.ruc_emisor = ?2 ",nativeQuery = true)
    List<String> getFechasWithBoleta01(String diaAnterior, String company);


    @Query(value = "SELECT p.id_payment_voucher as id,p.identificador_documento as identificador,p.fecha_emision_date as fechaEmision," +
            "p.estado,p.estado_sunat as estadoSunat,p.ruc_emisor as emisor FROM payment_voucher p "
            + "where (p.fecha_emision_date >= to_timestamp(?1, 'YYYY-MM-DD')) "
            + "order by p.fecha_emision_date desc, p.id_payment_voucher desc",nativeQuery = true)
    List<PaymentVoucherInterDto> findAllNoEnviados(String fechaHaceSieteDias);

    @Query(value = "select distinct(p.fecha_emision) from payment_voucher p " +
            "inner join payment_voucher pv on pv.ruc_emisor = p.ruc_emisor and pv.serie = p.serie_afectado " +
            "and pv.numero =p.numero_afectado and pv.tipo_comprobante in('03','01') " +
            "where "
            + " p.tipo_comprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "') "
            + "and p.estado_item in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and pv.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_ACEPTADO
            + "' and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "",nativeQuery = true)
    public List<String> getListFechasPendientesNotas();

    @Query(value = "select distinct(p.ruc_emisor) from payment_voucher p " +
            "inner join payment_voucher pv on pv.ruc_emisor = p.ruc_emisor and pv.serie = p.serie_afectado " +
            "and pv.numero =p.numero_afectado and pv.tipo_comprobante in('03','01') " +
            "where "
            + " p.tipo_comprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "') "
            + "and p.estado_item in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and pv.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_ACEPTADO
            + "' and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "",nativeQuery = true)
    public List<String> getListRucsPendientesNotas();

    @Query(value = "SELECT COUNT(p) FROM payment_voucher p "
            + "left join branch_offices o on p.oficina_id = o.id  "
            + "where p.ruc_emisor = ?1 and p.fecha_emision_date between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and (?4 is null or p.tipo_comprobante like ?4) and (?5 is null or p.num_doc_ident_receptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 is null or p.numero = ?7) and (?8 is null or o.id = ?8) ",nativeQuery = true)
    Long findAllSerchCount(String rucEmisor,
                           String fechaEmisionDesde, String fechaEmisionHasta,
                           String tipoComprobante, String numDocIdentReceptor,
                           String serie, Integer numero, Integer idOficina);

    @Query(value = "SELECT COUNT(p) FROM payment_voucher p "
            + "where p.ruc_emisor = ?1 ",nativeQuery = true)
    Integer findAllByRucCount(String ruc);

    @Query(value = "select pv.id_payment_voucher as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, " +
            "pvf.tipo_archivo as tipo from register_file_upload u \n" +
            "inner join payment_voucher_file pvf on pvf.id_register_file_send = u.id_register_file_send \n" +
            "inner join payment_voucher pv on pv.id_payment_voucher = pvf.id_payment_voucher \n" +
            "where pv.id_payment_voucher = ?1 and pv.uuid = ?2 and pvf.tipo_archivo = ?3 \n" +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findByIdPaymentVoucherAndUuidTipo(Long idPaymentVoucher, String uuid, String tipo);

    @Query(value = "SELECT p.id_payment_voucher as id,p.identificador_documento as identificador,p.fecha_emision_date as fechaEmision," +
            "p.estado,p.estado_sunat as estadoSunat,p.ruc_emisor as emisor, p.tipo_comprobante as tipoComprobante, p.serie , p.numero FROM payment_voucher p " +
            "where p.id_payment_voucher = ?1 and p.uuid = ?2 \n" ,nativeQuery = true)
    PaymentVoucherInterDto findByIdPaymentVoucherAndUuid(Long idPaymentVoucher, String uuid);

    @Query(value = "select p.ruc_emisor as emisor, c.razon_social as razon, c.email as email, p.identificador_documento as identificador, " +
            "p.fecha_emision as fechaEmision, p.fecha_registro as fechaRegistro," +
            "p.denominacion_receptor as denoReceptor, p.monto_imp_total_venta as montoImporte from payment_voucher p " +
            "inner join company c on c.bucket = p.ruc_emisor "
            + "where p.tipo_comprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_FACTURA + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "') "
            + "and p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_REGISTRADO + "' "
            + "and p.fecha_emision = ?1 order by p.ruc_emisor " ,nativeQuery = true)
    List<PaymentVoucherInterDto> getRucVoucherSex(String hace6DiasString);

    @Query(value = "select p.id_payment_voucher as id, p.ruc_emisor as emisor from payment_voucher p "
            + "where p.tipo_comprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_FACTURA + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "') "
            + "and p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_REGISTRADO + "' "
            + "and p.fecha_emision = ?1 order by p.ruc_emisor ",nativeQuery = true)
    List<PaymentVoucherInterDto> getRucVoucherSeven(String hace7DiasString);













    









    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') " +
            "and ( ?4 is null or p.tipoComprobante like ?4) "
            + "and (?5 is null or p.numDocIdentReceptor like ?5) "
            + "and (?6 is null or p.serie like ?6) "+
            "and (?7 is null or p.numero = ?7) and (?8 is null or o.id = ?8) "
            + "order by p.fechaEmisionDate desc, p.numero desc")
    List<PaymentVoucherEntity> findAllSerch(String rucEmisor,String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie,Integer numero, Integer idOficina);


    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and ( ?4 is null or p.tipoComprobante like ?4) "
            + "and (?5 is null or p.serie like ?5) " +
            " order by p.fechaEmisionDate asc,p.serie asc,p.numero asc")
    List<PaymentVoucherEntity> findAllByRuc(String rucEmisor,String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante,String serie);

    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 ")
    List<PaymentVoucherEntity> findAllByRuc(String rucEmisor);

    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and (?4 is null or p.tipoComprobante like ?4) and (?5 is null or p.numDocIdentReceptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 is null or p.numero = ?7) and (?8 is null or o.id = ?8) "
            + "order by p.fechaEmisionDate desc, p.numero desc")
    List<PaymentVoucherEntity> findAllSerchForPage(String rucEmisor,
                                                   String fechaEmisionDesde, String fechaEmisionHasta,
                                                   String tipoComprobante, String numDocIdentReceptor,
                                                   String serie, Integer numero, Integer idOficina, Pageable pageable);



    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.fechaEmision = :fechaEmision "
            + "and p.tipoComprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_BOLETA + "') "
            + "and p.estadoItem in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "order by p.tipoComprobante asc, p.fechaEmisionDate asc")
    public List<PaymentVoucherEntity> getListPaymentVoucherForSummaryDocuments(
            @Param("rucEmisor") String rucEmisor,
            @Param("fechaEmision") String fechaEmision);





    // Creando Ahora

    /*@Query("select p FROM PaymentVoucherEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.fechaEmision = :fechaEmision "
            + "and p.tipoComprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "') "
            + "and p.estadoItem in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "order by p.tipoComprobante asc, p.fechaEmisionDate asc")/**/
    @Query(value = "select p.* from payment_voucher p " +
            "inner join payment_voucher pv on pv.ruc_emisor = p.ruc_emisor and pv.serie = p.serie_afectado " +
            "and pv.numero =p.numero_afectado and pv.tipo_comprobante in('03','01') " +
            "where p.ruc_emisor = :rucEmisor "
            + "and p.fecha_emision = :fechaEmision "
            + "and p.tipo_comprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "') "
            + "and p.estado_item in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and pv.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_ACEPTADO
            + "' and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "order by p.fecha_emision_date, p.tipo_comprobante asc",nativeQuery = true)
    public List<PaymentVoucherEntity> getListPaymentVoucherForSummaryDocumentsNotaCredito(
            @Param("rucEmisor") String rucEmisor,
            @Param("fechaEmision") String fechaEmision);






    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.fechaEmision = :fechaEmision "
            + "and p.tipoComprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_BOLETA + "') "
            + "and p.estadoItem in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ") "
            + "and p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "order by p.tipoComprobante asc, p.fechaEmisionDate asc")
    public List<PaymentVoucherEntity> getListPaymentVoucherForSummaryDocumentsProceso(
            @Param("rucEmisor") String rucEmisor,
            @Param("fechaEmision") String fechaEmision);



    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.fechaEmision = :fechaEmision "
            + "and p.tipoComprobante = :tipo "
            + "and p.serie = :serie "
            + "and p.numero = :numero "
            + "and p.tipoComprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_BOLETA + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "') "
            + "and p.estadoItem in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and p.estado != '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' ")
    List<PaymentVoucherEntity> getListPaymentVoucherSpecificForSummaryDocuments(
            @Param("rucEmisor") String rucEmisor,
            @Param("fechaEmision") String fechaEmision,
            @Param("tipo") String tipo,
            @Param("serie") String serie,
            @Param("numero") Integer numero);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estadoAnterior = p.estado, "
            + "p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "', "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.idPaymentVoucher in :ids")
    public void updateStateToSendSunatForSummaryDocuments(
            @Param("ids") List<Long> ids,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estadoAnterior = p.estado, "
            + "p.estado = :estadoPendienteAnulacion, "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.identificadorDocumento in :identificadorComprobantes")
    public void updateStateToSendSunatForVoidedDocuments(
            @Param("identificadorComprobantes") List<String> identificadorComprobantes,
            @Param("estadoPendienteAnulacion") String estadoPendienteAnulacion,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);


    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_REGISTRADO + "', "
            + "p.estadoSunat = '" + ConstantesParameter.ESTADO_SUNAT_NO_ENVIADO + "', "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.identificadorDocumento in (:identificadorComprobantes) and p.estadoSunat <> '"+ConstantesParameter.ESTADO_SUNAT_ACEPTADO+"'")
    public void updateComprobantesOnResumenError(
            @Param("identificadorComprobantes") List<String> identificadorComprobantes,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);



    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_ANULADO + "', "
            + "p.estadoSunat = '" + ConstantesParameter.ESTADO_SUNAT_ANULADO + "', "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.identificadorDocumento in :identificadorComprobantes")
    public void updateComprobantesByBajaDocumentos(
            @Param("identificadorComprobantes") List<String> identificadorComprobantes,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = :estado, "
            + "p.estadoSunat = :estadoSunat, "
            + "p.estadoItem = " + ConstantesParameter.STATE_ITEM_RESPUESTA_SUNAT + ", "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.identificadorDocumento in :identificadorComprobantes")
    public void updateComprobantesBySummaryDocuments(
            @Param("identificadorComprobantes") List<String> identificadorComprobantes,
            @Param("estado") String estado,
            @Param("estadoSunat") String estadoSunat,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);

    @Query("select p from PaymentVoucherEntity p "
            + "where p.identificadorDocumento = :idDocumento")
    public PaymentVoucherEntity getIdentificadorDocument(@Param("idDocumento") String idDocumento);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estadoItem = " + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ", "
            + "p.estadoAnterior = p.estado, "
            + "p.estado = :estadoComprobante, "
            + "p.estadoSunat = :estadoSunat, "
            + "p.motivoAnulacion = :motivoAnulacion, "
            + "p.userNameModify = :usuario, "
            + "p.fechaModificacion = :fechaModificacion "
            + "where p.identificadorDocumento = :identificador")
    public void updateAnulacionBoletasAndNotasAsociadas(
            @Param("identificador") String identificador,
            @Param("estadoComprobante") String estadoComprobante,
            @Param("estadoSunat") String estadoSunat,
            @Param("motivoAnulacion") String motivoAnulacion,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion);

    @Query("select p.tipoComprobante, p.serie, p.numero, p.fechaEmision, "
            + "p.estado, p.codigoMoneda "
            + "from PaymentVoucherEntity p "
            + "where p.identificadorDocumento in :identificadores")
    public List<Object[]> getListaDatosDocumentosByIdentificadores(
            @Param("identificadores") List<String> identificadores);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = :estadoComprobante, "
            + "p.estadoSunat = :estadoEnSunat, "
            + "p.mensajeRespuesta = :mensajeRespuesta, "
            + "p.codigosRespuestaSunat = :codigosRespuesta "
            + "where p.idPaymentVoucher = :idPaymentVoucher")
    public void updateEstadoComprobante(
            @Param("idPaymentVoucher") Long idPaymentVoucher,
            @Param("estadoComprobante") String estadoComprobante,
            @Param("estadoEnSunat") String estadoEnSunat,
            @Param("mensajeRespuesta") String mensajeRespuesta,
            @Param("codigosRespuesta") String codigosRespuesta);

    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = :estadoComprobante, "
            + "p.mensajeRespuesta = :mensajeRespuesta, "
            + "p.codigosRespuestaSunat = :codigosRespuesta "
            + "where p.idPaymentVoucher = :idPaymentVoucher")
    public void updateEstadoComprobante(
            @Param("idPaymentVoucher") Long idPaymentVoucher,
            @Param("estadoComprobante") String estadoComprobante,
            @Param("mensajeRespuesta") String mensajeRespuesta,
            @Param("codigosRespuesta") String codigosRespuesta);

    PaymentVoucherEntity findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(
            String ruc, String tipo, String serie, Integer numero);


    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.estadoSunat not in ('" + ConstantesParameter.ESTADO_SUNAT_ACEPTADO + "', '" + ConstantesParameter.ESTADO_SUNAT_ANULADO + "', '" + ConstantesParameter.ESTADO_SUNAT_RECHAZADO + "') "
            + "and p.tipoComprobante in ('" + ConstantesSunat.TIPO_DOCUMENTO_FACTURA + "', '" + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "' ,'" + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "') "
            + "and p.estadoItem is null "
            + "and p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_REGISTRADO + "' "
    )
    public List<PaymentVoucherEntity> getListPaymentVoucherPorEnviarSunat(
            @Param("rucEmisor") String rucEmisor);


    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_REGISTRADO + "'"
            + "where p.idPaymentVoucher = :idPaymentVoucher ")
    public void updateEstadoComprobanteRegistrado(
            @Param("idPaymentVoucher") Long idPaymentVoucher);

/*    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_RECHAZADO + "', "
            + "p.estadoSunat = '" + ConstantesParameter.ESTADO_SUNAT_RECHAZADO + "' "
            + "where p.idPaymentVoucher = :idPaymentVoucher ")
    public void updateEstadoComprobanteRechazado(
            @Param("idPaymentVoucher") Long idPaymentVoucher
    );*/

    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.estado = '" + ConstantesParameter.ESTADO_COMPROBANTE_NO_ENVIADO + "' "
            + "and p.estadoSunat = '" + ConstantesParameter.ESTADO_SUNAT_NO_ENVIADO + "'	"
            + "and p.estadoItem is null "
    )
    public List<PaymentVoucherEntity> getListPaymentVoucherErrores();


    @Query(value = "SELECT p.id_payment_voucher as id, p.fecha_emision as fechaEmision,p.tipo_comprobante as tipoComprobante,p.serie,p.numero," +
            "p.tip_doc_ident_receptor as tipoDocIdentReceptor,p.num_doc_ident_receptor as numDocIdentReceptor,p.denominacion_receptor as denominacionReceptor," +
            "p.codigo_moneda as codigoMoneda,COALESCE(p.total_oper_gravada,0) as totalValorVentaOperacionGravada,COALESCE(p.total_oper_exonerada,0) as totalValorVentaOperacionExonerada," +
            "COALESCE(p.total_oper_inafecta,0) as totalValorVentaOperacionInafecta," +
            "(case WHEN p.estado = '08' THEN 0  WHEN p.tipo_comprobante = '07' THEN p.monto_imp_total_venta *-1  ELSE p.monto_imp_total_venta end) as montoImporteTotalVenta, " +
            "p.estado,p.estado_sunat as estadoSunat, COALESCE(p.monto_descuento_global,0) as montoDescuentoGlobal,COALESCE(p.sumatoria_igv,0) as sumatoriaIGV, " +
            "d.cod_producto as codigoProducto, d.descripcion_producto as descripcion, d.cantidad, COALESCE(d.valor_unit,0) as valorUnitario," +
            "COALESCE(d.valor_venta,0) as valorVenta, COALESCE(d.descuento,0) as descuento " +
            "FROM payment_voucher p " +
            "inner join details_payment_voucher d on d.id_payment_voucher = p.id_payment_voucher "
            + "where p.ruc_emisor = ?1 and p.fecha_emision_date between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and (?4 is null or p.tipo_comprobante like ?4) "
            + "and (?5 is null or p.serie like ?5) "
            + "order by p.id_payment_voucher desc,p.fecha_emision_date desc, p.numero desc",nativeQuery=true)
    List<PaymentVoucherInterDto> findAllSerchReport(String rucEmisor, String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante,
                                                  String serie);

   @Query(value = "SELECT p.id_payment_voucher as id, p.fecha_emision as fechaEmision,p.tipo_comprobante as tipoComprobante,p.serie,p.numero," +
           "p.tip_doc_ident_receptor as tipoDocIdentReceptor,p.num_doc_ident_receptor as numDocIdentReceptor,p.denominacion_receptor as denominacionReceptor," +
           "p.codigo_moneda as codigoMoneda,COALESCE(p.total_oper_gravada,0) as totalValorVentaOperacionGravada,COALESCE(p.total_oper_exonerada,0) as totalValorVentaOperacionExonerada," +
           "COALESCE(p.total_oper_inafecta,0) as totalValorVentaOperacionInafecta," +
           "(case WHEN p.estado = '08' THEN 0  WHEN p.tipo_comprobante = '07' THEN p.monto_imp_total_venta *-1  ELSE p.monto_imp_total_venta end) as montoImporteTotalVenta, p.estado,p.estado_sunat as estadoSunat, " +
           "p.monto_descuento_global as montoDescuentoGlobal,COALESCE(p.sumatoria_igv,0) as sumatoriaIGV " +
            "FROM payment_voucher p "
            + "where p.ruc_emisor = ?1 and p.fecha_emision_date between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') "
            + "and (?4 is null or p.tipo_comprobante like ?4) "
            + "and (?5 is null or p.serie like ?5) "
            + "order by p.fecha_emision_date desc, p.numero desc",nativeQuery=true)
    List<PaymentVoucherInterDto> findAllSerchReportContador(String rucEmisor, String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante,
                                                  String serie);





    @Modifying
    @Query("update PaymentVoucherEntity p "
            + "set p.estadoAnticipo = 1 "
            + "where p.identificadorDocumento = :identificadorComprobante ")
    public void updateComprobantesByEstadoAnticipo(
            @Param("identificadorComprobante") String identificadorDocumento);

    @Query(value = "SELECT COUNT(p) FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between ?2 and ?3 "
            + "and (?4 is null or p.tipoComprobante like ?4) and (?5 is null or p.numDocIdentReceptor like ?5) " +
            "and p.estado like ?9 and (?6 is null or p.serie like ?6) and (?7 is null) and (?8 is null or o.id = ?8) ")
    Long findAllAndEstadoSerchCount(String rucEmisor,
                                    Date fechaEmisionDesde, Date fechaEmisionHasta,
                                    String tipoComprobante, String numDocIdentReceptor,
                                    String serie, Integer numero, Integer idOficina, String estadoSunat);


    @Query(value = "SELECT p.fecha_emision as fechaEmision, p.tipo_comprobante as tipoComprobante, p.serie, p.numero, p.tip_comprob_afectado as tipoComprobanteAfectado,\n" +
            "p.serie_afectado as serieAfectado, p.numero_afectado as numeroAfectado, p.num_doc_ident_receptor as numDocIdentReceptor, p.denominacion_receptor as denominacionReceptor,\n" +
            "p.codigo_moneda as codigoMoneda, p.monto_imp_total_venta as montoImporteTotalVenta,p.estado , p.fecha_registro as fechaRegistro, p.ruc_emisor as rucEmisor,\n" +
            "p.estado_sunat as estadoSunat,p.identificador_documento as identificadorBaja, p.email_receptor as emailReceptor, p.id_payment_voucher as idPaymentVoucher,\n" +
            "p.identificador_documento as identificadorDocumento, p.uuid, p.mensaje_respuesta as mensajeRespuesta " +
            " FROM payment_voucher p  "
            + "where p.ruc_emisor = ?1 and p.fecha_emision_date between ?2 and ?3 "
            + "and (?4 is null or p.tipo_comprobante like ?4) and (?5 is null or p.num_doc_ident_receptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 = 0 or p.numero = ?7) and (?8 = 0 or p.oficina_id = ?8) "
            + "and p.estado like ?9 "
            + "order by p.fecha_emision_date desc, p.numero desc OFFSET ?10 LIMIT ?11 ",nativeQuery=true)
    List<PaymentVoucherInterDto> findAllAndEstadoSerchForPages(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor,
                                                             String serie, Integer numero, Integer idOficina, String estadoSunat, Integer numPagina, Integer perPage );

    @Query(value = "SELECT count( p.id_payment_voucher ) \n" +
            " FROM payment_voucher p  "
            + "where p.ruc_emisor = ?1 and p.fecha_emision_date between ?2 and ?3 "
            + "and (?4 is null or p.tipo_comprobante like ?4) and (?5 is null or p.num_doc_ident_receptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 = 0 or p.numero = ?7) and (?8 = 0 or p.oficina_id = ?8) "
            + "and p.estado like ?9 "
           ,nativeQuery=true)
    Integer countAllAndEstadoSerchForPages(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor,
                                                               String serie, Integer numero, Integer idOficina, String estadoSunat );





    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between ?2 and ?3 "
            + "and (?4 is null or p.tipoComprobante like ?4) and (?5 is null or p.numDocIdentReceptor like ?5) "
            + "and (?6 is null or p.serie like ?6) and (?7 = 0 or p.numero = ?7) and (?8 = 0 or o.id = ?8) "
            + "and p.estado like ?9 "
            + "order by p.fechaEmisionDate desc, p.numero desc")
    List<PaymentVoucherEntity> findAllForExportExcel(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor,
                                                             String serie, Integer numero, Integer idOficina, String estadoSunat);


    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between ?2 and ?3 "
            + "and (?4 is null or p.tipoComprobante like ?4) and (?5 is null or p.numDocIdentReceptor like ?5) "
            + "and (?6 is null or p.serie like ?6) "
            )
    List<PaymentVoucherEntity> findAllTest(String rucEmisor, Date fechaEmisionDesde, Date fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor,
                                                             String serie, Pageable pageable);



    PaymentVoucherEntity findFirst1ByTipoComprobanteAndSerieAndRucEmisorOrderByNumeroDesc(String tipoComprobante,
                                                                                          String serie, String ruc);



    List<PaymentVoucherEntity> findAllByFechaEmisionDateBetweenAndTipoComprobanteInAndNumDocIdentReceptorAndSerieStartingWithAndRucEmisorOrderByNumDocIdentReceptorAscSerieAsc(
            Date desde, Date hasta, List<String> tipoComprobante, String numDocIdentReceptor, String serie,
            String rucEmisor);

    List<PaymentVoucherEntity> findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionOrderByNumDocIdentReceptor(
            List<String> tipoComprobante, String numDocIdentReceptor, String rucEmisor, String tipoOperacion);

    List<PaymentVoucherEntity> findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionAndEstadoOrderByNumDocIdentReceptor(
            List<String> tipoComprobante, String numDocIdentReceptor, String rucEmisor, String tipoOperacion, String estado);

    List<PaymentVoucherEntity> findByIdPaymentVoucherIn(List<Long> ids);

    @Query("select p FROM PaymentVoucherEntity p "
            + "where p.tipoComprobante in ('"
            + ConstantesSunat.TIPO_DOCUMENTO_BOLETA + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + "', '"
            + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + "') "
            + "and p.estadoItem in ("
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION + ","
            + ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION + ") "
            + "and p.estado <> '" + ConstantesParameter.ESTADO_COMPROBANTE_PROCESO_ENVIO + "' "
            + "order by p.tipoComprobante asc, p.fechaEmisionDate asc")
    List<PaymentVoucher> getListPaymentVoucherByIdSummary(@Param("idSummary") Long idSummary);





    @Query("select p.fechaEmision, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.fechaEmision " )
    List<Object[]> getPaymentsByDay(@Param("ruc") String ruc, @Param("time") Date time);

    @Query("select p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmision = :time "
            + "GROUP BY p.tipoComprobante " )
    List<Object[]> getPaymentsByType(@Param("ruc") String ruc, @Param("time") String time);

    @Query("select p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeMonth(@Param("ruc") String ruc, @Param("time") Date time);

    @Query("select p.estado, p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmision = :time "
            + "GROUP BY p.estado,p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeAndState(@Param("ruc") String ruc, @Param("time") String time);

    @Query("select p.estado, p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.estado,p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeAndStateMonth(@Param("ruc") String ruc, @Param("time") Date time);

    @Query( "select p.fechaEmision, p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.fechaEmision, p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeAndDay(@Param("ruc") String ruc, @Param("time") Date time);

    @Query( "select p.userName, p.fechaEmision, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.userName, p.fechaEmision " )
    List<Object[]> getPaymentsByUserAndDay(@Param("ruc") String ruc, @Param("time") Date time);

    @Query( "select p.userName, MONTH(fechaEmisionDate), count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.userName, MONTH(fechaEmisionDate) " )
    List<Object[]> getPaymentsByUserAndMonth(@Param("ruc") String ruc, @Param("time") Date time);

    @Query("select p.fechaEmision, p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY p.fechaEmision, p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeWeek(@Param("ruc") String ruc, @Param("time") Date time);

    @Query("select MONTH(p.fechaEmisionDate), p.tipoComprobante, count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY MONTH(p.fechaEmisionDate), p.tipoComprobante " )
    List<Object[]> getPaymentsByTypeMonths(@Param("ruc") String ruc, @Param("time") Date time);

    @Query("select MONTH(fechaEmisionDate), count(p.id) " +
            "from PaymentVoucherEntity p "
            + "where p.rucEmisor = :ruc and p.fechaEmisionDate >= :time "
            + "GROUP BY MONTH(fechaEmisionDate) " )
    List<Object[]> getPaymentsByMonth(@Param("ruc") String ruc, @Param("time") Date time);

    @Query(value = "select pv.* " +
            "from payment_voucher pv " +
            "inner join (select id_payment_voucher_reference as reference,MAX(n_cuota) as max_n_cuota from payment_voucher where tipo_comprobante='01' and tipo_transaccion=2 group by id_payment_voucher_reference having id_payment_voucher_reference is not null) maximos " +
            "on pv.id_payment_voucher_reference=maximos.reference and pv.n_cuota=maximos.max_n_cuota " +
            "where pv.ruc_emisor= :rucEmisor and pv.num_doc_ident_receptor= :numDocIdentReceptor and pv.monto_pendiente>0 " +
            "and tipo_comprobante='01' and tipo_transaccion=2",nativeQuery = true)
    List<PaymentVoucherEntity> getPaymentVocuherByCredito(String numDocIdentReceptor, String rucEmisor);

    @Query(value = "select d.id_payment_voucher as idPayment, d.descripcion_producto as descripcion, " +
            "d.cod_unid_medida as codigoUnidadMedida,d.cantidad, d.precio_venta_unit as precioVentaUnitario, " +
            "d.descuento, d.valor_venta as valorVenta, d.afectacion_igv as afectacionIGV " +
            "from details_payment_voucher d " +
            "where d.id_payment_voucher =  :idPayment ",nativeQuery = true)
    List<DetailsPaymentInterDto> findDetailsById(Integer idPayment);


    @Query("SELECT c.ruc FROM CompanyEntity c WHERE c.estado = "
            + "'" + ConstantesParameter.REGISTRO_ACTIVO + "'")
    List<String> getCompaniesForSummaryDocuments();


}

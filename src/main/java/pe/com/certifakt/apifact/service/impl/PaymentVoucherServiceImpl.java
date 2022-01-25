package pe.com.certifakt.apifact.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dao.ComprobantesDAO;
import pe.com.certifakt.apifact.dto.*;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.exception.SignedException;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.*;
import pe.com.certifakt.apifact.util.*;
import pe.com.certifakt.apifact.validate.ComprobanteValidate;
import pe.com.certifakt.apifact.validate.PaymentVoucherParamsInputValidate;
import pe.com.certifakt.apifact.validate.PaymentVoucherValidate;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentVoucherServiceImpl implements PaymentVoucherService {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    DateFormat dateFormathora = new SimpleDateFormat("HH:mm:ss");
    @Value("${apifact.email}")
    private String emailFrom;
    @Value("${mercadopago.token}")
    private String mercadopagoToken;
    @Value("${paypal.clienteid}")
    private String clienteid;
    @Value("${paypal.secretid}")
    private String secretid;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;
    @Autowired
    private DetailsPaymentVoucherRepository detailsPaymentVoucherRepository;
    @Autowired
    private GuiaRelacionadaRepository guiaRelacionadaRepository;
    @Autowired
    private AditionalFieldRepository aditionalFieldRepository;
    @Autowired
    private CuotaPaymentVoucherRepository cuotaPaymentVoucherRepository;
    @Autowired
    private AnticipoRepository anticipoRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private SummaryDocumentRepository summaryDocumentRepository;
    @Autowired
    private TmpVoucherSendBillRepository tmpVoucherSendBillRepository;
    @Autowired
    private TypeFieldRepository typeFieldRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MerkdoPagoRepository merkdoPagoRepository;
    @Autowired
    private PaymentPaypalRepository paymentPaypalRepository;
    @Autowired
    private PaymentVoucherService paymentVoucherService;
    @Autowired
    private ComprobantesService comprobantesService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SunatService sunatService;

    @Autowired
    private PaymentVoucherValidate validate;
    @Autowired
    private ComprobanteValidate validateComprobantes;
    @Autowired
    private PaymentVoucherParamsInputValidate validateParams;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private AmazonS3ClientService amazonS3ClientService;
    @Autowired
    private ComprobantesDAO comprobantesDao;

    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private EmailCompanyNotifyRepository emailCompanyNotifyRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ComunicationSunatService comunicationSunatService;

    public PaymentVoucherEntity registrarPaymentVoucher(PaymentVoucher voucher, Long idRegisterFile, Boolean isEdit, PaymentVoucherEntity paymentVoucherOld, String estado,
                                                        String estadoAnterior,
                                                        String estadoEnSunat, Integer estadoItem, String mensajeRespuesta, String userRegistro, String userModificacion,
                                                        Timestamp fechaRegistro, Timestamp fechaModificacion,
                                                        OperacionLogEnum operacionLog) throws ServiceException {

        PaymentVoucherEntity entity = new PaymentVoucherEntity();
        CompanyEntity company = companyRepository.findByRuc(voucher.getRucEmisor());

        System.out.println("SEGUIMIENTO VOUCHER");
        System.out.println(voucher);
        //SI ESTA EDITANDO
        if (isEdit) {

            List<DetailsPaymentVoucherEntity> items = paymentVoucherOld.getDetailsPaymentVouchers();
            List<AnticipoEntity> anticipos = paymentVoucherOld.getAnticipos();
            List<AditionalFieldEntity> adicionales = paymentVoucherOld.getAditionalFields();
            List<CuotasPaymentVoucherEntity> cuotasPayment = paymentVoucherOld.getCuotas();
            List<GuiaRelacionadaEntity> guias = paymentVoucherOld.getGuiasRelacionadas();

            // LIMPIANDO DETALLES

            if (items != null && !items.isEmpty()) {

                for (DetailsPaymentVoucherEntity item : items) {

                    detailsPaymentVoucherRepository.deleteDetailsPaymentVoucher(item.getIdDetailsPayment());
                }
            }
            if (anticipos != null && !anticipos.isEmpty()) {

                for (AnticipoEntity anticipo : anticipos) {

                    anticipoRepository.deleteAnticipo(anticipo.getIdAnticipoPayment());
                }
            }

            if (adicionales != null && !adicionales.isEmpty()) {

                for (AditionalFieldEntity adicional : adicionales) {

                    aditionalFieldRepository.deleteAditionakField(adicional.getId());

                }
            }
            if (cuotasPayment != null && !cuotasPayment.isEmpty()) {

                for (CuotasPaymentVoucherEntity cuota : cuotasPayment) {

                    cuotaPaymentVoucherRepository.deleteCuotaPayment(cuota.getId());

                }
            }
            if (guias != null && !guias.isEmpty()) {

                for (GuiaRelacionadaEntity guia : guias) {

                    guiaRelacionadaRepository.deleteGuiaRelacionada(guia.getIdGuiaPayment());
                }

            }

            entity.setIdPaymentVoucher(paymentVoucherOld.getIdPaymentVoucher());
            entity.setUuid(paymentVoucherOld.getUuid());
            entity.setPaymentVoucherFiles(paymentVoucherOld.getPaymentVoucherFiles());
            entity.setFechaRegistro(paymentVoucherOld.getFechaRegistro());
        }

        StringBuilder msgLog = new StringBuilder();

        entity.setSerie(voucher.getSerie());
        entity.setNumero(voucher.getNumero());
        entity.setFechaEmision(voucher.getFechaEmision());
        entity.setFechaEmisionDate(UtilFormat.fechaDate(voucher.getFechaEmision()));
        entity.setHoraEmision(voucher.getHoraEmision());
        entity.setTipoComprobante(voucher.getTipoComprobante());
        entity.setCodigoMoneda(voucher.getCodigoMoneda());
        entity.setFechaVencimiento(voucher.getFechaVencimiento());
        entity.setTipoOperacion(voucher.getCodigoTipoOperacion());

        entity.setRucEmisor(voucher.getRucEmisor());
        entity.setCodigoLocalAnexo(voucher.getCodigoLocalAnexoEmisor());

        entity.setTipoDocIdentReceptor(voucher.getTipoDocumentoReceptor());
        entity.setNumDocIdentReceptor(voucher.getNumeroDocumentoReceptor());
        entity.setDenominacionReceptor(voucher.getDenominacionReceptor());

        entity.setEmailReceptor(voucher.getEmailReceptor());
        entity.setDireccionReceptor(voucher.getDireccionReceptor());

        entity.setCodigoTipoDocumentoRelacionado(voucher.getCodigoTipoOtroDocumentoRelacionado());
        entity.setSerieNumeroDocumentoRelacionado(voucher.getSerieNumeroOtroDocumentoRelacionado());

        entity.setTotalValorVentaOperacionExportada(voucher.getTotalValorVentaExportacion());
        entity.setTotalValorVentaOperacionGravada(voucher.getTotalValorVentaGravada());
        entity.setTotalValorVentaOperacionInafecta(voucher.getTotalValorVentaInafecta());
        entity.setTotalValorVentaOperacionExonerada(voucher.getTotalValorVentaExonerada());
        entity.setTotalValorVentaOperacionGratuita(voucher.getTotalValorVentaGratuita());
        entity.setTotalValorVentaGravadaIVAP(voucher.getTotalValorVentaGravadaIVAP());
        entity.setTotalValorBaseIsc(voucher.getTotalValorBaseIsc());
        entity.setTotalValorBaseOtrosTributos(voucher.getTotalValorBaseOtrosTributos());
        entity.setTotalDescuento(voucher.getTotalDescuento());

        entity.setMontoDescuentoGlobal(voucher.getDescuentoGlobales());
        entity.setMontoSumatorioOtrosCargos(voucher.getSumatoriaOtrosCargos());
        entity.setMontoImporteTotalVenta(voucher.getImporteTotalVenta());
        entity.setMontoTotalAnticipos(voucher.getTotalAnticipos());

        entity.setSumatoriaIGV(voucher.getTotalIgv());
        entity.setSumatoriaISC(voucher.getTotalIsc());
        entity.setSumatoriaTributosOperacionGratuita(voucher.getTotalImpOperGratuita());
        entity.setSumatoriaOtrosTributos(voucher.getTotalOtrostributos());
        entity.setSumatoriaIvap(voucher.getTotalIvap());

        entity.setSerieAfectado(voucher.getSerieAfectado());
        entity.setNumeroAfectado(voucher.getNumeroAfectado());
        entity.setTipoComprobanteAfectado(voucher.getTipoComprobanteAfectado());
        entity.setMotivoNota(voucher.getMotivoNota());
        entity.setCodigoTipoNotaCredito(voucher.getCodigoTipoNotaCredito());
        entity.setCodigoTipoNotaDebito(voucher.getCodigoTipoNotaDebito());

        entity.setIdentificadorDocumento(voucher.getRucEmisor() + "-" + voucher.getTipoComprobante() + "-"
                + voucher.getSerie() + "-" + voucher.getNumero());

        entity.setEstado(estado);
        entity.setEstadoAnterior(estadoAnterior);
        entity.setEstadoItem(estadoItem);
        entity.setEstadoSunat(estadoEnSunat);
        entity.setMensajeRespuesta(mensajeRespuesta);
        if (!isEdit) {
            entity.setFechaRegistro(fechaRegistro);
        }
        entity.setFechaModificacion(fechaModificacion);
        entity.setUserName(userRegistro);
        entity.setUserNameModify(userRegistro);

        entity.setOrdenCompra(voucher.getOrdenCompra());
        entity.setUblVersion(voucher.getUblVersion());
        entity.setCodigoHash(voucher.getCodigoHash());

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            entity.addFile(PaymentVoucherFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }

        entity.setCodigoMedioPago(voucher.getCodigoMedioPago());
        entity.setCuentaFinancieraBeneficiario(voucher.getCuentaFinancieraBeneficiario());
        entity.setCodigoBienDetraccion(voucher.getCodigoBienDetraccion());
        entity.setPorcentajeDetraccion(voucher.getPorcentajeDetraccion());
        entity.setMontoDetraccion(voucher.getMontoDetraccion());
        entity.setDetraccion(voucher.getDetraccion());


        entity.setTipoTransaccion(voucher.getTipoTransaccion());
        entity.setMontoPendiente(voucher.getMontoPendiente());
        entity.setCantidadCuotas(voucher.getCantidadCuotas());
        entity.setPagoCuenta(voucher.getPagoCuenta());


        /*
        entity.setNCuota(voucher.getNCuota());
        entity.setMontoCuota(voucher.getMontoCuota());
        entity.setMontoPendiente(voucher.getMontoPendiente());*/

        if (voucher.getAnticipos() != null && !voucher.getAnticipos().isEmpty()) {
            for (Anticipo anticipo : voucher.getAnticipos()) {

                AnticipoEntity anticipoEntity = new AnticipoEntity();
                anticipoEntity.setMontoAnticipo(anticipo.getMontoAnticipado());
                anticipoEntity.setNumeroAnticipo(anticipo.getNumeroAnticipo());
                anticipoEntity.setSerieAnticipo(anticipo.getSerieAnticipo());
                anticipoEntity.setTipoDocumentoAnticipo(anticipo.getTipoDocumentoAnticipo());

                entity.addAnticipo(anticipoEntity);
            }
        }

        if (voucher.getCamposAdicionales() != null && !voucher.getCamposAdicionales().isEmpty()) {

            for (CampoAdicional campoAdicional : voucher.getCamposAdicionales()) {
                AditionalFieldEntity aditionalFieldEntity = new AditionalFieldEntity();
                TypeFieldEntity typeField = typeFieldRepository.findByName(campoAdicional.getNombreCampo());
                if (typeField != null)
                    aditionalFieldEntity.setTypeField(typeField);
                else {
                    typeField = new TypeFieldEntity();
                    typeField.setName(campoAdicional.getNombreCampo());
                    typeField.setCompanys(new ArrayList<>());
                    typeField.getCompanys().add(company);
                    typeField = typeFieldRepository.save(typeField);
                    aditionalFieldEntity.setTypeField(typeField);
                }
                aditionalFieldEntity.setValorCampo(campoAdicional.getValorCampo());
                entity.addAditionalField(aditionalFieldEntity);
            }
        }
        if(voucher.getCuotas() != null && !voucher.getCuotas().isEmpty()){
            for (PaymentVoucherCuota cuota: voucher.getCuotas() ) {
                CuotasPaymentVoucherEntity centity = new CuotasPaymentVoucherEntity();
                centity.setNumero(cuota.getNumero());
                centity.setMonto(cuota.getMonto());
                centity.setFecha(cuota.getFecha());
                entity.addCuotas(centity);
            }
        }

        if (voucher.getGuiasRelacionadas() != null && !voucher.getGuiasRelacionadas().isEmpty()) {

            for (GuiaRelacionada guiaRelacionada : voucher.getGuiasRelacionadas()) {
                GuiaRelacionadaEntity guiaRelacionadaEntity = new GuiaRelacionadaEntity();
                guiaRelacionadaEntity.setCodigoTipoGuia(guiaRelacionada.getCodigoTipoGuia());
                guiaRelacionadaEntity.setSerieNumeroGuia(guiaRelacionada.getSerieNumeroGuia());
                entity.addGuiaRelacionada(guiaRelacionadaEntity);
            }
        }

        for (PaymentVoucherLine item : voucher.getItems()) {

            DetailsPaymentVoucherEntity detailEntity = new DetailsPaymentVoucherEntity();

            detailEntity.setNumeroItem(item.getNumeroItem());
            detailEntity.setCantidad(item.getCantidad());
            detailEntity.setCodigoUnidadMedida(item.getCodigoUnidadMedida());

            detailEntity.setDescripcion(item.getDescripcion());
            detailEntity.setCodigoProducto(item.getCodigoProducto());
            detailEntity.setCodigoProductoSunat(item.getCodigoProductoSunat());
            detailEntity.setCodigoProductoGS1(item.getCodigoProductoGS1());

            detailEntity.setValorUnitario(item.getValorUnitario());
            detailEntity.setPrecioVentaUnitario(item.getPrecioVentaUnitario());
            detailEntity.setValorReferencialUnitario(item.getValorReferencialUnitario());

            detailEntity.setMontoBaseExonerado(item.getMontoBaseExonerado());
            detailEntity.setMontoBaseExportacion(item.getMontoBaseExportacion());
            detailEntity.setMontoBaseGratuito(item.getMontoBaseGratuito());
            detailEntity.setMontoBaseIgv(item.getMontoBaseIgv());
            detailEntity.setMontoBaseInafecto(item.getMontoBaseInafecto());
            detailEntity.setMontoBaseIsc(item.getMontoBaseIsc());
            detailEntity.setMontoBaseIvap(item.getIvap());
            detailEntity.setMontoBaseOtrosTributos(item.getMontoBaseOtrosTributos());

            detailEntity.setAfectacionIGV(item.getIgv());
            detailEntity.setSistemaISC(item.getIsc());
            detailEntity.setIvap(item.getIvap());
            detailEntity.setTributoVentaGratuita(item.getImpuestoVentaGratuita());
            detailEntity.setOtrosTributos(item.getOtrosTributos());

            detailEntity.setCodigoTipoAfectacionIGV(item.getCodigoTipoAfectacionIGV());
            detailEntity.setCodigoTipoSistemaISC(item.getCodigoTipoCalculoISC());

            detailEntity.setPorcentajeIgv(item.getPorcentajeIgv());
            detailEntity.setPorcentajeIsc(item.getPorcentajeIsc());
            detailEntity.setPorcentajeIvap(item.getPorcentajeIvap());
            detailEntity.setPorcentajeOtrosTributos(item.getPorcentajeOtrosTributos());
            detailEntity.setPorcentajeTributoVentaGratuita(item.getPorcentajeTributoVentaGratuita());

            detailEntity.setDescuento(item.getDescuento());
            detailEntity.setCodigoDescuento(item.getCodigoDescuento());
            detailEntity.setValorVenta(item.getValorVenta());

            detailEntity.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detailEntity.setDetalleViajeDetraccion(item.getDetalleViajeDetraccion());
            detailEntity.setUbigeoOrigenDetraccion(item.getUbigeoOrigenDetraccion());
            detailEntity.setDireccionOrigenDetraccion(item.getDireccionOrigenDetraccion());
            detailEntity.setUbigeoDestinoDetraccion(item.getUbigeoDestinoDetraccion());
            detailEntity.setDireccionDestinoDetraccion(item.getDireccionDestinoDetraccion());
            detailEntity.setValorServicioTransporte(item.getValorServicioTransporte());
            detailEntity.setValorCargaEfectiva(item.getValorCargaEfectiva());
            detailEntity.setValorCargaUtil(item.getValorCargaUtil());

            detailEntity.setHidroMatricula(item.getHidroMatricula());
            detailEntity.setHidroCantidad(item.getHidroCantidad());
            detailEntity.setHidroDescripcionTipo(item.getHidroDescripcionTipo());
            detailEntity.setHidroEmbarcacion(item.getHidroEmbarcacion());
            detailEntity.setHidroFechaDescarga(item.getHidroFechaDescarga());
            detailEntity.setHidroLugarDescarga(item.getHidroLugarDescarga());

            detailEntity.setMontoIcbper(item.getMontoIcbper());
            detailEntity.setMontoBaseIcbper(item.getMontoBaseIcbper());

            detailEntity.setUnidadManejo(item.getUnidadManejo());
            detailEntity.setInstruccionesEspeciales(item.getInstruccionesEspeciales());
            detailEntity.setMarca(item.getMarca());

            entity.addDetailsPaymentVoucher(detailEntity);
        }

        if (userRegistro != null) {
            Optional<User> usuario = userRepository.findByUsername(userRegistro);
            entity.setOficina(usuario.get().getOficina());
        } else {
            Optional<User> usuario = userRepository.findByUsername(userModificacion);
            entity.setOficina(usuario.get().getOficina());
        }
       /* if(voucher.getTipoTransaccion()==2){
            if(voucher.getNCuota()==1){
                entity.setIdPaymentVoucherReference(voucher.getRucEmisor() + "-" + voucher.getTipoComprobante() + "-"
                        + voucher.getSerie() + "-" + voucher.getNumero());
            }else{
                entity.setIdPaymentVoucherReference(voucher.getIdPaymentVoucherReference());
            }
        }*/

        entity = paymentVoucherRepository.save(entity);

        msgLog.setLength(0);
        msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}{").
                append("entity:").append(entity.toString()).append("}");
        Logger.register(TipoLogEnum.INFO, entity.getRucEmisor(), entity.getIdentificadorDocumento(),
                operacionLog, SubOperacionLogEnum.INSERT_BD_PAYMENTE_VOCUHER,
                msgLog.toString());

        return entity;

    }

    @Override
    public Map<String, Object> getSummaryDocumentsByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante) throws ServiceException {

        Map<String, Object> result = new HashMap<>();
        List<Long> ids = new ArrayList<>();

        Summary summary = buildSummaryDocumentsByFechaEmision(fechaEmision, rucEmisor, comprobante, ids);
        result.put(ConstantesParameter.PARAM_BEAN_SUMMARY, summary);
        result.put(ConstantesParameter.PARAM_LIST_IDS, ids);
        return result;
    }

    @Override
    public Map<String, Object> getSummaryNotaCreditoDocumentsByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante) throws ServiceException {
        Map<String, Object> result = new HashMap<>();
        List<Long> ids = new ArrayList<>();

        Summary summary = buildSummaryDocumentsNotaCreditoByFechaEmision(fechaEmision, rucEmisor, comprobante, ids);
        result.put(ConstantesParameter.PARAM_BEAN_SUMMARY, summary);
        result.put(ConstantesParameter.PARAM_LIST_IDS, ids);
        return result;
    }

    private Summary buildSummaryDocumentsByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante, List<Long> ids) throws ServiceException {

        Summary summaryByDay = null;
        Integer correlativoSummary = null;
        CompanyEntity company = companyRepository.findByRuc(rucEmisor);
        String tipoDocumentoEmisor = ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC;
        String denominacionEmisor = company.getRazonSocial();

        List<PaymentVoucherEntity> comprobantes = new ArrayList<>();

        //SI VIENE UN COMPROBANTE EN ESPECIFICO POR FECHA
        if (comprobante != null && comprobante.getTipo() != null && comprobante.getSerie() != null && comprobante.getNumero() != null) {
            comprobantes = paymentVoucherRepository.getListPaymentVoucherSpecificForSummaryDocuments(rucEmisor, fechaEmision, comprobante.getTipo(), comprobante.getSerie(), comprobante.getNumero());

        }
        //SI SOLO SE HARA EL RESUMEN POR FECHA
        else {

            List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherForSummaryDocuments(rucEmisor, fechaEmision);
            comprobantes = listPaymentVoucher.subList(0, listPaymentVoucher.size() > 400 ? 400 : listPaymentVoucher.size());
        }

        if (comprobantes != null && !comprobantes.isEmpty()) {

            correlativoSummary = summaryDocumentRepository.getCorrelativoDiaByFechaEmisionInSummaryDocuments(rucEmisor,
                    fechaEmision);

            summaryByDay = new Summary();
            int numeroLinea = 0;
            correlativoSummary++;
            List<SummaryDetail> details = new ArrayList<SummaryDetail>();

            summaryByDay.setFechaEmision(fechaEmision);
            summaryByDay.setNroResumenDelDia(correlativoSummary);
            summaryByDay.setRucEmisor(rucEmisor);
            summaryByDay.setDenominacionEmisor(denominacionEmisor);
            summaryByDay.setTipoDocumentoEmisor(tipoDocumentoEmisor);

            List<PaymentVoucherEntity> comprobantesTemp;
            comprobantesTemp = comprobantes.stream().filter(com -> com.getBoletaAnuladaSinEmitir() !=
                    null && com.getBoletaAnuladaSinEmitir()).collect(Collectors.toList());

            //AGREGANDO BOLETAS O NOTAS ASOCIADAS A BOLETAS QUE FUERON ANULADAS SIN EMITIR
            for (PaymentVoucherEntity payment : comprobantesTemp) {

                SummaryDetail detail = new SummaryDetail();
                numeroLinea++;

                detail.setNumeroItem(numeroLinea);
                detail.setSerie(payment.getSerie());
                detail.setNumero(payment.getNumero());
                detail.setTipoComprobante(payment.getTipoComprobante());
                detail.setCodigoMoneda(payment.getCodigoMoneda());
                detail.setTipoDocumentoReceptor(payment.getTipoDocIdentReceptor());
                detail.setNumeroDocumentoReceptor(payment.getNumDocIdentReceptor());
                if (payment.getCodigoTipoNotaCredito() != null || payment.getCodigoTipoNotaDebito() != null) {

                    detail.setSerieAfectado(payment.getSerieAfectado());
                    detail.setNumeroAfectado(payment.getNumeroAfectado());
                    detail.setTipoComprobanteAfectado(payment.getTipoComprobanteAfectado());
                }
                detail.setStatusItem(ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION);
                detail.setImporteTotalVenta(payment.getMontoImporteTotalVenta());
                detail.setSumatoriaOtrosCargos(payment.getMontoSumatorioOtrosCargos());
                detail.setTotalIGV(payment.getSumatoriaIGV());
                detail.setTotalISC(payment.getSumatoriaISC());
                detail.setTotalOtrosTributos(payment.getSumatoriaOtrosTributos());
                detail.setTotalValorVentaOperacionExportacion(payment.getTotalValorVentaOperacionExportada());
                detail.setTotalValorVentaOperacionGravada(payment.getTotalValorVentaOperacionGravada());
                detail.setTotalValorVentaOperacionInafecta(payment.getTotalValorVentaOperacionInafecta());
                detail.setTotalValorVentaOperacionExonerado(payment.getTotalValorVentaOperacionExonerada());
                detail.setTotalValorVentaOperacionGratuita(payment.getTotalValorVentaOperacionGratuita());

                details.add(detail);
            }

            for (PaymentVoucherEntity payment : comprobantes) {

                SummaryDetail detail = new SummaryDetail();
                numeroLinea++;

                detail.setNumeroItem(numeroLinea);
                detail.setSerie(payment.getSerie());
                detail.setNumero(payment.getNumero());
                detail.setTipoComprobante(payment.getTipoComprobante());
                detail.setCodigoMoneda(payment.getCodigoMoneda());
                detail.setTipoDocumentoReceptor(payment.getTipoDocIdentReceptor());
                detail.setNumeroDocumentoReceptor(payment.getNumDocIdentReceptor());
                if (payment.getCodigoTipoNotaCredito() != null || payment.getCodigoTipoNotaDebito() != null) {
                    detail.setSerieAfectado(payment.getSerieAfectado());
                    detail.setNumeroAfectado(payment.getNumeroAfectado());
                    detail.setTipoComprobanteAfectado(payment.getTipoComprobanteAfectado());
                }
                detail.setStatusItem(payment.getEstadoItem());
                detail.setImporteTotalVenta(payment.getMontoImporteTotalVenta());
                detail.setSumatoriaOtrosCargos(payment.getMontoSumatorioOtrosCargos());
                detail.setTotalIGV(payment.getSumatoriaIGV());
                detail.setTotalISC(payment.getSumatoriaISC());
                detail.setTotalOtrosTributos(payment.getSumatoriaOtrosTributos());
                detail.setTotalValorVentaOperacionExportacion(payment.getTotalValorVentaOperacionExportada());
                detail.setTotalValorVentaOperacionGravada(payment.getTotalValorVentaOperacionGravada());
                detail.setTotalValorVentaOperacionInafecta(payment.getTotalValorVentaOperacionInafecta());
                detail.setTotalValorVentaOperacionExonerado(payment.getTotalValorVentaOperacionExonerada());
                detail.setTotalValorVentaOperacionGratuita(payment.getTotalValorVentaOperacionGratuita());

                details.add(detail);
                ids.add(payment.getIdPaymentVoucher());
            }

            summaryByDay.setItems(details);

        } else
            throw new ServiceException("No existen comprobantes para generar este resumen [" + fechaEmision + "]");

        return summaryByDay;
    }

    //Creado Ahora
    private Summary buildSummaryDocumentsNotaCreditoByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante, List<Long> ids) throws ServiceException {

        Summary summaryByDay = null;
        Integer correlativoSummary = null;
        CompanyEntity company = companyRepository.findByRuc(rucEmisor);
        String tipoDocumentoEmisor = ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC;
        String denominacionEmisor = company.getRazonSocial();

        List<PaymentVoucherEntity> comprobantes = new ArrayList<>();

        //SI VIENE UN COMPROBANTE EN ESPECIFICO POR FECHA
        if (comprobante != null && comprobante.getTipo() != null && comprobante.getSerie() != null && comprobante.getNumero() != null) {
            comprobantes = paymentVoucherRepository.getListPaymentVoucherSpecificForSummaryDocuments(rucEmisor, fechaEmision, comprobante.getTipo(), comprobante.getSerie(), comprobante.getNumero());

        }
        //SI SOLO SE HARA EL RESUMEN POR FECHA
        else {

            List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherForSummaryDocumentsNotaCredito(rucEmisor, fechaEmision);
            comprobantes = listPaymentVoucher.subList(0, listPaymentVoucher.size() > 400 ? 400 : listPaymentVoucher.size());
        }

        if (comprobantes != null && !comprobantes.isEmpty()) {

            correlativoSummary = summaryDocumentRepository.getCorrelativoDiaByFechaEmisionInSummaryDocuments(rucEmisor,
                    fechaEmision);

            summaryByDay = new Summary();
            int numeroLinea = 0;
            correlativoSummary++;
            List<SummaryDetail> details = new ArrayList<SummaryDetail>();

            summaryByDay.setFechaEmision(comprobantes.get(0).getFechaEmision());
            summaryByDay.setNroResumenDelDia(correlativoSummary);
            summaryByDay.setRucEmisor(rucEmisor);
            summaryByDay.setDenominacionEmisor(denominacionEmisor);
            summaryByDay.setTipoDocumentoEmisor(tipoDocumentoEmisor);

            List<PaymentVoucherEntity> comprobantesTemp;
            comprobantesTemp = comprobantes.stream().filter(com -> com.getBoletaAnuladaSinEmitir() !=
                    null && com.getBoletaAnuladaSinEmitir()).collect(Collectors.toList());

            //AGREGANDO BOLETAS O NOTAS ASOCIADAS A BOLETAS QUE FUERON ANULADAS SIN EMITIR
            for (PaymentVoucherEntity payment : comprobantesTemp) {

                SummaryDetail detail = new SummaryDetail();
                numeroLinea++;

                detail.setNumeroItem(numeroLinea);
                detail.setSerie(payment.getSerie());
                detail.setNumero(payment.getNumero());
                detail.setTipoComprobante(payment.getTipoComprobante());
                detail.setCodigoMoneda(payment.getCodigoMoneda());
                detail.setTipoDocumentoReceptor(payment.getTipoDocIdentReceptor());
                detail.setNumeroDocumentoReceptor(payment.getNumDocIdentReceptor());
                if (payment.getCodigoTipoNotaCredito() != null || payment.getCodigoTipoNotaDebito() != null) {

                    detail.setSerieAfectado(payment.getSerieAfectado());
                    detail.setNumeroAfectado(payment.getNumeroAfectado());
                    detail.setTipoComprobanteAfectado(payment.getTipoComprobanteAfectado());
                }
                detail.setStatusItem(ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION);
                detail.setImporteTotalVenta(payment.getMontoImporteTotalVenta());
                detail.setSumatoriaOtrosCargos(payment.getMontoSumatorioOtrosCargos());
                detail.setTotalIGV(payment.getSumatoriaIGV());
                detail.setTotalISC(payment.getSumatoriaISC());
                detail.setTotalOtrosTributos(payment.getSumatoriaOtrosTributos());
                detail.setTotalValorVentaOperacionExportacion(payment.getTotalValorVentaOperacionExportada());
                detail.setTotalValorVentaOperacionGravada(payment.getTotalValorVentaOperacionGravada());
                detail.setTotalValorVentaOperacionInafecta(payment.getTotalValorVentaOperacionInafecta());
                detail.setTotalValorVentaOperacionExonerado(payment.getTotalValorVentaOperacionExonerada());
                detail.setTotalValorVentaOperacionGratuita(payment.getTotalValorVentaOperacionGratuita());

                details.add(detail);
            }

            for (PaymentVoucherEntity payment : comprobantes) {

                SummaryDetail detail = new SummaryDetail();
                numeroLinea++;

                detail.setNumeroItem(numeroLinea);
                detail.setSerie(payment.getSerie());
                detail.setNumero(payment.getNumero());
                detail.setTipoComprobante(payment.getTipoComprobante());
                detail.setCodigoMoneda(payment.getCodigoMoneda());
                detail.setTipoDocumentoReceptor(payment.getTipoDocIdentReceptor());
                detail.setNumeroDocumentoReceptor(payment.getNumDocIdentReceptor());
                if (payment.getCodigoTipoNotaCredito() != null || payment.getCodigoTipoNotaDebito() != null) {
                    detail.setSerieAfectado(payment.getSerieAfectado());
                    detail.setNumeroAfectado(payment.getNumeroAfectado());
                    detail.setTipoComprobanteAfectado(payment.getTipoComprobanteAfectado());
                }
                detail.setStatusItem(payment.getEstadoItem());
                detail.setImporteTotalVenta(payment.getMontoImporteTotalVenta());
                detail.setSumatoriaOtrosCargos(payment.getMontoSumatorioOtrosCargos());
                detail.setTotalIGV(payment.getSumatoriaIGV());
                detail.setTotalISC(payment.getSumatoriaISC());
                detail.setTotalOtrosTributos(payment.getSumatoriaOtrosTributos());
                detail.setTotalValorVentaOperacionExportacion(payment.getTotalValorVentaOperacionExportada());
                detail.setTotalValorVentaOperacionGravada(payment.getTotalValorVentaOperacionGravada());
                detail.setTotalValorVentaOperacionInafecta(payment.getTotalValorVentaOperacionInafecta());
                detail.setTotalValorVentaOperacionExonerado(payment.getTotalValorVentaOperacionExonerada());
                detail.setTotalValorVentaOperacionGratuita(payment.getTotalValorVentaOperacionGratuita());

                details.add(detail);
                ids.add(payment.getIdPaymentVoucher());
            }

            summaryByDay.setItems(details);

        } else
            throw new ServiceException("No existen comprobantes para generar este resumen [" + fechaEmision + "]");

        return summaryByDay;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> generationPaymentVoucher(PaymentVoucher voucher, Boolean isEdit, UserPrincipal userName) throws ServiceException {
        if(voucher.getCodigoTipoOperacion()!=null){
            if(voucher.getCodigoTipoOperacion().equals("1001")){
                Leyenda leyenda =
                        new Leyenda("Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central","2006");
                voucher.setLeyendas(new ArrayList<>());
                voucher.getLeyendas().add(leyenda);
            }
        }

        return generationDocument(voucher, isEdit, userName.getUsername());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Map<String, Object>> resendPaymentVoucherBySummary(Long idSummary, UserPrincipal user) throws ServiceException{
        List<Map<String, Object>> result = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        SummaryDocumentEntity documentEntity = summaryDocumentRepository.getSummaryByIddocumentsummary(idSummary);
        List<PaymentVoucherEntity> paymentVouchers = paymentVoucherRepository
                .getListPaymentVoucherForSummaryDocumentsProceso(documentEntity.getRucEmisor(),documentEntity.getFechaEmision());

        for(PaymentVoucherEntity voucherEntity : paymentVouchers){

            result.add(regenerarPayment(voucherEntity));
        }

        return result;
        //PaymentVoucherEntity entity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);

    }

    @Override
    public Map<String,List<EmailSexDaysDetails>> getLisVoucherSeven() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -5);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, List<EmailSexDaysDetails>> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSex(hace6DiasString);
        List<Object[]> objectsItem = null;
        List<EmailSexDays> emailSexDays = convertObjectToPojo(objects);

        String rucEmisor = "";
        for(EmailSexDays days : emailSexDays){
            boolean respEmail = false;
            if(days.getEmail()!=null)
                respEmail = sendEmailSevenDay(days);

            listMap.put(days.getRuc(),days.getDetails());
        }

        return listMap;
    }
    @Override
    public Map<String,List<EmailSexDaysDetails>> getLisVoucherSix() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -4);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, List<EmailSexDaysDetails>> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSex(hace6DiasString);
        List<Object[]> objectsItem = null;
        List<EmailSexDays> emailSexDays = convertObjectToPojo(objects);

        String rucEmisor = "";
        for(EmailSexDays days : emailSexDays){
            boolean respEmail = false;
            if(days.getEmail()!=null)
                respEmail = sendEmailSevenDay(days);

            listMap.put(days.getRuc(),days.getDetails());
        }
        return listMap;
    }
    @Override
    public Map<String,List<EmailSexDaysDetails>> getLisVoucherFive() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -3);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, List<EmailSexDaysDetails>> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSex(hace6DiasString);
        List<Object[]> objectsItem = null;
        List<EmailSexDays> emailSexDays = convertObjectToPojo(objects);

        String rucEmisor = "";
        for(EmailSexDays days : emailSexDays){
            boolean respEmail = false;
            if(days.getEmail()!=null)
                respEmail = sendEmailSevenDay(days);
            listMap.put(days.getRuc(),days.getDetails());
        }

        return listMap;
    }
    @Override
    public Map<String,List<EmailSexDaysDetails>> getLisVoucherFour() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -2);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, List<EmailSexDaysDetails>> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSex(hace6DiasString);
        List<Object[]> objectsItem = null;
        List<EmailSexDays> emailSexDays = convertObjectToPojo(objects);

        String rucEmisor = "";
        for(EmailSexDays days : emailSexDays){
            boolean respEmail = false;
            if(days.getEmail()!=null)
                respEmail = sendEmailSevenDay(days);
            listMap.put(days.getRuc(),days.getDetails());
        }

        return listMap;
    }
    @Override
    public Map<String,List<EmailSexDaysDetails>> getLisVoucherThree() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -1);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, List<EmailSexDaysDetails>> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSex(hace6DiasString);
        List<Object[]> objectsItem = null;
        List<EmailSexDays> emailSexDays = convertObjectToPojo(objects);

        String rucEmisor = "";
        for(EmailSexDays days : emailSexDays){
            boolean respEmail = false;
            if(days.getEmail()!=null)
                respEmail = sendEmailSevenDay(days);
            listMap.put(days.getRuc(),days.getDetails());
        }

        return listMap;
    }

    @Override
    public Map<String,String> postVoucherSeven() {

        Date now = new Date();
        Date hace7Dias = UtilDate.sumarDiasAFecha(now, -6);
        String hace7DiasString = UtilDate.dateToString(hace7Dias, "yyyy-MM-dd");
        Map<String, String> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSeven(hace7DiasString);

        System.out.println(hace7DiasString);

        for(PaymentVoucherInterDto obj : objects){
            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        obj.getEmisor(),
                        obj.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                System.out.println("ENVIO AUTO");
                System.out.println(resp);
                System.out.println("----------------");
                if (resp.getEstado()) {
                    listMap.put(resp.getNombre(),resp.getMensaje());
                }

            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }

        }
        return listMap;
    }
    @Override
    public Map<String,String> postVoucherSix() {

        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -5);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");
        Map<String, String> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSeven(hace6DiasString);

        System.out.println(hace6DiasString);

        for(PaymentVoucherInterDto obj : objects){
            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        obj.getEmisor(),
                        obj.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                System.out.println("ENVIO AUTO");
                System.out.println(resp);
                System.out.println("----------------");
                if (resp.getEstado()) {
                    listMap.put(resp.getNombre(),resp.getMensaje());
                }

            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }

        }
        return listMap;
    }
    @Override
    public Map<String,String> postVoucherFive() {

        Date now = new Date();
        Date hace5Dias = UtilDate.sumarDiasAFecha(now, -4);
        String hace5DiasString = UtilDate.dateToString(hace5Dias, "yyyy-MM-dd");
        Map<String, String> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSeven(hace5DiasString);

        System.out.println(hace5DiasString);

        for(PaymentVoucherInterDto obj : objects){
            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        obj.getEmisor(),
                        obj.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                System.out.println("ENVIO AUTO");
                System.out.println(resp);
                System.out.println("----------------");
                if (resp.getEstado()) {
                    listMap.put(resp.getNombre(),resp.getMensaje());
                }

            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }

        }
        return listMap;
    }
    @Override
    public Map<String,String> postVoucherFour() {

        Date now = new Date();
        Date hace5Dias = UtilDate.sumarDiasAFecha(now, -3);
        String hace5DiasString = UtilDate.dateToString(hace5Dias, "yyyy-MM-dd");
        Map<String, String> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSeven(hace5DiasString);

        System.out.println(hace5DiasString);

        for(PaymentVoucherInterDto obj : objects){
            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        obj.getEmisor(),
                        obj.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                System.out.println("ENVIO AUTO");
                System.out.println(resp);
                System.out.println("----------------");
                if (resp.getEstado()) {
                    listMap.put(resp.getNombre(),resp.getMensaje());
                }

            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }

        }
        return listMap;
    }
    @Override
    public Map<String,String> postVoucherThree() {

        Date now = new Date();
        Date hace5Dias = UtilDate.sumarDiasAFecha(now, -2);
        String hace5DiasString = UtilDate.dateToString(hace5Dias, "yyyy-MM-dd");
        Map<String, String> listMap = new HashMap<>();
        List<PaymentVoucherInterDto> objects = paymentVoucherRepository.getRucVoucherSeven(hace5DiasString);

        System.out.println(hace5DiasString);

        for(PaymentVoucherInterDto obj : objects){
            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        obj.getEmisor(),
                        obj.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                System.out.println("ENVIO AUTO");
                System.out.println(resp);
                System.out.println("----------------");
                if (resp.getEstado()) {
                    listMap.put(resp.getNombre(),resp.getMensaje());
                }

            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }

        }
        return listMap;
    }
    private boolean sendEmailSevenDay(EmailSexDays days) {

        try {

            String emailToSend = "";
            emailToSend = days.getEmail();

            //EMAIL ADICIONALES
            List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = new ArrayList<>();
            emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(days.getRuc());
            List<String> emailsList = new ArrayList<>();
            if (!emailsAdicionalesNotificar.isEmpty())
                emailsList = emailsAdicionalesNotificar.stream().filter(e -> (e.getEmail().trim()).length()>0)
                        .map(e -> e.getEmail().trim()).collect(Collectors.toList());
            if (emailToSend != null && !emailToSend.isEmpty() && ((emailToSend.trim()).length()>0))
                emailsList.add(emailToSend);


            if (emailsList.size()>0){
                emailsList.add("jruizp@certicom.com.pe");
                emailsList.add("lpalacios@tns.com.pe");
                final List<String> emailListFinal = emailsList;
                if (StringsUtils.validateEmail(emailToSend)) {

                    MimeMessagePreparator preparator = new MimeMessagePreparator() {
                        public void prepare(MimeMessage mimeMessage) throws Exception {
                            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                            String textMensajeHtml = "<h2>Estimado Cliente, evite inconvenientes con la Sunat, tiene los siguientes comprobantes " +
                                    "pendientes por enviar a Sunat</h2><br>" +
                                    "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                    "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                    "<td style=\"border: solid 1px #000;\">EMITIDO</td>" +
                                    "<td style=\"border: solid 1px #000;\">DOCUMENTO</td>" +
                                    "<td style=\"border: solid 1px #000;\">RECEPTOR</td>" +
                                    "<td style=\"border: solid 1px #000;\">MONTO</td>" +
                                    "<td style=\"border: solid 1px #000;\">REGISTRADO</td></tr>";
                            for (EmailSexDaysDetails details : days.getDetails()){
                                textMensajeHtml += "<tr><td style=\"border: solid 1px #000;\">" +details.getEmision()+"</td>" +
                                        "<td style=\"border: solid 1px #000;\">"+details.getDocumento()+"</td>" +
                                        "<td style=\"border: solid 1px #000;\">" +details.getReceptor()+"</td>" +
                                        "<td style=\"border: solid 1px #000;\">"+details.getMonto().setScale(2,RoundingMode.HALF_UP)+"</td>" +
                                        "<td style=\"border: solid 1px #000;\">"+details.getRegistro()+"</td>" +
                                        "</tr>";
                            }
                            textMensajeHtml +="</table>";

                            helper.setSubject("Vence el plazo para enviar tus facturas a Sunat "+days.getRuc());

                            helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                            helper.setTo(days.getEmail());
                            helper.setText(textMensajeHtml, true);

                            String[] stringArray = new String[emailListFinal.size()];
                            for(int j =0;j<emailListFinal.size();j++){
                                stringArray[j] = emailListFinal.get(j);
                            }
                            helper.setCc(stringArray);
                        }
                    };

                    emailSender.send(preparator);
                }
            }


            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendEmailReportSevenDay(Map<String, String> stringMap) {
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -6);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");

        try {

            String emailToSend = "lpalacios@tns.com.pe";

            List<String> emailsList = new ArrayList<>();

            emailsList.add("jruizp@certicom.com.pe");
            emailsList.add("lpalacios@tns.com.pe");

            final List<String> emailListFinal = emailsList;

            if (StringsUtils.validateEmail(emailToSend)) {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        String textMensajeHtml = null;
                        final String[] textEmail = {""};
                        textMensajeHtml = "<h2>Se procedió a enviar automaticamente "+ stringMap.size() +" comprobantes</h2><br>" +
                                "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                "<td style=\"border: solid 1px #000;\">IDENTIFICADOR</td>" +
                                "<td style=\"border: solid 1px #000;\">MENSAJE</td>" +
                                "</tr>";
                        stringMap.forEach((k,v) -> {
                            textEmail[0] += "<tr><td>"+k+"</td><td>"+v+"</td></tr>";
                        });
                        textMensajeHtml += textEmail[0];
                        textMensajeHtml +="</table>";

                        helper.setSubject("Envio automatico a Sunat "+hace6DiasString);

                        helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                        String[] stringArray = new String[emailListFinal.size()];
                        for(int j =0;j<emailListFinal.size();j++){
                            stringArray[j] = emailListFinal.get(j);
                        }
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                    }
                };
                if(preparator != null){
                    emailSender.send(preparator);
                }else {
                    System.out.println("No existe preparator");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendEmailReportSixDay(Map<String, String> stringMap) {
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -5);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");

        try {

            String emailToSend = "lpalacios@tns.com.pe";

            List<String> emailsList = new ArrayList<>();

            emailsList.add("jruizp@certicom.com.pe");
            emailsList.add("lpalacios@tns.com.pe");

            final List<String> emailListFinal = emailsList;

            if (StringsUtils.validateEmail(emailToSend)) {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        String textMensajeHtml = null;
                        final String[] textEmail = {""};
                        textMensajeHtml = "<h2>Se procedió a enviar automaticamente "+ stringMap.size() +" comprobantes</h2><br>" +
                                "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                "<td style=\"border: solid 1px #000;\">IDENTIFICADOR</td>" +
                                "<td style=\"border: solid 1px #000;\">MENSAJE</td>" +
                                "</tr>";
                        stringMap.forEach((k,v) -> {
                            textEmail[0] += "<tr><td>"+k+"</td><td>"+v+"</td></tr>";
                        });
                        textMensajeHtml += textEmail[0];
                        textMensajeHtml +="</table>";

                        helper.setSubject("Envio automatico a Sunat "+hace6DiasString);

                        helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                        String[] stringArray = new String[emailListFinal.size()];
                        for(int j =0;j<emailListFinal.size();j++){
                            stringArray[j] = emailListFinal.get(j);
                        }
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                    }
                };
                if(preparator != null){
                    emailSender.send(preparator);
                }else {
                    System.out.println("No existe preparator");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendEmailReportFiveDay(Map<String, String> stringMap) {
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -4);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");

        try {

            String emailToSend = "lpalacios@tns.com.pe";

            List<String> emailsList = new ArrayList<>();

            emailsList.add("jruizp@certicom.com.pe");
            emailsList.add("lpalacios@tns.com.pe");

            final List<String> emailListFinal = emailsList;

            if (StringsUtils.validateEmail(emailToSend)) {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        String textMensajeHtml = null;
                        final String[] textEmail = {""};
                        textMensajeHtml = "<h2>Se procedió a enviar automaticamente "+ stringMap.size() +" comprobantes</h2><br>" +
                                "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                "<td style=\"border: solid 1px #000;\">IDENTIFICADOR</td>" +
                                "<td style=\"border: solid 1px #000;\">MENSAJE</td>" +
                                "</tr>";
                        stringMap.forEach((k,v) -> {
                            textEmail[0] += "<tr><td>"+k+"</td><td>"+v+"</td></tr>";
                        });
                        textMensajeHtml += textEmail[0];
                        textMensajeHtml +="</table>";

                        helper.setSubject("Envio automatico a Sunat "+hace6DiasString);

                        helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                        String[] stringArray = new String[emailListFinal.size()];
                        for(int j =0;j<emailListFinal.size();j++){
                            stringArray[j] = emailListFinal.get(j);
                        }
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                    }
                };
                if(preparator != null){
                    emailSender.send(preparator);
                }else {
                    System.out.println("No existe preparator");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendEmailReportFourDay(Map<String, String> stringMap) {
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -3);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");

        try {

            String emailToSend = "lpalacios@tns.com.pe";

            List<String> emailsList = new ArrayList<>();

            emailsList.add("jruizp@certicom.com.pe");
            emailsList.add("lpalacios@tns.com.pe");

            final List<String> emailListFinal = emailsList;

            if (StringsUtils.validateEmail(emailToSend)) {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        String textMensajeHtml = null;
                        final String[] textEmail = {""};
                        textMensajeHtml = "<h2>Se procedió a enviar automaticamente "+ stringMap.size() +" comprobantes</h2><br>" +
                                "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                "<td style=\"border: solid 1px #000;\">IDENTIFICADOR</td>" +
                                "<td style=\"border: solid 1px #000;\">MENSAJE</td>" +
                                "</tr>";
                        stringMap.forEach((k,v) -> {
                            textEmail[0] += "<tr><td>"+k+"</td><td>"+v+"</td></tr>";
                        });
                        textMensajeHtml += textEmail[0];
                        textMensajeHtml +="</table>";

                        helper.setSubject("Envio automatico a Sunat "+hace6DiasString);

                        helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                        String[] stringArray = new String[emailListFinal.size()];
                        for(int j =0;j<emailListFinal.size();j++){
                            stringArray[j] = emailListFinal.get(j);
                        }
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                    }
                };
                if(preparator != null){
                    emailSender.send(preparator);
                }else {
                    System.out.println("No existe preparator");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendEmailReportThreeDay(Map<String, String> stringMap) {
        Date now = new Date();
        Date hace6Dias = UtilDate.sumarDiasAFecha(now, -2);
        String hace6DiasString = UtilDate.dateToString(hace6Dias, "yyyy-MM-dd");

        try {

            String emailToSend = "lpalacios@tns.com.pe";

            List<String> emailsList = new ArrayList<>();

            emailsList.add("jruizp@certicom.com.pe");
            emailsList.add("lpalacios@tns.com.pe");

            final List<String> emailListFinal = emailsList;

            if (StringsUtils.validateEmail(emailToSend)) {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        String textMensajeHtml = null;
                        final String[] textEmail = {""};
                        textMensajeHtml = "<h2>Se procedió a enviar automaticamente "+ stringMap.size() +" comprobantes</h2><br>" +
                                "<table style=\"border-collapse: collapse;border: solid 1px #000; \">" +
                                "<tr style=\"border: solid 1px #000;color: #fff;background-color: #1F618D;\">" +
                                "<td style=\"border: solid 1px #000;\">IDENTIFICADOR</td>" +
                                "<td style=\"border: solid 1px #000;\">MENSAJE</td>" +
                                "</tr>";
                        stringMap.forEach((k,v) -> {
                            textEmail[0] += "<tr><td>"+k+"</td><td>"+v+"</td></tr>";
                        });
                        textMensajeHtml += textEmail[0];
                        textMensajeHtml +="</table>";

                        helper.setSubject("Envio automatico a Sunat "+hace6DiasString);

                        helper.setFrom(new InternetAddress(emailFrom, "CERTIFAKT"));
                        String[] stringArray = new String[emailListFinal.size()];
                        for(int j =0;j<emailListFinal.size();j++){
                            stringArray[j] = emailListFinal.get(j);
                        }
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                    }
                };
                if(preparator != null){
                    emailSender.send(preparator);
                }else {
                    System.out.println("No existe preparator");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void getPaymentMercadoPago() throws URISyntaxException {
        System.out.println("TRAER PAGOS mercado pago");
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        System.out.println(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        System.out.println(calendar.getTime());
        try {
            final String baseUrl = "https://api.mercadopago.com/v1/payments/search?access_token="+mercadopagoToken+
                    "&begin_date=NOW-180MINUTES&end_date=NOW&range=date_approved&sort=date_approved&criteria=asc&limit=350";
            URI uri = new URI(baseUrl);
            RestTemplate template = new RestTemplate();
            template.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            ResponseEntity<PaymentMercadoPagoListDto> pagoDtos = template.getForEntity(uri, PaymentMercadoPagoListDto.class);
            System.out.println(pagoDtos.getBody());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (PaymentMercadoPagoDto dto: pagoDtos.getBody().getResults()) {
                System.out.println("CAMBIAR HORAS "+dto.getDate_created());
                LocalDateTime dateTimeCreated = LocalDateTime.parse((dto.getDate_created()).substring(0,22));
                System.out.println(dateTimeCreated);
                LocalDateTime date2 = dateTimeCreated.minusHours(1);
                LocalDateTime dateTimeApproved = LocalDateTime.parse((dto.getDate_approved()).substring(0,22));
                LocalDateTime date1 = dateTimeApproved.minusHours(1);
                try {
                    Optional<MerkdopagoNotifyEntity> byId = merkdoPagoRepository.findByIdmerkdopago(dto.getId());
                    if(!byId.isPresent()){
                        MerkdopagoNotifyEntity notifyEntity = new MerkdopagoNotifyEntity();
                        notifyEntity.setRegistro(new Date());
                        notifyEntity.setIdmerkdopago(dto.getId());
                        notifyEntity.setTypeope(dto.getOperation_type());
                        notifyEntity.setAprobado((date1).toString());
                        notifyEntity.setPname(dto.getCard().getCardholder()==null?dto.getPayer().getFirst_name()+" "+
                                dto.getPayer().getLast_name():dto.getCard().getCardholder().getName());
                        notifyEntity.setPemail(dto.getPayer().getEmail());
                        notifyEntity.setPtypedoc(dto.getCard().getCardholder()==null?
                                dto.getPayer().getIdentification().getType()==null?"":
                                        dto.getPayer().getIdentification().getType():
                                dto.getCard().getCardholder().getIdentification().getType());
                        notifyEntity.setPnumberdoc(dto.getCard().getCardholder()==null?
                                dto.getPayer().getIdentification().getNumber()==null?"":
                                        dto.getPayer().getIdentification().getNumber():
                                dto.getCard().getCardholder().getIdentification().getNumber());
                        notifyEntity.setOrderid(dto.getOrder().getId());
                        notifyEntity.setOrdertype(dto.getOrder().getType());
                        notifyEntity.setLive(dto.getLive_mode());
                        notifyEntity.setStatus(dto.getStatus());
                        notifyEntity.setTamount(dto.getTransaction_amount());
                        notifyEntity.setAuthorization(dto.getAuthorization_code()==null?"":dto.getAuthorization_code());
                        notifyEntity.setCollector(dto.getCollector_id());
                        notifyEntity.setLastupdate(dto.getDate_last_updated());
                        notifyEntity.setCreado((date2).toString());
                        notifyEntity.setCurrency(dto.getCurrency_id());
                        notifyEntity.setDescription(dto.getDescription());
                        notifyEntity.setUsuario(1L);
                        System.out.println("GUARDAR UNA VEZ MERCADO PAGO");
                        merkdoPagoRepository.save(notifyEntity);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            merkdoPagoRepository.updateDuplicate();
        }
    }

    private Long id;
    private MercadoPagoOrderDTO order;
    private Boolean live_mode; // true
    private MercadoPagoCardDTO card;  // CLIENTE RECEPTOR DATOS
    private String status;  //
    private Float transaction_amount; // Costo del producto. (Obligatorio)
    private String description;


    @Override
    public void getPaymentsPaypal() throws URISyntaxException {

        Instant instantInicio = Instant.ofEpochSecond(new Date().toInstant().getEpochSecond());

        final String baseurl = "https://api.paypal.com/v1/oauth2/token";
        final String baseurlget = "https://api.paypal.com/v1/reporting/transactions?start_date="+
                instantInicio.minus(Duration.ofHours(13))+"&" +
                "end_date="+instantInicio.minus(Duration.ofHours(2))+"&fields=all&page_size=350";
        System.out.println(baseurlget);
        RestTemplate template = new RestTemplate();
        URI uri = new URI(baseurl);
        URI uriget = new URI(baseurlget);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");
        HttpEntity<MultiValueMap<String, String>> mapHttpEntity = new HttpEntity<>(map,headers);
        template.getInterceptors().add(new BasicAuthenticationInterceptor(clienteid,secretid));
        ResponseEntity<TokenPaypal> result = template.postForEntity(uri,mapHttpEntity,TokenPaypal.class);
        System.out.println("TOKEN PAYPAL");
        System.out.println(result.getBody().getAccess_token());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headerspaypal = new HttpHeaders();
        headerspaypal.setContentType(MediaType.APPLICATION_JSON);
        headerspaypal.set("Authorization","Bearer "+result.getBody().getAccess_token());
        restTemplate.getMessageConverters()
                .add(0,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpEntity <String> entityhttp = new HttpEntity<String>(headerspaypal);
        ResponseEntity<PaypalTransaction> response = null;
        try {
            response = restTemplate.exchange(uriget, HttpMethod.GET, entityhttp,PaypalTransaction.class);
            System.out.println("TAMANO DE LISTA PAYPAL "+response.getBody().getTransaction_details().size());
            System.out.println(response.getBody());
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat sdffecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfhora = new SimpleDateFormat("HH:mm:ss");


            for (TransactionDetails transaction:response.getBody().getTransaction_details()) {
                calendar.setTime(sdf.parse(transaction.getTransaction_info().getTransaction_initiation_date()));
                Optional<PaymentPaypalEntity> paypalEntity = paymentPaypalRepository.findByTransaccion(transaction.getTransaction_info().getTransaction_id());
                if(!paypalEntity.isPresent()){
                    System.out.println("GUARDAR UNA VEZ PAYPAL "+transaction.getTransaction_info().getTransaction_id());
                    paymentPaypalRepository.save(PaymentPaypalEntity.builder()
                            .transaccion(transaction.getTransaction_info().getTransaction_id())
                            .fecha(sdffecha.format(calendar.getTime()))
                            .hora(sdfhora.format(calendar.getTime()))
                            .transactionStatus(transaction.getTransaction_info().getTransaction_status())
                            .invoice(transaction.getTransaction_info().getInvoice_id())
                            .moneda(transaction.getTransaction_info().getTransaction_amount().getCurrency_code())
                            .valueAmount(transaction.getTransaction_info().getTransaction_amount().getValue())
                            .ciudad(transaction.getPayer_info().getCountry_code())
                            .clienteemail(transaction.getPayer_info().getEmail_address())
                            .cliente(transaction.getPayer_info().getPayer_name().getAlternate_full_name())
                            .productname(transaction.getCart_info().getItem_details().get(0).getItem_name()==null?
                                    "ENTRENAMIENTO MENSUAL":transaction.getCart_info().getItem_details().get(0).getItem_name())
                            .cantidad(1)
                            .created(new Timestamp(new Date().getTime()))
                            .build());
                }

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void generatePaymentMercadoPago() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        List<MerkdopagoNotifyEntity> merkdopagoNotifyEntities = merkdoPagoRepository.findByReporte(calendar.getTime());
        for (MerkdopagoNotifyEntity entity2 : merkdopagoNotifyEntities) {
            entity2.setUsuario(2l);
            merkdoPagoRepository.save(entity2);
        }
        for (MerkdopagoNotifyEntity entity : merkdopagoNotifyEntities) {

            PaymentVoucher payment = convertMercadoPagoToPaymentVoucher(entity);
            Map<String, Object> result;
            try {
                result = generationPaymentVoucher(
                        payment,
                        false,
                        UserPrincipal.builder().username("gts").build()
                );
                ResponsePSE pse = (ResponsePSE) result.get("responsePse");
                if (pse.getEstado()){
                    entity.setReporte(new Timestamp(new Date().getTime()));
                    merkdoPagoRepository.save(entity);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> getFechasPendientesNotas() {
        return paymentVoucherRepository.getListFechasPendientesNotas();

    }

    @Override
    public List<String> getRucsPendientesNotas() {
        return paymentVoucherRepository.getListRucsPendientesNotas();
    }

    @Override
    public void generatePaymentsPaypal() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        List<PaymentPaypalEntity> paypalEntities = paymentPaypalRepository.findAllByCreated(calendar.getTime());

        for (PaymentPaypalEntity entity : paypalEntities) {
            PaymentVoucher payment = convertPaypalToPaymenntVoucher(entity);
            Map<String, Object> result;
            try {
                result = generationPaymentVoucher(
                        payment,
                        false,
                        UserPrincipal.builder().username("gts").build()
                );
                ResponsePSE pse = (ResponsePSE) result.get("responsePse");
                if (pse.getEstado()){
                    entity.setGenerated(new Timestamp(new Date().getTime()));
                    paymentPaypalRepository.save(entity);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private PaymentVoucher convertMercadoPagoToPaymentVoucher(MerkdopagoNotifyEntity entity) throws ParseException {
        CompanyEntity company = companyService.getCompanyByRuc("20600408454");
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setRuc(company.getRuc());
        userPrincipal.setUsername("gts");
        PaymentVoucher paymentVoucher = new PaymentVoucher();
        paymentVoucher.setTipoComprobante("03");
        paymentVoucher.setSerie("B001");
        paymentVoucher.setNumero(getProximoNumero("03","B001",company.getRuc()));
        paymentVoucher.setFechaEmision((DateTimeFormatter.ISO_DATE).format(LocalDateTime.parse(entity.getCreado())));
        paymentVoucher.setHoraEmision((DateTimeFormatter.ISO_LOCAL_TIME).format(LocalDateTime.parse(entity.getCreado())));
        paymentVoucher.setCodigoMoneda(entity.getCurrency());
        if(entity.getPtypedoc()!=null){
            if(entity.getPtypedoc().equals("DNI")){
                paymentVoucher.setTipoDocumentoReceptor("1");
            }else{
                paymentVoucher.setTipoDocumentoReceptor("0");
            }
        }else{
            paymentVoucher.setTipoDocumentoReceptor("0");
        }

        paymentVoucher.setNumeroDocumentoReceptor(entity.getPnumberdoc()==null?"-":(entity.getPnumberdoc().trim()).length()==0?"-":entity.getPnumberdoc());
        paymentVoucher.setDenominacionReceptor(entity.getPname()==null?"-":(entity.getPname().trim()).length()==0?"-":(entity.getPname()).toUpperCase());
        paymentVoucher.setDireccionReceptor("");
        paymentVoucher.setEmailReceptor(entity.getPemail()==null?"":entity.getPemail());
        paymentVoucher.setTotalValorVentaGravada(convertDecimal(entity.getTamount() / 1.18));
        paymentVoucher.setTotalIgv(convertDecimal((entity.getTamount() / 1.18) * 0.18));
        paymentVoucher.setTotalDescuento(BigDecimal.valueOf(0));
        paymentVoucher.setImporteTotalVenta(convertDecimal(entity.getTamount()));
        paymentVoucher.setCodigoTipoOperacion("01");
        paymentVoucher.setRucEmisor("20600408454");
        paymentVoucher.setOrdenCompra(entity.getOrderid());

        PaymentVoucherLine detail = new PaymentVoucherLine();
        List<PaymentVoucherLine> list = new ArrayList<>();

        detail.setCodigoUnidadMedida("NIU");
        detail.setCantidad(convertDecimal(1));
        detail.setDescripcion(entity.getDescription());
        detail.setCodigoProducto(null);
        detail.setCodigoTipoAfectacionIGV("10");
        detail.setValorVenta(convertDecimal(entity.getTamount()  / 1.18));
        detail.setValorUnitario(convertDecimal(entity.getTamount()  / 1.18));
        detail.setPrecioVentaUnitario(convertDecimal(entity.getTamount()));
        detail.setPorcentajeIgv(convertDecimal(18));
        detail.setMontoBaseIgv(convertDecimal(entity.getTamount()  / 1.18));
        detail.setIgv(convertDecimal((entity.getTamount() / 1.18) * 0.18));

        list.add(detail);

        paymentVoucher.setItems(list);

        return paymentVoucher;
    }

    private PaymentVoucher convertPaypalToPaymenntVoucher(PaymentPaypalEntity entity) {
        PaymentVoucherLine line = new PaymentVoucherLine();
        List<PaymentVoucherLine> lines = new ArrayList<>();
        line.setCodigoUnidadMedida("NIU");
        line.setCantidad(convertDecimal(entity.getCantidad()));
        line.setCodigoProducto("");
        line.setDescripcion(entity.getProductname()==null?"ENTRENAMIENTO MENSUAL":entity.getProductname());

        line.setPorcentajeIgv(convertDecimal(18));
        line.setMontoBaseIgv(convertDecimal( Double.parseDouble(entity.getValueAmount()) / 1.18));
        line.setIgv(convertDecimal( (Double.parseDouble(entity.getValueAmount()) / 1.18) * 0.18));
        line.setCodigoTipoAfectacionIGV("10");
        line.setValorVenta(convertDecimal( Double.parseDouble(entity.getValueAmount()) / 1.18));
        line.setValorUnitario(convertDecimal(Double.parseDouble(entity.getValueAmount()) / 1.18));
        line.setPrecioVentaUnitario(convertDecimal(Double.parseDouble(entity.getValueAmount())));
        lines.add(line);

        return PaymentVoucher.builder()
                .tipoComprobante("03")
                .serie("B001")
                .numero(getProximoNumero("03","B001","20600408454"))
                .fechaEmision(entity.getFecha())
                .horaEmision(entity.getHora())
                .codigoMoneda(entity.getMoneda())
                .tipoDocumentoReceptor("0")
                .numeroDocumentoReceptor("-")
                .denominacionReceptor((entity.getCliente()).toUpperCase())
                .direccionReceptor(entity.getClientedireccion())
                .emailReceptor(entity.getClienteemail())
                .totalValorVentaGravada(convertDecimal(Double.parseDouble(entity.getValueAmount()) / 1.18))
                .totalIgv(convertDecimal((Double.parseDouble(entity.getValueAmount()) / 1.18) * 0.18 ))
                .totalDescuento(BigDecimal.valueOf(0))
                .importeTotalVenta(convertDecimal((Double.parseDouble(entity.getValueAmount()))))
                .codigoTipoOperacion("01")
                .items(lines)
                .rucEmisor("20600408454")
                .build();

    }

    private Integer getProximoNumero(String tipoDocumento, String serie, String ruc) {
        PaymentVoucherEntity ultimoComprobante = paymentVoucherRepository
                .findFirst1ByTipoComprobanteAndSerieAndRucEmisorOrderByNumeroDesc(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante.getNumero() + 1;
        }else {
            return 1;
        }
    }

    private void generarComprobantes(PaymentPaypalEntity entity) {
        PaymentVoucher voucher = new PaymentVoucher();
        voucher.setTipoComprobante("03");
        voucher.setSerie("B001");
        voucher.setNumero(1);
        voucher.setFechaEmision(entity.getFecha());
        voucher.setHoraEmision(entity.getHora());
        voucher.setCodigoMoneda(entity.getMoneda());

        voucher.setTipoDocumentoReceptor("01");
        voucher.setNumeroDocumentoReceptor("123123");
        voucher.setDenominacionReceptor(entity.getCliente());
        voucher.setDireccionReceptor(entity.getClientedireccion());
        voucher.setEmailReceptor(entity.getClienteemail());

        voucher.setTotalValorVentaGravada(convertDecimal(entity.getBruto1()));
        voucher.setTotalIgv(convertDecimal(entity.getIgv()));
        voucher.setTotalDescuento(convertDecimal(0));
        voucher.setImporteTotalVenta(convertDecimal(entity.getBruto1()+entity.getIgv()));
        voucher.setCodigoTipoOperacion("01");

        PaymentVoucherLine line = new PaymentVoucherLine();
        List<PaymentVoucherLine> lines = new ArrayList<>();
        line.setCantidad(convertDecimal(entity.getCantidad()));
        line.setCodigoProducto(entity.getProductid());
        line.setDescripcion(entity.getProductid());
        //line.setIgv(convertDecimal(entity.getIgv()));
        //line.setMontoBaseIgv(convertDecimal(entity.getBruto1()));
        //line.setPorcentajeIgv(null);
        line.setCodigoTipoAfectacionIGV("10");
        line.setCodigoUnidadMedida("NIU");
        line.setValorVenta(convertDecimal(entity.getCantidad() * entity.getBruto()*1.18));
        line.setValorUnitario(convertDecimal(entity.getBruto()));
        line.setPrecioVentaUnitario(convertDecimal(entity.getBruto()*1.18));
        lines.add(line);
        voucher.setItems(lines);

        voucher.setRucEmisor("20204040303");
        Map<String, Object> result;
        try {
            result = generationPaymentVoucher(
                    voucher,
                    false,
                    UserPrincipal.builder().username("20204040303").build()
            );
        }catch (Exception e){

        }


    }
    private BigDecimal convertDecimal(double entero){
        return new BigDecimal(entero, MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP);
    }
    private List<EmailSexDays> convertObjectToPojo(List<PaymentVoucherInterDto> objects) {
        List<EmailSexDays> emailSexDays=new ArrayList<>();
        EmailSexDays emailSexDay = null;
        EmailSexDaysDetails sexDaysDetails = null;
        String rucEmisor = "";
        for(PaymentVoucherInterDto obj : objects){

            if((obj.getEmisor()).equals(rucEmisor)){
                sexDaysDetails = new EmailSexDaysDetails();
                sexDaysDetails.setDocumento(obj.getIdentificador());
                sexDaysDetails.setEmision(obj.getFechaEmision());
                sexDaysDetails.setRegistro(obj.getFechaRegistro());
                sexDaysDetails.setReceptor(obj.getDenoReceptor());
                sexDaysDetails.setMonto(obj.getMontoImporte());
                emailSexDay.getDetails().add(sexDaysDetails);
            }else{

                emailSexDay = new EmailSexDays();
                emailSexDay.setRuc(obj.getEmisor());
                emailSexDay.setNombre(obj.getRazon());
                emailSexDay.setEmail(obj.getEmail());

                sexDaysDetails = new EmailSexDaysDetails();
                sexDaysDetails.setDocumento(obj.getIdentificador());
                sexDaysDetails.setEmision(obj.getFechaEmision());
                sexDaysDetails.setRegistro(obj.getFechaRegistro());
                sexDaysDetails.setReceptor(obj.getDenoReceptor());
                sexDaysDetails.setMonto(obj.getMontoImporte());

                emailSexDay.setDetails(new ArrayList<>());
                emailSexDay.getDetails().add(sexDaysDetails);

                emailSexDays.add(emailSexDay);

            }

            rucEmisor = obj.getEmisor();
        }

        return emailSexDays;
    }


    private Map<String, Object> regenerarPayment(PaymentVoucherEntity entity) {
        PaymentVoucher voucher = PaymentVoucher.builder()
                .tipoComprobante(entity.getTipoComprobante())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .fechaEmision(entity.getFechaEmision())
                .horaEmision(entity.getHoraEmision())
                .fechaVencimiento(entity.getFechaVencimiento())
                .codigoMoneda(entity.getCodigoMoneda())
                .codigoTipoOperacion(entity.getTipoOperacion())
                .rucEmisor(entity.getRucEmisor())
                .direccionOficinaEmisor(entity.getDireccionReceptor())
                .codigoLocalAnexoEmisor(entity.getCodigoLocalAnexo())
                .tipoDocumentoReceptor(entity.getTipoDocIdentReceptor())
                .numeroDocumentoReceptor(entity.getNumDocIdentReceptor())
                .denominacionReceptor(entity.getDenominacionReceptor())
                .direccionReceptor(entity.getDireccionReceptor())
                .emailReceptor(entity.getEmailReceptor())
                .guiasRelacionadas(GuiaRelacionada.transformToBeanList(entity.getGuiasRelacionadas()))
                .totalValorVentaExportacion(entity.getTotalValorVentaOperacionExportada())
                .totalValorVentaGravada(entity.getTotalValorVentaOperacionGravada())
                .totalValorVentaInafecta(entity.getTotalValorVentaOperacionInafecta())
                .totalValorVentaExonerada(entity.getTotalValorVentaOperacionExonerada())
                .totalValorVentaGratuita(entity.getTotalValorVentaOperacionGratuita())
                .totalDescuento(entity.getTotalDescuento())
                .totalIgv(entity.getSumatoriaIGV())
                .totalIsc(entity.getSumatoriaISC())
                .totalOtrostributos(entity.getSumatoriaOtrosTributos())
                .descuentoGlobales(entity.getMontoDescuentoGlobal())
                .importeTotalVenta(entity.getMontoImporteTotalVenta())
                .serieAfectado(entity.getSerieAfectado())
                .numeroAfectado(entity.getNumeroAfectado())
                .tipoComprobanteAfectado(entity.getTipoComprobanteAfectado())
                .codigoTipoNotaCredito(entity.getCodigoTipoNotaCredito())
                .codigoTipoNotaDebito(entity.getCodigoTipoNotaDebito())
                .motivoNota(entity.getMotivoNota())
                .items(PaymentVoucherLine.transformToBeanList(entity.getDetailsPaymentVouchers()))
                .identificadorDocumento(entity.getIdentificadorDocumento())
                .camposAdicionales(CampoAdicional.transformToBeanList(entity.getAditionalFields()))
                .cuotas(PaymentVoucherCuota.transformToBeanList(entity.getCuotas()))
                .codigoMedioPago(entity.getCodigoMedioPago())
                .cuentaFinancieraBeneficiario(entity.getCuentaFinancieraBeneficiario())
                .codigoBienDetraccion(entity.getCodigoBienDetraccion())
                .porcentajeDetraccion(entity.getPorcentajeDetraccion())
                .montoDetraccion(entity.getMontoDetraccion())
                .detraccion(entity.getDetraccion())
                .oficinaId(entity.getOficina()!=null ? entity.getOficina().getId() : null)
                .build();
        System.out.println("PAYMENT VOUCHER RESEND");
        return generationDocument(voucher, true, entity.getUserName());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> resendPaymentVoucher(Long idPaymentVoucher, UserPrincipal userName) throws ServiceException {

        PaymentVoucherEntity entity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
        PaymentVoucher voucher = PaymentVoucher.builder()
                .tipoComprobante(entity.getTipoComprobante())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .fechaEmision(entity.getFechaEmision())
                .horaEmision(entity.getHoraEmision())
                .fechaVencimiento(entity.getFechaVencimiento())
                .codigoMoneda(entity.getCodigoMoneda())
                .codigoTipoOperacion(entity.getTipoOperacion())
                .rucEmisor(entity.getRucEmisor())
                .direccionOficinaEmisor(entity.getDireccionReceptor())
                .codigoLocalAnexoEmisor(entity.getCodigoLocalAnexo())
                .tipoDocumentoReceptor(entity.getTipoDocIdentReceptor())
                .numeroDocumentoReceptor(entity.getNumDocIdentReceptor())
                .denominacionReceptor(entity.getDenominacionReceptor())
                .direccionReceptor(entity.getDireccionReceptor())
                .emailReceptor(entity.getEmailReceptor())
                .guiasRelacionadas(GuiaRelacionada.transformToBeanList(entity.getGuiasRelacionadas()))
                .totalValorVentaExportacion(entity.getTotalValorVentaOperacionExportada())
                .totalValorVentaGravada(entity.getTotalValorVentaOperacionGravada())
                .totalValorVentaInafecta(entity.getTotalValorVentaOperacionInafecta())
                .totalValorVentaExonerada(entity.getTotalValorVentaOperacionExonerada())
                .totalValorVentaGratuita(entity.getTotalValorVentaOperacionGratuita())
                .totalDescuento(entity.getTotalDescuento())
                .totalIgv(entity.getSumatoriaIGV())
                .totalIsc(entity.getSumatoriaISC())
                .totalOtrostributos(entity.getSumatoriaOtrosTributos())
                .descuentoGlobales(entity.getMontoDescuentoGlobal())
                .importeTotalVenta(entity.getMontoImporteTotalVenta())
                .serieAfectado(entity.getSerieAfectado())
                .numeroAfectado(entity.getNumeroAfectado())
                .tipoComprobanteAfectado(entity.getTipoComprobanteAfectado())
                .codigoTipoNotaCredito(entity.getCodigoTipoNotaCredito())
                .codigoTipoNotaDebito(entity.getCodigoTipoNotaDebito())
                .motivoNota(entity.getMotivoNota())
                .items(PaymentVoucherLine.transformToBeanList(entity.getDetailsPaymentVouchers()))
                .identificadorDocumento(entity.getIdentificadorDocumento())
                .camposAdicionales(CampoAdicional.transformToBeanList(entity.getAditionalFields()))
                .codigoMedioPago(entity.getCodigoMedioPago())
                .cuentaFinancieraBeneficiario(entity.getCuentaFinancieraBeneficiario())
                .codigoBienDetraccion(entity.getCodigoBienDetraccion())
                .porcentajeDetraccion(entity.getPorcentajeDetraccion())
                .montoDetraccion(entity.getMontoDetraccion())
                .detraccion(entity.getDetraccion())
                .oficinaId(entity.getOficina()!=null ? entity.getOficina().getId() : null)
                .build();
        return generationDocument(voucher, true, entity.getUserName());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> resendPaymentVoucherOnlyxml(Long idPaymentVoucher, UserPrincipal userName) throws ServiceException {

        PaymentVoucherEntity entity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
        PaymentVoucher voucher = PaymentVoucher.builder()
                .tipoComprobante(entity.getTipoComprobante())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .fechaEmision(entity.getFechaEmision())
                .horaEmision(entity.getHoraEmision())
                .fechaVencimiento(entity.getFechaVencimiento())
                .codigoMoneda(entity.getCodigoMoneda())
                .codigoTipoOperacion(entity.getTipoOperacion())
                .rucEmisor(entity.getRucEmisor())
                .direccionOficinaEmisor(entity.getDireccionReceptor())
                .codigoLocalAnexoEmisor(entity.getCodigoLocalAnexo())
                .tipoDocumentoReceptor(entity.getTipoDocIdentReceptor())
                .numeroDocumentoReceptor(entity.getNumDocIdentReceptor())
                .denominacionReceptor(entity.getDenominacionReceptor())
                .direccionReceptor(entity.getDireccionReceptor())
                .emailReceptor(entity.getEmailReceptor())
                .guiasRelacionadas(GuiaRelacionada.transformToBeanList(entity.getGuiasRelacionadas()))
                .totalValorVentaExportacion(entity.getTotalValorVentaOperacionExportada())
                .totalValorVentaGravada(entity.getTotalValorVentaOperacionGravada())
                .totalValorVentaInafecta(entity.getTotalValorVentaOperacionInafecta())
                .totalValorVentaExonerada(entity.getTotalValorVentaOperacionExonerada())
                .totalValorVentaGratuita(entity.getTotalValorVentaOperacionGratuita())
                .totalDescuento(entity.getTotalDescuento())
                .totalIgv(entity.getSumatoriaIGV())
                .totalIsc(entity.getSumatoriaISC())
                .totalOtrostributos(entity.getSumatoriaOtrosTributos())
                .descuentoGlobales(entity.getMontoDescuentoGlobal())
                .importeTotalVenta(entity.getMontoImporteTotalVenta())
                .serieAfectado(entity.getSerieAfectado())
                .numeroAfectado(entity.getNumeroAfectado())
                .tipoComprobanteAfectado(entity.getTipoComprobanteAfectado())
                .codigoTipoNotaCredito(entity.getCodigoTipoNotaCredito())
                .codigoTipoNotaDebito(entity.getCodigoTipoNotaDebito())
                .motivoNota(entity.getMotivoNota())
                .items(PaymentVoucherLine.transformToBeanList(entity.getDetailsPaymentVouchers()))
                .identificadorDocumento(entity.getIdentificadorDocumento())
                .camposAdicionales(CampoAdicional.transformToBeanList(entity.getAditionalFields()))
                .codigoMedioPago(entity.getCodigoMedioPago())
                .cuentaFinancieraBeneficiario(entity.getCuentaFinancieraBeneficiario())
                .codigoBienDetraccion(entity.getCodigoBienDetraccion())
                .porcentajeDetraccion(entity.getPorcentajeDetraccion())
                .montoDetraccion(entity.getMontoDetraccion())
                .detraccion(entity.getDetraccion())
                .oficinaId(entity.getOficina()!=null ? entity.getOficina().getId() : null)
                .build();
        return generationDocumentXml(voucher, true, entity.getUserName());
    }
    void formatPaymentVoucher(PaymentVoucher voucher) {
        /*
         * ubl 2.1
         */
        if (voucher.getTotalValorVentaGravada() != null && voucher.getTotalValorVentaGravada().compareTo(new BigDecimal(0)) == 0) {
            voucher.setTotalValorVentaGravada(null);
        }
        if (voucher.getTotalValorVentaGratuita() != null && voucher.getTotalValorVentaGratuita().compareTo(new BigDecimal(0)) == 0) {
            //voucher.setTotalValorVentaGratuita(null);
        }
        if (voucher.getTotalValorVentaExonerada() != null && voucher.getTotalValorVentaExonerada().compareTo(new BigDecimal(0)) == 0) {
            voucher.setTotalValorVentaExonerada(null);
        }
        if (voucher.getTotalValorVentaExportacion() != null && voucher.getTotalValorVentaExportacion().compareTo(new BigDecimal(0)) == 0) {
            voucher.setTotalValorVentaExportacion(null);
        }
        if (voucher.getTotalValorVentaInafecta() != null && voucher.getTotalValorVentaInafecta().compareTo(new BigDecimal(0)) == 0) {
            voucher.setTotalValorVentaInafecta(null);
        }
        if (voucher.getTotalIgv() != null && voucher.getTotalIgv().compareTo(new BigDecimal(0)) == 0) {
            voucher.setTotalIgv(null);
        }
        if (voucher.getMontoDetraccion() != null) {
            voucher.setMontoDetraccion(voucher.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
    }

    Map<String, Object> generationDocument(PaymentVoucher voucher, Boolean isEdit, String userName) throws ServiceException {
        String userNameBoleta = ConstantesParameter.USER_API_SCHEDULER;
        Map<String, Object> result = new HashMap<>();
        Map<String, String> templateGenerated;
        ResponsePSE response = new ResponsePSE();
        boolean isFacturaOrNoteAssociated = true;
        String fileXMLZipBase64 = null;
        String fileXMLBase64 = null;
        String messageResponse = null;
        String nameDocument = null;
        String estadoRegistro;
        String estadoEnSunat;
        Integer estadoItem = null;
        SendBillDTO dataSendBill;
        SendBoletaDTO sendBoletaDTO;
        PaymentVoucherEntity paymentVoucher = null;
        Long idPaymentVoucher;
        Date fechaActual;
        Boolean status;
        StringBuilder msgLog = new StringBuilder();
        PaymentVoucherEntity paymentVoucherOld = null;

        try {

            formatPaymentVoucher(voucher);
            CompanyEntity companyEntity = completarDatosEmisor(voucher);

            validate.validatePaymentVoucher(voucher, isEdit);
            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_FIELDS, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);


            //FLAG DE ENVIO POR SENDBILL O RESUMEN
            if (voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                    || ((voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                    || voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO))
                    && voucher.getTipoComprobanteAfectado().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))) {
                isFacturaOrNoteAssociated = false;
            }
            if (voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) && voucher.getTipoTransaccion()==null){
                voucher.setTipoTransaccion(BigDecimal.ONE);
            }
            //SI ES EDICION VALIDO ESTADOS
            if (isEdit) {
                messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
                paymentVoucherOld = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(voucher.getRucEmisor(), voucher.getTipoComprobante(), voucher.getSerie(), voucher.getNumero());

                //VALIDO QUE EXISTA EL COMPROBANTE A EDITAR
                if (paymentVoucherOld == null)
                    throw new ServiceException("Este comprobante que desea editar, no existe en la base de datos del PSE");

                //VALIDO QUE EL ESTADO DEL COMPROBANTE SEA REGISTRADO Y QUE AUN NO ESTE EN LA SUNAT PARA PODER EDITARLO
                if (isFacturaOrNoteAssociated) {
                    if (!paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo())
                            || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                            || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
                            ) {
                        throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
                    }
                }
            } else {
                messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
            }

            //SETEANDO ESTADO DEL ITEM RESUMEN
            if (!isFacturaOrNoteAssociated) {
                //SI ESTA EDITANDO UNA BOLETA O NOTAS ASOCIADAS A BOLETAS
                if (isEdit) {
                    //SI EL COMPROBANTE YA ESTA REGISTRADO EN SUNAT, EL ESTADO DE RESUMEN ES 2 MODIFICACION
                    if (paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
                        estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION;
                        //SI NO EL ESTADO ES 1 ADICIONAR
                    } else {
                        estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
                    }
                }
                //SI NO ESTA EDITANDO EL ESTADO PARA RESUMEN ES 1 ADICIONAR
                else {
                    estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
                }

            }


            if (companyEntity.getOse() != null && companyEntity.getOse().getId()==1) {
                templateGenerated = templateService.buildPaymentVoucherSignOse(voucher);
            } else {
                templateGenerated = templateService.buildPaymentVoucherSign(voucher);
            }

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.GENERATE_TEMPLATE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            fileXMLBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][UBL:").
                    append(UblVersion.TWODOTZERO).append("][nameDocument:").append(nameDocument).append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_XML, msgLog.toString());

            RegisterFileUploadEntity responseStorage = uploadXmlComprobante(companyEntity, nameDocument, voucher.getTipoComprobante(),
                    ConstantesParameter.REGISTRO_STATUS_NUEVO, fileXMLZipBase64);

//			msgLog.delete(0, msgLog.length()-1);
            msgLog.setLength(0);
            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][nameDocument:").
                    append(nameDocument).append("][uuidGenerado:").append("UUID").append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.STORAGE_FILE, msgLog.toString());


            fechaActual = Calendar.getInstance().getTime();
            estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            voucher.setCodigoHash(templateGenerated.get(ConstantesParameter.CODIGO_HASH));

            paymentVoucher = registrarPaymentVoucher(voucher, responseStorage.getIdRegisterFileSend(), isEdit, paymentVoucherOld, estadoRegistro, estadoRegistro, estadoEnSunat,
                    estadoItem, messageResponse, userName, null, new Timestamp(fechaActual.getTime()), null,
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER);


            msgLog.setLength(0);
            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][estadoRegistro:").
                    append(estadoRegistro).append("][estadoSunat:").append(estadoEnSunat).append("][").
                    append(voucher.toString()).append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.INSERT_BD, msgLog.toString());

            idPaymentVoucher = paymentVoucher.getIdPaymentVoucher();

            if (isFacturaOrNoteAssociated) {

                registerVoucherTemporal(idPaymentVoucher, nameDocument, UUIDGen.generate(), voucher.getTipoComprobante(), isEdit);

                msgLog.setLength(0);
                msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][idPaymentVoucher:").
                        append(idPaymentVoucher).append("][nameDocument:").append(nameDocument).append("][uuidSaved:").
                        append(UUIDGen.generate()).append("][TipoComprobante:").append(voucher.getTipoComprobante()).append("]");
                Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                        OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.INSERT_TMP_BD, msgLog.toString());

                dataSendBill = new SendBillDTO();
                dataSendBill.setRuc(voucher.getRucEmisor());
                dataSendBill.setIdPaymentVoucher(idPaymentVoucher);
                dataSendBill.setNameDocument(nameDocument);
                dataSendBill.setEnvioAutomaticoSunat(companyEntity.getEnvioAutomaticoSunat() == null ? true : companyEntity.getEnvioAutomaticoSunat());
                result.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, dataSendBill);
            }else if(companyEntity.getEnvioDirecto()!=null && companyEntity.getEnvioDirecto()==true) {
                sendBoletaDTO = new SendBoletaDTO();
                IdentificadorComprobante identificadorComprobante = new IdentificadorComprobante();
                identificadorComprobante.setTipo(voucher.getTipoComprobante());
                identificadorComprobante.setSerie(voucher.getSerie());
                identificadorComprobante.setNumero(voucher.getNumero());
                sendBoletaDTO.setRuc(voucher.getRucEmisor());
                sendBoletaDTO.setFechaEmision(voucher.getFechaEmision());
                sendBoletaDTO.setNameDocument(identificadorComprobante);
                sendBoletaDTO.setUser(userNameBoleta);
                sendBoletaDTO.setEnvioDirecto(companyEntity.getEnvioDirecto() == null ? false : companyEntity.getEnvioDirecto());
                result.put(ConstantesParameter.PARAM_BEAN_SEND_BOLETA,sendBoletaDTO);
            }
            status = true;

            result.put("idPaymentVoucher", idPaymentVoucher);

        } catch (ValidatorFieldsException e) {

            status = false;
            messageResponse = e.getMensajeValidacion();

            Logger.register(TipoLogEnum.WARNING, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_FIELDS, messageResponse);

        } catch (TemplateException | SignedException e) {

            status = false;
            messageResponse = "Error al generar plantilla del documento[" + voucher.getIdentificadorDocumento() + "] " + e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    messageResponse, voucher.toString(), e);

        } catch (ServiceException e) {

            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_XML,
                    messageResponse, voucher.toString(), e);

        } catch (DataIntegrityViolationException | ConstraintViolationException | SQLIntegrityConstraintViolationException e) {

            status = false;
            messageResponse = "Error al guardar comprobante en BD: [" + e.getMessage() + "]";

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.INSERT_BD,
                    messageResponse, voucher.toString(), e);

        } catch (Exception e) {

            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.IN_PROCESS,
                    messageResponse, voucher.toString(), e);

        }

        if (!status) {
            throw new ServiceException(messageResponse);
        }

        response.setMensaje(messageResponse);
        response.setEstado(status);
        response.setNombre(nameDocument);
        setUrlsToResponse(response, paymentVoucher);

        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.COMPLETED, response.toString());

        return result;
    }
    Map<String, Object> generationDocumentXml(PaymentVoucher voucher, Boolean isEdit, String userName) throws ServiceException {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> templateGenerated;
        ResponsePSE response = new ResponsePSE();
        boolean isFacturaOrNoteAssociated = true;
        String fileXMLZipBase64 = null;
        String fileXMLBase64 = null;
        String messageResponse = null;
        String nameDocument = null;
        String estadoRegistro;
        String estadoEnSunat;
        Integer estadoItem = null;
        SendBillDTO dataSendBill;
        PaymentVoucherEntity paymentVoucher = null;
        Long idPaymentVoucher;
        Date fechaActual;
        Boolean status;
        StringBuilder msgLog = new StringBuilder();
        PaymentVoucherEntity paymentVoucherOld = null;

        try {

            formatPaymentVoucher(voucher);
            CompanyEntity companyEntity = completarDatosEmisor(voucher);

            validate.validatePaymentVoucher(voucher, isEdit);
            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_FIELDS, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);


            //FLAG DE ENVIO POR SENDBILL O RESUMEN
            if (voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                    || ((voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                    || voucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO))
                    && voucher.getTipoComprobanteAfectado().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))) {
                isFacturaOrNoteAssociated = false;
            }

            //SI ES EDICION VALIDO ESTADOS
            if (isEdit) {
                messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
                paymentVoucherOld = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(voucher.getRucEmisor(), voucher.getTipoComprobante(), voucher.getSerie(), voucher.getNumero());

                //VALIDO QUE EXISTA EL COMPROBANTE A EDITAR
                if (paymentVoucherOld == null)
                    throw new ServiceException("Este comprobante que desea editar, no existe en la base de datos del PSE");

                //VALIDO QUE EL ESTADO DEL COMPROBANTE SEA REGISTRADO Y QUE AUN NO ESTE EN LA SUNAT PARA PODER EDITARLO
                if (isFacturaOrNoteAssociated) {
                    if (!paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo())
                            || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                            || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
                            ) {
                        throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
                    }
                }
            } else {
                messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
            }

            //SETEANDO ESTADO DEL ITEM RESUMEN
            if (!isFacturaOrNoteAssociated) {
                //SI ESTA EDITANDO UNA BOLETA O NOTAS ASOCIADAS A BOLETAS
                if (isEdit) {
                    //SI EL COMPROBANTE YA ESTA REGISTRADO EN SUNAT, EL ESTADO DE RESUMEN ES 2 MODIFICACION
                    if (paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
                        estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION;
                        //SI NO EL ESTADO ES 1 ADICIONAR
                    } else {
                        estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
                    }
                }
                //SI NO ESTA EDITANDO EL ESTADO PARA RESUMEN ES 1 ADICIONAR
                else {
                    estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
                }

            }


            if (companyEntity.getOse() != null  && companyEntity.getOse().getId()==1) {
                templateGenerated = templateService.buildPaymentVoucherSignOse(voucher);
            } else {
                templateGenerated = templateService.buildPaymentVoucherSign(voucher);
            }

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.GENERATE_TEMPLATE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            fileXMLBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][UBL:").
                    append(UblVersion.TWODOTZERO).append("][nameDocument:").append(nameDocument).append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_XML, msgLog.toString());

            RegisterFileUploadEntity responseStorage = uploadXmlComprobante(companyEntity, nameDocument, voucher.getTipoComprobante(),
                    ConstantesParameter.REGISTRO_STATUS_NUEVO, fileXMLZipBase64);

//			msgLog.delete(0, msgLog.length()-1);
            msgLog.setLength(0);
            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][nameDocument:").
                    append(nameDocument).append("][uuidGenerado:").append("UUID").append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.STORAGE_FILE, msgLog.toString());


            fechaActual = Calendar.getInstance().getTime();
            estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            voucher.setCodigoHash(templateGenerated.get(ConstantesParameter.CODIGO_HASH));

            paymentVoucher = registrarPaymentVoucher(voucher, responseStorage.getIdRegisterFileSend(), isEdit, paymentVoucherOld, estadoRegistro, estadoRegistro, estadoEnSunat,
                    estadoItem, messageResponse, userName, null, new Timestamp(fechaActual.getTime()), null,
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER);


            msgLog.setLength(0);
            msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][estadoRegistro:").
                    append(estadoRegistro).append("][estadoSunat:").append(estadoEnSunat).append("][").
                    append(voucher.toString()).append("]");

            Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.INSERT_BD, msgLog.toString());

            idPaymentVoucher = paymentVoucher.getIdPaymentVoucher();


            status = true;

            result.put("idPaymentVoucher", idPaymentVoucher);

        } catch (ValidatorFieldsException e) {

            status = false;
            messageResponse = e.getMensajeValidacion();

            Logger.register(TipoLogEnum.WARNING, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_FIELDS, messageResponse);

        } catch (TemplateException | SignedException e) {

            status = false;
            messageResponse = "Error al generar plantilla del documento xml [" + voucher.getIdentificadorDocumento() + "] " + e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    messageResponse, voucher.toString(), e);

        } catch (ServiceException e) {

            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.VALIDATE_XML,
                    messageResponse, voucher.toString(), e);

        } catch (DataIntegrityViolationException | ConstraintViolationException | SQLIntegrityConstraintViolationException e) {

            status = false;
            messageResponse = "Error al guardar comprobante en BD: [" + e.getMessage() + "]";

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.INSERT_BD,
                    messageResponse, voucher.toString(), e);

        } catch (Exception e) {

            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.IN_PROCESS,
                    messageResponse, voucher.toString(), e);

        }

        if (!status) {
            throw new ServiceException(messageResponse);
        }

        response.setMensaje(messageResponse);
        response.setEstado(status);
        response.setNombre(nameDocument);
        setUrlsToResponse(response, paymentVoucher);

        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        Logger.register(TipoLogEnum.INFO, voucher.getRucEmisor(), voucher.getIdentificadorDocumento(),
                OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.COMPLETED, response.toString());

        return result;
    }

    private void setUrlsToResponse(ResponsePSE response, PaymentVoucherEntity paymentVoucher) {
        if (paymentVoucher != null) {
            String urlTicket = urlServiceDownload + "descargapdfuuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/ticket/" + paymentVoucher.getIdentificadorDocumento();
            String urlA4 = urlServiceDownload + "descargapdfuuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/a4/" + paymentVoucher.getIdentificadorDocumento();
            String urlXml = urlServiceDownload + "descargaxmluuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/" + paymentVoucher.getIdentificadorDocumento();
            response.setUrlPdfTicket(urlTicket);
            response.setUrlPdfA4(urlA4);
            response.setUrlXml(urlXml);
            response.setCodigoHash(paymentVoucher.getCodigoHash());
        }
    }


    private CompanyEntity completarDatosEmisor(PaymentVoucher voucher) {
        CompanyEntity company = companyRepository.findByRuc(voucher.getRucEmisor());
        voucher.setDenominacionEmisor(company.getRazonSocial());
        voucher.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        voucher.setNombreComercialEmisor(company.getNombreComercial());
        voucher.setUblVersion(company.getUblVersion() != null ? company.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);
        return company;
    }

    private RegisterFileUploadEntity uploadXmlComprobante(CompanyEntity companyEntity, String nameDocument, String tipoComprobante, String estadoRegistro,
                                                          String fileXMLZipBase64) throws Exception {
        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "invoice", companyEntity);
        return file;
    }

    @Override
    public ResponsePSE getDocuments(PaymentVoucherParamsInput params) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        List<Comprobante> listaComprobantes = new ArrayList<>();
        List<PaymentVoucherEntity> listaComprobantesEntity;
        ResponsePSE respuesta = new ResponsePSE();
        respuesta.setEstado(false);
        try {
            validateParams.validatePaymentVoucherParamsInput(params);
            searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_RUC_EMISOR)
                    .operation(ConstantesParameter.OPERADOR_IGUAL).value(params.getRucEmisor()).build());
            searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_TIPO_COMPROBANTE)
                    .operation(ConstantesParameter.OPERADOR_IGUAL).value(params.getTipoComprobante()).build());
            if (params.getSerie() != null) {
                searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_SERIE)
                        .operation(ConstantesParameter.OPERADOR_IGUAL).value(params.getSerie()).build());
            }
            if (params.getNumero() != null && params.getNumero() > 0) {
                searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_NUMERO)
                        .operation(ConstantesParameter.OPERADOR_IGUAL).value(params.getNumero()).build());
            }
            if (StringUtils.isNotBlank(params.getFechaEmisionDesde())) {
                searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_FECHA_EMISION)
                        .operation(ConstantesParameter.OPERADOR_MAYOR_IGUAL)
                        .value(UtilFormat.fechaDate(params.getFechaEmisionDesde())).build());
            }
            if (StringUtils.isNotBlank(params.getFechaEmisionHasta())) {
                searchCriteriaList.add(SearchCriteria.builder().key(ConstantesParameter.FIELD_FECHA_EMISION)
                        .operation(ConstantesParameter.OPERADOR_MENOR_IGUAL)
                        .value(UtilFormat.fechaDate(params.getFechaEmisionHasta())).build());
            }

            listaComprobantesEntity = comprobantesDao.buscarComprobantes(searchCriteriaList);

            for (PaymentVoucherEntity voucherEntity : listaComprobantesEntity) {

                listaComprobantes.add(Comprobante.builder().rucEmisor(voucherEntity.getRucEmisor())
                        .tipoComprobante(voucherEntity.getTipoComprobante())
                        .fechaEmision(voucherEntity.getFechaEmision()).estado(voucherEntity.getEstado())
                        .moneda(voucherEntity.getCodigoMoneda()).serie(voucherEntity.getSerie())
                        .numero(voucherEntity.getNumero()).build());
            }
            respuesta.setEstado(true);
            respuesta.setRespuesta(listaComprobantes);
            respuesta.setMensaje(ConstantesParameter.MSG_RESP_OK);

        } catch (ValidatorFieldsException e) {

            respuesta.setMensaje(e.getMensajeValidacion());


        } catch (Exception e) {

            respuesta.setMensaje(e.getMessage());
        }

        return respuesta;
    }

    @Override
    public ResponsePSE getDocumentsByIdentificadores(List<Comprobante> comprobantesIn, String rucEmisor) {

        List<String> identificadoresDocumento = new ArrayList<>();
        List<Object[]> datosComprobantes;
        List<Comprobante> comprobantesOut;
        StringBuilder identificador;
        ResponsePSE respuesta = new ResponsePSE();

        try {
            respuesta.setEstado(false);

            validateComprobantes.validateListaComprobantes(comprobantesIn, rucEmisor);
            for (Comprobante comprobante : comprobantesIn) {

                identificador = new StringBuilder(rucEmisor).append("-").append(comprobante.getTipoComprobante())
                        .append("-").append(comprobante.getSerie()).append("-").append(comprobante.getNumero());

                identificadoresDocumento.add(identificador.toString());
            }

            datosComprobantes = paymentVoucherRepository
                    .getListaDatosDocumentosByIdentificadores(identificadoresDocumento);

            comprobantesOut = new ArrayList<>();
            for (Object[] document : datosComprobantes) {
                comprobantesOut.add(Comprobante.builder().tipoComprobante((String) document[0])
                        .serie((String) document[1]).numero((Integer) document[2]).fechaEmision((String) document[3])
                        .estado((String) document[4]).moneda((String) document[5]).build());
            }
            respuesta.setEstado(true);
            respuesta.setRespuesta(comprobantesOut);
            respuesta.setMensaje(ConstantesParameter.MSG_RESP_OK);

        } catch (ValidatorFieldsException e) {

            respuesta.setMensaje(e.getMensajeValidacion());

        } catch (Exception e) {

            respuesta.setMensaje(e.getMessage());
        }
        return respuesta;
    }

    @Override
    public Long getPaymentVoucherIdFromTemp(Long idTemporal) {
        Optional<TmpVoucherSendBillEntity> voucherPendiente = tmpVoucherSendBillRepository.findById(idTemporal);
        if (voucherPendiente.isPresent())
            return voucherPendiente.get().getIdPaymentVoucher();
        else return null;
    }

    @Override
    public PaymentVoucherEntity prepareComprobanteForEnvioSunat(String ruc, String tipo, String serie, Integer numero) throws ServiceException {

        PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(ruc, tipo, serie, numero);

        if (paymentVoucherEntity == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", ruc, tipo, serie, numero != null ? numero.toString() : ""));

        if (paymentVoucherEntity.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (paymentVoucherEntity.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        if (paymentVoucherEntity.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))
            throw new ServiceException("Por este metodo solo se permite enviar Facturas, Notas de crédito y Débito.");


        if (paymentVoucherEntity.getTipoComprobanteAfectado() != null) {
            if (
                    (paymentVoucherEntity.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) || paymentVoucherEntity.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO))
                            && paymentVoucherEntity.getTipoComprobanteAfectado().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                    ) {
                throw new ServiceException("Por este metodo solo se permite enviar Notas de crédito y Débito asociadas a Facturas, las notas asociadas a boletas se deben enviar por resumen diario.");
            }
        }

        return paymentVoucherEntity;
    }

  /*  @Transactional
	@Override
	public ResponsePSE updateBoleta(PaymentVoucher boleta, String authorization, String userName) {

		ResponsePSE response = new ResponsePSE();
		Map<String, String> templateGenerated;
		String fileXMLZipBase64;
		String nameDocument;
		String statusDocumentoUpload;
		PaymentVoucherEntity documento;
        PaymentVoucherEntity newBoleta;
		EstadoComprobanteEnum estado;
		Date fechaActual;
		StringBuilder msgLog = new StringBuilder();

		try {

			documento = validateBoleta.validateUpdateBoleta(boleta);

			msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}{").
				append("documentoToEditar:").append(documento.toString()).append("}");
			Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.VALIDATE_FIELDS, msgLog.toString());

			estado = EstadoComprobanteEnum.getEstadoComprobante(documento.getEstado());

			switch (estado) {

			case REGISTRADO:
				statusDocumentoUpload = ConstantesParameter.REGISTRO_STATUS_MODIFICAR_DOCUMENTO_REGISTRADO;
				break;
			case ACEPTADO:
			case ACEPTADO_ADVERTENCIA:
				statusDocumentoUpload = ConstantesParameter.REGISTRO_STATUS_MODIFICAR_DOCUMENTO_ACEPTADO;
				break;
			default:
				if (documento.getEstadoAnterior().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo())) {
					statusDocumentoUpload = ConstantesParameter.REGISTRO_STATUS_MODIFICAR_DOCUMENTO_REGISTRADO;
				} else {
					statusDocumentoUpload = ConstantesParameter.REGISTRO_STATUS_MODIFICAR_DOCUMENTO_ACEPTADO;
				}
			}
			completarDatosEmisor(boleta);

			templateGenerated = templateService.buildPaymentVoucherSign(boleta);
			Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.GENERATE_TEMPLATE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

			nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
			fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);

			ResponseStorage responseStorage = uploadXmlComprobante(boleta.getRucEmisor(), nameDocument, boleta.getTipoComprobante(), statusDocumentoUpload,
					fileXMLZipBase64, authorization);

			msgLog.setLength(0);
			msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}{").
			append("nameDocument:").append(nameDocument).append("}{tipoComprobante:").
			append(boleta.getTipoComprobante()).append("}{estadoBoleta:").append(statusDocumentoUpload).
			append("}");
			Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.STORAGE_FILE, msgLog.toString());

			paymentVoucherRepository.delete(documento.getIdPaymentVoucher());

			msgLog.setLength(0);
			msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}{").
			append("IdPaymentVoucher:").append(documento.getIdPaymentVoucher()).append("}");
			Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
					OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.DELETE_BD_PAYMENT_VOUCHER,
					msgLog.toString());

			fechaActual = Calendar.getInstance().getTime();

			if (statusDocumentoUpload.equals(ConstantesParameter.REGISTRO_STATUS_MODIFICAR_DOCUMENTO_REGISTRADO)) {
                newBoleta = registrarPaymentVoucher(boleta,responseStorage.getIdRegisterFileUpload(), EstadoComprobanteEnum.REGISTRADO.getCodigo(),
						EstadoComprobanteEnum.REGISTRADO.getCodigo(), EstadoSunatEnum.NO_ENVIADO.getAbreviado(),
						ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION,
						ConstantesParameter.MSG_MODIFICACION_DOCUMENTO_OK, userName, null,
						new Timestamp(fechaActual.getTime()), null, OperacionLogEnum.UPDATE_BOLETA);
			} else {
                newBoleta = registrarPaymentVoucher(boleta,responseStorage.getIdRegisterFileUpload(), EstadoComprobanteEnum.ACEPTADO.getCodigo(),
						documento.getEstadoAnterior(), EstadoSunatEnum.ACEPTADO.getAbreviado(),
						ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION,
						ConstantesParameter.MSG_MODIFICACION_DOCUMENTO_OK, documento.getUserName(), userName,
						documento.getFechaRegistro(), new Timestamp(fechaActual.getTime()),OperacionLogEnum.UPDATE_BOLETA);
			}
			Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.INSERT_BD, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);


			response.setNombre(nameDocument);
			response.setEstado(true);
			response.setMensaje(ConstantesParameter.MSG_MODIFICACION_DOCUMENTO_OK);

            setUrlsToResponse(response, newBoleta);

        } catch (ValidatorFieldsException e) {

			response.setEstado(false);
			response.setMensaje(e.getMensajeValidacion());

			Logger.register(TipoLogEnum.WARNING, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.VALIDATE_FIELDS, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

			new SentryExceptionResolver().resolveException(null, null, e, e);

		} catch (TemplateException | SignedException e) {

			response.setEstado(false);
			response.setMensaje("Error al generar plantilla del documento[" + boleta.getIdentificadorDocumento()
					+ "] " + e.getMessage());

			Logger.register(TipoLogEnum.ERROR, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.GENERATE_TEMPLATE,
				response.getMensaje(), boleta.toString(), e);

			new SentryExceptionResolver().resolveException(null, null, e, e);
		} catch (PersonalizedException e) {

			response.setEstado(false);
			response.setMensaje(e.getMessage());

			Logger.register(TipoLogEnum.ERROR, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
				OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.VALIDATE_XML,
				response.getMensaje(), boleta.toString(), e);

			new SentryExceptionResolver().resolveException(null, null, e, e);
		} catch (Exception e) {

			response.setEstado(false);
			response.setMensaje(e.getMessage());

			Logger.register(TipoLogEnum.ERROR, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
					OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.IN_PROCESS,
					response.getMensaje(), boleta.toString(), e);

			new SentryExceptionResolver().resolveException(null, null, e, e);
		}

		Logger.register(TipoLogEnum.INFO, boleta.getRucEmisor(), boleta.getIdentificadorDocumento(),
	        	OperacionLogEnum.UPDATE_BOLETA, SubOperacionLogEnum.COMPLETED, response.toString());

		return response;
	}*/

    private void registerVoucherTemporal(Long idPaymentVoucher, String nombreCompletoDocumento, String uuidSaved,
                                         String tipoComprobante, Boolean isEdit) {

        TmpVoucherSendBillEntity tmpEntity = null;

        if (!isEdit) {
            tmpEntity = new TmpVoucherSendBillEntity();
        } else {
            tmpEntity = tmpVoucherSendBillRepository.findByIdPaymentVoucher(idPaymentVoucher);
        }

        tmpEntity.setEstado(EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        tmpEntity.setIdPaymentVoucher(idPaymentVoucher);
        tmpEntity.setNombreDocumento(nombreCompletoDocumento);
        tmpEntity.setUuidSaved(uuidSaved);
        tmpEntity.setTipoComprobante(tipoComprobante);

        tmpVoucherSendBillRepository.save(tmpEntity);

    }

   /* @Transactional
    @Override
    public void actualizarEstadoComprobante(PaymentVoucherEntity paymentVoucherEntity) {

        List<ErrorEntity> listCatologoPendientes = errorRepository.getListCatalogoPendientes(paymentVoucherEntity.getCodigosRespuestaSunat());

        if (listCatologoPendientes != null && listCatologoPendientes.size() > 0) {

            paymentVoucherRepository.updateEstadoComprobanteRegistrado(paymentVoucherEntity.getIdPaymentVoucher());

        } else {

            List<ErrorEntity> listCatologoErrores = errorRepository.getListCatalogoErrores(paymentVoucherEntity.getCodigosRespuestaSunat());

            if (listCatologoErrores != null && listCatologoErrores.size() > 0) {

                paymentVoucherRepository.updateEstadoComprobanteRechazado(paymentVoucherEntity.getIdPaymentVoucher());

            }
        }
    }*/

    @Override
    public ResponsePSE consultaCdrComprobante(String authorization, String ruc, String tipo, String serie, Integer numero) {

        if (tipo == null || tipo.isEmpty()) {
            throw new ServiceException("Debe ingresar tipo de documento");
        }

        if (serie == null || serie.isEmpty()) {
            throw new ServiceException("Debe ingresar serie");
        }

        if (numero == null) {
            throw new ServiceException("Debe ingresar numero documento");
        }

        PaymentVoucherEntity comprobante = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(ruc, tipo, serie, numero);

        if (comprobante == null) {
            throw new ServiceException("El comprobante que desea consultar no existe.");
        }

        ResponsePSE respuesta = new ResponsePSE();
        respuesta.setEstado(true);
        respuesta.setEstadoSunat(StringsUtils.getRespuestaEstadoSunat(comprobante.getEstadoSunat()));

        if (comprobante.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
            if (comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                    comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO) ||
                    comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                    ) {

                String nombreArchivoCdr = String.format("%s-%s-%s-%s-%s", "R", comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString());
                respuesta.setNombre(nombreArchivoCdr);

                try {
                    String urlCdr = urlServiceDownload + "descargacdruuid/" + comprobante.getIdPaymentVoucher() + "/" + comprobante.getUuid() + "/R-" + comprobante.getIdentificadorDocumento();
                    respuesta.setUrlCdr(urlCdr);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }


        return respuesta;
    }

    @Override
    public List<PaymentVoucherInterDto> getFacturasNoEnviadasCon7Dias() {
        Date now = new Date();
        Date hace7Dias = UtilDate.sumarDiasAFecha(now, -6);

        String hace7DiasString = UtilDate.dateToString(hace7Dias, "yyyy-MM-dd");

        return paymentVoucherRepository.findAllNoEnviados(hace7DiasString);
    }



}




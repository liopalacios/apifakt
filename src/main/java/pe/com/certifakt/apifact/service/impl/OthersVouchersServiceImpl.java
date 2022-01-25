package pe.com.certifakt.apifact.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.OtherCpeDto;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.exception.SignedException;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.OthersVouchersService;
import pe.com.certifakt.apifact.service.TemplateService;
import pe.com.certifakt.apifact.util.*;
import pe.com.certifakt.apifact.validate.GuiaRemisionValidate;
import pe.com.certifakt.apifact.validate.OtherDocumentCpeValidate;

//import javax.xml.soap.Detail;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OthersVouchersServiceImpl implements OthersVouchersService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AmazonS3ClientService amazonS3ClientService;

    @Autowired
    private OtherCpeRepository otherCpeRepository;

    @Autowired
    private TypeFieldRepository typeFieldRepository;

    @Autowired
    private DetailGuiaRemisionRepository detailGuiaRepository;

    @Autowired
    private DetailOtherCpeRepository detailOtherCpeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AditionalFieldGuiaRepository aditionalFieldGuiaRepository;

    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;

    @Autowired
    private GuiaRemisionValidate guiaRemisionValidate;

    @Autowired
    private OtherDocumentCpeValidate otherDocumentCpeValidate;

    @Autowired
    private TramoTrasladoRepository tramoTrasladoRepository;

    @Autowired
    private UserRepository userRepository;


    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    @Override
    public Map<String, Object> getAllComprobantesByFiltersQuery(UserPrincipal userResponse, String fechaEmisionDesde,
                                                                 String fechaEmisionHasta, String tipoComprobante, String numDocIdentReceptor, String serie, Integer numero,
                                                                 Integer pageNumber, Integer perPage) {



        if (numero == null) numero = 0;
        Page<OtherCpeEntity> result = otherCpeRepository.findAllSerchForPage(userResponse.getRuc(),
                fechaEmisionDesde, fechaEmisionHasta,"%" + tipoComprobante + "%","%" + numDocIdentReceptor + "%",
                "%" + serie + "%", numero, new PageRequest((pageNumber - 1), perPage));

        //--> AQUI COLOCAMOS LA CONVERSION

        return ImmutableMap.of("otherCpeList", result.map(OtherCpeDto::transformToDtoLite), "total", result.getTotalElements());

    }


    @Override
    public Integer getSiguienteNumeroOtherCpe(String tipoDocumento, String serie, String ruc) {

        OtherCpeEntity ultimoComprobante = otherCpeRepository
                .findFirst1ByTipoComprobanteAndSerieAndNumeroDocumentoIdentidadEmisorOrderByNumeroDesc(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante.getNumero() + 1;
        }


        return 1;
    }

    @Override
    public List<InfoEstadoSunat> getEstadoSunatByListaIds(List<Long> ids) {
        List<InfoEstadoSunat> respuesta = new ArrayList<>();
        List<OtherCpeEntity> comprobantes = otherCpeRepository.findByIdOtroCPEIn(ids);
        ObjectMapper obj = new ObjectMapper();

        comprobantes.forEach(pv -> {
            respuesta.add(InfoEstadoSunat.builder().id(pv.getIdOtroCPE()).estado(pv.getEstado())
                    .estadoSunat(pv.getEstadoEnSunat()).build());
        });
        return respuesta;
    }


    @Transactional
    @Override
    public Map<String, Object> generationOtherDocument(OtherDocumentCpe otherDocumentCPE, String authorization,Boolean isEdit, String userName) {

        Map<String, Object> result = new HashMap<>();
        ResponsePSE response = new ResponsePSE();
        Map<String, String> templateGenerated;
        String estadoRegistro;
        String messageResponse;
        String fileXMLZipBase64;
        String nameDocument;
        Long idDocumentCpeSaved;
        String estadoEnSunat;
        SendOtherDocumentDTO dataSendOtherDocumentCPE;
        OtherCpeEntity otherVoucherOld = null;

        try {

            formatDecimalRetencionPercepcion(otherDocumentCPE);
            CompanyEntity company = completarDatosEmisor(otherDocumentCPE);
            otherDocumentCpeValidate.validateOtherDocument(otherDocumentCPE, isEdit);
            Logger.register(TipoLogEnum.INFO, otherDocumentCPE.getNumeroDocumentoIdentidadReceptor(), otherDocumentCPE.getSerie()+"-"+otherDocumentCPE.getNumero(),
                    OperacionLogEnum.REGISTER_OTHER_VOUCHER, SubOperacionLogEnum.VALIDATE_FIELDS, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            if (isEdit) {
                otherVoucherOld = otherCpeRepository.findByNumeroDocumentoIdentidadEmisorAndTipoComprobanteAndSerieAndNumero(otherDocumentCPE.getNumeroDocumentoIdentidadEmisor(),otherDocumentCPE.getTipoComprobante(),otherDocumentCPE.getSerie(),otherDocumentCPE.getNumero());

                if (otherVoucherOld == null)
                    throw new ServiceException("El comprobante que desea editar, no existe en la base de datos del PSE");

                if (!otherVoucherOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo())
                        || otherVoucherOld.getEstadoEnSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                        || otherVoucherOld.getEstadoEnSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
                ) {
                    throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
                }

            } else {
                messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
            }


            estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;

            templateGenerated = templateService.buildOtherDocumentCpeSign(otherDocumentCPE);
            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);


            RegisterFileUploadEntity responseStorage = uploadXmlOthersVouchers(
                    company,
                    nameDocument,
                    otherDocumentCPE.getTipoComprobante(),
                    ConstantesParameter.REGISTRO_STATUS_NUEVO,
                    fileXMLZipBase64,
                    authorization);

            idDocumentCpeSaved = registrarOtherDocumentCPE(
                    otherDocumentCPE,
                    responseStorage.getIdRegisterFileSend(),
                    isEdit,
                    otherVoucherOld,
                    userName,
                    estadoRegistro,
                    estadoEnSunat,
                    messageResponse);

            dataSendOtherDocumentCPE = new SendOtherDocumentDTO();
            dataSendOtherDocumentCPE.setIdVoucher(idDocumentCpeSaved);
            dataSendOtherDocumentCPE.setNameDocument(nameDocument);
            dataSendOtherDocumentCPE.setUuidSaved(UUIDGen.generate());
            dataSendOtherDocumentCPE.setRuc(otherDocumentCPE.getNumeroDocumentoIdentidadEmisor());
            dataSendOtherDocumentCPE.setTipoComprobante(otherDocumentCPE.getTipoComprobante());
            dataSendOtherDocumentCPE.setEnvioAutomaticoSunat(company.getEnvioAutomaticoSunat());
            result.put(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE, dataSendOtherDocumentCPE);

            response.setEstado(true);
            response.setMensaje(messageResponse);
            response.setNombre(nameDocument);

            setUrlsToResponseOtherCpe(response, otherCpeRepository.findByIdOtroCPE(idDocumentCpeSaved));

        } catch (ValidatorFieldsException e) {

            response.setEstado(false);
            response.setMensaje(e.getMensajeValidacion());

        } catch (TemplateException | SignedException e) {
            response.setEstado(false);
            response.setMensaje(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.setEstado(false);
            response.setMensaje(e.getMessage());
            e.printStackTrace();
        }
        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        return result;
    }

    @Override
    public OtherCpeEntity prepareComprobanteForEnvioSunat(String ruc, String tipo, String serie, Integer numero) throws ServiceException {

        OtherCpeEntity paymentVoucherEntity = otherCpeRepository.findByNumeroDocumentoIdentidadEmisorAndTipoComprobanteAndSerieAndNumero(ruc, tipo, serie, numero);

        if (paymentVoucherEntity == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", ruc, tipo, serie, numero != null ? numero.toString() : ""));

        if (paymentVoucherEntity.getEstadoEnSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (paymentVoucherEntity.getEstadoEnSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        return paymentVoucherEntity;
    }

    @Override
    public GuiaRemisionEntity prepareGuiaForEnvioSunat(String ruc, String serie, Integer numero) throws ServiceException {
        String tipo = "09";
        GuiaRemisionEntity guiaRemisionEntity = guiaRemisionRepository.findByNumeroDocumentoIdentidadRemitenteAndSerieAndNumero(ruc,serie,numero);

        if (guiaRemisionEntity == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", ruc, tipo, serie, numero != null ? numero.toString() : ""));

        if (guiaRemisionEntity.getEstadoEnSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (guiaRemisionEntity.getEstadoEnSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        return guiaRemisionEntity;
    }

    private void setUrlsToResponseOtherCpe(ResponsePSE response, OtherCpeEntity otherCpe) {
        if (otherCpe != null) {
            String urlPdf = urlServiceDownload + "descargapdfuuidothercpe/" + otherCpe.getIdOtroCPE() + "/" + otherCpe.getUuid() + "/" + otherCpe.getIdentificadorDocumento();
            String urlXml = urlServiceDownload + "descargaxmluuidothercpe/" + otherCpe.getIdOtroCPE() + "/" + otherCpe.getUuid() + "/" + otherCpe.getIdentificadorDocumento();
            response.setUrlPdf(urlPdf);
            response.setUrlXml(urlXml);
            response.setNombre(otherCpe.getIdentificadorDocumento());

        }
    }

    @Transactional
    private Long registrarOtherDocumentCPE(OtherDocumentCpe otherCpe, Long idRegisterFile,Boolean isEdit,OtherCpeEntity otherCpeOld, String userName,
                                                     String estadoRegistro, String estadoEnSunat, String messageResponse) {

        Date fechaActual = Calendar.getInstance().getTime();
        OtherCpeEntity entity = new OtherCpeEntity();
        String identificadorDocumento;
        identificadorDocumento = otherCpe.getNumeroDocumentoIdentidadEmisor() + "-"
                + otherCpe.getTipoComprobante() + "-"
                + otherCpe.getSerie() + "-"
                + otherCpe.getNumero();

        if (isEdit) {

            List<DetailOtherCpeEntity> items = otherCpeOld.getDetails();

            if (items != null && !items.isEmpty()) {

                for (DetailOtherCpeEntity item : items) {
                    detailOtherCpeRepository.deleteDetailsOtherCpe(item.getIdDetailOtherCpe());
                }
            }
            entity.setIdOtroCPE(otherCpeOld.getIdOtroCPE());
            entity.setUuid(otherCpeOld.getUuid());
            entity.setOtherCpeFiles(otherCpeOld.getOtherCpeFiles());
            entity.setFechaRegistro(otherCpeOld.getFechaRegistro());
        }

        entity.setSerie(otherCpe.getSerie());
        entity.setNumero(otherCpe.getNumero());
        entity.setFechaEmision(otherCpe.getFechaEmision());
        entity.setFechaEmisionDate(UtilFormat.fechaDate(otherCpe.getFechaEmision()));
        entity.setTipoComprobante(otherCpe.getTipoComprobante());

        entity.setNumeroDocumentoIdentidadEmisor(otherCpe.getNumeroDocumentoIdentidadEmisor());
        entity.setTipoDocumentoIdentidadEmisor(otherCpe.getTipoDocumentoIdentidadEmisor());
        entity.setNombreComercialEmisor(otherCpe.getNombreComercialEmisor());
        entity.setDenominacionEmisor(otherCpe.getDenominacionEmisor());

        entity.setUbigeoDomicilioFiscalEmisor(otherCpe.getUbigeoDomicilioFiscalEmisor());
        entity.setDireccionCompletaDomicilioFiscalEmisor(otherCpe.getDireccionCompletaDomicilioFiscalEmisor());
        entity.setUrbanizacionDomicilioFiscalEmisor(otherCpe.getUrbanizacionDomicilioFiscalEmisor());
        entity.setDepartamentoDomicilioFiscalEmisor(otherCpe.getDepartamentoDomicilioFiscalEmisor());
        entity.setProvinciaDomicilioFiscalEmisor(otherCpe.getProvinciaDomicilioFiscalEmisor());
        entity.setDistritoDomicilioFiscalEmisor(otherCpe.getDistritoDomicilioFiscalEmisor());
        entity.setCodigoPaisDomicilioFiscalEmisor(otherCpe.getCodigoPaisDomicilioFiscalEmisor());

        entity.setNumeroDocumentoIdentidadReceptor(otherCpe.getNumeroDocumentoIdentidadReceptor());
        entity.setTipoDocumentoIdentidadReceptor(otherCpe.getTipoDocumentoIdentidadReceptor());
        entity.setNombreComercialReceptor(otherCpe.getNombreComercialReceptor());
        entity.setDenominacionReceptor(otherCpe.getDenominacionReceptor());
        entity.setEmailReceptor(otherCpe.getEmailReceptor());

        entity.setUbigeoDomicilioFiscalReceptor(otherCpe.getUbigeoDomicilioFiscalReceptor());
        entity.setDireccionCompletaDomicilioFiscalReceptor(otherCpe.getDireccionCompletaDomicilioFiscalReceptor());
        entity.setUrbanizacionDomicilioFiscalReceptor(otherCpe.getUrbanizacionDomicilioFiscalReceptor());
        entity.setDepartamentoDomicilioFiscalReceptor(otherCpe.getDepartamentoDomicilioFiscalReceptor());
        entity.setProvinciaDomicilioFiscalReceptor(otherCpe.getProvinciaDomicilioFiscalReceptor());
        entity.setDistritoDomicilioFiscalReceptor(otherCpe.getDistritoDomicilioFiscalReceptor());
        entity.setCodigoPaisDomicilioFiscalReceptor(otherCpe.getCodigoPaisDomicilioFiscalReceptor());

        entity.setRegimen(otherCpe.getRegimen());
        entity.setTasa(otherCpe.getTasa());
        entity.setObservaciones(otherCpe.getObservaciones());
        entity.setImporteTotalRetenidoPercibido(otherCpe.getImporteTotalRetenidoPercibido());
        entity.setImporteTotalPagadoCobrado(otherCpe.getImporteTotalPagadoCobrado());
        entity.setMontoRedondeoImporteTotal(otherCpe.getMontoRedondeoImporteTotal());
        entity.setCodigoMoneda(otherCpe.getCodigoMoneda());
        entity.setIdentificadorDocumento(identificadorDocumento);
        entity.setEstado(estadoRegistro);
        entity.setEstadoAnterior(estadoRegistro);
        entity.setEstadoEnSunat(estadoEnSunat);
        entity.setMensajeRespuesta(messageResponse);
        entity.setFechaRegistro(new Timestamp(fechaActual.getTime()));
        entity.setUserName(userName);

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            entity.addFile(OtherCpeFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }


        for (DocumentCpe detail : otherCpe.getDocumentosRelacionados()) {

            DetailOtherCpeEntity detailEntity = new DetailOtherCpeEntity();

            detailEntity.setTipoDocumentoRelacionado(detail.getTipoDocumentoRelacionado());
            detailEntity.setSerieDocumentoRelacionado(detail.getSerieDocumentoRelacionado());
            detailEntity.setNumeroDocumentoRelacionado(detail.getNumeroDocumentoRelacionado());
            detailEntity.setFechaEmisionDocumentoRelacionado(detail.getFechaEmisionDocumentoRelacionado());
            detailEntity.setImporteTotalDocumentoRelacionado(detail.getImporteTotalDocumentoRelacionado());
            detailEntity.setMonedaDocumentoRelacionado(detail.getMonedaDocumentoRelacionado());

            detailEntity.setFechaPagoCobro(detail.getFechaPagoCobro());
            detailEntity.setNumeroPagoCobro(detail.getNumeroPagoCobro());
            detailEntity.setImportePagoSinRetencionCobro(detail.getImportePagoSinRetencionCobro());
            detailEntity.setMonedaPagoCobro(detail.getMonedaPagoCobro());

            detailEntity.setImporteRetenidoPercibido(detail.getImporteRetenidoPercibido());
            detailEntity.setMonedaImporteRetenidoPercibido(detail.getMonedaImporteRetenidoPercibido());
            detailEntity.setFechaRetencionPercepcion(detail.getFechaRetencionPercepcion());
            detailEntity.setImporteTotalToPagarCobrar(detail.getImporteTotalToPagarCobrar());
            detailEntity.setMonedaImporteTotalToPagarCobrar(detail.getMonedaImporteTotalToPagarCobrar());

            detailEntity.setMonedaReferenciaTipoCambio(detail.getMonedaReferenciaTipoCambio());
            detailEntity.setMonedaObjetivoTasaCambio(detail.getMonedaObjetivoTasaCambio());
            detailEntity.setTipoCambio(detail.getTipoCambio());
            detailEntity.setFechaCambio(detail.getFechaCambio());

            detailEntity.setEstado(ConstantesParameter.REGISTRO_ACTIVO);

            entity.addDetails(detailEntity);
        }

        if (!isEdit) {
            entity.setFechaRegistro(new Timestamp(fechaActual.getTime()));
        }

        if (userName != null) {
            Optional<User> usuario = userRepository.findByUsername(userName);
            entity.setOficina(usuario.get().getOficina());
        } else {
            Optional<User> usuario = userRepository.findByUsername(null);
            entity.setOficina(usuario.get().getOficina());
        }

        otherCpeRepository.save(entity);

        return entity.getIdOtroCPE();
    }

    private CompanyEntity completarDatosEmisor(OtherDocumentCpe otherDocumentoCpe) {

        CompanyEntity company = companyRepository.findByRuc(otherDocumentoCpe.getNumeroDocumentoIdentidadEmisor());
        otherDocumentoCpe.setDenominacionEmisor(company.getRazonSocial());
        otherDocumentoCpe.setTipoDocumentoIdentidadEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        otherDocumentoCpe.setNombreComercialEmisor(company.getNombreComercial());
        otherDocumentoCpe.setDireccionCompletaDomicilioFiscalEmisor(company.getDireccion());

        return company;
    }

    private CompanyEntity completarDatosRemitente(GuiaRemision guia) {

        String numeroIdentidadRemitente = guia.getNumeroDocumentoIdentidadRemitente();
        CompanyEntity company = companyRepository.findByRuc(numeroIdentidadRemitente);
        guia.setDenominacionRemitente(company.getRazonSocial());
        guia.setTipoDocumentoIdentidadRemitente(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        if (StringUtils.isNotBlank(guia.getSerieGuiaBaja()) && guia.getNumeroGuiaBaja() != null && guia.getNumeroGuiaBaja() > 0) {
            guia.setTipoComprobanteBaja(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION);
            guia.setDescripcionComprobanteBaja(ConstantesParameter.GUIA_REMISION);
        }
        return company;
    }

    private RegisterFileUploadEntity uploadXmlOthersVouchers(
            CompanyEntity companyEntity,
            String nameDocument,
            String tipoComprobante,
            String estadoRegistro,
            String fileXMLZipBase64,
            String authorization) throws Exception {

        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "othercps", companyEntity);
        return file;

    }

    @Override
    public Map<String, Object> generationGuiaRemision(GuiaRemision guiaRemision, String authorization, Boolean isEdit,
                                                      String userName) {

        Map<String, Object> result = new HashMap<>();
        ResponsePSE response = new ResponsePSE();
        Map<String, String> templateGenerated;
        String estadoRegistro;
        String messageResponse;
        String fileXMLZipBase64;
        String nameDocument;
        Long idGuiaRemisionSaved;
        String estadoEnSunat;
        SendOtherDocumentDTO dataSendOtherDocumentCPE;
        GuiaRemisionEntity guiaRemisionOld = null;

        try {

            formatDecimalGuiaRemision(guiaRemision);
            guiaRemisionValidate.validateGuiaRemision(guiaRemision, isEdit);
            Logger.register(TipoLogEnum.INFO, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.VALIDATE_FIELDS, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            if (isEdit) {
                messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
                guiaRemisionOld = guiaRemisionRepository.findByNumeroDocumentoIdentidadRemitenteAndSerieAndNumero(guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getSerie(),guiaRemision.getNumero());

                if (guiaRemisionOld == null)
                    throw new ServiceException("Esta guia que desea editar, no existe en la base de datos del PSE");

                    if (!guiaRemisionOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo())
                            || guiaRemisionOld.getEstadoEnSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                            || guiaRemisionOld.getEstadoEnSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
                    ) {
                        throw new ServiceException("Esta guia no se puede editar, ya fue declarado a Sunat.");
                    }

            } else {
                messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
            }

            CompanyEntity companyEntity = completarDatosRemitente(guiaRemision);
            estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;

            templateGenerated = templateService.buildGuiaRemisionSign(guiaRemision);
            Logger.register(TipoLogEnum.INFO, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.GENERATE_TEMPLATE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);
            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);

            RegisterFileUploadEntity responseStorage = uploadXmlOthersVouchers(
                    companyEntity,
                    nameDocument,
                    guiaRemision.getTipoComprobante(),
                    ConstantesParameter.REGISTRO_STATUS_NUEVO,
                    fileXMLZipBase64,
                    authorization);
            for (CampoAdicionalGuia cam: guiaRemision.getCamposAdicionales() ) {
                cam.getValorCampo().replace("?","");
            }
            Logger.register(TipoLogEnum.INFO, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.STORAGE_FILE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            idGuiaRemisionSaved = registrarGuiaRemision(
                    guiaRemision,
                    responseStorage.getIdRegisterFileSend(),
                    isEdit,
                    guiaRemisionOld,
                    userName,
                    estadoRegistro,
                    estadoEnSunat,
                    messageResponse);
            Logger.register(TipoLogEnum.INFO, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.INSERT_BD, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            dataSendOtherDocumentCPE = new SendOtherDocumentDTO();
            dataSendOtherDocumentCPE.setIdVoucher(idGuiaRemisionSaved);
            dataSendOtherDocumentCPE.setNameDocument(nameDocument);
            dataSendOtherDocumentCPE.setUuidSaved(UUIDGen.generate());
            dataSendOtherDocumentCPE.setRuc(guiaRemision.getNumeroDocumentoIdentidadRemitente());
            dataSendOtherDocumentCPE.setTipoComprobante(guiaRemision.getTipoComprobante());
            dataSendOtherDocumentCPE.setEnvioAutomaticoSunat(companyEntity.getEnvioAutomaticoSunat() == null ? true : companyEntity.getEnvioAutomaticoSunat());
            result.put(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE, dataSendOtherDocumentCPE);

            setUrlsToResponse(response, guiaRemisionRepository.findByIdGuiaRemision(idGuiaRemisionSaved));
            response.setEstado(true);
            response.setMensaje(messageResponse);
            response.setNombre(nameDocument);

        } catch (ValidatorFieldsException e) {

            response.setEstado(false);
            response.setMensaje(e.getMensajeValidacion());

            Logger.register(TipoLogEnum.WARNING, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.VALIDATE_FIELDS, e.getMensajeValidacion());
        } catch (TemplateException | SignedException e) {

            response.setEstado(false);
            response.setMensaje(e.getMessage());

            Logger.register(TipoLogEnum.ERROR, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    e.getMessage(), guiaRemision.toString(), e);

        } catch (Exception e) {
            response.setEstado(false);
            response.setMensaje(e.getMessage());

            Logger.register(TipoLogEnum.ERROR, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.IN_PROCESS,
                    e.getMessage(), guiaRemision.toString(), e);

        }
        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        Logger.register(TipoLogEnum.INFO, guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getIdentificadorDocumento(),
                OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.COMPLETED, result.toString());

        return result;
    }

    @Transactional
    private Long registrarGuiaRemision(GuiaRemision guiaRemision, Long idRegisterFile,Boolean isEdit,GuiaRemisionEntity guiaRemisionOld, String userName, String estadoRegistro,
                                       String estadoEnSunat, String messageResponse) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        CompanyEntity company = companyRepository.findByRuc(guiaRemision.getIdentificadorDocumento().substring(0,10));
        Date fechaActual = Calendar.getInstance().getTime();
        GuiaRemisionEntity entity = new GuiaRemisionEntity();
        String identificadorDocumento;
        identificadorDocumento = guiaRemision.getNumeroDocumentoIdentidadRemitente() + "-"
                + guiaRemision.getTipoComprobante() + "-"
                + guiaRemision.getSerie() + "-"
                + guiaRemision.getNumero();

        if (isEdit) {

            List<DetailGuiaRemisionEntity> items = guiaRemisionOld.getDetailsGuiaRemision();
            List<AditionalFieldGuiaEntity> adicionales = guiaRemisionOld.getAditionalFields();
            List<TramoTrasladoEntity> tramos = guiaRemisionOld.getTramos();

            if (items != null && !items.isEmpty()) {

                for (DetailGuiaRemisionEntity item : items) {

                    detailGuiaRepository.deleteDetailsGuiaRemision(item.getIdDetailGuiaRemision());
                }
            }

            if (adicionales != null && !adicionales.isEmpty()) {

                for (AditionalFieldGuiaEntity adicional : adicionales) {
                    aditionalFieldGuiaRepository.deleteAditionakField(adicional.getId());
                }
            }

            if (tramos != null && !tramos.isEmpty()) {

                for (TramoTrasladoEntity tramo : tramos) {
                    tramoTrasladoRepository.deleteTramoGuiaRemision(tramo.getIdTramoGuiaRemision());
                }
            }


            entity.setIdGuiaRemision(guiaRemisionOld.getIdGuiaRemision());
            entity.setUuid(guiaRemisionOld.getUuid());
            entity.setGuiaRemisionFiles(guiaRemisionOld.getGuiaRemisionFiles());
            entity.setFechaRegistro(guiaRemisionOld.getFechaRegistro());
        }

        entity.setSerie(guiaRemision.getSerie());
        entity.setNumero(guiaRemision.getNumero());
        entity.setFechaEmision(guiaRemision.getFechaEmision());
        entity.setFechaEmisionDate(formatter.parse(guiaRemision.getFechaEmision()));
        entity.setHoraEmision(guiaRemision.getHoraEmision());
//		entity.setObservaciones(guiaRemision.getObservaciones());
        entity.setTipoComprobante(guiaRemision.getTipoComprobante());

        entity.setSerieBaja(guiaRemision.getSerieGuiaBaja());
        entity.setNumeroBaja(guiaRemision.getNumeroGuiaBaja());
        entity.setTipoComprobanteBaja(guiaRemision.getTipoComprobanteBaja());
        entity.setDescripcionComprobanteBaja(guiaRemision.getDescripcionComprobanteBaja());

        entity.setNumeracionDAM(guiaRemision.getNumeracionDAM());
        entity.setNumeracionManifiestoCarga(guiaRemision.getNumeracionManifiestoCarga());
        entity.setIdentificadorDocumentoRelacionado(guiaRemision.getIdentificadorDocumentoRelacionado());
        entity.setCodigoTipoDocumentoRelacionado(guiaRemision.getCodigoTipoDocumentoRelacionado());

        entity.setNumeroDocumentoIdentidadRemitente(guiaRemision.getNumeroDocumentoIdentidadRemitente());
        entity.setTipoDocumentoIdentidadRemitente(guiaRemision.getTipoDocumentoIdentidadRemitente());
        entity.setDenominacionRemitente(guiaRemision.getDenominacionRemitente());

        entity.setNumeroDocumentoIdentidadDestinatario(guiaRemision.getNumeroDocumentoIdentidadDestinatario());
        entity.setTipoDocumentoIdentidadDestinatario(guiaRemision.getTipoDocumentoIdentidadDestinatario());
        entity.setDenominacionDestinatario(guiaRemision.getDenominacionDestinatario());

        entity.setNumeroDocumentoIdentidadProveedor(guiaRemision.getNumeroDocumentoIdentidadProveedor());
        entity.setTipoDocumentoIdentidadProveedor(guiaRemision.getTipoDocumentoIdentidadProveedor());
        entity.setDenominacionProveedor(guiaRemision.getDenominacionProveedor());

        entity.setMotivoTraslado(guiaRemision.getMotivoTraslado());
        entity.setDescripcionMotivoTraslado(guiaRemision.getDescripcionMotivoTraslado());
        entity.setIndicadorTransbordoProgramado(guiaRemision.getIndicadorTransbordoProgramado());
        entity.setPesoTotalBrutoBienes(guiaRemision.getPesoTotalBrutoBienes());
        entity.setUnidadMedidaPesoBruto(guiaRemision.getUnidadMedidaPesoBruto());
        entity.setNumeroBultos(guiaRemision.getNumeroBultos());

        entity.setUbigeoPuntoLlegada(guiaRemision.getUbigeoPuntoLlegada());
        entity.setDireccionPuntoLlegada(guiaRemision.getDireccionPuntoLlegada());
        entity.setDireccionPuntoPartida(guiaRemision.getDireccionPuntoPartida());
        entity.setUbigeoPuntoPartida(guiaRemision.getUbigeoPuntoPartida());

        entity.setCodigoPuerto(guiaRemision.getCodigoPuerto());
        entity.setNumeroContenedor(guiaRemision.getNumeroContenedor());

        /*----------------------------------------------------------------------*/
        entity.setTotalValorVentaOperacionExportada(guiaRemision.getTotalValorVentaExportacion());
        entity.setTotalValorVentaOperacionGravada(guiaRemision.getTotalValorVentaGravada());
        entity.setTotalValorVentaOperacionInafecta(guiaRemision.getTotalValorVentaInafecta());
        entity.setTotalValorVentaOperacionExonerada(guiaRemision.getTotalValorVentaExonerada());
        entity.setTotalValorVentaOperacionGratuita(guiaRemision.getTotalValorVentaGratuita());
        entity.setTotalValorVentaGravadaIVAP(guiaRemision.getTotalValorVentaGravadaIVAP());
        entity.setTotalValorBaseIsc(guiaRemision.getTotalValorBaseIsc());
        entity.setTotalValorBaseOtrosTributos(guiaRemision.getTotalValorBaseOtrosTributos());
        entity.setTotalDescuento(guiaRemision.getTotalDescuento());

        entity.setMontoDescuentoGlobal(guiaRemision.getDescuentoGlobales());
        entity.setMontoSumatorioOtrosCargos(guiaRemision.getSumatoriaOtrosCargos());
        entity.setMontoImporteTotalVenta(guiaRemision.getImporteTotalVenta());
        entity.setMontoTotalAnticipos(guiaRemision.getTotalAnticipos());

        entity.setSumatoriaIGV(guiaRemision.getTotalIgv());
        entity.setSumatoriaISC(guiaRemision.getTotalIsc());
        entity.setSumatoriaTributosOperacionGratuita(guiaRemision.getTotalImpOperGratuita());
        entity.setSumatoriaOtrosTributos(guiaRemision.getTotalOtrostributos());
        entity.setSumatoriaIvap(guiaRemision.getTotalIvap());
        /*------------------------------------------------------------------------*/

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            entity.addFile(GuiaRemisionFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }

        for (TramoTraslado tramo : guiaRemision.getTramosTraslados()) {
            TramoTrasladoEntity tramoEntity = new TramoTrasladoEntity();
            tramoEntity.setCorrelativoTramo(tramo.getCorrelativoTramo());
            tramoEntity.setModalidadTraslado(tramo.getModalidadTraslado());
            tramoEntity.setFechaInicioTraslado(tramo.getFechaInicioTraslado());
            tramoEntity.setNumeroDocumentoIdentidadTransportista(tramo.getNumeroDocumentoIdentidadTransportista());
            tramoEntity.setTipoDocumentoIdentidadTransportista(tramo.getTipoDocumentoIdentidadTransportista());
            tramoEntity.setDenominacionTransportista(tramo.getDenominacionTransportista());
            tramoEntity.setNumeroPlacaVehiculo(tramo.getNumeroPlacaVehiculo());
            tramoEntity.setNumeroDocumentoIdentidadConductor(tramo.getNumeroDocumentoIdentidadConductor());
            tramoEntity.setTipoDocumentoIdentidadConductor(tramo.getTipoDocumentoIdentidadConductor());
            tramoEntity.setEstado(ConstantesParameter.REGISTRO_ACTIVO);

            entity.addTramos(tramoEntity);
        }
        for (GuiaItem item : guiaRemision.getBienesToTransportar()) {

            DetailGuiaRemisionEntity detailEntity = new DetailGuiaRemisionEntity();
            detailEntity.setNumeroOrden(item.getNumeroOrden());
            detailEntity.setCantidad(item.getCantidad());
            detailEntity.setUnidadMedida(item.getUnidadMedida());
            detailEntity.setDescripcion(item.getDescripcion());
            detailEntity.setCodigoItem(item.getCodigoItem());
            detailEntity.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detailEntity.setPrecioItem(item.getPrecioItem());

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

            entity.addDetailsGuiaRemision(detailEntity);
        }

        if (guiaRemision.getCamposAdicionales() != null && !guiaRemision.getCamposAdicionales().isEmpty()) {

            for (CampoAdicionalGuia campoAdicionalGuia : guiaRemision.getCamposAdicionales()) {

                AditionalFieldGuiaEntity aditionalFieldGuiaEntity = new AditionalFieldGuiaEntity();
                TypeFieldEntity typeField = typeFieldRepository.findByName(campoAdicionalGuia.getNombreCampo());
                if (typeField != null)
                    aditionalFieldGuiaEntity.setTypeField(typeField);
                else {
                    typeField = new TypeFieldEntity();
                    typeField.setName(campoAdicionalGuia.getNombreCampo());
                    typeField.setCompanys(new ArrayList<>());
                    typeField.getCompanys().add(company);
                    typeField = typeFieldRepository.save(typeField);
                    aditionalFieldGuiaEntity.setTypeField(typeField);

                }
                aditionalFieldGuiaEntity.setNombreCampo(campoAdicionalGuia.getNombreCampo());
                aditionalFieldGuiaEntity.setValorCampo(campoAdicionalGuia.getValorCampo());

                entity.addAditionalField(aditionalFieldGuiaEntity);
            }
        }

        /*for (String obs : guiaRemision.getObservaciones()) {

            GuiaRemisionObservacionEntity observacionGuia = new GuiaRemisionObservacionEntity();
            observacionGuia.setObservacion(obs);
            observacionGuia.setEstado(ConstantesParameter.REGISTRO_ACTIVO);

            entity.addObservaciones(observacionGuia);

        }*/

        entity.setIdentificadorDocumento(identificadorDocumento);
        entity.setEstado(estadoRegistro);
        entity.setEstadoAnterior(estadoRegistro);
        entity.setEstadoEnSunat(estadoEnSunat);
        entity.setMensajeRespuesta(messageResponse);
        if (!isEdit) {
            entity.setFechaRegistro(new Timestamp(fechaActual.getTime()));
        }
        entity.setUserName(userName);

        if (userName != null) {
            Optional<User> usuario = userRepository.findByUsername(userName);
            entity.setOficina(usuario.get().getOficina());
        } else {
            Optional<User> usuario = userRepository.findByUsername(null);
            entity.setOficina(usuario.get().getOficina());
        }

        guiaRemisionRepository.save(entity);

        return entity.getIdGuiaRemision();
    }

    public void formatDecimalGuiaRemision(GuiaRemision guiaRemision) {
        System.out.println("GuiaRemision: "+guiaRemision);
        System.out.println("GuiaRemision peso total bruto bienes: "+guiaRemision.getPesoTotalBrutoBienes());
        if (guiaRemision.getPesoTotalBrutoBienes() != null)
            guiaRemision.setPesoTotalBrutoBienes(guiaRemision.getPesoTotalBrutoBienes().setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println("guia remision bienes to transportar: "+guiaRemision.getBienesToTransportar());
        /*guiaRemision.getBienesToTransportar().forEach(line -> {
            if (line.getCantidad() != null)
                line.setCantidad(line.getCantidad().setScale(2, BigDecimal.ROUND_HALF_UP));
        });*/
    }

    private void formatDecimalRetencionPercepcion(OtherDocumentCpe otherDocumentCPE) {

        if (otherDocumentCPE.getTasa() != null)
            otherDocumentCPE.setTasa(otherDocumentCPE.getTasa().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (otherDocumentCPE.getImporteTotalRetenidoPercibido() != null)
            otherDocumentCPE.setImporteTotalRetenidoPercibido(otherDocumentCPE.getImporteTotalRetenidoPercibido().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (otherDocumentCPE.getImporteTotalPagadoCobrado() != null)
            otherDocumentCPE.setImporteTotalPagadoCobrado(otherDocumentCPE.getImporteTotalPagadoCobrado().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (otherDocumentCPE.getMontoRedondeoImporteTotal() != null)
            otherDocumentCPE.setMontoRedondeoImporteTotal(otherDocumentCPE.getMontoRedondeoImporteTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

        otherDocumentCPE.getDocumentosRelacionados().forEach(line -> {
            if (line.getImporteTotalDocumentoRelacionado() != null)
                line.setImporteTotalDocumentoRelacionado(line.getImporteTotalDocumentoRelacionado().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getImportePagoSinRetencionCobro() != null)
                line.setImportePagoSinRetencionCobro(line.getImportePagoSinRetencionCobro().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getImporteRetenidoPercibido() != null)
                line.setImporteRetenidoPercibido(line.getImporteRetenidoPercibido().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getImporteTotalToPagarCobrar() != null)
                line.setImporteTotalToPagarCobrar(line.getImporteTotalToPagarCobrar().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getTipoCambio() != null)
                line.setTipoCambio(line.getTipoCambio().setScale(6, BigDecimal.ROUND_HALF_UP));
        });
    }

    private void setUrlsToResponse(ResponsePSE response, GuiaRemisionEntity guiaRemisionEntity) {

        if (guiaRemisionEntity != null) {

            String urlA4 = urlServiceDownload + "descargapdfuuidguia/" + guiaRemisionEntity.getIdGuiaRemision() + "/" + guiaRemisionEntity.getUuid() + "/a4/" + guiaRemisionEntity.getIdentificadorDocumento();
            String urlXml = urlServiceDownload + "descargaxmluuidguia/" + guiaRemisionEntity.getIdGuiaRemision() + "/" + guiaRemisionEntity.getUuid() + "/" + guiaRemisionEntity.getIdentificadorDocumento();
            response.setUrlPdfA4(urlA4);
            response.setUrlXml(urlXml);
        }
    }


}

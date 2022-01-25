package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.util.UUIDGen;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * The persistent class for the payment_voucher database table.
 */
@Entity
@Table(name = "payment_voucher", indexes = {@Index(name = "payment_ident_doc_idx", columnList = "identificador_documento")})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComprobantesEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "comprobante_pago_seq", sequenceName = "comprobante_pago_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "comprobante_pago_seq")
    @Column(name = "id_payment_voucher")
    private Long id_payment_voucher;

    @Column(name = "serie", length = 4, nullable = false)
    private String serie;
    @Column(name = "numero", nullable = false)
    private Integer numero;
    @Column(name = "fecha_emision", length = 10, nullable = false)
    private String fecha_emision;

    @JsonSerialize(using = UnixTimestampDateSerializer.class)
    @Column(name = "fecha_emision_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha_emision_date;


    @Column(name = "hora_emision", length = 8)
    private String hora_emision;
    @Column(name = "tipo_comprobante", length = 2, nullable = false)
    private String tipo_comprobante;
    @Column(name = "codigo_moneda", length = 3, nullable = false)
    private String codigo_moneda;
    @Column(name = "fecha_vencimiento", length = 10)
    private String fecha_vencimiento;
    @Column(name = "tipo_operacion", length = 4)
    private String tipo_operacion;

    @Column(name = "ruc_emisor", length = 11, nullable = false)
    private String ruc_emisor;
    @Column(name = "cod_local_anexo", length = 4)
    private String cod_local_anexo;

    @Column(name = "tip_doc_ident_receptor", length = 1)
    private String tip_doc_ident_receptor;
    @Column(name = "num_doc_ident_receptor", length = 15)
    private String num_doc_ident_receptor;
    @Column(name = "denominacion_receptor", length = 100)
    private String denominacion_receptor;

    @Column(name = "direccion_receptor", length = 400)
    private String direccion_receptor;

    @Column(name = "email_receptor", length = 400)
    private String email_receptor;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AditionalFieldEntity> aditionalFields = new ArrayList<>();

    @Column(name = "cod_tip_doc_ref", length = 2)
    private String cod_tip_doc_ref;
    @Column(name = "serie_numero_referencia", length = 30)
    private String serie_numero_referencia;

    @Column(name = "total_oper_exportada", precision = 35, scale = 20)
    private BigDecimal total_oper_exportada;
    @Column(name = "total_oper_gravada", precision = 35, scale = 20)
    private BigDecimal total_oper_gravada;
    @Column(name = "total_oper_inafecta", precision = 35, scale = 20)
    private BigDecimal total_oper_inafecta;
    @Column(name = "total_oper_exonerada", precision = 35, scale = 20)
    private BigDecimal total_oper_exonerada;
    @Column(name = "total_oper_gratuita", precision = 35, scale = 20)
    private BigDecimal total_oper_gratuita;
    @Column(name = "total_base_isc", precision = 35, scale = 20)
    private BigDecimal total_base_isc;
    @Column(name = "total_base_otros_trib", precision = 35, scale = 20)
    private BigDecimal total_base_otros_trib;
    @Column(name = "total_oper_ivap", precision = 35, scale = 20)
    private BigDecimal total_oper_ivap;
    @Column(name = "total_descuento", precision = 35, scale = 20)
    private BigDecimal total_descuento;

    @Column(name = "sum_trib_grat", precision = 35, scale = 20)
    private BigDecimal sum_trib_grat;
    @Column(name = "sum_ivap", precision = 35, scale = 20)
    private BigDecimal sum_ivap;
    @Column(name = "sumatoria_igv", precision = 35, scale = 20)
    private BigDecimal sumatoria_igv;
    @Column(name = "sumatoria_isc", precision = 35, scale = 20)
    private BigDecimal sumatoria_isc;
    @Column(name = "sumatoria_otros_trib", precision = 35, scale = 20)
    private BigDecimal sumatoria_otros_trib;

    @Column(name = "monto_descuento_global", precision = 35, scale = 20)
    private BigDecimal monto_descuento_global;
    @Column(name = "monto_sum_otros_carg", precision = 35, scale = 20)
    private BigDecimal monto_sum_otros_carg;
    @Column(name = "monto_imp_total_venta", precision = 35, scale = 20)
    private BigDecimal monto_imp_total_venta;
    @Column(name = "monto_total_anticipos", precision = 35, scale = 20)
    private BigDecimal monto_total_anticipos;

    //Usado para Nota Credito/Debito
    @Column(name = "serie_afectado", length = 4)
    private String serie_afectado;
    @Column(name = "numero_afectado")
    private Integer numero_afectado;
    @Column(name = "tip_comprob_afectado", length = 2)
    private String tip_comprob_afectado;
    @Column(name = "motivo_nota", length = 500)
    private String motivo_nota;
    @Column(name = "cod_tip_nota_cred", length = 2)
    private String cod_tip_nota_cred;
    @Column(name = "cod_tip_nota_debit", length = 2)
    private String cod_tip_nota_debit;

    @Column(name = "identificador_documento", length = 28, nullable = false, unique = true)
    private String identificador_documento;

    @Column(name = "estado_item")
    private Integer estado_item;
    @Column(name = "estado_sunat", length = 5)
    private String estado_sunat;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "estado_anterior", length = 2)
    private String estado_anterior;
    @Column(name = "mensaje_respuesta", length = 1500)
    private String mensaje_respuesta;
    @Column(name = "fecha_registro", nullable = false)
    private Timestamp fecha_registro;
    @Column(name = "fecha_modificacion")
    private Timestamp fecha_modificacion;
    @Column(name = "user_name", length = 80)
    private String user_name;
    @Column(name = "user_name_modify", length = 80)
    private String user_name_modify;

    @Column(name = "motivo_anulacion", length = 100)
    private String motivo_anulacion;


    @OneToMany(mappedBy = "id_payment_voucher", cascade = CascadeType.ALL)
    private List<DetallesComprobantesEntity> detailsPaymentVouchers;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<AnticipoEntity> anticipos;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<GuiaRelacionadaEntity> guiasRelacionadas;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "orden_compra")
    private String orden_compra;


    @Column(name = "estado_anticipo")
    private Integer estado_anticipo;


    @Column(name = "codigo_respuesta_sunat", length = 80)
    private String codigo_respuesta_sunat;

    @Column(name = "ubl_version", length = 4)
    private String ubl_version;

    @Column(name = "boleta_anulada_sin_emitir")
    private Boolean boleta_anulada_sin_emitir;

    @JsonIgnore
    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<PaymentVoucherFileEntity> paymentVoucherFiles = new ArrayList<>();

    @Column(name = "codigo_hash", length = 100)
    private String codigo_hash;

    @PrePersist
    private void prePersist() {
        this.uuid = UUIDGen.generate();
    }

    @Column(name = "codigo_medio_pago", length = 3)
    private String codigo_medio_pago;

    @Column(name = "cuenta_financiera_beneficiario", length = 100)
    private String cuenta_financiera_beneficiario;

    @Column(name = "codigo_bien_detraccion", length = 3)
    private String codigo_bien_detraccion;

    @Column(name = "porcentaje_detraccion")
    private BigDecimal porcentaje_detraccion;

    @Column(name = "monto_detraccion", precision = 35, scale = 20)
    private BigDecimal monto_detraccion;

    @Column(name = "detraccion")
    private String detraccion;

   /* @Column(name = "tipo_de_transaccion")
    private int tipo_de_transaccion;*/



    @JsonIgnore
    @ManyToOne
    private BranchOfficeEntity oficina;


    @Transient
    private String identificadorBaja;

/*
    public void addFile(PaymentVoucherFileEntity file) {
        file.setOrden(this.getFiles().size() + 1);

        //SI SE AGREGA UN XML VERIFICO SI YA EXISTE ALGUNO, PARA CAMBIARLE EL ESTADO (CASO DE EDICIONES DE COMPROBANTES)
        if (file.getTipoArchivo().equals(TipoArchivoEnum.XML)) {
            getFiles().forEach(f -> {
                if (f.getTipoArchivo().equals(TipoArchivoEnum.XML)) {
                    f.setEstadoArchivo(EstadoArchivoEnum.INACTIVO);
                }
            });
        }
        getFiles().add(file);
        file.setPaymentVoucher(this);
    }*/

    @JsonIgnore
    public List<PaymentVoucherFileEntity> getFiles() {
        if (this.paymentVoucherFiles == null) {
            this.paymentVoucherFiles = new ArrayList<PaymentVoucherFileEntity>();
        }
        return this.paymentVoucherFiles;
    }

    @JsonIgnore
    public RegisterFileUploadEntity getXmlActivo() {
        Optional<PaymentVoucherFileEntity> resp = this.getFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XML) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

    @JsonIgnore
    public RegisterFileUploadEntity getCdrActivo() {
        Optional<PaymentVoucherFileEntity> resp = this.getFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.CDR) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }


   /* public AnticipoEntity addAnticipo(AnticipoEntity anticipo) {
        getAnticipos().add(anticipo);
        anticipo.setPaymentVoucher(this);

        return anticipo;
    }

    public GuiaRelacionadaEntity addGuiaRelacionada(GuiaRelacionadaEntity guiaRelacionada) {
        getGuiasRelacionadas().add(guiaRelacionada);
        guiaRelacionada.setPaymentVoucher(this);

        return guiaRelacionada;
    }*/

    public DetallesComprobantesEntity addDetailsPaymentVoucher(DetallesComprobantesEntity detailsPaymentVoucher) {
        getDetailsPaymentVouchers().add(detailsPaymentVoucher);
        detailsPaymentVoucher.setPaymentVoucher(this);

        return detailsPaymentVoucher;
    }


    public List<DetallesComprobantesEntity> getDetailsPaymentVouchers() {
        if (this.detailsPaymentVouchers == null) {
            this.detailsPaymentVouchers = new ArrayList<DetallesComprobantesEntity>();
        }
        return this.detailsPaymentVouchers;
    }/*



    public List<AnticipoEntity> getAnticipos() {
        if (this.anticipos == null) {
            this.anticipos = new ArrayList<AnticipoEntity>();
        }
        return this.anticipos;
    }

    public List<GuiaRelacionadaEntity> getGuiasRelacionadas() {
        if (this.guiasRelacionadas == null) {
            this.guiasRelacionadas = new ArrayList<GuiaRelacionadaEntity>();
        }
        return this.guiasRelacionadas;
    }
/*
    public void addAditionalField(AditionalFieldEntity aditionalFieldEntity) {
        this.aditionalFields.add(aditionalFieldEntity);
        aditionalFieldEntity.setPaymentVoucher(this);
    }
*/
    @Override
    public String toString() {

        StringBuilder atributos = new StringBuilder();
        atributos.append("[PaymentVoucherEntity]").
                append(", idPaymentVoucher: ").append(id_payment_voucher).
                append(", serie: ").append(serie).
                append(", numero: ").append(numero).
                append(", fechaEmision: ").append(fecha_emision).
                append(", tipoComprobante: ").append(tipo_comprobante).
                append(", codigoMoneda: ").append(codigo_moneda).
                append(", fechaVencimiento: ").append(fecha_vencimiento).
                append(", tipoOperacion: ").append(tipo_operacion).
                append(", rucEmisor: ").append(ruc_emisor).
                append(", codigoLocalAnexo: ").append(cod_local_anexo).
                append(", tipoDocIdentReceptor: ").append(tip_doc_ident_receptor).
                append(", numDocIdentReceptor: ").append(num_doc_ident_receptor).
                append(", denominacionReceptor: ").append(denominacion_receptor).
                append(", direccionReceptor: ").append(direccion_receptor).
                append(", emailReceptor: ").append(email_receptor).
                append(", codigoTipoDocumentoRelacionado: ").append(cod_tip_doc_ref).
                append(", serieNumeroDocumentoRelacionado: ").append(serie_numero_referencia).
                append(", totalValorVentaOperacionExportada: ").append(total_oper_exportada).
                append(", totalValorVentaOperacionGravada: ").append(total_oper_gravada).
                append(", totalValorVentaOperacionInafecta: ").append(total_oper_inafecta).
                append(", totalValorVentaOperacionExonerada: ").append(total_oper_exonerada).
                append(", totalValorVentaOperacionGratuita: ").append(total_oper_gratuita).
                append(", totalDescuento: ").append(total_descuento).
                append(", sumatoriaIGV: ").append(sumatoria_igv).
                append(", sumatoriaISC: ").append(sumatoria_isc).
                append(", sumatoriaOtrosTributos: ").append(sumatoria_otros_trib).
                append(", montoDescuentoGlobal: ").append(monto_descuento_global).
                append(", montoSumatorioOtrosCargos: ").append(monto_sum_otros_carg).
                append(", montoImporteTotalVenta: ").append(monto_imp_total_venta).
                append(", montoTotalAnticipos: ").append(monto_total_anticipos).
                append(", serieAfectado: ").append(serie_afectado).
                append(", numeroAfectado: ").append(numero_afectado).
                append(", tipoComprobanteAfectado: ").append(tip_comprob_afectado).
                append(", motivoNota: ").append(motivo_nota).
                append(", codigoTipoNotaCredito: ").append(cod_tip_nota_cred).
                append(", codigoTipoNotaDebito: ").append(cod_tip_nota_debit).
                append(", identificadorDocumento: ").append(identificador_documento).
                append(", estadoItem: ").append(estado_item).
                append(", estadoSunat: ").append(estado_sunat).
                append(", estado: ").append(estado).
                append(", mensajeRespuesta: ").append(mensaje_respuesta).
                append(", fechaModificacion: ").append(fecha_modificacion).
                append(", fechaRegistro: ").append(fecha_registro).
                append(", userName: ").append(user_name).
                append(", ordenCompra: ").append(orden_compra).
                append(", codigosRespuestaSunat: ").append(codigo_respuesta_sunat).
                append(", motivoAnulacion: ").append(motivo_anulacion).
                append(", userNameModify: ").append(user_name_modify).
                append(", codigoMedioPago: ").append(codigo_medio_pago).
                append(", cuentaFinancieraBeneficiario: ").append(cuenta_financiera_beneficiario).
                append(", codigoBien: ").append(codigo_bien_detraccion).
                append(", porcentajeDetraccion: ").append(porcentaje_detraccion).
                append(", montoDetraccion: ").append(monto_detraccion).
                append(", detraccion: ").append(detraccion).
                append(", idDetailsPayment: ").append(id_details_payment).
                append(", cantidad: ").append(cantidad).
                append(", codigoUnidadMedida: ").append(cod_unid_medida).
                append(", descripcion: ").append(descripcion_producto).
                append(", codigoProductoSunat: ").append(cod_prod_sunat).
                append(", codigoProducto: ").append(cod_producto).
                append(", codigoProductoGS1: ").append(cod_producto_gs1).
                append(", valorUnitario: ").append(valor_unit).
                append(", precioVentaUnitario: ").append(precio_venta_unit).
                append(", valorReferencialUnitario: ").append(valor_referencial_unit).
                append(", montoBaseIgv: ").append(monto_base_igv).
                append(", montoBaseIvap: ").append(monto_base_ivap).
                append(", montoBaseExportacion: ").append(monto_base_export).
                append(", montoBaseExonerado: ").append(monto_base_exone).
                append(", montoBaseInafecto: ").append(monto_base_inafec).
                append(", montoBaseGratuito: ").append(monto_base_grat).
                append(", montoBaseIsc: ").append(monto_base_isc).
                append(", montoBaseOtrosTributos: ").append(monto_base_otros).
                append(", tributoVentaGratuita: ").append(monto_trib_vta_grat).
                append(", otrosTributos: ").append(monto_otros_trib).
                append(", ivap: ").append(monto_ivap).
                append(", montoIcbper: ").append(monto_icbper).
                append(", montoBaseIcbper: ").append(monto_base_icbper).
                append(", afectacionIGV: ").append(afectacion_igv).
                append(", sistemaISC: ").append(sistema_isc).
                append(", porcentajeIgv: ").append(porcent_igv).
                append(", porcentajeIvap: ").append(porcent_ivap).
                append(", porcentajeIsc: ").append(porcent_isc).
                append(", porcentajeOtrosTributos: ").append(porcent_otr_trib).
                append(", porcentajeTributoVentaGratuita: ").append(porcent_vta_grat).
                append(", codigoTipoSistemaISC: ").append(cod_tip_sistema_isc).
                append(", codigoTipoAfectacionIGV: ").append(cod_tip_afectacion_igv).
                append(", valorVenta: ").append(valor_venta).
                append(", descuento: ").append(descuento).
                append(", codigoDescuento: ").append(codigo_dscto).
                append(", estado: ").append(estado).
                append(", paymentVoucher: ").append(id_payment_voucher).
                append(", detalleViajeDetraccion: ").append(detalleViajeDetraccion).
                append(", ubigeoOrigenDetraccion: ").append(ubigeoOrigenDetraccion).
                append(", direccionOrigenDetraccion: ").append(direccionOrigenDetraccion).
                append(", ubigeoDestinoDetraccion: ").append(ubigeoDestinoDetraccion).
                append(", direccionDestinoDetraccion: ").append(direccionDestinoDetraccion).
                append(", valorServicioTransporte: ").append(valorServicioTransporte).
                append(", valorCargaEfectiva: ").append(valorCargaEfectiva).
                append(", valorCargaUtil: ").append(valorCargaUtil).
                append(", hidroMatricula: ").append(hidro_matricula).
                append(", hidroEmbarcacion: ").append(hidro_embarcacion).
                append(", hidroDescripcionTipo: ").append(hidro_descripcion_tipo).
                append(", hidroLugarDescarga: ").append(hidro_lugar_descarga).
                append(", hidroFechaDescarga: ").append(hidro_fecha_descarga).
                append(", hidroCantidad: ").append(hidro_cantidad);


        return atributos.toString();
    }


    public Long getIdPaymentVoucher() {
        return id_payment_voucher;
    }

    public void setIdPaymentVoucher(Long idPaymentVoucher) {
        this.id_payment_voucher = idPaymentVoucher;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getFechaEmision() {
        return fecha_emision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fecha_emision = fechaEmision;
    }

    public Date getFechaEmisionDate() {
        return fecha_emision_date;
    }

    public void setFechaEmisionDate(Date fechaEmisionDate) {
        this.fecha_emision_date = fechaEmisionDate;
    }

    public String getHoraEmision() {
        return hora_emision;
    }

    public void setHoraEmision(String horaEmision) {
        this.hora_emision = horaEmision;
    }

    public String getTipoComprobante() {
        return tipo_comprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipo_comprobante = tipoComprobante;
    }

    public String getCodigoMoneda() {
        return codigo_moneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigo_moneda = codigoMoneda;
    }

    public String getFechaVencimiento() {
        return fecha_vencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fecha_vencimiento = fechaVencimiento;
    }

    public String getTipoOperacion() {
        return tipo_operacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipo_operacion = tipoOperacion;
    }

    public String getRucEmisor() {
        return ruc_emisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.ruc_emisor = rucEmisor;
    }

    public String getCodigoLocalAnexo() {
        return cod_local_anexo;
    }

    public void setCodigoLocalAnexo(String codigoLocalAnexo) {
        this.cod_local_anexo = codigoLocalAnexo;
    }

    public String getTipoDocIdentReceptor() {
        return tip_doc_ident_receptor;
    }

    public void setTipoDocIdentReceptor(String tipoDocIdentReceptor) {
        this.tip_doc_ident_receptor = tipoDocIdentReceptor;
    }

    public String getNumDocIdentReceptor() {
        return num_doc_ident_receptor;
    }

    public void setNumDocIdentReceptor(String numDocIdentReceptor) {
        this.num_doc_ident_receptor = numDocIdentReceptor;
    }

    public String getDenominacionReceptor() {
        return denominacion_receptor;
    }

    public void setDenominacionReceptor(String denominacionReceptor) {
        this.denominacion_receptor = denominacionReceptor;
    }

    public String getDireccionReceptor() {
        return direccion_receptor;
    }

    public void setDireccionReceptor(String direccionReceptor) {
        this.direccion_receptor = direccionReceptor;
    }

    public String getEmailReceptor() {
        return email_receptor;
    }

    public void setEmailReceptor(String emailReceptor) {
        this.email_receptor = emailReceptor;
    }

    public List<AditionalFieldEntity> getAditionalFields() {
        return aditionalFields;
    }

    public void setAditionalFields(List<AditionalFieldEntity> aditionalFields) {
        this.aditionalFields = aditionalFields;
    }

    public String getCodigoTipoDocumentoRelacionado() {
        return cod_tip_doc_ref;
    }

    public void setCodigoTipoDocumentoRelacionado(String codigoTipoDocumentoRelacionado) {
        this.cod_tip_doc_ref = codigoTipoDocumentoRelacionado;
    }

    public String getSerieNumeroDocumentoRelacionado() {
        return serie_numero_referencia;
    }

    public void setSerieNumeroDocumentoRelacionado(String serieNumeroDocumentoRelacionado) {
        this.serie_numero_referencia = serieNumeroDocumentoRelacionado;
    }

    public BigDecimal getTotalValorVentaOperacionExportada() {
        return total_oper_exportada;
    }

    public void setTotalValorVentaOperacionExportada(BigDecimal totalValorVentaOperacionExportada) {
        this.total_oper_exportada = totalValorVentaOperacionExportada;
    }

    public BigDecimal getTotalValorVentaOperacionGravada() {
        return total_oper_gravada;
    }

    public void setTotalValorVentaOperacionGravada(BigDecimal totalValorVentaOperacionGravada) {
        this.total_oper_gravada = totalValorVentaOperacionGravada;
    }

    public BigDecimal getTotalValorVentaOperacionInafecta() {
        return total_oper_inafecta;
    }

    public void setTotalValorVentaOperacionInafecta(BigDecimal totalValorVentaOperacionInafecta) {
        this.total_oper_inafecta = totalValorVentaOperacionInafecta;
    }

    public BigDecimal getTotalValorVentaOperacionExonerada() {
        return total_oper_exonerada;
    }

    public void setTotalValorVentaOperacionExonerada(BigDecimal totalValorVentaOperacionExonerada) {
        this.total_oper_exonerada = totalValorVentaOperacionExonerada;
    }

    public BigDecimal getTotalValorVentaOperacionGratuita() {
        return total_oper_gratuita;
    }

    public void setTotalValorVentaOperacionGratuita(BigDecimal totalValorVentaOperacionGratuita) {
        this.total_oper_gratuita = totalValorVentaOperacionGratuita;
    }

    public BigDecimal getTotalValorBaseIsc() {
        return total_base_isc;
    }

    public void setTotalValorBaseIsc(BigDecimal totalValorBaseIsc) {
        this.total_base_isc = totalValorBaseIsc;
    }

    public BigDecimal getTotalValorBaseOtrosTributos() {
        return total_base_otros_trib;
    }

    public void setTotalValorBaseOtrosTributos(BigDecimal totalValorBaseOtrosTributos) {
        this.total_base_otros_trib = totalValorBaseOtrosTributos;
    }

    public BigDecimal getTotalValorVentaGravadaIVAP() {
        return total_oper_ivap;
    }

    public void setTotalValorVentaGravadaIVAP(BigDecimal totalValorVentaGravadaIVAP) {
        this.total_oper_ivap = totalValorVentaGravadaIVAP;
    }

    public BigDecimal getTotalDescuento() {
        return total_descuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.total_descuento = totalDescuento;
    }

    public BigDecimal getSumatoriaTributosOperacionGratuita() {
        return sum_trib_grat;
    }

    public void setSumatoriaTributosOperacionGratuita(BigDecimal sumatoriaTributosOperacionGratuita) {
        this.sum_trib_grat = sumatoriaTributosOperacionGratuita;
    }

    public BigDecimal getSumatoriaIvap() {
        return sum_ivap;
    }

    public void setSumatoriaIvap(BigDecimal sumatoriaIvap) {
        this.sum_ivap = sumatoriaIvap;
    }

    public BigDecimal getSumatoriaIGV() {
        return sumatoria_igv;
    }

    public void setSumatoriaIGV(BigDecimal sumatoriaIGV) {
        this.sumatoria_igv = sumatoriaIGV;
    }

    public BigDecimal getSumatoriaISC() {
        return sumatoria_isc;
    }

    public void setSumatoriaISC(BigDecimal sumatoriaISC) {
        this.sumatoria_isc = sumatoriaISC;
    }

    public BigDecimal getSumatoriaOtrosTributos() {
        return sumatoria_otros_trib;
    }

    public void setSumatoriaOtrosTributos(BigDecimal sumatoriaOtrosTributos) {
        this.sumatoria_otros_trib = sumatoriaOtrosTributos;
    }

    public BigDecimal getMontoDescuentoGlobal() {
        return monto_descuento_global;
    }

    public void setMontoDescuentoGlobal(BigDecimal montoDescuentoGlobal) {
        this.monto_descuento_global = montoDescuentoGlobal;
    }

    public BigDecimal getMontoSumatorioOtrosCargos() {
        return monto_sum_otros_carg;
    }

    public void setMontoSumatorioOtrosCargos(BigDecimal montoSumatorioOtrosCargos) {
        this.monto_sum_otros_carg = montoSumatorioOtrosCargos;
    }

    public BigDecimal getMontoImporteTotalVenta() {
        return monto_imp_total_venta;
    }

    public void setMontoImporteTotalVenta(BigDecimal montoImporteTotalVenta) {
        this.monto_imp_total_venta = montoImporteTotalVenta;
    }

    public BigDecimal getMontoTotalAnticipos() {
        return monto_total_anticipos;
    }

    public void setMontoTotalAnticipos(BigDecimal montoTotalAnticipos) {
        this.monto_total_anticipos = montoTotalAnticipos;
    }

    public String getSerieAfectado() {
        return serie_afectado;
    }

    public void setSerieAfectado(String serieAfectado) {
        this.serie_afectado = serieAfectado;
    }

    public Integer getNumeroAfectado() {
        return numero_afectado;
    }

    public void setNumeroAfectado(Integer numeroAfectado) {
        this.numero_afectado = numeroAfectado;
    }

    public String getTipoComprobanteAfectado() {
        return tip_comprob_afectado;
    }

    public void setTipoComprobanteAfectado(String tipoComprobanteAfectado) {
        this.tip_comprob_afectado = tipoComprobanteAfectado;
    }

    public String getMotivoNota() {
        return motivo_nota;
    }

    public void setMotivoNota(String motivoNota) {
        this.motivo_nota = motivoNota;
    }

    public String getCodigoTipoNotaCredito() {
        return cod_tip_nota_cred;
    }

    public void setCodigoTipoNotaCredito(String codigoTipoNotaCredito) {
        this.cod_tip_nota_cred = codigoTipoNotaCredito;
    }

    public String getCodigoTipoNotaDebito() {
        return cod_tip_nota_debit;
    }

    public void setCodigoTipoNotaDebito(String codigoTipoNotaDebito) {
        this.cod_tip_nota_debit = codigoTipoNotaDebito;
    }

    public String getIdentificadorDocumento() {
        return identificador_documento;
    }

    public void setIdentificadorDocumento(String identificadorDocumento) {
        this.identificador_documento = identificadorDocumento;
    }

    public Integer getEstadoItem() {
        return estado_item;
    }

    public void setEstadoItem(Integer estadoItem) {
        this.estado_item = estadoItem;
    }

    public String getEstadoSunat() {
        return estado_sunat;
    }

    public void setEstadoSunat(String estadoSunat) {
        this.estado_sunat = estadoSunat;
    }


    public String getEstadoAnterior() {
        return estado_anterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estado_anterior = estadoAnterior;
    }

    public String getMensajeRespuesta() {
        return mensaje_respuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensaje_respuesta = mensajeRespuesta;
    }

    public Timestamp getFechaRegistro() {
        return fecha_registro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fecha_registro = fechaRegistro;
    }

    public Timestamp getFechaModificacion() {
        return fecha_modificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fecha_modificacion = fechaModificacion;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String userName) {
        this.user_name = userName;
    }

    public String getUserNameModify() {
        return user_name_modify;
    }

    public void setUserNameModify(String userNameModify) {
        this.user_name_modify = userNameModify;
    }

    public String getMotivoAnulacion() {
        return motivo_anulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivo_anulacion = motivoAnulacion;
    }

    public void setDetailsPaymentVouchers(List<DetallesComprobantesEntity> detailsPaymentVouchers) {
        this.detailsPaymentVouchers = detailsPaymentVouchers;
    }

    public void setAnticipos(List<AnticipoEntity> anticipos) {
        this.anticipos = anticipos;
    }

    public void setGuiasRelacionadas(List<GuiaRelacionadaEntity> guiasRelacionadas) {
        this.guiasRelacionadas = guiasRelacionadas;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrdenCompra() {
        return orden_compra;
    }

    public void setOrdenCompra(String ordenCompra) {
        this.orden_compra = ordenCompra;
    }

    public Integer getEstadoAnticipo() {
        return estado_anticipo;
    }

    public void setEstadoAnticipo(Integer estadoAnticipo) {
        this.estado_anticipo = estadoAnticipo;
    }

    public String getCodigosRespuestaSunat() {
        return codigo_respuesta_sunat;
    }

    public void setCodigosRespuestaSunat(String codigosRespuestaSunat) {
        this.codigo_respuesta_sunat = codigosRespuestaSunat;
    }

    public String getUblVersion() {
        return ubl_version;
    }

    public void setUblVersion(String ublVersion) {
        this.ubl_version = ublVersion;
    }

    public Boolean getBoletaAnuladaSinEmitir() {
        return boleta_anulada_sin_emitir;
    }

    public void setBoletaAnuladaSinEmitir(Boolean boletaAnuladaSinEmitir) {
        this.boleta_anulada_sin_emitir = boletaAnuladaSinEmitir;
    }

    public List<PaymentVoucherFileEntity> getPaymentVoucherFiles() {
        return paymentVoucherFiles;
    }

    public void setPaymentVoucherFiles(List<PaymentVoucherFileEntity> paymentVoucherFiles) {
        this.paymentVoucherFiles = paymentVoucherFiles;
    }

    public String getCodigoHash() {
        return codigo_hash;
    }

    public void setCodigoHash(String codigoHash) {
        this.codigo_hash = codigoHash;
    }

    public String getCodigoMedioPago() {
        return codigo_medio_pago;
    }

    public void setCodigoMedioPago(String codigoMedioPago) {
        this.codigo_medio_pago = codigoMedioPago;
    }

    public String getCuentaFinancieraBeneficiario() {
        return cuenta_financiera_beneficiario;
    }

    public void setCuentaFinancieraBeneficiario(String cuentaFinancieraBeneficiario) {
        this.cuenta_financiera_beneficiario = cuentaFinancieraBeneficiario;
    }

    public String getCodigoBienDetraccion() {
        return codigo_bien_detraccion;
    }

    public void setCodigoBienDetraccion(String codigoBienDetraccion) {
        this.codigo_bien_detraccion = codigoBienDetraccion;
    }

    public BigDecimal getPorcentajeDetraccion() {
        return porcentaje_detraccion;
    }

    public void setPorcentajeDetraccion(BigDecimal porcentajeDetraccion) {
        this.porcentaje_detraccion = porcentajeDetraccion;
    }

    public BigDecimal getMontoDetraccion() {
        return monto_detraccion;
    }

    public void setMontoDetraccion(BigDecimal montoDetraccion) {
        this.monto_detraccion = montoDetraccion;
    }

    public String getDetraccion() {
        return detraccion;
    }

    public void setDetraccion(String detraccion) {
        this.detraccion = detraccion;
    }

    public BranchOfficeEntity getOficina() {
        return oficina;
    }

    public void setOficina(BranchOfficeEntity oficina) {
        this.oficina = oficina;
    }

    public String getIdentificadorBaja() {
        return identificadorBaja;
    }

    public void setIdentificadorBaja(String identificadorBaja) {
        this.identificadorBaja = identificadorBaja;
    }

    @Column(name="id_details_payment")
    private Long id_details_payment;

    @Column(name="cantidad", precision=12, scale=2)
    private BigDecimal cantidad;
    @Column(name="cod_unid_medida", length=3)
    private String cod_unid_medida;

    @Column(name="descripcion_producto", length=500)
    private String descripcion_producto;
    @Column(name="cod_prod_sunat", length=20)
    private String cod_prod_sunat;
    @Column(name="cod_producto", length=30)
    private String cod_producto;
    @Column(name="cod_producto_gs1", length=20)
    private String cod_producto_gs1;

    @Column(name="valor_unit", precision=35, scale=20)
    private BigDecimal valor_unit;

    @Column(name="precio_venta_unit", precision=35, scale=20)
    private BigDecimal precio_venta_unit;
    @Column(name="valor_referencial_unit", precision=35, scale=20)
    private BigDecimal valor_referencial_unit;

    @Column(name="monto_base_igv", precision=35, scale=20)
    private BigDecimal monto_base_igv;
    @Column(name="monto_base_ivap", precision=35, scale=20)
    private BigDecimal monto_base_ivap;
    @Column(name="monto_base_export", precision=35, scale=20)
    private BigDecimal monto_base_export;
    @Column(name="monto_base_exone", precision=35, scale=20)
    private BigDecimal monto_base_exone;
    @Column(name="monto_base_inafec", precision=35, scale=20)
    private BigDecimal monto_base_inafec;
    @Column(name="monto_base_grat", precision=35, scale=20)
    private BigDecimal monto_base_grat;
    @Column(name="monto_base_isc", precision=35, scale=20)
    private BigDecimal monto_base_isc;
    @Column(name="monto_base_otros", precision=35, scale=20)
    private BigDecimal monto_base_otros;

    @Column(name="monto_trib_vta_grat", precision=35, scale=20)
    private BigDecimal monto_trib_vta_grat;
    @Column(name="monto_otros_trib", precision=35, scale=20)
    private BigDecimal monto_otros_trib;
    @Column(name="monto_ivap", precision=35, scale=20)
    private BigDecimal monto_ivap;


    @Column(name="monto_icbper", precision=35, scale=20)
    private BigDecimal monto_icbper;

    @Column(name="monto_base_icbper", precision=35, scale=20)
    private BigDecimal monto_base_icbper;

    //hace referencia al tributo 1000
    @Column(name="afectacion_igv", precision=35, scale=20)
    private BigDecimal afectacion_igv;
    //hace referencia al tributo 2000
    @Column(name="sistema_isc", precision=35, scale=20)
    private BigDecimal sistema_isc;

    @Column(name="porcent_igv", precision=8, scale=3)
    private BigDecimal porcent_igv;
    @Column(name="porcent_ivap", precision=8, scale=3)
    private BigDecimal porcent_ivap;
    @Column(name="porcent_isc", precision=8, scale=3)
    private BigDecimal porcent_isc;
    @Column(name="porcent_otr_trib", precision=8, scale=3)
    private BigDecimal porcent_otr_trib;
    @Column(name="porcent_vta_grat", precision=8, scale=3)
    private BigDecimal porcent_vta_grat;

    @Column(name="cod_tip_sistema_isc", length=19)
    private String cod_tip_sistema_isc;
    @Column(name="cod_tip_afectacion_igv", length=19)
    private String cod_tip_afectacion_igv;

    @Column(name="valor_venta", precision=35, scale=20)
    private BigDecimal valor_venta;
    @Column(name="descuento", precision=35, scale=20)
    private BigDecimal descuento;
    @Column(name="codigo_dscto", length=3)
    private String codigo_dscto;

    //DETRACCION 027
    @Column
    private String detalleViajeDetraccion;
    @Column
    private String ubigeoOrigenDetraccion;
    @Column
    private String direccionOrigenDetraccion;
    @Column
    private String ubigeoDestinoDetraccion;
    @Column
    private String direccionDestinoDetraccion;
    @Column
    private BigDecimal valorServicioTransporte;
    @Column
    private BigDecimal valorCargaEfectiva;
    @Column
    private BigDecimal valorCargaUtil;

    //CAMPOS HIDROBIOLOGICOS

    @Column(name = "hidro_matricula")
    private String hidro_matricula;

    @Column(name = "hidro_embarcacion")
    private String hidro_embarcacion;

    @Column(name = "hidro_descripcion_tipo")
    private String hidro_descripcion_tipo;

    @Column(name = "hidro_lugar_descarga")
    private String hidro_lugar_descarga;

    @Column(name = "hidro_fecha_descarga")
    private String hidro_fecha_descarga;

    @Column(name = "hidro_cantidad")
    private String hidro_cantidad;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getIdDetailsPayment() {
        return id_details_payment;
    }

    public void setIdDetailsPayment(Long idDetailsPayment) {
        this.id_details_payment = idDetailsPayment;
    }


    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoUnidadMedida() {
        return cod_unid_medida;
    }

    public void setCodigoUnidadMedida(String codigoUnidadMedida) {
        this.cod_unid_medida = codigoUnidadMedida;
    }

    public String getDescripcion() {
        return descripcion_producto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion_producto = descripcion;
    }

    public String getCodigoProductoSunat() {
        return cod_prod_sunat;
    }

    public void setCodigoProductoSunat(String codigoProductoSunat) {
        this.cod_prod_sunat = codigoProductoSunat;
    }

    public String getCodigoProducto() {
        return cod_producto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.cod_producto = codigoProducto;
    }

    public String getCodigoProductoGS1() {
        return cod_producto_gs1;
    }

    public void setCodigoProductoGS1(String codigoProductoGS1) {
        this.cod_producto_gs1 = codigoProductoGS1;
    }

    public BigDecimal getValorUnitario() {
        return valor_unit;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valor_unit = valorUnitario;
    }

    public BigDecimal getPrecioVentaUnitario() {
        return precio_venta_unit;
    }

    public void setPrecioVentaUnitario(BigDecimal precioVentaUnitario) {
        this.precio_venta_unit = precioVentaUnitario;
    }

    public BigDecimal getValorReferencialUnitario() {
        return valor_referencial_unit;
    }

    public void setValorReferencialUnitario(BigDecimal valorReferencialUnitario) {
        this.valor_referencial_unit = valorReferencialUnitario;
    }

    public BigDecimal getMontoBaseIgv() {
        return monto_base_igv;
    }

    public void setMontoBaseIgv(BigDecimal montoBaseIgv) {
        this.monto_base_igv = montoBaseIgv;
    }

    public BigDecimal getMontoBaseIvap() {
        return monto_base_ivap;
    }

    public void setMontoBaseIvap(BigDecimal montoBaseIvap) {
        this.monto_base_ivap = montoBaseIvap;
    }

    public BigDecimal getMontoBaseExportacion() {
        return monto_base_export;
    }

    public void setMontoBaseExportacion(BigDecimal montoBaseExportacion) {
        this.monto_base_export = montoBaseExportacion;
    }

    public BigDecimal getMontoBaseExonerado() {
        return monto_base_exone;
    }

    public void setMontoBaseExonerado(BigDecimal montoBaseExonerado) {
        this.monto_base_exone = montoBaseExonerado;
    }

    public BigDecimal getMontoBaseInafecto() {
        return monto_base_inafec;
    }

    public void setMontoBaseInafecto(BigDecimal montoBaseInafecto) {
        this.monto_base_inafec = montoBaseInafecto;
    }

    public BigDecimal getMontoBaseGratuito() {
        return monto_base_grat;
    }

    public void setMontoBaseGratuito(BigDecimal montoBaseGratuito) {
        this.monto_base_grat = montoBaseGratuito;
    }

    public BigDecimal getMontoBaseIsc() {
        return monto_base_isc;
    }

    public void setMontoBaseIsc(BigDecimal montoBaseIsc) {
        this.monto_base_isc = montoBaseIsc;
    }

    public BigDecimal getMontoBaseOtrosTributos() {
        return monto_base_otros;
    }

    public void setMontoBaseOtrosTributos(BigDecimal montoBaseOtrosTributos) {
        this.monto_base_otros = montoBaseOtrosTributos;
    }

    public BigDecimal getTributoVentaGratuita() {
        return monto_trib_vta_grat;
    }

    public void setTributoVentaGratuita(BigDecimal tributoVentaGratuita) {
        this.monto_trib_vta_grat = tributoVentaGratuita;
    }

    public BigDecimal getOtrosTributos() {
        return monto_otros_trib;
    }

    public void setOtrosTributos(BigDecimal otrosTributos) {
        this.monto_otros_trib = otrosTributos;
    }

    public BigDecimal getIvap() {
        return monto_ivap;
    }

    public void setIvap(BigDecimal ivap) {
        this.monto_ivap = ivap;
    }

    public BigDecimal getMontoIcbper() {
        return monto_icbper;
    }

    public void setMontoIcbper(BigDecimal montoIcbper) {
        this.monto_icbper = montoIcbper;
    }

    public BigDecimal getMontoBaseIcbper() {
        return monto_base_icbper;
    }

    public void setMontoBaseIcbper(BigDecimal montoBaseIcbper) {
        this.monto_base_icbper = montoBaseIcbper;
    }

    public BigDecimal getAfectacionIGV() {
        return afectacion_igv;
    }

    public void setAfectacionIGV(BigDecimal afectacionIGV) {
        this.afectacion_igv = afectacionIGV;
    }

    public BigDecimal getSistemaISC() {
        return sistema_isc;
    }

    public void setSistemaISC(BigDecimal sistemaISC) {
        this.sistema_isc = sistemaISC;
    }

    public BigDecimal getPorcentajeIgv() {
        return porcent_igv;
    }

    public void setPorcentajeIgv(BigDecimal porcentajeIgv) {
        this.porcent_igv = porcentajeIgv;
    }

    public BigDecimal getPorcentajeIvap() {
        return porcent_ivap;
    }

    public void setPorcentajeIvap(BigDecimal porcentajeIvap) {
        this.porcent_ivap = porcentajeIvap;
    }

    public BigDecimal getPorcentajeIsc() {
        return porcent_isc;
    }

    public void setPorcentajeIsc(BigDecimal porcentajeIsc) {
        this.porcent_isc = porcentajeIsc;
    }

    public BigDecimal getPorcentajeOtrosTributos() {
        return porcent_otr_trib;
    }

    public void setPorcentajeOtrosTributos(BigDecimal porcentajeOtrosTributos) {
        this.porcent_otr_trib = porcentajeOtrosTributos;
    }

    public BigDecimal getPorcentajeTributoVentaGratuita() {
        return porcent_vta_grat;
    }

    public void setPorcentajeTributoVentaGratuita(BigDecimal porcentajeTributoVentaGratuita) {
        this.porcent_vta_grat = porcentajeTributoVentaGratuita;
    }

    public String getCodigoTipoSistemaISC() {
        return cod_tip_sistema_isc;
    }

    public void setCodigoTipoSistemaISC(String codigoTipoSistemaISC) {
        this.cod_tip_sistema_isc = codigoTipoSistemaISC;
    }

    public String getCodigoTipoAfectacionIGV() {
        return cod_tip_afectacion_igv;
    }

    public void setCodigoTipoAfectacionIGV(String codigoTipoAfectacionIGV) {
        this.cod_tip_afectacion_igv = codigoTipoAfectacionIGV;
    }

    public BigDecimal getValorVenta() {
        return valor_venta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valor_venta = valorVenta;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getCodigoDescuento() {
        return codigo_dscto;
    }

    public void setCodigoDescuento(String codigoDescuento) {
        this.codigo_dscto = codigoDescuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalleViajeDetraccion() {
        return detalleViajeDetraccion;
    }

    public void setDetalleViajeDetraccion(String detalleViajeDetraccion) {
        this.detalleViajeDetraccion = detalleViajeDetraccion;
    }

    public String getUbigeoOrigenDetraccion() {
        return ubigeoOrigenDetraccion;
    }

    public void setUbigeoOrigenDetraccion(String ubigeoOrigenDetraccion) {
        this.ubigeoOrigenDetraccion = ubigeoOrigenDetraccion;
    }

    public String getDireccionOrigenDetraccion() {
        return direccionOrigenDetraccion;
    }

    public void setDireccionOrigenDetraccion(String direccionOrigenDetraccion) {
        this.direccionOrigenDetraccion = direccionOrigenDetraccion;
    }

    public String getUbigeoDestinoDetraccion() {
        return ubigeoDestinoDetraccion;
    }

    public void setUbigeoDestinoDetraccion(String ubigeoDestinoDetraccion) {
        this.ubigeoDestinoDetraccion = ubigeoDestinoDetraccion;
    }

    public String getDireccionDestinoDetraccion() {
        return direccionDestinoDetraccion;
    }

    public void setDireccionDestinoDetraccion(String direccionDestinoDetraccion) {
        this.direccionDestinoDetraccion = direccionDestinoDetraccion;
    }

    public BigDecimal getValorServicioTransporte() {
        return valorServicioTransporte;
    }

    public void setValorServicioTransporte(BigDecimal valorServicioTransporte) {
        this.valorServicioTransporte = valorServicioTransporte;
    }

    public BigDecimal getValorCargaEfectiva() {
        return valorCargaEfectiva;
    }

    public void setValorCargaEfectiva(BigDecimal valorCargaEfectiva) {
        this.valorCargaEfectiva = valorCargaEfectiva;
    }

    public BigDecimal getValorCargaUtil() {
        return valorCargaUtil;
    }

    public void setValorCargaUtil(BigDecimal valorCargaUtil) {
        this.valorCargaUtil = valorCargaUtil;
    }

    public String getHidroMatricula() {
        return hidro_matricula;
    }

    public void setHidroMatricula(String hidroMatricula) {
        this.hidro_matricula = hidroMatricula;
    }

    public String getHidroEmbarcacion() {
        return hidro_embarcacion;
    }

    public void setHidroEmbarcacion(String hidroEmbarcacion) {
        this.hidro_embarcacion = hidroEmbarcacion;
    }

    public String getHidroDescripcionTipo() {
        return hidro_descripcion_tipo;
    }

    public void setHidroDescripcionTipo(String hidroDescripcionTipo) {
        this.hidro_descripcion_tipo = hidroDescripcionTipo;
    }

    public String getHidroLugarDescarga() {
        return hidro_lugar_descarga;
    }

    public void setHidroLugarDescarga(String hidroLugarDescarga) {
        this.hidro_lugar_descarga = hidroLugarDescarga;
    }

    public String getHidroFechaDescarga() {
        return hidro_lugar_descarga;
    }

    public void setHidroFechaDescarga(String hidroFechaDescarga) {
        this.hidro_fecha_descarga = hidroFechaDescarga;
    }

    public String getHidroCantidad() {
        return hidro_cantidad;
    }

    public void setHidroCantidad(String hidroCantidad) {
        this.hidro_cantidad = hidroCantidad;
    }
}

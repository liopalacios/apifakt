package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
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
@Builder
public class PaymentVoucherEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "comprobante_pago_seq", sequenceName = "comprobante_pago_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "comprobante_pago_seq")
    @Column(name = "id_payment_voucher")
    private Long idPaymentVoucher;

    @Column(name = "serie", length = 4, nullable = false)
    private String serie;
    @Column(name = "numero", nullable = false)
    private Integer numero;
    @Column(name = "fecha_emision", length = 10, nullable = false)
    private String fechaEmision;

    @JsonSerialize(using = UnixTimestampDateSerializer.class)
    @Column(name = "fecha_emision_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaEmisionDate;


    @Column(name = "hora_emision", length = 8)
    private String horaEmision;
    @Column(name = "tipo_comprobante", length = 2, nullable = false)
    private String tipoComprobante;
    @Column(name = "codigo_moneda", length = 3, nullable = false)
    private String codigoMoneda;
    @Column(name = "fecha_vencimiento", length = 10)
    private String fechaVencimiento;
    @Column(name = "tipo_operacion", length = 4)
    private String tipoOperacion;

    @Column(name = "ruc_emisor", length = 11, nullable = false)
    private String rucEmisor;
    @Column(name = "cod_local_anexo", length = 4)
    private String codigoLocalAnexo;

    @Column(name = "tip_doc_ident_receptor", length = 1)
    private String tipoDocIdentReceptor;
    @Column(name = "num_doc_ident_receptor", length = 15)
    private String numDocIdentReceptor;
    @Column(name = "denominacion_receptor", length = 100)
    private String denominacionReceptor;

    @Column(name = "direccion_receptor", length = 400)
    private String direccionReceptor;

    @Column(name = "email_receptor  ", length = 400)
    private String emailReceptor;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<AditionalFieldEntity> aditionalFields = new ArrayList<>();

    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<CuotasPaymentVoucherEntity> cuotas = new ArrayList<>();

    @Column(name = "cod_tip_doc_ref", length = 2)
    private String codigoTipoDocumentoRelacionado;
    @Column(name = "serie_numero_referencia", length = 30)
    private String serieNumeroDocumentoRelacionado;

    @Column(name = "total_oper_exportada", precision = 35, scale = 20)
    private BigDecimal totalValorVentaOperacionExportada;
    @Column(name = "total_oper_gravada", precision = 35, scale = 20)
    private BigDecimal totalValorVentaOperacionGravada;
    @Column(name = "total_oper_inafecta", precision = 35, scale = 20)
    private BigDecimal totalValorVentaOperacionInafecta;
    @Column(name = "total_oper_exonerada", precision = 35, scale = 20)
    private BigDecimal totalValorVentaOperacionExonerada;
    @Column(name = "total_oper_gratuita", precision = 35, scale = 20)
    private BigDecimal totalValorVentaOperacionGratuita;
    @Column(name = "total_base_isc", precision = 35, scale = 20)
    private BigDecimal totalValorBaseIsc;
    @Column(name = "total_base_otros_trib", precision = 35, scale = 20)
    private BigDecimal totalValorBaseOtrosTributos;
    @Column(name = "total_oper_ivap", precision = 35, scale = 20)
    private BigDecimal totalValorVentaGravadaIVAP;
    @Column(name = "total_descuento", precision = 35, scale = 20)
    private BigDecimal totalDescuento;

    @Column(name = "sum_trib_grat", precision = 35, scale = 20)
    private BigDecimal sumatoriaTributosOperacionGratuita;
    @Column(name = "sum_ivap", precision = 35, scale = 20)
    private BigDecimal sumatoriaIvap;
    @Column(name = "sumatoria_igv", precision = 35, scale = 20)
    private BigDecimal sumatoriaIGV;
    @Column(name = "sumatoria_isc", precision = 35, scale = 20)
    private BigDecimal sumatoriaISC;
    @Column(name = "sumatoria_otros_trib", precision = 35, scale = 20)
    private BigDecimal sumatoriaOtrosTributos;

    @Column(name = "monto_descuento_global", precision = 35, scale = 20)
    private BigDecimal montoDescuentoGlobal;
    @Column(name = "monto_sum_otros_carg", precision = 35, scale = 20)
    private BigDecimal montoSumatorioOtrosCargos;
    @Column(name = "monto_imp_total_venta", precision = 35, scale = 20)
    private BigDecimal montoImporteTotalVenta;
    @Column(name = "monto_total_anticipos", precision = 35, scale = 20)
    private BigDecimal montoTotalAnticipos;

    //Usado para Nota Credito/Debito
    @Column(name = "serie_afectado", length = 4)
    private String serieAfectado;
    @Column(name = "numero_afectado")
    private Integer numeroAfectado;
    @Column(name = "tip_comprob_afectado", length = 2)
    private String tipoComprobanteAfectado;
    @Column(name = "motivo_nota", length = 500)
    private String motivoNota;
    @Column(name = "cod_tip_nota_cred", length = 2)
    private String codigoTipoNotaCredito;
    @Column(name = "cod_tip_nota_debit", length = 2)
    private String codigoTipoNotaDebito;

    @Column(name = "identificador_documento", length = 28, nullable = false, unique = true)
    private String identificadorDocumento;

    @Column(name = "estado_item")
    private Integer estadoItem;
    @Column(name = "estado_sunat", length = 5)
    private String estadoSunat;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "estado_anterior", length = 2)
    private String estadoAnterior;
    @Column(name = "mensaje_respuesta", length = 1500)
    private String mensajeRespuesta;
    @Column(name = "fecha_registro", nullable = false)
    private Timestamp fechaRegistro;
    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;
    @Column(name = "user_name", length = 80)
    private String userName;
    @Column(name = "user_name_modify", length = 80)
    private String userNameModify;

    @Column(name = "motivo_anulacion", length = 100)
    private String motivoAnulacion;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    @OrderBy("numeroItem ASC")
    private List<DetailsPaymentVoucherEntity> detailsPaymentVouchers;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<AnticipoEntity> anticipos;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<GuiaRelacionadaEntity> guiasRelacionadas;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "orden_compra")
    private String ordenCompra;


    @Column(name = "estado_anticipo")
    private Integer estadoAnticipo;


    @Column(name = "codigo_respuesta_sunat", length = 80)
    private String codigosRespuestaSunat;

    @Column(name = "ubl_version", length = 4)
    private String ublVersion;

    @Column(name = "boleta_anulada_sin_emitir")
    private Boolean boletaAnuladaSinEmitir;

    @JsonIgnore
    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
    private List<PaymentVoucherFileEntity> paymentVoucherFiles = new ArrayList<>();

    @Column(name = "codigo_hash", length = 100)
    private String codigoHash;

    @PrePersist
    private void prePersist() {
        this.uuid = UUIDGen.generate();
    }

    @Column(name = "codigo_medio_pago", length = 3)
    private String codigoMedioPago;

    @Column(name = "cuenta_financiera_beneficiario", length = 100)
    private String cuentaFinancieraBeneficiario;

    @Column(name = "codigo_bien_detraccion", length = 3)
    private String codigoBienDetraccion;

    @Column(name = "porcentaje_detraccion")
    private BigDecimal porcentajeDetraccion;

    @Column(name = "monto_detraccion", precision = 35, scale = 20)
    private BigDecimal montoDetraccion;

    @Column(name = "detraccion")
    private String detraccion;



    @Column(name = "tipo_transaccion")
    private BigDecimal tipoTransaccion;

    @Column(name = "monto_pendiente", precision = 35, scale = 20)
    private BigDecimal montoPendiente;

    @Column(name = "cantidad_cuotas")
    private BigDecimal cantidadCuotas;

    @Column(name = "pago_cuenta")
    private BigDecimal pagoCuenta;


/*
   @Column(name = "tipo_transaccion")
   private int tipoTransaccion;

   @Column(name = "monto_pendiente", precision = 35, scale = 20)
   private BigDecimal montoPendiente;

   @Column(name = "monto_cuota", precision = 35, scale = 20)
   private BigDecimal montoCuota;

   @Column(name = "numero_cuota")
   private String numeroCuota;

    @Column(name = "n_cuota")
    private Integer nCuota;

    @Column(name = "id_payment_voucher_reference")
    private String idPaymentVoucherReference;
*/
    @JsonIgnore
    @ManyToOne
    private BranchOfficeEntity oficina;


    @Transient
    private String identificadorBaja;


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
    }

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


    public AnticipoEntity addAnticipo(AnticipoEntity anticipo) {
        getAnticipos().add(anticipo);
        anticipo.setPaymentVoucher(this);

        return anticipo;
    }

    public GuiaRelacionadaEntity addGuiaRelacionada(GuiaRelacionadaEntity guiaRelacionada) {
        getGuiasRelacionadas().add(guiaRelacionada);
        guiaRelacionada.setPaymentVoucher(this);

        return guiaRelacionada;
    }

    public DetailsPaymentVoucherEntity addDetailsPaymentVoucher(DetailsPaymentVoucherEntity detailsPaymentVoucher) {
        getDetailsPaymentVouchers().add(detailsPaymentVoucher);
        detailsPaymentVoucher.setPaymentVoucher(this);

        return detailsPaymentVoucher;
    }


    public List<DetailsPaymentVoucherEntity> getDetailsPaymentVouchers() {
        if (this.detailsPaymentVouchers == null) {
            this.detailsPaymentVouchers = new ArrayList<DetailsPaymentVoucherEntity>();
        }
        return this.detailsPaymentVouchers;
    }

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

    public void addAditionalField(AditionalFieldEntity aditionalFieldEntity) {
        this.aditionalFields.add(aditionalFieldEntity);
        aditionalFieldEntity.setPaymentVoucher(this);
    }

    public void addCuotas(CuotasPaymentVoucherEntity cuota) {
        this.cuotas.add(cuota);
        cuota.setPaymentVoucher(this);
    }

    @Override
    public String toString() {

        StringBuilder atributos = new StringBuilder();
        atributos.append("[PaymentVoucherEntity]").
                append(", idPaymentVoucher: ").append(idPaymentVoucher).
                append(", serie: ").append(serie).
                append(", numero: ").append(numero).
                append(", fechaEmision: ").append(fechaEmision).
                append(", tipoComprobante: ").append(tipoComprobante).
                append(", codigoMoneda: ").append(codigoMoneda).
                append(", fechaVencimiento: ").append(fechaVencimiento).
                append(", tipoOperacion: ").append(tipoOperacion).
                append(", rucEmisor: ").append(rucEmisor).
                append(", codigoLocalAnexo: ").append(codigoLocalAnexo).
                append(", tipoDocIdentReceptor: ").append(tipoDocIdentReceptor).
                append(", numDocIdentReceptor: ").append(numDocIdentReceptor).
                append(", denominacionReceptor: ").append(denominacionReceptor).
                append(", direccionReceptor: ").append(direccionReceptor).
                append(", emailReceptor: ").append(emailReceptor).
                append(", codigoTipoDocumentoRelacionado: ").append(codigoTipoDocumentoRelacionado).
                append(", serieNumeroDocumentoRelacionado: ").append(serieNumeroDocumentoRelacionado).
                append(", totalValorVentaOperacionExportada: ").append(totalValorVentaOperacionExportada).
                append(", totalValorVentaOperacionGravada: ").append(totalValorVentaOperacionGravada).
                append(", totalValorVentaOperacionInafecta: ").append(totalValorVentaOperacionInafecta).
                append(", totalValorVentaOperacionExonerada: ").append(totalValorVentaOperacionExonerada).
                append(", totalValorVentaOperacionGratuita: ").append(totalValorVentaOperacionGratuita).
                append(", totalDescuento: ").append(totalDescuento).
                append(", sumatoriaIGV: ").append(sumatoriaIGV).
                append(", sumatoriaISC: ").append(sumatoriaISC).
                append(", sumatoriaOtrosTributos: ").append(sumatoriaOtrosTributos).
                append(", montoDescuentoGlobal: ").append(montoDescuentoGlobal).
                append(", montoSumatorioOtrosCargos: ").append(montoSumatorioOtrosCargos).
                append(", montoImporteTotalVenta: ").append(montoImporteTotalVenta).
                append(", montoTotalAnticipos: ").append(montoTotalAnticipos).
                append(", serieAfectado: ").append(serieAfectado).
                append(", numeroAfectado: ").append(numeroAfectado).
                append(", tipoComprobanteAfectado: ").append(tipoComprobanteAfectado).
                append(", motivoNota: ").append(motivoNota).
                append(", codigoTipoNotaCredito: ").append(codigoTipoNotaCredito).
                append(", codigoTipoNotaDebito: ").append(codigoTipoNotaDebito).
                append(", identificadorDocumento: ").append(identificadorDocumento).
                append(", estadoItem: ").append(estadoItem).
                append(", estadoSunat: ").append(estadoSunat).
                append(", estado: ").append(estado).
                append(", mensajeRespuesta: ").append(mensajeRespuesta).
                append(", fechaModificacion: ").append(fechaModificacion).
                append(", fechaRegistro: ").append(fechaRegistro).
                append(", userName: ").append(userName).
                append(", ordenCompra: ").append(ordenCompra).
                append(", codigosRespuestaSunat: ").append(codigosRespuestaSunat).
                append(", motivoAnulacion: ").append(motivoAnulacion).
                append(", userNameModify: ").append(userNameModify).
                append(", codigoMedioPago: ").append(codigoMedioPago).
                append(", cuentaFinancieraBeneficiario: ").append(cuentaFinancieraBeneficiario).
                append(", codigoBien: ").append(codigoBienDetraccion).
                append(", porcentajeDetraccion: ").append(porcentajeDetraccion).
                append(", montoDetraccion: ").append(montoDetraccion).
                append(", detraccion: ").append(detraccion);

        return atributos.toString();
    }
}

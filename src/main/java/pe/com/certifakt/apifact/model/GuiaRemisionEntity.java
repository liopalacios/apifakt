package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.util.UUIDGen;

import javax.persistence.*;
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
@Table(name = "guia_remision", indexes = {@Index(name = "guia_ident_doc_idx", columnList = "identificador_documento")})
@Getter
@Setter
public class GuiaRemisionEntity {

    @Id
    @SequenceGenerator(name = "guia_remision_seq", sequenceName = "guia_remision_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "guia_remision_seq")
    @Column(name = "id_guia_remision")
    private Long idGuiaRemision;

    @Column(name = "serie", length = 10, nullable = false)
    private String serie;
    @Column(name = "numero", nullable = false)
    private Integer numero;
    @Column(name = "hora_emision", length = 8)
    private String horaEmision;
    @Column(name = "fecha_emision", length = 20, nullable = false)
    private String fechaEmision;
    @Column(name = "fecha_emision_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date fechaEmisionDate;
    @Column(name = "tipo_comprobante", length = 2, nullable = false)
    private String tipoComprobante;
//	@Column(name="observaciones", length=250)
//	private String observaciones;

    @Column(name = "serie_baja", length = 4)
    private String serieBaja;
    @Column(name = "numero_baja")
    private Integer numeroBaja;
    @Column(name = "tipo_comprobante_baja", length = 2)
    private String tipoComprobanteBaja;
    @Column(name = "descrip_comprobante_baja", length = 100)
    private String descripcionComprobanteBaja;

    @Column(name = "num_dam", length = 20)
    private String numeracionDAM;
    @Column(name = "num_manif_carga", length = 20)
    private String numeracionManifiestoCarga;
    @Column(name = "ident_docum_relac", length = 20)
    private String identificadorDocumentoRelacionado;
    @Column(name = "cod_tipo_docum_relac", length = 2)
    private String codigoTipoDocumentoRelacionado;

    @Column(name = "num_docum_ident_remit", length = 15, nullable = false)
    private String numeroDocumentoIdentidadRemitente;
    @Column(name = "tipo_docum_ident_remit", length = 1)
    private String tipoDocumentoIdentidadRemitente;
    @Column(name = "denominacion_remit", length = 100)
    private String denominacionRemitente;

    @Column(name = "num_docum_ident_destin", length = 15)
    private String numeroDocumentoIdentidadDestinatario;
    @Column(name = "tipo_docum_ident_destin", length = 1)
    private String tipoDocumentoIdentidadDestinatario;
    @Column(name = "denominacion_destin", length = 300)
    private String denominacionDestinatario;

    @Column(name="num_docum_ident_tercero", length=15)
    private String numeroDocumentoIdentidadTercero;
    @Column(name="tipo_docum_ident_tercero", length=1)
    private String tipoDocumentoIdentidadTercero;
    @Column(name="denominacion_tercero", length=100)
    private String denominacionTercero;

    @Column(name = "num_docum_ident_proveed", length = 20)
    private String numeroDocumentoIdentidadProveedor;
    @Column(name = "tipo_docum_ident_proveed", length = 1)
    private String tipoDocumentoIdentidadProveedor;
    @Column(name = "denominacion_proveed", length = 100)
    private String denominacionProveedor;

    /*Columnas de la guia para el manejo del item*/
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


    /**/

    @Column(name = "motivo_traslado", length = 10)
    private String motivoTraslado;
    @Column(name = "descrip_mot_traslado", length = 100)
    private String descripcionMotivoTraslado;
    @Column(name = "indicador_transbordo", nullable = false)
    private Boolean indicadorTransbordoProgramado;
    @Column(name = "peso_total", precision = 12, scale = 3, nullable = false)
    private BigDecimal pesoTotalBrutoBienes;
    @Column(name = "unid_medida_peso", length = 4)
    private String unidadMedidaPesoBruto;
    @Column(name = "numero_bultos")
    private Long numeroBultos;

    @Column(name = "ubigeo_llegada", length = 8)
    private String ubigeoPuntoLlegada;
    @Column(name = "direccion_llegada", length = 200)
    private String direccionPuntoLlegada;

    @Column(name = "num_contenedor", length = 17)
    private String numeroContenedor;

    @Column(name = "ubigeo_partida", length = 8)
    private String ubigeoPuntoPartida;
    @Column(name = "direccion_partida", length = 300)
    private String direccionPuntoPartida;

    @Column(name = "cod_puerto", length = 3)
    private String codigoPuerto;

    @Column(name = "identificador_documento", length = 40, nullable = false, unique = true)
    private String identificadorDocumento;

    @Column(name = "estado_sunat", length = 15)
    private String estadoEnSunat;
    @Column(name = "estado", length = 20)
    private String estado;
    @Column(name = "estado_anterior", length = 20)
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

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL)
    private List<AditionalFieldGuiaEntity> aditionalFields = new ArrayList<>();

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL)
    @OrderBy("numeroOrden ASC")
    private List<DetailGuiaRemisionEntity> detailsGuiaRemision;

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL)
    private List<TramoTrasladoEntity> tramos;

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL)
    private List<GuiaRemisionObservacionEntity> observacionesGuia;

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL)
    private List<GuiaRemisionFileEntity> guiaRemisionFiles = new ArrayList<>();

    @Column(name = "uuid")
    private String uuid;

    @JsonIgnore
    @ManyToOne
    private BranchOfficeEntity oficina;


    public void addFile(GuiaRemisionFileEntity file) {
        if (this.guiaRemisionFiles == null) this.guiaRemisionFiles = new ArrayList<>();
        file.setOrden(this.guiaRemisionFiles.size() + 1);
        this.guiaRemisionFiles.add(file);
        file.setGuiaRemision(this);
    }

    public RegisterFileUploadEntity getXmlActivo() {
        Optional<GuiaRemisionFileEntity> resp = this.getGuiaRemisionFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XML) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

    public RegisterFileUploadEntity getCdrActivo() {
        Optional<GuiaRemisionFileEntity> resp = this.getGuiaRemisionFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.CDR) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

    public DetailGuiaRemisionEntity addDetailsGuiaRemision(DetailGuiaRemisionEntity detailsGuiaRemision) {
        getDetailsGuiaRemision().add(detailsGuiaRemision);
        detailsGuiaRemision.setGuiaRemision(this);

        return detailsGuiaRemision;
    }

    public TramoTrasladoEntity addTramos(TramoTrasladoEntity tramoTraslado) {
        getTramos().add(tramoTraslado);
        tramoTraslado.setGuiaRemision(this);

        return tramoTraslado;
    }

    public GuiaRemisionObservacionEntity addObservaciones(GuiaRemisionObservacionEntity observacion) {
        getObservacionesGuia().add(observacion);
        observacion.setGuiaRemision(this);

        return observacion;
    }

    public List<DetailGuiaRemisionEntity> getDetailsGuiaRemision() {
        if (this.detailsGuiaRemision == null) {
            this.detailsGuiaRemision = new ArrayList<DetailGuiaRemisionEntity>();
        }
        return this.detailsGuiaRemision;
    }

    public List<TramoTrasladoEntity> getTramos() {
        if (this.tramos == null) {
            this.tramos = new ArrayList<TramoTrasladoEntity>();
        }
        return this.tramos;
    }

    public List<GuiaRemisionObservacionEntity> getObservacionesGuia() {
        if (this.observacionesGuia == null) {
            this.observacionesGuia = new ArrayList<GuiaRemisionObservacionEntity>();
        }
        return this.observacionesGuia;
    }

    public void addAditionalField(AditionalFieldGuiaEntity aditionalFieldGuiaEntity) {
        aditionalFieldGuiaEntity.setGuiaRemision(this);
        this.aditionalFields.add(aditionalFieldGuiaEntity);
    }

    @PrePersist
    private void setearUuid() {
        this.uuid = UUIDGen.generate();
    }

}
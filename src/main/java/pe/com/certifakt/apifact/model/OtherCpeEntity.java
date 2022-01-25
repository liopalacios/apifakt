package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "otros_cpe", indexes = {@Index(name = "otro_cpe_ident_doc_idx", columnList = "identificador_documento")})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtherCpeEntity {

    @Id
    @SequenceGenerator(name = "otros_cpe_seq", sequenceName = "otros_cpe_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "otros_cpe_seq")
    @Column(name = "id_otros_cpe")
    private Long idOtroCPE;

    @Column(name = "serie", length = 4, nullable = false)
    private String serie;
    @Column(name = "numero", nullable = false)
    private Integer numero;
    @Column(name = "fecha_emision", length = 20)
    private String fechaEmision;

    @Column(name = "fecha_emision_date")
    private Date fechaEmisionDate;

    @Column(name = "hora_emision", length = 8)
    private String horaEmision;
    @Column(name = "tipo_comprobante", length = 2, nullable = false)
    private String tipoComprobante;

    @Column(name = "num_doc_emisor", length = 20, nullable = false)
    private String numeroDocumentoIdentidadEmisor;
    @Column(name = "tip_doc_emisor", length = 10, nullable = false)
    private String tipoDocumentoIdentidadEmisor;
    @Column(name = "nomb_comerc_emisor", length = 1500)
    private String nombreComercialEmisor;
    @Column(name = "denominacion_emisor", length = 1500, nullable = false)
    private String denominacionEmisor;

    @Column(name = "ubigeo_emisor", length = 10)
    private String ubigeoDomicilioFiscalEmisor;
    @Column(name = "direccion_emisor", length = 500)
    private String direccionCompletaDomicilioFiscalEmisor;
    @Column(name = "urbanizacion_emisor", length = 30)
    private String urbanizacionDomicilioFiscalEmisor;
    @Column(name = "departamento_emisor", length = 30)
    private String departamentoDomicilioFiscalEmisor;
    @Column(name = "provincia_emisor", length = 30)
    private String provinciaDomicilioFiscalEmisor;
    @Column(name = "distrito_emisor", length = 30)
    private String distritoDomicilioFiscalEmisor;
    @Column(name = "cod_pais_emisor", length = 2)
    private String codigoPaisDomicilioFiscalEmisor;

    @Column(name = "num_doc_receptor", length = 11, nullable = false)
    private String numeroDocumentoIdentidadReceptor;
    @Column(name = "tip_doc_receptor", length = 1, nullable = false)
    private String tipoDocumentoIdentidadReceptor;
    @Column(name = "nomb_comerc_receptor", length = 100)
    private String nombreComercialReceptor;
    @Column(name = "denominacion_receptor", length = 100, nullable = false)
    private String denominacionReceptor;

    @Column(name = "ubigeo_receptor", length = 6)
    private String ubigeoDomicilioFiscalReceptor;
    @Column(name = "direccion_receptor", length = 300)
    private String direccionCompletaDomicilioFiscalReceptor;
    @Column(name = "urbanizacion_receptor", length = 30)
    private String urbanizacionDomicilioFiscalReceptor;
    @Column(name = "departamento_receptor", length = 30)
    private String departamentoDomicilioFiscalReceptor;
    @Column(name = "provincia_receptor", length = 30)
    private String provinciaDomicilioFiscalReceptor;
    @Column(name = "distrito_receptor", length = 30)
    private String distritoDomicilioFiscalReceptor;
    @Column(name = "cod_pais_receptor", length = 2)
    private String codigoPaisDomicilioFiscalReceptor;

    @Column(name = "email_receptor", length = 400)
    private String emailReceptor;

    @Column(name = "regimen", length = 2, nullable = false)
    private String regimen;
    @Column(name = "tasa", scale = 2, nullable = false)
    private BigDecimal tasa;
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    @Column(name = "total_retenido_percibido", precision = 12, scale = 2, nullable = false)
    private BigDecimal importeTotalRetenidoPercibido;
    @Column(name = "total_pagado_cobrado", precision = 12, scale = 2, nullable = false)
    private BigDecimal importeTotalPagadoCobrado;
    @Column(name = "monto_redond_import_tot", precision = 12, scale = 2)
    private BigDecimal montoRedondeoImporteTotal;
    @Column(name = "cod_moneda", length = 3, nullable = false)
    private String codigoMoneda;

    @Column(name = "identificador_documento", length = 28, nullable = false)
    private String identificadorDocumento;

    @Column(name = "estado", length = 20)
    private String estado;
    @Column(name = "estado_anterior", length = 20)
    private String estadoAnterior;
    @Column(name = "estado_sunat", length = 10)
    private String estadoEnSunat;
    @Column(name = "mensaje_respuesta", length = 1500)
    private String mensajeRespuesta;
    @Column(name = "fecha_registro", nullable = false)
    private Timestamp fechaRegistro;
    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;
    @Column(name = "user_name", length = 80, nullable = false)
    private String userName;
    @Column(name = "user_name_modify", length = 80)
    private String userNameModify;

    @JsonIgnore
    @ManyToOne
    private BranchOfficeEntity oficina;


    @OneToMany(mappedBy = "otherCpe", cascade = CascadeType.ALL)
    private List<DetailOtherCpeEntity> details;

    @JsonIgnore
    @OneToMany(mappedBy = "otherCpe", cascade = CascadeType.ALL)
    private List<OtherCpeFileEntity> otherCpeFiles = new ArrayList<>();

    @Column(name = "uuid")
    private String uuid;

    @PrePersist
    private void setearUuid() {
        this.uuid = UUIDGen.generate();
    }

    public void addFile(OtherCpeFileEntity file) {
        if (this.otherCpeFiles == null) this.otherCpeFiles = new ArrayList<>();
        file.setOrden(this.otherCpeFiles.size() + 1);
        this.otherCpeFiles.add(file);
        file.setOtherCpe(this);

    }


    public DetailOtherCpeEntity addDetails(DetailOtherCpeEntity detailRetention) {
        getDetails().add(detailRetention);
        detailRetention.setOtherCpe(this);
        return detailRetention;
    }

    public List<DetailOtherCpeEntity> getDetails() {
        if (this.details == null) {
            this.details = new ArrayList<DetailOtherCpeEntity>();
        }
        return this.details;
    }

    @JsonIgnore
    public RegisterFileUploadEntity getXmlActivo() {
        Optional<OtherCpeFileEntity> resp = this.getOtherCpeFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XML) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

    @JsonIgnore
    public RegisterFileUploadEntity getCdrActivo() {
        Optional<OtherCpeFileEntity> resp = this.getOtherCpeFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.CDR) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

}

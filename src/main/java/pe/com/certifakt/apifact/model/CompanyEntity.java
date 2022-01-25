package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The persistent class for the company database table.
 */
@Entity
@Table(name = "company", indexes = {@Index(name = "company_ruc_idx", columnList = "ruc")})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cod_company")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "company_seq")
    private Integer id;

    @Column(name = "bucket", length = 150, nullable = false)
    private String bucket;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @Column(name = "fecha_baja")
    private Timestamp fechaBaja;

    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;

    @Column(name = "pais", length = 90)
    private String pais;

    @Column(name = "razon_social", length = 500, nullable = false)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 500)
    private String nombreComercial;

    @Column(name = "ruc", length = 50, unique = true)
    private String ruc;

    @Column(name = "telefono", length = 100)
    private String telefono;

    @Column(name = "email", length = 200)
    private String email;


    @Column(name = "ncuenta", length = 300)
    private String numeroCuenta;


    //bi-directional many-to-one association to Ubigeo
    @ManyToOne
    @JoinColumn(name = "cod_ubigeo")
    private UbigeoEntity ubigeo;


    //CONFIGURACION
    @Column(name = "envio_automatico_sunat")
    private Boolean envioAutomaticoSunat;

    @Column(name = "envio_directo")
    private Boolean envioDirecto;

    @Column(name="precios_incluido_igv")
    private Boolean preciosIncluidoIgv;

    @Column(name = "ubl_version", length = 10)
    private String UblVersion;


    @ManyToMany(mappedBy = "companys")
    private List<TypeFieldEntity> typeFields;

    @Column(name = "cant_comprobante_dinamico")
    private Integer cantComprobanteDinamico;

    @Column(name = "view_code")
    private Boolean viewCode;

    @Column(name = "format")
    private Integer format;

    @Column(name = "send_ticket")
    private Integer sendticket;

    @Column(name = "view_guia")
    private Boolean viewGuia;

    @Column(name = "trial")
    private Boolean trial;

    @Column(name = "view_otro_comprobante")
    private Boolean viewOtroComprobante;

    @JsonIgnore
    @Column(name="passw", length=150)
    private String osepassword;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="ose_id")
    private OsesEntity ose;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_register_file_send")
    private RegisterFileUploadEntity archivoLogo;

    @ManyToOne
    private UnitCode defaultUnitCode;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "company_unitcode",
            joinColumns = @JoinColumn(name = "cod_company"),
            inverseJoinColumns = @JoinColumn(name = "id_unitcode")
    )
    private List<UnitCode> unitCodes = new ArrayList<>();


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CuentaEntity> cuentas = new ArrayList<>();

    public void addCuenta(CuentaEntity cuenta) {
        this.cuentas.add(cuenta);
        cuenta.setCompany(this);
    }

    public void removeCuenta(CuentaEntity cuenta) {
        this.cuentas.remove(cuenta);
    }


    public void addUnitCode(UnitCode unitCode) {
        unitCodes.add(unitCode);
        unitCode.getCompanyEntities().add(this);
    }

    public void removeUnitCode(UnitCode unitCode) {
        unitCodes.remove(unitCode);
        unitCode.getCompanyEntities().remove(this);
    }

}
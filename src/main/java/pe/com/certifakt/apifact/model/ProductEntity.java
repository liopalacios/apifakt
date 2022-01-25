package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the company database table.
 */
@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "product_seq")
    private Long id;

    @Column
    private String codigo;

    @Column
    private String descripcion;

    @Column
    private String moneda;

    @Column
    private String unidadMedida;

    @Column
    private String tipoAfectacion;

    @Column
    private BigDecimal valorVentaSinIgv;

    @Column
    private BigDecimal valorVentaConIgv;

    @Column
    private String codigoSunat;

    @Column
    private String unidadManejo;

    @Column
    private String instruccionesEspeciales;

    @Column
    private String marca;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "cod_company")
    private CompanyEntity company;

    @Column
    private boolean estado = true;

    @JsonIgnore
    @Column
    private Date createdOn;

    @JsonIgnore
    @Column
    private String createdBy;

    @JsonIgnore
    @Column
    private String updatedBy;

    @JsonIgnore
    @Column
    private Date updatedOn;

    @PrePersist
    void onPrePersist() {
        this.createdOn = new Date();
    }

    @PreUpdate
    void onPreUpdate() {
        this.updatedOn = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getTipoAfectacion() {
        return tipoAfectacion;
    }

    public void setTipoAfectacion(String tipoAfectacion) {
        this.tipoAfectacion = tipoAfectacion;
    }

    public BigDecimal getValorVentaSinIgv() {
        return valorVentaSinIgv;
    }

    public void setValorVentaSinIgv(BigDecimal valorVentaSinIgv) {
        this.valorVentaSinIgv = valorVentaSinIgv;
    }

    public BigDecimal getValorVentaConIgv() {
        return valorVentaConIgv;
    }

    public void setValorVentaConIgv(BigDecimal valorVentaConIgv) {
        this.valorVentaConIgv = valorVentaConIgv;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public boolean isEstado() {
        return estado;
    }
    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCodigoSunat() {
        return codigoSunat;
    }

    public void setCodigoSunat(String codigoSunat) {
        this.codigoSunat = codigoSunat;
    }

    public String getUnidadManejo() {
        return unidadManejo;
    }

    public void setUnidadManejo(String unidadManejo) {
        this.unidadManejo = unidadManejo;
    }

    public String getInstruccionesEspeciales() {
        return instruccionesEspeciales;
    }

    public void setInstruccionesEspeciales(String instruccionesEspeciales) {
        this.instruccionesEspeciales = instruccionesEspeciales;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
}
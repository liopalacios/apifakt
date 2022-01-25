package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the catalog_sunat database table.
 */
@Entity
@Table(name = "catalog_sunat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogSunatEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "catalog_seq", sequenceName = "catalog_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "catalog_seq")
    private Long id;
    private String numero;
    private String codigo;
    private String descripcion;
    private String codigoRelacionado;
    private Integer orden;

    public CatalogSunatEntity(String numero, String codigo, String descripcion, String codigoRelacionado, Integer orden) {
        this.numero = numero;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.codigoRelacionado = codigoRelacionado;
        this.orden = orden;
    }
}
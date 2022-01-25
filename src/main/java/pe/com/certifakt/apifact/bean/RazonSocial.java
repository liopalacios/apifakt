package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazonSocial {
	
	private Boolean success;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String tipo;
    private String nombre_o_razon_social;
    private String estado_del_contribuyente;
    private String condicion_de_domicilio;
    private String ubigeo;
    private String tipo_de_via;
    private String nombre_de_via;
    private String codigo_de_zona;
    private String tipo_de_zona;
    private String numero;
    private String interior;
    private String lote;
    private String dpto;
    private String manzana;
    private String kilometro;
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccion;
    private String direccion_completa;
    private String ultima_actualizacion;
    private String error;
    
}

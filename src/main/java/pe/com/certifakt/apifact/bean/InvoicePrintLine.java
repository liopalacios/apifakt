
package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePrintLine {

	private Integer nume;
	private String codigo;
	private String unidad;
    private String unidadNombre;
    private String descripcion;
    private String codigoSunat;
    private String cantidad;
    private String precioUnitario;
    private String descuento;
    private String total;
    private String precioUnitarioSinIGV;
    private String totalSinIGV;
    private String codetypeigv;
    private String codigoDescripcion;
    private String montoReferencial;
    private String unidadManejo;
    private String instruccionesEspeciales;
    private String marca;

}

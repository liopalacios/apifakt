package pe.com.certifakt.apifact.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
public class Traslado {


	private String traslado_modalidad;
	private String traslado_fecha_inicio;
	private String traslado_transportista_num_doc_ident;
	private String traslado_transportista_tipo_doc_ident;
	private String traslado_transportista_denominacion;
	private String traslado_placa_vehiculo;
    private String traslado_conductor_num_doc_ident;
    private String traslado_conductor_tipo_doc_ident;
	private String brevete;

    
	public Traslado(String traslado_modalidad, String traslado_fecha_inicio,
			String traslado_transportista_num_doc_ident, String traslado_transportista_tipo_doc_ident,
			String traslado_transportista_denominacion, String traslado_placa_vehiculo,
			String traslado_conductor_num_doc_ident, String traslado_conductor_tipo_doc_ident, String brevete) {
		super();
		this.traslado_modalidad = traslado_modalidad;
		this.traslado_fecha_inicio = traslado_fecha_inicio;
		this.traslado_transportista_num_doc_ident = traslado_transportista_num_doc_ident;
		this.traslado_transportista_tipo_doc_ident = traslado_transportista_tipo_doc_ident;
		this.traslado_transportista_denominacion = traslado_transportista_denominacion;
		this.traslado_placa_vehiculo = traslado_placa_vehiculo;
		this.traslado_conductor_num_doc_ident = traslado_conductor_num_doc_ident;
		this.traslado_conductor_tipo_doc_ident = traslado_conductor_tipo_doc_ident;
		this.brevete=brevete;
	}

}

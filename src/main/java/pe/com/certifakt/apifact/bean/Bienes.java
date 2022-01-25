package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.math.BigDecimal;

@Data
public class Bienes {
	
	private BigDecimal bienes_a_transportar_cantidad;
    private String bienes_a_transportar_unidad_medida;
    private String bienes_a_transportar_descripcion;
    private String bienes_a_transportar_codigo_item;
    private String cadena_cantidad;
	private BigDecimal bienes_peso;
	private int index;
	private String bienes_instruccion_especial;
	private String bienes_numero_serie;
    
	public Bienes(BigDecimal bienes_a_transportar_cantidad, String bienes_a_transportar_unidad_medida,
			String bienes_a_transportar_descripcion, String bienes_a_transportar_codigo_item,BigDecimal bienes_peso,
				  int index,String bienes_instruccion_especial,String bienes_numero_serie) {
		super();
		if(bienes_a_transportar_cantidad!=null)
			this.bienes_a_transportar_cantidad = bienes_a_transportar_cantidad.setScale(2,BigDecimal.ROUND_HALF_UP);
		this.bienes_a_transportar_unidad_medida = bienes_a_transportar_unidad_medida;
		this.bienes_a_transportar_descripcion = bienes_a_transportar_descripcion;
		this.bienes_a_transportar_codigo_item = bienes_a_transportar_codigo_item;
		this.bienes_peso=bienes_peso;
		this.index=index;
		this.bienes_instruccion_especial=bienes_instruccion_especial;
		this.bienes_numero_serie=bienes_numero_serie;
	}


}

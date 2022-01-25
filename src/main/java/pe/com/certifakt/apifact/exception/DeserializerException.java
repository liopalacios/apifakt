package pe.com.certifakt.apifact.exception;

public class DeserializerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String codigoRespuesta;
	private String mensaje;
	
	public DeserializerException(String codigoRespuesta, String mensaje) {
		super(mensaje);
		this.codigoRespuesta = codigoRespuesta;
		this.mensaje = mensaje;
	}
	public DeserializerException(String mensaje) {
		super(mensaje);
		this.mensaje = mensaje;
	}
	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}
	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}

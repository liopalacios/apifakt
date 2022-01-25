package pe.com.certifakt.apifact.exception;

public class ValidatorFieldsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mensajeValidacion;

	public ValidatorFieldsException(String mensajeValidacion) {
		super();
		this.mensajeValidacion = mensajeValidacion;
	}

	public String getMensajeValidacion() {
		return mensajeValidacion;
	}

	public void setMensajeValidacion(String mensajeValidacion) {
		this.mensajeValidacion = mensajeValidacion;
	}

}

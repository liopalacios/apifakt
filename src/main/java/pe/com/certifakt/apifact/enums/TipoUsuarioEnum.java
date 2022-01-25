package pe.com.certifakt.apifact.enums;

public enum TipoUsuarioEnum {

	ADMIN("Administrador", "01"),
	USER("Usuario", "02");
	
	private TipoUsuarioEnum(String tipoUsuario, String codigoTipoUsuario) {
		
		this.tipoUsuario = tipoUsuario;
		this.codigoTipoUsuario = codigoTipoUsuario;
	}
	
	private final String tipoUsuario;
	private final String codigoTipoUsuario;
	
	public String getTipoUsuario() {
		return tipoUsuario;
	}
	public String getCodigoTipoUsuario() {
		return codigoTipoUsuario;
	}
}

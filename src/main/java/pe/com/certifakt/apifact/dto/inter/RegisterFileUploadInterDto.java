package pe.com.certifakt.apifact.dto.inter;

public interface RegisterFileUploadInterDto {
    Boolean getIsOld();
    String getBucket();
    String getNombreGenerado();
    String getRucCompany();
    String getUuid();
    String getExtension();
    String getUuidPayment();
    Long getIdPayment();
    String getTipo();
}

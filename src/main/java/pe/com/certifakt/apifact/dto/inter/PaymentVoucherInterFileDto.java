package pe.com.certifakt.apifact.dto.inter;

public interface PaymentVoucherInterFileDto {
    Long getId();
    String getIdentificador();
    String getFechaEmision();
    String getEstado();
    String getEstadoSunat();
    String getEmisor();
}

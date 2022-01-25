package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.model.PaymentVoucherFileEntity;

public interface PaymentVoucherFileRepository extends JpaRepository<PaymentVoucherFileEntity, Long> {

    PaymentVoucherFileEntity findFirst1ByPaymentVoucher_IdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idPayment, TipoArchivoEnum tipoArchivo, EstadoArchivoEnum estadoArchivo);

    @Query(value = "select pv.id_payment_voucher as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, " +
            "pvf.tipo_archivo as tipo from register_file_upload u \n" +
            "inner join payment_voucher_file pvf on pvf.id_register_file_send = u.id_register_file_send \n" +
            "inner join payment_voucher pv on pv.id_payment_voucher = pvf.id_payment_voucher \n" +
            "where pv.id_payment_voucher = ?1 and pvf.tipo_archivo = ?2 and estado_archivo = ?3 \n" +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idPayment, String tipoArchivo, String estadoArchivo);

    @Query("select p FROM PaymentVoucherFileEntity p "
            + "where p.paymentVoucher.idPaymentVoucher= :idPaymentVoucher "
            + "and p.tipoArchivo = 'CDR' "
    )
    public PaymentVoucherFileEntity getPaymentVoucherFileEntityCDR(
            @Param("idPaymentVoucher") Long idPaymentVoucher);


}

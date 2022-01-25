package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.model.TmpVoucherSendBillEntity;

import java.util.List;

public interface TmpVoucherSendBillRepository extends CrudRepository<TmpVoucherSendBillEntity, Long> {

    @Procedure(name = "TmpVoucherSendBillEntity.getVoucherPendientesToSendBill")
    List<TmpVoucherSendBillEntity> getVoucherPendientesToSendBill();

    /*
    @Query("select tmp FROM TmpVoucherSendBillEntity tmp "
            + "where tmp.idPaymentVoucher = :idPaymentVoucher ")
    public List<TmpVoucherSendBillEntity> getPaymentVoucherTemp(
            @Param("idPaymentVoucher") String idPaymentVoucher);
    */
    public TmpVoucherSendBillEntity findByIdPaymentVoucher(Long idPaymentVoucher);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update TmpVoucherSendBillEntity tmp "
            + "set tmp.estado = :estado "
            + "where tmp.idTmpSendBill = :identificador")
    public void updateStatusVoucherTmp(
            @Param("identificador") Long identificador,
            @Param("estado") String estado);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("update TmpVoucherSendBillEntity tmp "
            + "set tmp.estado = :estado "
            + "where tmp.idTmpSendBill = :identificador")
    public void updateStatusExcelTmp(
            @Param("identificador") Long identificador,
            @Param("estado") String estado);


}

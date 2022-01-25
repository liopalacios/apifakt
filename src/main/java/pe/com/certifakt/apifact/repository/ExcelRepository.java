package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.certifakt.apifact.model.DowloadExcelEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import java.util.List;

public interface ExcelRepository extends JpaRepository<DowloadExcelEntity, Long> {

    @Query(value = "SELECT p FROM PaymentVoucherEntity p "
            + "left join p.oficina o "
            + "where p.rucEmisor = ?1 and p.fechaEmisionDate between to_date(?2, 'DD-MM-YYYY') and to_date(?3, 'DD-MM-YYYY') " +
            "and ( ?4 is null or p.tipoComprobante like ?4) "
            + "and (?5 is null or p.serie like ?5)"
            + "order by p.fechaEmision desc, p.numero desc")
    List<PaymentVoucherEntity> findAllSerchForExcel(String usuario, String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante, String serie);




}

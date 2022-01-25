package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.CuotasPaymentVoucherEntity;
import pe.com.certifakt.apifact.model.DetailsPaymentVoucherEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVoucherCuota {


    private Integer numero;
    private BigDecimal monto;
    private String fecha;

    public static List<PaymentVoucherCuota> transformToBeanList(List<CuotasPaymentVoucherEntity> lines) {
        List<PaymentVoucherCuota> items = new ArrayList<>();
        if (lines == null) return items;

        lines.forEach(CuotasPaymentVoucherEntity -> {
            items.add(PaymentVoucherCuota.builder()
                    .numero(CuotasPaymentVoucherEntity.getNumero())
                    .monto(CuotasPaymentVoucherEntity.getMonto())
                    .fecha(CuotasPaymentVoucherEntity.getFecha())
                    .build());
        });

        return items;


    }

}

package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSexDaysDetails {
    private String documento;
    private String emision;
    private Date registro;
    private String receptor;
    private BigDecimal monto;

}

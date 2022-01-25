package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="merkdopago_notify" )
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MerkdopagoNotifyEntity {
    @Id
    @SequenceGenerator(name="merkdopago_notify_id_seq", sequenceName="merkdopago_notify_id_seq", allocationSize=1 )
    @GeneratedValue(strategy=GenerationType.AUTO, generator="merkdopago_notify_id_seq")
    private Long id;

    @Column(name="id_merkdo")
    private Long idmerkdopago;

    private String topic;

    @Column(name = "reporte")
    private Timestamp reporte;

    private Date registro;

    private Boolean produccion;

    private String creado;

    private Long usuario;

    private String action;

    private String dataid;
    @Column(name = "type_operation")
    private String typeope;
    private String aprobado;
    @Column(name = "payer_email")
    private String pemail;
    @Column(name = "payer_name")
    private String pname;
    @Column(name = "payer_type_doc")
    private String ptypedoc;
    @Column(name = "payer_number_doc")
    private String pnumberdoc;
    @Column(name = "order_pago_id")
    private String orderid;
    @Column(name = "order_pago_type")
    private String ordertype;
    private boolean live;
    @Column(name = "status_mercado")
    private String status;
    @Column(name = "transaction_amount")
    private Float tamount;
    private String description;
    @Column(name = "authorization_code")
    private String authorization;
    @Column(name = "collector_id")
    private BigDecimal collector;
    @Column(name = "data_last_updated")
    private String lastupdate;
    private String currency;

}

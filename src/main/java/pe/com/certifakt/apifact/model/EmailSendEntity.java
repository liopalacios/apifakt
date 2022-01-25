package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "email_send")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailSendEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="email_send_seq", sequenceName = "email_send_seq" , allocationSize=1)
    @GeneratedValue(strategy= GenerationType.AUTO, generator="email_send_seq")
    @Column(name = "id_email_send")
    private Long idEmailSend;

    @Column(name = "email")
    private String email;

    @Column(name="id_dowload_excel")
    private Long idDowloadExcel;

    @Column(name = "fecha")
    private Date fecha;
    @Column(name = "usuario")
    private String usuario;

}

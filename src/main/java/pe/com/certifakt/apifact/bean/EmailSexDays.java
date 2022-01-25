package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSexDays {
    private String ruc;
    private String email;
    private String nombre;
    private List<EmailSexDaysDetails> details;
}

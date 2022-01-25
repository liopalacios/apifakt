package pe.com.certifakt.apifact.bean;


import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestValidation implements Serializable {
    private String xmlBase64;
    private String nameDocument;
    private String ublVersion;
}

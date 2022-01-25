package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ErrorXml  implements Serializable {
    private String errorCode;
    private String errorDescription;

}

package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.enums.ComunicacionSunatEnum;

import java.io.Serializable;

/**
 * @author Luis
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSunat implements Serializable {

    private String contentBase64;
    private String statusCode;
    private boolean success;
    private String message;
    private String nameDocument;
    private String ticket;
    private String rucEmisor;
    private ComunicacionSunatEnum estadoComunicacionSunat;

}
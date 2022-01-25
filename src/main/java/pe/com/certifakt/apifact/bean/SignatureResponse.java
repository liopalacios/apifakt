package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureResponse  implements Serializable {

    private Boolean status;
    private ByteArrayOutputStream signatureFile;
    private String digestValue;
}

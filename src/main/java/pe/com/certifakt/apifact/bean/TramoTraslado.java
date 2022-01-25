package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.TramoTrasladoDeserializer;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = TramoTrasladoDeserializer.class)
public class TramoTraslado implements Serializable {

    private Integer correlativoTramo;
    private String modalidadTraslado;
    private String fechaInicioTraslado;
    //Transportista (transporte publico)
    private String numeroDocumentoIdentidadTransportista;
    private String tipoDocumentoIdentidadTransportista;
    private String denominacionTransportista;
    //Vehiculo (transporte privado)
    private String numeroPlacaVehiculo;
    //Conductor transporte privado
    private String numeroDocumentoIdentidadConductor;
    private String tipoDocumentoIdentidadConductor;

}

package pe.com.certifakt.apifact.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ApiError {

    private HttpStatus estado;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    private String mensaje;



    private ApiError() {
        fecha = new Date();
    }

    ApiError(HttpStatus status) {
        this();
        this.estado = status;
    }

    ApiError(HttpStatus status, Throwable ex) {
        this();
        this.estado = status;
        this.mensaje = "Ocurrió un error.";

    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.estado = status;
        this.mensaje = message;

    }



}


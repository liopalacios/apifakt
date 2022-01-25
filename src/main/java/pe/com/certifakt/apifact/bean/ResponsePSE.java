/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author Luis
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePSE  implements Serializable {

    private Boolean estado;
    private String mensaje;
    private String nombre;

    private String urlPdf;
    private String urlPdfTicket;
    private String urlPdfA4;
    private String urlXml;

    private String ticket;

    private String estadoSunat;
    private String urlCdr;

    private Object respuesta;

    private String codigoHash;

    private Integer intentosGetStatus;
}

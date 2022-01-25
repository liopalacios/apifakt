package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pe.com.certifakt.apifact.deserializer.GuiaRemisionDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonDeserialize(using = GuiaRemisionDeserializer.class)
public class GuiaRemision implements Serializable {

    private static final long serialVersionUID = -7234214601570568717L;

    private String serie;
    private Integer numero;
    private String fechaEmision;
    private String horaEmision;
    private String tipoComprobante;
    /**
     * Serie de la guia dada de baja por cambio de destinatario
     */
    private String serieGuiaBaja;
    /**
     * Numero de la guia dada de baja por cambio de destinatario
     */
    private Integer numeroGuiaBaja;
    private String tipoComprobanteBaja;
    private String descripcionComprobanteBaja;
    //documento relacionado
    /**
     * numeracionDAM, esta asociado tipo de documento relacionado 01 del catalogo 21
     */
    private String numeracionDAM;
    /**
     * numeracionDAM, esta asociado tipo de documento relacionado 04 del catalogo 21
     */
    private String numeracionManifiestoCarga;
    private String identificadorDocumentoRelacionado;
    private String codigoTipoDocumentoRelacionado;
    //Datos del remitente
    private String numeroDocumentoIdentidadRemitente;
    private String tipoDocumentoIdentidadRemitente;
    private String denominacionRemitente;
    //Datos del destinatario
    private String numeroDocumentoIdentidadDestinatario;
    private String tipoDocumentoIdentidadDestinatario;
    private String denominacionDestinatario;
    //Datos del proveedor
    private String numeroDocumentoIdentidadProveedor;
    private String tipoDocumentoIdentidadProveedor;
    private String denominacionProveedor;
    //Datos del envio
    private String motivoTraslado;
    private String descripcionMotivoTraslado;
    private Boolean indicadorTransbordoProgramado;
    private BigDecimal pesoTotalBrutoBienes;
    private String unidadMedidaPesoBruto;
    private Long numeroBultos;
    private List<TramoTraslado> tramosTraslados;
    //Direccion punto de llegada
    private String ubigeoPuntoLlegada;
    private String direccionPuntoLlegada;
    //Datos del contenedor
    private String numeroContenedor;
    //Direccion punto de partida
    private String ubigeoPuntoPartida;
    private String direccionPuntoPartida;
    //Puerto o aeropuerto embarque/desembarque
    private String codigoPuerto;

    private String identificadorDocumento;

    private List<GuiaItem> bienesToTransportar;

    private List<String> observaciones;

    private List<CampoAdicionalGuia> camposAdicionales;

    /*Atributos adicionales para los items*/
    private BigDecimal totalValorVentaExportacion;
    private BigDecimal totalValorVentaGravada;
    private BigDecimal totalValorVentaGravadaIVAP;
    private BigDecimal totalValorVentaInafecta;
    private BigDecimal totalValorVentaExonerada;
    private BigDecimal totalValorVentaGratuita;
    private BigDecimal totalValorBaseOtrosTributos;
    private BigDecimal totalValorBaseIsc;
    private BigDecimal totalIgv;
    private BigDecimal totalIvap;
    private BigDecimal totalIsc;
    private BigDecimal totalImpOperGratuita;
    private BigDecimal totalOtrostributos;
    private BigDecimal totalDescuento;
    private BigDecimal descuentoGlobales;
    private BigDecimal sumatoriaOtrosCargos;
    private BigDecimal totalAnticipos;
    private BigDecimal importeTotalVenta;

}

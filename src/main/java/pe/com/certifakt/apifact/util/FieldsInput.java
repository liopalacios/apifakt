package pe.com.certifakt.apifact.util;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public abstract class FieldsInput<T> extends JsonDeserializer<T> {

	@Value("${json.listaByIdDocumentos.input.numero}")
	protected String numeroForBusquedaByIdDocumentosLabel;
	@Value("${json.listaByIdDocumentos.input.serie}")
	protected String serieForBusquedaByIdDocumentosLabel;
	@Value("${json.listaByIdDocumentos.input.tipoComprobante}")
	protected String tipoComprobanteForBusquedaByIdDocumentosLabel;

	@Value("${json.lista.input.numero}")
	protected String numeroToBuscarLabel;
	@Value("${json.lista.input.serie}")
	protected String serieToBuscarLabel;
	@Value("${json.lista.input.tipoComprobante}")
	protected String tipoComprobanteToBuscarLabel;
	@Value("${json.lista.input.fechaEmisionDesde}")
	protected String fechaEmisionDesdeToBuscarLabel;
	@Value("${json.lista.input.fechaEmisionHasta}")
	protected String fechaEmisionHastaToBuscarLabel;

//	@Value("${json.annular.input.rucEmisor}")
//	protected String rucEmisorToAnularLabel;
//	@Value("${json.annular.input.fechaEmision}")
//	protected String fechaEmisionToAnularLabel;
	@Value("${json.annular.input.numeroDocumento}")
	protected String numeroToAnularLabel;
	@Value("${json.annular.input.serieDocumento}")
	protected String serieToAnularLabel;
	@Value("${json.annular.input.tipoComprobante}")
	protected String tipoComprobanteToAnularLabel;
	@Value("${json.annular.input.tipoComprobanteRelacionado}")
	protected String tipoComprobanteRelacionadoToAnularLabel;
	@Value("${json.annular.input.motivoAnulacion}")
	protected String motivoToAnularLabel;

	/*
	@Value("${json.voided.input.fechaEmision}")
	protected String fechaEmisionToDarBajaLabel;
	@Value("${json.voided.input.rucEmisor}")
	protected String rucEmisorToDarBajaLabel;
	@Value("${json.voided.input.items}")
	protected String itemsToDarBajaLabel;

	@Value("${json.voided.item.input.numeroDocumento}")
	protected String numeroDocumentoLabel;
	@Value("${json.voided.item.input.serieDocumento}")
	protected String serieDocumentoLabel;
	@Value("${json.voided.item.input.tipoComprobante}")
	protected String tipoComprobanteToDarBajaLabel;
	@Value("${json.voided.item.input.razon}")
	protected String razonLabel;
	 */

	@Value("${json.payment_voucher.input.tipoComprobante}")
	protected String tipoComprobanteLabel;
	@Value("${json.payment_voucher.input.serie}")
	protected String serieLabel;
	@Value("${json.payment_voucher.input.numero}")
	protected String numeroLabel;
	@Value("${json.payment_voucher.input.fechaEmision}")
	protected String fechaEmisionLabel;
	@Value("${json.payment_voucher.input.horaEmision}")
	protected String horaEmisionLabel;
	@Value("${json.payment_voucher.input.fechaVencimiento}")
	protected String fechaVencimientoLabel;
	@Value("${json.payment_voucher.input.codigoMoneda}")
	protected String codigoMonedaLabel;


	@Value("${json.payment_voucher.input.tipoTransaccion}")
	protected String tipoTransaccionLabel;
	@Value("${json.payment_voucher.input.montoPendiente}")
	protected String montoPendienteLabel;
	@Value("${json.payment_voucher.input.cantidadCuotas}")
	protected String cantidadCuotasLabel;
	@Value("${json.payment_voucher.input.pagoCuenta}")
	protected String pagoCuentaLabel;

	/*
	@Value("${json.payment_voucher.input.montoCuota}")
	protected String montoCuotaLabel;
	@Value("${json.payment_voucher.input.numeroCuota}")
	protected String numeroCuotaLabel;
	@Value("${json.payment_voucher.input.nCuota}")
	protected String nCuotaLabel;
	@Value("${json.payment_voucher.input.idPaymentVoucherReference}")*/
	protected String idPaymentVoucherReferenceLabel;
	@Value("${json.payment_voucher.input.rucEmisor}")
	protected String rucEmisorLabel;
	@Value("${json.payment_voucher.input.codigoLocalAnexoEmisor}")
	protected String codigoLocalAnexoEmisorLabel;
	@Value("${json.payment_voucher.input.tipoDocumentoReceptor}")
	protected String tipoDocumentoReceptorLabel;
	@Value("${json.payment_voucher.input.numeroDocumentoReceptor}")
	protected String numeroDocumentoReceptorLabel;
	@Value("${json.payment_voucher.input.denominacionReceptor}")
	protected String denominacionReceptorLabel;

    @Value("${json.payment_voucher.input.direccionReceptor}")
    protected String direccionReceptorLabel;
    @Value("${json.payment_voucher.input.emailReceptor}")
    protected String emailReceptorLabel;

//	@Value("${json.payment_voucher.input.tipoGuiaRemision}")
//	protected String tipoGuiaRemisionLabel;
//	@Value("${json.payment_voucher.input.numeroGuiaRemision}")
//	protected String numeroGuiaRemisionLabel;

	@Value("${json.payment_voucher.input.tipoDocumentoRelacionado}")
	protected String tipoDocumentoRelacionadoLabel;
	@Value("${json.payment_voucher.input.numeroDocumentoRelacionado}")
	protected String numeroDocumentoRelacionadoLabel;

	@Value("${json.payment_voucher.input.totalValorVentaExportacion}")
	protected String totalValorVentaExportacionlabel;
	@Value("${json.payment_voucher.input.totalValorVentaGravada}")
	protected String totalValorVentaGravadaLabel;
	@Value("${json.payment_voucher.input.totalValorVentaGravadaIVAP}")
	protected String totalValorVentaGravadaIVAPLabel;
	@Value("${json.payment_voucher.input.totalValorVentaInafecta}")
	protected String totalValorVentaInafectaLabel;
	@Value("${json.payment_voucher.input.totalValorVentaExonerada}")
	protected String totalValorVentaExoneradaLabel;
	@Value("${json.payment_voucher.input.totalValorVentaGratuita}")
	protected String totalValorVentaGratuitaLabel;
	@Value("${json.payment_voucher.input.totalValorBaseIsc}")
	protected String totalValorBaseIscLabel;
	@Value("${json.payment_voucher.input.totalValorBaseOtrosTributos}")
	protected String totalValorBaseOtrosTributosLabel;

	@Value("${json.payment_voucher.input.redondeoImporteTotal}")
	protected String montoRedondeoImporteTotalLabel;
	@Value("${json.payment_voucher.input.totalDescuento}")
	protected String totalDescuentoLabel;
	@Value("${json.payment_voucher.input.totalIgv}")
	protected String totalIgvLabel;
	@Value("${json.payment_voucher.input.totalIsc}")
	protected String totalIscLabel;
	@Value("${json.payment_voucher.input.totalImpOperGratuita}")
	protected String totalImpOperGratuitaLabel;
	@Value("${json.payment_voucher.input.totalOtrostributos}")
	protected String totalOtrostributosLabel;
	@Value("${json.payment_voucher.input.descuentoGlobales}")
	protected String descuentoGlobalesLabel;
	@Value("${json.payment_voucher.input.totalOtrosCargos}")
	protected String totalOtrosCargosLabel;
	@Value("${json.payment_voucher.input.totalAnticipos}")
	protected String totalAnticiposLabel;
	@Value("${json.payment_voucher.input.importeTotal}")
	protected String importeTotalLabel;
	@Value("${json.payment_voucher.input.tipoOperacion}")
	protected String tipoOperacionLabel;
	@Value("${json.payment_voucher.input.tipoNotaCredito}")
	protected String tipoNotaCreditoLabel;
	@Value("${json.payment_voucher.input.tipoNotaDebito}")
	protected String tipoNotaDebitoLabel;
	@Value("${json.payment_voucher.input.serieAfectado}")
	protected String serieAfectadoLabel;
	@Value("${json.payment_voucher.input.numeroAfectado}")
	protected String numeroAfectadoLabel;
	@Value("${json.payment_voucher.input.tipoComprobanteAfectado}")
	protected String tipoComprobanteAfectadoLabel;
	@Value("${json.payment_voucher.input.motivoNota}")
	protected String motivoNotaLabel;
	@Value("${json.payment_voucher.input.items}")
	protected String itemsLabel;

	@Value("${json.payment_voucher.input.anticipos}")
	protected String anticiposLabel;

	@Value("${json.payment_voucher.input.anticipo.serie}")
	protected String serieAnticipoLabel;
	@Value("${json.payment_voucher.input.anticipo.numero}")
	protected String numeroAnticipoLabel;
	@Value("${json.payment_voucher.input.anticipo.tipoDocumento}")
	protected String tipoDocumentoAnticipoLabel;
	@Value("${json.payment_voucher.input.anticipo.monto}")
	protected String montoAnticipadoLabel;

	//
    @Value("${json.payment_voucher.input.guiasRelacionadas}")
    protected String guiasRelacionadasLabel;

    @Value("${json.payment_voucher.input.guiaRelacionada.codigoTipoGuia}")
    protected String codigoTipoGuiaLabel;
    @Value("${json.payment_voucher.input.guiaRelacionada.serieNumeroGuia}")
    protected String serieNumeroGuiaLabel;
    //


    @Value("${json.payment_voucher.input.ordenCompra}")
    protected String ordenCompraLabel;

    @Value("${json.payment_voucher.input.camposAdicionales}")
    protected String camposAdicionalesLabel;


	@Value("${json.payment_voucher.input.cuotas}")
	protected String cuotasLabel;


    @Value("${json.payment_voucher.input.campoAdicional.nombreCampo}")
    protected String nombreCampoAdicionalLabel;
    @Value("${json.payment_voucher.input.campoAdicional.valorCampo}")
    protected String valorCampoAdicionalLabel;

	@Value("${json.payment_voucher.input.cuota.numero}")
	protected String numeroCuotaLabel;
	@Value("${json.payment_voucher.input.cuota.monto}")
	protected String montoCuotaLabel;
	@Value("${json.payment_voucher.input.cuota.fecha}")
	protected String fechaCuotaLabel;

	@Value("${json.payment_voucher.item.input.codigoUnidadMedida}")
	protected String codigoUnidadMedidaLabel;
	@Value("${json.payment_voucher.item.input.cantidad}")
	protected String cantidadLabel;
	@Value("${json.payment_voucher.item.input.descripcion}")
	protected String descripcionLabel;
	@Value("${json.payment_voucher.item.input.codigoProducto}")
	protected String codigoProductoLabel;
	@Value("${json.payment_voucher.item.input.codigoProductoSunat}")
	protected String codigoProductoSunatLabel;
	@Value("${json.payment_voucher.item.input.hidroCantidad}")
	protected String hidroCantidadLabel;
	@Value("${json.payment_voucher.item.input.hidroDescripcionTipo}")
	protected String hidroDescripcionTipoLabel;
	@Value("${json.payment_voucher.item.input.hidroEmbarcacion}")
	protected String hidroEmbarcacionLabel;
	@Value("${json.payment_voucher.item.input.hidroFechaDescarga}")
	protected String hidroFechaDescargaLabel;
	@Value("${json.payment_voucher.item.input.hidroLugarDescarga}")
	protected String hidroLugarDescargaLabel;
	@Value("${json.payment_voucher.item.input.hidroMatricula}")
	protected String hidroMatriculaLabel;
	@Value("${json.payment_voucher.item.input.codigoProductoGS1}")
	protected String codigoProductoGS1Label;
	@Value("${json.payment_voucher.item.input.valorUnitario}")
	protected String valorUnitarioLabel;
	@Value("${json.payment_voucher.item.input.valorVenta}")
	protected String valorVentaLabel;
	@Value("${json.payment_voucher.item.input.descuento}")
	protected String descuentoLabel;
	@Value("${json.payment_voucher.item.input.codigoDescuento}")
	protected String codigoDescuentoLabel;
	@Value("${json.payment_voucher.item.input.precioVentaUnitario}")
	protected String precioVentaUnitarioLabel;
	@Value("${json.payment_voucher.item.input.valorReferencialUnitario}")
	protected String valorReferencialUnitarioLabel;

	@Value("${json.payment_voucher.item.input.montoBaseIgv}")
	protected String montoBaseIgvLabel;
	@Value("${json.payment_voucher.item.input.montoBaseIvap}")
	protected String montoBaseIvapLabel;
	@Value("${json.payment_voucher.item.input.montoBaseExportacion}")
	protected String montoBaseExportacionLabel;
	@Value("${json.payment_voucher.item.input.montoBaseExonerado}")
	protected String montoBaseExoneradoLabel;
	@Value("${json.payment_voucher.item.input.montoBaseInafecto}")
	protected String montoBaseInafectoLabel;
	@Value("${json.payment_voucher.item.input.montoBaseGratuito}")
	protected String montoBaseGratuitoLabel;
	@Value("${json.payment_voucher.item.input.montoBaseIsc}")
	protected String montoBaseIscLabel;
	@Value("${json.payment_voucher.item.input.montoBaseIcbper}")
	protected String montoBaseIcbperLabel;
	@Value("${json.payment_voucher.item.input.montoIcbper}")
	protected String montoIcbperLabel;
	@Value("${json.payment_voucher.item.input.montoBaseOtrosTributos}")
	protected String montoBaseOtrosTributosLabel;

	@Value("${json.payment_voucher.item.input.impuestoVentaGratuita}")
	protected String impuestoVentaGratuitaLabel;
	@Value("${json.payment_voucher.item.input.otrosTributos}")
	protected String otrosTributosLabel;
	@Value("${json.payment_voucher.item.input.ivap}")
	protected String ivapLabel;
	@Value("${json.payment_voucher.item.input.igv}")
	protected String igvLabel;

	@Value("${json.payment_voucher.item.input.porcentajeIgv}")
	protected String porcentajeIgvLabel;
	@Value("${json.payment_voucher.item.input.porcentajeIvap}")
	protected String porcentajeIvapLabel;
	@Value("${json.payment_voucher.item.input.porcentajeIsc}")
	protected String porcentajeIscLabel;
	@Value("${json.payment_voucher.item.input.porcentajeOtrosTributos}")
	protected String porcentajeOtrosTributosLabel;
	@Value("${json.payment_voucher.item.input.porcentajeTributoVentaGratuita}")
	protected String porcentajeTributoVentaGratuitaLabel;

	@Value("${json.payment_voucher.item.input.tipoAfectacionIGV}")
	protected String tipoAfectacionIGVLabel;
	@Value("${json.payment_voucher.item.input.isc}")
	protected String iscLabel;
	@Value("${json.payment_voucher.item.input.tipoCalculoISC}")
	protected String tipoCalculoISCLabel;

//	@Value("${json.retention.input.serie}")
//	protected String serieOtroCpeLabel;
//	@Value("${json.retention.input.numero}")
//	protected String numeroOtroCpeLabel;
//	@Value("${json.retention.input.fechaEmision}")
//	protected String fechaEmisionOtroCpeLabel;
//	@Value("${json.retention.input.tipoComprobante}")
//	protected String tipoComprobanteOtroCpeLabel;

	//Retencion
	@Value("${json.retention.input.numeroDocumentoIdentidadProveedor}")
	protected String numeroDocumentoIdentidadReceptorRetencionLabel;
	@Value("${json.retention.input.tipoDocumentoIdentidadProveedor}")
	protected String tipoDocumentoIdentidadReceptorRetencionLabel;
	@Value("${json.retention.input.nombreComercialProveedor}")
	protected String nombreComercialReceptorRetencionLabel;
	@Value("${json.retention.input.denominacionProveedor}")
	protected String denominacionReceptorRetencionLabel;

	@Value("${json.retention.input.ubigeoProveedor}")
	protected String ubigeoDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.direccionProveedor}")
	protected String direccionCompletaDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.urbanizacionProveedor}")
	protected String urbanizacionDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.departamentoProveedor}")
	protected String departamentoDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.provinciaProveedor}")
	protected String provinciaDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.distritoProveedor}")
	protected String distritoDomicilioFiscalReceptorRetencionLabel;
	@Value("${json.retention.input.codigoPaisProveedor}")
	protected String codigoPaisDomicilioFiscalReceptorRetencionLabel;

    @Value("${json.retention.input.emailProveedor}")
    protected String emailReceptorRetencionLabel;

	@Value("${json.retention.input.regimenRetencion}")
	protected String regimenRetencionLabel;
	@Value("${json.retention.input.tasaRetencion}")
	protected String tasaRetencionLabel;
	@Value("${json.retention.input.observaciones}")
	protected String observacionesOtroCpeLabel;
	@Value("${json.retention.input.importeTotalRetenido}")
	protected String importeTotalRetenidoLabel;
	@Value("${json.retention.input.importeTotalPagado}")
	protected String importeTotalPagadoLabel;
	@Value("${json.retention.input.codigoMoneda}")
	protected String codigoMonedaRetencionLabel;
	@Value("${json.retention.input.documentosRelacionados}")
	protected String documentosRelacionadosRetencionLabel;

	//percepcion

	@Value("${json.perception.input.numeroDocumentoIdentidadCliente}")
	protected String numeroDocumentoIdentidadReceptorPercepcionLabel;
	@Value("${json.perception.input.tipoDocumentoIdentidadCliente}")
	protected String tipoDocumentoIdentidadReceptorPercepcionLabel;
	@Value("${json.perception.input.nombreComercialCliente}")
	protected String nombreComercialReceptorPercepcionLabel;
	@Value("${json.perception.input.denominacionCliente}")
	protected String denominacionReceptorPercepcionLabel;

	@Value("${json.perception.input.ubigeoCliente}")
	protected String ubigeoDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.direccionCliente}")
	protected String direccionCompletaDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.urbanizacionCliente}")
	protected String urbanizacionDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.departamentoCliente}")
	protected String departamentoDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.provinciaCliente}")
	protected String provinciaDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.distritoCliente}")
	protected String distritoDomicilioFiscalReceptorPercepcionLabel;
	@Value("${json.perception.input.codigoPaisCliente}")
	protected String codigoPaisDomicilioFiscalReceptorPercepcionLabel;

	@Value("${json.perception.input.emailCliente}")
	protected String emailReceptorPercepcionLabel;

	@Value("${json.perception.input.regimenPercepcion}")
	protected String regimenPercepcionLabel;
	@Value("${json.perception.input.tasaPercepcion}")
	protected String tasaPercepcionLabel;
	@Value("${json.perception.input.importeTotalPercibido}")
	protected String importeTotalPercibidoLabel;
	@Value("${json.perception.input.importeTotalCobrado}")
	protected String importeTotalCobradoLabel;
	@Value("${json.perception.input.codigoMoneda}")
	protected String codigoMonedaPercepcionLabel;
	@Value("${json.perception.input.documentosRelacionados}")
	protected String documentosRelacionadosPercepcionLabel;

	//Detalle de retencion
	@Value("${json.retention.detalle.input.tipoDocumento}")
	protected String tipoDocumentoRelacionadoOtroCpeLabel;
	@Value("${json.retention.detalle.input.serieDocumento}")
	protected String serieDocumentoRelacionadoOtroCpeLabel;
	@Value("${json.retention.detalle.input.numeroDocumento}")
	protected String numeroDocumentoRelacionadoOtroCpeLabel;
	@Value("${json.retention.detalle.input.fechaEmision}")
	protected String fechaEmisionDocumentoRelacionadoOtroCpeLabel;
	@Value("${json.retention.detalle.input.importeTotal}")
	protected String importeTotalDocumentoRelacionadoOtroCpeLabel;
	@Value("${json.retention.detalle.input.moneda}")
	protected String monedaDocumentoRelacionadoOtroCpeLabel;

	@Value("${json.retention.detalle.input.fechaPago}")
	protected String fechaPagoCobroRetencionLabel;
	@Value("${json.retention.detalle.input.numeroPago}")
	protected String numeroPagoCobroRetencionLabel;
	@Value("${json.retention.detalle.input.pagoSinRetencion}")
	protected String importePagoSinRetencionCobroRetencionLabel;
	@Value("${json.retention.detalle.input.monedaPago}")
	protected String monedaPagoCobroRetencionLabel;

	@Value("${json.retention.detalle.input.importeRetenido}")
	protected String importeRetenidoPercibidoRetencionLabel;
	@Value("${json.retention.detalle.input.monedaImporteRetenido}")
	protected String monedaImporteRetenidoPercibidoRetencionLabel;
	@Value("${json.retention.detalle.input.fechaRetencion}")
	protected String fechaRetencionPercepcionRetencionLabel;
	@Value("${json.retention.detalle.input.importeTotalPagar}")
	protected String importeTotalToPagarCobrarRetencionLabel;
	@Value("${json.retention.detalle.input.monedaImporteTotalPagar}")
	protected String monedaImporteTotalToPagarCobrarRetencionLabel;

	@Value("${json.perception.detalle.input.fechaCobro}")
	protected String fechaPagoCobroPercepcionLabel;
	@Value("${json.perception.detalle.input.numeroCobro}")
	protected String numeroPagoCobroPercepcionLabel;
	@Value("${json.perception.detalle.input.importeCobro}")
	protected String importePagoSinRetencionCobroPercepcionLabel;
	@Value("${json.perception.detalle.input.monedaCobro}")
	protected String monedaPagoCobroPercepcionLabel;

	@Value("${json.perception.detalle.input.importePercibido}")
	protected String importeRetenidoPercibidoPercepcionLabel;
	@Value("${json.perception.detalle.input.monedaImportePercibido}")
	protected String monedaImporteRetenidoPercibidoPercepcionLabel;
	@Value("${json.perception.detalle.input.fechaPercepcion}")
	protected String fechaRetencionPercepcionPercepcionLabel;
	@Value("${json.perception.detalle.input.importeTotalCobrar}")
	protected String importeTotalToPagarCobrarPercepcionLabel;
	@Value("${json.perception.detalle.input.monedaImporteTotalCobrar}")
	protected String monedaImporteTotalToPagarCobrarPercepcionLabel;

	@Value("${json.retention.detalle.input.monedaReferenciaTipoCambio}")
	protected String monedaReferenciaTipoCambioOtroCpeLabel;
	@Value("${json.retention.detalle.input.monedaObjetivoTasaCambio}")
	protected String monedaObjetivoTasaCambioOtroCpeLabel;
	@Value("${json.retention.detalle.input.tipoCambio}")
	protected String tipoCambioOtroCpeLabel;
	@Value("${json.retention.detalle.input.fechaCambio}")
	protected String fechaCambioOtroCpeLabel;

	//Guia de Remision
	@Value("${json.guiaRemision.input.serie}")
	protected String serieGuiaLabel;
	@Value("${json.guiaRemision.input.numero}")
	protected String numeroGuiaLabel;
	@Value("${json.guiaRemision.input.fechaEmision}")
	protected String fechaEmisionGuiaLabel;
	@Value("${json.guiaRemision.input.observaciones}")
	protected String observacionesGuiaLabel;
	@Value("${json.guiaRemision.input.serieBaja}")
	protected String serieBajaGuiaLabel;
	@Value("${json.guiaRemision.input.numeroBaja}")
	protected String numeroBajaGuiaLabel;
	@Value("${json.guiaRemision.input.numeracionDAM}")
	protected String numeracionDAMLabel;
	@Value("${json.guiaRemision.input.numeracionManifiestoCarga}")
	protected String numeracionManifiestoCargaLabel;
	@Value("${json.guiaRemision.input.identificadorDocumentoRelacionado}")
	protected String identificadorDocumentoRelacionadoGuiaLabel;
	@Value("${json.guiaRemision.input.tipoDocumentoRelacionado}")
	protected String tipoDocumentoRelacionadoGuiaLabel;
	@Value("${json.guiaRemision.input.numeroIdentidadDestinatario}")
	protected String numeroIdentidadDestinatarioGuiaLabel;
	@Value("${json.guiaRemision.input.tipoDocumentoIdentidadDestinatario}")
	protected String tipoDocumentoIdentidadDestinatarioGuiaLabel;
	@Value("${json.guiaRemision.input.denominacionDestinatario}")
	protected String denominacionDestinatarioGuiaLabel;
	@Value("${json.guiaRemision.input.numeroIdentidadTercero}")
	protected String numeroDocumentoIdentidadProveedorGuiaLabel;
	@Value("${json.guiaRemision.input.tipoDocumentoIdentidadTercero}")
	protected String tipoDocumentoIdentidadProveedorGuiaLabel;
	@Value("${json.guiaRemision.input.denominacionTercero}")
	protected String denominacionProveedorGuiaLabel;
	@Value("${json.guiaRemision.input.motivoTraslado}")
	protected String motivoTrasladoGuiaLabel;
	@Value("${json.guiaRemision.input.descripcionMotivoTraslado}")
	protected String descripcionMotivoTrasladoGuiaLabel;
	@Value("${json.guiaRemision.input.indicadorTransbordoProgramado}")
	protected String indicadorTransbordoProgramadoGuiaLabel;
	@Value("${json.guiaRemision.input.pesoTotalBrutoBienes}")
	protected String pesoTotalBrutoBienesGuiaLabel;
	@Value("${json.guiaRemision.input.unidadMedidaPesoBruto}")
	protected String unidadMedidaPesoBrutoGuiaLabel;
	@Value("${json.guiaRemision.input.numeroBultos}")
	protected String numeroBultosGuiaLabel;
	@Value("${json.guiaRemision.input.ubigeoPuntoLlegada}")
	protected String ubigeoPuntoLlegadaGuiaLabel;
	@Value("${json.guiaRemision.input.direccionPuntoLlegada}")
	protected String direccionPuntoLlegadaGuiaLabel;
	@Value("${json.guiaRemision.input.numeroContenedor}")
	protected String numeroContenedorGuiaLabel;
	@Value("${json.guiaRemision.input.ubigeoPuntoPartida}")
	protected String ubigeoPuntoPartidaGuiaLabel;
	@Value("${json.guiaRemision.input.direccionPuntoPartida}")
	protected String direccionPuntoPartidaGuiaLabel;
	@Value("${json.guiaRemision.input.codigoPuerto}")
	protected String codigoPuertoGuiaLabel;
	@Value("${json.guiaRemision.input.tramos}")
	protected String tramosGuiaLabel;
	@Value("${json.guiaRemision.input.items}")
	protected String itemsGuiaLabel;
	//Guia Item
	@Value("${json.guiaRemision.item.input.cantidad}")
	protected String cantidadGuiaLabel;
	@Value("${json.guiaRemision.item.input.unidadMedida}")
	protected String unidadMedidaGuiaLabel;
	@Value("${json.guiaRemision.item.input.descripcion}")
	protected String descripcionGuiaLabel;
	@Value("${json.guiaRemision.item.input.codigoItem}")
	protected String codigoItemGuiaLabel;
	@Value("${json.guiaRemision.item.input.precioItem}")
	protected String precioItemGuiaLabel;
	//Tramo de traslado
	@Value("${json.guiaRemision.tramo.input.modalidadTraslado}")
	protected String modalidadTrasladoGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.fechaInicioTraslado}")
	protected String fechaInicioTrasladoGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.numeroDocumentoIdentidadTransportista}")
	protected String numeroDocumentoIdentidadTransportistaGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.tipoDocumentoIdentidadTransportista}")
	protected String tipoDocumentoIdentidadTransportistaGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.denominacionTransportista}")
	protected String denominacionTransportistaGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.numeroPlacaVehiculo}")
	protected String numeroPlacaVehiculoGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.numeroDocumentoIdentidadConductor}")
	protected String numeroDocumentoIdentidadConductorGuiaLabel;
	@Value("${json.guiaRemision.tramo.input.tipoDocumentoIdentidadConductor}")
	protected String tipoDocumentoIdentidadConductorGuiaLabel;

	//Campo adicionales
	@Value("${json.guiaRemision.input.camposAdicionales}")
	protected String camposAdicionalesGuiaLabel;

	@Value("${json.guiaRemision.input.campoAdicional.nombreCampo}")
	protected String nombreCampoAdicionalGuiaLabel;
	@Value("${json.guiaRemision.input.campoAdicional.valorCampo}")
	protected String valorCampoAdicionalGuiaLabel;

   //Detracciones
	@Value("${json.payment_voucher.input.codigoMedioPago}")
	protected String codigoMedioPagoLabel;

	@Value("${json.payment_voucher.input.cuentaFinancieraBeneficiario}")
	protected String cuentaFinancieraBeneficiarioLabel;

	@Value("${json.payment_voucher.input.codigoBienDetraccion}")
	protected String codigoBienDetraccionLabel;

	@Value("${json.payment_voucher.input.porcentajeDetraccion}")
	protected String porcentajeDetraccionLabel;



	@Value("${json.payment_voucher.input.montoDetraccion}")
	protected String montoDetraccionLabel;

	@Value("${json.payment_voucher.input.detraccion}")
    protected String detraccionLabel;

	// retention 027
    @Value("${json.payment_voucher.item.input.detalleViajeDetraccion}")
    protected String detalleViajeDetraccionLabel;

    @Value("${json.payment_voucher.item.input.ubigeoOrigenDetraccion}")
    protected String ubigeoOrigenDetraccionLabel;

    @Value("${json.payment_voucher.item.input.direccionOrigenDetraccion}")
    protected String direccionOrigenDetraccionLabel;

    @Value("${json.payment_voucher.item.input.ubigeoDestinoDetraccion}")
    protected String ubigeoDestinoDetraccionLabel;

    @Value("${json.payment_voucher.item.input.direccionDestinoDetraccion}")
    protected String direccionDestinoDetraccionLabel;

    @Value("${json.payment_voucher.item.input.valorServicioTransporte}")
    protected String valorServicioTransporteLabel;

    @Value("${json.payment_voucher.item.input.valorCargaEfectiva}")
    protected String valorCargaEfectivaLabel;

    @Value("${json.payment_voucher.item.input.valorCargaUtil}")
    protected String valorCargaUtilLabel;

	@Value("${json.payment_voucher.item.input.unidadManejo}")
	protected String unidadManejoLabel;

	@Value("${json.payment_voucher.item.input.instruccionesEspeciales}")
	protected String instruccionesEspecialesLabel;

	@Value("${json.payment_voucher.item.input.marca}")
	protected String marcaLabel;



}

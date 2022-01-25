

package pe.com.certifakt.apifact.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.enums.Catalogo12;
import pe.com.certifakt.apifact.enums.TipoPdfEnum;
import pe.com.certifakt.apifact.exception.QRGenerationException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.ReportService;
import pe.com.certifakt.apifact.util.GenerateLetraNumber;
import pe.com.certifakt.apifact.util.QR;
import pe.com.certifakt.apifact.util.StringsUtils;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private DetailsPaymentVoucherRepository detailsPaymentVoucherRepository;

    @Autowired
    private OtherCpeRepository otherCpeRepository;

    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;

    @Autowired
    private CompanyRepository companyRepository;


    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    @Value("${urlspublicas.imagenes}")
    private String urlstorageURL;


    @Autowired
    private UnitCodeRepository unitCodeRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    @Autowired
    private AmazonS3ClientService amazonS3ClientService;

    HashMap<Integer,String> mapTransaccion = new HashMap<Integer, String>();

    public ReportServiceImpl() {
        this.mapTransaccion.put(1,"CONTADO");
        this.mapTransaccion.put(2,"CREDITO");
    }

    @Override
    @Transactional
    public ByteArrayInputStream getPdfComprobanteA4(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException {

        PaymentVoucherEntity comprobante = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(ruc, tipo, serie, numero);
        if (comprobante == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        CompanyEntity companyEntity = companyRepository.findByRuc(comprobante.getRucEmisor());

        return new ByteArrayInputStream(getPdfComprobantePorTipoFormato(comprobante, companyEntity, TipoPdfEnum.A4.getTipo()));

    }


    @Override
    @Transactional
    public ByteArrayInputStream getReporteEcxel(String ruc, String filtroDesde, String filtroHasta, String filtroTipoComprobante, String filtroRuc,
                                                String filtroSerie, Integer filtroNumero) throws ServiceException, IOException {

        if (filtroTipoComprobante != null) {
            filtroTipoComprobante = "%" + filtroTipoComprobante + "%";
        }
        if (filtroRuc != null) {
            filtroRuc = "%" + filtroRuc + "%";
        }
        if (filtroSerie != null) {
            filtroSerie = "%" + filtroSerie + "%";
        }
        Integer idOficina = null;
        List<PaymentVoucherEntity> result = paymentVoucherRepository.findAllSerch(ruc,
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, idOficina);
				/*, "%" +  + "%",
				"%" + serie + "%", numero, idOficina);*/
        if (result.isEmpty())
            throw new ServiceException("No se encontro data con los filtros");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setColumnWidth((short) 0, (short) ((50 * 8) / ((double) 1 / 20)));
        sheet.setColumnWidth((short) 1, (short) ((50 * 8) / ((double) 1 / 20)));
        workbook.setSheetName(0, "XSSFWorkbook example");
        Font font1 = workbook.createFont();
        font1.setFontHeightInPoints((short) 10);
        font1.setColor((short) 0xc); // make it blue
        font1.setBold(true);
        XSSFCellStyle cellStyle1 = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle1.setFont(font1);

        Font font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 10);
        font2.setColor((short) Font.COLOR_NORMAL);
        XSSFCellStyle cellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle2.setFont(font2);

        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("FECHA");
        cell1.setCellStyle(cellStyle1);
        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("TIPO");
        cell2.setCellStyle(cellStyle1);
        Cell cell3 = headerRow.createCell(2);
        cell1.setCellValue("NUMERO");
        cell1.setCellStyle(cellStyle1);
        Cell cell4 = headerRow.createCell(3);
        cell2.setCellValue("DOCUMENTO");
        cell2.setCellStyle(cellStyle1);

        int rownum;
        Row row = null;
        Cell cell = null;

        for (rownum = (short) 1; rownum <= result.size(); rownum++) {
            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(result.get(rownum - 1).getFechaEmisionDate());
            cell.setCellStyle(cellStyle2);
            cell = row.createCell(1);
            cell.setCellValue(result.get(rownum - 1).getTipoComprobante());
            cell.setCellStyle(cellStyle2);
            row = sheet.createRow(rownum);
            cell = row.createCell(2);
            cell.setCellValue(result.get(rownum - 1).getSerie() + "-" + result.get(rownum - 1).getNumero());
            cell.setCellStyle(cellStyle2);
            cell = row.createCell(3);
            cell.setCellValue(result.get(rownum - 1).getTipoDocIdentReceptor());
            cell.setCellStyle(cellStyle2);
        }

        final String FILE_NAME = "./xssf_example.xlsx";
        FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
        workbook.write(outputStream);

        byte[] b = Files.readAllBytes(Paths.get(FILE_NAME));

        return new ByteArrayInputStream(b);

        // return  (getPdfComprobantePorTipoFormato(comprobante,companyEntity,TipoPdfEnum.A4.getTipo()));

    }

    @Override
    public ByteArrayInputStream exportAllData(String ruc) throws Exception {
        String[] colums = {"Id","Serie","Numero","Estado"};
        Workbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("Facturas");
        Row row = sheet.createRow(0);

        for (int i = 0; i < colums.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(colums[i]);
        }

        List<PaymentVoucherEntity> comprobantes = paymentVoucherRepository.findAllByRuc(ruc);
        int initRow = 1;
        for(PaymentVoucherEntity comprobante : comprobantes){
            row = sheet.createRow(initRow);
            row.createCell(0).setCellValue(comprobante.getIdPaymentVoucher());
            row.createCell(1).setCellValue(comprobante.getSerie());
            row.createCell(2).setCellValue(comprobante.getNumero());
            row.createCell(3).setCellValue(comprobante.getEstado());

            initRow++;
        }

        workbook.write(stream);
        workbook.close();
        return new ByteArrayInputStream(stream.toByteArray());

    }


    public ByteArrayInputStream getPdfComprobanteOtherCpe(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException {

        OtherCpeEntity comprobante = otherCpeRepository.findByNumeroDocumentoIdentidadEmisorAndTipoComprobanteAndSerieAndNumero(ruc, tipo, serie, numero);
        if (comprobante == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        CompanyEntity companyEntity = companyRepository.findByRuc(comprobante.getNumeroDocumentoIdentidadEmisor());

        return new ByteArrayInputStream(getPdfOtherCpeByte(comprobante, companyEntity));

    }

    @Override
    public ByteArrayInputStream getPdfComprobanteTicket(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException {
        PaymentVoucherEntity comprobante = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(ruc, tipo, serie, numero);
        if (comprobante == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        CompanyEntity companyEntity = companyRepository.findByRuc(comprobante.getRucEmisor());

        return new ByteArrayInputStream(getPdfComprobantePorTipoFormato(comprobante, companyEntity, TipoPdfEnum.TICKET.getTipo()));

    }

    @Override
    public ByteArrayInputStream getPdfComprobanteGuia(String ruc, String serie, Integer numero, String tipo) throws ServiceException, QRGenerationException, ParseException {
        GuiaRemisionEntity guia = guiaRemisionRepository.findByNumeroDocumentoIdentidadRemitenteAndSerieAndNumero(ruc, serie, numero);
        if (guia == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        CompanyEntity companyEntity = companyRepository.findByRuc(guia.getNumeroDocumentoIdentidadRemitente());

        return new ByteArrayInputStream(getPdfComprobanteGuia(guia, companyEntity, tipo));

    }

    @Override
    @Transactional
    public ByteArrayInputStream getPdfComprobanteA4Publico(String ruc, String tipo, String serie, Integer numero, String fecha, BigDecimal monto) throws ServiceException, QRGenerationException, ParseException {

        PaymentVoucherEntity comprobante = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(ruc, tipo, serie, numero);
        if (comprobante == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        if (!fecha.equals(comprobante.getFechaEmision()))
            throw new ServiceException("El comprobante que desea descargar no existe.");

        if (monto.toBigInteger().compareTo(comprobante.getMontoImporteTotalVenta().toBigInteger()) != 0) {
            throw new ServiceException("El comprobante que desea descargar no existe.");
        }

        return getPdfComprobanteA4(ruc, tipo, serie, numero);

    }

    public byte[] getPdfComprobantePorTipoFormato(PaymentVoucherEntity comprobante, CompanyEntity companyEntity, String tipoPdf) throws ServiceException, QRGenerationException, ParseException {

        formatDecimalPaymentVoucher(comprobante);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", otherSymbols);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> params = new HashMap<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateVencimiento = null;
        if (comprobante.getFechaVencimiento() != null && !comprobante.getFechaVencimiento().isEmpty()) {
            dateVencimiento = format.parse(comprobante.getFechaVencimiento());
        }
        params.put("tipoComprobante", comprobante.getTipoComprobante());
        params.put("nombreComprobante", StringsUtils.getNombreTipoComprobante(comprobante.getTipoComprobante()));
        params.put("nombreComercial", companyEntity.getNombreComercial());
        params.put("razonSocial", companyEntity.getRazonSocial());
        params.put("telefono", companyEntity.getTelefono());
        params.put("email", companyEntity.getEmail());
        params.put("ruc", comprobante.getRucEmisor());
        params.put("direccion", companyEntity.getDireccion());
        params.put("ciudad", "");
        params.put("porcentajeigv", new BigDecimal("18.00"));
        params.put("serie", comprobante.getSerie());
        params.put("numero", comprobante.getNumero());
        params.put("serieCorrelativo", String.format("%s - %s", comprobante.getSerie(), comprobante.getNumero().toString()));

        params.put("numDocCliente", comprobante.getNumDocIdentReceptor());
        if(comprobante.getTipoComprobante().equals("01") && comprobante.getTipoTransaccion()!=null){
            params.put("tipoTransaccion", this.mapTransaccion.get(comprobante.getTipoTransaccion().intValue()));
        }

        if (comprobante.getTipoDocIdentReceptor() != null)
            params.put("tipoDocCliente", StringsUtils.getNombreTipoDocumentoReceptor(comprobante.getTipoDocIdentReceptor()));
        params.put("codigoTipoDocumentoIdentidad", comprobante.getTipoDocIdentReceptor());
        params.put("nombreCliente", comprobante.getDenominacionReceptor());
        params.put("moneda", comprobante.getCodigoMoneda());
        params.put("direccionCliente", comprobante.getDireccionReceptor());

        params.put("cajero", "");
        params.put("fechaDate", comprobante.getFechaEmisionDate());
        params.put("fecha", comprobante.getFechaEmision());
        params.put("hora", comprobante.getHoraEmision());

        params.put("puntoVenta", comprobante.getOficina() != null ? (comprobante.getOficina().getNombreCorto()!=null?
                comprobante.getOficina().getNombreCorto():""):"");
        String stringMoneda = comprobante.getCodigoMoneda() != null ? comprobante.getCodigoMoneda().equalsIgnoreCase("USD") ? "Dólares Americanos" : comprobante.getCodigoMoneda().equalsIgnoreCase("EUR") ? "Euros" :"Soles" : "Soles";

        String montoLetras = comprobante.getMontoImporteTotalVenta() != null ? GenerateLetraNumber.Convertir(comprobante.getMontoImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), stringMoneda, true) : "";
        params.put("montoLetras", montoLetras);
        params.put("gratuita", comprobante.getTotalValorVentaOperacionGratuita() != null ? df.format(comprobante.getTotalValorVentaOperacionGratuita()) : "0.00");
        params.put("exonerada", comprobante.getTotalValorVentaOperacionExonerada() != null ? df.format(comprobante.getTotalValorVentaOperacionExonerada()) : "0.00");
        params.put("inafecta", comprobante.getTotalValorVentaOperacionInafecta() != null ? df.format(comprobante.getTotalValorVentaOperacionInafecta()) : "0.00");
        params.put("gravada", comprobante.getTotalValorVentaOperacionGravada() != null ? df.format(comprobante.getTotalValorVentaOperacionGravada()) : "0.00");

        System.out.println("444");
        params.put("isc", comprobante.getSumatoriaISC() != null ? df.format(comprobante.getSumatoriaISC()) : "0.00");
        params.put("descuento", comprobante.getTotalDescuento() != null ? df.format(comprobante.getTotalDescuento()) : "0.00");
        params.put("subtotalpagar", df.format(
                (comprobante.getMontoImporteTotalVenta()!= null ?comprobante.getMontoImporteTotalVenta():BigDecimal.ZERO).doubleValue() -
                        (comprobante.getTotalDescuento() != null ? comprobante.getTotalDescuento() : BigDecimal.ZERO ).doubleValue() -
                        (comprobante.getSumatoriaIGV() != null ? comprobante.getSumatoriaIGV() : BigDecimal.ZERO).doubleValue()
            )
        );
        params.put("totalPagar", comprobante.getMontoImporteTotalVenta() != null ?
                df.format(comprobante.getMontoImporteTotalVenta().doubleValue() +
                                (comprobante.getMontoSumatorioOtrosCargos() != null ?
                                        comprobante.getMontoSumatorioOtrosCargos() : BigDecimal.ZERO).doubleValue()
                        ) : null);
        params.put("subtotalDetraccion", comprobante.getMontoImporteTotalVenta() != null ?
                df.format(comprobante.getMontoImporteTotalVenta().subtract(comprobante.getMontoDetraccion()!=null?comprobante.getMontoDetraccion():BigDecimal.ZERO)) : "0.00");
        params.put("motivoemision", comprobante.getMotivoNota());
        List<AditionalFieldEntity> adicionales = new ArrayList<>();
        for (AditionalFieldEntity bean : comprobante.getAditionalFields()) {
            AditionalFieldEntity result = new AditionalFieldEntity();
            result.setValorCampo(bean.getValorCampo());
            result.setNombreCampo(bean.getTypeField().getName());
            adicionales.add(result);
        }

        List<PaymentVoucherCuotaString> cuotas = new ArrayList<>();
        for (CuotasPaymentVoucherEntity cuota : comprobante.getCuotas()) {
            PaymentVoucherCuotaString cuo = new PaymentVoucherCuotaString();
            cuo.setNumero(cuota.getNumero()==null?"":cuota.getNumero().toString());
            cuo.setFecha(cuota.getFecha()==null?"":cuota.getFecha());
            cuo.setMonto(cuota.getMonto()==null?"":df.format(cuota.getMonto()));
            cuotas.add(cuo);
        }
        params.put("aditionalFields", adicionales);
        params.put("cuotasList", cuotas);
        params.put("detraccion", comprobante.getDetraccion() != null ? comprobante.getDetraccion(): "N");
        params.put("montoDetraccion", comprobante.getMontoDetraccion() != null ? df.format(comprobante.getMontoDetraccion()): "0.00");
        params.put("montoPendiente", comprobante.getMontoPendiente() != null ? df.format(comprobante.getMontoPendiente()): "0.00");
        params.put("ocargos", comprobante.getMontoSumatorioOtrosCargos() != null ? df.format(comprobante.getMontoSumatorioOtrosCargos()) : "0.00");
        params.put("isProduction", isProduction);
        params.put("ordenCompra", comprobante.getOrdenCompra());
        params.put("fechaVencimiento", comprobante.getFechaVencimiento());
        params.put("fechaVencimientoDate", dateVencimiento);
        params.put("estado_sunat", comprobante.getEstadoSunat());
        params.put("mensajeSunat", comprobante.getMensajeRespuesta());
        params.put("porcentajeDetraccion", comprobante.getPorcentajeDetraccion() != null ? comprobante.getPorcentajeDetraccion().toString() : "10");
        params.put("codigoBienDetraccion", comprobante.getCodigoBienDetraccion());
        params.put("codigoProducto", companyEntity.getViewCode() == null ? false : companyEntity.getViewCode());
        params.put("motivoNota", comprobante.getMotivoNota() == null ? "" : comprobante.getMotivoNota());
        params.put("guiasRemision", comprobante.getGuiasRelacionadas());
        params.put("codigoHash", comprobante.getCodigoHash());
        params.put("totalImpuestoOpGrat", comprobante.getSumatoriaTributosOperacionGratuita() != null ? df.format(comprobante.getSumatoriaTributosOperacionGratuita()) : "0.00");
        params.put("codigoTipoNotaCredito", comprobante.getCodigoTipoNotaCredito());
        params.put("codigoTipoNotaDebito", comprobante.getCodigoTipoNotaDebito());
        if (comprobante.getCodigoTipoNotaCredito() != null)
            params.put("motivoTipoNotaCredito", StringsUtils.getTipoNotaCredito(comprobante.getCodigoTipoNotaCredito()));
        if (comprobante.getCodigoTipoNotaDebito() != null)
            params.put("motivoTipoNotaDebito", StringsUtils.getTipoNotaDebito(comprobante.getCodigoTipoNotaDebito()));

        List<String> cuentas = new ArrayList<>();
        if (companyEntity.getCuentas() != null && !companyEntity.getCuentas().isEmpty()) {
            cuentas.addAll(companyEntity.getCuentas().stream()
                    .filter(item -> item.getDetraccion() != null && item.getDetraccion())
                    .map(cu -> cu.getName() + "-" + cu.getNumber())
                    .collect(Collectors.toList()));
        }
        params.put("numeroCuenta", String.join(", ", cuentas));
        List<CuentaEntity> cuentasNoD = new ArrayList<>();
        if (companyEntity.getCuentas() != null && !companyEntity.getCuentas().isEmpty()) {
            cuentasNoD.addAll(companyEntity.getCuentas().stream()
                    .filter(item -> (item.getDetraccion() == null || !item.getDetraccion()))
                    .collect(Collectors.toList()));
        }
        params.put("numeroCuentaNoD", cuentasNoD);
        params.put("numeroCuentaTodas", companyEntity.getCuentas());
        Map<String, String> aditionalMap = new HashMap<>();
        for (AditionalFieldEntity adit : comprobante.getAditionalFields()) {
            aditionalMap.put(adit.getTypeField().getName(), adit.getValorCampo());
        }

        String listGuiasRemisionRemitente = "";
        for (GuiaRelacionadaEntity guia : comprobante.getGuiasRelacionadas()) {
            aditionalMap.put(StringsUtils.getNombreTipoComprobante(guia.getCodigoTipoGuia()), guia.getSerieNumeroGuia());
            listGuiasRemisionRemitente = listGuiasRemisionRemitente + guia.getSerieNumeroGuia() + " , ";
        }

        params.put("aditionalMap", aditionalMap);
        if (listGuiasRemisionRemitente != null && listGuiasRemisionRemitente.length() > 0) {
            params.put("listGuiasRemisionRemitente", listGuiasRemisionRemitente.substring(0, listGuiasRemisionRemitente.length() - 2));
        }
        if (companyEntity.getArchivoLogo() != null) {
            params.put("urlImage", String.format("%s%s", urlstorageURL, companyEntity.getArchivoLogo().getIdRegisterFileSend().toString()));
        }

        if (comprobante.getTipoComprobanteAfectado() != null && comprobante.getSerieAfectado() != null) {
            String documentoAfectado = StringsUtils.getNombreTipoComprobanteNoElectro(comprobante.getTipoComprobanteAfectado()) + " " + comprobante.getSerieAfectado() + "-" + comprobante.getNumeroAfectado();
            params.put("documentoAfectado", documentoAfectado);
            params.put("serieAfectado", comprobante.getSerieAfectado());
            params.put("numeroAfectado", comprobante.getNumeroAfectado());

        }

        File fileTemp = null;
        try {
            fileTemp = File.createTempFile("temp", ".png");
        } catch (IOException ex) {
            throw new QRGenerationException(ex.getMessage());
        }
        File fileTempGenerated = QR.generateQR(fileTemp, String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s", comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString(),
                (comprobante.getSumatoriaIGV() == null ? "0.00" : comprobante.getSumatoriaIGV().toString()), comprobante.getMontoImporteTotalVenta(), comprobante.getFechaEmision(), (comprobante.getTipoDocIdentReceptor() == null ? "" : comprobante.getTipoDocIdentReceptor()),
                comprobante.getNumDocIdentReceptor()), 300, 300);
        params.put("qr", fileTempGenerated.getAbsoluteFile().toString());

        List<InvoicePrintLine> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        System.out.print("----------------------------------------------------------------------------");
        if (companyEntity.getCantComprobanteDinamico() != null) {
            int totalMax;
            if (companyEntity.getCantComprobanteDinamico() > comprobante.getDetailsPaymentVouchers().size()) {
                totalMax = companyEntity.getCantComprobanteDinamico();
            } else {
                totalMax = comprobante.getDetailsPaymentVouchers().size();
            }
            for (int i = 0; i < totalMax; i++) {
                if (comprobante.getDetailsPaymentVouchers().size() > i) {
                    InvoicePrintLine invoicePrintLine = new InvoicePrintLine();

                    String codigo = comprobante.getDetailsPaymentVouchers().get(i).getCodigoProducto();
                    String codetypeigv = comprobante.getDetailsPaymentVouchers().get(i).getCodigoTipoAfectacionIGV();
                    String descripcion = comprobante.getDetailsPaymentVouchers().get(i).getDescripcion();
                    String codigoSunat = comprobante.getDetailsPaymentVouchers().get(i).getCodigoProductoSunat();
                    String unidadManejo = comprobante.getDetailsPaymentVouchers().get(i).getUnidadManejo();
                    String instruccionesEspeciales = comprobante.getDetailsPaymentVouchers().get(i).getInstruccionesEspeciales();
                    String marca = comprobante.getDetailsPaymentVouchers().get(i).getMarca();
                    String montoref = comprobante.getDetailsPaymentVouchers().get(i).getMontoBaseGratuito()!=null?comprobante.getDetailsPaymentVouchers().get(i).getMontoBaseGratuito().setScale(2, RoundingMode.HALF_UP).toString():"";
                    String codigoDescripcion = (codigo != null ? codigo : "") + " " + (descripcion != null ? descripcion : "");
                    invoicePrintLine.setNume(i + 1);
                    invoicePrintLine.setCodigo(codigo);
                    invoicePrintLine.setDescripcion(descripcion);
                    invoicePrintLine.setCodigoSunat(codigoSunat);
                    invoicePrintLine.setCodetypeigv(codetypeigv);
                    invoicePrintLine.setCodigoDescripcion(codigoDescripcion);
                    invoicePrintLine.setMontoReferencial(montoref);
                    if(unidadManejo != null){
                        invoicePrintLine.setUnidadManejo(unidadManejo);
                    }
                    if(instruccionesEspeciales != null){
                        invoicePrintLine.setInstruccionesEspeciales(instruccionesEspeciales);
                    }
                    if(marca != null){
                        invoicePrintLine.setMarca(marca);
                    }
                    invoicePrintLine.setCantidad(comprobante.getDetailsPaymentVouchers().get(i).getCantidad().toString());
                    invoicePrintLine.setDescuento(comprobante.getDetailsPaymentVouchers().get(i).getDescuento() != null ? df.format(comprobante.getDetailsPaymentVouchers().get(i).getDescuento()) : "0.00");
                    invoicePrintLine.setTotal(df.format(comprobante.getDetailsPaymentVouchers().get(i).getValorVenta()
                            .add(comprobante.getDetailsPaymentVouchers().get(i).getAfectacionIGV() == null ? (BigDecimal.ZERO) : comprobante.getDetailsPaymentVouchers().get(i).getAfectacionIGV()).setScale(2, BigDecimal.ROUND_HALF_UP)));

                    if (comprobante.getDetailsPaymentVouchers().get(i).getPrecioVentaUnitario() != null) {
                        invoicePrintLine.setPrecioUnitario(df.format(comprobante.getDetailsPaymentVouchers().get(i).getPrecioVentaUnitario()));

                    } else {
                        invoicePrintLine.setPrecioUnitario(invoicePrintLine.getTotal());
                    }


                    if (comprobante.getDetailsPaymentVouchers().get(i).getValorUnitario().compareTo(new BigDecimal("0.00")) > 0) {
                        invoicePrintLine.setPrecioUnitarioSinIGV(df.format(comprobante.getDetailsPaymentVouchers().get(i).getValorUnitario()));

                    } else {
                        if (comprobante.getDetailsPaymentVouchers().get(i).getValorReferencialUnitario() != null) {
                            invoicePrintLine.setPrecioUnitarioSinIGV(df.format(comprobante.getDetailsPaymentVouchers().get(i).getValorReferencialUnitario()));
                        } else {
                            invoicePrintLine.setPrecioUnitarioSinIGV("0.00");
                        }
                    }

                    invoicePrintLine.setTotalSinIGV(df.format(comprobante.getDetailsPaymentVouchers().get(i).getValorVenta()));

                    if(Integer.parseInt(comprobante.getDetailsPaymentVouchers().get(i).getCodigoTipoAfectacionIGV()) != 21 && Integer.parseInt(comprobante.getDetailsPaymentVouchers().get(i).getCodigoTipoAfectacionIGV()) != 13){
                        subtotal = subtotal.add(comprobante.getDetailsPaymentVouchers().get(i).getValorVenta());
                    }

                    invoicePrintLine.setUnidad(comprobante.getDetailsPaymentVouchers().get(i).getCodigoUnidadMedida());

                    UnitCode unitCode = unitCodeRepository.findByCode(comprobante.getDetailsPaymentVouchers().get(i).getCodigoUnidadMedida());
                    invoicePrintLine.setUnidadNombre(unitCode != null ? unitCode.getDescription() : comprobante.getDetailsPaymentVouchers().get(i).getCodigoUnidadMedida());


                    items.add(invoicePrintLine);
                } else {
                    items.add(null);
                }
            }
        } else {
            Integer i = 1;
            for (DetailsPaymentVoucherEntity item : comprobante.getDetailsPaymentVouchers()) {
                InvoicePrintLine invoicePrintLine = new InvoicePrintLine();
                String codigo = item.getCodigoProducto();
                String descripcion = item.getDescripcion();
                String codigoDescripcion = (codigo != null ? codigo : "") + " " + (descripcion != null ? descripcion : "");
                invoicePrintLine.setNume(i);
                invoicePrintLine.setCodigo(codigo);
                invoicePrintLine.setDescripcion(item.getDescripcion());
                invoicePrintLine.setCodigoDescripcion(codigoDescripcion);
                invoicePrintLine.setCantidad(item.getCantidad().toString());
                invoicePrintLine.setCodetypeigv(item.getCodigoTipoAfectacionIGV());
                invoicePrintLine.setDescuento(item.getDescuento() != null ? df.format(item.getDescuento()) : "0.00");
                invoicePrintLine.setTotal(df.format(item.getValorVenta().add(item.getAfectacionIGV() == null ?
                        (new BigDecimal(0)) : item.getAfectacionIGV()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                if (item.getPrecioVentaUnitario() == null)
                    invoicePrintLine.setPrecioUnitario(invoicePrintLine.getTotal());
                else
                    invoicePrintLine.setPrecioUnitario(df.format(item.getPrecioVentaUnitario()));
                invoicePrintLine.setPrecioUnitarioSinIGV(df.format(item.getValorUnitario()));
                invoicePrintLine.setTotalSinIGV(df.format(item.getValorVenta()));

                if(item.getCodigoTipoAfectacionIGV()!=null){
                    if(Integer.parseInt(item.getCodigoTipoAfectacionIGV()) != 21 && Integer.parseInt(item.getCodigoTipoAfectacionIGV()) != 13){
                        subtotal = subtotal.add(item.getValorVenta());
                    }
                }


                invoicePrintLine.setUnidad(item.getCodigoUnidadMedida());

                UnitCode unitCode = unitCodeRepository.findByCode(item.getCodigoUnidadMedida());
                invoicePrintLine.setUnidadNombre(unitCode != null ? unitCode.getDescription() : item.getCodigoUnidadMedida());
                invoicePrintLine.setCodetypeigv(item.getCodigoTipoAfectacionIGV());
                invoicePrintLine.setCodigo(item.getCodigoProducto());
                items.add(invoicePrintLine);
                i++;
            }
        }

        BigDecimal subtotalDescuento = (comprobante.getTotalValorVentaOperacionGravada() != null ?
                (comprobante.getTotalValorVentaOperacionGravada()) : BigDecimal.ZERO).subtract(comprobante.getTotalDescuento() != null
                ? comprobante.getTotalDescuento() : BigDecimal.ZERO);
        BigDecimal subtotalConDescuento = (comprobante.getTotalValorVentaOperacionGravada() != null ?
                (comprobante.getTotalValorVentaOperacionGravada()) : BigDecimal.ZERO).add(comprobante.getTotalDescuento() != null
                ? comprobante.getTotalDescuento() : BigDecimal.ZERO);

        params.put("subtotalDescuento", df.format(subtotalDescuento).toString());
        params.put("subtotalConDescuento", df.format(subtotalConDescuento).toString());
        BigDecimal decimalsum = new BigDecimal(0);
        if (comprobante.getAnticipos() != null && !comprobante.getAnticipos().isEmpty()) {
            AtomicReference<BigDecimal> sumatoriaAnticipos = new AtomicReference<>();
            sumatoriaAnticipos.set(BigDecimal.ZERO);
            comprobante.getAnticipos().forEach(ant -> {
                InvoicePrintLine invoicePrintLine = new InvoicePrintLine();
                String textAnticipo = String.format("%s : %s %s-%s", "Anticipo", StringsUtils.getNombreTipoComprobanteResumido(ant.getTipoDocumentoAnticipo()), ant.getSerieAnticipo(), ant.getNumeroAnticipo().toString());
                PaymentVoucherEntity compAnticipo = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(comprobante.getRucEmisor(), ant.getTipoDocumentoAnticipo().equals(Catalogo12.FACTURA_ANTICIPOS.getCodigo()) ? "01" : "03", ant.getSerieAnticipo(), ant.getNumeroAnticipo());
                invoicePrintLine.setDescripcion(compAnticipo != null ? textAnticipo + ", con fecha: (" + compAnticipo.getFechaEmision() + ")" : textAnticipo);
                invoicePrintLine.setCantidad("");
                invoicePrintLine.setPrecioUnitarioSinIGV(null);
                invoicePrintLine.setTotal("-" + df.format(ant.getMontoAnticipo()));

                invoicePrintLine.setTotalSinIGV("-" + df.format(ant.getMontoAnticipo().divide(new BigDecimal(1.18),2,RoundingMode.HALF_UP)));
                OptionalInt index = IntStream.range(0, items.size())
                        .filter(userInd -> items.get(userInd) == null)
                        .findFirst();
                if (index.isPresent()) {
                    items.set(index.getAsInt(), invoicePrintLine);
                } else {
                    items.add(invoicePrintLine);
                }

                sumatoriaAnticipos.set(sumatoriaAnticipos.get().add(ant.getMontoAnticipo()));

            });
            decimalsum = sumatoriaAnticipos.get().divide(new BigDecimal(1.18),2,RoundingMode.HALF_UP);
            params.put("anticipo", df.format(sumatoriaAnticipos.get()));
        }
        if(decimalsum.compareTo(BigDecimal.ZERO)==1){
            subtotal = subtotal.subtract(decimalsum);
        }

        params.put("igv", comprobante.getSumatoriaIGV() != null ? df.format(comprobante.getSumatoriaIGV().doubleValue() +
                ((comprobante.getMontoSumatorioOtrosCargos() != null ?
                        comprobante.getMontoSumatorioOtrosCargos() : BigDecimal.ZERO).doubleValue() * 0.18)
        ) : "0.00");
        params.put("subtotal", subtotal.toString());
        params.put("ineto", String.format("%.2f",subtotal.doubleValue()-(comprobante.getTotalDescuento()==null?0:comprobante.getTotalDescuento().doubleValue())));

        params.put("listProducts", items);
        params.put("sexo", "Masculino");

        try {

            InputStream jasperStream = getJasperTemplateFromStorage(tipoPdf, companyEntity.getRuc(), companyEntity.getFormat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrio un error al generar el PDF");
        }
    }

    public byte[] getPdfOtherCpeByte(OtherCpeEntity comprobante, CompanyEntity companyEntity) throws ServiceException, QRGenerationException, ParseException {

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", otherSymbols);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> params = new HashMap<>();

        params.put("tipoComprobante", comprobante.getTipoComprobante());
        params.put("nombreComprobante", StringsUtils.getNombreTipoComprobante(comprobante.getTipoComprobante()));
        params.put("nombreComercial", companyEntity.getNombreComercial());
        params.put("razonSocial", companyEntity.getRazonSocial());
        params.put("telefono", companyEntity.getTelefono());
        params.put("email", companyEntity.getEmail());
        params.put("ruc", comprobante.getNumeroDocumentoIdentidadEmisor());
        params.put("direccion", companyEntity.getDireccion());
        params.put("ciudad", "");
        params.put("porcentajeigv", new BigDecimal("18.00"));
        params.put("serie", comprobante.getSerie());
        params.put("numero", comprobante.getNumero());
        params.put("serieCorrelativo", String.format("%s - %s", comprobante.getSerie(), comprobante.getNumero().toString()));

        params.put("numDocCliente", comprobante.getNumeroDocumentoIdentidadReceptor());
        if (comprobante.getTipoDocumentoIdentidadReceptor() != null)
            params.put("tipoDocCliente", StringsUtils.getNombreTipoDocumentoReceptor(comprobante.getTipoDocumentoIdentidadReceptor()));
        params.put("codigoTipoDocumentoIdentidad", comprobante.getTipoDocumentoIdentidadReceptor());
        params.put("nombreCliente", comprobante.getDenominacionReceptor());
        params.put("moneda", comprobante.getCodigoMoneda());
        params.put("direccionCliente", comprobante.getDireccionCompletaDomicilioFiscalReceptor());

        params.put("cajero", "");
        params.put("fecha", comprobante.getFechaEmision());
        Date fechaDate = UtilFormat.fechaDate(comprobante.getFechaEmision());
        params.put("fechaDate", fechaDate);
        params.put("hora", comprobante.getHoraEmision());
        params.put("puntoVenta", "PUNTO DE VENTA: ");
        String stringMoneda = comprobante.getCodigoMoneda() != null ? comprobante.getCodigoMoneda().equalsIgnoreCase("USD") ? "Dólares Americanos" : comprobante.getCodigoMoneda().equalsIgnoreCase("EUR") ? "EUROS" : "Soles" : "Soles";
        params.put("importeTotalRetenido", comprobante.getImporteTotalRetenidoPercibido() != null ? df.format(comprobante.getImporteTotalRetenidoPercibido()) : "0.00");
        params.put("importeTotalPagado", comprobante.getImporteTotalPagadoCobrado() != null ? df.format(comprobante.getImporteTotalPagadoCobrado()) : "0.00");

        params.put("regimenRetencion", "TASA 3%");

        params.put("isProduction", isProduction);

        params.put("estado_sunat", comprobante.getEstadoEnSunat());
        params.put("mensajeSunat", comprobante.getMensajeRespuesta());

        List<CuentaEntity> cuentasNoD = new ArrayList<>();
        if (companyEntity.getCuentas() != null && !companyEntity.getCuentas().isEmpty()) {
            cuentasNoD.addAll(companyEntity.getCuentas().stream()
                    .filter(item -> (item.getDetraccion() == null || !item.getDetraccion()))
                    .collect(Collectors.toList()));
        }
        params.put("numeroCuentaNoD", cuentasNoD);
        params.put("numeroCuentaTodas", companyEntity.getCuentas());

        if (companyEntity.getArchivoLogo() != null) {
            params.put("urlImage", String.format("%s%s", urlstorageURL, companyEntity.getArchivoLogo().getIdRegisterFileSend().toString()));
        }

        File fileTemp = null;
        try {
            fileTemp = File.createTempFile("temp", ".png");
        } catch (IOException ex) {
            throw new QRGenerationException(ex.getMessage());
        }
        File fileTempGenerated = QR.generateQR(fileTemp, String.format("%s | %s | %s | %s", comprobante.getNumeroDocumentoIdentidadEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString()), 300, 300);
        params.put("qr", fileTempGenerated.getAbsoluteFile().toString());

        List<OtherCpePrintLine> items = new ArrayList<>();

        if (companyEntity.getCantComprobanteDinamico() != null) {
            int totalMax;
            if (companyEntity.getCantComprobanteDinamico() > comprobante.getDetails().size()) {
                totalMax = companyEntity.getCantComprobanteDinamico();
            } else {
                totalMax = comprobante.getDetails().size();
            }
            for (int i = 0; i < totalMax; i++) {
                if (comprobante.getDetails().size() > i) {
                    OtherCpePrintLine invoicePrintLine = new OtherCpePrintLine();

                    String tipoDoc = comprobante.getDetails().get(i).getTipoDocumentoRelacionado();
                    String serie = comprobante.getDetails().get(i).getSerieDocumentoRelacionado();
                    String numero = comprobante.getDetails().get(i).getNumeroDocumentoRelacionado().toString();
                    String fecha = comprobante.getDetails().get(i).getFechaEmisionDocumentoRelacionado();
                    String importeRetenido = comprobante.getDetails().get(i).getImporteRetenidoPercibido() != null ? comprobante.getDetails().get(i).getImporteRetenidoPercibido().toString() : "0.00";
                    String importePagado = comprobante.getDetails().get(i).getImporteTotalToPagarCobrar() != null ? comprobante.getDetails().get(i).getImporteTotalToPagarCobrar().toString() : "0.00";

                    String importeTotal = comprobante.getDetails().get(i).getImporteTotalDocumentoRelacionado() != null ? comprobante.getDetails().get(i).getImporteTotalDocumentoRelacionado().toString() : "0.00";

                    String numeroPago = comprobante.getDetails().get(i).getNumeroPagoCobro() != null ? comprobante.getDetails().get(i).getNumeroPagoCobro().toString() : "0";
                    String tasa = "3.00 %";

                    String documento = StringsUtils.getNombreTipoComprobante(tipoDoc) + " " + serie + "-" + numero;

                    invoicePrintLine.setDocumento(documento);
                    invoicePrintLine.setFecha(fecha);
                    invoicePrintLine.setImportePagado(importePagado);
                    invoicePrintLine.setImporteRetenido(importeRetenido);
                    invoicePrintLine.setImporteTotal(importeTotal);
                    invoicePrintLine.setNumeroPago(numeroPago);
                    invoicePrintLine.setTasa(tasa);
                    invoicePrintLine.setTipoDoc(tipoDoc);
                    invoicePrintLine.setSerie(serie);
                    invoicePrintLine.setNumero(numero);
                    items.add(invoicePrintLine);
                } else {
                    items.add(null);
                }
            }
        } else {
            Integer i = 1;
            for (DetailOtherCpeEntity item : comprobante.getDetails()) {
                OtherCpePrintLine invoicePrintLine = new OtherCpePrintLine();

                String tipoDoc = item.getTipoDocumentoRelacionado();
                String serie = item.getSerieDocumentoRelacionado();
                String numero = item.getNumeroDocumentoRelacionado().toString();
                String fecha = item.getFechaEmisionDocumentoRelacionado();
                String importeRetenido = item.getImporteRetenidoPercibido() != null ? item.getImporteRetenidoPercibido().toString() : "0.00";
                String importePagado = item.getImporteTotalToPagarCobrar() != null ? item.getImporteTotalToPagarCobrar().toString() : "0.00";
                String documento = StringsUtils.getNombreTipoComprobante(tipoDoc) + " " + serie + "-" + numero;


                invoicePrintLine.setDocumento(documento);
                invoicePrintLine.setFecha(fecha);
                invoicePrintLine.setImportePagado(importePagado);
                invoicePrintLine.setImporteRetenido(importeRetenido);

                items.add(invoicePrintLine);
                i++;
            }
        }

        params.put("listProducts", items);

        try {

            InputStream jasperStream = getJasperTemplateFromStorage(TipoPdfEnum.OTHER_CPE.getTipo(), companyEntity.getRuc(), companyEntity.getFormat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrio un error al generar el PDF");
        }
    }

    public byte[] getPdfComprobanteGuia(GuiaRemisionEntity guia, CompanyEntity companyEntity, String tipoPdf) throws ServiceException, QRGenerationException, ParseException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> params = new HashMap<>();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", otherSymbols);
        String brevete="";
        String ordenCompra="";
        int index = 0;

        try {
            //Parametros para el Jasper
            params.put("direccion", companyEntity.getDireccion());
            params.put("razonSocial", companyEntity.getRazonSocial());
            params.put("nombreComercial", companyEntity.getNombreComercial());
            params.put("telefono", companyEntity.getTelefono());
            params.put("email", companyEntity.getEmail());
            params.put("ruc", companyEntity.getRuc());
            if (companyEntity.getArchivoLogo() != null) {
                params.put("urlImage", String.format("%s%s", urlstorageURL, companyEntity.getArchivoLogo().getIdRegisterFileSend().toString()));
            }
            params.put("serie", guia.getSerie());
            params.put("numero", guia.getNumero());
            params.put("fechaEmision", guia.getFechaEmisionDate());
            params.put("tipoComprobante", "GUÍA DE REMISIÓN ELECTRÓNICA - REMITENTE");
            params.put("observaciones", guia.getObservacionesGuia());
            params.put("serieBaja", guia.getSerieBaja());
            params.put("numeroBaja", guia.getNumeroBaja());
            params.put("comprobanteBaja", guia.getTipoComprobanteBaja());
            params.put("descripcionComprobanteBaja", guia.getDescripcionComprobanteBaja());
            params.put("identificadorDocumentoRelacionado", guia.getIdentificadorDocumentoRelacionado());
            params.put("codigoTipoDocumentoRelacionado", guia.getCodigoTipoDocumentoRelacionado());
            params.put("numeroDocumentoIdentidadRemitente", guia.getNumeroDocumentoIdentidadRemitente());
            params.put("tipoDocumentoIdentidadRemitente", guia.getTipoDocumentoIdentidadRemitente());
            params.put("denominacionRemitente", guia.getDenominacionRemitente());
            params.put("numeroDocumentoIdentidadDestinatario", guia.getNumeroDocumentoIdentidadDestinatario());
            params.put("tipoDocumentoIdentidadDestinatario", guia.getTipoDocumentoIdentidadDestinatario());
            params.put("denominacionDestinatario", guia.getDenominacionDestinatario());
            params.put("numeroDocumentoIdentidadTercero", guia.getNumeroDocumentoIdentidadTercero());
            params.put("tipoDocumentoIdentidadTercero", guia.getTipoDocumentoIdentidadTercero());
            params.put("denominacionTercero", guia.getDenominacionTercero());
            params.put("motivoTraslado", StringsUtils.getMotivoTraslado(guia.getMotivoTraslado()));
            params.put("descripcionMotivoTraslado", guia.getDescripcionMotivoTraslado());
            params.put("indicadorTransbordoProgramado", guia.getIndicadorTransbordoProgramado());
            if (guia.getPesoTotalBrutoBienes() != null)
                params.put("pesoTotalBrutoBienes", df.format(guia.getPesoTotalBrutoBienes()));
            params.put("unidadMedidaPesoBruto", guia.getUnidadMedidaPesoBruto());
            params.put("numeroBultos", guia.getNumeroBultos());
            params.put("ubigeoPuntoLlegada", guia.getUbigeoPuntoLlegada());

            DistritoEntity ubigeoLlegada = distritoRepository.findById(guia.getUbigeoPuntoLlegada()).get();
            params.put("ubigeoCadenaLlegada", ubigeoLlegada.getProvincia().getDepartamento().getDescripcion() + " - " + ubigeoLlegada.getProvincia().getDescripcion() + " - " + ubigeoLlegada.getDescripcion());
            params.put("direccionPuntoLlegada", guia.getDireccionPuntoLlegada());
            params.put("numeroContenedor", guia.getNumeroContenedor());
            params.put("ubigeoPuntoPartida", guia.getUbigeoPuntoPartida());

            DistritoEntity ubigeoPartida = distritoRepository.findById(guia.getUbigeoPuntoPartida()).get();
            params.put("ubigeoCadenaPartida", ubigeoPartida.getProvincia().getDepartamento().getDescripcion() + " - " + ubigeoPartida.getProvincia().getDescripcion() + " - " + ubigeoPartida.getDescripcion());
            params.put("direccionPuntoPartida", guia.getDireccionPuntoPartida());
            params.put("codigoPuerto", guia.getCodigoPuerto());
            params.put("identificadorDocumento", guia.getIdentificadorDocumento());
            params.put("estadoEnSunat", guia.getEstadoEnSunat());
            params.put("estado", guia.getEstado());
            params.put("estadoAnterior", guia.getEstadoAnterior());
            params.put("mensajeRespuesta", guia.getMensajeRespuesta());
            params.put("fechaRegistro", guia.getFechaRegistro());
            params.put("fechaModificacion", guia.getFechaModificacion());
            params.put("userName", guia.getUserName());
            params.put("userNameModify", guia.getUserNameModify());
            params.put("isProduction", isProduction);


            File fileTemp = null;
            try {
                fileTemp = File.createTempFile("temp", ".png");
            } catch (IOException ex) {
                throw new QRGenerationException(ex.getMessage());
            }
            File fileTempGenerated = QR.generateQR(fileTemp, String.format("%s | %s | %s | %s | %s | %s | %s | %s", companyEntity.getRuc(), guia.getTipoComprobante(), guia.getSerie(), guia.getNumero().toString(),
                    (guia.getPesoTotalBrutoBienes() == null ? "0.00" : guia.getPesoTotalBrutoBienes().toString()), guia.getFechaEmision(), (guia.getTipoDocumentoIdentidadDestinatario() == null ? "" : guia.getTipoDocumentoIdentidadDestinatario()),
                    guia.getNumeroDocumentoIdentidadDestinatario()), 300, 300);
            params.put("qr", fileTempGenerated.getAbsoluteFile().toString());

            List<Bienes> bienes = new ArrayList<>();
            for (DetailGuiaRemisionEntity item : guia.getDetailsGuiaRemision()) {
                index++;
                UnitCode unitCode = unitCodeRepository.findByCode(item.getUnidadMedida());
                Bienes result = new Bienes(item.getCantidad(), unitCode.getDescription(),
                        item.getDescripcion(), item.getCodigoItem(),item.getPeso(),index,item.getInstruccion_especial(),item.getNumero_serie());
                result.setCadena_cantidad(df.format(item.getCantidad()));
                bienes.add(result);
            }
            params.put("bienes", bienes);

            List<Campos> campos = new ArrayList<>();
            for (AditionalFieldGuiaEntity campo : guia.getAditionalFields()) {
                if (campo.getNombreCampo().equals("BREVETE")){
                    brevete=campo.getValorCampo();
                }else if (campo.getNombreCampo().equals("ORDENCOMPRA")){
                    ordenCompra=campo.getValorCampo();
                }
                else{
                    campos.add(new Campos(campo.getNombreCampo(),campo.getValorCampo()));
                }
            }
            params.put("campos", campos);
            params.put("ordenCompra", ordenCompra);
            List<Traslado> traslados = new ArrayList<>();
            for (TramoTrasladoEntity item : guia.getTramos()) {
                traslados.add(new Traslado(StringsUtils.getModalidadTraslado(item.getModalidadTraslado()), item.getFechaInicioTraslado(),
                        item.getNumeroDocumentoIdentidadTransportista(), StringsUtils.getNombreTipoDocumentoReceptor(item.getTipoDocumentoIdentidadTransportista()),
                        item.getDenominacionTransportista(), item.getNumeroPlacaVehiculo(), item.getNumeroDocumentoIdentidadConductor(),
                        StringsUtils.getNombreTipoDocumentoReceptor(item.getTipoDocumentoIdentidadConductor()),brevete));
            }

            params.put("tramos", traslados);

            InputStream jasperStream = getJasperTemplateFromStorage(tipoPdf, companyEntity.getRuc(), companyEntity.getFormat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrio un error al generar el PDF");
        }
    }


    @Override
    public ByteArrayInputStream getPdfComprobanteuid(Long idPaymentVoucher, String uid, String nameDocument, String tipoPdf) throws ServiceException, QRGenerationException, ServiceException, ParseException {

        PaymentVoucherInterDto comprobante = paymentVoucherRepository.findByIdPaymentVoucherAndUuid(idPaymentVoucher, uid);

        if (comprobante == null) {
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NULL");
        }

        if (!comprobante.getIdentificador().trim().equalsIgnoreCase(nameDocument))
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NAMEDOCUMENT");

        if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.TICKET.getTipo()))
            return getPdfComprobanteTicket(comprobante.getEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.A4.getTipo()))
            return getPdfComprobanteA4(comprobante.getEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else throw new ServiceException("COMPROBANTE NO ENCONTRADO TICKETA4");

    }

    @Override
    public ByteArrayInputStream getPdfComprobanteOtherCpeUuid(Long idother, String uuid, String nameDocument) throws ServiceException, QRGenerationException, ServiceException, ParseException {

        OtherCpeEntity comprobante = otherCpeRepository.findByIdOtroCPEAndUuid(idother, uuid);

        if (comprobante == null) {
            throw new ServiceException("COMPROBANTE NO ENCONTRADO");
        }

        if (!comprobante.getIdentificadorDocumento().trim().equalsIgnoreCase(nameDocument))
            throw new ServiceException("COMPROBANTE NO ENCONTRADO");

        return getPdfComprobanteOtherCpe(comprobante.getNumeroDocumentoIdentidadEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());

    }


    void formatDecimalPaymentVoucher(PaymentVoucherEntity voucher) {
        if (voucher.getMontoDescuentoGlobal() != null)
            voucher.setMontoDescuentoGlobal(voucher.getMontoDescuentoGlobal().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getMontoImporteTotalVenta() != null)
            voucher.setMontoImporteTotalVenta(voucher.getMontoImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getMontoSumatorioOtrosCargos() != null)
            voucher.setMontoSumatorioOtrosCargos(voucher.getMontoSumatorioOtrosCargos().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalDescuento() != null)
            voucher.setTotalDescuento(voucher.getTotalDescuento().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getSumatoriaIGV() != null)
            voucher.setSumatoriaIGV(voucher.getSumatoriaIGV().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getSumatoriaISC() != null)
            voucher.setSumatoriaISC(voucher.getSumatoriaISC().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getSumatoriaOtrosTributos() != null)
            voucher.setSumatoriaOtrosTributos(voucher.getSumatoriaOtrosTributos().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalValorVentaOperacionExonerada() != null)
            voucher.setTotalValorVentaOperacionExonerada(voucher.getTotalValorVentaOperacionExonerada().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalValorVentaOperacionExportada() != null)
            voucher.setTotalValorVentaOperacionExportada(voucher.getTotalValorVentaOperacionExportada().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalValorVentaOperacionGratuita() != null)
            voucher.setTotalValorVentaOperacionGratuita(voucher.getTotalValorVentaOperacionGratuita().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalValorVentaOperacionGravada() != null)
            voucher.setTotalValorVentaOperacionGravada(voucher.getTotalValorVentaOperacionGravada().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (voucher.getTotalValorVentaOperacionInafecta() != null)
            voucher.setTotalValorVentaOperacionInafecta(voucher.getTotalValorVentaOperacionInafecta().setScale(2, BigDecimal.ROUND_HALF_UP));
        voucher.getDetailsPaymentVouchers().forEach(line -> {
            if (line.getAfectacionIGV() != null){
                line.setAfectacionIGV(line.getAfectacionIGV().setScale(2, BigDecimal.ROUND_HALF_UP));
            }else{
                line.setAfectacionIGV(new BigDecimal(0));
            }

            if (line.getCantidad() != null)
                line.setCantidad(line.getCantidad().setScale(3, BigDecimal.ROUND_HALF_UP));
            if (line.getDescuento() != null)
                line.setDescuento(line.getDescuento().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getSistemaISC() != null)
                line.setSistemaISC(line.getSistemaISC().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getPrecioVentaUnitario() != null)
                line.setPrecioVentaUnitario(line.getPrecioVentaUnitario().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getValorReferencialUnitario() != null)
                line.setValorReferencialUnitario(line.getValorReferencialUnitario().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getValorUnitario() != null)
                line.setValorUnitario(line.getValorUnitario().setScale(2, BigDecimal.ROUND_HALF_UP));
            if (line.getValorVenta() != null)
                line.setValorVenta(line.getValorVenta().setScale(2, BigDecimal.ROUND_HALF_UP));
        });

    }

    @Override
    public ByteArrayInputStream getPdfGuiaUuid(Long idGuiaRemision, String uid, String nameDocument, String tipoPdf) throws ServiceException, QRGenerationException, ServiceException, ParseException {

        GuiaRemisionEntity guiaRemision = guiaRemisionRepository.findByidGuiaRemisionAndUuid(idGuiaRemision, uid);

        if (guiaRemision == null) {
            throw new ServiceException("GUIA REMISION NO ENCONTRADO");
        }

        if (!guiaRemision.getIdentificadorDocumento().trim().equalsIgnoreCase(nameDocument))
            throw new ServiceException("GUIA REMISION NO ENCONTRADO");

        if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.A4.getTipo()))
            return getPdfComprobanteGuia(guiaRemision.getNumeroDocumentoIdentidadRemitente(), guiaRemision.getSerie(), guiaRemision.getNumero(),TipoPdfEnum.GUIA.getTipo());
        else throw new ServiceException("COMPROBANTE NO ENCONTRADO");
    }


    InputStream getJasperTemplateFromStorage(String tipoPdf, String ruc, Integer format) {
        if (format == null) format = 1;
        String keyname = String.format("%s-%s.jrxml", ruc, tipoPdf);
        String keynameFormat = String.format("%s-%s-%s.jrxml", "certifaktinvoice", tipoPdf, format.toString());

        ByteArrayOutputStream bos = null;
        try {
            bos = amazonS3ClientService.downloadFile(keyname, "plantillas");
        } catch (Exception e) {
            bos = amazonS3ClientService.downloadFile(keynameFormat, "plantillas");
        }

        ByteArrayInputStream jrxmlIs = new ByteArrayInputStream(bos.toByteArray());
        ByteArrayOutputStream bosJasper = new ByteArrayOutputStream();

        try {
            JasperCompileManager.compileReportToStream(jrxmlIs, bosJasper);
            return new ByteArrayInputStream(bosJasper.toByteArray());
        } catch (JRException e) {
            e.printStackTrace();
            throw new ServiceException("Error en plantilla Jasper");
        }
    }


}
package pe.com.certifakt.apifact.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.enums.EstadoSunatEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.repository.ExcelRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.util.Map;


public class UtilExcelD {
    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private ExcelRepository ExcelRepository;


    public ByteArrayInputStream CustomDownloadExcel(UserPrincipal user, String fechaEmisionDesde, String fechaEmisionHasta, String tipoComprobante,
                                                    String serie, String Correo) throws IOException {


        String[] columns = {"Fecha Emisión", "Tipo", "Número",
                "Tipo documento", "Num. Documento", "Receptor", "Moneda",
                "Gravada", "Exonerada", "Inafecta", "IGV", "Descuento total",
                "Monto Total", "Estado", "Estado Sunat", "Otros tributos",
                "Gratuita", "Detracción", "Imp. Detracción",
                "Comp. afectado", "Codigo", "Descripcion", "Cantidad", "Valor unidad", "Valor venta",
                "Descuento"
        };

        Workbook workbook = new XSSFWorkbook();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row


        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("Lista De Comprobantes");

        if (tipoComprobante != null) {
            tipoComprobante = "%" + tipoComprobante + "%";
        }
        if (serie != null) {
            serie = "%" + serie + "%";
        }

        Row row = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        List<PaymentVoucherEntity> result = this.ExcelRepository.findAllSerchForExcel(user.getRuc(), fechaEmisionDesde,
                fechaEmisionHasta, "%" + tipoComprobante + "%", "%" + serie + "%");

        int initRow = 1;
        int detRow = 1;


        for (PaymentVoucherEntity paymentVoucherEntity : result) {
            row = sheet.createRow(initRow);
            row.createCell(0).setCellValue(paymentVoucherEntity.getFechaEmision());
            row.createCell(1).setCellValue(paymentVoucherEntity.getTipoComprobante());
            row.createCell(2).setCellValue(paymentVoucherEntity.getNumero());
            row.createCell(3).setCellValue(paymentVoucherEntity.getTipoDocIdentReceptor());
            row.createCell(4).setCellValue(paymentVoucherEntity.getRucEmisor());
            row.createCell(5).setCellValue(paymentVoucherEntity.getNumDocIdentReceptor());
            row.createCell(6).setCellValue(paymentVoucherEntity.getCodigoMoneda());
            row.createCell(7).setCellValue(paymentVoucherEntity.getTotalValorVentaOperacionGravada() == null ? "" : paymentVoucherEntity.getTotalValorVentaOperacionGravada().toString());
            row.createCell(8).setCellValue(paymentVoucherEntity.getTotalValorVentaOperacionExonerada() == null ? "" : paymentVoucherEntity.getTotalValorVentaOperacionExonerada().toString());
            row.createCell(9).setCellValue(paymentVoucherEntity.getTotalValorVentaOperacionInafecta() == null ? "" : paymentVoucherEntity.getTotalValorVentaOperacionInafecta().toString());
            row.createCell(10).setCellValue(paymentVoucherEntity.getSumatoriaIGV() == null ? "" : paymentVoucherEntity.getSumatoriaIGV().toString());
            row.createCell(11).setCellValue(paymentVoucherEntity.getTotalDescuento() == null ? "" : paymentVoucherEntity.getTotalDescuento().toString());
            row.createCell(12).setCellValue(paymentVoucherEntity.getMontoImporteTotalVenta() == null ? "" : paymentVoucherEntity.getMontoImporteTotalVenta().toString());
            row.createCell(13).setCellValue(paymentVoucherEntity.getEstado() == null ? "" : paymentVoucherEntity.getEstado().equals(EstadoComprobanteEnum.ACEPTADO.getCodigo()) ? "Aceptado" : "-");
            row.createCell(14).setCellValue(paymentVoucherEntity.getEstadoSunat() == null ? "" : paymentVoucherEntity.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()) ? "Aceptado" : "-");
            row.createCell(15).setCellValue(paymentVoucherEntity.getSumatoriaOtrosTributos() == null ? "" : paymentVoucherEntity.getSumatoriaOtrosTributos().toString());
            row.createCell(16).setCellValue(paymentVoucherEntity.getTotalValorVentaOperacionGratuita() == null ? "" : paymentVoucherEntity.getTotalValorVentaOperacionGratuita().toString());
            row.createCell(17).setCellValue(paymentVoucherEntity.getDetraccion() == null ? "" : paymentVoucherEntity.getDetraccion());
            row.createCell(18).setCellValue(paymentVoucherEntity.getPorcentajeDetraccion() == null ? "" : paymentVoucherEntity.getPorcentajeDetraccion().toString());
            row.createCell(19).setCellValue(paymentVoucherEntity.getTipoComprobanteAfectado() == null ? "" : paymentVoucherEntity.getTipoComprobanteAfectado());
            initRow++;

            for (DetailsPaymentVoucherEntity detailsPaymentVoucher : paymentVoucherEntity.getDetailsPaymentVouchers()) {
                row = sheet.createRow(detRow);
                row.createCell(20).setCellValue(detailsPaymentVoucher.getCodigoProducto());
                row.createCell(21).setCellValue(detailsPaymentVoucher.getDescripcion());
                row.createCell(22).setCellValue(detailsPaymentVoucher.getCantidad() == null ? "" : detailsPaymentVoucher.getCantidad().toString());
                row.createCell(23).setCellValue(detailsPaymentVoucher.getValorUnitario() == null ? "" : detailsPaymentVoucher.getValorUnitario().toString());
                row.createCell(24).setCellValue(detailsPaymentVoucher.getValorVenta() == null ? "" : detailsPaymentVoucher.getValorVenta().toString());
                row.createCell(25).setCellValue(detailsPaymentVoucher.getDescuento() == null ? "" : detailsPaymentVoucher.getDescuento().toString());
                detRow++;
            }
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        initRow++;


        workbook.write(stream);
        workbook.close();


        return new ByteArrayInputStream(stream.toByteArray());
    }

    public static ByteArrayInputStream generateZipFromExcel(String filename, byte[] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename);
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        zos.write(input);
        zos.closeEntry();
        zos.close();
        return new ByteArrayInputStream(baos.toByteArray());}



    private static CellStyle createBorderedStyle(Workbook wb){
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();

        CellStyle style = wb.createCellStyle();
        style.setBorderRight(thin);
        style.setRightBorderColor(black);
        style.setBorderBottom(thin);
        style.setBottomBorderColor(black);
        style.setBorderLeft(thin);
        style.setLeftBorderColor(black);
        style.setBorderTop(thin);
        style.setTopBorderColor(black);
        return style;
    }

    private static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<>();
        DataFormat df = wb.createDataFormat();

        CellStyle style;
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);
        styles.put("header", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("header_date", style);

        Font font1 = wb.createFont();
        font1.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font1);
        styles.put("cell_b", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font1);
        styles.put("cell_b_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font1);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_b_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_g", style);

        Font font2 = wb.createFont();
        font2.setColor(IndexedColors.BLUE.getIndex());
        font2.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font2);
        styles.put("cell_bb", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_bg", style);

        Font font3 = wb.createFont();
        font3.setFontHeightInPoints((short)14);
        font3.setColor(IndexedColors.DARK_BLUE.getIndex());
        font3.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font3);
        style.setWrapText(true);
        styles.put("cell_h", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(true);
        styles.put("cell_normal", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        styles.put("cell_normal_centered", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setWrapText(true);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_normal_date", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setIndention((short)1);
        style.setWrapText(true);
        styles.put("cell_indented", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("cell_blue", style);

        return styles;

    }


    public static ByteArrayInputStream generateExcelFromList(String nombreHoja, String[] columns, List<List<String>> data) {

        try {
            // Create a Workbook
            Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

            Map<String,CellStyle> styles = createStyles(workbook);

            // Create a Sheet
            Sheet sheet = workbook.createSheet(nombreHoja);

            sheet.setDisplayGridlines(false);
            sheet.setPrintGridlines(false);
            sheet.setFitToPage(true);
            sheet.setHorizontallyCenter(true);
            PrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setLandscape(true);

            sheet.setAutobreaks(true);
            printSetup.setFitHeight((short)1);
            printSetup.setFitWidth((short)1);

            Row row = sheet.createRow(0);
            row.setHeightInPoints(12.75f);


            // Create a Font for styling header cells
            /*Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());
            //Create a Font for styling body cells
            Font bodyFont = workbook.createFont();


            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            //Create a CellStyle color with the font
            CellStyle bodyCellStyle = workbook.createCellStyle();
            bodyCellStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());*/


            // Create cells
            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(styles.get("header"));
            }

            /*//Creating Font color
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(styles.get(styles.get("cell_b_centered")));
            }*/

            // Create Other rows and cells with data
            /*int rowNum = 1;
            for (List<String> dataColumns : data) {

                 rowData = sheet.createRow(rowNum++);

                // Create cells
                for (int i = 0; i < dataColumns.size(); i++) {
                    row2.createCell(i).setCellValue(dataColumns.get(i));
                    System.out.println(dataColumns.get(i));
                }
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }*/

            // Write the output to a file
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            //ZipOutputStream zip =new ZipOutputStream(fileOut);
            //ZipEntry zipEntry=new ZipEntry(archivo);

            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

            return new ByteArrayInputStream(fileOut.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrió un error al generar el archivo excel");
        }
    }
    public static List<List<String>> convertListTicketsToListString(List<PaymentVoucherEntity> tickets) {
        List<List<String>> rows = new ArrayList<>();
        for (PaymentVoucherEntity ticket : tickets) {

            String gravada = ticket.getTotalValorVentaOperacionGravada() != null ? (ticket.getTotalValorVentaOperacionGravada().setScale(2, RoundingMode.HALF_UP)).toString() : "0";
            String exonerada = ticket.getTotalValorVentaOperacionExonerada() != null ? (ticket.getTotalValorVentaOperacionExonerada().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String inafecta = ticket.getTotalValorVentaOperacionInafecta() != null ? (ticket.getTotalValorVentaOperacionInafecta().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String descuentoTotal = ticket.getTotalDescuento() != null ? (ticket.getTotalDescuento().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String isc = ticket.getSumatoriaISC() != null ? (ticket.getSumatoriaISC().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String igv = ticket.getSumatoriaIGV() != null ? (ticket.getSumatoriaIGV().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String otros = ticket.getSumatoriaOtrosTributos() != null ? ticket.getSumatoriaOtrosTributos().toString() : "0";

            String total = ticket.getMontoImporteTotalVenta() != null ? (ticket.getMontoImporteTotalVenta().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String totalGratuita = ticket.getTotalValorVentaOperacionGratuita() != null ? ticket.getTotalValorVentaOperacionGratuita().toString() : "0";
            String detraccion = ticket.getPorcentajeDetraccion() != null ? "Si" : "No";
            String impDetraccion = ticket.getPorcentajeDetraccion() != null ? ticket.getPorcentajeDetraccion() + " %" : "";
            String tipoDocAfe = ticket.getTipoComprobanteAfectado() != null ? ticket.getTipoComprobanteAfectado() : "";
            String serieDocAfe = ticket.getSerieAfectado() != null ? ticket.getSerieAfectado() : "";
            String numeroDocAfe = ticket.getNumeroAfectado() != null ? ticket.getNumeroAfectado().toString() : "";
            String aceptadoSunat = (ticket.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()) || ticket.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())) ? "Aceptado" : "-";
            String campo1="";
            String campo2="";
            String campo3="";
            String campo4="";
            String campo5="";
            String campo6="";
            List<String> columns = new ArrayList<>();

            columns.add(ticket.getFechaEmision());
            columns.add(getNombreTipoComprobante(ticket.getTipoComprobante()));
            columns.add(ticket.getSerie()+"-"+ticket.getNumero().toString());
            columns.add(ticket.getNumDocIdentReceptor());
            columns.add(ticket.getDenominacionReceptor());
            columns.add(ticket.getCodigoMoneda());
            columns.add(gravada);
            columns.add(exonerada);
            columns.add(inafecta);
            columns.add(igv);
            columns.add(descuentoTotal);
            columns.add(total);
            columns.add(ticket.getEstado().equals(EstadoComprobanteEnum.ACEPTADO.getCodigo()) ? "Aceptado" : "-");
            columns.add(ticket.getEstado().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()) ? "Aceptado" : "-");
            columns.add(otros);
            columns.add(totalGratuita);
            columns.add(detraccion);
            columns.add(impDetraccion);
            columns.add(tipoDocAfe+"-"+serieDocAfe+"-"+numeroDocAfe);

            ticket.getAditionalFields();

            if((ticket.getRucEmisor().trim()).equals("20381032702") || (ticket.getRucEmisor().trim()).equals("20477841407")){
                for (AditionalFieldEntity fieldEntity : ticket.getAditionalFields()) {
                    if(fieldEntity.getTypeField().getId()==1){
                        campo1 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }else if(fieldEntity.getTypeField().getId()==273){
                        campo2 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }else if(fieldEntity.getTypeField().getId()==274){
                        campo3 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }else if(fieldEntity.getTypeField().getId()==275){
                        campo4 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }else if(fieldEntity.getTypeField().getId()==276){
                        campo5 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }else if(fieldEntity.getTypeField().getId()==291){
                        campo6 = fieldEntity.getValorCampo() != null ? fieldEntity.getValorCampo() : "";
                    }
                }
            }
            columns.add("");
            columns.add("");
            columns.add("");
            columns.add("");
            columns.add("");
            columns.add("");
            columns.add(campo1);
            columns.add(campo2);
            columns.add(campo3);
            columns.add(campo4);
            columns.add(campo5);
            columns.add(campo6);
            rows.add(columns);
            for (DetailsPaymentVoucherEntity dticket : ticket.getDetailsPaymentVouchers()) {
                String cantidad = dticket.getCantidad() != null ? dticket.getCantidad().toString() : "0";
                List<String> dcolumns = new ArrayList<>();
                String valorunit = dticket.getValorUnitario() != null ? (dticket.getValorUnitario().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
                String valorventa = dticket.getValorVenta() != null ? (dticket.getValorVenta().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
                String desc = dticket.getDescuento() != null ? (dticket.getDescuento().setScale(2,RoundingMode.HALF_UP)).toString() : "0";

                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");
                dcolumns.add("");

                dcolumns.add(dticket.getCodigoProducto());
                dcolumns.add(dticket.getDescripcion());
                dcolumns.add(cantidad);
                dcolumns.add(valorunit);
                dcolumns.add(valorventa);
                dcolumns.add(desc);

                rows.add(dcolumns);
            }
        }
        return rows;
    }
    public static String getNombreTipoComprobante(String tipo){
        String tipoComprobante = null ;
        switch (tipo){
            case "03" :tipoComprobante = "Boleta";break;
            case "01" : tipoComprobante = "Factura"; break;
            case "07" : tipoComprobante = "Nota de credito"; break;
            case "08" : tipoComprobante = "Nota de debito"; break;
        }
        return tipoComprobante;
    }
}
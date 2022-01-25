package pe.com.certifakt.apifact.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.enums.EstadoSunatEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;


import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class UtilExcel {

    public static byte[] generateExcelFromList(String nombreHoja, String[] columns, List<List<String>> data) {

        try {
            // Create a Workbook
            Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

            // Create a Sheet
            Sheet sheet = workbook.createSheet(nombreHoja);

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Create cells
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Create Other rows and cells with data
            int rowNum = 1;
            for (List<String> dataColumns : data) {

                Row row = sheet.createRow(rowNum++);

                // Create cells
                for (int i = 0; i < dataColumns.size(); i++) {
                    row.createCell(i).setCellValue(dataColumns.get(i));
                }
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

            return fileOut.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrió un error al generar el archivo excel");
        }
    }


    public static List<List<String>> convertListTicketsToListString(List<PaymentVoucherEntity> tickets) {
        List<List<String>> rows = new ArrayList<>();
        for (PaymentVoucherEntity ticket : tickets) {

            String gravada = ticket.getTotalValorVentaOperacionGravada() != null ? (ticket.getTotalValorVentaOperacionGravada().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
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
            columns.add(getNombreTipoDocumento(ticket.getTipoDocIdentReceptor()));
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
    public static List<List<String>> convertListTicketsToListStringForExl(List<ComprobantesEntity> tickets) {
        List<List<String>> rows = new ArrayList<>();
        for (ComprobantesEntity ticket : tickets) {

            String gravada = ticket.getTotal_oper_gravada() != null ? (ticket.getTotal_oper_gravada().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String exonerada = ticket.getTotal_oper_exonerada() != null ? (ticket.getTotal_oper_exonerada().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String inafecta = ticket.getTotal_oper_inafecta() != null ? (ticket.getTotal_oper_inafecta().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String descuentoTotal = ticket.getTotal_descuento() != null ? (ticket.getTotal_descuento().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String isc = ticket.getSumatoria_isc() != null ? (ticket.getSumatoria_isc().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String igv = ticket.getSumatoria_igv() != null ? (ticket.getSumatoria_igv().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String otros = ticket.getSumatoria_otros_trib() != null ? ticket.getSumatoria_otros_trib().toString() : "0";

            String total = ticket.getMonto_imp_total_venta() != null ? (ticket.getMonto_imp_total_venta().setScale(2,RoundingMode.HALF_UP)).toString() : "0";
            String totalGratuita = ticket.getMonto_imp_total_venta() != null ? ticket.getMonto_imp_total_venta().toString() : "0";
            String detraccion = ticket.getPorcentaje_detraccion() != null ? "Si" : "No";
            String impDetraccion = ticket.getPorcentaje_detraccion() != null ? ticket.getPorcentaje_detraccion() + " %" : "";
            String tipoDocAfe = ticket.getTip_comprob_afectado() != null ? ticket.getTip_comprob_afectado() : "";
            String serieDocAfe = ticket.getSerie_afectado() != null ? ticket.getSerie_afectado() : "";
            String numeroDocAfe = ticket.getNumero_afectado() != null ? ticket.getNumero_afectado().toString() : "";
            String aceptadoSunat = (ticket.getEstado_sunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()) || ticket.getEstado_sunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())) ? "Aceptado" : "-";
            String campo1="";
            String campo2="";
            String campo3="";
            String campo4="";
            String campo5="";
            String campo6="";
            List<String> columns = new ArrayList<>();

            columns.add(ticket.getFecha_emision());
            columns.add(getNombreTipoComprobante(ticket.getTipo_comprobante()));
            columns.add(ticket.getSerie()+"-"+ticket.getNumero().toString());
            columns.add(getNombreTipoDocumento(ticket.getTip_doc_ident_receptor()));
            columns.add(ticket.getNum_doc_ident_receptor());
            columns.add(ticket.getDenominacion_receptor());
            columns.add(ticket.getCodigo_moneda());
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

            if((ticket.getRuc_emisor().trim()).equals("20381032702") || (ticket.getRuc_emisor().trim()).equals("20477841407")){
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
            for (DetallesComprobantesEntity dticket : ticket.getDetailsPaymentVouchers()) {
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

    public static void generateZipFromExcel() {
    }

    public static String getNombreTipoDocumento(String tipo){
        String tipoComprobante = null ;

        switch (tipo){
            case "0" :tipoComprobante = "DOC.TRIB.NO.DOM.SIN.RUC";break;
            case "1" : tipoComprobante = "DNI"; break;
            case "4" : tipoComprobante = "Carnet de Extranjeria"; break;
            case "6" : tipoComprobante = "RUC"; break;
            case "7" : tipoComprobante = "Pasaporte"; break;
            case "A" : tipoComprobante = "CED. Diplomatica de Identidad"; break;
            default: tipoComprobante = "Documento Desconocido"; break;
            //return tipo;
        }
       return tipoComprobante;
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

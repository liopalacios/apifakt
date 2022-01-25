package pe.com.certifakt.apifact.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.StopWatch;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.bean.ResponseStorage;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.ExcelService;


import java.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private AmazonS3 s3client;

    @Value("${apifact.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private CompanyRepository companyRepository;


     DowloadExcelEntity dowloadExcelEntity;

    @Autowired
     ExcelRepository excelRepository;

    @Autowired
    DowloadExcelRepository dowloadExcelRepository;

    @Autowired
    EmailSendRepository emailSendRepository;

    @Autowired
    AmazonS3ClientService amazonS3ClientService;

    @Autowired
    DocumentDowloadFileRepository documentDowloadFileRepository;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private DetailsPaymentVoucherRepository detailsPaymentVoucherRepository;

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
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
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
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font1);
        styles.put("cell_b_left", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font1);
        styles.put("cell_b_right", style);

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
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_prod", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font1);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(df.getFormat("d-mmm"));
        styles.put("cell_prod_left", style);

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

        Font fontwhite = wb.createFont();
        fontwhite.setBold(true);
        fontwhite.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFont(fontwhite);
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("ACEPTADO", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFont(fontwhite);
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("RECHAZADO", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFont(fontwhite);
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("ANULADO", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFont(fontwhite);
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("NO_ENVIADO", style);

        style = createBorderedStyle(wb);
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFont(fontwhite);
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("PROCESANDO", style);


        return styles;
    }

    @Override
    public ByteArrayInputStream CustomDownloadExcel(UserPrincipal user,String filtroDesde, String filtroHasta, String filtroTipoComprobante,
                                                    String filtroSerie) throws Exception {



        String[] colums = {"Fecha Emisión", "Tipo", "Número",
                "Tipo documento", "Num. Documento", "Receptor", "Moneda",
                "Gravada", "Exonerada", "Inafecta", "IGV", "Descuento total",
                "Monto Total", "Estado", "Estado Sunat", "Otros tributos",
                "Gratuita", "Detracción", "Imp. Detracción",
                "Comp. afectado", "Codigo", "Descripcion", "Cantidad", "Valor unidad", "Valor venta",
                "Descuento"};
        Workbook workbook = new HSSFWorkbook();

        Map<String, CellStyle> styles = createStyles(workbook);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("Reporte_Comprobantes");
        /* Estilos */
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


        for (int i = 0; i < colums.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(colums[i]);
            cell.setCellStyle(styles.get("header"));
        }

        if (filtroTipoComprobante != null) {
            filtroTipoComprobante = "%" + filtroTipoComprobante + "%";
        }

        if (filtroSerie != null) {
            filtroSerie = "%" + filtroSerie + "%";
        }
        System.out.println("PREPARADO PARA LA CONSULTA");
        List<PaymentVoucherEntity> comprobantes = paymentVoucherRepository.findAllByRuc(user.getRuc(),filtroDesde,filtroHasta,filtroTipoComprobante,filtroSerie);
        System.out.println("TERMINO LA CONSULTA");
        Row rowData;
        int initRow = 1;
        for(PaymentVoucherEntity comprobante : comprobantes){
            rowData = sheet.createRow(initRow);
            Cell cell1 = rowData.createCell(0);
            cell1.setCellValue(comprobante.getFechaEmision());
            cell1.setCellStyle(styles.get("cell_b"));
            Cell cell2 = rowData.createCell(1);
            cell2.setCellValue(comprobante.getTipoComprobante().equals("01")?"FACTURA":comprobante.getTipoComprobante().equals("03")?"BOLETA":comprobante.getTipoComprobante().equals("07")?"NOTA DE CREDITO":comprobante.getTipoComprobante().equals("08")?"NOTA DE DEBITO":"GUIA DE REMISION");
            cell2.setCellStyle(styles.get("cell_b_centered"));
            Cell cell3 = rowData.createCell(2);
            cell3.setCellValue(comprobante.getSerie() + "-" + comprobante.getNumero());
            cell3.setCellStyle(styles.get("cell_b_centered"));
            Cell cell4 = rowData.createCell(3);
            cell4.setCellValue(Integer.parseInt(comprobante.getTipoDocIdentReceptor())==6?"RUC":Integer.parseInt(comprobante.getTipoDocIdentReceptor())==1?"DNI":Integer.parseInt(comprobante.getTipoDocIdentReceptor())==0?"DTND":Integer.parseInt(comprobante.getTipoDocIdentReceptor())==4?"CE":"PASAPORTE");
            cell4.setCellStyle(styles.get("cell_b_centered"));
            Cell cell5 = rowData.createCell(4);
            cell5.setCellValue(comprobante.getNumDocIdentReceptor());
            cell5.setCellStyle(styles.get("cell_b_centered"));
            Cell cell6 = rowData.createCell(5);
            cell6.setCellValue(comprobante.getDenominacionReceptor());
            cell6.setCellStyle(styles.get("cell_b_left"));
            Cell cell7 = rowData.createCell(6);
            cell7.setCellValue(comprobante.getCodigoMoneda());
            cell7.setCellStyle(styles.get("cell_b_centered"));
            Cell cell8 = rowData.createCell(7);
            cell8.setCellValue(comprobante.getTotalValorVentaOperacionGravada()==null?"":String.format("%.2f",comprobante.getTotalValorVentaOperacionGravada()));
            cell8.setCellStyle(styles.get("cell_b_right"));
            Cell cell9 = rowData.createCell(8);
            cell9.setCellValue(comprobante.getTotalValorVentaOperacionExonerada()==null?"":String.format("%.2f",comprobante.getTotalValorVentaOperacionExonerada()));
            cell9.setCellStyle(styles.get("cell_b_right"));
            Cell cell10 = rowData.createCell(9);
            cell10.setCellValue(comprobante.getTotalValorVentaOperacionInafecta()==null?"":String.format("%.2f",comprobante.getTotalValorVentaOperacionInafecta()));
            cell10.setCellStyle(styles.get("cell_b_right"));
            Cell cell11 = rowData.createCell(10);
            cell11.setCellValue(comprobante.getSumatoriaIGV()==null?"":String.format("%.2f",comprobante.getSumatoriaIGV()));
            cell11.setCellStyle(styles.get("cell_b_right"));
            Cell cell12 = rowData.createCell(11);
            cell12.setCellValue(comprobante.getTotalDescuento()==null?"":String.format("%.2f",comprobante.getTotalDescuento()));
            cell12.setCellStyle(styles.get("cell_b_right"));
            Cell cell13 = rowData.createCell(12);
            cell13.setCellValue(comprobante.getMontoImporteTotalVenta()==null?"":String.format("%.2f",comprobante.getMontoImporteTotalVenta()));
            cell13.setCellStyle(styles.get("cell_b_right"));

            Cell cell14 = rowData.createCell(13);
            cell14.setCellValue(Integer.parseInt(comprobante.getEstado())==1?"REGISTRADO":Integer.parseInt(comprobante.getEstado())==2?"ACEPTADO":Integer.parseInt(comprobante.getEstado())==6?"ERROR":Integer.parseInt(comprobante.getEstado())==5?"RECHAZADO":Integer.parseInt(comprobante.getEstado())==8?"ANULADO":Integer.parseInt(comprobante.getEstado())==9?"PENDIENTE ANULACION":comprobante.getEstado());
            cell14.setCellStyle(styles.get("cell_b_centered"));

            Cell cell15 = rowData.createCell(14);
            cell15.setCellValue(comprobante.getEstadoSunat().equals("ACEPT")?"ACEPTADO":comprobante.getEstadoSunat().equals("N_ENV")?"NO_ENVIADO":comprobante.getEstadoSunat().equals("RECHA")?"RECHAZADO":comprobante.getEstadoSunat().equals("ANULA")?"ANULADO":"PROCESANDO");
            cell15.setCellStyle(styles.get("cell_b_centered"));

            Cell cell16 = rowData.createCell(15);
            cell16.setCellValue(comprobante.getSumatoriaOtrosTributos()==null?"":String.format("%.2f",comprobante.getSumatoriaOtrosTributos()));
            cell16.setCellStyle(styles.get("cell_b_right"));
            Cell cell17 = rowData.createCell(16);
            cell17.setCellValue(comprobante.getTotalValorVentaOperacionGratuita()==null?"":String.format("%.2f",comprobante.getTotalValorVentaOperacionGratuita()));
            cell17.setCellStyle(styles.get("cell_b_right"));
            Cell cell18 = rowData.createCell(17);
            cell18.setCellValue(comprobante.getDetraccion());
            cell18.setCellStyle(styles.get("cell_b_centered"));
            Cell cell19 = rowData.createCell(18);
            cell19.setCellValue(comprobante.getMontoDetraccion()==null?"":String.format("%.2f",comprobante.getMontoDetraccion()));
            cell19.setCellStyle(styles.get("cell_b_right"));
            Cell cell20 = rowData.createCell(19);
            cell20.setCellValue(comprobante.getTipoComprobanteAfectado());
            cell20.setCellStyle(styles.get("cell_b_centered"));


            initRow++;

            for(DetailsPaymentVoucherEntity detalle: comprobante.getDetailsPaymentVouchers()){
                Row row1 = sheet.createRow(initRow);

                Cell cell21 = row1.createCell(20);
                cell21.setCellValue(detalle.getCodigoProducto());
                cell21.setCellStyle(styles.get("cell_prod"));

                Cell cell22 = row1.createCell(21);
                cell22.setCellValue(detalle.getDescripcion());
                cell22.setCellStyle(styles.get("cell_prod_left"));

                Cell cell23 = row1.createCell(22);
                cell23.setCellValue(String.format("%.0f",detalle.getCantidad()));
                cell23.setCellStyle(styles.get("cell_prod"));

                Cell cell24 = row1.createCell(23);
                cell24.setCellValue(String.format("%.2f",detalle.getValorUnitario()));
                cell24.setCellStyle(styles.get("cell_prod"));

                Cell cell25 = row1.createCell(24);
                cell25.setCellValue(String.format("%.2f",detalle.getValorVenta()));
                cell25.setCellStyle(styles.get("cell_prod"));

                Cell cell26 = row1.createCell(25);
                cell26.setCellValue(detalle.getDescuento()==null?"":String.format("%.2f",detalle.getDescuento()));
                cell26.setCellStyle(styles.get("cell_prod"));


                initRow++;
            }
        }

        for(int i = 0; i< 26; i++){
            sheet.autoSizeColumn(i);
        }

        workbook.write(stream);
        workbook.close();

        return new ByteArrayInputStream(stream.toByteArray());



    }


    @Override
    public void registrarExcel(UserPrincipal user, String filtroDesde, String filtroHasta,String filtroTipoComprobante,String filtroSerie,String linkS3,ByteArrayInputStream stream) {


        DowloadExcelEntity excel = new DowloadExcelEntity();
        RegisterDownloadUploadEntity responseStorage = null;
        Date date = new Date();


        excel.setIdentificador(user.getRuc()+"-"+filtroDesde+"-"+filtroHasta);
        excel.setTipoDocumento(filtroTipoComprobante);
        excel.setSerie(filtroSerie);
        excel.setEstadoDescarga("D");
        excel.setLinkS3(linkS3);
        excel.setFechaSolicitud(new SimpleDateFormat("yyyy-MM-dd").format(date));
        excel.setFechaRegistro(date);
        excel.setCodCompany(user.getIdEmpresa());

        DowloadExcelEntity excelCreado = dowloadExcelRepository.findByIdentificadorAndTipoDocumentoAndSerieAndEstadoDescargaAndFechaSolicitud(excel.getIdentificador()
                ,excel.getTipoDocumento(),excel.getSerie(),excel.getEstadoDescarga(),excel.getFechaSolicitud());

        List<DowloadExcelEntity> listado = dowloadExcelRepository.obtenerExcels();
        CompanyEntity companyEntity = companyRepository.findById(user.getIdEmpresa()).get();


        responseStorage = amazonS3ClientService.uploadDocumentStorage(stream, user.getRuc()+"-"+filtroDesde+"-"+filtroHasta, "descargasexcel", companyEntity);


        if (responseStorage.getIdRegisterDownloadSend() != null) {
            excel.addFile(DocumentDownloadFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerDownloadUpload(RegisterDownloadUploadEntity.builder().idRegisterDownloadSend(responseStorage.getIdRegisterDownloadSend()).build())
                    .tipoArchivo(TipoArchivoEnum.XLS)
                    .build());
        }

        if(excelCreado == null){
            dowloadExcelRepository.save(excel);
        }

        /*EmailSendEntity email = new EmailSendEntity();

        email.setEmail(filtroEmail);
        email.setFecha(date);
        email.setUsuario(user.getUsername());
        if(excelCreado == null){



            email.setIdDowloadExcel(Long.valueOf(listado.size())+36);

        }else{
            email.setIdDowloadExcel(excelCreado.getIdExcelDocument());
        }


        emailSendRepository.save(email);*/

    }

    @Override
    public void uploadFilexlsx(Integer codCompany, String tipoComprobante, ByteArrayInputStream stream, String nombreDocumento) {

        CompanyEntity companyEntity = companyRepository.findById(codCompany).get();
        RegisterDownloadUploadEntity responseStorage = amazonS3ClientService.uploadDocumentStorage(stream, nombreDocumento, "descargasexcel", companyEntity);

        DowloadExcelEntity dowloadExcel = new DowloadExcelEntity();

        System.out.println("idregisterdowloadsend: "+responseStorage.getIdRegisterDownloadSend());

        if (responseStorage.getIdRegisterDownloadSend() != null) {
            dowloadExcel.addFile(DocumentDownloadFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerDownloadUpload(RegisterDownloadUploadEntity.builder().idRegisterDownloadSend(responseStorage.getIdRegisterDownloadSend()).build())
                    .tipoArchivo(TipoArchivoEnum.XLS)
                    .build());
        }

    }
    @Override
    public Map<String, Object> getExcels(UserPrincipal userResponse, Integer pageNumber, Integer perPage) {
        Page<DowloadExcelEntity> result = dowloadExcelRepository.findAllExcelsForPagesOrderByFecha_solicitudDesc(userResponse.getIdEmpresa(), new PageRequest((pageNumber - 1), perPage));
        return ImmutableMap.of("excelsList", result.getContent(), "total", result.getTotalElements());
    }




    @Override
    public ByteArrayInputStream downloadFileStorage(RegisterDownloadUploadEntity fileStorage) {

        String bucket, name;

        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/descargasexcel/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getExtension());
        }

        return new ByteArrayInputStream(getFile(bucket, name).toByteArray());
    }

    public ByteArrayOutputStream getFile(String bucketName, String keyName) {
        try {

            StopWatch watch = new StopWatch();
            watch.start();

            S3Object s3object = this.s3client.getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Descarga de archivo:", keyName, watch.getTime()));

            return baos;
        } catch (Exception ioe) {
            log.info("NO SE ENCONTRO ARCHIVO EN S3 , BUCKET: "+bucketName + " NAME: "+keyName);
            log.error("Exception: " + ioe.getMessage());
            throw new ServiceException("El servicio de storage está fuera de servicio, comuniquese con el administrador.");
        }
    }
    @Override
    public ByteArrayInputStream downloadFileInvoice(Long id, TipoArchivoEnum tipoArchivoEnum) {
        DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findByIdExcelDocument(id);
        RegisterDownloadUploadEntity file = null;
        if (tipoArchivoEnum.equals(TipoArchivoEnum.XLS)) {
            file = dowloadExcelEntity.getExcelActivo();
        }
        return downloadFileStorage(file);
    }






}

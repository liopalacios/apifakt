package pe.com.certifakt.apifact.controller.webapp;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.http.client.entity.DeflateInputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.ConsultaComprobante;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoPdfEnum;
import pe.com.certifakt.apifact.exception.QRGenerationException;
import pe.com.certifakt.apifact.model.DowloadExcelEntity;
import pe.com.certifakt.apifact.repository.DowloadExcelRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.ReportService;
import pe.com.certifakt.apifact.util.GZipCompressingInputStream;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.*;
import java.text.ParseException;
import java.util.zip.*;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class DescargaApi {

    private final ReportService reportService;
    private final AmazonS3ClientService amazonS3ClientService;
   // private final DowloadExcelEntity dowloadExcelEntity;
   private DowloadExcelRepository dowloadExcelRepository;


    @GetMapping("/descargapdf/{tipo}/{serie}/{numero}")
    public ResponseEntity<?> descargapdf(@CurrentUser UserPrincipal user, @PathVariable String tipo, @PathVariable String serie, @PathVariable Integer numero) throws IOException, ParseException, QRGenerationException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteTicket(user.getRuc(), tipo, serie, numero));
        ByteArrayResource resource = new ByteArrayResource(targetArray);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/descargapdf-A4/{tipo}/{serie}/{numero}")
    public ResponseEntity<?> descargapdfA4(@CurrentUser UserPrincipal user, @PathVariable String tipo, @PathVariable String serie, @PathVariable Integer numero) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteA4(user.getRuc(), tipo, serie, numero));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/descargapdf-othercpe/{tipo}/{serie}/{numero}")
    public ResponseEntity<?> descargapdfOthercpe(@CurrentUser UserPrincipal user, @PathVariable String tipo, @PathVariable String serie, @PathVariable Integer numero) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteOtherCpe(user.getRuc(), tipo, serie, numero));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/descargapdf-Guia/{serie}/{numero}")
    public ResponseEntity<?> descargapdfGuia(@CurrentUser UserPrincipal user, @PathVariable String serie, @PathVariable Integer numero) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteGuia(user.getRuc(), serie, numero, TipoPdfEnum.GUIA.getTipo()));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/descargapdf-Guia-Ticket/{serie}/{numero}")
    public ResponseEntity<?> descargapdfGuiaTicket(@CurrentUser UserPrincipal user, @PathVariable String serie, @PathVariable Integer numero) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteGuia(user.getRuc(), serie, numero, TipoPdfEnum.GUIA_TICKET.getTipo()));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }


    @PostMapping("/descargapdfpublico-A4/blob")
    public ResponseEntity<?> descargapdfpublicoA4(@RequestBody ConsultaComprobante consultaComprobante) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteA4Publico(consultaComprobante.getRuc(), consultaComprobante.getTipo(), consultaComprobante.getSerie(), consultaComprobante.getNumero(), consultaComprobante.getFecha(), consultaComprobante.getMonto()));


        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

    }

    @GetMapping("/descargapdfpublico/{ruc}/{tipo}/{serie}/{numero}")
    public ResponseEntity<?> descargapdfpublico(@PathVariable String ruc, @PathVariable String tipo, @PathVariable String serie, @PathVariable Integer numero) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteTicket(ruc, tipo, serie, numero));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/descargapdfuuid/{id}/{uuid}/{tipoPdf}/{nameDocument}")
    public ResponseEntity<?> descargapdfuuid(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String tipoPdf, @PathVariable String nameDocument, HttpServletResponse response) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteuid(id, uuid, nameDocument, tipoPdf));


        log.info("ID " + id);
        log.info("uuid " + uuid);
        log.info("TipoPDF " + tipoPdf);
        log.info("NameDocument " + nameDocument);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

    }


    @GetMapping("/descargaxmluuid/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargaxmluuid(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileInvoice(id, uuid, TipoArchivoEnum.XML);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }

    @GetMapping("/descargaexcel/{id}/{nameDocument}")
    public ResponseEntity<?> descargaexcel(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileExcel(id, TipoArchivoEnum.XLS);

        DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findByIdExcelDocument(id);
        byte[] bytes = IOUtils.toByteArray(is);
        String nombreDocumento = dowloadExcelEntity.getIdentificador();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(nombreDocumento+".xls");

        entry.setSize(bytes.length);
        zos.putNextEntry(entry);
        zos.write(bytes);
        zos.closeEntry();
        zos.close();
        ByteArrayInputStream stream = new ByteArrayInputStream(baos.toByteArray());

        byte[] targetArray = ByteStreams.toByteArray(stream);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok().body(resource);



}


    @GetMapping("/descargacdruuid/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargacdruuid(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileInvoice(id, uuid, TipoArchivoEnum.CDR);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }

    @GetMapping("/descargacdruuidname/{nameDocument}")
    public ResponseEntity<?> descargacdruuidname(@CurrentUser UserPrincipal user, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileInvoiceName(TipoArchivoEnum.CDR, nameDocument);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }

    //OTHER DOCUMENTS
    @GetMapping("/descargapdfuuidothercpe/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargapdfuuidOtherCpe(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfComprobanteOtherCpeUuid(id, uuid, nameDocument));


        log.info("ID " + id);
        log.info("uuid " + uuid);
        log.info("NameDocument " + nameDocument);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

    }


    @GetMapping("/descargaxmluuidothercpe/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargaxmluuidOtherCpe(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileOther(id, uuid, TipoArchivoEnum.XML);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }


    @GetMapping("/descargacdruuidothercpe/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargacdruuidOtherCpe(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileOther(id, uuid, TipoArchivoEnum.CDR);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }


    //GUIA

    @GetMapping("/descargapdfuuidguia/{id}/{uuid}/{tipoPdf}/{nameDocument}")
    public ResponseEntity<?> descargaPdfUuidGuia(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String tipoPdf, @PathVariable String nameDocument, HttpServletResponse response) throws IOException, QRGenerationException, ParseException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getPdfGuiaUuid(id, uuid, nameDocument, tipoPdf));


        log.info("ID " + id);
        log.info("uuid " + uuid);
        log.info("TipoPDF " + tipoPdf);
        log.info("NameDocument " + nameDocument);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

    }

    @GetMapping("/descargaxmluuidguia/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargaXmlUuidGuia(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileGuia(id, uuid, TipoArchivoEnum.XML);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }

    @GetMapping("/descargacdruuidguia/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> descargaCdrUuidGuia(@CurrentUser UserPrincipal user, @PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument, HttpServletResponse response) throws IOException {

        InputStream is = amazonS3ClientService.downloadFileGuia(id, uuid, TipoArchivoEnum.CDR);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);

    }

    @GetMapping("/descargareporteexcel")
    public ResponseEntity<?> descargareporteexcel(@CurrentUser UserPrincipal user,
                                                  @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
                                                  @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
                                                  @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
                                                  @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
                                                  @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
                                                  @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero
    ) throws IOException {

        byte[] targetArray = ByteStreams.toByteArray(reportService.getReporteEcxel(user.getRuc(), filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero));

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }


}

package pe.com.certifakt.apifact.controller;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.DowloadExcelRepository;
import pe.com.certifakt.apifact.repository.RegisterDownloadUploadRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ComunicationSunatService;
import pe.com.certifakt.apifact.service.ExcelService;
import pe.com.certifakt.apifact.service.impl.AmazonS3ClientServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/download")
@Slf4j
public class ExcelController {


    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private RegisterDownloadUploadRepository registerDownloadUploadRepository;

    @Autowired
    DowloadExcelRepository dowloadExcelRepository;

    @Autowired
    private ComunicationSunatService comunicationSunatService;

    @Autowired
    AmazonS3ClientServiceImpl amazonS3ClientService;
   // private DowloadExcelEntity dowloadExcelEntity;

    @RequestMapping(value = {"/generar-descarga-xl"}, method = RequestMethod.GET)
    public void comprobantesconsulta(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
          //  @RequestParam(name = "ruc", required = false) String ruc,
            @CurrentUser UserPrincipal userPrincipal) throws Exception {
        String linkS3 = "";

        Date date = new Date();
       // CompanyEntity companyEntity = companyRepository.findById(codCompany).get();
        //responseStorage().getIdRegisterDownloadSend();
        ByteArrayInputStream stream = excelService.CustomDownloadExcel(userPrincipal,filtroDesde,filtroHasta,filtroTipoComprobante,filtroSerie);

        excelService.registrarExcel(userPrincipal, filtroDesde, filtroHasta, filtroTipoComprobante, linkS3, filtroSerie, stream);




        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=comprobantes.xls");

        //Date date = new Date();

        //DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findByIdentificadorAndTipoDocumentoAndSerieAndEstadoDescargaAndFechaSolicitud
      //          (userPrincipal.getRuc()+"-"+filtroDesde+"-"+filtroHasta,filtroTipoComprobante,filtroSerie,"D",new SimpleDateFormat("yyyy-MM-dd").format(date));

       //RegisterDownloadUploadEntity resp;
       //resp = amazonS3ClientService.uploadDocumentStorage(stream, "descargasexcel");
        //return  ResponseEntity.ok().body(new InputStreamResource(stream));


    }
    @PostMapping("/subirExcel")
    public @ResponseBody String uploadExcel(UserPrincipal principal,
                        @RequestParam("xls") MultipartFile excel,
                        HttpServletResponse response)
    {

        if (principal != null)
            log.info(principal.toString());

        RegisterDownloadUploadEntity resp;
        try {
            //resp = amazonS3ClientService.uploadDocumentStorage(excel, "descargasexcel");
        } catch (Exception e) {
            e.printStackTrace();

            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            throw new ServiceException("No se pudo subir la imagen");
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        //return resp.getIdRegisterDownloadSend().toString();
        return null;

    }











}
